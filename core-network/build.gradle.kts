import com.example.rooster.convention.configureKotlinAndroid
import com.example.rooster.convention.libs

plugins {
    alias(libs.plugins.rooster.android.library)
    alias(libs.plugins.rooster.android.hilt)
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
}

android {
    namespace = "com.example.rooster.core.network"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    configureKotlinAndroid(this)
}

dependencies {
    implementation(project(":core:core-common")) // For any common utilities or base DTOs

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // Retrofit & OkHttp
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp.logging.interceptor)

    // Hilt for Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler) // If using KAPT, or use KSP if project is configured for it

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}
