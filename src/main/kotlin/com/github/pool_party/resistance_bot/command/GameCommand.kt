package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.startGame
import com.github.pool_party.resistance_bot.message.GET_ADMIN_SUGGEST
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
import com.github.pool_party.resistance_bot.utils.logging
import com.github.pool_party.resistance_bot.utils.makeRegistrationMarkup
import com.github.pool_party.resistance_bot.utils.sendMessageLogging
import kotlinx.coroutines.delay
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration.Companion.minutes

class GameCommand(private val stateStorage: StateStorage, private val longCoder: Coder<Long>) :
    AbstractCommand("game", "start the registration", HELP_GAME, CommandType.REGISTRATION) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId

        if (message.chat.type != "group" && message.chat.type != "supergroup") {
            sendMessageLogging(chatId, ON_PRIVATE_CHAT_REGISTRATION, addBotMarkup())
            return
        }

        val registrationMessageIdFuture = CompletableFuture<Int>()

        if (!stateStorage.newRegistrationState(chatId, registrationMessageIdFuture, message.chat.title)) {
            sendMessageLogging(chatId, ON_ONGOING_REGISTRATION)
            return
        }

        val registrationMessage: Message

        try {
            registrationMessage = sendMessageLogging(
                chatId,
                REGISTRATION_MSG,
                makeRegistrationMarkup(longCoder.encode(chatId)),
            ).join()
        } catch (e: Throwable) {
            registrationMessageIdFuture.completeExceptionally(e)
            throw e
        }

        val registrationMessageId = registrationMessage.message_id
        registrationMessageIdFuture.complete(registrationMessageId)

        if (canPin(chatId)) {
            pinChatMessage(chatId, registrationMessageId, disableNotification = true).logging()
        } else {
            sendMessageLogging(chatId, GET_ADMIN_SUGGEST)
        }

        // TODO check behaviour.
        val state = stateStorage.getRegistrationState(chatId) ?: return

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

            sendMessageLogging(chatId, onRegistrationTimestamp(timeLeft + extendTime * extends.get()))

            delayTime = extendTime
        }

        state.tryStartAndDo { startGame(chatId, stateStorage) }
    }

    private fun Bot.canPin(chatId: Long): Boolean {
        val myId = getMe().join().id.toLong()
        return getChatMember(chatId, myId).join().can_pin_messages ?: false
    }
}
