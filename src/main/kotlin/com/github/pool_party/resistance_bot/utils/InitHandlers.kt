package com.github.pool_party.resistance_bot.utils

import com.elbekD.bot.Bot
import com.github.pool_party.resistance_bot.Interaction
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
import com.github.pool_party.resistance_bot.command.TagCommand
import com.github.pool_party.resistance_bot.state.Coder
import com.github.pool_party.resistance_bot.state.InMemoryStateStorage
import com.github.pool_party.resistance_bot.state.LongCoder
import com.github.pool_party.resistance_bot.state.StateStorage

fun Bot.initHandlers() {
    val stateStorage: StateStorage = InMemoryStateStorage()
    val longCoder: Coder<Long> = LongCoder()

    val callbacks = listOf(
        SquadChoiceCallback(stateStorage),
        MissionVoteCallback(stateStorage),
        SquadVoteCallback(stateStorage),
    )

    val interactions: MutableList<Interaction> = mutableListOf(
        CallbackDispatcher(callbacks),
        GameCommand(stateStorage, longCoder),
        StartCommand(stateStorage, longCoder),
        StopCommand(stateStorage),
        ExtendCommand(stateStorage),
        RulesCommand(),
        TagCommand(stateStorage),
    )

    val commands = interactions.mapNotNull { it as? Command }.toMutableList()

    val helpCommand = HelpCommand(commands)
    commands += helpCommand
    interactions.add(helpCommand)

    interactions.forEach { it.apply(this) }
    setMyCommands(commands.map { it.toBotCommand() })
}
