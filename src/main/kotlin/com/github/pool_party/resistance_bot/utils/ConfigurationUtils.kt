package com.github.pool_party.resistance_bot.utils

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

object ConfigurationUtils {

    private val configuration = EnvironmentVariables()
        .overriding(ConfigurationProperties.fromResource("default.properties"))
        .let {
            val testProperties = "test.properties"
            if (ClassLoader.getSystemClassLoader().getResource(testProperties) != null)
                ConfigurationProperties.fromResource(testProperties) overriding it
            else it
        }

    fun <K> boolean() = Configured<Boolean, K>(booleanType)

    fun <K> int() = Configured<Int, K>(intType)

    fun <K> string() = Configured<String, K>(stringType)

    fun <K> seconds() = ConfiguredDuration<K>("seconds", Duration::seconds)

    private val <T> KProperty<T>.configName
        get() = name.lowercase().replace('_', '.')

    class Configured<T, K>(private val parse: (PropertyLocation, String) -> T) {

        private var value: T? = null

        operator fun getValue(thisRef: K, property: KProperty<*>): T {
            if (value == null) {
                value = configuration[Key(property.configName, parse)]
            }
            return value!!
        }

        operator fun setValue(thisRef: K, property: KProperty<*>, value: T) {
            this.value = value
        }
    }

    class ConfiguredDuration<K>(private val name: String, private val duration: (Int) -> Duration) {

        private var value: Duration? = null

        operator fun getValue(thisRef: K, property: KProperty<*>): Duration {
            if (value == null) {
                value = duration(configuration[Key("${property.configName}.$name", intType)])
            }
            return value!!
        }
    }
}
