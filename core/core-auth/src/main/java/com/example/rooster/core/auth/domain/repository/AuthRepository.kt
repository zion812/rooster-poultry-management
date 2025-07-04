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
 feat/login-screen-v1
    // suspend fun verifyEmail(token: String): Result<Unit> // From pending list, for link-based verification if different from Firebase's default

    // New methods for email verification flow
    suspend fun sendCurrentUserEmailVerification(): Result<Unit>
    suspend fun reloadCurrentUser(): Result<User?> // Returns updated User or null if no user/error, or specific error type

    // suspend fun verifyEmail(token: String): Result<Unit> // From pending list, can add later if part of this task scope
 main
}
// Notes:
// - Modified `signUp` signature to include `role: UserRole` and `phoneNumber: String?`.
// - `resetPassword` and `updateProfile` were already present and match the pending list.
 feat/login-screen-v1
// - Added `sendCurrentUserEmailVerification` for triggering Firebase's email verification.
// - Added `reloadCurrentUser` to refresh Firebase user state (especially isEmailVerified).
// - The `verifyEmail(token: String)` from the original pending list might be for a custom token-based
//   verification system. Firebase's typical flow doesn't use a token passed back to the app this way;
//   the user clicks a link, and the app reloads the user or checks on next login.
//   For now, focusing on the standard Firebase email verification flow.

// - `verifyEmail` is mentioned in the pending list for AuthRepository. If it's critical for this
//   phase, it can be added now. For now, focusing on updating signUp for registration.
 main
