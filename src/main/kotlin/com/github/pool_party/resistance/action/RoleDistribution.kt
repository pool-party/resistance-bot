package com.github.pool_party.resistance.action

import com.elbekD.bot.Bot
import com.github.pool_party.resistance.state.StateStorage
import kotlin.math.min

suspend fun Bot.distributeRoles(chatId: Long, stateStorage: StateStorage) {
    val state = stateStorage[chatId]
    if (state == null) {
        sendMessage(chatId, "TODO: game over xd")
        return
    }

    // TODO configuration
    val shuffled = state.members.shuffled()
    val size = shuffled.size
    val spies = shuffled.take(min(size, 2))
    val resistance = shuffled.drop(min(size, 2))

    spies.forEach { sendMessage(it.id, "TODO: spy") }
    resistance.forEach { sendMessage(it.id, "TODO: resistance") }

    chooseSquad(chatId, stateStorage)
}
