

plugins {
    kotlin("jvm")
    id("com.squareup.sqldelight")
}

repositories {
    mavenCentral()
    /*maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }*/
}

dependencies{
    implementation(kotlin("stdlib"))

    implementation("com.squareup.sqldelight:sqlite-driver:1.5.2")
    //val kordexVersion: String by project
    //implementation("com.kotlindiscord.kord.extensions:kord-extensions:$kordexVersion")
}

kotlin {
    version = "1.5.31"
    explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict
}

sqldelight {
    database("ActivityDatabase") {
        packageName = "chenjox.biw.activity.database"
        verifyMigrations = true
    }
}