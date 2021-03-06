package com.github.pool_party.resistance_bot

import com.elbekD.bot.Bot
import com.elbekD.bot.server
import com.github.pool_party.resistance_bot.utils.initHandlers

fun main() {
    val token = Configuration.TELEGRAM_TOKEN
    val userName = Configuration.USERNAME

    val bot = if (Configuration.LONGPOLL) {
        Bot.createPolling(userName, token)
    } else {
        Bot.createWebhook(userName, token) {
            url = "${Configuration.APP_URL}/$token"

            server {
                host = Configuration.HOST
                port = Configuration.PORT
            }
        }
    }

    bot.initHandlers()
    bot.start()
}
