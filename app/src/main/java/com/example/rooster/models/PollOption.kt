package com.example.rooster.models

/**
 * Represents a poll option for a community poll.
 */
data class PollOption(
    val id: String,
    val text: String,
    val voteCount: Int = 0,
)

/**
 * Represents a user response to a poll.
 */
data class PollResponse(
    val pollId: String,
    val optionId: String,
    val userId: String = "currentUser", // stub current user
    val timestamp: Long = System.currentTimeMillis(),
)
