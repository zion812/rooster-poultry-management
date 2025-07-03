package com.example.rooster.core.auth.domain.repository

import com.example.rooster.core.auth.domain.model.User
import com.example.rooster.core.auth.domain.model.UserRole // Import UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    // Updated signUp to include role and optional phoneNumber
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        role: UserRole,
        phoneNumber: String? // Nullable for optional phone number
    ): Result<User>
    suspend fun signOut()
    suspend fun resetPassword(email: String): Result<Unit> // This was in the pending list
    fun getCurrentUser(): Flow<User?>
    suspend fun updateProfile(user: User): Result<User> // This was in the pending list
    suspend fun isUserSignedIn(): Boolean
    // suspend fun verifyEmail(token: String): Result<Unit> // From pending list, can add later if part of this task scope
}
// Notes:
// - Modified `signUp` signature to include `role: UserRole` and `phoneNumber: String?`.
// - `resetPassword` and `updateProfile` were already present and match the pending list.
// - `verifyEmail` is mentioned in the pending list for AuthRepository. If it's critical for this
//   phase, it can be added now. For now, focusing on updating signUp for registration.