package com.github.pool_party.resistance_bot.state

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class RegistrationState(val chatName: String? = null, registrationMessageIdFuture: CompletableFuture<Int>) : State() {

    // This field is guarded by [startedMutex], that must be locked on registering new users.
    val members = mutableListOf<Member>()

    val registrationMessageId: Int by lazy { registrationMessageIdFuture.join() }

    var registrationExtendCounter = AtomicInteger(0)

    private val startedMutex = Mutex()

    private var started = false

    suspend fun withStarted(action: (Boolean) -> Boolean): Boolean {
        val result = startedMutex.withLock { action(started) }
        started = result
        return result
    }

    suspend fun tryStartAndDo(action: suspend () -> Unit) {
        var startingGame = false

        withStarted {
            startingGame = !it
            true
        }

        if (startingGame) {
            action()
        }
    }
}
