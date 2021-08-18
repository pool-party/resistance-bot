package com.github.pool_party.resistance.action

import com.elbekD.bot.Bot
import com.elbekD.bot.types.InlineKeyboardButton
import com.github.pool_party.resistance.callback.CallbackAction
import com.github.pool_party.resistance.callback.CallbackData
import com.github.pool_party.resistance.callback.VoteCallbackData
import com.github.pool_party.resistance.callback.VoteType
import com.github.pool_party.resistance.encode
import com.github.pool_party.resistance.toMarkUp

suspend fun Bot.vote(chatId: Long, memberIds: List<Long>, voteType: VoteType) {

    fun makeButton(text: String, verdict: Boolean) = InlineKeyboardButton(
        text,
        callback_data = encode(
            CallbackData.of(CallbackAction.VOTE, VoteCallbackData(chatId, memberIds.size, verdict, voteType))
        )
    )

    for (memberId in memberIds) {
        sendMessage(
            memberId,
            "TODO: vote",
            markup = listOf(makeButton("""✅""",true), makeButton("""❌""", false)).toMarkUp()
        ).join()
    }
}
