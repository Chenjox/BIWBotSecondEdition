

plugins {
    kotlin("jvm") apply true
    kotlin("plugin.serialization") apply true
}

repositories {
    mavenCentral()
    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }
}

dependencies{
    implementation(kotlin("stdlib"))

    val kordexVersion: String by project
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:$kordexVersion")


    val kotlinxDatetimeVersion: String by project
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

    val kotlinxSerializationVersion: String by project
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
}

kotlin {
    version = "1.5.31"
    explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict

}