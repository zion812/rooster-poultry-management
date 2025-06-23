package com.example.rooster.data.entities

/**
 * User data entity
 */
data class User(
    val userId: String,
    val username: String,
    val email: String,
    val displayName: String? = null,
    val profileImageUrl: String? = null,
    val profilePicture: String? = null,
    val role: String = "farmer",
    val phoneNumber: String? = null,
    val location: String? = null,
    val isActive: Boolean = true,
    val coins: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)
