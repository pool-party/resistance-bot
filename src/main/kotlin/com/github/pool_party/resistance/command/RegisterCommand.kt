package com.github.pool_party.resistance.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance.Configuration
import com.github.pool_party.resistance.action.startGame
import com.github.pool_party.resistance.chatId
import com.github.pool_party.resistance.makeRegisterMarkup
import com.github.pool_party.resistance.state.GameDescription
import com.github.pool_party.resistance.state.HashStorage
import com.github.pool_party.resistance.state.StateStorage
import kotlinx.coroutines.delay
import java.util.concurrent.CompletableFuture

class RegisterCommand(private val stateStorage: StateStorage, private val hashStorage: HashStorage) :
    AbstractCommand("register", "TODO: register long", "TODO: register help") {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId

        if (!stateStorage.newState(chatId)) {
            sendMessage(chatId, "TODO: game in progress")
            return
        }

        val registerMessageIdFuture = CompletableFuture<Int>()
        val gameDescription = GameDescription(chatId, registerMessageIdFuture)

        val registrationMessage =
            sendMessage(chatId, "TODO: registration", markup = makeRegisterMarkup(hashStorage.newHash(gameDescription)))
                .join()
        registerMessageIdFuture.complete(registrationMessage.message_id)

        val state = stateStorage[chatId] ?: return

        delay((Configuration.REGISTRATION_SECONDS - 60) * 1000L)

        if (state.started.get()) return
        sendMessage(chatId, "TODO: 1m")

        delay(30_000L)

        if (state.started.get()) return
        sendMessage(chatId, "TODO: 30s")

        delay(30_000L)

        if (!state.started.compareAndSet(false, true)) {
            startGame(chatId, stateStorage)
        }
    }
}
