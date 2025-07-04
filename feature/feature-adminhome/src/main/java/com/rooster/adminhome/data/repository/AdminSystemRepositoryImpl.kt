package com.rooster.adminhome.data.repository

import com.rooster.adminhome.data.source.AdminSystemRemoteDataSource
// TODO: Import local data source for caching
import com.rooster.adminhome.domain.model.SystemMetric
import com.rooster.adminhome.domain.repository.AdminSystemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdminSystemRepositoryImpl @Inject constructor(
    private val remoteDataSource: AdminSystemRemoteDataSource
    // TODO: private val localDataSource: AdminSystemLocalDataSource
) : AdminSystemRepository {

    override fun getCurrentSystemMetrics(): Flow<List<SystemMetric>> {
        // TODO: Implement offline-first caching strategy as per AGENTS.md
        // For now, directly fetching from remote
        return remoteDataSource.getCurrentSystemMetrics()
    }
}
