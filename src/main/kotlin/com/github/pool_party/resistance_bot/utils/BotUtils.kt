package com.github.pool_party.resistance_bot.utils

import com.elbekD.bot.Bot
import com.elbekD.bot.types.InlineKeyboardButton
import com.elbekD.bot.types.InlineKeyboardMarkup
import com.elbekD.bot.types.Message
import com.elbekD.bot.types.User
import com.github.pool_party.resistance_bot.Configuration
import com.github.pool_party.resistance_bot.message.INIT_MARKUP
import com.github.pool_party.resistance_bot.message.REGISTRATION_BUTTON
import com.github.pool_party.resistance_bot.message.VOTING_SUGGEST
import mu.KotlinLogging
import kotlin.time.Duration

private val logger = KotlinLogging.logger { }

val Message.chatId
    get() = chat.id

val User.name
    get() = sequenceOf(first_name, last_name).filterNotNull().joinToString(" ")

fun List<InlineKeyboardButton>.toMarkUp() = InlineKeyboardMarkup(listOf(this))

fun makeRegistrationMarkup(hash: String) =
    listOf(InlineKeyboardButton(REGISTRATION_BUTTON, url = botLink(hash))).toMarkUp()

fun addBotMarkup() = listOf(InlineKeyboardButton(INIT_MARKUP,
    url = "https://t.me/${Configuration.USERNAME}?startgroup=true")).toMarkUp()

fun goToBotMarkup() = listOf(InlineKeyboardButton(VOTING_SUGGEST, url = botLink())).toMarkUp()

fun botLink(hash: String? = null) =
    "https://t.me/${Configuration.USERNAME}${if (hash == null) "" else "?start=${hash}"}"

fun makeUserLink(name: String, id: Long) = "[${name}](tg://user?id=${id})"

fun durationToString(duration: Duration): String {
    fun appendTimeType(number: Long, measure: String) =
        if (number != 0L) if (number != 1L) "$number ${measure}s" else "$number $measure" else ""

    val minutes = appendTimeType(duration.inWholeMinutes, "minute")
    val seconds = appendTimeType(duration.inWholeSeconds % 60, "second")

    val res = if (seconds.isBlank()) minutes else "$minutes $seconds"
    return res.ifBlank { "0" } // TODO handle blank normally.
}
