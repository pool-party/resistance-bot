package com.github.pool_party.resistance.action

import com.elbekD.bot.Bot
import com.elbekD.bot.types.InlineKeyboardButton
import com.github.pool_party.resistance.Configuration
import com.github.pool_party.resistance.callback.CallbackData
import com.github.pool_party.resistance.callback.MissionVoteCallbackData
import com.github.pool_party.resistance.callback.SquadVoteCallbackData
import com.github.pool_party.resistance.toMarkUp

private suspend fun Bot.vote(text: String, memberIds: List<Long>, verdictVoteCallBackData: (Boolean) -> CallbackData) {

    fun makeButton(text: String, verdict: Boolean) =
        InlineKeyboardButton(text, callback_data = verdictVoteCallBackData(verdict).encoded)

    for (memberId in memberIds) {
        sendMessage(
            memberId,
            text,
            markup = listOf(
                makeButton(Configuration.APPROVE_MARK,true),
                makeButton(Configuration.REJECT_MARK, false),
            ).toMarkUp()
        ).join()
    }
}

suspend fun Bot.squadVote(chatId: Long, memberIds: List<Long>) =
    vote("TODO: squad vote", memberIds) { SquadVoteCallbackData(chatId, it) }

suspend fun Bot.missionVote(chatId: Long, memberIds: List<Long>) =
    vote("TODO: mission vote", memberIds) { MissionVoteCallbackData(chatId, it, memberIds.size) }
