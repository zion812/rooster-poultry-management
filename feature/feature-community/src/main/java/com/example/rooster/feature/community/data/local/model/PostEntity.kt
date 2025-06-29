package com.example.rooster.feature.community.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rooster.feature.community.data.local.CommunityTypeConverters

@Entity(
    tableName = "community_posts",
    foreignKeys = [
        ForeignKey(
            entity = CommunityUserProfileEntity::class,
            parentColumns = ["userId"],
            childColumns = ["authorUserId"],
            onDelete = ForeignKey.CASCADE // If user is deleted, their posts are deleted
        )
    ],
    indices = [Index(value = ["authorUserId"]), Index(value = ["createdTimestamp"])]
)
@TypeConverters(CommunityTypeConverters::class)
data class PostEntity(
    @PrimaryKey val postId: String,
    val authorUserId: String,
    val authorDisplayName: String, // Denormalized
    val authorProfilePictureUrl: String? = null, // Denormalized
    val contentText: String?,
    val imageUrls: List<String>? = null, // Handled by TypeConverter
    val videoUrl: String? = null,
    val createdTimestamp: Long,
    var updatedTimestamp: Long?,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val tags: List<String>? = null, // Handled by TypeConverter
    val location: String? = null,
    val mentionsUserIds: List<String>? = null, // Handled by TypeConverter
    val isEdited: Boolean = false,
    val relatedFlockId: String? = null,
    val likedBy: List<String> = emptyList(), // Handled by TypeConverter
 feature/phase1-foundations-community-likes
    var needsSync: Boolean = true, // For offline created/updated posts
    var syncAttempts: Int = 0,
    var lastSyncAttemptTimestamp: Long = 0L
=======
 feature/phase1-foundations-community-likes
    var needsSync: Boolean = true, // For offline created/updated posts
    var syncAttempts: Int = 0,
    var lastSyncAttemptTimestamp: Long = 0L
=======
    var needsSync: Boolean = true // For offline created/updated posts
 main
 main
)
