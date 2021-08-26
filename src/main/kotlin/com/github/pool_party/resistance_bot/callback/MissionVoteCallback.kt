package com.github.pool_party.resistance_bot.callback

import com.elbekD.bot.Bot
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.state.State
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.state.Vote
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mission")
data class MissionVoteCallbackData(
    override val gameChatId: Long,
    override val verdict: Boolean,
    val memberNumber: Int,
) : CallbackData(), VoteCallbackData

class MissionVoteCallback(stateStorage: StateStorage) : AbstractVoteCallback(stateStorage) {

    override val callbackDataKClass = MissionVoteCallbackData::class

    override suspend fun getMemberNumber(voteCallbackData: VoteCallbackData): Int? =
        (voteCallbackData as? MissionVoteCallbackData)?.memberNumber

    override suspend fun Bot.processResults(chatId: Long, state: State, votes: List<Vote>) {
        val result = votes.asSequence().map { it.second }.all { it }

        if (result) {
            sendMessage(chatId, "TODO: mission has completed successfully").join()

            if (++state.resistancePoints >= Configuration.WIN_NUMBER) {
                sendMessage(chatId, "TODO: resistance won")
                stateStorage.gameOver(chatId)
                return
            }
        } else {
            sendMessage(chatId, "TODO: mission has failed").join()

            if (++state.spyPoints >= Configuration.WIN_NUMBER) {
                sendMessage(chatId, "TODO: spies won")
                stateStorage.gameOver(chatId)
                return
            }
        }

        nextSquadChoice(chatId, state)
    }
}
