package com.example.rooster.core.auth.model

/**
 * Represents basic authenticated user information.
 */
data class AuthUser(
    val uid: String,
    val email: String?,
    val isEmailVerified: Boolean = false,
    val displayName: String? = null, // Often available directly from auth provider
    // Add other common auth fields if necessary (e.g., photoUrl from provider)
)
