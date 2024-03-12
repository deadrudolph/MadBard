plugins {
    id("android-application-convention")
}

android {
    lintOptions.disable += "Instantiatable"

    defaultConfig {
        applicationId = "com.deadrudolph.madbard"
    }

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
    implementation(projects.features.home.featureHome)
    implementation(projects.features.builder.featureBuilder)
    implementation(projects.features.player.featurePlayer)
    implementation(projects.features.tuner)

    implementation(libs.appcompat)
    implementation(libs.cicerone)
    implementation(libs.coreKtx)
    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
    implementation(libs.material)
    implementation(libs.timber)
    implementation(libs.composeActivity)
    implementation(libs.composeUI)
    implementation(libs.composeMaterial)
    implementation(libs.composeUITooling)
    implementation(libs.composeFoundation)
    implementation(libs.voyagerNavigator)
    implementation(libs.voyagerTabNavigator)
    implementation(libs.voyagerTransitions)

/*    debugImplementation(libs.hyperionAttr)
    debugImplementation(libs.hyperionBuildConfig)
    debugImplementation(libs.hyperionCore)
    debugImplementation(libs.hyperionCrash)
    debugImplementation(libs.hyperionDisk)
    debugImplementation(libs.hyperionMeasurement)
    debugImplementation(libs.hyperionPhoenix)
    debugImplementation(libs.hyperionRecorder)
    debugImplementation(libs.hyperionSharedPreferences)
    debugImplementation(libs.hyperionTimber)*/
}
