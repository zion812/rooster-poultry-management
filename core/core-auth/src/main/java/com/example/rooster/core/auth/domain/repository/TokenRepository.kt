package com.example.rooster.core.auth.domain.repository // Updated package

import com.example.rooster.core.common.Result // It's good practice to use the common Result type
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing user token balances and operations.
 * These tokens might be related to app usage credits, premium features, etc.
 * This is distinct from authentication tokens.
 */
interface TokenRepository {

    /**
     * Retrieves the current user's token balance.
     * Consider returning Result<Int> or Flow<Result<Int>> for better error handling and reactivity.
     */
    suspend fun getTokenBalance(): Result<Int> // Modernized signature

    /**
     * Deducts a specified number of tokens from the current user.
     * @param count The number of tokens to deduct.
     */
    suspend fun deductTokens(userId: String, count: Int = 1): Result<Unit> // Modernized signature

    /**
     * Adds a specified number of tokens to the current user.
     * Typically called after a successful token purchase or reward.
     * @param count The number of tokens to add.
     */
    suspend fun addTokens(userId: String, count: Int): Result<Unit> // Modernized signature
}
