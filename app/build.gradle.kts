plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

// ktlint {
//     android.set(true)
//     ignoreFailures.set(true)
// }

android {
    namespace = "com.example.rooster"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rooster"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "RAZORPAY_KEY", "\"rzp_test_dummy\"")
            versionNameSuffix = "-debug"
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("release") {
            buildConfigField("String", "RAZORPAY_KEY", "\"rzp_live_dummy\"")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Firebase Crashlytics configuration
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
        create("staging") {
            initWith(getByName("release"))
            buildConfigField("String", "RAZORPAY_KEY", "\"rzp_test_dummy\"")
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
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
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
    // Core modules - all working modules
    implementation(project(":core:core-common"))
    implementation(project(":core:core-network"))

    // Feature modules - temporarily disabled to get working build
    // implementation(project(":feature:feature-marketplace"))
    // implementation(project(":feature:feature-auctions"))
    // implementation(project(":feature:feature-farm"))

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
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.firebase.crashlytics)
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-messaging")

    // Parse SDK
    implementation("com.github.parse-community.Parse-SDK-Android:parse:4.3.0")
    implementation("com.github.parse-community.Parse-SDK-Android:fcm:4.3.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.5.2")
    ksp("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Charts for analytics dashboard
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // Razorpay Payment Gateway
    implementation("com.razorpay:checkout:1.6.38")

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
