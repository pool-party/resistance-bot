package com.github.pool_party.resistance.callback

import com.elbekD.bot.Bot
import com.github.pool_party.resistance.Configuration
import com.github.pool_party.resistance.action.missionVote
import com.github.pool_party.resistance.makeUserLink
import com.github.pool_party.resistance.state.Member
import com.github.pool_party.resistance.state.SquadStorage
import com.github.pool_party.resistance.state.State
import com.github.pool_party.resistance.state.StateStorage
import com.github.pool_party.resistance.state.VoteStorage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("s")
data class SquadVoteCallbackData(
    @SerialName("a")
    override val gameChatId: Long,
    @SerialName("b")
    override val verdict: Boolean,
) : CallbackData(), VoteCallbackData

class SquadVoteCallback(
    voteStorage: VoteStorage,
    stateStorage: StateStorage,
    squadStorage: SquadStorage,
) : AbstractVoteCallback(voteStorage, stateStorage, squadStorage) {

    override val callbackDataKClass = SquadVoteCallbackData::class

    override suspend fun getMemberNumber(voteCallbackData: VoteCallbackData): Int? =
        stateStorage[voteCallbackData.gameChatId]?.members?.size

    override suspend fun Bot.processResults(chatId: Long, state: State, votes: List<Pair<Member, Boolean>>) {
        val result = votes.count { it.second } >= votes.size
        val downVoters = votes.asSequence().filter { !it.second }.map { it.first }

        if (result) {
            val squad = squadStorage[chatId]
            if (squad == null) {
                sendMessage(chatId, "TODO: gg 500")
                return
            }

            sendMessage(chatId, "TODO: SQUAD chosen")
            missionVote(chatId, squad)
            return
        }

        sendMessage(
            chatId,
            """
                |TODO: SQUAD choice failed
                |against:
                |${downVoters.joinToString("\n|") { makeUserLink(it.name, it.id) }}
            """.trimMargin("|"),
            "MarkdownV2"
        )

        if (++state.squadRejections >= Configuration.REJECTIONS_NUMBER) {
            sendMessage(chatId, "TODO: spies won")
            stateStorage.gameOver(chatId)
            return
        }
        nextSquadChoice(chatId, state)
    }
}
