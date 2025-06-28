package com.example.rooster.core.auth.datasources

import com.example.rooster.core.common.Result
import com.example.rooster.core.auth.model.AuthUser // To be created: Represents basic user details for auth
import com.example.rooster.core.auth.model.UserProfileData // To be created: Represents richer profile data
import kotlinx.coroutines.flow.Flow

/**
 * Interface for authentication and user profile data operations.
 */
interface UserDataSource {

    // --- Authentication ---

    /**
     * Registers a new user.
     * @param email User's email.
     * @param password User's password.
     * @param initialProfileData Optional initial profile data to set upon registration.
     * @return Result indicating success (with AuthUser) or failure.
     */
    suspend fun signUp(email: String,password: String, initialProfileData: Map<String, Any?>? = null): Result<AuthUser>

    /**
     * Logs in an existing user.
     * @param email User's email.
     * @param password User's password.
     * @return Result indicating success (with AuthUser) or failure.
     */
    suspend fun logIn(email: String, password: String): Result<AuthUser>

    /**
     * Logs out the current user.
     */
    suspend fun logOut(): Result<Unit>

    /**
     * Gets the current authenticated user.
     * @return AuthUser if a user is authenticated, null otherwise.
     */
    suspend fun getCurrentUser(): AuthUser? // Changed from Flow to direct suspend for current state

    /**
     * A flow that emits the current authenticated user or null if logged out.
     */
    fun observeAuthState(): Flow<AuthUser?>

    /**
     * Sends a password reset email.
     * @param email The user's email address.
     * @return Result indicating success or failure.
     */
    suspend fun requestPasswordReset(email: String): Result<Unit>


    // --- User Profile (Core Fields on ParseUser or linked Profile Class) ---

    /**
     * Fetches the full profile data for a given user ID.
     * @param userId The ID of the user whose profile is to be fetched.
     * @return Result containing UserProfileData or an error.
     */
    suspend fun getUserProfile(userId: String): Result<UserProfileData>

    /**
      * A flow that emits profile data updates for a given user ID.
      * @param userId The ID of the user.
      * @return Flow emitting UserProfileData or null if not found/error.
      */
    fun observeUserProfile(userId: String): Flow<UserProfileData?>


    /**
     * Updates the current user's profile data.
     * @param userProfileData The profile data to update. Only non-null fields might be updated.
     * @return Result indicating success or failure.
     */
    suspend fun updateUserProfile(userId: String, profileData: UserProfileData): Result<Unit>

    /**
     * Gets the current user's ID.
     * @return The user ID string, or null if no user is authenticated.
     */
    fun getCurrentUserId(): String? // Duplicate of UserIdProvider, consider consolidation
}
