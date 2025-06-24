package com.example.rooster.models

/**
 * Represents a user post with rich media.
 */
data class Post(
    val id: String,
    val authorId: String,
    val text: String,
    val mediaUrls: List<String> = emptyList(),
    val mediaTypes: List<MediaType> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
)

/**
 * Types of media in a post.
 */
enum class MediaType {
    IMAGE,
    VIDEO,
    AUDIO,
}
