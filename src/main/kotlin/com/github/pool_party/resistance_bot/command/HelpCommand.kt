package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.message.ON_HELP_ERROR
import com.github.pool_party.resistance_bot.message.helpMessage
import com.github.pool_party.resistance_bot.utils.chatId
import com.github.pool_party.resistance_bot.utils.sendMessageLogging

class HelpCommand(commands: List<Command>) :
    AbstractCommand(
        "help",
        "show this usage guide",
        helpMessage(
            commands.groupBy { it.commandType }.values.map { list -> list.associate { it.command to it.description } }
        ),
        CommandType.UTILS,
    ) {

    private val helpMessages = commands.associate { it.command.removePrefix("/") to it.helpMessage }

    override suspend fun Bot.action(message: Message, args: List<String>) {
        sendMessageLogging(
            message.chatId,
            when {
                args.isEmpty() -> helpMessage
                args.size > 1 -> ON_HELP_ERROR
                else -> helpMessages[args[0].removePrefix("/")] ?: ON_HELP_ERROR
            },
        )
    }
}
