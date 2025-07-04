package com.rooster.adminhome.data.source

import com.rooster.adminhome.domain.model.FinancialAnalyticHighlight
import kotlinx.coroutines.flow.Flow

interface AdminFinancialRemoteDataSource {
    fun getFinancialHighlights(): Flow<List<FinancialAnalyticHighlight>>
}
