import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
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
val hasKeystore =
    keystorePropertiesFile.exists() &&
            keystoreProperties.getProperty("storeFile")?.isNotBlank() == true

if (!hasKeystore) {
    logger.info("🔐 Release keystore not configured. See keystore.properties.template for setup instructions.")
    logger.info("📋 Debug builds will continue to use debug signing for development.")
}

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
        versionCode = 3
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Room schema export
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    signingConfigs {
        create("release") {
            // Only configure release signing if keystore properties exist
            val storeFilePath = keystoreProperties.getProperty("storeFile")
            if (!storeFilePath.isNullOrBlank() && keystorePropertiesFile.exists()) {
                storeFile = file(storeFilePath)
                storePassword =
                    keystoreProperties.getProperty("storePassword") ?: ""
                keyAlias =
                    keystoreProperties.getProperty("keyAlias") ?: ""
                keyPassword =
                    keystoreProperties.getProperty("keyPassword") ?: ""
            }
        }
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "RAZORPAY_KEY", "\"rzp_test_dummy\"")
            buildConfigField("String", "PAYMENT_API_BASE_URL", "\"http://10.0.2.2:3000/debug/\"")
            buildConfigField("String", "BACKEND_BASE_URL", "\"http://10.0.2.2:3000/api/\"")
            buildConfigField("String", "FARM_MGMT_API_BASE_URL", "\"http://10.0.2.2:5000/\"") // For local Flask API via emulator
            buildConfigField(
                "String",
                "MAPS_API_KEY",
                "\"${project.findProperty("MAPS_API_KEY") ?: "dummy_key"}\""
            )
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
            buildConfigField(
                "String",
                "PAYMENT_API_BASE_URL",
                "\"https://api.roosterapp.com/payment/release/\"",
            )
            buildConfigField("String", "BACKEND_BASE_URL", "\"https://api.roosterapp.com/api/\"")
            // TODO: Define a proper production URL for Farm Mgmt API when available
            buildConfigField("String", "FARM_MGMT_API_BASE_URL", "\"http://10.0.2.2:5000/\"") // Placeholder, same as debug for now
            buildConfigField(
                "String",
                "MAPS_API_KEY",
                "\"${project.findProperty("MAPS_API_KEY") ?: "dummy_key"}\""
            )
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
            buildConfigField(
                "String",
                "PAYMENT_API_BASE_URL",
                "\"https://staging.api.roosterapp.com/payment/\"",
            )
            buildConfigField(
                "String",
                "BACKEND_BASE_URL",
                "\"https://staging.api.roosterapp.com/api/\"",
            )
            // TODO: Define a proper staging URL for Farm Mgmt API when available
            buildConfigField("String", "FARM_MGMT_API_BASE_URL", "\"http://10.0.2.2:5000/\"") // Placeholder, same as debug for now
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
        freeCompilerArgs += listOf(
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
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/NOTICE.txt"
        }
    }

    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = false
    }
}

dependencies {
    // Core modules - Enhanced architecture
    implementation(project(":core:core-common"))
    implementation(project(":core:navigation"))

    // Feature modules - Essential for basic functionality
    implementation(project(":feature:feature-auth")) // Re-enabled with fixes
    implementation(project(":feature:feature-home"))
    implementation(project(":feature:feature-splash"))
    implementation(project(":feature:feature-profile"))

    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    implementation("com.google.firebase:firebase-storage-ktx")

    // Parse SDK
    implementation(libs.parse)
    implementation("com.github.parse-community.Parse-SDK-Android:fcm:4.3.0")

    // Room Database
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

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
    implementation(libs.razorpay) {
        exclude(group = "com.razorpay", module = "standard-core")
    }

    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Material Components
    implementation("com.google.android.material:material:1.11.0")

    // Color Picker
    implementation("com.github.skydoves:colorpickerview:2.2.4")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Logging
    implementation(libs.timber.logger)

    // Performance monitoring
    implementation("androidx.tracing:tracing:1.2.0")

    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

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

    // LeakCanary for memory leak detection - Critical for Phase 1 fixes
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
}
