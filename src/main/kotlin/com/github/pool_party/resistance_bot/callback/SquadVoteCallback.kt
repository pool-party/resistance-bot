package com.github.pool_party.resistance_bot.callback

import com.elbekD.bot.Bot
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.missionVote
import com.github.pool_party.resistance_bot.state.GameState
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.state.Vote
import com.github.pool_party.resistance_bot.utils.goToBotMarkup
import com.github.pool_party.resistance_bot.utils.makeUserLink
import com.github.pool_party.resistance_bot.utils.sendMessageLogging
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("squad")
data class SquadVoteCallbackData(override val gameChatId: Long, override val verdict: Boolean) :
    CallbackData(), VoteCallbackData

class SquadVoteCallback(stateStorage: StateStorage) : AbstractVoteCallback(stateStorage) {

    override val callbackDataKClass = SquadVoteCallbackData::class

    override suspend fun getMemberNumber(voteCallbackData: VoteCallbackData): Int? =
        stateStorage.getGameState(voteCallbackData.gameChatId)?.members?.size

    override suspend fun Bot.processResults(chatId: Long, state: GameState, votes: List<Vote>) {
        val result = votes.count { it.second } >= votes.size
        val downVoters = votes.asSequence().filter { !it.second }.map { it.first }

        if (result) {
            val squad = state.squad
            if (squad == null) {
                sendMessageLogging(chatId, "TODO: gg 500")
                return
            }

            sendMessageLogging(chatId, "TODO: SQUAD chosen", goToBotMarkup())
            missionVote(chatId, squad.map { it.id })
            return
        }

        sendMessageLogging(
            chatId,
            """
                |TODO: SQUAD choice failed
                |against:
                |${downVoters.joinToString("\n|") { makeUserLink(it.name, it.id) }}
            """.trimMargin("|"),
        )

        if (++state.squadRejections >= Configuration.REJECTIONS_NUMBER) {
            sendMessageLogging(chatId, "TODO: spies won")
            stateStorage.gameOver(chatId)
            return
        }
        nextSquadChoice(chatId, state)
    }
}
