package com.github.pool_party.resistance.state

import java.util.concurrent.ConcurrentHashMap

interface VoteStorage {

    /**
     * For a chat id returns a list of pairs of user id and their verdict.
     */
    operator fun get(chatId: Long): List<Pair<Member, Boolean>>

    fun set(chatId: Long, member: Member, verdict: Boolean)

    fun clear(chatId: Long)
}

class InMemoryVoteStorage : VoteStorage {

    private val votes = ConcurrentHashMap<Long, MutableMap<Long, Pair<Member, Boolean>>>()

    override fun get(chatId: Long) = votes[chatId]?.values?.toList().orEmpty()

    override fun set(chatId: Long, member: Member, verdict: Boolean) {
        votes.getOrPut(chatId) { HashMap() }[member.id] = member to verdict
    }

    override fun clear(chatId: Long) {
        votes.remove(chatId)
    }
}
