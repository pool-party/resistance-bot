package com.github.pool_party.resistance_bot.action

import com.elbekD.bot.Bot
import com.elbekD.bot.types.InlineKeyboardButton
import com.github.pool_party.resistance_bot.callback.SquadChoiceCallbackData
import com.github.pool_party.resistance_bot.message.LEADER_CHOOSE_MSG
import com.github.pool_party.resistance_bot.message.roundSummary
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.toMarkUp

suspend fun Bot.chooseSquad(chatId: Long, stateStorage: StateStorage) {

    val state = stateStorage[chatId] ?: return
    val leader = state.members.firstOrNull() ?: return

    // TODO Consider using pictures from assets as the roadmaps.
    sendMessage(chatId, roundSummary(state, leader), "MarkdownV2")

    sendMessage(
        leader.id,
        LEADER_CHOOSE_MSG,
        markup = state.members
            .asSequence()
            .map { InlineKeyboardButton(it.name, callback_data = SquadChoiceCallbackData(chatId, it.id).encoded) }
            .toList()
            .toMarkUp()
    )
}
