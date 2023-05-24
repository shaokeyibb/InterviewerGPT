@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)

    alias(libs.plugins.mirai.console)

    application
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":mirai-console-plugin"))

    api(libs.mirai.console.terminal)
    api(libs.mirai.core)
}

application {
    // Define the main class for the application.
    mainClass.set("io.hikarilan.interviewergpt.mirai.starter.Main")
}


