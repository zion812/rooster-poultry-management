import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization) // Added Kotlinx Serialization plugin
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

// Load signing config from keystore.properties
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties =
    Properties().apply {
        if (keystorePropertiesFile.exists()) {
            load(keystorePropertiesFile.inputStream())
        }
    }

// Flag if release keystore is available
val hasKeystore = keystorePropertiesFile.exists()

// Ktlint enforcement
ktlint {
    android.set(true)
    ignoreFailures.set(false)
}

android {
    namespace = "com.example.rooster"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rooster"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            // Only configure release signing if keystore properties exist
            val storeFilePath = keystoreProperties.getProperty("storeFile")
            if (!storeFilePath.isNullOrBlank() && keystorePropertiesFile.exists()) {
                storeFile = file(storeFilePath)
                storePassword = keystoreProperties.getProperty("storePassword") ?: ""
                keyAlias = keystoreProperties.getProperty("keyAlias") ?: ""
                keyPassword = keystoreProperties.getProperty("keyPassword") ?: ""
            } else {
                println("INFO: No release keystore configured; release builds will not be signed here.")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "RAZORPAY_KEY", "\"rzp_test_dummy\"")
            buildConfigField("String", "PAYMENT_API_BASE_URL", "\"http://10.0.2.2:3000/debug/\"") // Placeholder
            versionNameSuffix = "-debug"
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        getByName("release") {
            buildConfigField("String", "RAZORPAY_KEY", "\"rzp_live_dummy\"")
            buildConfigField("String", "PAYMENT_API_BASE_URL", "\"https://api.roosterapp.com/payment/release/\"") // Placeholder
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            // Firebase Crashlytics configuration
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
            if (hasKeystore) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        create("staging") {
            initWith(getByName("release"))
            buildConfigField("String", "RAZORPAY_KEY", "\"rzp_test_dummy\"")
            buildConfigField("String", "PAYMENT_API_BASE_URL", "\"https://staging.api.roosterapp.com/payment/\"") // Placeholder
            // Remove staging suffix to prevent Google Services mismatch
            // applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs +=
            listOf(
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = false
    }
}

dependencies {
    // Core modules
    implementation(project(":core:core-common"))
    implementation(project(":core:core-network"))
    implementation(project(":core:navigation"))
    implementation(project(":core:search"))
    implementation(project(":core:analytics"))
    implementation(project(":feature:feature-farm"))
    implementation(project(":feature:feature-marketplace"))
    implementation(project(":feature:feature-auctions")) // Added auctions feature

    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation(libs.androidx.navigation.compose) // This is for app-level navigation if core:navigation doesn't replace all NavHost usage

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics) // This is the direct SDK, core:analytics will wrap it
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)

    // Parse SDK
    implementation(libs.parse)
    implementation("com.github.parse-community.Parse-SDK-Android:fcm:4.3.0")

    // Room Database
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    // WorkManager
    implementation(libs.work.runtime.ktx)

    // Permissions
    implementation(libs.accompanist.permissions)

    // Image loading
    implementation(libs.coil.compose)

    // Charts for analytics dashboard
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // JSON
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.kotlinx.serialization.json)

    // Razorpay Payment Gateway
    implementation("com.razorpay:checkout:1.6.38")

    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Material Components for layout_behavior, cardElevation, etc.
    implementation("com.google.android.material:material:1.11.0")

    // Skydoves ColorPickerView for advanced color picker widget support
    implementation("com.github.skydoves:colorpickerview:2.2.4")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Logging
    implementation(libs.timber.logger)

    // Testing
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.20")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")

    // Android Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.7")

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
