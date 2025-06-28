package com.example.rooster.core.auth.model

import android.net.Uri // For local URI before upload, or String for remote URL. Using String for remote.

/**
 * Represents richer user profile data, typically stored in a User table or a linked Profile table.
 * This aligns with fields previously in CommunityUserProfile and general user needs.
 */
data class UserProfileData(
    val userId: String, // Should match AuthUser.uid
    val email: String?, // Often stored here too for display/contact
    var displayName: String?, // User-chosen display name
    var profilePictureUrl: String? = null,
    var bio: String? = null,
    var location: String? = null, // e.g., "Krishna District, Andhra Pradesh"
    var farmName: String? = null, // Optional, if the user is a farmer
    var interests: List<String>? = null,
    var phoneNumber: String? = null, // Added from previous considerations

    // Community specific denormalized counts (could be updated by functions or separate service)
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val postCount: Int = 0,

    val lastActiveTimestamp: Long? = null,
    val joinDateTimestamp: Long,

    // Flags / Badges
    val isVerifiedFarmer: Boolean = false,
    val isEnthusiast: Boolean = false,
    val isKycVerified: Boolean = false, // For marketplace seller verification

    // Other potential fields
    // val dateOfBirth: Long? = null,
    // val gender: String? = null,
    // val preferredLanguage: String? = null,
    val additionalProperties: Map<String, String>? = null // For future expansion
)
