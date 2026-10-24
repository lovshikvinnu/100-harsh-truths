plugins {
    id("com.android.application") version "8.5.0" apply false
    id("com.android.library") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
}

// AGP 8.5 requires Gradle 8.7+
// Kotlin 2.0.0 with Compose Compiler plugin
// Compose BOM 2024.10.00 works with Kotlin 2.0.0

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}