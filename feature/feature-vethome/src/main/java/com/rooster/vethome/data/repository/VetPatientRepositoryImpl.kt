package com.rooster.vethome.data.repository

import com.rooster.vethome.data.source.VetPatientRemoteDataSource
import com.rooster.vethome.domain.model.PatientHistorySummary
import com.rooster.vethome.domain.repository.VetPatientRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VetPatientRepositoryImpl @Inject constructor(
    private val remoteDataSource: VetPatientRemoteDataSource
    // TODO: Add localDataSource for caching
) : VetPatientRepository {
    override fun getRecentPatientSummaries(vetId: String, count: Int): Flow<List<PatientHistorySummary>> {
        return remoteDataSource.getRecentPatientSummaries(vetId, count)
    }
}
