package com.github.pool_party.resistance_bot.utils

import com.elbekD.bot.types.InlineKeyboardButton
import com.elbekD.bot.types.InlineKeyboardMarkup
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.callback.StopCallbackData
import com.github.pool_party.resistance_bot.message.INIT_MARKUP
import com.github.pool_party.resistance_bot.message.REGISTRATION_BUTTON
import com.github.pool_party.resistance_bot.message.VOTING_SUGGEST
import com.github.pool_party.telegram_bot_utils.utils.toMarkUp

fun makeRegistrationMarkup(hash: String) =
    listOf(InlineKeyboardButton(REGISTRATION_BUTTON, url = botLink(hash))).toMarkUp()

fun addBotMarkup() = listOf(
    InlineKeyboardButton(INIT_MARKUP, url = "https://t.me/${Configuration.USERNAME}?startgroup=true")
).toMarkUp()

fun goToBotMarkup() = listOf(InlineKeyboardButton(VOTING_SUGGEST, url = botLink())).toMarkUp()

fun botLink(hash: String? = null) =
    "https://t.me/${Configuration.USERNAME}${if (hash == null) "" else "?start=$hash"}"

fun makeUserLink(name: String, id: Long) = "[$name](tg://user?id=$id)"

fun makeStopVoteMarkup(chatId: Long, votes: Map<Int, Boolean>): InlineKeyboardMarkup {

    fun makeButton(text: String, verdict: Boolean) =
        InlineKeyboardButton(
            "$text ${votes.values.count { it == verdict }}",
            callback_data = StopCallbackData(chatId, verdict).encoded,
        )

    return listOf(
        makeButton("""Stop 😨""", true),
        makeButton(""" Continue 😎""", false),
    ).toMarkUp()
}
