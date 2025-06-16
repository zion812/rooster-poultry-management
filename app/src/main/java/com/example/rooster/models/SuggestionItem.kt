package com.example.rooster.models

import java.util.*

/**
 * Represents an action suggestion for a specific bird.
 */
data class SuggestionItem(
    val id: String = UUID.randomUUID().toString(),
    val birdId: String,
    val message: String,
    val date: Long = System.currentTimeMillis(),
)
