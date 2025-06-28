plugins {
    alias(libs.plugins.android.library) // or kotlin("jvm") if no Android specific deps
    alias(libs.plugins.kotlin.android)  // or kotlin("jvm")
    alias(libs.plugins.hilt) // For DI if providing implementations here, or just for interfaces
    alias(libs.plugins.ksp)  // For Hilt KSP
}

android { // Keep if using android.library, remove if pure Kotlin module
    namespace = "com.example.rooster.core.auth"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        // No testInstrumentationRunner for pure Kotlin usually, unless specific Android utils needed
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    api(project(":core:core-common")) // Expose UserDataSource interface, Result type

    // Hilt
    implementation(libs.hilt.android) // Or libs.hilt.core if pure Kotlin
    ksp(libs.hilt.compiler)

    // Coroutines (for suspend functions in interface)
    implementation(libs.bundles.coroutines) // Assuming this bundle has kotlinx-coroutines-core

    // Parse SDK (if interfaces are very Parse-specific, otherwise impl module handles this)
    // For now, keeping interfaces generic. Implementation module will depend on Parse SDK.
    // api(libs.parse) // This would make core-auth Parse-aware, maybe not ideal for pure interface module

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk) // For testing implementations if any are in this module
    testImplementation(libs.kotlinx.coroutines.test)
}
