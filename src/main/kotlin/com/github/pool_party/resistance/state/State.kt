package com.github.pool_party.resistance.state

import java.util.LinkedList

data class Member(val id: Long, val name: String)

data class State(
    val members: MutableList<Member> = LinkedList(),
    var squadRejections: Int = 0,
    var resistancePoints: Int = 0,
    var spyPoints: Int = 0,
)
