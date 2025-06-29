package com.example.rooster.feature.community.domain.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.domain.model.CommunityUserProfile
import kotlinx.coroutines.flow.Flow

interface CommunityUserProfileRepository {
    fun getCommunityUserProfile(userId: String, forceRefresh: Boolean = false): Flow<Result<CommunityUserProfile?>>
    suspend fun createCommunityUserProfile(profile: CommunityUserProfile): Result<String>
    suspend fun updateCommunityUserProfile(profile: CommunityUserProfile): Result<Unit>

    // Methods for SyncWorker
    suspend fun getUnsyncedUserProfileEntities(): List<com.example.rooster.feature.community.data.local.model.CommunityUserProfileEntity>
    suspend fun syncUserProfileRemote(profile: CommunityUserProfile): Result<Unit>
    suspend fun updateLocalUserProfileEntity(profileEntity: com.example.rooster.feature.community.data.local.model.CommunityUserProfileEntity)
    fun mapUserProfileEntityToDomain(profileEntity: com.example.rooster.feature.community.data.local.model.CommunityUserProfileEntity): CommunityUserProfile

    // Add search or other specific query methods as needed
    // fun searchCommunityUsers(query: String): Flow<Result<List<CommunityUserProfile>>>
}
