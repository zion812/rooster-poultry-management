package com.example.rooster.feature.community.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val commentId: String,
    val postId: String, // FK to Post.postId
    val authorUserId: String, // FK to CommunityUserProfile.userId
    val authorDisplayName: String, // Denormalized
    val authorProfilePictureUrl: String? = null, // Denormalized
    val contentText: String,
    val createdTimestamp: Long,
    var updatedTimestamp: Long?,
    val likeCount: Int = 0,
    // val reactions: Map<ReactionType, Int>? = null, // If supporting multiple reaction types
    val parentCommentId: String? = null, // For threaded replies/nested comments
    val replyCount: Int = 0, // Number of direct replies to this comment
    val isEdited: Boolean = false,
    val mentionsUserIds: List<String>? = null
)
