package com.github.pool_party.resistance_bot.callback

import com.github.pool_party.telegram_bot_utils.interaction.callback.AbstractCallbackDispatcher
import com.github.pool_party.telegram_bot_utils.interaction.callback.Callback
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

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

class CallbackDispatcher(callbacks: List<Callback<CallbackData>>) :
    AbstractCallbackDispatcher<CallbackData>(callbacks) {

    override fun getCallbackData(data: String): CallbackData = CallbackData.of(data)
}
