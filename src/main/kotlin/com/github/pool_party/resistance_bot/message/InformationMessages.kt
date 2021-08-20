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
