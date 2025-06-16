package com.example.rooster.models

/**
 * Represents a community event or election in Parse backend.
 */
data class ParseEvent(
    val id: String,
    val title: String,
    val description: String,
    val date: Long,
    val type: String,
    val participants: List<String> = emptyList(),
    val isJoined: Boolean = false,
)
