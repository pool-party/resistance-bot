package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.chatId
import com.github.pool_party.resistance_bot.message.HELP_EXTEND
import com.github.pool_party.resistance_bot.state.StateStorage

class ExtendCommand(private val stateStorage: StateStorage) :
    AbstractCommand("extend", "add extra 30 seconds for registration", HELP_EXTEND) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        //val chatId = message.chatId

        TODO("Not yet implemented")
    }
}
