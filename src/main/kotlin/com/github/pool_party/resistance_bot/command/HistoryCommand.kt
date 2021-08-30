package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.message.HELP_HISTORY
import com.github.pool_party.resistance_bot.message.ON_NO_REGISTRATION
import com.github.pool_party.resistance_bot.message.history
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.chatId
import com.github.pool_party.resistance_bot.utils.sendMessageLogging

class HistoryCommand(private val stateStorage: StateStorage) :
    AbstractCommand("history", "show round history", HELP_HISTORY, CommandType.GAME) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId
        val state = stateStorage.getGameState(chatId)

        if (state == null) {
            sendMessageLogging(chatId, ON_NO_REGISTRATION)
            return
        }

        // TODO handle empty call.
        sendMessageLogging(chatId, history(state.history))
    }
}
