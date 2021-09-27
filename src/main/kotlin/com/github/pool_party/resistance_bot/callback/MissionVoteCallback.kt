package com.github.pool_party.resistance_bot.callback

import com.elbekD.bot.Bot
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.Configuration.Stickers
import com.github.pool_party.resistance_bot.state.GameState
import com.github.pool_party.resistance_bot.state.RoundResult
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.state.Vote
import com.github.pool_party.resistance_bot.utils.sendMessageLogging
import com.github.pool_party.resistance_bot.utils.sendStickerLogging
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

    override suspend fun Bot.processResults(chatId: Long, state: GameState, votes: List<Vote>) {
        val result = votes.asSequence().map { it.second }.all { it }

        if (result) {
            sendStickerLogging(chatId, Stickers.SUCCESS)
            sendMessageLogging(chatId, "TODO: mission has completed successfully").join()

            if (++state.resistancePoints >= Configuration.WIN_NUMBER) {
                sendMessageLogging(chatId, "TODO: resistance won")
                stateStorage.gameOver(chatId)
                return
            }
        } else {
            sendStickerLogging(chatId, Stickers.FAIL)
            sendMessageLogging(chatId, "TODO: mission has failed").join()

            if (++state.spyPoints >= Configuration.WIN_NUMBER) {
                sendMessageLogging(chatId, "TODO: spies won")
                stateStorage.gameOver(chatId)
                return
            }
        }

        state.history += RoundResult(result, state.squad!!.map { it.name })
        nextSquadChoice(chatId, state)
    }
}
