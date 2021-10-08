package com.github.pool_party.resistance_bot.action

import com.elbekD.bot.Bot
import com.elbekD.bot.types.InlineKeyboardButton
import com.github.pool_party.flume.utils.sendMessageLogging
import com.github.pool_party.flume.utils.toMarkUp
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.callback.CallbackData
import com.github.pool_party.resistance_bot.callback.MissionVoteCallbackData
import com.github.pool_party.resistance_bot.callback.SquadVoteCallbackData
import com.github.pool_party.resistance_bot.message.MISSION_VOTE
import com.github.pool_party.resistance_bot.message.TEAM_VOTE

private suspend fun Bot.vote(text: String, memberIds: List<Long>, verdictVoteCallBackData: (Boolean) -> CallbackData) {

    fun makeButton(text: String, verdict: Boolean) =
        InlineKeyboardButton(text, callback_data = verdictVoteCallBackData(verdict).encoded)

    for (memberId in memberIds) {
        sendMessageLogging(
            memberId,
            text,
            listOf(
                makeButton(Configuration.APPROVE_MARK, true),
                makeButton(Configuration.REJECT_MARK, false),
            ).toMarkUp()
        ).join()
    }
}

suspend fun Bot.squadVote(chatId: Long, memberIds: List<Long>) =
    vote(TEAM_VOTE, memberIds) { SquadVoteCallbackData(chatId, it) }

suspend fun Bot.missionVote(chatId: Long, memberIds: List<Long>) =
    vote(MISSION_VOTE, memberIds) { MissionVoteCallbackData(chatId, it, memberIds.size) }
