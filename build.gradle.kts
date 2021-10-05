import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
}

group = "org.pool-party"
version = "0.1.0"

repositories {
    maven("https://jitpack.io")
    mavenCentral()
    flatDir {
        dirs = mutableSetOf(file("dependencies"))
    }
}

val jupyterVersion = "5.6.0"
val kotlinVersion = "1.5.30"

dependencies {
    implementation("org.jetbrains.kotlin", "kotlin-stdlib-jdk8", kotlinVersion)
    implementation("org.jetbrains.kotlin", "kotlin-reflect", kotlinVersion)
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-protobuf", "1.3.0")
    runtimeOnly("org.jetbrains.kotlinx", "kotlinx-serialization-runtime", "1.0-M1-1.4.0-rc")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.5.2")

    implementation("com.github.elbekD", "kt-telegram-bot", "1.3.8")
    implementation("org.pool-party", "flume", "0.0.1")

    implementation("com.natpryce", "konfig", "1.6.10.0")

    implementation("org.slf4j", "slf4j-simple", "2.0.0-alpha2")
    implementation("io.github.microutils", "kotlin-logging", "2.0.11")

    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit5", kotlinVersion)
    testImplementation("org.junit.jupiter", "junit-jupiter-api", jupyterVersion)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", jupyterVersion)
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    configurations["compileClasspath"].forEach { from(zipTree(it.absoluteFile)) }
    configurations["runtimeClasspath"].forEach { from(zipTree(it.absoluteFile)) }

    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.github.pool_party.resistance_bot.MainKt"
            )
        )
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.DelicateCoroutinesApi"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
}
