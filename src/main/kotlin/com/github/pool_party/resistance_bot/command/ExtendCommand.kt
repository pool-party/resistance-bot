package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.message.HELP_EXTEND
import com.github.pool_party.resistance_bot.message.ON_IN_GAME_EXTEND
import com.github.pool_party.resistance_bot.message.ON_NO_REGISTRATION
import com.github.pool_party.resistance_bot.message.ON_ONGOING_GAME
import com.github.pool_party.resistance_bot.message.onExtend
import com.github.pool_party.resistance_bot.state.GameState
import com.github.pool_party.resistance_bot.state.RegistrationState
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.inc
import com.github.pool_party.telegram_bot_utils.interaction.command.AbstractCommand
import com.github.pool_party.telegram_bot_utils.utils.chatId
import com.github.pool_party.telegram_bot_utils.utils.sendMessageLogging

class ExtendCommand(private val stateStorage: StateStorage) :
    AbstractCommand(
        "extend",
        "add extra ${Configuration.REGISTRATION_EXTEND.inWholeSeconds}s for registration",
        HELP_EXTEND,
    ) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId

        when (val state = stateStorage.getState(chatId)) {
            null -> sendMessageLogging(chatId, ON_NO_REGISTRATION)

            is GameState -> sendMessageLogging(chatId, ON_IN_GAME_EXTEND)

            is RegistrationState -> {
                state.withStarted {
                    if (it) {
                        sendMessageLogging(chatId, ON_ONGOING_GAME)
                        return@withStarted true
                    }

                    state.registrationExtendCounter++
                    sendMessageLogging(chatId, onExtend(null))
                    false
                }
            }
        }
    }
}
