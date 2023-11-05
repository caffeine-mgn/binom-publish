# binom-publish

[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Kotlin 1.9.20](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg?style=flat&logo=kotlin)](http://kotlinlang.org)

### Usage

`build.gradle.kts`
```kotlin
plugins {
    id("pw.binom.publish") version "0.1.12"
}
```

`settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        maven(url = "https://repo.binom.pw")
        // other repositories
    }
}
```