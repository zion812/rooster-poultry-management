package com.example.rooster.core.auth.repository

import com.example.rooster.core.auth.datasources.UserDataSource
import com.example.rooster.core.auth.model.AuthUser
import com.example.rooster.core.common.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    suspend fun signUp(email: String, password: String, initialProfileData: Map<String, Any?>? = null): Result<AuthUser>
    suspend fun logIn(email: String, password: String): Result<AuthUser>
    suspend fun logOut(): Result<Unit>
    suspend fun getCurrentUser(): AuthUser?
    fun observeAuthState(): Flow<AuthUser?>
    suspend fun requestPasswordReset(email: String): Result<Unit>
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : AuthRepository {

    override suspend fun signUp(email: String, password: String, initialProfileData: Map<String, Any?>?): Result<AuthUser> =
        withContext(Dispatchers.IO) { // Perform on IO dispatcher
            userDataSource.signUp(email, password, initialProfileData)
        }

    override suspend fun logIn(email: String, password: String): Result<AuthUser> =
        withContext(Dispatchers.IO) {
            userDataSource.logIn(email, password)
        }

    override suspend fun logOut(): Result<Unit> =
        withContext(Dispatchers.IO) {
            userDataSource.logOut()
        }

    override suspend fun getCurrentUser(): AuthUser? =
        withContext(Dispatchers.IO) {
            userDataSource.getCurrentUser()
        }

    override fun observeAuthState(): Flow<AuthUser?> {
        return userDataSource.observeAuthState() // Flow is typically already on appropriate dispatcher
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            userDataSource.requestPasswordReset(email)
        }
}
