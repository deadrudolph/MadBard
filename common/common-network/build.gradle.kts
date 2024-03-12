plugins {
    id("android-library-convention")
}

dependencies {
    implementation(projects.common.commonDi)
    implementation(projects.common.commonDomain)

    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
    implementation(libs.httpLoggingInterceptor)
    implementation(libs.kotlinStdlib)
    implementation(libs.moshiKotlin)
    implementation(libs.moshiAdapters)
    implementation(libs.retrofit)
    implementation(libs.retrofitMoshiConverter)

    debugImplementation(libs.chuck)

    releaseImplementation(libs.chuckNoOp)
}
