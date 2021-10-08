package com.github.pool_party.resistance_bot

import com.github.pool_party.resistance_bot.callback.CallbackDispatcher
import com.github.pool_party.resistance_bot.callback.MissionVoteCallback
import com.github.pool_party.resistance_bot.callback.SquadChoiceCallback
import com.github.pool_party.resistance_bot.callback.SquadVoteCallback
import com.github.pool_party.resistance_bot.callback.StopVoteCallback
import com.github.pool_party.resistance_bot.command.ExtendCommand
import com.github.pool_party.resistance_bot.command.GameCommand
import com.github.pool_party.resistance_bot.command.HistoryCommand
import com.github.pool_party.resistance_bot.command.RulesCommand
import com.github.pool_party.resistance_bot.command.StartCommand
import com.github.pool_party.resistance_bot.command.StopCommand
import com.github.pool_party.resistance_bot.command.TagCommand
import com.github.pool_party.resistance_bot.state.Coder
import com.github.pool_party.resistance_bot.state.InMemoryStateStorage
import com.github.pool_party.resistance_bot.state.LongCoder
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.telegram_bot_utils.bot.BotBuilder

fun main() {
    val stateStorage: StateStorage = InMemoryStateStorage()
    val longCoder: Coder<Long> = LongCoder()

    val callbacks = listOf(
        SquadChoiceCallback(stateStorage),
        MissionVoteCallback(stateStorage),
        SquadVoteCallback(stateStorage),
        StopVoteCallback(stateStorage),
    )

    BotBuilder(Configuration).apply {
        interactions = listOf(
            // registration
            listOf(
                GameCommand(stateStorage, longCoder),
                StartCommand(stateStorage, longCoder),
                StopCommand(stateStorage),
            ),
            // game
            listOf(
                HistoryCommand(stateStorage),
                TagCommand(stateStorage),
            ),
            // utils
            listOf(
                ExtendCommand(stateStorage),
                RulesCommand(),
            ),
            // other
            listOf(CallbackDispatcher(callbacks)),
        )
    }.start()
}
