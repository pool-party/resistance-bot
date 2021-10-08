package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.flume.interaction.command.AbstractCommand
import com.github.pool_party.flume.utils.chatId
import com.github.pool_party.flume.utils.sendMessageLogging
import com.github.pool_party.resistance_bot.message.HELP_TAG
import com.github.pool_party.resistance_bot.message.ON_NO_REGISTRATION
import com.github.pool_party.resistance_bot.message.tagAfkPlayers
import com.github.pool_party.resistance_bot.state.StateStorage

class TagCommand(private val stateStorage: StateStorage) :
    AbstractCommand("tag", "notify players delaying a game", HELP_TAG) {

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
            sendMessageLogging(chatId, ON_NO_REGISTRATION, replyTo = message.message_id)
            return
        }

        sendMessageLogging(chatId, tagAfkPlayers(members))
    }
}
