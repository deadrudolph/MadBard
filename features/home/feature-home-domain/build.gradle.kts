plugins {
    id("android-library-convention")
}

dependencies {
    implementation(projects.common.commonDi)
    implementation(projects.common.commonNetwork)

    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
    implementation(libs.retrofit)
    implementation(libs.moshiKotlin)
    implementation(libs.stateUtils)
}
