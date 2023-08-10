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
    implementation(projects.common.commonUtils)
    implementation(projects.core)
    implementation(projects.navigation)
    implementation(projects.uicomponents)
    implementation(projects.features.player.featurePlayerDomain)
    implementation(projects.common.commonDomain)

    implementation(libs.appcompat)
    implementation(libs.cicerone)
    implementation(libs.composeUI)
    implementation(libs.composeMaterial)
    implementation(libs.composeUITooling)
    implementation(libs.composeFoundation)
    implementation(libs.dagger)
    implementation(libs.immutableList)
    kapt(libs.daggerCompiler)

    implementation(libs.fragmentKtx)
    implementation(libs.material)
    implementation(libs.stateUtils)
    implementation(libs.timber)
    implementation(libs.viewModelScope)
    implementation(libs.voyagerNavigator)
    implementation(libs.voyagerViewModel)
    implementation(libs.voyagerTabNavigator)
    implementation(libs.voyagerTransitions)
    implementation(libs.coilCompose)
    implementation(libs.composeConstraint)
}
