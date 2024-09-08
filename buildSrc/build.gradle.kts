/*
buildscript {
    val kotlinVersion = properties.get("kotlin.version") as String
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}
*/
val kotlinVersion = kotlin.coreLibrariesVersion

plugins {
    kotlin("jvm") version "2.0.20"
//    id("org.jetbrains.kotlin.jvm")
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

buildConfig {
    packageName(project.group.toString())
    buildConfigField("String", "KOTLIN_VERSION", "\"$kotlinVersion\"")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    api("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20")
}
