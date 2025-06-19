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

// Core modules - stable
include(":core:core-common")
include(":core:core-network")

// Temporarily disable modules with dependency issues
// include(":core:navigation")
// include(":core:search")

// Feature modules - temporarily disabled until core is stable
// include(":feature:feature-marketplace")
// include(":feature:feature-auctions")
