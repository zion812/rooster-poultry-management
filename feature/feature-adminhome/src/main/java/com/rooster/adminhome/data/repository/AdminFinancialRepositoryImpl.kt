package com.rooster.adminhome.data.repository

import com.rooster.adminhome.data.source.AdminFinancialRemoteDataSource
import com.rooster.adminhome.domain.model.FinancialAnalyticHighlight
import com.rooster.adminhome.domain.repository.AdminFinancialRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdminFinancialRepositoryImpl @Inject constructor(
    private val remoteDataSource: AdminFinancialRemoteDataSource
    // TODO: Add localDataSource for caching
) : AdminFinancialRepository {
    override fun getFinancialHighlights(): Flow<List<FinancialAnalyticHighlight>> {
        // TODO: Implement caching
        return remoteDataSource.getFinancialHighlights()
    }
}
