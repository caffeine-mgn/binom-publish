package pw.binom.publish.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.konan.target.HostManager
import pw.binom.publish.propertyOrNull

class PrepareProject : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(BinomPublishPlugin::class.java)
        target.plugins.apply(CentralPublicationPlugin::class.java)
        target.plugins.apply(PublicationAuthorPlugin::class.java)
        target.plugins.apply(SignPlugin::class.java)
        target.plugins.apply(LintKotlinPlugin::class.java)

        val kotlin = target.extensions.findByType(KotlinMultiplatformExtension::class.java) ?: return
        if (target.propertyOrNull("disable-javadoc") != "true") {
            target.plugins.apply(MultiplatformDocsPlugin::class.java)
        }
        if (HostManager.hostIsMac) {
            kotlin.targets.removeIf {
                val preset = it.preset?.name
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
//            kotlin.sourceSets.removeIf {
//                it.requiresVisibilityOf
//            }
        }
    }
}
