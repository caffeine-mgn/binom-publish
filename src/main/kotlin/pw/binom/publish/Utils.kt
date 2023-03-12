package pw.binom.publish

import org.gradle.api.*
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.TaskContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinNativeCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile
import org.jetbrains.kotlin.konan.target.HostManager
import java.io.ByteArrayOutputStream
import kotlin.reflect.KClass

const val BINOM_REPOSITORY_URL = "https://repo.binom.pw"
const val PUBLISH_PLUGIN_NOT_EXIST_MESSAGE = "Do you shure plugin \"maven-publish\" was applyed?"
const val BINOM_REPO_USER_PROPERTY = "binom.repo.user"
const val BINOM_REPO_PASSWORD_PROPERTY = "binom.repo.password"

fun Project.propertyOrNull(property: String) =
    if (hasProperty(property)) property(property) as String else null

fun RepositoryHandler.binom() {
    maven {
        it.setUrl(BINOM_REPOSITORY_URL)
    }
}

fun Project.stringProperty(property: String) =
    propertyOrNull(property) ?: throw GradleException("Property \"$property\" not set")

@get:JvmName("getPublishingOrNull")
val Project.publishing
    get() = (extensions.findByName("publishing") as PublishingExtension?)

fun Project.getPublishing() =
    publishing ?: throw GradleException("Can't find publishing extension. $PUBLISH_PLUGIN_NOT_EXIST_MESSAGE")

val Project.isSnapshot
    get() = (version as String).endsWith("-SNAPSHOT")

fun Project.applyPluginIfNotApplied(name: String) {
    apply {
        it.plugin(name)
    }
}

fun Project.applyPluginIfNotApplied(clazz: KClass<out Plugin<*>>) {
    apply {
        it.plugin(clazz.java)
    }
}

fun Project.getExternalVersion() =
    System.getenv("GITHUB_REF_NAME") // github
        ?: System.getenv("DRONE_TAG") // drone
        ?: System.getenv("CI_COMMIT_TAG") // gitlab
        ?: propertyOrNull("version")
            ?.takeIf { it != "unspecified" }
        ?: "1.0.0-SNAPSHOT"

fun Project.getGitBranch(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        it.commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
        it.standardOutput = stdout
    }
    return stdout.toString().trim()
}

fun Project.getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        it.commandLine("git", "rev-parse", "--short", "HEAD")
        it.standardOutput = stdout
    }
    return stdout.toString().trim()
}

fun TaskContainer.eachKotlinCompile(func: (Task) -> Unit) {
    this.mapNotNull { it as? AbstractKotlinCompile<*> }
        .forEach(func)
    eachKotlinNativeCompile(func)
}

fun TaskContainer.eachKotlinNativeCompile(func: (AbstractKotlinNativeCompile<*, *, *>) -> Unit) {
    this
//        .mapNotNull { it as? AbstractKotlinNativeCompile<*, *> }
        .mapNotNull { it as? KotlinNativeCompile }
        .filter { "Test" !in it.name }
        .forEach(func)
}

fun TaskContainer.eachKotlinTest(func: (Task) -> Unit) {
    this.mapNotNull { it as? org.jetbrains.kotlin.gradle.tasks.KotlinTest }
        .forEach(func)
    this.mapNotNull { it as? org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest }
        .forEach(func)
}

fun ifNotMac(func: () -> Unit) {
    if (!HostManager.hostIsMac) {
        func()
    }
}

fun Project.applyMacSeparateBuild() {
    val kotlin = extensions.findByType(KotlinMultiplatformExtension::class.java)
        ?: return
    kotlin.targets.forEach {
        it.compilations.forEach {
            val preset = it.target.preset?.name
            it.compileKotlinTaskProvider.get().onlyIf {
                when (preset) {
                    "iosArm32",
                    "iosArm64",
                    "iosSimulatorArm64",
                    "iosX64",
                    "macosArm64",
                    "macosX64",
                    "watchosArm32",
                    "watchosArm64",
                    "watchosSimulatorArm64",
                    "watchosX64",
                    "watchosX86",
                    "tvos",
                    "tvosArm64",
                    "tvosSimulatorArm64",
                    "tvosX64",
                    -> true

                    else -> false
                }
            }
        }
        it.targetName
    }
}

fun <T : Named> NamedDomainObjectContainer<T>.forEach(mask: String, func: (T) -> Unit) {
    forEach { it ->
        if (it.name.isWildcardMatch(mask)) {
            func(it)
        }
    }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.dependsOn(mask: String, to: String): List<KotlinSourceSet> {
    val toSourceSet = this.getByName(to)
    return dependsOn(mask = mask, to = toSourceSet)
}

fun NamedDomainObjectContainer<KotlinSourceSet>.dependsOn(mask: String, to: KotlinSourceSet): List<KotlinSourceSet> {
    val deps = filter { it !== to && it.name.isWildcardMatch(mask) }
    deps.forEach {
        it.dependsOn(to)
    }
    return deps
}

fun Project.getKotlin() = extensions.getByType(KotlinMultiplatformExtension::class.java)
