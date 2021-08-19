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

    abstract suspend fun Bot.processResults(
        chatId: Long,
        result: Boolean,
        state: State,
        downVoters: List<Member>,
    )

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

        val vote = voteStorage[gameChatId]

        if (getMemberNumber(voteCallbackData).let { it != null && it <= vote.size }) {
            val result = vote.count { it.second } >= vote.size // TODO >
            voteStorage.clear(gameChatId)

            val state = stateStorage[gameChatId]
            if (state == null) {
                sendMessage(gameChatId, "TODO: game over kekw")
                return
            }

            processResults(
                gameChatId,
                result,
                state,
                vote.asSequence().filter { !it.second }.map { it.first }.toList(),
            )
        }
    }

    protected suspend fun Bot.nextSquadChoice(chatId: Long, state: State) {
        val members = state.members
        members += members.removeFirst()
        chooseSquad(chatId, stateStorage)
    }
}
