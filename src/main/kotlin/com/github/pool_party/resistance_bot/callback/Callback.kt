package com.github.pool_party.resistance_bot.callback

import com.elbekD.bot.Bot
import com.elbekD.bot.types.CallbackQuery
import com.github.pool_party.resistance_bot.Interaction
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import mu.KotlinLogging
import kotlin.reflect.KClass

/**
 * There is a hack with short property serial names to fit into 64 Telegram API callback data bytes.
 */
@Serializable
sealed class CallbackData {
    abstract val gameChatId: Long

    val encoded: String
        get() = ProtoBuf.encodeToByteArray(this).joinToString("") { it.toInt().toChar().toString() }

    companion object {
        fun of(string: String) =
            ProtoBuf.decodeFromByteArray<CallbackData>(string.map { it.code.toByte() }.toByteArray())
    }
}

interface Callback {

    val callbackDataKClass: KClass<out CallbackData>

    suspend fun Bot.process(callbackQuery: CallbackQuery, callbackData: CallbackData)
}

class CallbackDispatcher(callbacks: List<Callback>) : Interaction {

    private val logger = KotlinLogging.logger {}

    private val callbackMap: Map<KClass<out Any>, Callback> = callbacks.associateBy { it.callbackDataKClass }

    override fun apply(bot: Bot) = bot.onCallbackQuery {
        val callbackData = it.data?.let { data -> CallbackData.of(data) }

        logger.info {
            val bytes = it.data?.asSequence()?.joinToString("") { char -> "%02x".format(char.code.toByte()) }
            "callback ${it.from.username}@${it.message?.chat?.title}: $bytes >=> $callbackData"
        }

        if (callbackData == null) return@onCallbackQuery

        val callback = callbackMap[callbackData::class] ?: return@onCallbackQuery

        with(callback) { bot.process(it, callbackData) }
    }
}
