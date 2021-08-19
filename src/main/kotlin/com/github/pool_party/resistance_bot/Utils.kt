package com.github.pool_party.resistance_bot

import com.elbekD.bot.Bot
import com.elbekD.bot.types.*
import com.github.pool_party.resistance_bot.callback.CallbackDispatcher
import com.github.pool_party.resistance_bot.callback.MissionVoteCallback
import com.github.pool_party.resistance_bot.callback.SquadChoiceCallback
import com.github.pool_party.resistance_bot.callback.SquadVoteCallback
import com.github.pool_party.resistance_bot.command.Command
import com.github.pool_party.resistance_bot.command.HelpCommand
import com.github.pool_party.resistance_bot.command.LetsGoCommand
import com.github.pool_party.resistance_bot.command.GameCommand
import com.github.pool_party.resistance_bot.command.StartCommand
import com.github.pool_party.resistance_bot.message.REGISTRATION_BUTTON
import com.github.pool_party.resistance_bot.state.HashStorage
import com.github.pool_party.resistance_bot.state.InMemoryHashStorage
import com.github.pool_party.resistance_bot.state.InMemorySquadStorage
import com.github.pool_party.resistance_bot.state.InMemoryStateStorage
import com.github.pool_party.resistance_bot.state.InMemoryVoteStorage
import com.github.pool_party.resistance_bot.state.SquadStorage
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.state.VoteStorage

val Message.chatId
    get() = chat.id

val User.name
    get() = sequenceOf(first_name, last_name).filterNotNull().joinToString(" ")

fun List<InlineKeyboardButton>.toMarkUp() = InlineKeyboardMarkup(listOf(this))

fun makeRegistrationMarkup(hash: String) =
    listOf(InlineKeyboardButton(REGISTRATION_BUTTON, url = registrationLink(hash))).toMarkUp()

fun registrationLink(hash: String) = """https://t.me/${Configuration.USERNAME}?start=${hash}"""

fun makeUserLink(name: String, id: Long) = "[${name}](tg://user?id=${id})"

fun timestampToString(time: Long): String {
    val minutesDigit = time / 60_000L
    val secondsDigit = time % 60_000L

    val res = "${appendTimeType(minutesDigit)} ${appendTimeType(secondsDigit)}"
    return res.ifBlank { "0" } // TODO handle blank normally.
}

fun appendTimeType(digit: Long) = if (digit != 0L) if (digit != 1L) "$digit minutes" else "$digit minute" else ""

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

    val interactions: MutableList<Interaction> = mutableListOf(
        CallbackDispatcher(callbacks),
        StartCommand(stateStorage, hashStorage),
        GameCommand(stateStorage, hashStorage),
        LetsGoCommand(stateStorage),
    )

    val commands = interactions.mapNotNull { it as? Command }.toMutableList()

    val helpCommand = HelpCommand(commands.associate { it.command.removePrefix("/") to it.helpMessage })
    commands += helpCommand
    interactions.add(helpCommand)

    interactions.forEach { it.apply(this) }
    setMyCommands(commands.map { it.toBotCommand() })
}
