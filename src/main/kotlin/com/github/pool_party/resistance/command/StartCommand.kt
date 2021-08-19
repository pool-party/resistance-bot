package com.github.pool_party.resistance.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance.Configuration
import com.github.pool_party.resistance.action.startGame
import com.github.pool_party.resistance.chatId
import com.github.pool_party.resistance.makeRegisterMarkup
import com.github.pool_party.resistance.makeUserLink
import com.github.pool_party.resistance.name
import com.github.pool_party.resistance.state.HashStorage
import com.github.pool_party.resistance.state.Member
import com.github.pool_party.resistance.state.StateStorage

class StartCommand(private val stateStorage: StateStorage, private val hashStorage: HashStorage) :
    AbstractCommand("start", "TODO: long start", "TODO: description start") {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val hash = args.singleOrNull()
        val gameDescription = hash?.let { hashStorage[it] }
        val state = gameDescription?.let { stateStorage[gameDescription.chatId] }
        val senderId = message.chatId
        val senderName = message.from?.name ?: return

        if (hash == null || gameDescription == null || state == null) {
            sendMessage(senderId, "TODO: hi")
            return
        }

        val members = state.members
        if (members.asSequence().map { it.id }.contains(senderId)) {
            sendMessage(senderId, "TODO: u have already registered")
            return
        }

        members += Member(senderId, senderName)

        // TODO prly back to chat link, if possible
        sendMessage(senderId, "TODO: successfully registered")

        editMessageText(
            gameDescription.chatId,
            gameDescription.registrationMessageId.join(),
            text =
                """
                    |TODO: same message as before

                    |Registered:
                    |${members.joinToString("\n|") { it.name }}
                    |---
                    |${members.size} / ${Configuration.PLAYERS_GAME_MINIMUM}-${Configuration.PLAYERS_GAME_MAXIMUM}
                """.trimMargin("|"),
            markup = makeRegisterMarkup(hash)
        )

        if (members.size >= Configuration.PLAYERS_GAME_MAXIMUM && state.started.compareAndSet(false, true)) {
            startGame(gameDescription.chatId, stateStorage)
        }
    }
}
