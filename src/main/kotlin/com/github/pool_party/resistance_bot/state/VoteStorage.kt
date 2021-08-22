package com.github.pool_party.resistance_bot.state

import java.util.concurrent.ConcurrentHashMap

/**
 * Pair of user id and their verdict.
 */
typealias Vote = Pair<Member, Boolean>

interface VoteStorage {

    /**
     * For a chat id returns a list of [Vote].
     */
    operator fun get(chatId: Long): List<Vote>

    fun set(chatId: Long, member: Member, verdict: Boolean)

    fun clear(chatId: Long)
}

class InMemoryVoteStorage : VoteStorage {

    private val votes = ConcurrentHashMap<Long, MutableMap<Long, Vote>>()

    override fun get(chatId: Long) = votes[chatId]?.values?.toList().orEmpty()

    override fun set(chatId: Long, member: Member, verdict: Boolean) {
        votes.getOrPut(chatId) { HashMap() }[member.id] = member to verdict
    }

    override fun clear(chatId: Long) {
        votes.remove(chatId)
    }
}
