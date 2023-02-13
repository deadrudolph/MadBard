plugins {
    `kotlin-dsl`
}

group = "com.appiplay.android.buildlogic"

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(projects.gradleExt)
    implementation(libs.detektGradle)
    implementation(libs.versionsGradle)

    // workaround for https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}