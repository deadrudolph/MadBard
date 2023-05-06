import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("android-library-convention")
}

dependencies {
    implementation(projects.common.commonDi)
    implementation(projects.common.commonNetwork)
    implementation(projects.common.commonDomain)
    implementation(projects.common.commonDatabase)

    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
    implementation(libs.retrofit)
    implementation(libs.moshiKotlin)
    implementation(libs.stateUtils)
}