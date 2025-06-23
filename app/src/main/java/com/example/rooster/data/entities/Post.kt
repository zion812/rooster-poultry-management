package com.example.rooster.data.entities

/**
 * Post entity
 */
data class Post(
    val id: String = "post_${System.currentTimeMillis()}",
    val userId: String,
    val username: String,
    val profilePicture: String? = null,
    val content: String,
    val mediaUrls: List<String> = emptyList(),
    val type: PostType = PostType.TEXT,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isLikedByCurrentUser: Boolean = false
)

/**
 * Post type enum
 */
enum class PostType {
    TEXT,
    IMAGE,
    VIDEO,
    POLL,
    EVENT
}

/**
 * Comment entity
 */
data class Comment(
    val id: String = "comment_${System.currentTimeMillis()}",
    val postId: String,
    val userId: String,
    val username: String,
    val profilePicture: String? = null,
    val content: String,
    val likesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isLikedByCurrentUser: Boolean = false
)

/**
 * Post interaction entity
 */
data class PostInteraction(
    val id: String = "interaction_${System.currentTimeMillis()}",
    val postId: String,
    val userId: String,
    val type: InteractionType,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Interaction type enum
 */
enum class InteractionType {
    LIKE,
    SHARE,
    SAVE,
    REPORT
}