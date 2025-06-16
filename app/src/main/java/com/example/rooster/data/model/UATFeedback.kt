package com.example.rooster.data.model

import com.parse.ParseClassName
import com.parse.ParseObject

@ParseClassName("UATFeedback")
class UATFeedback : ParseObject() {
    var category: String?
        get() = getString("category")
        set(value) {
            put("category", value ?: "")
        }

    var priority: String?
        get() = getString("priority")
        set(value) {
            put("priority", value ?: "")
        }

    var message: String?
        get() = getString("message")
        set(value) {
            put("message", value ?: "")
        }

    var starRating: Int
        get() = getInt("starRating")
        set(value) {
            put("starRating", value)
        }

    var deviceInfo: String?
        get() = getString("deviceInfo")
        set(value) {
            put("deviceInfo", value ?: "")
        }

    var networkQuality: String?
        get() = getString("networkQuality")
        set(value) {
            put("networkQuality", value ?: "")
        }

    var photoUrls: List<String>?
        get() = getList("photoUrls")
        set(value) {
            put("photoUrls", value ?: emptyList<String>())
        }

    var userId: String? // Optional: To link feedback to a user
        get() = getString("userId")
        set(value) {
            if (value != null) put("userId", value) else remove("userId")
        }

    var appVersion: String?
        get() = getString("appVersion")
        set(value) {
            if (value != null) put("appVersion", value) else remove("appVersion")
        }
}
