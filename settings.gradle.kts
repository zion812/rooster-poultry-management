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

// Core modules - Enhanced architecture
include(":core:core-common")
include(":core:core-network")
include(":core:core-auth")
include(":core:core-database")
include(":core:core-payment")
include(":core:core-security")
include(":core:core-offline")
include(":core:navigation")
include(":core:search")
include(":core:analytics")

// Feature modules - Comprehensive system
include(":feature:feature-marketplace")
include(":feature:feature-auctions")
include(":feature:feature-farm")
include(":feature:feature-community")