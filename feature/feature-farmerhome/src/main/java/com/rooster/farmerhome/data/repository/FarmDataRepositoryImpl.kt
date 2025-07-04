package com.rooster.farmerhome.data.repository

 feature/dashboard-scaffolding-and-weather-api
import com.rooster.farmerhome.core.common.util.DataState
import com.rooster.farmerhome.data.local.datasource.FarmBasicInfoLocalDataSource
import com.rooster.farmerhome.data.local.model.toDomain
import com.rooster.farmerhome.data.local.model.toEntity
import com.rooster.farmerhome.data.source.FarmDataRemoteDataSource
import com.rooster.farmerhome.domain.model.FarmBasicInfo
import com.rooster.farmerhome.domain.repository.FarmDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import java.time.Duration
import javax.inject.Inject

class FarmDataRepositoryImpl @Inject constructor(
    private val remoteDataSource: FarmDataRemoteDataSource,
    private val localDataSource: FarmBasicInfoLocalDataSource
) : FarmDataRepository {

    private val farmInfoCacheDuration = Duration.ofHours(1) // Cache farm info for 1 hour

    private fun isCacheStale(timestamp: Long): Boolean {
        return (System.currentTimeMillis() - timestamp) > farmInfoCacheDuration.toMillis()
    }

    override fun getFarmBasicInfo(farmId: String): Flow<DataState<FarmBasicInfo?>> {
        return localDataSource.getFarmBasicInfo(farmId).flatMapLatest { localEntity ->
            val localDomainData = localEntity?.toDomain()
            if (localEntity != null && !isCacheStale(localEntity.timestamp)) {
                flow { emit(DataState.Success(localDomainData, isFromCache = true, isStale = false)) }
            } else {
                flow<DataState<FarmBasicInfo?>> {
                    emit(DataState.Loading(localDomainData))
                    try {
                        // Fetch from remote
                        remoteDataSource.getFarmBasicInfo(farmId).collect { remoteFarmInfo ->
                            if (remoteFarmInfo != null) {
                                localDataSource.insertFarmBasicInfo(remoteFarmInfo.toEntity())
                                emit(DataState.Success(remoteFarmInfo, isFromCache = false, isStale = false))
                            } else {
                                // Farm not found by API, or other API error returning null
                                // If we have stale local data, show it with an error.
                                // Otherwise, it's a "not found" type of error.
                                val errorMsg = "Farm details not found via API."
                                if (localDomainData != null) {
                                    emit(DataState.Error(Exception(errorMsg), localDomainData, Duration.ofMillis(System.currentTimeMillis() - (localEntity?.timestamp ?: 0))))
                                } else {
                                    emit(DataState.Error(Exception(errorMsg), null, null))
                                }
                            }
                        }
                    } catch (e: Exception) {
                        if (localDomainData != null) {
                            emit(DataState.Error(e, localDomainData, Duration.ofMillis(System.currentTimeMillis() - (localEntity?.timestamp ?: 0))))
                        } else {
                            emit(DataState.Error(e, null, null))
                        }
                    }
                }
            }
        }.catch { e ->
            // Catch errors from localDataSource.getFarmBasicInfo() or unhandled upstream
            emit(DataState.Error(e, null, null, "Failed to load farm information."))
        }

import com.rooster.farmerhome.data.source.FarmDataRemoteDataSource
// TODO: Import local data source for caching
import com.rooster.farmerhome.domain.model.FarmBasicInfo
import com.rooster.farmerhome.domain.repository.FarmDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FarmDataRepositoryImpl @Inject constructor(
    private val remoteDataSource: FarmDataRemoteDataSource
    // TODO: private val localDataSource: FarmDataLocalDataSource
) : FarmDataRepository {

    override fun getFarmBasicInfo(farmId: String): Flow<FarmBasicInfo?> {
        // TODO: Implement offline-first caching strategy as per AGENTS.md
        // For now, directly fetching from remote
        return remoteDataSource.getFarmBasicInfo(farmId)
 main
    }
}
