package pw.binom.publish.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import pw.binom.publish.applyPluginIfNotApplied
import pw.binom.publish.eachKotlinNativeCompile

class LintKotlinPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.applyPluginIfNotApplied(org.jmailen.gradle.kotlinter.KotlinterPlugin::class)
        val lintKotlin = target.tasks.findByName("lintKotlin")
        target.tasks.findByName("compileKotlin")?.dependsOn(lintKotlin)
        target.tasks.eachKotlinNativeCompile {
            it.dependsOn(lintKotlin)
        }
    }
}
