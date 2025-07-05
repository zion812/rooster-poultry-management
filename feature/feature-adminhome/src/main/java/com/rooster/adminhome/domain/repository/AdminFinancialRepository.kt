package com.rooster.adminhome.domain.repository

import com.rooster.adminhome.domain.model.FinancialAnalyticHighlight
import kotlinx.coroutines.flow.Flow

interface AdminFinancialRepository {
    fun getFinancialHighlights(): Flow<List<FinancialAnalyticHighlight>>
}
