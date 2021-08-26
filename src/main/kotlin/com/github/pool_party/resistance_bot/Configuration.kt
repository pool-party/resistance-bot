package com.github.pool_party.resistance_bot

import com.github.pool_party.resistance_bot.state.Board
import com.github.pool_party.resistance_bot.utils.ConfigurationUtils.boolean
import com.github.pool_party.resistance_bot.utils.ConfigurationUtils.int
import com.github.pool_party.resistance_bot.utils.ConfigurationUtils.string
import com.github.pool_party.resistance_bot.utils.ConfigurationUtils.seconds

object Configuration {

    val APP_URL by string()
    val USERNAME by string()
    val PORT by int()
    val HOST by string()

    val LONGPOLL by boolean()

    val TELEGRAM_TOKEN by string()

    val REGISTRATION_TIME by seconds()

    val REGISTRATION_EXTEND by seconds()

    val REJECTIONS_NUMBER by int()

    const val SPY_MARK = """🦹‍♂️"""

    const val APPROVE_MARK = """👍"""

    const val REJECT_MARK = """👎"""

    const val WIN_NUMBER = 3

    val BOARDS = sequenceOf(
        // TODO TEST
        Board(1, 0, listOf(1, 1, 1, 1, 1)),
        Board(1, 1, listOf(1, 2, 1, 2, 1)),
        Board(2, 1, listOf(1, 2, 1, 2, 1)),
        Board(2, 1, listOf(1, 2, 1, 2, 1)),
        Board(3, 1, listOf(1, 2, 1, 2, 1)),

        Board(3, 2, listOf(2, 3, 2, 3, 3)),
        Board(3, 3, listOf(2, 3, 4, 3, 4)),
        // TODO boards for 7-10
        Board(4, 3, listOf(2, 3, 4, 3, 4)),
        Board(5, 3, listOf(2, 3, 4, 3, 4)),
        Board(6, 3, listOf(2, 3, 4, 3, 4)),
        Board(6, 4, listOf(2, 3, 4, 3, 4)),
    ).associateBy { it.capacity }

    val PLAYERS_GAME_MINIMUM = BOARDS.keys.minOrNull()!!

    val PLAYERS_GAME_MAXIMUM = BOARDS.keys.maxOrNull()!!

    init {
        check((PLAYERS_GAME_MINIMUM..PLAYERS_GAME_MAXIMUM).all { it in BOARDS })
    }
}
