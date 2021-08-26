package com.github.pool_party.resistance_bot.action

import com.elbekD.bot.Bot
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.message.ON_GAME_START
import com.github.pool_party.resistance_bot.message.ON_LESS_PLAYERS
import com.github.pool_party.resistance_bot.message.ON_MORE_PLAYERS
import com.github.pool_party.resistance_bot.state.StateStorage

suspend fun Bot.startGame(chatId: Long, stateStorage: StateStorage) {
    val state = stateStorage[chatId] ?: return

    editMessageText(chatId, state.registrationMessageId, text = "TODO: registration is closed")
    unpinChatMessage(chatId, state.registrationMessageId)

    val playersAmount = state.members.size

    if (playersAmount !in Configuration.PLAYERS_GAME_MINIMUM..Configuration.PLAYERS_GAME_MAXIMUM) {
        sendMessage(
            chatId,
            if (playersAmount < Configuration.PLAYERS_GAME_MINIMUM) ON_LESS_PLAYERS else ON_MORE_PLAYERS,
            "MarkdownV2",
        )
        stateStorage.gameOver(chatId)
        return
    }

    // TODO send picture from assets with caption.
    sendMessage(chatId, ON_GAME_START, "MarkdownV2").join()
    distributeRoles(chatId, stateStorage)
}
