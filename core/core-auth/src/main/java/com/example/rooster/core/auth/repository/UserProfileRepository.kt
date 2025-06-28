package com.example.rooster.core.auth.repository

import com.example.rooster.core.auth.datasources.UserDataSource
import com.example.rooster.core.auth.model.UserProfileData
import com.example.rooster.core.common.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface UserProfileRepository {
    suspend fun getUserProfile(userId: String): Result<UserProfileData>
    fun observeUserProfile(userId: String): Flow<UserProfileData?>
    suspend fun updateUserProfile(userId: String, profileData: UserProfileData): Result<Unit>
}

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserProfileRepository {

    override suspend fun getUserProfile(userId: String): Result<UserProfileData> =
        withContext(Dispatchers.IO) {
            userDataSource.getUserProfile(userId)
        }

    override fun observeUserProfile(userId: String): Flow<UserProfileData?> {
        return userDataSource.observeUserProfile(userId)
    }

    override suspend fun updateUserProfile(userId: String, profileData: UserProfileData): Result<Unit> =
        withContext(Dispatchers.IO) {
            // Ensure that the userId in profileData matches the userId parameter for consistency,
            // or rely on the UserDataSource to handle this (e.g., by only allowing updates for current user).
            // For now, assuming UserDataSource.updateUserProfile correctly targets the given userId.
            if (profileData.userId != userId) {
                 // This check might be more appropriate at a UseCase level or within UserDataSource if it's strict
                return@withContext Result.Error(IllegalArgumentException("User ID in profile data does not match target User ID for update."))
            }
            userDataSource.updateUserProfile(userId, profileData)
        }
}
