package com.example.rooster.core.common.user

import kotlinx.coroutines.flow.Flow

/**
 * Provides access to the current authenticated user's ID.
 */
interface UserIdProvider {
    /**
     * Gets the current user's ID.
     * @return The user ID string, or null if no user is authenticated.
     */
    fun getCurrentUserId(): String?

    /**
     * A flow that emits the current user's ID.
     * Emits null if no user is authenticated.
     * This can be used to reactively observe changes in authentication state.
     */
    val currentUserIdFlow: Flow<String?>
}
