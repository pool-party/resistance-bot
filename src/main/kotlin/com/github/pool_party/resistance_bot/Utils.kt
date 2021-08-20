package com.github.pool_party.resistance_bot

import com.elbekD.bot.Bot
import com.elbekD.bot.types.*
import com.github.pool_party.resistance_bot.callback.CallbackDispatcher
import com.github.pool_party.resistance_bot.callback.MissionVoteCallback
import com.github.pool_party.resistance_bot.callback.SquadChoiceCallback
import com.github.pool_party.resistance_bot.callback.SquadVoteCallback
import com.github.pool_party.resistance_bot.command.Command
import com.github.pool_party.resistance_bot.command.ExtendCommand
import com.github.pool_party.resistance_bot.command.GameCommand
import com.github.pool_party.resistance_bot.command.HelpCommand
import com.github.pool_party.resistance_bot.command.RulesCommand
import com.github.pool_party.resistance_bot.command.StartCommand
import com.github.pool_party.resistance_bot.command.StopCommand
import com.github.pool_party.resistance_bot.message.REGISTRATION_BUTTON
import com.github.pool_party.resistance_bot.message.VOTING_SUGGEST
import com.github.pool_party.resistance_bot.state.HashStorage
import com.github.pool_party.resistance_bot.state.InMemoryHashStorage
import com.github.pool_party.resistance_bot.state.InMemorySquadStorage
import com.github.pool_party.resistance_bot.state.InMemoryStateStorage
import com.github.pool_party.resistance_bot.state.InMemoryVoteStorage
import com.github.pool_party.resistance_bot.state.SquadStorage
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.state.VoteStorage
import kotlin.time.Duration

val Message.chatId
    get() = chat.id

val User.name
    get() = sequenceOf(first_name, last_name).filterNotNull().joinToString(" ")

fun List<InlineKeyboardButton>.toMarkUp() = InlineKeyboardMarkup(listOf(this))

fun makeRegistrationMarkup(hash: String) =
    listOf(InlineKeyboardButton(REGISTRATION_BUTTON, url = botLink(hash))).toMarkUp()

fun goToBotMarkup() = listOf(InlineKeyboardButton(VOTING_SUGGEST, url = botLink())).toMarkUp()

fun botLink(hash: String? = null) =
    "https://t.me/${Configuration.USERNAME}${if (hash == null) "" else "?start=${hash}"}"

fun makeUserLink(name: String, id: Long) = "[${name}](tg://user?id=${id})"

fun durationToString(duration: Duration): String {
    fun appendTimeType(number: Long, measure: String) =
        if (number != 0L) if (number != 1L) "$number ${measure}s" else "$number $measure" else ""

    val minutes = appendTimeType(duration.inWholeMinutes, "minute")
    val seconds = appendTimeType(duration.inWholeSeconds % 60, "second")

    val res = if (seconds.isBlank()) minutes else "$minutes $seconds"
    return res.ifBlank { "0" } // TODO handle blank normally.
}

fun Bot.initHandlers() {
    val stateStorage: StateStorage = InMemoryStateStorage()
    val voteStorage: VoteStorage = InMemoryVoteStorage()
    val hashStorage: HashStorage = InMemoryHashStorage()
    val squadStorage: SquadStorage = InMemorySquadStorage()

    val callbacks = listOf(
        SquadChoiceCallback(stateStorage, squadStorage),
        MissionVoteCallback(voteStorage, stateStorage, squadStorage),
        SquadVoteCallback(voteStorage, stateStorage, squadStorage),
    )

    // TODO Add new commands.
    val interactions: MutableList<Interaction> = mutableListOf(
        CallbackDispatcher(callbacks),
        StartCommand(stateStorage, hashStorage),
        GameCommand(stateStorage, hashStorage),
        ExtendCommand(stateStorage),
        StopCommand(stateStorage),
        RulesCommand(),
    )

    val commands = interactions.mapNotNull { it as? Command }.toMutableList()

    val helpCommand = HelpCommand(commands.associate { it.command.removePrefix("/") to it.helpMessage })
    commands += helpCommand
    interactions.add(helpCommand)

    interactions.forEach { it.apply(this) }
    setMyCommands(commands.map { it.toBotCommand() })
}
