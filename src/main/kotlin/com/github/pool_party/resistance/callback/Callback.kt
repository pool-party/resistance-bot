package com.github.pool_party.resistance.callback

import com.elbekD.bot.Bot
import com.elbekD.bot.types.CallbackQuery
import com.github.pool_party.resistance.Interaction
import com.github.pool_party.resistance.decode
import com.github.pool_party.resistance.encodeInner
import kotlinx.serialization.Serializable
import mu.KotlinLogging

enum class CallbackAction {
    SQUAD_CHOICE, VOTE
}

@Serializable
data class CallbackData(val callbackAction: CallbackAction, val otherData: String) {

    // TODO maybe it is possible to overcome this boilerplate
    companion object {
        fun of(string: String) = decode<CallbackData>(string)

        inline fun <reified T> of(callbackAction: CallbackAction, otherData: T) =
            CallbackData(callbackAction, encodeInner(otherData))
    }
}

interface Callback {

    val callbackAction: CallbackAction

    suspend fun Bot.process(callbackQuery: CallbackQuery, callbackData: CallbackData)
}

class CallbackDispatcher(private val callbacks: Map<CallbackAction, Callback>) : Interaction {

    private val logger = KotlinLogging.logger {}

    override fun apply(bot: Bot) = bot.onCallbackQuery {
        logger.info { "callback ${it.from.username}@${it.message?.chat?.title}: ${it.data}" }

        val callbackData = it.data?.let { CallbackData.of(it) } ?: return@onCallbackQuery
        val callback = callbacks[callbackData.callbackAction] ?: return@onCallbackQuery

        with(callback) { bot.process(it, callbackData) }
    }
}
