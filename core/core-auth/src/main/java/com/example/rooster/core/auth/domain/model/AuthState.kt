package com.example.rooster.core.auth.domain.model

// Assuming User is in the same package: com.example.rooster.core.auth.domain.model.User

/**
 * Represents the different states of user authentication.
 */
sealed class AuthState {
    /**
     * Authentication process is ongoing.
     */
    object Loading : AuthState()

    /**
     * User is successfully authenticated.
     * @param user The authenticated user's details from com.example.rooster.core.auth.domain.model.User.
     */
    data class Authenticated(val user: User) : AuthState()

    /**
     * No user is currently authenticated.
     */
    object Unauthenticated : AuthState()

    /**
     * An error occurred during the authentication process.
     * @param message A descriptive error message.
     */
    data class Error(val message: String) : AuthState()
}
