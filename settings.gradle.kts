enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "MadBard"

includeBuild("build-logic")
include(
    "app",
    "core",
    "navigation",
    "uicomponents",
    "common:common-network",
    "common:common-database",
    "common:common-di",
    ":common:common-domain",
    ":common:common-utils"
)
include(
    "features:home:feature-home-domain",
    "features:home:feature-home"
)
include(
    ":features:builder:feature-builder",
    ":features:builder:feature-builder-domain"
)

include(
    ":features:player:feature-player",
    ":features:player:feature-player-domain"
)

include(":features:tuner")

pluginManagement {

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
