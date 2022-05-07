package pw.binom.publish

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.TaskContainer
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinNativeCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile
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

fun TaskContainer.eachKotlinNativeCompile(func: (AbstractKotlinNativeCompile<*, *>) -> Unit) {
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
                    "watchosX86" -> true
                    else -> false
                }
            }
        }
        it.targetName
    }
}
