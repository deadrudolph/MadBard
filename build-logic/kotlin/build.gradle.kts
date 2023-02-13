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

    implementation(libs.kotlinGradle)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib-jdk8
    implementation(libs.kotlinStdlib)
}
