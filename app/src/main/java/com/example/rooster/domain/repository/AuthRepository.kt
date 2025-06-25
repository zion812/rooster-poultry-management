package com.example.rooster.domain.repository

import com.example.rooster.util.Result
import com.parse.ParseUser
import kotlinx.coroutines.flow.Flow

/**
 * Domain layer repository interface for authentication operations
 */
interface AuthRepository {
    /**
     * Current authenticated user as a Flow
     */
    val currentUser: Flow<ParseUser?>

    /**
     * Login with phone number and OTP
     */
    suspend fun requestOtp(phoneNumber: String): Result<Unit>

    /**
     * Verify OTP code
     */
    suspend fun verifyOtp(
        phoneNumber: String,
        otp: String,
    ): Result<ParseUser>

    /**
     * Login with phone number and password
     */
    suspend fun login(
        phoneNumber: String,
        password: String,
    ): Result<ParseUser>

    /**
     * Register new user
     */
    suspend fun register(
        username: String,
        email: String,
        password: String,
        role: String
    ): Result<ParseUser>

    /**
     * Get current user
     */
    fun getCurrentUser(): ParseUser?

    /**
     * Logout current user
     */
    suspend fun logout(): Result<Unit>
}
