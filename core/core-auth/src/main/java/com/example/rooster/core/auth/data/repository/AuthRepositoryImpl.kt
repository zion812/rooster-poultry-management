package com.example.rooster.core.auth.data.repository

import com.example.rooster.core.auth.domain.model.User
import com.example.rooster.core.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            // Mock implementation for development
            val user = User(
                id = "mock_user_$email",
                email = email,
                name = "Mock User",
                phoneNumber = "+91 9876543210",
                isEmailVerified = true
            )
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            val user = User(
                id = "mock_user_$email",
                email = email,
                name = name,
                phoneNumber = null,
                isEmailVerified = false
            )
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            // Mock implementation
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return _currentUser.asStateFlow()
    }

    override suspend fun updateProfile(user: User): Result<User> {
        return try {
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isUserSignedIn(): Boolean {
        return _currentUser.value != null
    }
}