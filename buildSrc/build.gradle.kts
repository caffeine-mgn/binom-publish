buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
    }
}

plugins {
    kotlin("jvm") version "1.7.0"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
    api("org.jetbrains.dokka:dokka-gradle-plugin:1.6.21")
}
