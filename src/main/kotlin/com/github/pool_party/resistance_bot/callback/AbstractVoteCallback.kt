package com.github.pool_party.resistance_bot.callback

import com.elbekD.bot.Bot
import com.elbekD.bot.types.CallbackQuery
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.chooseSquad
import com.github.pool_party.resistance_bot.name
import com.github.pool_party.resistance_bot.state.Member
import com.github.pool_party.resistance_bot.state.SquadStorage
import com.github.pool_party.resistance_bot.state.State
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.state.Vote
import com.github.pool_party.resistance_bot.state.VoteStorage

interface VoteCallbackData {
    val gameChatId: Long
    val verdict: Boolean
}

abstract class AbstractVoteCallback(
    private val voteStorage: VoteStorage,
    protected val stateStorage: StateStorage,
    protected val squadStorage: SquadStorage,
) : Callback {

    abstract suspend fun getMemberNumber(voteCallbackData: VoteCallbackData): Int?

    abstract suspend fun Bot.processResults(chatId: Long, state: State, votes: List<Vote>)

    override suspend fun Bot.process(callbackQuery: CallbackQuery, callbackData: CallbackData) {
        val voteCallbackData = callbackData as? VoteCallbackData ?: return
        val callbackQueryId = callbackQuery.id
        val gameChatId = voteCallbackData.gameChatId
        val user = callbackQuery.from
        voteStorage.set(gameChatId, Member(user.id.toLong(), user.name), voteCallbackData.verdict)
        val messageId = callbackQuery.message?.message_id

        answerCallbackQuery(callbackQueryId)

        if (messageId == null) {
            return
        }

        //TODO Make unique symbols for different votes.
        editMessageText(
            user.id,
            messageId,
            text = if (voteCallbackData.verdict) Configuration.APPROVE_MARK else Configuration.REJECT_MARK
        )

        val votes = voteStorage[gameChatId]

        if (getMemberNumber(voteCallbackData).let { it != null && it <= votes.size }) {
            voteStorage.clear(gameChatId)

            val state = stateStorage[gameChatId] ?: return

            processResults(gameChatId, state, votes)
        }
    }

    protected suspend fun Bot.nextSquadChoice(chatId: Long, state: State) {
        val members = state.members
        members += members.removeFirst()
        chooseSquad(chatId, stateStorage)
    }
}
