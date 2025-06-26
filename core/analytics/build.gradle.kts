plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // No Compose needed for a typical analytics abstraction layer
    alias(libs.plugins.hilt) // For injecting the service
    alias(libs.plugins.ksp)   // For Hilt
}

android {
    namespace = "com.example.rooster.core.analytics"
    compileSdk = 35 // Match app module

    defaultConfig {
        minSdk = 24 // Match app module
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    // No buildFeatures { compose = true } needed
}

dependencies {
    implementation(project(":core:core-common")) // For context or common utilities if needed

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Firebase Analytics (the actual implementation provider)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx) // Use the KTX version


    // Testing
    testImplementation(libs.junit)
    // Add other testing libraries as needed, e.g., MockK
}
