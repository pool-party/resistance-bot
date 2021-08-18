package com.github.pool_party.resistance.callback

import com.elbekD.bot.Bot
import com.elbekD.bot.types.CallbackQuery
import com.github.pool_party.resistance.Configuration
import com.github.pool_party.resistance.action.chooseSquad
import com.github.pool_party.resistance.action.vote
import com.github.pool_party.resistance.decodeInner
import com.github.pool_party.resistance.makeUserLink
import com.github.pool_party.resistance.name
import com.github.pool_party.resistance.state.Member
import com.github.pool_party.resistance.state.SquadStorage
import com.github.pool_party.resistance.state.State
import com.github.pool_party.resistance.state.StateStorage
import com.github.pool_party.resistance.state.VoteStorage
import kotlinx.serialization.Serializable

enum class VoteType {
    SQUAD, MISSION
}

// TODO prly gameChatId is a common value
@Serializable
data class VoteCallbackData(val gameChatId: Long, val memberNumber: Int, val verdict: Boolean, val voteType: VoteType) {
    companion object {
        fun of(string: String) = decodeInner<VoteCallbackData>(string)
    }
}

class VoteCallback(
    private val voteStorage: VoteStorage,
    private val stateStorage: StateStorage,
    private val squadStorage: SquadStorage
) : Callback {

    override val callbackAction = CallbackAction.VOTE

    override suspend fun Bot.process(callbackQuery: CallbackQuery, callbackData: CallbackData) {
        val callbackQueryId = callbackQuery.id
        val voteCallbackData = VoteCallbackData.of(callbackData.otherData)
        val gameChatId = voteCallbackData.gameChatId
        val user = callbackQuery.from
        voteStorage.set(gameChatId, Member(user.id.toLong(), user.name), voteCallbackData.verdict)
        val messageId = callbackQuery.message?.message_id

        answerCallbackQuery(callbackQueryId)

        if (messageId == null) {
            return
        }

        editMessageText(user.id, messageId, text = if (voteCallbackData.verdict) """✅""" else """❌""")

        val vote = voteStorage[gameChatId]

        if (vote.size >= voteCallbackData.memberNumber) {
            val result = vote.count { it.second } >= vote.size // TODO >
            voteStorage.clear(gameChatId)

            val state = stateStorage[gameChatId]
            if (state == null) {
                sendMessage(gameChatId, "TODO: game over kekw")
                return
            }

            when (voteCallbackData.voteType) {
                VoteType.SQUAD -> processSquadResults(gameChatId, result, state, vote.asSequence().filter { !it.second }.map { it.first }.toList())
                VoteType.MISSION -> processMissionResults(gameChatId, result, state)
            }
        }
    }

    private suspend fun Bot.processSquadResults(
        chatId: Long,
        result: Boolean,
        state: State,
        potentialSpies: List<Member>,
    ) {
        if (result) {
            val squad = squadStorage[chatId]
            if (squad == null) {
                sendMessage(chatId, "TODO: gg 500")
                return
            }

            sendMessage(chatId, "TODO: SQUAD chosen")
            vote(chatId, squad, VoteType.MISSION)
        } else {
            sendMessage(
                chatId,
                """
                    |TODO: SQUAD choice failed
                    |against:
                    |${potentialSpies.joinToString("\n|") { makeUserLink(it.name, it.id) }}
                """.trimMargin("|"),
                "MarkdownV2"
            )

            if (++state.squadRejections >= Configuration.REJECTIONS_NUMBER) {
                sendMessage(chatId, "TODO: spies won")
                stateStorage.gameOver(chatId)
                return
            }
            nextSquadChoice(chatId, state)
        }
    }

    private suspend fun Bot.processMissionResults(chatId: Long, result: Boolean, state: State) {
        if (result) {
            sendMessage(chatId, "TODO: mission has completed successfully").join()
            state.resistancePoints++
        } else {
            sendMessage(chatId, "TODO: mission has failed").join()
            state.spyPoints++
        }

        if (state.spyPoints >= Configuration.WIN_NUMBER) {
            sendMessage(chatId, "TODO: spies won")
            stateStorage.gameOver(chatId)
            return
        }

        if (state.resistancePoints >= Configuration.WIN_NUMBER) {
            sendMessage(chatId, "TODO: resistance won")
            stateStorage.gameOver(chatId)
            return
        }

        nextSquadChoice(chatId, state)
    }

    private suspend fun Bot.nextSquadChoice(chatId: Long, state: State) {
        val members = state.members
        members += members.removeFirst()
        chooseSquad(chatId, stateStorage)
    }
}
