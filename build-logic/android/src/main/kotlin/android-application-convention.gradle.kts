import com.android.build.gradle.BaseExtension
import com.google.firebase.appdistribution.gradle.AppDistributionExtension

plugins {
    id("com.android.application")
    id("base-android-convention")
    id("kotlin-android")
    id("base-kotlin-convention")
    kotlin("kapt")
    id("com.google.firebase.appdistribution")
    id("dependency-locking-convention")
}
configure<BaseExtension> {
    buildTypes {
        getByName("release") {
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )

            applicationIdSuffix = ".dev"
        }
    }
}

/*You need set your APP_ID from google-services.json*/
configure<AppDistributionExtension> {
    serviceCredentialsFile = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
    groups = "QA"
    appId = "1:719859225345:android:d52478d38961df46a30100"
}
