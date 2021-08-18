package com.github.pool_party.resistance.state

import java.util.concurrent.ConcurrentHashMap

interface SquadStorage {

    operator fun get(chatId: Long): List<Long>?

    operator fun set(chatId: Long, squad: List<Long>)

    fun remove(chatId: Long)
}

class InMemorySquadStorage : SquadStorage {

    private val squads = ConcurrentHashMap<Long, List<Long>>()

    override fun get(chatId: Long) = squads[chatId]

    override fun set(chatId: Long, squad: List<Long>) {
        squads[chatId] = squad
    }

    override fun remove(chatId: Long) {
        squads.remove(chatId)
    }
}
