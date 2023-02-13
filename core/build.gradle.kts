plugins {
    id("android-library-convention")
}

dependencies {
    implementation(projects.navigation)

    implementation(libs.activityKtx)
    implementation(libs.appcompat)
    implementation(libs.composeUI)
    implementation(libs.cicerone)
    implementation(libs.material)
    implementation(libs.dagger)
    implementation(libs.daggerCompiler)
    implementation(libs.fragmentKtx)
    implementation(libs.stateUtils)
    implementation(libs.timber)
}