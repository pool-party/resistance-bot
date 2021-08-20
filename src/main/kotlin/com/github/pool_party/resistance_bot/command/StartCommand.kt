package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.startGame
import com.github.pool_party.resistance_bot.chatId
import com.github.pool_party.resistance_bot.makeRegistrationMarkup
import com.github.pool_party.resistance_bot.message.*
import com.github.pool_party.resistance_bot.name
import com.github.pool_party.resistance_bot.state.HashStorage
import com.github.pool_party.resistance_bot.state.Member
import com.github.pool_party.resistance_bot.state.StateStorage

class StartCommand(private val stateStorage: StateStorage, private val hashStorage: HashStorage) :
    AbstractCommand("start", "finish the registration and begin a game", HELP_START) {

    override suspend fun Bot.action(message: Message, args: List<String>) = when {
        message.chat.type.let { it == "group" || it == "supergroup" } -> startGameCommand(message)
        args.size == 1 -> register(message, args[0])
        else -> sendGreetings(message)
    }

    private suspend fun Bot.register(message: Message, hash: String) {
        val gameDescription = hashStorage[hash]
        val state = gameDescription?.let { stateStorage[gameDescription.chatId] }
        val senderId = message.chatId
        // We may allow to play without the name or alert that you can't (?)
        val senderName = message.from?.name ?: return

        if (gameDescription == null || state == null) {
            // Isn't it the case before the registration? We can suggest to initialize it with /game (?)
            sendGreetings(message)
            return
        }

        val members = state.members
        if (members.asSequence().map { it.id }.contains(senderId)) {
            sendMessage(senderId, ON_REGISTRATION_REPEAT, "MarkdownV2")
            return
        }

        members += Member(senderId, senderName)
        val registrationMessageId = gameDescription.registrationMessageId.join() ?: return

        // TODO prly back to chat link, if possible (As far as I got, not possible)
        sendMessage(senderId, onRegistrationSuccess(gameDescription.chatName), "MarkdownV2")

        // TODO doesn't work with `parseMode`, check later
        editMessageText(
            gameDescription.chatId,
            registrationMessageId,
            text = onNewPlayerUpdate(members),
//            parseMode = "MarkdownV2",
            markup = makeRegistrationMarkup(hash)
        )

        if (members.size >= Configuration.PLAYERS_GAME_MAXIMUM && state.started.compareAndSet(false, true)) {
            startGame(gameDescription.chatId, stateStorage)
        }
    }

    private fun Bot.sendGreetings(message: Message) {
        sendMessage(message.chatId, INIT_MSG)
    }

    private suspend fun Bot.startGameCommand(message: Message) {
        val chatId = message.chatId
        val state = stateStorage[chatId]

        if (state == null) {
            sendMessage(chatId, ON_NO_REGISTRATION_START, "MarkdownV2")
            return
        }

        if (state.started.compareAndSet(false, true)) {
            startGame(chatId, stateStorage)
        }
    }
}
