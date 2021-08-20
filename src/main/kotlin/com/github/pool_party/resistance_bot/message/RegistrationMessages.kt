package com.github.pool_party.resistance_bot.message

val REGISTRATION_MSG =
    """
    *Registration is open ✨*

    Looking for players ⛹️
    """.trimIndent()

const val REGISTRATION_BUTTON = """Join the game 🎲"""

val ON_ONGOING_REGISTRATION =
    """
    *Registration is already in progress 🚧*

    Type /start to begin a game or /stop to cancel the current registration
    """.trimIndent()

fun onRegistrationTimestamp(time: String) =
    """
    *$time left until the start of the game 📣*

    Type /extend to add extra 30 seconds
    Type /start to begin a game or /stop to cancel the current registration
    """.trimIndent()

val ON_LESS_PLAYERS =
    """
    *Not enough players to begin a game... 💁🏻‍♂️*
    """.trimIndent()

val ON_MORE_PLAYERS =
    """
    *Too much players to begin a game... 🤯👨‍👩‍👧‍👦*
    """.trimIndent()

val ON_REGISTRATION_STOP =
    """
    Registration is cancelled... 🗙
    """.trimIndent()

val ON_GAME_START =
    """
    *Game is beginning\.\.\. 🙌*
    """.trimIndent()

