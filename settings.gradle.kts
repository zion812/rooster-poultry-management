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
include(":core:navigation")

// Feature modules - Essential for basic functionality
include(":feature:feature-auth") // Re-enabled with proper build configuration
include(":feature:feature-home")
include(":feature:feature-splash")
include(":feature:feature-profile")