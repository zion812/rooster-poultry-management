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
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "rooster-app"

// Main application module
include(":app")

// Core modules - shared functionality
include(":core:core-common")
include(":core:core-network")
include(":core:core-database")
include(":core:core-datastore")
include(":core:core-testing")
include(":core:core-ui")

// Feature modules - independent features
include(":feature:feature-auth")
include(":feature:feature-marketplace")
include(":feature:feature-social")
include(":feature:feature-farm")
include(":feature:feature-health")
include(":feature:feature-analytics")

// Shared modules - business logic
include(":shared:shared-domain")
include(":shared:shared-data")
include(":shared:shared-test")
