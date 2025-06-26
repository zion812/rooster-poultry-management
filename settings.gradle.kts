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

// Enable navigation and search modules for production (temporarily disabled)
include(":core:navigation")
include(":core:search")
include(":core:analytics")

// Feature modules (commented out until implemented)
include(":feature:feature-marketplace")
include(":feature:feature-auctions")
include(":feature:feature-farm")
