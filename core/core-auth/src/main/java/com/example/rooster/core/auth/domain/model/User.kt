package com.example.rooster.core.auth.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val phoneNumber: String? = null,
    val role: UserRole = UserRole.FARMER,
    val profileImageUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class UserRole {
    FARMER,
    BUYER,
    ADMIN,
    VETERINARIAN
}