package pw.binom.publish

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType
import org.jetbrains.kotlin.konan.target.HostManager

interface TargetConfig {
    operator fun String.unaryMinus()
    operator fun String.unaryPlus()
}

private val commonTargets = setOf(
    "jvm", "js",
    "androidNativeArm32", "androidNativeArm64",
    "androidNativeX64", "androidNativeX86",
    "linuxArm64", "linuxX64", "mingwX64",
    "wasmJs","wasmWasi"
)
private val macTargets = setOf(
    "iosArm64", "iosSimulatorArm64",
    "iosX64", "macosArm64",
    "macosX64", "tvosArm64",
    "tvosSimulatorArm64", "watchosArm32",
    "watchosArm64", "watchosDeviceArm64",
    "watchosSimulatorArm64", "watchosX64",
)

fun KotlinMultiplatformExtension.allTargets() = allTargets { }
fun KotlinMultiplatformExtension.allTargets(func: (TargetConfig.() -> Unit)) {
    val tasks = HashSet<String>()
    tasks += commonTargets
    if (HostManager.hostIsMac) {
        tasks += macTargets
    }
    func(object : TargetConfig {
        override fun String.unaryMinus() {
            tasks -= this
        }

        override fun String.unaryPlus() {
            tasks += this
        }
    })
    tasks.forEach { task ->
        when (task) {
            "jvm" ->
                jvm {
//                    compilations.all {
//            it.kotlinOptions.jvmTarget = kotlinJvmTarget
//                    }
                }

            "js" ->
                js(KotlinJsCompilerType.IR) {
                    browser {
//          testTask {
//            useKarma {
//              useFirefoxHeadless()
//            }
//          }
                    }
                    nodejs()
                }

            "wasmJs" ->
                wasmJs {
                    browser()
                    nodejs()
                    d8()
                }

            "wasmWasi" -> wasmWasi{
                nodejs()
            }

            "android" ->
                androidTarget {
                    publishAllLibraryVariants()
                }

            "androidNativeArm32" -> androidNativeArm32()
            "androidNativeArm64" -> androidNativeArm64()
            "androidNativeX64" -> androidNativeX64()
            "androidNativeX86" -> androidNativeX86()
            "linuxArm64" -> linuxArm64()
            "linuxX64" -> linuxX64()
            "mingwX64" -> mingwX64()
            "iosArm64" -> iosArm64()
            "iosSimulatorArm64" -> iosSimulatorArm64()
            "iosX64" -> iosX64()
            "macosArm64" -> macosArm64()
            "macosX64" -> macosX64()
            "tvosArm64" -> tvosArm64()
            "tvosSimulatorArm64" -> tvosSimulatorArm64()
            "tvosX64" -> tvosX64()
            "watchosArm32" -> watchosArm32()
            "watchosArm64" -> watchosArm64()
            "watchosDeviceArm64" -> watchosDeviceArm64()
            "watchosSimulatorArm64" -> watchosSimulatorArm64()
            "watchosX64" -> watchosX64()
            else -> throw IllegalArgumentException("Unknown target $task")
        }
    }
}