package com.example.rooster.data.repositories

import com.example.rooster.domain.repository.AuthRepository
import com.example.rooster.util.Result
import com.parse.ParseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import com.example.rooster.data.AuthRepository as ConcreteAuthRepository

/**
 * Implementation of AuthRepository interface that wraps the concrete AuthRepository
 */
@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val concreteAuthRepository: ConcreteAuthRepository,
    ) : AuthRepository {
        private val _currentUser = MutableStateFlow<ParseUser?>(null)
        override val currentUser: Flow<ParseUser?> = _currentUser.asStateFlow()

        init {
            // Initialize with current user if available
            _currentUser.value = concreteAuthRepository.getCurrentUser()
        }

        override suspend fun requestOtp(phoneNumber: String): Result<Unit> {
            // Mock OTP implementation - in real app this would integrate with SMS service
            return Result.Success(Unit)
        }

        override suspend fun verifyOtp(
            phoneNumber: String,
            otp: String,
        ): Result<ParseUser> {
            // Mock OTP verification - in real app this would verify with SMS service
            // For now, simulate successful verification and login
            return login(phoneNumber, "defaultPassword")
        }

        override suspend fun login(
            phoneNumber: String,
            password: String,
        ): Result<ParseUser> {
            val result = concreteAuthRepository.login(phoneNumber, password)
            if (result.isSuccess) {
                val authResult = result.getOrNull()
                if (authResult?.isSuccess == true && authResult.user != null) {
                    _currentUser.value = authResult.user
                    return Result.Success(authResult.user)
                } else {
                    return Result.Error(Exception("Login failed"))
                }
            } else {
                return Result.Error(result.exceptionOrNull() ?: Exception("Login failed"))
            }
        }

        override suspend fun register(
            username: String,
            email: String,
            password: String,
            role: String,
        ): Result<ParseUser> {
            val result = concreteAuthRepository.register(username, email, password, role)
            if (result.isSuccess) {
                val authResult = result.getOrNull()
                if (authResult?.isSuccess == true && authResult.user != null) {
                    _currentUser.value = authResult.user
                    return Result.Success(authResult.user)
                } else {
                    return Result.Error(Exception("Registration failed"))
                }
            } else {
                return Result.Error(result.exceptionOrNull() ?: Exception("Registration failed"))
            }
        }

        override fun getCurrentUser(): ParseUser? {
            return concreteAuthRepository.getCurrentUser()
        }

        override suspend fun logout(): Result<Unit> {
            val result = concreteAuthRepository.logout()
            if (result.isSuccess) {
                _currentUser.value = null
                return Result.Success(Unit)
            } else {
                return Result.Error(result.exceptionOrNull() ?: Exception("Logout failed"))
            }
        }
    }
