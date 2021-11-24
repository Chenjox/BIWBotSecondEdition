

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenCentral()
    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies{
    implementation(kotlin("stdlib"))

    val kordexVersion: String by project
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:$kordexVersion")

    val tomlFourKomaVersion: String by project
    implementation("cc.ekblad:4koma:$tomlFourKomaVersion")

    implementation(project(":extensions:date"))
    implementation(project(":extensions:cloud"))
    implementation(project(":extensions:activity:collector"))
    implementation("org.slf4j:slf4j-simple:1.7.25")
}

java {
    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11
    targetCompatibility = org.gradle.api.JavaVersion.VERSION_11
}

kotlin {
    version = "1.5.31"
    explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict

}

tasks{
    shadowJar {
        archiveBaseName.set("shadow")
        archiveClassifier.set("")
        archiveVersion.set("")
        manifest {
            attributes(Pair("Main-Class", "chenjox.biw.bot.BiwBotKt"))
        }
    }
}