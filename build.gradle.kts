buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jetbrains.com/intellij-repository/releases") }
        maven { url = uri("https://jetbrains.bintray.com/intellij-third-party-dependencies") }
    }
    dependencies {
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.2")
    }
}

plugins {
    kotlin("jvm") version "1.5.31" apply false
    kotlin("plugin.serialization") version "1.5.31" apply false
    id("com.squareup.sqldelight") version "1.5.2" apply false
}

group = "chenjox.biw"
version = "2.0-ALPHA"



/*
dependencies {
    implementation(kotlin("stdlib"))
}
 */