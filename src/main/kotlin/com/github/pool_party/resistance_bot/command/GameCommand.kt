package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.startGame
import com.github.pool_party.resistance_bot.addBotMarkup
import com.github.pool_party.resistance_bot.chatId
import com.github.pool_party.resistance_bot.durationToString
import com.github.pool_party.resistance_bot.makeRegistrationMarkup
import com.github.pool_party.resistance_bot.message.HELP_GAME
import com.github.pool_party.resistance_bot.message.ON_ONGOING_REGISTRATION
import com.github.pool_party.resistance_bot.message.ON_PRIVATE_CHAT_REGISTRATION
import com.github.pool_party.resistance_bot.message.REGISTRATION_MSG
import com.github.pool_party.resistance_bot.message.onRegistrationTimestamp
import com.github.pool_party.resistance_bot.state.GameDescription
import com.github.pool_party.resistance_bot.state.HashStorage
import com.github.pool_party.resistance_bot.state.StateStorage
import kotlinx.coroutines.delay
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration.Companion.minutes

class GameCommand(private val stateStorage: StateStorage, private val hashStorage: HashStorage) :
    AbstractCommand("game", "start the registration", HELP_GAME) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId

        if (message.chat.type != "group" && message.chat.type != "supergroup") {
            sendMessage(chatId, ON_PRIVATE_CHAT_REGISTRATION, "MarkdownV2", markup = addBotMarkup())
            return
        }

        if (!stateStorage.newState(chatId)) {
            sendMessage(chatId, ON_ONGOING_REGISTRATION, "MarkdownV2")
            return
        }

        val registrationMessageIdFuture = CompletableFuture<Int>()
        val gameDescription = GameDescription(chatId, message.chat.title, registrationMessageIdFuture)

        val registrationMessage: Message

        try {
            registrationMessage = sendMessage(
                chatId,
                REGISTRATION_MSG,
                "MarkdownV2",
                markup = makeRegistrationMarkup(hashStorage.newHash(gameDescription))
            ).join()
        } catch (e: Throwable) {
            registrationMessageIdFuture.complete(null)
            throw e
        }

        registrationMessageIdFuture.complete(registrationMessage.message_id)

        // TODO check behaviour.
        val state = stateStorage[chatId] ?: return

        val registrationDuration = Configuration.REGISTRATION_TIME
        var delayTime =
            if (registrationDuration > minutes(1)) registrationDuration - minutes(1)
            else registrationDuration
        var timeLeft = registrationDuration

        while (true) {
            delay(delayTime)
            timeLeft -= delayTime

            if (state.started.get()) return

            if (!timeLeft.isPositive()) break

            sendMessage(chatId, onRegistrationTimestamp(durationToString(timeLeft)), "MarkdownV2")

            delayTime = Configuration.REGISTRATION_ANNOUNCEMENT_DELAY
        }

        if (state.started.compareAndSet(false, true)) {
            startGame(chatId, stateStorage)
        }
    }
}
