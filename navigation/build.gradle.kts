plugins {
    id("android-library-convention")
}

dependencies {
    implementation(libs.voyagerNavigator)

    implementation(libs.cicerone)
    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
    implementation(libs.fragmentKtx)
}