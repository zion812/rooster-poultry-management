package com.example.rooster.feature.community.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rooster.feature.community.data.local.CommunityTypeConverters

@Entity(
    tableName = "community_comments",
    foreignKeys = [
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["postId"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE // If post is deleted, comments are deleted
        ),
        ForeignKey(
            entity = CommunityUserProfileEntity::class,
            parentColumns = ["userId"],
            childColumns = ["authorUserId"],
            onDelete = ForeignKey.SET_NULL // If user is deleted, comment author becomes null or anonymous
        ),
        ForeignKey(
            entity = CommentEntity::class, // Self-referencing for parent comment
            parentColumns = ["commentId"],
            childColumns = ["parentCommentId"],
            onDelete = ForeignKey.CASCADE // If parent comment is deleted, replies are deleted
        )
    ],
    indices = [
        Index(value = ["postId"]),
        Index(value = ["authorUserId"]),
        Index(value = ["parentCommentId"]),
        Index(value = ["createdTimestamp"])
    ]
)
@TypeConverters(CommunityTypeConverters::class)
data class CommentEntity(
    @PrimaryKey val commentId: String,
    val postId: String,
    val authorUserId: String?, // Nullable if author is deleted
    val authorDisplayName: String, // Denormalized, won't become null if user is deleted
    val authorProfilePictureUrl: String? = null, // Denormalized
    val contentText: String,
    val createdTimestamp: Long,
    var updatedTimestamp: Long?,
    val likeCount: Int = 0,
    val parentCommentId: String? = null,
    val replyCount: Int = 0,
    val isEdited: Boolean = false,
    val mentionsUserIds: List<String>? = null, // Handled by TypeConverter
    var needsSync: Boolean = true // For offline created/updated comments
)
