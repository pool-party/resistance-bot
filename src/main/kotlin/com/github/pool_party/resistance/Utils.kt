package com.github.pool_party.resistance

import com.elbekD.bot.Bot
import com.elbekD.bot.types.InlineKeyboardButton
import com.elbekD.bot.types.InlineKeyboardMarkup
import com.elbekD.bot.types.Message
import com.elbekD.bot.types.User
import com.github.pool_party.resistance.callback.CallbackDispatcher
import com.github.pool_party.resistance.callback.SquadChoiceCallback
import com.github.pool_party.resistance.callback.VoteCallback
import com.github.pool_party.resistance.command.Command
import com.github.pool_party.resistance.command.HelpCommand
import com.github.pool_party.resistance.command.RegisterCommand
import com.github.pool_party.resistance.command.StartCommand
import com.github.pool_party.resistance.state.HashStorage
import com.github.pool_party.resistance.state.InMemoryHashStorage
import com.github.pool_party.resistance.state.InMemorySquadStorage
import com.github.pool_party.resistance.state.InMemoryStateStorage
import com.github.pool_party.resistance.state.InMemoryVoteStorage
import com.github.pool_party.resistance.state.SquadStorage
import com.github.pool_party.resistance.state.StateStorage
import com.github.pool_party.resistance.state.VoteStorage
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.protobuf.ProtoBuf

val Message.chatId
    get() = chat.id

val User.name
    get() = sequenceOf(first_name, last_name).filterNotNull().joinToString(" ")

fun makeUserLink(name: String, id: Long) = "[${name}](tg://user?id=${id})"

fun List<InlineKeyboardButton>.toMarkUp() = InlineKeyboardMarkup(listOf(this))

inline fun <reified T> encodeInner(value: T) = ProtoBuf.encodeToHexString(value)

inline fun <reified T> decodeInner(string: String) = ProtoBuf.decodeFromHexString<T>(string)

inline fun <reified T> encode(value: T) = String(ProtoBuf.encodeToByteArray(value))

inline fun <reified T> decode(string: String) = ProtoBuf.decodeFromByteArray<T>(string.toByteArray())

fun Bot.initHandlers() {
    val stateStorage: StateStorage = InMemoryStateStorage()
    val voteStorage: VoteStorage = InMemoryVoteStorage()
    val hashStorage: HashStorage = InMemoryHashStorage()
    val squadStorage: SquadStorage = InMemorySquadStorage()

    val callbacks = listOf(
        SquadChoiceCallback(stateStorage, squadStorage),
        VoteCallback(voteStorage, stateStorage, squadStorage),
    )

    val interactions: MutableList<Interaction> = mutableListOf(
        CallbackDispatcher(callbacks.associateBy { it.callbackAction }),
        StartCommand(stateStorage, hashStorage),
        RegisterCommand(stateStorage, hashStorage),
    )

    val commands = interactions.mapNotNull { it as? Command }.toMutableList()

    val helpCommand = HelpCommand(commands.associate { it.command.removePrefix("/") to it.helpMessage })
    commands += helpCommand
    interactions.add(helpCommand)

    interactions.forEach { it.apply(this) }
    setMyCommands(commands.map { it.toBotCommand() })
}
