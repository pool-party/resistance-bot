package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.message.HELP_STOP
import com.github.pool_party.resistance_bot.message.ON_NO_REGISTRATION
import com.github.pool_party.resistance_bot.message.ON_REGISTRATION_STOP
import com.github.pool_party.resistance_bot.state.GameState
import com.github.pool_party.resistance_bot.state.RegistrationState
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.chatId
import com.github.pool_party.resistance_bot.utils.deleteMessageLogging
import com.github.pool_party.resistance_bot.utils.makeStopVoteMarkup
import com.github.pool_party.resistance_bot.utils.sendMessageLogging
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap

class StopCommand(private val stateStorage: StateStorage) :
    AbstractCommand(
        "stop",
        "cancel the current registration",
        HELP_STOP,
        CommandType.REGISTRATION,
    ) {

    override suspend fun Bot.action(message: Message, args: List<String>) {
        val chatId = message.chatId

        when (val state = stateStorage.getState(chatId)) {
            // TODO message
            null -> sendMessageLogging(chatId, ON_NO_REGISTRATION)

            is RegistrationState -> {
                sendMessageLogging(chatId, ON_REGISTRATION_STOP)
                deleteMessageLogging(chatId, state.registrationMessageId)
                stateStorage.gameOver(chatId)
            }

            is GameState -> {
                val votes = ConcurrentHashMap<Int, Boolean>()
                if (!state.stopVotes.compareAndSet(null, votes)) return
                val voteMessageId =
                    sendMessageLogging(chatId, "TODO: stop the game?", markup = makeStopVoteMarkup(chatId, votes))
                        .join()
                        .message_id

                delay(Configuration.STOP_GAME_VOTING)
                processResults(stateStorage, chatId, voteMessageId)
            }
        }
    }

    companion object {
        suspend fun Bot.processResults(stateStorage: StateStorage, chatId: Long, voteMessageId: Int) {
            val state = stateStorage.getGameState(chatId) ?: return
            val votes = state.stopVotes.getAndSet(null) ?: return
            deleteMessageLogging(chatId, voteMessageId)

            val (upVotes, downVotes) = votes.values.partition { it }.toList().map { it.size }

            if (upVotes >= 1 && upVotes > downVotes) {
                sendMessageLogging(chatId, ON_REGISTRATION_STOP)
                stateStorage.gameOver(chatId)
            } else {
                sendMessageLogging(chatId, "Failed to stop the game")
            }
        }
    }
}
