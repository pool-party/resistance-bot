package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.startGame
import com.github.pool_party.resistance_bot.chatId
import com.github.pool_party.resistance_bot.makeRegistrationMarkup
import com.github.pool_party.resistance_bot.message.HELP_GAME
import com.github.pool_party.resistance_bot.message.ON_ONGOING_REGISTRATION
import com.github.pool_party.resistance_bot.message.REGISTRATION_MSG
import com.github.pool_party.resistance_bot.message.onRegistrationTimestamp
import com.github.pool_party.resistance_bot.state.GameDescription
import com.github.pool_party.resistance_bot.state.HashStorage
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.timestampToString
import kotlinx.coroutines.delay
import java.util.concurrent.CompletableFuture

class GameCommand(private val stateStorage: StateStorage, private val hashStorage: HashStorage) :
    AbstractCommand("game", "start the registration", HELP_GAME) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId

        if (!stateStorage.newState(chatId)) {
            sendMessage(chatId, ON_ONGOING_REGISTRATION, "MarkdownV2")
            return
        }

        val registrationMessageIdFuture = CompletableFuture<Int>()
        val gameDescription = GameDescription(chatId, registrationMessageIdFuture)

        val registrationMessage =
            sendMessage(
                chatId,
                REGISTRATION_MSG,
                "MarkdownV2",
                markup = makeRegistrationMarkup(hashStorage.newHash(gameDescription))
            ).join()
        registrationMessageIdFuture.complete(registrationMessage.message_id)

        // TODO check behaviour.
        val state = stateStorage[chatId] ?: return

        // TODO feels like this whole while loop can look better.
        var delayTime = (Configuration.REGISTRATION_SECONDS - 60) * 1000L
        var timeLeft = Configuration.REGISTRATION_SECONDS * 1000L - delayTime
        while (timeLeft > 0) {
            delay(delayTime)

            if (state.started.get()) return

            sendMessage(chatId, onRegistrationTimestamp(timestampToString(timeLeft)), "MarkdownV2")

            delayTime = Configuration.REGISTRATION_ANNOUNCEMENT_DELAY.toLong()
            timeLeft -= Configuration.REGISTRATION_ANNOUNCEMENT_DELAY
        }

        if (!state.started.compareAndSet(false, true)) {
            startGame(chatId, stateStorage)
        }
    }
}
