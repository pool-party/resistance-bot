package com.github.pool_party.resistance_bot.state

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

data class GameDescription(val chatId: Long, val registrationMessageId: CompletableFuture<Int>)

interface HashStorage {

    /**
     * Returns chat id by a hash code.
     */
    operator fun get(hash: String): GameDescription?

    /**
     * Returns and removes hash string.
     */
    fun take(hash: String): GameDescription?

    /**
     * Generates, stores and returns new hash.
     */
    fun newHash(gameDescription: GameDescription): String
}

class InMemoryHashStorage : HashStorage {

    private val hashes = ConcurrentHashMap<String, GameDescription>()

    private val random = Random.Default

    override operator fun get(hash: String) = hashes[hash]

    override fun take(hash: String) = hashes.remove(hash)

    override fun newHash(gameDescription: GameDescription): String {
        val generated = generate()
        hashes[generated] = gameDescription
        return generated
    }

    // TODO maybe something more adequate
    private fun generate(): String = random.nextLong().toString().replace("-", "a")
}
