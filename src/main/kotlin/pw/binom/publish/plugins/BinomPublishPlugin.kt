package pw.binom.publish.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import pw.binom.publish.*
import java.net.URI
import java.util.logging.Logger

private const val msg = "Publication to Binom repository is disabled."

class BinomPublishPlugin : Plugin<Project> {
    private val logger = Logger.getLogger(this::class.java.name)
    override fun apply(target: Project) {
        val publishing = target.publishing
        if (publishing == null) {
            target.logger.warn("$msg $PUBLISH_PLUGIN_NOT_EXIST_MESSAGE")
            return
        }

        val user = target.propertyOrNull(BINOM_REPO_USER_PROPERTY)
        val password = target.propertyOrNull(BINOM_REPO_PASSWORD_PROPERTY)

        if (user == null || password == null) {
            val sb = StringBuilder()
            sb.appendLine("$msg Reasons:")
            if (user == null) {
                sb.appendLine("  $BINOM_REPO_USER_PROPERTY not set")
            }

            if (password == null) {
                sb.appendLine("  $BINOM_REPO_PASSWORD_PROPERTY not set")
            }
            target.logger.warn(sb.toString())
            return
        }

        publishing.repositories {
            it.maven {
                it.name = "Binom"
                it.url = URI(BINOM_REPOSITORY_URL)
                it.credentials {
                    it.username = target.stringProperty(BINOM_REPO_USER_PROPERTY)
                    it.password = target.stringProperty(BINOM_REPO_PASSWORD_PROPERTY)
                }
            }
        }
    }
}
