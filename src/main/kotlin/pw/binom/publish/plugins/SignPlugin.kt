package pw.binom.publish.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import pw.binom.publish.PUBLISH_PLUGIN_NOT_EXIST_MESSAGE
import pw.binom.publish.applyPluginIfNotApplied
import pw.binom.publish.propertyOrNull
import pw.binom.publish.publishing

private const val GPG_KEY_ID_PROPERTY = "binom.gpg.key_id"
private const val GPG_PASSWORD_PROPERTY = "binom.gpg.password"
private const val GPG_PRIVATE_KEY_PROPERTY = "binom.gpg.private_key"

class SignPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.applyPluginIfNotApplied("signing")
        target.afterEvaluate {
            val publishing = target.publishing
            if (publishing == null) {
                target.logger.warn("Can't sign jar files. $PUBLISH_PLUGIN_NOT_EXIST_MESSAGE")
                return@afterEvaluate
            }

            val gpgKeyId = target.propertyOrNull(GPG_KEY_ID_PROPERTY)
            val gpgPassword = target.propertyOrNull(GPG_PASSWORD_PROPERTY)
            val gpgPrivateKey = target.propertyOrNull(GPG_PRIVATE_KEY_PROPERTY)?.replace("|", "\n")

            if (gpgKeyId == null || gpgPassword == null || gpgPrivateKey == null) {
                val sb = StringBuilder()
                sb.appendLine("gpg configuration missing. Jar will be publish without sign. Reasons:")
                if (gpgKeyId == null) {
                    sb.appendLine("  Property $GPG_KEY_ID_PROPERTY not found")
                }
                if (gpgPassword == null) {
                    sb.appendLine("  Property $GPG_PASSWORD_PROPERTY not found")
                }
                if (gpgPrivateKey == null) {
                    sb.appendLine("  Property $GPG_PRIVATE_KEY_PROPERTY not found")
                }
                target.logger.warn(sb.toString())
                return@afterEvaluate
            }

            target.extensions.configure(SigningExtension::class.java) {
                it.useInMemoryPgpKeys(gpgKeyId, gpgPrivateKey, gpgPassword)
                it.sign(publishing.publications)
                it.setRequired(target.tasks.withType(AbstractPublishToMaven::class.java))
            }

            target.tasks.withType(AbstractPublishToMaven::class.java) { publishTask ->
                val signTasks = target.tasks.withType(Sign::class.java)
                /*
                val s = publishTask.name.removePrefix("publish")
                val i = s.indexOf("PublicationTo")
                if (i == -1) {
                    TODO()
                }
                val bb = s.substring(0, i)
                val signName = "sign${bb}Publication"
                val signTask = signTasks.find { it -> it.name == signName } ?: TODO("Sign task not found")
                println("${publishTask.name}->$bb -> ${signTask.name}")
                */
//                publishTask.dependsOn(signTask)
//                publishTask.mustRunAfter(signTask)
                publishTask.mustRunAfter(signTasks)
//                publishTask.dependsOn(target.tasks.withType(Sign::class.java))
            }

        }
    }
}
