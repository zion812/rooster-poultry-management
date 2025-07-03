plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.rooster.core.database"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // Room schema export
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
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
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/NOTICE.txt"
        }
    }
}

dependencies {
    implementation(project(":core:core-common"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Coroutines
    implementation(libs.bundles.coroutines)

    // WorkManager for background sync
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // DataStore for preferences
    implementation(libs.datastore.preferences)

    // JSON serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson)

    // Network connectivity
    implementation("androidx.lifecycle:lifecycle-process:${libs.versions.lifecycle.viewmodel.get()}")

    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation("androidx.room:room-testing:${libs.versions.room.get()}")
    androidTestImplementation(libs.bundles.android.testing)
    androidTestImplementation("androidx.work:work-testing:${libs.versions.work.get()}")
    
    // Debug
    debugImplementation(libs.androidx.ui.tooling)
}