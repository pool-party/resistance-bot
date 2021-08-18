package com.github.pool_party.resistance.callback

import com.elbekD.bot.Bot
import com.elbekD.bot.types.CallbackQuery
import com.elbekD.bot.types.InlineKeyboardMarkup
import com.github.pool_party.resistance.Configuration
import com.github.pool_party.resistance.action.vote
import com.github.pool_party.resistance.decodeInner
import com.github.pool_party.resistance.makeUserLink
import com.github.pool_party.resistance.name
import com.github.pool_party.resistance.state.SquadStorage
import com.github.pool_party.resistance.state.StateStorage
import kotlinx.serialization.Serializable

@Serializable
data class SquadChoiceCallbackData(val gameChatId: Long, val personId: Long) {
    companion object {
        fun of(string: String) = decodeInner<SquadChoiceCallbackData>(string)
    }
}

class SquadChoiceCallback(private val stateStorage: StateStorage, private val squadStorage: SquadStorage) : Callback {

    override val callbackAction = CallbackAction.SQUAD_CHOICE

    private val chosen = """ 🔫"""

    override suspend fun Bot.process(callbackQuery: CallbackQuery, callbackData: CallbackData) {
        val squadChoiceCallbackData = SquadChoiceCallbackData.of(callbackData.otherData)

        val callbackQueryId = callbackQuery.id
        val user = callbackQuery.from
        val userId = user.id
        val message = callbackQuery.message
        val replyMarkup = message?.reply_markup
        val gameChatId = squadChoiceCallbackData.gameChatId
        val state = stateStorage[gameChatId]

        if (replyMarkup == null || state == null) {
            answerCallbackQuery(callbackQueryId)
            return
        }

        val buttons = replyMarkup.inline_keyboard.flatMap { it.asSequence() }
        val clicked = buttons.find {
            val parsed = SquadChoiceCallbackData.of(CallbackData.of(it.callback_data!!).otherData).personId
            return@find parsed == squadChoiceCallbackData.personId
        }

        if (clicked == null) {
            answerCallbackQuery(callbackQueryId)
            return
        }

        val clickedChosen = clicked.text.endsWith(chosen)

        val warriors = buttons.asSequence().map { it.text }.filter { it.endsWith(chosen) }.toList()

        // TODO
        if (!clickedChosen && warriors.size >= Configuration.PLAYERS_MISSION - 1) {
            answerCallbackQuery(callbackQueryId, "TODO: good job")
            // TODO edit -> chosen team
            deleteMessage(userId, message.message_id)

            sendMessage(
                gameChatId,
                """
                    |TODO:
                    |${makeUserLink(user.name, user.id.toLong())} has chosen:

                    |${(warriors + clicked.text).asSequence().map { it.removeSuffix(chosen) }.joinToString("\n|")}
                """.trimMargin("|"),
                parseMode = "MarkdownV2"
            )

            val memberIds = buttons.asSequence()
                .filter { it.text.endsWith(chosen) }
                .map { SquadChoiceCallbackData.of(CallbackData.of(it.callback_data!!).otherData).personId }
                .plus(squadChoiceCallbackData.personId)
                .toList()

            squadStorage[gameChatId] = memberIds

            vote(gameChatId, state.members.map { it.id }, VoteType.SQUAD)
            return
        }

        editMessageReplyMarkup(
            userId,
            message.message_id,
            markup =
                InlineKeyboardMarkup(
                    listOf(
                        buttons.map {
                            if (it != clicked) {
                                return@map it
                            }

                            val newText =
                                if (clickedChosen) clicked.text.removeSuffix(chosen)
                                else "${clicked.text}$chosen"
                            return@map it.copy(text = newText)
                        }
                    )
                )
        )

        answerCallbackQuery(callbackQueryId)
    }
}
