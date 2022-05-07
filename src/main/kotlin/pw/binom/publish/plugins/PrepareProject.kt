package pw.binom.publish.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.konan.target.HostManager

class PrepareProject : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(BinomPublishPlugin::class.java)
        target.plugins.apply(CentralPublicationPlugin::class.java)
        target.plugins.apply(PublicationAuthorPlugin::class.java)
        target.plugins.apply(SignPlugin::class.java)

        val kotlin = target.extensions.findByType(KotlinMultiplatformExtension::class.java) ?: return
        target.plugins.apply(MultiplatformDocsPlugin::class.java)
        if (HostManager.hostIsMac) {
            target.afterEvaluate {
                kotlin.targets.forEach {
                    it.compilations.forEach {
                        val preset = it.target.preset?.name
                        it.compileKotlinTaskProvider.configure {
                            it.onlyIf {
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
                    }
                    it.targetName
                }
            }
        }
    }
}
