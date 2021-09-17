package com.github.pool_party.resistance_bot.callback

import com.elbekD.bot.Bot
import com.elbekD.bot.types.CallbackQuery
import com.github.pool_party.resistance_bot.message.ON_REGISTRATION_STOP
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.deleteMessageLogging
import com.github.pool_party.resistance_bot.utils.editMessageReplyMarkupLogging
import com.github.pool_party.resistance_bot.utils.makeStopVoteMarkup
import com.github.pool_party.resistance_bot.utils.sendMessageLogging
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mu.KotlinLogging

@Serializable
@SerialName("stop")
data class StopCallbackData(override val gameChatId: Long, val verdict: Boolean) : CallbackData()

class StopVoteCallback(private val stateStorage: StateStorage) : Callback {

    private val logger = KotlinLogging.logger {}

    override val callbackDataKClass = StopCallbackData::class

    override suspend fun Bot.process(callbackQuery: CallbackQuery, callbackData: CallbackData) {
        val stopCallbackData = callbackData as? StopCallbackData ?: return
        val gameChatId = stopCallbackData.gameChatId
        val user = callbackQuery.from
        val state = stateStorage.getGameState(gameChatId) ?: return
        val stopVotes = state.stopVotes.get()
        if (stopVotes == null) {
            logger.warn { "Stop vote is null" }
            return
        }

        val newVerdict = stopCallbackData.verdict
        val previousVerdict = stopVotes.put(user.id, newVerdict)

        when {
            stopVotes.size >= state.members.size -> {
                callbackQuery.message?.let {
                    deleteMessageLogging(gameChatId, it.message_id)
                }
                state.stopVotes.set(null)

                // TODO copy paste?
                val (upVotes, downVotes) = stopVotes.values.partition { it }.toList().map { it.size }

                if (upVotes > 1 && upVotes > downVotes) {
                    stateStorage.gameOver(gameChatId)
                    sendMessageLogging(gameChatId, ON_REGISTRATION_STOP)
                } else {
                    sendMessageLogging(gameChatId, "Failed to stop the game")
                }
            }
            previousVerdict != newVerdict -> {
                callbackQuery.message?.let {
                    editMessageReplyMarkupLogging(
                        gameChatId,
                        it.message_id,
                        makeStopVoteMarkup(gameChatId, stopVotes),
                    )
                }
            }
        }

        answerCallbackQuery(callbackQuery.id)
    }
}
