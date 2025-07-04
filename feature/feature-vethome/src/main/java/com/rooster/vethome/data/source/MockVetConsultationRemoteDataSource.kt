package com.rooster.vethome.data.source

import com.example.rooster.testing.MockDataProvider
import com.rooster.vethome.domain.model.ConsultationQueueItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockVetConsultationRemoteDataSource @Inject constructor() : VetConsultationRemoteDataSource {
    override fun getConsultationQueue(vetId: String): Flow<List<ConsultationQueueItem>> = flow {
        delay(650) // Simulate delay
        // Assuming vetId might influence the mock data, but MockDataProvider handles it for now
        emit(MockDataProvider.VetHome.consultationQueue(vetId, 7)) // Fetch 7 for more variety
    }
}
