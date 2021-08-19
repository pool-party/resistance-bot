package com.github.pool_party.resistance_bot.action

import com.elbekD.bot.Bot
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.message.ON_GAME_START
import com.github.pool_party.resistance_bot.message.ON_LESS_PLAYERS
import com.github.pool_party.resistance_bot.message.ON_MORE_PLAYERS
import com.github.pool_party.resistance_bot.state.StateStorage
import java.io.File

suspend fun Bot.startGame(chatId: Long, stateStorage: StateStorage) {
    val playersAmount = stateStorage[chatId]?.members?.size

    // TODO configuration.
    if (playersAmount in Configuration.PLAYERS_GAME_MINIMUM..Configuration.PLAYERS_GAME_MAXIMUM) {
        sendPhoto(chatId, File("/assets/back-plot-a.png"), ON_GAME_START, "Markdown").join()
        distributeRoles(chatId, stateStorage)
        return
    }

    sendMessage(
        chatId,
        if (playersAmount == null || playersAmount < Configuration.PLAYERS_GAME_MINIMUM) ON_LESS_PLAYERS
        else ON_MORE_PLAYERS,
        "Markdown"
    )
    stateStorage.gameOver(chatId)
}
