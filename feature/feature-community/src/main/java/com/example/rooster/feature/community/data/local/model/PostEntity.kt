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
    // Denormalized
    val authorDisplayName: String,
    // Denormalized
    val authorProfilePictureUrl: String? = null,
    val contentText: String?,
    // Handled by TypeConverter
    val imageUrls: List<String>? = null,
    val videoUrl: String? = null,
    val createdTimestamp: Long,
    var updatedTimestamp: Long?,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    // Handled by TypeConverter
    val tags: List<String>? = null,
    val location: String? = null,
    // Handled by TypeConverter
    val mentionsUserIds: List<String>? = null,
    val isEdited: Boolean = false,
    val relatedFlockId: String? = null,
    // Handled by TypeConverter
    val likedBy: List<String> = emptyList(),
    // For offline created/updated posts
    var needsSync: Boolean = true,
    var syncAttempts: Int = 0,
    var lastSyncAttemptTimestamp: Long = 0L
)
