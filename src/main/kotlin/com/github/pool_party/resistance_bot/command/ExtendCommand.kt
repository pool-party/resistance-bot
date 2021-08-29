package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.message.HELP_EXTEND
import com.github.pool_party.resistance_bot.message.ON_NO_REGISTRATION
import com.github.pool_party.resistance_bot.message.ON_ONGOING_GAME
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.chatId
import com.github.pool_party.resistance_bot.utils.inc
import com.github.pool_party.resistance_bot.utils.sendMessageLogging

class ExtendCommand(private val stateStorage: StateStorage) :
    AbstractCommand(
        "extend",
        "add extra ${Configuration.REGISTRATION_EXTEND.inWholeSeconds} for registration",
        HELP_EXTEND,
        CommandType.UTILS,
    ) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId
        val state = stateStorage.getRegistrationState(chatId)

        if (state == null) {
            sendMessageLogging(chatId, ON_NO_REGISTRATION)
            return
        }

        state.withStarted {
            if (it) {
                sendMessageLogging(chatId, ON_ONGOING_GAME)
                return@withStarted true
            }

            state.registrationExtendCounter++
            sendMessageLogging(chatId, "TODO: successfully extended, TODO: time left")
            false
        }
    }
}
