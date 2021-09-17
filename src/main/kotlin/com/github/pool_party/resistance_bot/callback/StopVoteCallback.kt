package com.github.pool_party.resistance_bot.callback

import com.elbekD.bot.Bot
import com.elbekD.bot.types.CallbackQuery
import com.github.pool_party.resistance_bot.command.StopCommand.Companion.processResults
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.editMessageReplyMarkupLogging
import com.github.pool_party.resistance_bot.utils.makeStopVoteMarkup
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

        answerCallbackQuery(callbackQuery.id)
        val messageId = callbackQuery.message?.message_id ?: return

        when {
            stopVotes.size >= state.members.size -> processResults(stateStorage, gameChatId, messageId)
            previousVerdict != newVerdict ->
                editMessageReplyMarkupLogging(gameChatId, messageId, makeStopVoteMarkup(gameChatId, stopVotes))
        }
    }
}
