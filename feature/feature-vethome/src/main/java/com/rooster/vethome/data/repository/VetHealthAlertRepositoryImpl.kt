package com.rooster.vethome.data.repository

import com.rooster.vethome.data.source.VetHealthAlertRemoteDataSource
import com.rooster.vethome.domain.model.VetHealthAlert
import com.rooster.vethome.domain.repository.VetHealthAlertRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VetHealthAlertRepositoryImpl @Inject constructor(
    private val remoteDataSource: VetHealthAlertRemoteDataSource
    // TODO: Add localDataSource for caching if applicable
) : VetHealthAlertRepository {
    override fun getActiveHealthAlerts(vetId: String, count: Int): Flow<List<VetHealthAlert>> {
        return remoteDataSource.getActiveHealthAlerts(vetId, count)
    }
}
