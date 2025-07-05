package com.rooster.vethome.data.source

import com.rooster.vethome.domain.model.ConsultationQueueItem
import kotlinx.coroutines.flow.Flow

interface VetConsultationRemoteDataSource {
    fun getConsultationQueue(vetId: String): Flow<List<ConsultationQueueItem>>
}
