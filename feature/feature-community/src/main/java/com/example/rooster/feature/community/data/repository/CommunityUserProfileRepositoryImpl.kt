package com.example.rooster.feature.community.data.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.data.local.dao.CommunityUserProfileDao
import com.example.rooster.feature.community.data.local.model.CommunityUserProfileEntity
import com.example.rooster.feature.community.data.remote.CommunityRemoteDataSource
import com.example.rooster.feature.community.domain.model.CommunityUserProfile
import com.example.rooster.feature.community.domain.repository.CommunityUserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityUserProfileRepositoryImpl @Inject constructor(
    private val localDataSource: CommunityUserProfileDao,
    private val remoteDataSource: CommunityRemoteDataSource
) : CommunityUserProfileRepository {

    override fun getCommunityUserProfile(userId: String, forceRefresh: Boolean): Flow<Result<CommunityUserProfile?>> {
        return localBackedCommunityResource(
            localCall = { localDataSource.getProfileByUserId(userId).map { it?.let(::mapEntityToDomain) } },
            remoteCall = { remoteDataSource.getCommunityUserProfileStream(userId).firstOrNull() ?: Result.Success(null) },
            saveRemoteResult = { remoteProfileDomain ->
                if (remoteProfileDomain != null) {
                    val localEntity = localDataSource.getProfileByUserIdSuspend(remoteProfileDomain.userId)
                    if (localEntity?.needsSync == true) {
                        Timber.w("Community: Local profile for user ID ${remoteProfileDomain.userId} has unsynced changes. Remote update will be ignored for now.")
                    } else {
                        localDataSource.insertProfile(mapDomainToEntity(remoteProfileDomain, needsSync = false))
                        Timber.d("Community: Cache updated from remote for user profile ID ${remoteProfileDomain.userId}.")
                    }
                }
            },
            shouldFetch = { forceRefresh || it == null }
        ).flowOn(Dispatchers.IO)
    }

    override suspend fun createCommunityUserProfile(profile: CommunityUserProfile): Result<String> =
        withContext(Dispatchers.IO) {
        try {
            val entity = mapDomainToEntity(profile, needsSync = true)
            localDataSource.insertProfile(entity)
            val remoteResult = remoteDataSource.createCommunityUserProfile(profile)
            when (remoteResult) {
                is Result.Success -> {
                    localDataSource.insertProfile(entity.copy(needsSync = false))
                    Result.Success(profile.userId) // Return the user ID as the created resource ID
                }

                is Result.Error -> {
                    Timber.w(
                        remoteResult.exception,
                        "Failed to sync user profile to remote, will retry later"
                    )
                    Result.Success(profile.userId) // Return local ID, will sync later
                }

                Result.Loading -> Result.Success(profile.userId)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateCommunityUserProfile(profile: CommunityUserProfile): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val entity = mapDomainToEntity(profile, needsSync = true)
                localDataSource.insertProfile(entity)
                val remoteResult = remoteDataSource.updateCommunityUserProfile(profile)
                when (remoteResult) {
                    is Result.Success -> {
                        localDataSource.insertProfile(entity.copy(needsSync = false))
                        Result.Success(Unit)
                    }

                    is Result.Error -> {
                        Timber.w(
                            remoteResult.exception,
                            "Failed to sync user profile update to remote"
                        )
                        Result.Success(Unit) // Local update succeeded, will sync later
                    }

                    Result.Loading -> Result.Success(Unit)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getUnsyncedUserProfileEntities(): List<CommunityUserProfileEntity> = withContext(Dispatchers.IO) {
        localDataSource.getUnsyncedProfilesSuspend()
    }

    override suspend fun syncUserProfileRemote(profile: CommunityUserProfile): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteDataSource.updateCommunityUserProfile(profile)
            when (remoteResult) {
                is Result.Success -> {
                    Result.Success(Unit)
                }

                is Result.Error -> {
                    Timber.e(
                        remoteResult.exception,
                        "Failed to sync user profile ${profile.userId} to remote."
                    )
                    Result.Error(remoteResult.exception)
                }

                Result.Loading -> Result.Success(Unit) // Treat loading as success for now
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during remote user profile sync for ${profile.userId}")
            Result.Error(e)
        }
    }

    override suspend fun updateLocalUserProfileEntity(profileEntity: CommunityUserProfileEntity) {
        withContext(Dispatchers.IO) {
            localDataSource.insertProfile(profileEntity)
        }
    }

    override fun mapUserProfileEntityToDomain(profileEntity: CommunityUserProfileEntity): CommunityUserProfile {
        return mapEntityToDomain(profileEntity)
    }

    private fun mapEntityToDomain(entity: CommunityUserProfileEntity): CommunityUserProfile {
        return CommunityUserProfile(
            userId = entity.userId,
            displayName = entity.displayName,
            profilePictureUrl = entity.profilePictureUrl,
            bio = entity.bio,
            location = entity.location,
            farmName = entity.farmName,
            interests = entity.interests,
            followerCount = entity.followerCount,
            followingCount = entity.followingCount,
            postCount = entity.postCount,
            lastActiveTimestamp = entity.lastActiveTimestamp,
            joinDateTimestamp = entity.joinDateTimestamp,
            isVerifiedFarmer = entity.isVerifiedFarmer,
            isEnthusiast = entity.isEnthusiast
        )
    }

    private fun mapDomainToEntity(domain: CommunityUserProfile, needsSync: Boolean): CommunityUserProfileEntity {
        return CommunityUserProfileEntity(
            userId = domain.userId,
            displayName = domain.displayName,
            profilePictureUrl = domain.profilePictureUrl,
            bio = domain.bio,
            location = domain.location,
            farmName = domain.farmName,
            interests = domain.interests,
            followerCount = domain.followerCount,
            followingCount = domain.followingCount,
            postCount = domain.postCount,
            lastActiveTimestamp = domain.lastActiveTimestamp,
            joinDateTimestamp = domain.joinDateTimestamp,
            isVerifiedFarmer = domain.isVerifiedFarmer,
            isEnthusiast = domain.isEnthusiast,
            needsSync = needsSync
        )
    }

    private inline fun <D, S> localBackedCommunityResource(
        crossinline localCall: () -> Flow<D?>,
        crossinline remoteCall: suspend () -> Result<S?>,
        crossinline saveRemoteResult: suspend (S) -> Unit,
        crossinline shouldFetch: (D?) -> Boolean = { true }
    ): Flow<Result<D?>> = flow {
        emit(Result.Loading)
        val localData = localCall().firstOrNull()
        if (localData != null) {
            emit(Result.Success(localData))
        }

        if (shouldFetch(localData)) {
            when (val remoteResult = remoteCall()) {
                is Result.Success -> {
                    val data = remoteResult.data
                    if (data != null) {
                        saveRemoteResult(data)
                        localCall().collect { updatedLocalData ->
                            emit(Result.Success(updatedLocalData))
                        }
                    } else {
                        if (localData == null) emit(Result.Success(null))
                    }
                }
                is Result.Error -> emit(Result.Error(remoteResult.exception))
                Result.Loading -> {}
            }
        } else if (localData == null) {
            emit(Result.Success(null))
        }
    }.catch { e -> emit(Result.Error(e as Throwable)) }
}