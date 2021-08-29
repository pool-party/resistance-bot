package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.message.HELP_TAG
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.chatId
import com.github.pool_party.resistance_bot.utils.makeUserLink
import com.github.pool_party.resistance_bot.utils.sendMessageLogging

class TagCommand(private val stateStorage: StateStorage) :
    AbstractCommand("tag", "notifies players delaying a game", HELP_TAG) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId
        val state = stateStorage.getGameState(chatId)

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
            sendMessageLogging(chatId, "No one to wait for", replyTo = message.message_id)
            return
        }

        sendMessageLogging(
            chatId,
            """
            |TODO: waiting for:
            |${members.joinToString("|\n") { makeUserLink(it.name, it.id) }}
            """.trimMargin(),
        )
    }
}
