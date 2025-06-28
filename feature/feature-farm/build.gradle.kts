plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.rooster.feature.farm"
    compileSdk = 35 // Match app module

    defaultConfig {
        minSdk = 24 // Match app module
        // targetSdk = 35 // Not typically set in library modules, inherited from app

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Or true, depending on project policy for feature modules
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
        sourceCompatibility = JavaVersion.VERSION_11 // Match app module
        targetCompatibility = JavaVersion.VERSION_11 // Match app module
    }
    kotlinOptions {
        jvmTarget = "11" // Match app module
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4" // Match app module
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core:core-common"))
    implementation(project(":core:core-network")) // Assuming repositories might use this

    // Hilt for DI
    implementation(libs.hilt.android)
 jules/arch-assessment-1
    ksp(libs.hilt.compiler) // Make sure this is the KSP version if using KSP for Hilt
    implementation(libs.androidx.hilt.work) // Hilt WorkManager Integration
    ksp(libs.androidx.hilt.compiler) // Hilt WorkManager Integration KSP // or specific libs.androidx.hilt.work.compiler if defined
    implementation(libs.hilt.navigation.compose) // If feature has its own navigation graphs
=======
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)

    // Work Manager for background sync
    implementation(libs.work.runtime.ktx)
 main

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Room Database
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    // Firebase (if used directly, though often abstracted by core-network or repositories)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore) // For Firestore
    implementation(libs.firebase.database)  // For Realtime Database

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation("androidx.compose.material:material-icons-extended")


    // Lifecycle for ViewModels
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose) // For collectAsStateWithLifecycle

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Testing
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.20")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.7")

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
}
