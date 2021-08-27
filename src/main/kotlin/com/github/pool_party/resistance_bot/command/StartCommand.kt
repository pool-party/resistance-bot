package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.startGame
import com.github.pool_party.resistance_bot.message.HELP_START
import com.github.pool_party.resistance_bot.message.INIT_MSG
import com.github.pool_party.resistance_bot.message.ON_NO_REGISTRATION
import com.github.pool_party.resistance_bot.message.ON_ONGOING_GAME
import com.github.pool_party.resistance_bot.message.ON_REGISTRATION_REPEAT
import com.github.pool_party.resistance_bot.message.onNewPlayerUpdate
import com.github.pool_party.resistance_bot.message.onRegistrationSuccess
import com.github.pool_party.resistance_bot.state.Coder
import com.github.pool_party.resistance_bot.state.Member
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.addBotMarkup
import com.github.pool_party.resistance_bot.utils.chatId
import com.github.pool_party.resistance_bot.utils.editMessageTextLogging
import com.github.pool_party.resistance_bot.utils.makeRegistrationMarkup
import com.github.pool_party.resistance_bot.utils.name
import com.github.pool_party.resistance_bot.utils.sendMessageLogging

class StartCommand(private val stateStorage: StateStorage, private val longCoder: Coder<Long>) :
    AbstractCommand(
        "start",
        "finish the registration and begin a game",
        HELP_START,
        CommandType.REGISTRATION,
    ) {

    override suspend fun Bot.action(message: Message, args: List<String>) = when {
        message.chat.type.let { it == "group" || it == "supergroup" } -> startGameCommand(message)
        args.size == 1 -> register(message, args[0])
        else -> sendGreetings(message)
    }

    private suspend fun Bot.register(message: Message, code: String) {
        val chatId = longCoder.decode(code)
        val state = stateStorage.getRegistrationState(chatId)
        val senderId = message.chatId
        val senderName = message.from?.name ?: return

        if (state == null) {
            sendGreetings(message)
            return
        }

        var startingGame = false

        state.withStarted { started ->
            if (started) {
                sendMessageLogging(senderId, ON_ONGOING_GAME)
                return@withStarted true
            }

            val members = state.members

            if (members.asSequence().map { it.id }.contains(senderId)) {
                sendMessageLogging(senderId, ON_REGISTRATION_REPEAT)
                return@withStarted false
            }

            members += Member(senderId, senderName)
            val registrationMessageId = state.registrationMessageId

            sendMessageLogging(senderId, onRegistrationSuccess(state.chatName))

            editMessageTextLogging(
                chatId,
                registrationMessageId,
                onNewPlayerUpdate(members),
                makeRegistrationMarkup(code),
            ).join()

            startingGame = members.size >= Configuration.PLAYERS_GAME_MAXIMUM
            startingGame
        }

        if (startingGame) startGame(chatId, stateStorage)
    }

    private fun Bot.sendGreetings(message: Message) {
        sendMessageLogging(message.chatId, INIT_MSG, addBotMarkup())
    }

    private suspend fun Bot.startGameCommand(message: Message) {
        val chatId = message.chatId
        val state = stateStorage.getRegistrationState(chatId)

        if (state == null) {
            sendMessageLogging(chatId, ON_NO_REGISTRATION)
            return
        }

        state.tryStartAndDo { startGame(chatId, stateStorage) }
    }
}
