plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.rooster.feature.community"
    compileSdk = 35 // Match app module

    defaultConfig {
        minSdk = 24 // Match app module
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Or true, based on project policy
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
        freeCompilerArgs += listOf("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
    }
    buildFeatures {
        compose = true // If UI components will be part of this module
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
    implementation(project(":core:core-network"))
    // implementation(project(":core:navigation")) // If needed for this feature's internal navigation

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler) // Ensure this is the KSP variant if project uses KSP for Hilt
    // implementation(libs.hilt.navigation.compose) // If using Hilt for Compose navigation

    // Room Database
    implementation(libs.room.runtime)
    ksp(libs.room.compiler) // or libs.room.compiler_ksp
    implementation(libs.room.ktx)

    // WorkManager (if this feature needs its own background sync)
    // implementation(libs.androidx.work.runtime.ktx)
    // implementation(libs.androidx.hilt.work)
    // ksp(libs.androidx.hilt.compiler)


    // Compose (if UI is in this module)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // implementation(libs.androidx.compose.material.icons)
    // implementation("androidx.compose.material:material-icons-extended")

    // Lifecycle for ViewModels (if ViewModels are in this module)
    // implementation(libs.androidx.lifecycle.viewmodel.compose)
    // implementation(libs.androidx.lifecycle.runtime.compose)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    // androidTestImplementation("androidx.test.ext:junit:1.1.5")
    // debugImplementation(libs.androidx.ui.tooling)
}
