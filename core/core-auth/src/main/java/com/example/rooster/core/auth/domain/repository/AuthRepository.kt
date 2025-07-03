package com.example.rooster.core.auth.domain.repository

import com.example.rooster.core.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String, name: String): Result<User>
    suspend fun signOut()
    suspend fun resetPassword(email: String): Result<Unit>
    fun getCurrentUser(): Flow<User?>
    suspend fun updateProfile(user: User): Result<User>
    suspend fun isUserSignedIn(): Boolean
}