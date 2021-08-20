package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.action.startGame
import com.github.pool_party.resistance_bot.chatId
import com.github.pool_party.resistance_bot.makeRegistrationMarkup
import com.github.pool_party.resistance_bot.name
import com.github.pool_party.resistance_bot.state.HashStorage
import com.github.pool_party.resistance_bot.state.Member
import com.github.pool_party.resistance_bot.state.StateStorage

class StartCommand(private val stateStorage: StateStorage, private val hashStorage: HashStorage) :
    AbstractCommand("start", "TODO: long start", "TODO: description start") {

    override suspend fun Bot.action(message: Message, args: List<String>) = when {
        message.chat.type.let { it == "group" || it == "supergroup" } -> startGameCommand(message)
        args.size == 1 -> register(message, args[0])
        else -> sendGreetings(message)
    }

    private suspend fun Bot.register(message: Message, hash: String) {
        val gameDescription = hashStorage[hash]
        val state = gameDescription?.let { stateStorage[gameDescription.chatId] }
        val senderId = message.chatId
        val senderName = message.from?.name ?: return

        if (gameDescription == null || state == null) {
            sendGreetings(message)
            return
        }

        val members = state.members
        if (members.asSequence().map { it.id }.contains(senderId)) {
            sendMessage(senderId, "TODO: u have already registered")
            return
        }

        members += Member(senderId, senderName)
        val registrationMessageId = gameDescription.registrationMessageId.join() ?: return

        // TODO prly back to chat link, if possible
        sendMessage(senderId, "TODO: successfully registered")

        editMessageText(
            gameDescription.chatId,
            registrationMessageId,
            text =
                """
                    |TODO: same message as before

                    |Registered:
                    |${members.joinToString("\n|") { it.name }}
                    |---
                    |${members.size} / ${Configuration.PLAYERS_GAME_MINIMUM}-${Configuration.PLAYERS_GAME_MAXIMUM}
                """.trimMargin("|"),
            markup = makeRegistrationMarkup(hash)
        )

        if (members.size >= Configuration.PLAYERS_GAME_MAXIMUM && state.started.compareAndSet(false, true)) {
            startGame(gameDescription.chatId, stateStorage)
        }
    }

    private fun Bot.sendGreetings(message: Message) {
        sendMessage(message.chatId, "TODO: hi")
    }

    private suspend fun Bot.startGameCommand(message: Message) {
        val chatId = message.chatId
        val state = stateStorage[chatId]

        if (state == null) {
            sendMessage(chatId, "TODO: /game first")
            return
        }

        if (state.started.compareAndSet(false, true)) {
            startGame(chatId, stateStorage)
        }
    }
}
