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
// include(":core:core-network") // Temporarily disabled
// include(":core:core-auth") // Temporarily disabled
// include(":core:core-database") // Temporarily disabled
// include(":core:core-payment") // Temporarily disabled
// include(":core:core-security") // Not found
// include(":core:core-offline") // Not found
include(":core:navigation")
// include(":core:search") // Temporarily disabled
// include(":core:analytics") // Temporarily disabled

// Feature modules - Comprehensive system
// include(":feature:feature-marketplace") // Temporarily disabled
// include(":feature:feature-auctions") // Temporarily disabled
// include(":feature:feature-farm") // Temporarily disabled
// include(":feature:feature-community") // Temporarily disabled