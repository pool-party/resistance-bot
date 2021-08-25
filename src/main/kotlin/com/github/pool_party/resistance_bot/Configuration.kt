package com.github.pool_party.resistance_bot

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

    val REGISTRATION_ANNOUNCEMENT_DELAY by seconds()

    val PLAYERS_GAME_MINIMUM by int()

    val PLAYERS_GAME_MAXIMUM by int()

    val PLAYERS_MISSION by int()

    val WIN_NUMBER by int()

    val REJECTIONS_NUMBER by int()

    const val SPY_MARK = """🦹‍♂️"""

    const val APPROVE_MARK = """👍"""

    const val REJECT_MARK = """👎"""
}
