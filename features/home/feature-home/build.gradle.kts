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
    implementation(projects.common.commonDi)
    implementation(projects.core)
    implementation(projects.navigation)
    implementation(projects.uicomponents)
    implementation(projects.features.home.featureHomeDomain)

    implementation(libs.appcompat)
    implementation(libs.cicerone)
    implementation(libs.composeUI)
    implementation(libs.composeMaterial)
    implementation(libs.composeUITooling)
    implementation(libs.composeFoundation)
    implementation(libs.dagger)
    kapt(libs.daggerCompiler)

    implementation(libs.fragmentKtx)
    implementation(libs.material)
    implementation(libs.stateUtils)
    implementation(libs.viewModelScope)
    implementation(libs.voyagerNavigator)
    implementation(libs.voyagerViewModel)
    implementation(libs.voyagerTabNavigator)
    implementation(libs.voyagerTransitions)
}