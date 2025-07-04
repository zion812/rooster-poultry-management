package com.rooster.farmerhome.data.repository

import com.rooster.farmerhome.core.common.util.DataState
import com.rooster.farmerhome.data.local.datasource.ProductionSummaryLocalDataSource
import com.rooster.farmerhome.data.local.model.toDomain
import com.rooster.farmerhome.data.source.ProductionMetricsRemoteDataSource
import com.rooster.farmerhome.domain.model.ProductionSummary
import com.rooster.farmerhome.domain.repository.ProductionMetricsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import java.time.Duration
import javax.inject.Inject

class ProductionMetricsRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductionMetricsRemoteDataSource,
    private val localDataSource: ProductionSummaryLocalDataSource
) : ProductionMetricsRepository {

    private val productionSummaryCacheDuration = Duration.ofHours(2) // Cache for 2 hours

    private fun isCacheStale(timestamp: Long): Boolean {
        return (System.currentTimeMillis() - timestamp) > productionSummaryCacheDuration.toMillis()
    }

    override fun getProductionSummary(farmId: String): Flow<DataState<ProductionSummary?>> {
        return localDataSource.getProductionSummaryWithMetrics(farmId).flatMapLatest { localDataWithMetrics ->
            val localDomainData = localDataWithMetrics?.toDomain()
            if (localDataWithMetrics != null && !isCacheStale(localDataWithMetrics.summary.timestamp)) {
                flow { emit(DataState.Success(localDomainData, isFromCache = true, isStale = false)) }
            } else {
                flow<DataState<ProductionSummary?>> {
                    emit(DataState.Loading(localDomainData))
                    try {
                        remoteDataSource.getProductionSummary(farmId).collect { remoteSummary ->
                            // Assuming remoteSummary is never null from a successful API call,
                            // but its internal lists might be empty.
                            // The API returns ProductionSummary, not ProductionSummary?
                            // So if collect is called, remoteSummary is non-null.
                            localDataSource.insertProductionSummary(farmId, remoteSummary)
                            emit(DataState.Success(remoteSummary, isFromCache = false, isStale = false))
                        }
                    } catch (e: Exception) {
                        if (localDomainData != null) {
                            emit(DataState.Error(e, localDomainData, Duration.ofMillis(System.currentTimeMillis() - (localDataWithMetrics?.summary?.timestamp ?: 0))))
                        } else {
                            emit(DataState.Error(e, null, null))
                        }
                    }
                }
            }
        }.catch { e ->
            emit(DataState.Error(e, null, null, "Failed to load production summary."))
        }
    }
}
