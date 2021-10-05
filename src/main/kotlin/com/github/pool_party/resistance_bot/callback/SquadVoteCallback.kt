package com.github.pool_party.resistance_bot.callback

import com.elbekD.bot.Bot
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.missionVote
import com.github.pool_party.resistance_bot.message.TEAM_APPROVED
import com.github.pool_party.resistance_bot.message.gameResult
import com.github.pool_party.resistance_bot.message.teamRejected
import com.github.pool_party.resistance_bot.state.GameState
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.state.Vote
import com.github.pool_party.resistance_bot.utils.goToBotMarkup
import com.github.pool_party.telegram_bot_utils.utils.sendMessageLogging
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
        val isApproved = votes.count { it.second } >= votes.size

        if (isApproved) {
            val squad = state.squad ?: return

            sendMessageLogging(chatId, TEAM_APPROVED, goToBotMarkup())
            missionVote(chatId, squad.map { it.id })
            return
        }

        val membersAgainst = votes.filter { !it.second }.map { it.first }

        sendMessageLogging(chatId, teamRejected(membersAgainst))

        if (++state.squadRejections >= Configuration.REJECTIONS_NUMBER) {
            sendMessageLogging(chatId, gameResult(true))
            stateStorage.gameOver(chatId)
            return
        }

        nextSquadChoice(chatId, state)
    }
}
