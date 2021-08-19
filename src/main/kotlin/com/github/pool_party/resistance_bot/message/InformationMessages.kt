package com.github.pool_party.resistance_bot.message

// TODO figure out if admin rights are necessary and provide following messages at all required endpoints.
//  Create a button for adding bot to the group chat (this message provided only in private chat) (ex. Mafia bot).
val INIT_MSG =
    """
    Hey! I'm an online version of board game "Resistance"!

    You may start by going through the list of /rules
    Or jump right in with /game command

    Bot requires admin rights to organize the game in your group chat

    Type /help for more information
    """.trimIndent()

// TODO Make /rules, /extend, /stop commands (and others)
val HELP_MSG =
    """
    Available commands:

        /game  - start the registration
        /start - finish the registration and begin a game
        /stop  - cancel the current registration

        /extend - add extra 30 seconds for registration

        /help  - show this usage guide
        /rules - show the list of rules
    """.trimIndent()

// TODO extend help messages.

val HELP_GAME =
    """
    /game - start the registration
    """.trimIndent()

val HELP_START =
    """
    /start - finish the registration and begin a game
    """.trimIndent()

val HELP_STOP =
    """
    /stop - cancel the current registration
    """.trimIndent()

val HELP_EXTEND =
    """
    /extend - add extra 30 seconds for registration
    """.trimIndent()

val HELP_RULES =
    """
    /rules - show the list of rules
    """.trimIndent()
