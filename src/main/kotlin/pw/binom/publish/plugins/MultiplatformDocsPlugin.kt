package pw.binom.publish.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import pw.binom.publish.PUBLISH_PLUGIN_NOT_EXIST_MESSAGE
import pw.binom.publish.applyPluginIfNotApplied
import pw.binom.publish.publishing

class MultiplatformDocsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.applyPluginIfNotApplied(org.jetbrains.dokka.gradle.DokkaPlugin::class)
        val publishing = target.publishing
        if (publishing == null) {
            target.logger.warn("Can't Generate Documentation for project. $PUBLISH_PLUGIN_NOT_EXIST_MESSAGE")
            return
        }
        val dokkaTask = target.tasks.getByName("dokkaHtml")
        val dokkaJarTask = target.tasks.register("dokkaHtmlJar", Jar::class.java) {
            it.dependsOn(dokkaTask)
            it.archiveClassifier.set("javadoc")
            it.from(dokkaTask.outputs)
        }
        publishing.publications {
            it.configureEach {
                it as MavenPublication
                it.artifact(dokkaJarTask)
            }
        }
        target.tasks.withType(org.jetbrains.dokka.gradle.DokkaTask::class.java).configureEach {
            it.dokkaSourceSets.removeIf {
                it.name.endsWith("Test")
            }
//            it.dokkaSourceSets {
//                dokkaSourceSets.removeIf { it.name.endsWith("Test") }
//                println("Names: ${dokkaSourceSets.names}")
//            }
        }

        /*
        publishing.publications {
            it.forEach { publication ->
                println("--->${publication.name}")
                publication as MavenPublication
                val docTask = target.tasks.register("${publication.name}Doc", DokkaTask::class.java) { dokkaTask ->
//                    dokkaTask.plugins.dependencies.add(target.dependencies.create("org.jetbrains.dokka:kotlin-as-java-plugin:1.6.10"))
//                    dokkaTask.plugins.dependencies.add(target.dependencies.create("org.jetbrains.dokka:javadoc-plugin:1.6.10"))
                    dokkaTask.dokkaSourceSets.create(publication.name) {
                        it.includeNonPublic.set(false)
                        it.displayName.set(publication.name)
                        val platform = when (publication.name) {
                            "js" -> org.jetbrains.dokka.Platform.js
                            "jvm" -> org.jetbrains.dokka.Platform.jvm
                            "kotlinMultiplatform" -> org.jetbrains.dokka.Platform.common
                            else -> org.jetbrains.dokka.Platform.native
                        }
                        it.platform.set(platform)
//                        it.sourceRoots.from(kotlin.sourceSets.getByName("jvmMain").kotlin.srcDirs)
                        it.sourceRoots.from(kotlin.sourceSets.getByName("commonMain").kotlin.srcDirs)
                        val source = kotlin.sourceSets.findByName("${publication.name}Main")
                        if (source != null) {
                            it.sourceRoots.from(source.kotlin.srcDirs)
                        }
                    }
                }
                val docJar = target.tasks.register("${docTask.name}Jar", Jar::class.java) {
                    it.dependsOn(docTask)
                    it.archiveClassifier.set("javadoc")
                    it.archiveBaseName.set("${docTask.name}-javadoc")
                    docTask.get().also { docTask2 ->
                        it.from(docTask2.outputs)
                    }
                }
                publication.artifact(docJar)
            }
        }
        */
    }
}
