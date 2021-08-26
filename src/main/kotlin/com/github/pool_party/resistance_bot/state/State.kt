package com.github.pool_party.resistance_bot.state

import com.github.pool_party.resistance_bot.Configuration.BOARDS
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.LinkedList
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Pair of user id and their verdict.
 */
typealias Vote = Pair<Member, Boolean>

data class Member(val id: Long, val name: String)

class State(val chatName: String? = null, registrationMessageIdFuture: CompletableFuture<Int>) {

    // This field is guarded by [startedMutex], that must be locked on registering new users.
    val members: MutableList<Member> = LinkedList()

    val leader
        get() = members.firstOrNull()!!

    val registrationMessageId: Int by lazy { registrationMessageIdFuture.join() }

    var registrationExtendCounter = AtomicInteger(0)

    /**
     * This field should be accessed after the registration.
     */
    val board by lazy { BOARDS[members.size] ?: throw IllegalArgumentException() }

    // Because of vote returning [Boolean] the vote results can be processed in one thread only
    // (the one that first collects last voter).
    // Therefore, point fields and [squads] field can be raw types with no multithreading security.
    var squadRejections: Int = 0

    var resistancePoints: Int = 0

    var spyPoints: Int = 0

    var squad: List<Long>? = null

    val votes: MutableMap<Long, Vote> = ConcurrentHashMap()

    private val startedMutex = Mutex()

    private var started = false

    /**
     * Returns whether the vote collection has been changed or not.
     */
    fun vote(member: Member, verdict: Boolean): Boolean = votes.put(member.id, member to verdict) == null

    suspend fun nextLeader() {
        startedMutex.withLock { members += members.removeFirst()  }
    }

    suspend fun withStarted(action: (Boolean) -> Boolean): Boolean {
        val result = startedMutex.withLock { action(started) }
        started = result
        return result
    }

    suspend fun tryStartAndDo(action: suspend () -> Unit) {
        var startingGame = false

        withStarted {
            startingGame = !it
            true
        }

        if (startingGame) {
            action()
        }
    }
}
