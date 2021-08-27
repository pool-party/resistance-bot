package com.github.pool_party.resistance_bot.state

import com.github.pool_party.resistance_bot.Configuration
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

/**
 * Pair of user id and their verdict.
 */
typealias Vote = Pair<Member, Boolean>

data class RoundResult(val success: Boolean, val squadNames: List<String>) {

    override fun toString() =
        (if (success) Configuration.APPROVE_MARK else Configuration.REJECT_MARK) +
            " " +
            squadNames.joinToString { "`$it`" }
}

class GameState(registeredMembers: List<Member>) : State() {

    val members: List<Member>
        get() = privateMembers

    private val privateMembers = registeredMembers.toMutableList()

    /**
     * This field should be accessed after the registration.
     */
    val board = checkNotNull(Configuration.BOARDS[registeredMembers.size])

    val spies = privateMembers.shuffled().take(board.spyNumber)

    val resistance = privateMembers - spies

    val leader
        get() = privateMembers.firstOrNull()!!

    val currentMissionAgentNumber
        get() = board.missionAgentNumber[spyPoints + resistancePoints]

    // Because of vote returning [Boolean] the vote results can be processed in one thread only
    // (the one that first collects last voter).
    // Therefore, point fields and [squads] field can be raw types with no multithreading security.
    var squadRejections: Int = 0

    var resistancePoints: Int = 0

    var spyPoints: Int = 0

    var squad: List<Member>? = null

    val votes: MutableMap<Long, Vote> = ConcurrentHashMap()

    val history = mutableListOf<RoundResult>()

    private val membersMutex = Mutex()

    /**
     * Returns whether the vote collection has been changed or not.
     */
    fun vote(member: Member, verdict: Boolean): Boolean = votes.put(member.id, member to verdict) == null

    suspend fun nextLeader() = membersMutex.withLock { privateMembers += privateMembers.removeFirst() }
}
