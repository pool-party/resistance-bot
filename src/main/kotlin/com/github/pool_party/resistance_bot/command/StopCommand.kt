package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.message.HELP_STOP
import com.github.pool_party.resistance_bot.message.ON_REGISTRATION_STOP
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.chatId
import com.github.pool_party.resistance_bot.utils.logging
import com.github.pool_party.resistance_bot.utils.sendMessageLogging

// TODO Add ability to cancel the current game (think of ways to prevent miss clicks and clown moves).
class StopCommand(private val stateStorage: StateStorage) :
    AbstractCommand(
        "stop",
        "cancel the current registration",
        HELP_STOP,
        CommandType.REGISTRATION,
    ) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId

        sendMessageLogging(chatId, ON_REGISTRATION_STOP)
        stateStorage.getRegistrationState(chatId)?.registrationMessageId?.let { deleteMessage(chatId, it).logging() }
        stateStorage.gameOver(chatId)
    }
}
