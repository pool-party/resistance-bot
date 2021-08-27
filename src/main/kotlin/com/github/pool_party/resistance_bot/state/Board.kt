package com.github.pool_party.resistance_bot.state

import com.github.pool_party.resistance_bot.Configuration

class Board(val resistanceNumber: Int, val spyNumber: Int, val missionAgentNumber: List<Int>) {

    val capacity = resistanceNumber + spyNumber

    init {
//        check(resistanceNumber > 0 && spyNumber > 0)
        check(missionAgentNumber.size == Configuration.WIN_NUMBER * 2 - 1)
        check(missionAgentNumber.all { it in 0..(capacity - spyNumber + 1) })
    }
}
