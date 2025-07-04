package com.rooster.vethome.data.source

import com.example.rooster.testing.MockDataProvider
import com.rooster.vethome.domain.model.VetHealthAlert
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockVetHealthAlertRemoteDataSource @Inject constructor() : VetHealthAlertRemoteDataSource {
    override fun getActiveHealthAlerts(vetId: String, count: Int): Flow<List<VetHealthAlert>> = flow {
        delay(750) // Simulate delay
        emit(MockDataProvider.VetHome.activeHealthAlerts(vetId, count))
    }
}
