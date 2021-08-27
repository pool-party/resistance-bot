package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.BotCommand
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Interaction
import mu.KotlinLogging
import java.time.LocalDateTime
import kotlin.system.measureNanoTime

enum class CommandType {
    REGISTRATION,
    GAME,
    UTILS,
}

interface Command : Interaction {

    /**
     * Command name starting with "/".
     */
    val command: String

    /**
     * Command description that will be displayed in a small popup in Telegram as you type commands.
     */
    val description: String

    /**
     * Command help message that will be displayed on command /help <command>
     */
    val helpMessage: String

    /**
     * Affects only indentation in /help command
     */
    val commandType: CommandType

    fun toBotCommand() = BotCommand(command, description)
}

abstract class AbstractCommand(
    commandName: String,
    override val description: String,
    override val helpMessage: String,
    override val commandType: CommandType,
) : Command {

    private val logger = KotlinLogging.logger {}

    override val command = "/$commandName"

    abstract suspend fun Bot.action(message: Message, args: List<String>)

    override fun apply(bot: Bot) = bot.onCommand(command) { message, args ->
        logger.info {
            "${LocalDateTime.now()} $command <- ${message.from?.username}@${message.chat.title}: \"${message.text}\""
        }

        val nanoseconds = measureNanoTime { bot.action(message, parseArgs(args)) }

        logger.info {
            "$command -> finished in ${nanoseconds / 1000000000}.${nanoseconds % 1000000000}s"
        }
    }

    private fun parseArgs(args: String?): List<String> =
        args?.split(' ')?.map { it.trim().lowercase() }?.filter { it.isNotBlank() }.orEmpty()
}
