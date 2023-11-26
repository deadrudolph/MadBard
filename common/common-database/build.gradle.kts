plugins {
    id("android-library-convention")
    id("kotlin-kapt")
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
}

dependencies {
    implementation(projects.common.commonDi)
    implementation(projects.common.commonDomain)
    implementation(libs.ksp)
    implementation(libs.dagger)
    implementation(libs.moshiKotlin)
    implementation(libs.moshiAdapters)
    kapt(libs.daggerCompiler)
    implementation(libs.stateUtils)
    implementation(libs.room)
    implementation(libs.roomKtx)
    ksp(libs.room.compiler)
}
