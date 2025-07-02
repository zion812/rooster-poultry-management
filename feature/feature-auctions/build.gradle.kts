plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // alias(libs.plugins.kotlin.serialization) // If needed for specific models in this feature
}

android {
    namespace = "com.example.rooster.feature.auctions"
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
        create("staging") {
            initWith(getByName("release"))
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
    implementation(project(":core:core-network")) // For ViewModels to fetch data
    implementation(project(":core:navigation"))   // For NavController and defining routes/graphs
    implementation(project(":core:analytics"))  // For logging analytics events

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose) // For Hilt ViewModels in navigated Composables
    implementation(libs.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Parse SDK - Required for auction functionality
    implementation(libs.parse)
    implementation("com.github.parse-community.Parse-SDK-Android:coroutines:4.2.0")

    // Firebase - Required for Crashlytics logging
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    // Payments
    implementation(libs.razorpay)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Lifecycle & ViewModels
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Logging
    implementation(libs.timber.logger)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    debugImplementation(libs.androidx.ui.tooling) // For @Preview
}
