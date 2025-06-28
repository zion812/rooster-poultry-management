package com.example.rooster.feature.community.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CommunityUserProfile(
    val userId: String, // Foreign key to the main user account
    val displayName: String, // User-chosen display name for community interactions
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val location: String? = null, // e.g., "Krishna District, Andhra Pradesh"
    val farmName: String? = null, // Optional, if the user is a farmer
    val interests: List<String>? = null, // e.g., ["Aseel Roosters", "Organic Farming", "Sankranti Festivals"]
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val postCount: Int = 0,
    val lastActiveTimestamp: Long? = null,
    val joinDateTimestamp: Long,
    val isVerifiedFarmer: Boolean = false, // Badge indicating verified farmer status
    val isEnthusiast: Boolean = false // Badge for rooster enthusiasts
    // Add other fields like links to social media, website, etc.
)
