buildscript {
    val kotlinVersion = properties.get("kotlin.version") as String
    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

val kotlinVersion = project.property("kotlin.version") as String

plugins {
    kotlin("jvm") version "1.9.20"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    api("org.jetbrains.dokka:dokka-gradle-plugin:1.8.10")
}
