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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.appcompat)
    implementation(libs.cicerone)
    implementation(libs.composeUI)
    implementation(libs.composeMaterial)
    implementation(libs.composeUITooling)
    implementation(libs.composeFoundation)
    implementation(libs.composeConstraint)
    implementation(libs.dagger)
    implementation(libs.timber)
    kapt(libs.daggerCompiler)

    implementation(projects.common.commonDi)
    implementation(projects.common.commonDomain)
    implementation(projects.common.commonUtils)
    implementation(projects.core)
    implementation(libs.timber)
    implementation(projects.navigation)
    implementation(projects.uicomponents)

    implementation(libs.immutableList)
    implementation(libs.fragmentKtx)
    implementation(libs.material)
    implementation(libs.stateUtils)
    implementation(libs.viewModelScope)
    implementation(libs.voyagerNavigator)
    implementation(libs.voyagerViewModel)
    implementation(libs.voyagerTabNavigator)
    implementation(libs.voyagerTransitions)

    // DI
    implementation("io.insert-koin:koin-android:3.2.0")

    // Others
    implementation("com.github.adrielcafe.satchel:satchel-core:1.0.3")
    implementation("com.markodevcic:peko:2.1.3")
}
