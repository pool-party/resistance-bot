package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.message.HELP_EXTEND
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.chatId
import com.github.pool_party.resistance_bot.utils.makeUserLink

class TagCommand(private val stateStorage: StateStorage) :
    AbstractCommand("tag", "tag the ones you are waiting for", HELP_EXTEND) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId
        val state = stateStorage[chatId]

        val members = if (state == null) {
            listOf()
        } else {
            if (state.squad == null) {
                listOf(state.leader)
            } else {
                // TODO votes for squad and for mission
                val voters = state.votes.keys
                state.members.filter { it.id !in voters }
            }
        }

        if (members.isEmpty()) {
            sendMessage(chatId, "No one to wait for", replyTo = message.message_id)
            return
        }

        sendMessage(
            chatId,
            """
            |TODO: waiting for:
            |${members.joinToString("|\n") { makeUserLink(it.name, it.id) }}
            """.trimMargin(),
            "MarkdownV2",
        )
    }
}
