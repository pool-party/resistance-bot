package com.github.pool_party.resistance_bot.action

import com.elbekD.bot.Bot
import com.github.pool_party.flume.utils.sendMessageLogging
import com.github.pool_party.resistance_bot.message.RECEIVE_RESISTANCE_ROLE
import com.github.pool_party.resistance_bot.message.receiveSpyRole
import com.github.pool_party.resistance_bot.state.StateStorage

suspend fun Bot.distributeRoles(chatId: Long, stateStorage: StateStorage) {
    val state = stateStorage.getGameState(chatId) ?: return

    // TODO send cards from assets with caption.
    val spies = state.spies
    spies.forEach { spy ->
        sendMessageLogging(spy.id, receiveSpyRole((spies - spy).map { it.name })).join()
    }
    state.resistance.forEach { sendMessageLogging(it.id, RECEIVE_RESISTANCE_ROLE).join() }

    chooseSquad(chatId, stateStorage)
}
