package com.example.rooster.core.auth.data.repository

import com.example.rooster.core.auth.domain.repository.TokenRepository
import com.example.rooster.core.common.Result
import com.parse.ParseCloud
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [TokenRepository] that uses a Parse backend for managing user-specific tokens
 * (e.g., app usage credits, not to be confused with authentication tokens).
 *
 * This implementation assumes that the Parse SDK is initialized and a user may be logged in.
 * Operations like deducting or adding tokens are expected to be handled by Parse Cloud Code functions
 * for security and atomicity.
 *
 * **Note:** This is a placeholder implementation. The actual field names (e.g., "tokenBalance")
 * and Cloud Code function names (e.g., "deductTokens", "addTokens") must match the
 * Parse backend configuration.
 */
@Singleton
class ParseTokenRepositoryImpl @Inject constructor() : TokenRepository {

    // Assuming Parse SDK is initialized elsewhere and ParseUser.current() is available.

    /**
     * Retrieves the current user's token balance from their Parse User object.
     * Assumes a field named "tokenBalance" exists on the Parse User class.
     *
     * @return [Result.Success] with the token balance if successful.
     *         [Result.Error] if no user is logged in or if an error occurs.
     */
    override suspend fun getTokenBalance(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val currentUser = ParseUser.getCurrentUser()
            if (currentUser == null) {
                Timber.w("GetTokenBalance: No current Parse user.")
                return@withContext Result.Error(Exception("User not logged in to Parse."))
            }
            // Assuming "tokenBalance" is a field in the User class in Parse
            val balance = currentUser.getInt("tokenBalance")
            Result.Success(balance)
        } catch (e: Exception) {
            Timber.e(e, "ParseTokenRepository: Error getting token balance.")
            Result.Error(e)
        }
    }

    /**
     * Deducts a specified number of tokens from the current user by calling a Parse Cloud Code function.
     * Assumes a Cloud Code function named "deductTokens" exists.
     *
     * @param userId The ID of the user (currently unused in this placeholder, as Cloud Code often uses current session).
     * @param count The number of tokens to deduct.
     * @return [Result.Success] if the Cloud Code function call is successful.
     *         [Result.Error] if the call fails.
     */
    override suspend fun deductTokens(userId: String, count: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // For Parse, often such logic is handled by Cloud Code for security and atomicity.
            val params = HashMap<String, Any>()
            params["count"] = count
            // params["userId"] = userId // Cloud function usually gets current user from session

            ParseCloud.callFunction<Any>("deductTokens", params).await()
            // Cloud function should throw an error if deduction fails.
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "ParseTokenRepository: Error deducting tokens for user $userId.")
            Result.Error(e)
        }
    }

    /**
     * Adds a specified number of tokens to the current user by calling a Parse Cloud Code function.
     * Assumes a Cloud Code function named "addTokens" exists.
     *
     * @param userId The ID of the user (currently unused in this placeholder).
     * @param count The number of tokens to add.
     * @return [Result.Success] if the Cloud Code function call is successful.
     *         [Result.Error] if the call fails.
     */
    override suspend fun addTokens(userId: String, count: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Similar to deduct, often handled by Cloud Code.
            val params = HashMap<String, Any>()
            params["count"] = count
            // params["userId"] = userId

            ParseCloud.callFunction<Any>("addTokens", params).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "ParseTokenRepository: Error adding tokens for user $userId.")
            Result.Error(e)
        }
    }
}
