package com.github.pool_party.resistance_bot.message

import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.state.Member
import kotlin.time.Duration

val REGISTRATION_MSG =
    """
    *Registration is open โจ*

    Looking for players โน๏ธ
    """.trimIndent()

const val REGISTRATION_BUTTON = """Join the game ๐ฒ"""

const val GET_PIN_RIGHTS_SUGGEST = """_Allow bot to pin the messages for a comfort registration ๐_"""

// TODO split on more concrete messages.
val ON_NO_REGISTRATION =
    """
    *No game registration found ๐*

    Type /game to start the registration
    """.trimIndent()

const val ON_PRIVATE_CHAT_REGISTRATION = """Unable to start a game in a private chat ๐โโ๏ธ"""

val ON_ONGOING_GAME =
    """
    *Game is on ๐จ*

    Only single active game in chat is allowed
    """.trimIndent()

val ON_IN_GAME_EXTEND =
    """
    *Game is on ๐จ*

    Extend command is available only during the registration
    """.trimIndent()

val ON_ONGOING_REGISTRATION =
    """
    *Registration is already in progress ๐ง*

    Type /start to begin a game or /stop to cancel the current registration
    """.trimIndent()

fun onRegistrationTimestamp(time: Duration) =
    """
    *$time left until the start of the game ๐ฃ*

    Type /extend to add extra 30 seconds
    Type /start to begin a game or /stop to cancel the current registration
    """.trimIndent()

// TODO Add time handling.
fun onExtend(time: Duration?) =
    """
    *Registration extended ๐*
    """.trimIndent()

const val ON_LESS_PLAYERS = """*Not enough players to begin a game... ๐๐ปโโ๏ธ*"""

const val ON_MORE_PLAYERS = """*Too much players to begin a game... ๐คฏ๐จโ๐ฉโ๐งโ๐ฆ*"""

fun onNewPlayerUpdate(members: List<Member>) =
    """
    |*Registration is open โจ*

    |${members.size} player${if (members.size == 1) "" else "s"} already joined:
    |  - ${members.joinToString("\n|  - ") { "`${it.name}`" }}

    |_${Configuration.PLAYERS_GAME_MINIMUM} to ${Configuration.PLAYERS_GAME_MAXIMUM} players is necessary_
    """.trimMargin()

const val ON_REGISTRATION_STOP = """Registration is cancelled... ๐"""

fun onRegistrationSuccess(chatName: String?) =
    """You have joined the game${if (chatName == null) "" else " in *$chatName*"}! ๐ฏ"""

const val ON_REGISTRATION_REPEAT = """*You have already joined! ๐*"""

const val ON_GAME_START = """*Game is beginning... ๐*"""
