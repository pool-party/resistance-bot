package com.github.pool_party.resistance_bot.state

import java.util.concurrent.ConcurrentHashMap

interface StateStorage {

    operator fun get(chatId: Long): State?

    /**
     * Returns whether new game has been initialized or there is already one in progress.
     */
    fun newState(chatId: Long): Boolean

    fun gameOver(chatId: Long)
}

class InMemoryStateStorage : StateStorage {

    private val states = ConcurrentHashMap<Long, State>()

    override fun get(chatId: Long) = states[chatId]

    override fun newState(chatId: Long): Boolean = states.putIfAbsent(chatId, State()) == null

    override fun gameOver(chatId: Long) {
        states.remove(chatId)
    }
}
