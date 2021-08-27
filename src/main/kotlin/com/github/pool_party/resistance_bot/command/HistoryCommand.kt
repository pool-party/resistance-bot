package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.chatId
import com.github.pool_party.resistance_bot.utils.sendMessageLogging

class HistoryCommand(private val stateStorage: StateStorage) :
    AbstractCommand("history", "shows round history", "TODO: long") {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId
        val state = stateStorage.getGameState(chatId)

        if (state == null) {
            sendMessageLogging(chatId, "TODO: no game")
            return
        }

        sendMessageLogging(
            chatId,
            """
            |TODO: history:
            |${state.history.joinToString("\n|")}
            """.trimMargin(),
        )
    }
}
