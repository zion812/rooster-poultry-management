package com.rooster.vethome.data.repository

import com.rooster.vethome.data.source.VetConsultationRemoteDataSource
import com.rooster.vethome.domain.model.ConsultationQueueItem
import com.rooster.vethome.domain.repository.VetConsultationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VetConsultationRepositoryImpl @Inject constructor(
    private val remoteDataSource: VetConsultationRemoteDataSource
    // TODO: Add localDataSource for caching if applicable
) : VetConsultationRepository {
    override fun getConsultationQueue(vetId: String): Flow<List<ConsultationQueueItem>> {
        return remoteDataSource.getConsultationQueue(vetId)
    }
}
