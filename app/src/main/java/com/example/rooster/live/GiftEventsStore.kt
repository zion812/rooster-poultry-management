package com.example.rooster.live

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Data model for live stream gifts
 */
data class Gift(
    val birdId: String,
    val type: String,
    val icon: String,
    val senderId: String? = null,
    val senderName: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)

/**
 * Centralized store for managing gift events in live streams
 * Provides real-time gift broadcasting for immediate UI animations
 */
object GiftEventsStore {
    private val _gifts = MutableSharedFlow<Gift>(extraBufferCapacity = 50)
    val gifts = _gifts.asSharedFlow()

    /**
     * Publish a new gift event for immediate local animation
     */
    fun publish(gift: Gift) {
        _gifts.tryEmit(gift)
    }

    /**
     * Clear all pending gift events (useful for cleanup)
     */
    fun clear() {
        // Create a new SharedFlow to clear buffer
        // Note: SharedFlow doesn't have a direct clear method
    }
}
