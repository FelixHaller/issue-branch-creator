import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.closure
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.4.31"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "0.7.2"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.1.2"
    // detekt linter - read more: https://detekt.github.io/detekt/kotlindsl.html
    id("io.gitlab.arturbosch.detekt") version "1.16.0"

    id("com.palantir.git-version") version "0.12.3"
}

val gitVersion: groovy.lang.Closure<*> by extra

// Import variables from gradle.properties file
val pluginGroup: String by project
val pluginName: String by project
val pluginVersion: String = gitVersion() as String
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project

val platformType: String by project
val platformVersion: String by project
val platformDownloadSources: String by project

group = pluginGroup
version = pluginVersion

// Configure project's dependencies
repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.2")
    implementation("com.github.slugify:slugify:2.4")
    implementation("org.apache.commons:commons-text:1.9")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.16.0")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName = pluginName
    version = platformVersion
    type = platformType
    downloadSources = platformDownloadSources.toBoolean()
    updateSinceUntilBuild = true

    setPlugins("git4idea")

//  Plugin Dependencies:
//  https://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_dependencies.html
//
//  setPlugins("java")
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
    config = files("./detekt-config.yml")

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
}

changelog {
    version = pluginVersion
    headerParserRegex = "^(\\d*)\\.(\\d*)\\.(\\d*)$"
}

tasks {
    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    listOf("compileKotlin", "compileTestKotlin").forEach {
        getByName<KotlinCompile>(it) {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    withType<Detekt> {
        jvmTarget = "1.8"
    }

    patchPluginXml {
        version(pluginVersion)
        sinceBuild(pluginSinceBuild)
        untilBuild(pluginUntilBuild)

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription(closure {
            File("./README.md").readText().lines().run {
                subList(indexOf("<!-- Plugin description -->") + 1, indexOf("<!-- Plugin description end -->"))
            }.joinToString("/n").run { markdownToHTML(this) }
        })

        // Get the latest available change notes from the changelog file
        changeNotes(closure {
            changelog.getLatest().toHTML()
        })
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token(System.getenv("PUBLISH_TOKEN"))
    }
}
