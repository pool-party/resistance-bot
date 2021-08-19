package com.github.pool_party.resistance_bot.action

import com.elbekD.bot.Bot
import com.github.pool_party.resistance_bot.message.RECEIVE_RESISTANCE_ROLE
import com.github.pool_party.resistance_bot.message.RECEIVE_SPY_ROLE
import com.github.pool_party.resistance_bot.state.StateStorage
import kotlin.math.min

suspend fun Bot.distributeRoles(chatId: Long, stateStorage: StateStorage) {
    val state = stateStorage[chatId] ?: return

    // TODO configuration
    val shuffled = state.members.shuffled()
    val size = shuffled.size
    val spies = shuffled.take(min(size, 2))
    val resistance = shuffled.drop(min(size, 2))

    // TODO send cards from assets with caption.
    spies.forEach { sendMessage(it.id, RECEIVE_SPY_ROLE) }
    resistance.forEach { sendMessage(it.id, RECEIVE_RESISTANCE_ROLE) }

    chooseSquad(chatId, stateStorage)
}
