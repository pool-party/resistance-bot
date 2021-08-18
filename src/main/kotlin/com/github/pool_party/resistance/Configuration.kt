package com.github.pool_party.resistance

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.PropertyLocation
import com.natpryce.konfig.booleanType
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import kotlin.reflect.KProperty

object Configuration {

    private val configuration = EnvironmentVariables()
        .overriding(ConfigurationProperties.fromResource("default.properties"))
        .let {
            val testProperties = "test.properties"
            if (ClassLoader.getSystemClassLoader().getResource(testProperties) != null)
                ConfigurationProperties.fromResource(testProperties) overriding it
            else it
        }

    val APP_URL by Configured(stringType)
    val USERNAME by Configured(stringType)
    val PORT by Configured(intType)
    val HOST by Configured(stringType)

    val LONGPOLL by Configured(booleanType)

    val TELEGRAM_TOKEN by Configured(stringType)

    val REGISTRATION_SECONDS by Configured(intType)

    val PLAYERS_GAME by Configured(intType)

    val PLAYERS_MISSION by Configured(intType)

    val WIN_NUMBER by Configured(intType)

    val REJECTIONS_NUMBER by Configured(intType)

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
}
