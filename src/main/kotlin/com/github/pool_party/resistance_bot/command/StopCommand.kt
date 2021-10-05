package com.github.pool_party.resistance_bot.command

import com.elbekD.bot.Bot
import com.elbekD.bot.types.Message
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.message.HELP_STOP
import com.github.pool_party.resistance_bot.message.ON_GAME_STOP
import com.github.pool_party.resistance_bot.message.ON_NON_PLAYER_STOP
import com.github.pool_party.resistance_bot.message.ON_NO_REGISTRATION
import com.github.pool_party.resistance_bot.message.ON_ONGOING_STOP_VOTE
import com.github.pool_party.resistance_bot.message.ON_REGISTRATION_STOP
import com.github.pool_party.resistance_bot.message.ON_STOP_VOTE_FAIL
import com.github.pool_party.resistance_bot.message.stopTheGameVote
import com.github.pool_party.resistance_bot.state.GameState
import com.github.pool_party.resistance_bot.state.RegistrationState
import com.github.pool_party.resistance_bot.state.StateStorage
import com.github.pool_party.resistance_bot.utils.makeStopVoteMarkup
import com.github.pool_party.telegram_bot_utils.interaction.command.AbstractCommand
import com.github.pool_party.telegram_bot_utils.utils.chatId
import com.github.pool_party.telegram_bot_utils.utils.deleteMessageLogging
import com.github.pool_party.telegram_bot_utils.utils.name
import com.github.pool_party.telegram_bot_utils.utils.sendMessageLogging
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap

class StopCommand(private val stateStorage: StateStorage) :
    AbstractCommand(
        "stop",
        "stop a game or registration",
        HELP_STOP,
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
                val messageId = message.message_id
                val votes = ConcurrentHashMap<Int, Boolean>()

                if (state.members.none { it.id == message.from?.id?.toLong() }) {
                    sendMessageLogging(chatId, ON_NON_PLAYER_STOP, replyTo = messageId)
                    return
                }
                if (!state.stopVotes.compareAndSet(null, votes)) {
                    sendMessageLogging(chatId, ON_ONGOING_STOP_VOTE, replyTo = messageId)
                    return
                }

                val voteMessageId =
                    sendMessageLogging(
                        chatId,
                        stopTheGameVote(message.from?.name),
                        markup = makeStopVoteMarkup(chatId, votes)
                    ).join().message_id

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
                sendMessageLogging(chatId, ON_GAME_STOP)
                stateStorage.gameOver(chatId)
            } else {
                sendMessageLogging(chatId, ON_STOP_VOTE_FAIL)
            }
        }
    }
}
