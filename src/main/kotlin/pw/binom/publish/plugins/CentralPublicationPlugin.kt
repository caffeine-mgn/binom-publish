package pw.binom.publish.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import pw.binom.publish.*
import java.net.URI
import java.util.logging.Logger

private const val msg = "Publication to Binom repository is disabled."

class CentralPublicationPlugin : Plugin<Project> {
    private val logger = Logger.getLogger(this::class.java.name)
    override fun apply(target: Project) {
        target.afterEvaluate {
            val centralUserName = target.stringProperty("binom.central.username")
            val centralPassword = target.stringProperty("binom.central.password")
            val publishing = target.publishing
            if (publishing == null) {
                target.logger.warn("$msg $PUBLISH_PLUGIN_NOT_EXIST_MESSAGE")
                return@afterEvaluate
            }
            publishing.repositories {
                it.maven {
                    it.name = "Central"
                    val url = if (target.isSnapshot)
                        "https://s01.oss.sonatype.org/content/repositories/snapshots"
                    else
                        "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2"
                    it.url = URI(url)
                    it.credentials {
                        it.username = centralUserName
                        it.password = centralPassword
                    }
                }
            }
        }
    }
}
