
/**
 * Tests run from IDE in included builds can't recognize root wrapper
 * https://youtrack.jetbrains.com/issue/IDEA-262528
 */
plugins {
    id("convention.detekt")
    id("convention.lifecycle")
    id("convention.dependency-updates")
    id("com.android.library") version "7.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
}

buildscript {

    dependencies {
        classpath(libs.googleServices)
    }

    repositories {
        google()
        mavenCentral()

        maven { url = uri("https://plugins.gradle.org/m2/") }
        gradlePluginPortal()
        flatDir {
            dirs("libs")
        }
    }

    dependencies {
        classpath(libs.androidGradle)
        classpath(libs.kotlinGradle)
    }
}

tasks.withType<Wrapper>().configureEach {
    distributionType = Wrapper.DistributionType.BIN
    gradleVersion = "7.6.1"
}
subprojects {
    if (gradle.startParameter.isConfigureOnDemand &&
        parent != rootProject
    ) {
        generateSequence(parent) { project -> project.parent.takeIf { it != rootProject } }
            .forEach { evaluationDependsOn(it.path) }
    }
}

val initialTaskNames: List<String> = project.gradle.startParameter.taskNames
project.gradle.startParameter.setTaskNames(initialTaskNames)

val checkAll = tasks.named("checkAll") {
    group = "Puls Checks"
    description = "Run all tests and static analysis tools"

    dependsOn(tasks.named("detektAll"))
}
