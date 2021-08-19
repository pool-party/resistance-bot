package com.github.pool_party.resistance_bot.action

import com.elbekD.bot.Bot
import com.elbekD.bot.types.InlineKeyboardButton
import com.github.pool_party.resistance_bot.callback.SquadChoiceCallbackData
import com.github.pool_party.resistance_bot.makeUserLink
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.toMarkUp

suspend fun Bot.chooseSquad(chatId: Long, stateStorage: StateStorage) {

    val state = stateStorage[chatId]
    val leader = state?.members?.firstOrNull()

    if (state == null || leader == null) {
        sendMessage(chatId, "TODO: game over wtf")
        return
    }

    sendMessage(
        chatId,
        """
            TODO: state:

            ```
                 score: resistance ${state.resistancePoints} - ${state.spyPoints} red spies
            rejections: ${state.squadRejections}
            ```

            next leader: ${makeUserLink(leader.name, leader.id)}
        """.trimIndent(),
        parseMode = "MarkdownV2"
    )

    sendMessage(
        leader.id,
        "TODO: choose",
        markup = state.members
            .asSequence()
            .map { InlineKeyboardButton(it.name, callback_data = SquadChoiceCallbackData(chatId, it.id).encoded) }
            .toList()
            .toMarkUp()
    )
}
