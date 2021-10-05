package com.github.pool_party.resistance_bot.callback

import com.elbekD.bot.Bot
import com.elbekD.bot.types.CallbackQuery
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.squadVote
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.goToBotMarkup
import com.github.pool_party.resistance_bot.utils.makeUserLink
import com.github.pool_party.telegram_bot_utils.interaction.callback.Callback
import com.github.pool_party.telegram_bot_utils.utils.editMessageReplyMarkupLogging
import com.github.pool_party.telegram_bot_utils.utils.editMessageTextLogging
import com.github.pool_party.telegram_bot_utils.utils.name
import com.github.pool_party.telegram_bot_utils.utils.sendMessageLogging
import com.github.pool_party.telegram_bot_utils.utils.toMarkUp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("choice")
data class SquadChoiceCallbackData(override val gameChatId: Long, val personId: Long) : CallbackData() {
    companion object {
        fun of(string: String) = CallbackData.of(string) as SquadChoiceCallbackData
    }
}

class SquadChoiceCallback(private val stateStorage: StateStorage) : Callback<CallbackData> {

    override val callbackDataKClass = SquadChoiceCallbackData::class

    private val chosen = " ${Configuration.CHOSEN_MARK}"

    override suspend fun Bot.process(callbackQuery: CallbackQuery, callbackData: CallbackData) {
        val squadChoiceCallbackData = callbackData as? SquadChoiceCallbackData ?: return

        val callbackQueryId = callbackQuery.id
        val user = callbackQuery.from
        val userId = user.id
        val message = callbackQuery.message
        val replyMarkup = message?.reply_markup
        val gameChatId = squadChoiceCallbackData.gameChatId
        val state = stateStorage.getGameState(gameChatId)

        if (replyMarkup == null || state == null) {
            answerCallbackQuery(callbackQueryId)
            return
        }

        val buttons = replyMarkup.inline_keyboard.flatMap { it.asSequence() }
        val clicked = buttons.find {
            val parsed = SquadChoiceCallbackData.of(it.callback_data!!).personId
            return@find parsed == squadChoiceCallbackData.personId
        }

        if (clicked == null) {
            answerCallbackQuery(callbackQueryId)
            return
        }

        val clickedChosen = clicked.text.endsWith(chosen)

        val warriorMembers = buttons.asSequence()
            .filter { it.text.endsWith(chosen) || (!clickedChosen && it == clicked) }
            .map { SquadChoiceCallbackData.of(it.callback_data!!).personId }
            .mapNotNull { id -> state.members.find { it.id == id } }
            .toList()

        if (!clickedChosen && warriorMembers.size >= state.currentMissionAgentNumber - 1) {

            answerCallbackQuery(callbackQueryId, "TODO: good job")

            state.squad = warriorMembers

            editMessageTextLogging(
                userId.toLong(),
                message.message_id,
                """
                    |TODO: chosen:
                    |${warriorMembers.joinToString("\n|") { it.name }}
                """.trimMargin()
            )

            sendMessageLogging(
                gameChatId,
                """
                    |TODO:
                    |${makeUserLink(user.name, user.id.toLong())} has chosen:

                    |${warriorMembers.joinToString("\n|") { it.name }}
                """.trimMargin(),
                goToBotMarkup(),
            )

            squadVote(gameChatId, state.members.map { it.id })
            return
        }

        editMessageReplyMarkupLogging(
            userId.toLong(),
            message.message_id,
            markup = buttons.map {
                if (it != clicked) return@map it

                val newText =
                    if (clickedChosen) clicked.text.removeSuffix(chosen)
                    else "${clicked.text}$chosen"
                return@map it.copy(text = newText)
            }.toMarkUp()
        )

        answerCallbackQuery(callbackQueryId)
    }
}
