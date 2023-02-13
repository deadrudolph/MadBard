import gradleext.gradleconventions.withVersionCatalog
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.internal.os.OperatingSystem

plugins {
    /**
     * https://docs.gradle.org/current/userguide/base_plugin.html
     * base plugin added to add wiring on check->build tasks
     */
    base
    id("io.gitlab.arturbosch.detekt")
}

// workaround for https://github.com/gradle/gradle/issues/15383
project.withVersionCatalog { libs ->
    dependencies {
        detektPlugins(libs.detektFormatting)
    }
}

val detektAll = tasks.register<Detekt>("detektAll") {
    description = "Runs over whole code base without the starting overhead for each module."
    parallel = true
    autoCorrect = true
    setSource(files(projectDir))

    /**
     * About config:
     * yaml is a copy of https://github.com/detekt/detekt/blob/master/detekt-core/src/main/resources/default-detekt-config.yml
     * all rules are disabled by default, enabled one by one
     */
    config.setFrom(files(project.rootDir.resolve("conf/detekt.yml")))
    buildUponDefaultConfig = false

    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
    exclude("**/.gradle/**")
    reports {
        html.required.set(true)
        html.outputLocation.set(File("build/detekt/HtmlReportTask.html"))
    }
}

@Suppress("SpreadOperator")
tasks.register<Exec>("openHtmlReport") {
    onlyIf { !project.hasProperty("ci") }
    group = "Build Setup"
    description = "open detekt Html report"
    commandLine(*getOpenCommand(detektAll.get().reports.html.outputLocation.asFile.orNull))
}

tasks.named("check").configure {
    dependsOn(detektAll)
}
fun getOpenCommand(file: File?): Array<String> {
    file ?: return arrayOf()
    val currentOs = OperatingSystem.current()
    return when {
        currentOs.isMacOsX -> arrayOf("open", file.path)
        currentOs.isLinux -> arrayOf("xdg-open", file.path)
        currentOs.isWindows -> arrayOf("cmd", "/c", "start", file.path)
        else -> arrayOf()
    }
}
