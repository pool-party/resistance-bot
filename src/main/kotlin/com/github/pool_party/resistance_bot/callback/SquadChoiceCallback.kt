package com.github.pool_party.resistance_bot.callback

import com.elbekD.bot.Bot
import com.elbekD.bot.types.CallbackQuery
import com.elbekD.bot.types.InlineKeyboardMarkup
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.squadVote
import com.github.pool_party.resistance_bot.makeUserLink
import com.github.pool_party.resistance_bot.name
import com.github.pool_party.resistance_bot.state.SquadStorage
import com.github.pool_party.resistance_bot.state.StateStorage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
@SerialName("c")
data class SquadChoiceCallbackData(
    @SerialName("a")
    override val gameChatId: Long,
    @SerialName("b")
    val personId: Long,
) : CallbackData() {
    companion object {
        fun of(string: String) = Json { ignoreUnknownKeys = true }.decodeFromString<SquadChoiceCallbackData>(string)
    }
}

class SquadChoiceCallback(private val stateStorage: StateStorage, private val squadStorage: SquadStorage) : Callback {

    override val callbackDataKClass = SquadChoiceCallbackData::class

    private val chosen = Configuration.NINJA_MARK

    override suspend fun Bot.process(callbackQuery: CallbackQuery, callbackData: CallbackData) {
        val squadChoiceCallbackData = callbackData as? SquadChoiceCallbackData ?: return

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
            val parsed = SquadChoiceCallbackData.of(it.callback_data!!).personId
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
            editMessageText(
                userId,
                message.message_id,
                text = """
                    |TODO: chosen:
                    |${(warriors + clicked.text).asSequence().map { it.removeSuffix(chosen) }.joinToString("\n|")}
                """.trimMargin("|")
            )

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
                .map { SquadChoiceCallbackData.of(it.callback_data!!).personId }
                .plus(squadChoiceCallbackData.personId)
                .toList()

            squadStorage[gameChatId] = memberIds

            squadVote(gameChatId, state.members.map { it.id })
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
