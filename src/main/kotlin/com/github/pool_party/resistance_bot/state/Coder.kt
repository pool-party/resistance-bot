package com.github.pool_party.resistance_bot.state

import kotlin.random.Random

interface Coder<T> {

    fun encode(value: T): String

    fun decode(string: String): T
}

class LongCoder : Coder<Long> {

    private val base = 'a'

    private val bits = 4

    private val key = Random.nextLong()

    override fun encode(value: Long): String {
        val mixed = value xor key
        return (0 until Long.SIZE_BITS / bits).asSequence()
            .map { base + mixed.ushr(it * bits).and(0b1111).toInt() }
            .map { it.toString() }
            .joinToString("")
    }

    override fun decode(string: String): Long =
        key xor string.foldIndexed(0L) { index, long, it -> long or (it - base).toLong().shl(index * bits) }
}
