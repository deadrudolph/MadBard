plugins {
    `kotlin-dsl`
}

group = "buildlogic"

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}

dependencies {
    implementation(projects.kotlin)
    implementation(projects.gradleExt)

    implementation(libs.kotlinGradle)
    implementation(libs.androidGradle)
    implementation(libs.firebaseDistribution)

    // workaround for https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.kotlinStdlib)
}
