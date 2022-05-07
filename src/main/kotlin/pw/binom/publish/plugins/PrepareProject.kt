package pw.binom.publish.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class PrepareProject : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(BinomPublishPlugin::class.java)
        target.plugins.apply(CentralPublicationPlugin::class.java)
        target.plugins.apply(PublicationAuthorPlugin::class.java)
        target.plugins.apply(SignPlugin::class.java)

        val kotlin = target.extensions.findByType(KotlinMultiplatformExtension::class.java)
        if (kotlin != null) {
            target.plugins.apply(MultiplatformDocsPlugin::class.java)
        }
    }
}
