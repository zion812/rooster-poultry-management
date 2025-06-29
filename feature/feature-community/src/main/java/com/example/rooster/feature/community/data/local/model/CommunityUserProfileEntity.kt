package com.example.rooster.feature.community.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rooster.feature.community.data.local.CommunityTypeConverters

@Entity(tableName = "community_user_profiles")
@TypeConverters(CommunityTypeConverters::class)
data class CommunityUserProfileEntity(
    @PrimaryKey val userId: String,
    val displayName: String,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val location: String? = null,
    val farmName: String? = null,
    val interests: List<String>? = null, // Handled by TypeConverter
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val postCount: Int = 0,
    val lastActiveTimestamp: Long? = null,
    val joinDateTimestamp: Long,
    val isVerifiedFarmer: Boolean = false,
    val isEnthusiast: Boolean = false,
    var needsSync: Boolean = true, // For offline created/updated profiles
    var syncAttempts: Int = 0,
    var lastSyncAttemptTimestamp: Long = 0L
)
