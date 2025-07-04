package com.example.rooster.core.auth.domain.repository

import com.example.rooster.core.auth.domain.model.User
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.common.Result // Ensure this is the correct Result type
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>

    suspend fun signUp(
        email: String,
        password: String,
        displayName: String, // Changed from 'name' to 'displayName' to match User model
        role: UserRole,
        phoneNumber: String?
    ): Result<User>

    suspend fun signOut() // Should this return Result<Unit> for consistency? For now, keeping as is.

    suspend fun resetPassword(email: String): Result<Unit>

    fun getCurrentUser(): Flow<User?> // This Flow should emit the consolidated User model

    suspend fun updateProfile(user: User): Result<User> // User is the consolidated model

    suspend fun isUserSignedIn(): Boolean

    suspend fun sendCurrentUserEmailVerification(): Result<Unit>

    suspend fun reloadCurrentUser(): Result<User?> // Returns updated consolidated User or null
}
