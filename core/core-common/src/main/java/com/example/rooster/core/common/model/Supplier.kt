package com.example.rooster.core.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey val id: String,
    val name: String,
    val location: String?,
    @ColumnInfo(name = "join_date") val joinDate: Long?,
    @ColumnInfo(name = "overall_rating") val overallRating: Double?,
    @ColumnInfo(name = "total_reviews") val totalReviews: Int = 0,
    val bio: String?,
    @ColumnInfo(name = "profile_image_url") val profileImageUrl: String?,
    @ColumnInfo(name = "contact_email") val contactEmail: String?,
    @ColumnInfo(name = "contact_phone") val contactPhone: String?,
    @ColumnInfo(name = "is_verified") val isVerified: Boolean = false,
    // Link to products or fetch separately? For now, profile is separate.
    // List of product IDs could be an option: val productIds: List<String> = emptyList()
    @ColumnInfo(name = "needs_sync") val needsSync: Boolean = false,
    @ColumnInfo(name = "sync_attempts") val syncAttempts: Int = 0,
    @ColumnInfo(name = "last_sync_attempt_timestamp") val lastSyncAttemptTimestamp: Long = 0L
)
