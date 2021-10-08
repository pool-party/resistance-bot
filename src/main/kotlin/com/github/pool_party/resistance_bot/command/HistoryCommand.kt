package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.flume.interaction.command.AbstractCommand
import com.github.pool_party.flume.utils.chatId
import com.github.pool_party.flume.utils.sendMessageLogging
import com.github.pool_party.resistance_bot.message.HELP_HISTORY
import com.github.pool_party.resistance_bot.message.ON_NO_REGISTRATION
import com.github.pool_party.resistance_bot.message.history
import com.github.pool_party.resistance_bot.state.StateStorage

class HistoryCommand(private val stateStorage: StateStorage) :
    AbstractCommand("history", "show round history", HELP_HISTORY) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId
        val state = stateStorage.getGameState(chatId)

        if (state == null) {
            sendMessageLogging(chatId, ON_NO_REGISTRATION)
            return
        }

        sendMessageLogging(chatId, history(state.history))
    }
}
