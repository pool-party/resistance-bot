package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.message.HELP_STOP
import com.github.pool_party.resistance_bot.message.ON_REGISTRATION_STOP
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.chatId

class StopCommand(private val stateStorage: StateStorage) :
    AbstractCommand("stop", "cancel the current registration", HELP_STOP) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId

        sendMessage(chatId, ON_REGISTRATION_STOP, "MarkdownV2")
        stateStorage[chatId]?.registrationMessageId?.let { deleteMessage(chatId, it) }
        stateStorage.gameOver(chatId)
    }
}
