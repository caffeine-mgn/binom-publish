package pw.binom.publish

import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun NamedDomainObjectContainer<KotlinSourceSet>.useDefault() {
    fun KotlinSourceSet.dp(other: KotlinSourceSet?): KotlinSourceSet {
        if (other != null) {
            dependsOn(other)
        }
        return this
    }

    fun Pair<KotlinSourceSet, KotlinSourceSet>.dp(other: Pair<KotlinSourceSet?, KotlinSourceSet?>): Pair<KotlinSourceSet, KotlinSourceSet> {
        first.dp(other.first)
        second.dp(other.second)
        return this
    }

    fun dependsOn(target: String, vararg other: Pair<KotlinSourceSet?, KotlinSourceSet?>): List<KotlinSourceSet> {
        val result = ArrayList<KotlinSourceSet>()
        other.forEach { other ->
            other.first?.let {
                result += dependsOn("${target}Main", it)
            }
            other.second?.let {
                result += dependsOn("${target}Test", it)
            }
        }
        return result
    }

    fun findTarget(name: String) = findByName("${name}Main") to findByName("${name}Test")
    fun createTarget(name: String) = create("${name}Main") to create("${name}Test")
    val js = findTarget("js")
    val common = findTarget("common")
    val jvmLike = createTarget("jvmLike").dp(common)
    val nativeCommon = createTarget("nativeCommon").dp(common)
    val nativeRunnableMain = createTarget("nativeRunnable").dp(nativeCommon)
    val posixDesktop = createTarget("posixDesktop").dp(nativeRunnableMain)
    val mingwMain = createTarget("mingw").dp(nativeRunnableMain)
    val posixMain = createTarget("posix").dp(nativeRunnableMain)

    val linuxMain = createTarget("linux").dp(posixMain)
    val androidNativeMain = createTarget("androidNative")
    val darwinMain = createTarget("darwin").dp(posixMain)

    dependsOn("jvm", jvmLike)
    dependsOn("android", jvmLike)
    dependsOn("linux*", linuxMain)
    dependsOn("mingw*", mingwMain)
    dependsOn("watchos*", posixMain)
    dependsOn("macos*", darwinMain, posixDesktop)
    dependsOn("ios*", darwinMain)
    dependsOn("tvos*", darwinMain)
    dependsOn("androidNative*", androidNativeMain)
    dependsOn("wasm*", nativeCommon)
    dependsOn("androidMain", jvmLike)

    val linuxLikeMain = createTarget("linuxLike").dp(posixMain).dp(posixDesktop)
    androidNativeMain.dp(linuxLikeMain)
    linuxMain.dp(linuxLikeMain)

    common.second?.let {
        it.dependencies {
            api(kotlin("test-common"))
            api(kotlin("test-annotations-common"))
        }
    }

    jvmLike.second.let {
        it.dependencies {
            api(kotlin("test"))
        }
    }
    js.second?.apply {
        dependencies {
            api(kotlin("test-js"))
        }
    }
}
