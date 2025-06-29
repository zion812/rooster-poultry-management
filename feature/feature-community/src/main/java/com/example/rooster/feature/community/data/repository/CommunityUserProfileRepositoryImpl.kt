package com.example.rooster.feature.community.data.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.data.local.dao.CommunityUserProfileDao
import com.example.rooster.feature.community.data.local.model.CommunityUserProfileEntity
import com.example.rooster.feature.community.data.remote.CommunityRemoteDataSource
import com.example.rooster.feature.community.domain.model.CommunityUserProfile
import com.example.rooster.feature.community.domain.repository.CommunityUserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
            saveRemoteResult = { remoteProfileDomain -> // S is CommunityUserProfile
                if (remoteProfileDomain != null) {
                    val localEntity = localDataSource.getProfileByUserIdSuspend(remoteProfileDomain.userId)
                    if (localEntity?.needsSync == true) {
                        Timber.w("Community: Local profile for user ID ${remoteProfileDomain.userId} has unsynced changes. Remote update from listener/fetch will be ignored for now.")
                    } else {
                        localDataSource.insertProfile(mapDomainToEntity(remoteProfileDomain, needsSync = false))
                        Timber.d("Community: Cache updated from remote for user profile ID ${remoteProfileDomain.userId}.")
                    }
                }
            },
            shouldFetch = { localData -> forceRefresh || localData == null }
        ).flowOn(Dispatchers.IO)
    }

    override suspend fun createCommunityUserProfile(profile: CommunityUserProfile): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = mapDomainToEntity(profile, needsSync = true)
            localDataSource.insertProfile(entity)
            val remoteResult = remoteDataSource.createCommunityUserProfile(profile)
            if (remoteResult is Result.Success) {
                localDataSource.insertProfile(entity.copy(needsSync = false))
            }
            // Return remote result (Unit or Error)
            remoteResult
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUnsyncedUserProfiles(): List<CommunityUserProfile> = withContext(Dispatchers.IO) {
        localDataSource.getUnsyncedProfilesSuspend().map { mapEntityToDomain(it) }
    }

    override suspend fun syncUserProfile(profile: CommunityUserProfile): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Assuming remoteDataSource.updateCommunityUserProfile can create if not exists,
            // or a specific createOrUpdateProfile method exists.
            // For simplicity, let's use update, assuming profile.userId is always present.
            val remoteResult = remoteDataSource.updateCommunityUserProfile(profile)
            if (remoteResult is Result.Success) {
                localDataSource.insertProfile(mapDomainToEntity(profile, needsSync = false)) // Use insert for REPLACE
                Result.Success(Unit)
            } else {
                Result.Error((remoteResult as Result.Error).exception)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateCommunityUserProfile(profile: CommunityUserProfile): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = mapDomainToEntity(profile, needsSync = true)
            localDataSource.updateProfile(entity) // Assuming updateProfile in DAO or use insert with REPLACE
            val remoteResult = remoteDataSource.updateCommunityUserProfile(profile)
            if (remoteResult is Result.Success) {
                localDataSource.updateProfile(entity.copy(needsSync = false))
            }
            remoteResult
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

import timber.log.Timber // Ensure Timber is imported

    // New methods for SyncWorker
    override suspend fun getUnsyncedUserProfileEntities(): List<CommunityUserProfileEntity> = withContext(Dispatchers.IO) {
        localDataSource.getUnsyncedProfilesSuspend()
    }

    override suspend fun syncUserProfileRemote(profile: CommunityUserProfile): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // This remote method should handle create or update (upsert) based on profile.userId
            val remoteResult = remoteDataSource.updateCommunityUserProfile(profile) // update can often mean upsert
            // Or if distinct create/update methods exist on remoteDataSource:
            // val remoteResult = if (isNewProfile) remoteDataSource.createCommunityUserProfile(profile) else remoteDataSource.updateCommunityUserProfile(profile)

            if (remoteResult is Result.Success) {
                Result.Success(Unit)
            } else {
                Timber.e((remoteResult as? Result.Error)?.exception, "Failed to sync user profile ${profile.userId} to remote.")
                Result.Error((remoteResult as Result.Error).exception)
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during remote user profile sync for ${profile.userId}")
            Result.Error(e)
        }
    }

    override suspend fun updateLocalUserProfileEntity(profileEntity: CommunityUserProfileEntity) {
        withContext(Dispatchers.IO) {
            localDataSource.insertProfile(profileEntity) // insert with OnConflictStrategy.REPLACE acts as update
        }
    }

    override fun mapUserProfileEntityToDomain(profileEntity: CommunityUserProfileEntity): CommunityUserProfile {
        return mapEntityToDomain(profileEntity) // Use existing private mapper
    }
    // End of new methods for SyncWorker

    // --- Mappers ---
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
}

// Generic helper for network-bound resource pattern (similar to one in Marketplace)
// D: Domain model type, S: Remote source type
private inline fun <D, S> localBackedCommunityResource(
    crossinline localCall: () -> Flow<D?>,
    crossinline remoteCall: suspend () -> Result<S?>,
    crossinline saveRemoteResult: suspend (S) -> Unit,
    crossinline shouldFetch: (D?) -> Boolean = { true }
): Flow<Result<D?>> = flow<Result<D?>> {
    emit(Result.Loading)
    val localData = localCall().firstOrNull()
    if (localData != null) {
        emit(Result.Success(localData))
    }

    if (shouldFetch(localData)) {
        when (val remoteResult = remoteCall()) {
            is Result.Success -> {
                if (remoteResult.data != null) {
                    saveRemoteResult(remoteResult.data)
                    localCall().collect { updatedLocalData -> emit(Result.Success(updatedLocalData)) }
                } else {
                    if (localData == null) emit(Result.Success(null))
                }
            }
            is Result.Error -> emit(Result.Error(remoteResult.exception, localData))
            Result.Loading -> {}
        }
    } else if (localData == null) {
        emit(Result.Success(null))
    }
}.catch { e -> emit(Result.Error(e)) }
