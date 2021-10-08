package com.github.pool_party.resistance_bot.action

import com.elbekD.bot.Bot
import com.elbekD.bot.types.InlineKeyboardButton
import com.github.pool_party.flume.utils.sendMessageLogging
import com.github.pool_party.flume.utils.toMarkUp
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.callback.SquadChoiceCallbackData
import com.github.pool_party.resistance_bot.message.leaderChooseMessage
import com.github.pool_party.resistance_bot.message.roundSummary
import com.github.pool_party.resistance_bot.state.StateStorage

suspend fun Bot.chooseSquad(chatId: Long, stateStorage: StateStorage) {

    val state = stateStorage.getGameState(chatId) ?: return
    val leader = state.leader

    state.squad = null

    // TODO Consider using pictures from assets as the roadmaps.
    sendMessageLogging(chatId, roundSummary(state, leader))

    val spies = state.spies
    val spyLeader = leader in spies

    sendMessageLogging(
        leader.id,
        leaderChooseMessage(state.currentMissionAgentNumber),
        state.members
            .map {
                InlineKeyboardButton(
                    (if (spyLeader && it in spies) "${Configuration.SPY_MARK} " else "") + it.name,
                    callback_data = SquadChoiceCallbackData(chatId, it.id).encoded,
                )
            }
            .toMarkUp()
    ).join()
}
