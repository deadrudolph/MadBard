plugins {
    id("android-library-convention")
    id("kotlin-kapt")
}

dependencies {
    implementation(projects.common.commonDi)
    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
}
