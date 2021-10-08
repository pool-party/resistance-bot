package com.github.pool_party.resistance_bot.callback

import com.elbekD.bot.Bot
import com.elbekD.bot.types.CallbackQuery
import com.github.pool_party.flume.interaction.callback.Callback
import com.github.pool_party.flume.utils.editMessageTextLogging
import com.github.pool_party.flume.utils.name
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.chooseSquad
import com.github.pool_party.resistance_bot.state.GameState
import com.github.pool_party.resistance_bot.state.Member
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.state.Vote

interface VoteCallbackData {
    val gameChatId: Long
    val verdict: Boolean
}

abstract class AbstractVoteCallback(protected val stateStorage: StateStorage) : Callback<CallbackData> {

    abstract suspend fun getMemberNumber(voteCallbackData: VoteCallbackData): Int?

    abstract suspend fun Bot.processResults(chatId: Long, state: GameState, votes: List<Vote>)

    override suspend fun Bot.process(callbackQuery: CallbackQuery, callbackData: CallbackData) {
        val voteCallbackData = callbackData as? VoteCallbackData ?: return
        val gameChatId = voteCallbackData.gameChatId
        val state = stateStorage.getGameState(gameChatId) ?: return
        val user = callbackQuery.from
        val newVote = state.vote(Member(user.id.toLong(), user.name), voteCallbackData.verdict)
        val messageId = callbackQuery.message?.message_id

        answerCallbackQuery(callbackQuery.id)

        if (messageId == null) return

        // TODO Make unique symbols for different votes.
        editMessageTextLogging(
            user.id.toLong(),
            messageId,
            text = if (voteCallbackData.verdict) Configuration.APPROVE_MARK else Configuration.REJECT_MARK
        )

        val voteList = state.votes.values.toList()

        if (newVote && getMemberNumber(voteCallbackData).let { it != null && it <= voteList.size }) {
            // This block of code is executed only by the first succeeded thread.
            state.votes.clear()
            processResults(gameChatId, state, voteList)
        }
    }

    protected suspend fun Bot.nextSquadChoice(chatId: Long, state: GameState) {
        state.nextLeader()
        chooseSquad(chatId, stateStorage)
    }
}
