package com.rooster.vethome.data.source

import com.example.rooster.testing.MockDataProvider
import com.rooster.vethome.domain.model.PatientHistorySummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockVetPatientRemoteDataSource @Inject constructor() : VetPatientRemoteDataSource {
    override fun getRecentPatientSummaries(vetId: String, count: Int): Flow<List<PatientHistorySummary>> = flow {
        delay(550) // Simulate delay
        emit(MockDataProvider.VetHome.recentPatientSummaries(vetId, count))
    }
}
