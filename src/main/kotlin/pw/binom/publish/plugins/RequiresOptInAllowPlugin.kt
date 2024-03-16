package pw.binom.publish.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import pw.binom.publish.getKotlin

class RequiresOptInAllowPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val kotlin = target.getKotlin()
        kotlin.targets.configureEach {
            it.compilations.all {
                it.compileTaskProvider.get().compilerOptions {
                    this.optIn.add("kotlin.RequiresOptIn")
                }
            }
        }
    }
}
