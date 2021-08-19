package com.github.pool_party.resistance_bot.state

import java.util.LinkedList
import java.util.concurrent.atomic.AtomicBoolean

data class Member(val id: Long, val name: String)

data class State(
    val members: MutableList<Member> = LinkedList(),
    var squadRejections: Int = 0,
    var resistancePoints: Int = 0,
    var spyPoints: Int = 0,
    var started: AtomicBoolean = AtomicBoolean(false),
)
