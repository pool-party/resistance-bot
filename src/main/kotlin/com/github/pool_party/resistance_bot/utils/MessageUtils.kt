package com.github.pool_party.resistance_bot.utils

import com.elbekD.bot.Bot
import com.elbekD.bot.types.InlineKeyboardMarkup
import com.elbekD.bot.types.Message
import com.elbekD.bot.types.ReplyKeyboard
import mu.KotlinLogging
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger { }

fun <T> CompletableFuture<T>.logging(prefix: String = ""): CompletableFuture<T> = handleAsync { value, throwable ->
    if (throwable != null) {
        logger.error { "$prefix: ${throwable.message}:\n${throwable.stackTraceToString()}" }
        throw throwable
    }
    value
}

private fun String.escapeMarkdown() = replace("[-!.]".toRegex()) { "\\${it.groupValues[0]}" }

fun Bot.sendMessageLogging(
    chatId: Long,
    text: String,
    markup: ReplyKeyboard? = null,
    replyTo: Int? = null
): CompletableFuture<out Message> {
    logger.debug { "Sending '$text'" }
    return sendMessage(chatId, text.escapeMarkdown(), "MarkdownV2", replyTo = replyTo, markup = markup)
        .logging("Failed to send message \"$text\"")
}

fun Bot.editMessageTextLogging(
    chatId: Long,
    messageId: Int,
    text: String,
    markup: InlineKeyboardMarkup? = null
): CompletableFuture<out Message> {
    logger.debug { "Editing '$text'" }
    return editMessageText(chatId, messageId, text = text.escapeMarkdown(), parseMode = "MarkdownV2", markup = markup)
        .logging("Failed to send message \"$text\"")
}
