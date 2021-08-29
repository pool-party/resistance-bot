package com.github.pool_party.resistance_bot.message

// TODO figure out if admin rights are necessary and provide following messages at all required endpoints.
//  Create a button for adding bot to the group chat (this message provided only in private chat) (ex. Mafia bot).
val INIT_MSG =
    """
    Hey! I'm an online version of board game "Resistance"!

    You may start by going through the list of /rules
    Or add bot to your group chat and jump right in with /game command

    Bot requires admin rights to organize the game in your group chat

    Type /help for more information
    """.trimIndent()

const val INIT_MARKUP = """Add a game to your group chat ✉️"""

fun helpMessage(groups: List<Map<String, String>>) =
    """
    |Available commands:
    |
    |${
        groups.joinToString("\n|\n|") { list ->
            list.asSequence().joinToString("\n|") { "${it.key} - ${it.value}" }
        }
    }
    """.trimMargin()

val HELP_GAME =
    """
    /game - start the registration

    Registration lasts 3 minutes by default, with notifies during the last minute
    You can begin the game whenever you ready with /start command,
    Cancel the registration with /stop command
    Or prolong the registration by 30 seconds with /extend command

    Type /help for more information
    """.trimIndent()

val HELP_START =
    """
    /start - finish the registration and begin a game

    Works only in group chats, shows welcoming message in the private chats
    """.trimIndent()

val HELP_STOP =
    """
    /stop - cancel the current registration
    """.trimIndent()

val HELP_TAG =
    """
    /tag - notifies players delaying a game

    Mentions players who still haven't made necessary actions to continue a game
    """.trimIndent()

val HELP_HISTORY =
    """
    /history - shows round history

    Contains the information about mission's order, results and teams
    """.trimIndent()

val HELP_EXTEND =
    """
    /extend - add extra 30 seconds for registration
    """.trimIndent()

// TODO PullPartyBot message, might change
val ON_HELP_ERROR =
    """
    The Lord helps those who help themselves 👼

    Expected no arguments or command to explain
    Follow /help with the unclear command or leave empty for general guide
    """.trimIndent()

val HELP_RULES =
    """
    /rules - show the list of rules
    """.trimIndent()

// TODO
val GAME_RULES =
    """
    Rules: smth
    """.trimIndent()
