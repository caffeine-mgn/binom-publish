package pw.binom.publish.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import pw.binom.publish.getKotlin

class RequiresOptInAllowPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val kotlin = target.getKotlin()
        kotlin.targets.configureEach {
            it.compilations.all {
                it.compileKotlinTask.kotlinOptions.freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn")
            }
        }
    }
}
