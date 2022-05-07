package pw.binom.publish.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import pw.binom.publish.*
import java.net.URI
import java.util.logging.Logger

private const val CENTRAL_USERNAME_PROPERTY = "binom.central.username"
private const val CENTRAL_PASSWORD_PROPERTY = "binom.central.password"
private const val msg = "Publication to Central repository is disabled."

class CentralPublicationPlugin : Plugin<Project> {
    private val logger = Logger.getLogger(this::class.java.name)
    override fun apply(target: Project) {
        target.afterEvaluate {
            val publishing = target.publishing
            if (publishing == null) {
                target.logger.warn("$msg $PUBLISH_PLUGIN_NOT_EXIST_MESSAGE")
                return@afterEvaluate
            }

            val centralUserName = target.propertyOrNull(CENTRAL_USERNAME_PROPERTY)
            val centralPassword = target.propertyOrNull(CENTRAL_PASSWORD_PROPERTY)

            if (centralUserName == null || centralPassword == null) {
                val sb = StringBuilder()
                sb.appendLine("$msg Reasons:")
                if (centralUserName == null) {
                    sb.appendLine("  $CENTRAL_USERNAME_PROPERTY not set")
                }

                if (centralPassword == null) {
                    sb.appendLine("  $CENTRAL_PASSWORD_PROPERTY not set")
                }
                target.logger.warn(sb.toString())
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
