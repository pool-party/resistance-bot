package com.github.pool_party.resistance_bot.state

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

interface StateStorage {

    fun getState(chatId: Long): State?

    fun getRegistrationState(chatId: Long): RegistrationState?

    fun getGameState(chatId: Long): GameState?

    fun startGame(chatId: Long)

    /**
     * Returns whether new game has been initialized or there is already one in progress.
     */
    fun newRegistrationState(
        chatId: Long,
        registrationMessageId: CompletableFuture<Int>,
        chatName: String? = null,
    ): Boolean

    fun gameOver(chatId: Long)
}

class InMemoryStateStorage : StateStorage {

    private val states = ConcurrentHashMap<Long, State>()

    @Suppress("UNCHECKED_CAST")
    private fun <T : State> get(chatId: Long): T? = states[chatId] as? T

    override fun getState(chatId: Long): State? = get(chatId)

    override fun getRegistrationState(chatId: Long): RegistrationState? = get(chatId)

    override fun getGameState(chatId: Long): GameState? = get(chatId)

    override fun startGame(chatId: Long) {
        val state = checkNotNull(getRegistrationState(chatId))
        states[chatId] = GameState(state.members)
    }

    override fun newRegistrationState(
        chatId: Long,
        registrationMessageId: CompletableFuture<Int>,
        chatName: String?,
    ): Boolean =
        states.putIfAbsent(chatId, RegistrationState(chatName, registrationMessageId)) == null

    override fun gameOver(chatId: Long) {
        states.remove(chatId)
    }
}
