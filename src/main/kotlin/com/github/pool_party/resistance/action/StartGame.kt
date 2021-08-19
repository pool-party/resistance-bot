package com.github.pool_party.resistance.action

import com.elbekD.bot.Bot
import com.github.pool_party.resistance.Configuration
import com.github.pool_party.resistance.state.StateStorage

suspend fun Bot.startGame(chatId: Long, stateStorage: StateStorage) {
    // TODO configuration
    if (stateStorage[chatId]?.members?.size.let { it == null || it < Configuration.PLAYERS_GAME_MINIMUM }) {
        sendMessage(chatId, "TODO: not enough players")
        stateStorage.gameOver(chatId)
        return
    }

    sendMessage(chatId, "TODO: starting").join()
    distributeRoles(chatId, stateStorage)
}
