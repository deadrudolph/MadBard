plugins {
    id("android-library-convention")
}

android {
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}
dependencies {
    implementation(libs.composeActivity)
    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
    implementation(libs.kotlinStdlib)
    implementation(libs.viewModelScope)
    implementation(libs.voyagerNavigator)
    implementation(libs.voyagerViewModel)
}