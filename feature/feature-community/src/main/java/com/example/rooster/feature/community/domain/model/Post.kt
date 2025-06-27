package com.example.rooster.feature.community.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val postId: String,
    val authorUserId: String, // FK to CommunityUserProfile.userId
    val authorDisplayName: String, // Denormalized for easy display
    val authorProfilePictureUrl: String? = null, // Denormalized
    val contentText: String?,
    val imageUrls: List<String>? = null, // For multiple images
    val videoUrl: String? = null,
    val createdTimestamp: Long,
    var updatedTimestamp: Long?,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0, // If sharing is a feature
    val tags: List<String>? = null, // e.g., #SankrantiRooster, #FarmLife
    val location: String? = null, // Optional location tag for the post
    // val visibility: PostVisibility = PostVisibility.PUBLIC, // Enum for public, followers_only, etc.
    val mentionsUserIds: List<String>? = null, // List of User IDs mentioned in the post
    // Store aggregated reactions if more complex than just likes
    // val reactions: Map<ReactionType, Int>? = null,
    val isEdited: Boolean = false,
    val relatedFlockId: String? = null // Optional: Link to a specific flock/rooster from feature-farm
)

/*
@Serializable
enum class PostVisibility {
    PUBLIC,
    FOLLOWERS_ONLY,
    // MUTUAL_CONNECTIONS, // If a friendship model exists
    // PRIVATE_GROUP_ONLY // If posts can be made within specific groups
}
*/
