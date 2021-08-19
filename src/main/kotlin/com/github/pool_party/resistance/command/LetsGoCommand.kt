package com.github.pool_party.resistance.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance.action.startGame
import com.github.pool_party.resistance.chatId
import com.github.pool_party.resistance.state.StateStorage

class LetsGoCommand(private val stateStorage: StateStorage)
    : AbstractCommand("letsgo", "TODO: long letsgo", "TODO: description: letsgo") {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId
        val state = stateStorage[chatId]

        if (state == null) {
            sendMessage(chatId, "TODO: /register first")
            return
        }

        if (state.started.compareAndSet(false, true)) {
            startGame(chatId, stateStorage)
        }
    }
}
