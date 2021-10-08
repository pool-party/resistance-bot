package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.flume.interaction.command.AbstractCommand
import com.github.pool_party.flume.utils.chatId
import com.github.pool_party.flume.utils.sendMessageLogging
import com.github.pool_party.resistance_bot.message.GAME_RULES
import com.github.pool_party.resistance_bot.message.HELP_RULES

class RulesCommand :
    AbstractCommand("rules", "show the list of rules", HELP_RULES) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        sendMessageLogging(message.chatId, GAME_RULES)
    }
}
