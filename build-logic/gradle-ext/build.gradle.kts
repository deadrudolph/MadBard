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
    // workaround for https://github.com/gradle/gradle/issues/15383
    implementation(libs.commonsConfiguration)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
