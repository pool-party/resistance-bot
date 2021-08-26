package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.message.HELP_EXTEND
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.chatId
import com.github.pool_party.resistance_bot.utils.inc

class ExtendCommand(private val stateStorage: StateStorage) :
    AbstractCommand(
        "extend",
        "add extra ${Configuration.REGISTRATION_EXTEND.inWholeSeconds} seconds for registration",
        HELP_EXTEND,
    ) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId
        val state = stateStorage[chatId]

        if (state == null) {
            sendMessage(chatId, "TODO: no registration in progress")
            return
        }

        state.withStarted {
            if (it) {
                sendMessage(chatId, "TODO: already started")
                return@withStarted true
            }

            state.registrationExtendCounter++
            sendMessage(chatId, "TODO: successfully extended, TODO: time left")
            false
        }
    }
}
