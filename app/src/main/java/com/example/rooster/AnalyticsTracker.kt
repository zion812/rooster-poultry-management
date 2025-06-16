package com.example.rooster

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object AnalyticsTracker {
    private val analytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }

    fun trackEvent(
        eventName: String,
        dimensions: Map<String, String>,
    ) {
        try {
            val bundle =
                Bundle().apply {
                    dimensions.forEach { (key, value) ->
                        putString(key, value)
                    }
                }
            analytics.logEvent(eventName, bundle)
            Log.d("AnalyticsTracker", "Tracked event: $eventName")
        } catch (e: Exception) {
            Log.e("AnalyticsTracker", "Failed to track event: $eventName", e)
        }
    }

    fun trackLogin(userRole: String) {
        trackEvent("user_login", mapOf("user_role" to userRole))
    }

    fun trackPostCreated(userRole: String) {
        trackEvent("post_created", mapOf("user_role" to userRole))
    }

    fun trackFowlAdded(userRole: String) {
        trackEvent("fowl_added", mapOf("user_role" to userRole))
    }

    fun trackListingCreated(userRole: String) {
        trackEvent("listing_created", mapOf("user_role" to userRole))
    }

    fun trackTransferVerified(userRole: String) {
        trackEvent("transfer_verified", mapOf("user_role" to userRole))
    }
}
