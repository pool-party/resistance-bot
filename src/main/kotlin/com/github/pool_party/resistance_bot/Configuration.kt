package com.github.pool_party.resistance_bot

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.PropertyLocation
import com.natpryce.konfig.booleanType
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import kotlin.reflect.KProperty
import kotlin.time.Duration

object Configuration {

    private val configuration = EnvironmentVariables()
        .overriding(ConfigurationProperties.fromResource("default.properties"))
        .let {
            val testProperties = "test.properties"
            if (ClassLoader.getSystemClassLoader().getResource(testProperties) != null)
                ConfigurationProperties.fromResource(testProperties) overriding it
            else it
        }

    val APP_URL by ConfiguredString()
    val USERNAME by ConfiguredString()
    val PORT by ConfiguredInt()
    val HOST by ConfiguredString()

    val LONGPOLL by Configured(booleanType)

    val TELEGRAM_TOKEN by ConfiguredString()

    val REGISTRATION_TIME by ConfiguredSeconds()

    val REGISTRATION_ANNOUNCEMENT_DELAY by ConfiguredSeconds()

    val PLAYERS_GAME_MINIMUM by ConfiguredInt()

    val PLAYERS_GAME_MAXIMUM by ConfiguredInt()

    val PLAYERS_MISSION by ConfiguredInt()

    val WIN_NUMBER by ConfiguredInt()

    val REJECTIONS_NUMBER by ConfiguredInt()

    const val SPY_MARK = """🕵️"""

    const val APPROVE_MARK = """👍"""

    const val REJECT_MARK = """👎"""

    private open class Configured<T>(private val parse: (PropertyLocation, String) -> T) {

        private var value: T? = null

        operator fun getValue(thisRef: Configuration, property: KProperty<*>): T {
            if (value == null) {
                value = configuration[Key(property.name.lowercase().replace('_', '.'), parse)]
            }
            return value!!
        }

        operator fun setValue(thisRef: Configuration, property: KProperty<*>, value: T) {
            this.value = value
        }
    }

    private class ConfiguredInt : Configured<Int>(intType)

    private class ConfiguredString : Configured<String>(stringType)

    private open class ConfiguredDuration(private val name: String, private val duration: (Int) -> Duration) {

        private var value: Duration? = null

        operator fun getValue(thisRef: Configuration, property: KProperty<*>): Duration {
            if (value == null) {
                value = duration(configuration[Key("${property.name.lowercase().replace('_', '.')}.$name", intType)])
            }
            return value!!
        }
    }

    private class ConfiguredSeconds : ConfiguredDuration("seconds", Duration::seconds)
}
