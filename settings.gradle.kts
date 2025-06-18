// Enable version catalog type-safe accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "rooster-poultry-management"
include(":app")

// Temporarily disable modules causing build issues
// include(":core:core-common")
// include(":core:core-network")
// include(":core:analytics")
// include(":core:ai")
// include(":core:search")
// include(":feature:feature-farm")
// include(":feature:feature-analytics")
// include(":feature:ai")

