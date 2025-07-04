package com.rooster.farmerhome.data.repository

 feature/dashboard-scaffolding-and-weather-api
import com.rooster.farmerhome.core.common.util.DataState
import com.rooster.farmerhome.data.local.datasource.FarmHealthAlertLocalDataSource
import com.rooster.farmerhome.data.local.model.toDomain
import com.rooster.farmerhome.data.local.model.toEntity
import com.rooster.farmerhome.data.source.FarmHealthAlertRemoteDataSource
import com.rooster.farmerhome.domain.model.FarmHealthAlert
import com.rooster.farmerhome.domain.repository.FarmHealthAlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Duration
import javax.inject.Inject

class FarmHealthAlertRepositoryImpl @Inject constructor(
    private val remoteDataSource: FarmHealthAlertRemoteDataSource,
    private val localDataSource: FarmHealthAlertLocalDataSource
) : FarmHealthAlertRepository {

    private val alertsCacheDuration = Duration.ofMinutes(15)

    private fun isCacheListStale(entities: List<com.rooster.farmerhome.data.local.model.FarmHealthAlertEntity>): Boolean {
        if (entities.isEmpty()) return true
        // Use the timestamp of the most recently cached alert in the list as a proxy for list freshness
        val newestAlertCacheTimestamp = entities.maxOfOrNull { it.cacheTimestamp } ?: return true
        return (System.currentTimeMillis() - newestAlertCacheTimestamp) > alertsCacheDuration.toMillis()
    }

    override fun getHealthAlertsForFarm(farmId: String): Flow<DataState<List<FarmHealthAlert>>> {
        return localDataSource.getHealthAlertsForFarm(farmId).flatMapLatest { localAlertEntities ->
            val localDomainAlerts = localAlertEntities.map { it.toDomain() }

            if (localAlertEntities.isNotEmpty() && !isCacheListStale(localAlertEntities)) {
                 flow { emit(DataState.Success(localDomainAlerts, isFromCache = true, isStale = false)) }
            } else {
                flow<DataState<List<FarmHealthAlert>>> {
                    // Emit loading, potentially with stale data if localDomainAlerts is not empty
                    emit(DataState.Loading(localDomainAlerts.ifEmpty { null }))
                    try {
                        remoteDataSource.getHealthAlertsForFarm(farmId).collect { remoteAlerts ->
                            // Replace all local alerts for this farm with the fresh list from remote
                            localDataSource.insertHealthAlertsForFarm(farmId, remoteAlerts.map { it.toEntity() })
                            // The localDataSource flow will emit the new list, but to ensure this
                            // outer flow completes with the fresh data immediately:
                            emit(DataState.Success(remoteAlerts, isFromCache = false, isStale = false))
                        }
                    } catch (e: Exception) {
                        if (localDomainAlerts.isNotEmpty()) {
                             val oldestTimestamp = localAlertEntities.minOfOrNull { it.cacheTimestamp } ?: 0L
                            emit(DataState.Error(e, localDomainAlerts, Duration.ofMillis(System.currentTimeMillis() - oldestTimestamp)))
                        } else {
                            emit(DataState.Error(e, null, null))
                        }
                    }
                }
            }
        }.catch { e ->
            emit(DataState.Error(e, null, null, "Failed to load health alerts."))
        }
    }

    override suspend fun markAlertAsRead(farmId: String, alertId: String): Result<Unit> {
        try {
            // 1. Optimistic local update
            // The DAO's updateReadStatus only needs alertId, but we use farmId here for consistency
            // if other local logic depended on it, or if DAO was stricter.
            localDataSource.markAlertAsRead(farmId, alertId)
            // Note: This local update won't immediately reflect in the Flow from getHealthAlertsForFarm
            // unless the Flow is re-collected or the underlying data source for the Flow is explicitly triggered.
            // The ViewModel will refetch the list, which is simpler for now.

            // 2. Network call
            val remoteResult = remoteDataSource.markAlertAsRead(farmId, alertId)

            if (remoteResult.isFailure) {
                // 3. If network fails, revert local change
                localDataSource.markAlertAsUnread(farmId, alertId) // Revert
                // The UI will refresh via ViewModel refetching the list, showing the reverted state.
                return Result.failure(remoteResult.exceptionOrNull() ?: Exception("Network call failed to mark alert as read; local change reverted."))
            }
            // If remote succeeds, local change is already done. ViewModel refetch will show it.
            return Result.success(Unit)
        } catch (e: Exception) {
            // Attempt to revert if any other exception occurred
            try {
                localDataSource.markAlertAsUnread(farmId, alertId)
            } catch (revertError: Exception) {
                // Log revertError
            }
            return Result.failure(e)
        }

import com.rooster.farmerhome.data.source.FarmHealthAlertRemoteDataSource
// TODO: Import local data source when created
import com.rooster.farmerhome.domain.model.FarmHealthAlert
import com.rooster.farmerhome.domain.repository.FarmHealthAlertRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FarmHealthAlertRepositoryImpl @Inject constructor(
    private val remoteDataSource: FarmHealthAlertRemoteDataSource
    // TODO: private val localDataSource: FarmHealthAlertLocalDataSource
) : FarmHealthAlertRepository {

    override fun getHealthAlertsForFarm(farmId: String): Flow<List<FarmHealthAlert>> {
        // TODO: Implement offline-first caching strategy as per AGENTS.md
        // For now, directly fetching from remote
        return remoteDataSource.getHealthAlertsForFarm(farmId)
    }

    override suspend fun markAlertAsRead(alertId: String): Result<Unit> {
        // TODO: Update local cache as well
        val result = remoteDataSource.markAlertAsRead(alertId)
        if (result.isSuccess) {
            // TODO: Invalidate or update local cache for this alert
        }
        return result
 main
    }
}
