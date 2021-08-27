package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.startGame
import com.github.pool_party.resistance_bot.message.HELP_START
import com.github.pool_party.resistance_bot.message.INIT_MSG
import com.github.pool_party.resistance_bot.message.ON_NO_REGISTRATION_START
import com.github.pool_party.resistance_bot.message.ON_REGISTRATION_REPEAT
import com.github.pool_party.resistance_bot.message.onNewPlayerUpdate
import com.github.pool_party.resistance_bot.message.onRegistrationSuccess
import com.github.pool_party.resistance_bot.state.Coder
import com.github.pool_party.resistance_bot.state.Member
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.addBotMarkup
import com.github.pool_party.resistance_bot.utils.chatId
import com.github.pool_party.resistance_bot.utils.makeRegistrationMarkup
import com.github.pool_party.resistance_bot.utils.name

class StartCommand(private val stateStorage: StateStorage, private val longCoder: Coder<Long>) :
    AbstractCommand("start", "finish the registration and begin a game", HELP_START) {

    override suspend fun Bot.action(message: Message, args: List<String>) = when {
        message.chat.type.let { it == "group" || it == "supergroup" } -> startGameCommand(message)
        args.size == 1 -> register(message, args[0])
        else -> sendGreetings(message)
    }

    private suspend fun Bot.register(message: Message, code: String) {
        val chatId = longCoder.decode(code)
        val state = stateStorage[chatId]
        val senderId = message.chatId
        val senderName = message.from?.name ?: return

        if (state == null) {
            sendGreetings(message)
            return
        }

        var startingGame = false

        state.withStarted { started ->
            if (started) {
                sendMessage(senderId, "TODO: the game has already started")
                return@withStarted true
            }

            val members = state.members

            if (members.asSequence().map { it.id }.contains(senderId)) {
                sendMessage(senderId, ON_REGISTRATION_REPEAT, "MarkdownV2")
                return@withStarted false
            }

            members += Member(senderId, senderName)
            val registrationMessageId = state.registrationMessageId

            sendMessage(senderId, onRegistrationSuccess(state.chatName), "MarkdownV2")

            editMessageText(
                chatId,
                registrationMessageId,
                text = onNewPlayerUpdate(members),
                parseMode = "MarkdownV2",
                markup = makeRegistrationMarkup(code),
            ).join()

            startingGame = members.size >= Configuration.PLAYERS_GAME_MINIMUM
            startingGame
        }

        if (startingGame) startGame(chatId, stateStorage)
    }

    private fun Bot.sendGreetings(message: Message) {
        sendMessage(message.chatId, INIT_MSG, markup = addBotMarkup())
    }

    private suspend fun Bot.startGameCommand(message: Message) {
        val chatId = message.chatId
        val state = stateStorage[chatId]

        if (state == null) {
            sendMessage(chatId, ON_NO_REGISTRATION_START, "MarkdownV2")
            return
        }

        state.tryStartAndDo { startGame(chatId, stateStorage) }
    }
}
