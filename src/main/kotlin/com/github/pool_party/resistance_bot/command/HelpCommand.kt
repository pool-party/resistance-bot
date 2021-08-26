package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.message.ON_HELP_ERROR
import com.github.pool_party.resistance_bot.message.helpMessage
import com.github.pool_party.resistance_bot.utils.chatId

class HelpCommand(commands: List<Command>) :
    AbstractCommand(
        "help",
        "show this usage guide",
        helpMessage(commands.associate { it.command to it.description }),
    ) {

    private val helpMessages = commands.associate { it.command.removePrefix("/") to it.helpMessage }

    override suspend fun Bot.action(message: Message, args: List<String>) {
        sendMessage(
            message.chatId,
            when {
                args.isEmpty() -> helpMessage
                args.size > 1 -> ON_HELP_ERROR
                else -> helpMessages[args[0].removePrefix("/")] ?: ON_HELP_ERROR
            },
            "MarkdownV2"
        )
    }
}
