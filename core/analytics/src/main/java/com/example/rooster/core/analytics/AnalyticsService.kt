package com.example.rooster.core.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// Interface for our Analytics Service
interface AnalyticsService {
    fun logEvent(eventName: String, params: Bundle = Bundle())
    fun setUserProperty(propertyName: String, value: String?)
    // Add more specific methods as needed, e.g., trackScreenView, trackUserLogin, etc.
}

@Singleton
class FirebaseAnalyticsService @Inject constructor(
    @ApplicationContext private val context: Context
) : AnalyticsService {

    private val firebaseAnalytics: FirebaseAnalytics by lazy { Firebase.analytics }

    override fun logEvent(eventName: String, params: Bundle) {
        // Basic validation or transformation of eventName if needed
        // e.g., ensuring it adheres to Firebase naming conventions (alphanumeric & underscores)
        val safeEventName = eventName
            .replace(Regex("[^a-zA-Z0-9_]"), "")
            .take(40) // Max length for event names

        firebaseAnalytics.logEvent(safeEventName, params)
    }

    override fun setUserProperty(propertyName: String, value: String?) {
        // Basic validation or transformation of propertyName if needed
        val safePropertyName = propertyName
            .replace(Regex("[^a-zA-Z0-9_]"), "")
            .take(24) // Max length for user property names (Firebase specific)

        firebaseAnalytics.setUserProperty(safePropertyName, value)
    }

    // Example of a more specific event
    fun trackScreenView(screenName: String, screenClass: String? = null) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            screenClass?.let { putString(FirebaseAnalytics.Param.SCREEN_CLASS, it) }
        }
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

    fun trackSearchPerformed(term: String, resultCount: Int, category: String?, region: String?) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, term)
            putLong("search_result_count", resultCount.toLong()) // Custom param
            category?.let { putString("search_category", it) }
            region?.let { putString("search_region", it) }
        }
        logEvent("search_performed_custom", params) // Use a custom event name
    }
}

// Hilt Module to provide AnalyticsService
// This would typically be in a di package within this module
// e.g., core/analytics/src/main/java/com/example/rooster/core/analytics/di/AnalyticsModule.kt
/*
package com.example.rooster.core.analytics.di

import com.example.rooster.core.analytics.AnalyticsService
import com.example.rooster.core.analytics.FirebaseAnalyticsService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsService(
        firebaseAnalyticsService: FirebaseAnalyticsService
    ): AnalyticsService
}
*/
