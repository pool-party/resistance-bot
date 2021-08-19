package com.github.pool_party.resistance.callback

import com.elbekD.bot.Bot
import com.elbekD.bot.types.CallbackQuery
import com.github.pool_party.resistance.Configuration
import com.github.pool_party.resistance.action.chooseSquad
import com.github.pool_party.resistance.name
import com.github.pool_party.resistance.state.Member
import com.github.pool_party.resistance.state.SquadStorage
import com.github.pool_party.resistance.state.State
import com.github.pool_party.resistance.state.StateStorage
import com.github.pool_party.resistance.state.VoteStorage

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

    abstract suspend fun Bot.processResults(chatId: Long, state: State, votes: List<Pair<Member, Boolean>>)

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

        editMessageText(
            user.id,
            messageId,
            text = if (voteCallbackData.verdict) Configuration.APPROVE_MARK else Configuration.REJECT_MARK
        )

        val votes = voteStorage[gameChatId]

        if (getMemberNumber(voteCallbackData).let { it != null && it <= votes.size }) {
            voteStorage.clear(gameChatId)

            val state = stateStorage[gameChatId]
            if (state == null) {
                sendMessage(gameChatId, "TODO: game over kekw")
                return
            }

            processResults(gameChatId, state, votes)
//                vote.asSequence().filter { !it.second }.map { it.first }.toList(),
        }
    }

    protected suspend fun Bot.nextSquadChoice(chatId: Long, state: State) {
        val members = state.members
        members += members.removeFirst()
        chooseSquad(chatId, stateStorage)
    }
}
