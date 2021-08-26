package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.startGame
import com.github.pool_party.resistance_bot.message.HELP_GAME
import com.github.pool_party.resistance_bot.message.ON_ONGOING_REGISTRATION
import com.github.pool_party.resistance_bot.message.ON_PRIVATE_CHAT_REGISTRATION
import com.github.pool_party.resistance_bot.message.REGISTRATION_MSG
import com.github.pool_party.resistance_bot.message.onRegistrationTimestamp
import com.github.pool_party.resistance_bot.state.Coder
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.addBotMarkup
import com.github.pool_party.resistance_bot.utils.chatId
import com.github.pool_party.resistance_bot.utils.dec
import com.github.pool_party.resistance_bot.utils.durationToString
import com.github.pool_party.resistance_bot.utils.makeRegistrationMarkup
import kotlinx.coroutines.delay
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration.Companion.minutes

class GameCommand(private val stateStorage: StateStorage, private val longCoder: Coder<Long>) :
    AbstractCommand("game", "start the registration", HELP_GAME) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId

        if (message.chat.type != "group" && message.chat.type != "supergroup") {
            sendMessage(chatId, ON_PRIVATE_CHAT_REGISTRATION, "MarkdownV2", markup = addBotMarkup())
            return
        }

        val registrationMessageIdFuture = CompletableFuture<Int>()

        if (!stateStorage.newState(chatId, registrationMessageIdFuture, message.chat.title)) {
            sendMessage(chatId, ON_ONGOING_REGISTRATION, "MarkdownV2")
            return
        }

        val registrationMessage: Message

        try {
            registrationMessage = sendMessage(
                chatId,
                REGISTRATION_MSG,
                "MarkdownV2",
                markup = makeRegistrationMarkup(longCoder.encode(chatId))
            ).join()
        } catch (e: Throwable) {
            registrationMessageIdFuture.completeExceptionally(e)
            throw e
        }

        val registrationMessageId = registrationMessage.message_id
        registrationMessageIdFuture.complete(registrationMessageId)
        pinChatMessage(chatId, registrationMessageId, disableNotification = true)

        // TODO check behaviour.
        val state = stateStorage[chatId] ?: return

        val extendTime = Configuration.REGISTRATION_EXTEND
        val registrationDuration = Configuration.REGISTRATION_TIME
        var delayTime =
            if (registrationDuration > minutes(1)) registrationDuration - extendTime
            else registrationDuration
        var timeLeft = registrationDuration

        // TODO make adequate
        while (true) {
            delay(delayTime)

            if (state.withStarted { it }) return

            val extends = state.registrationExtendCounter

            if (extends.get() > 0) {
                state.registrationExtendCounter--
            } else {
                timeLeft -= delayTime

                if (!timeLeft.isPositive()) break
            }

            sendMessage(chatId, onRegistrationTimestamp(durationToString(timeLeft)), "MarkdownV2")

            delayTime = extendTime
        }

        state.tryStartAndDo { startGame(chatId, stateStorage) }
    }
}
