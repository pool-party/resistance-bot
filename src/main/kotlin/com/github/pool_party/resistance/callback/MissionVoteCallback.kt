package com.github.pool_party.resistance.callback

import com.elbekD.bot.Bot
import com.github.pool_party.resistance.Configuration
import com.github.pool_party.resistance.state.Member
import com.github.pool_party.resistance.state.SquadStorage
import com.github.pool_party.resistance.state.State
import com.github.pool_party.resistance.state.StateStorage
import com.github.pool_party.resistance.state.VoteStorage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("m")
data class MissionVoteCallbackData(
    @SerialName("a")
    override val gameChatId: Long,
    @SerialName("b")
    override val verdict: Boolean,
    @SerialName("c")
    val memberNumber: Int,
) : CallbackData(), VoteCallbackData

class MissionVoteCallback(
    voteStorage: VoteStorage,
    stateStorage: StateStorage,
    squadStorage: SquadStorage,
) : AbstractVoteCallback(voteStorage, stateStorage, squadStorage) {

    override val callbackDataKClass = MissionVoteCallbackData::class

    override suspend fun getMemberNumber(voteCallbackData: VoteCallbackData): Int? =
        (voteCallbackData as? MissionVoteCallbackData)?.memberNumber

    override suspend fun Bot.processResults(chatId: Long, result: Boolean, state: State, downVoters: List<Member>) {
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
