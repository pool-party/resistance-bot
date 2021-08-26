package com.github.pool_party.resistance_bot.utils

import java.util.concurrent.atomic.AtomicInteger

private fun AtomicInteger.update(update: (Int) -> Int): AtomicInteger {
    while (true) {
        val value = get()
        if (compareAndSet(value, update(value))) break
    }
    return this
}

operator fun AtomicInteger.inc() = update { it + 1 }

operator fun AtomicInteger.dec() = update { it - 1 }
