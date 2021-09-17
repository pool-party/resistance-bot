package com.github.pool_party.resistance_bot.action

import com.elbekD.bot.Bot
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.message.ON_GAME_START
import com.github.pool_party.resistance_bot.message.ON_LESS_PLAYERS
import com.github.pool_party.resistance_bot.message.ON_MORE_PLAYERS
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.deleteMessageLogging
import com.github.pool_party.resistance_bot.utils.logging
import com.github.pool_party.resistance_bot.utils.sendMessageLogging

suspend fun Bot.startGame(chatId: Long, stateStorage: StateStorage) {
    val state = stateStorage.getRegistrationState(chatId) ?: return

    deleteMessageLogging(chatId, state.registrationMessageId)

    val playersAmount = state.members.size

    if (playersAmount !in Configuration.PLAYERS_GAME_MINIMUM..Configuration.PLAYERS_GAME_MAXIMUM) {
        sendMessageLogging(
            chatId,
            if (playersAmount < Configuration.PLAYERS_GAME_MINIMUM) ON_LESS_PLAYERS else ON_MORE_PLAYERS,
        )
        stateStorage.gameOver(chatId)
        return
    }

    stateStorage.startGame(chatId)
    // TODO send picture from assets with caption.
    sendMessageLogging(chatId, ON_GAME_START).join()
    distributeRoles(chatId, stateStorage)
}
