package com.github.pool_party.resistance_bot.action

import com.elbekD.bot.Bot
import com.github.pool_party.resistance_bot.message.RECEIVE_RESISTANCE_ROLE
import com.github.pool_party.resistance_bot.message.RECEIVE_SPY_ROLE
import com.github.pool_party.resistance_bot.state.StateStorage

suspend fun Bot.distributeRoles(chatId: Long, stateStorage: StateStorage) {
    val state = stateStorage[chatId] ?: return

    val shuffled = state.members.shuffled()
    val spyNumber = state.board.spyNumber

    // TODO send cards from assets with caption.
    shuffled.take(spyNumber).forEach { sendMessage(it.id, RECEIVE_SPY_ROLE, "MarkdownV2") }
    shuffled.drop(spyNumber).forEach { sendMessage(it.id, RECEIVE_RESISTANCE_ROLE, "MarkdownV2") }

    chooseSquad(chatId, stateStorage)
}
