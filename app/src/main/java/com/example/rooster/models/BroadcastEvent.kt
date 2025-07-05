package com.example.rooster.models

import java.util.*

/**
 * Represents a live broadcast event by a user.
 */
data class BroadcastEvent(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val type: String, // "video" or "audio"
    val startTime: Long = System.currentTimeMillis(),
    val isLive: Boolean = true,
)
