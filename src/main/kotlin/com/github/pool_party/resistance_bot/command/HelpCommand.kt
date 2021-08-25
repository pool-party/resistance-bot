package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.message.HELP_MSG
import com.github.pool_party.resistance_bot.message.ON_HELP_ERROR
import com.github.pool_party.resistance_bot.utils.chatId

class HelpCommand(private val helpMessages: Map<String, String>) :
    AbstractCommand("help", "show this usage guide", HELP_MSG) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId

        if (args.isEmpty()) {
            sendMessage(chatId, HELP_MSG)
            return
        }

        if (args.size > 1) {
            sendMessage(chatId, ON_HELP_ERROR)
            return
        }

        sendMessage(
            chatId,
            helpMessages[args[0].removePrefix("/")] ?: ON_HELP_ERROR,
            "MarkdownV2",
        )
    }
}
