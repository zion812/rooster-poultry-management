package com.rooster.farmerhome.data.repository

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
    }
}
