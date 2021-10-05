package com.github.pool_party.resistance_bot

import com.github.pool_party.resistance_bot.state.Board
import com.github.pool_party.telegram_bot_utils.configuration.AbstractConfiguration

object Configuration : AbstractConfiguration() {

    val REGISTRATION_TIME by seconds()

    val REGISTRATION_EXTEND by seconds()

    val STOP_GAME_VOTING by seconds()

    val REJECTIONS_NUMBER by int()

    const val SPY_MARK = """👹"""

    const val CHOSEN_MARK = """🦹‍♂️"""

    const val APPROVE_MARK = """👍"""

    const val REJECT_MARK = """👎"""

    const val WIN_NUMBER = 3

    val BOARDS = sequenceOf(
        Board(3, 2, listOf(2, 3, 2, 3, 3)),
        Board(3, 3, listOf(2, 3, 4, 3, 4)),
        Board(4, 3, listOf(2, 3, 3, 4, 4)),
        Board(5, 3, listOf(3, 4, 4, 5, 5)),
        Board(6, 3, listOf(3, 4, 4, 5, 5)),
        Board(6, 4, listOf(3, 4, 4, 5, 5)),
    ).associateBy { it.capacity }

    val PLAYERS_GAME_MINIMUM = BOARDS.keys.minOrNull()!!

    val PLAYERS_GAME_MAXIMUM = BOARDS.keys.maxOrNull()!!

    init {
        check((PLAYERS_GAME_MINIMUM..PLAYERS_GAME_MAXIMUM).all { it in BOARDS })
    }
}
