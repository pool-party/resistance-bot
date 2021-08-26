package com.github.pool_party.resistance_bot.message

import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.state.Member
import kotlin.time.Duration

val REGISTRATION_MSG =
    """
    *Registration is open ✨*

    Looking for players ⛹️
    """.trimIndent()

const val REGISTRATION_BUTTON = """Join the game 🎲"""

val ON_NO_REGISTRATION_START =
    """
    *No game registration found 🔎*

    Type /game to start the registration
    Then use /start command to begin a game
    """.trimIndent()

val ON_PRIVATE_CHAT_REGISTRATION =
    """
    Unable to start a game in a private chat 🙅‍♀️
    """.trimIndent()

val ON_ONGOING_REGISTRATION =
    """
    *Registration is already in progress 🚧*

    Type /start to begin a game or /stop to cancel the current registration
    """.trimIndent()

fun onRegistrationTimestamp(time: Duration) =
    """
    *$time left until the start of the game 📣*

    Type /extend to add extra 30 seconds
    Type /start to begin a game or /stop to cancel the current registration
    """.trimIndent()

const val ON_LESS_PLAYERS = """*Not enough players to begin a game\.\.\. 💁🏻‍♂️*"""

const val ON_MORE_PLAYERS = """*Too much players to begin a game\.\.\. 🤯👨‍👩‍👧‍👦*"""

fun onNewPlayerUpdate(members: List<Member>) =
    """
    |*Registration is open ✨*

    |${members.size} player${if (members.size == 1) "" else "s"} already joined:
    |  \- ${members.joinToString("\n|  \\- ") { "`${it.name}`" }}

    |_${Configuration.PLAYERS_GAME_MINIMUM} to ${Configuration.PLAYERS_GAME_MAXIMUM} players is necessary_
    """.trimMargin()

const val ON_REGISTRATION_STOP = """Registration is cancelled... 🗙"""

fun onRegistrationSuccess(chatName: String?) =
    """You have joined the game${if (chatName == null) "" else " in *$chatName*"}\! 🎯"""

const val ON_REGISTRATION_REPEAT = """*You have already joined\! 🔗*"""

const val ON_GAME_START = """*Game is beginning\.\.\. 🙌*"""

