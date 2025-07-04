package com.rooster.adminhome.data.source

import com.example.rooster.testing.MockDataProvider
import com.rooster.adminhome.domain.model.FinancialAnalyticHighlight
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockAdminFinancialRemoteDataSource @Inject constructor() : AdminFinancialRemoteDataSource {
    override fun getFinancialHighlights(): Flow<List<FinancialAnalyticHighlight>> = flow {
        delay(700) // Simulate delay
        emit(MockDataProvider.Admin.financialHighlights())
    }
}
