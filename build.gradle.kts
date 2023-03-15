import pw.binom.propertyOrNull

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
    id("org.jmailen.kotlinter") version "3.10.0"
    id("com.gradle.plugin-publish") version "0.16.0"
}

allprojects {
    version = System.getenv("GITHUB_REF_NAME")
        ?: propertyOrNull("version")
            ?.takeUnless { it == "unspecified" }
        ?: "1.0.0-SNAPSHOT"
    group = "pw.binom"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2")
    }
}

apply {
    plugin(pw.binom.plugins.BinomPublishPlugin::class.java)
    plugin(pw.binom.plugins.CentralPublicationPlugin::class.java)
    plugin(pw.binom.plugins.PublicationAuthorPlugin::class.java)
    plugin(pw.binom.plugins.SignPlugin::class.java)
    plugin(org.jetbrains.dokka.gradle.DokkaPlugin::class.java)
}

extensions.getByType(pw.binom.plugins.PublicationPomInfoExtension::class).apply {
    useApache2License()
    gitScm("https://github.com/caffeine-mgn/binom-publish")
    author(
        id = "subochev",
        name = "Anton Subochev",
        email = "caffeine.mgn@gmail.com"
    )
}

dependencies {
    api(gradleApi())
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:${pw.binom.Versions.KOTLIN_VERSION}")
    api("org.jetbrains.dokka:dokka-gradle-plugin:1.6.10")
    api("org.jmailen.gradle:kotlinter-gradle:3.14.0")
}

kotlinter {
//    indentSize = 4
    disabledRules = arrayOf("no-wildcard-imports")
}
tasks {
    val dokkaJavadoc by getting
    val javadocJar by creating(Jar::class) {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }
}

gradlePlugin {
    plugins {
        create("binom-publish") {
            id = "binom-publish"
            implementationClass = "pw.binom.publish.plugins.PrepareProject"
            description = "Publication Helper"
            isAutomatedPublishing = false
        }
    }
}

publishing {
    publications {
        val sources = tasks.getByName("kotlinSourcesJar")
        val docs = tasks.getByName("javadocJar")
        create<MavenPublication>("Main") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["kotlin"])
            artifact(sources)
            artifact(docs)
        }
    }
}

tasks {
    val compileKotlin by getting {
        dependsOn("lintKotlinMain")
    }
}
