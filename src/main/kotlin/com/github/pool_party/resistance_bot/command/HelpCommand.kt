package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.chatId

class HelpCommand(private val helpMessages: Map<String, String>) :
    AbstractCommand("help", "show this usage guide", "TODO: help") {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId

        if (args.isEmpty()) {
            sendMessage(chatId, "TODO: help")
            return
        }

        if (args.size > 1) {
            sendMessage(chatId, "TODO: help error")
            return
        }

        sendMessage(
            chatId,
            helpMessages[args[0].removePrefix("/")] ?: "TODO: help error",
            "MarkdownV2",
        )
    }
}
