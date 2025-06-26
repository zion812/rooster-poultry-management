package com.example.rooster.core.common.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing user token balances and operations.
 */
interface TokenRepository {

    /**
     * Retrieves the current user's token balance.
     * This might return a Flow if the balance can change and UI needs to observe it.
     * Or a simple suspend function if it's a one-time fetch for an operation.
     * For now, let's use a suspend fun that takes a callback, similar to the old TokenService.
     * A Flow-based approach would be more modern.
     */
    // suspend fun getTokenBalance(): Result<Int> // Alternative Flow-based approach
    suspend fun loadTokenBalance(onResult: (Int) -> Unit) // To match existing TokenService for easier refactor

    /**
     * Deducts a specified number of tokens (usually 1) from the current user.
     * @param count The number of tokens to deduct.
     * @param onResult Callback indicating success (true) or failure (false).
     */
    // suspend fun deductToken(count: Int = 1): Result<Boolean> // Alternative
    suspend fun deductTokens(count: Int = 1, onResult: (Boolean) -> Unit) // To match existing

    /**
     * Adds a specified number of tokens to the current user.
     * Typically called after a successful token purchase.
     * @param count The number of tokens to add.
     * @param onResult Callback indicating success (true) or failure (false).
     */
    // suspend fun addTokens(count: Int): Result<Boolean> // Alternative
    suspend fun addTokens(count: Int, onResult: (Boolean) -> Unit) // To match existing
}
