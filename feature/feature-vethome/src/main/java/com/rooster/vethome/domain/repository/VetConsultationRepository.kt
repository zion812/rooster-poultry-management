package com.rooster.vethome.domain.repository

import com.rooster.vethome.domain.model.ConsultationQueueItem
import kotlinx.coroutines.flow.Flow

interface VetConsultationRepository {
    fun getConsultationQueue(vetId: String): Flow<List<ConsultationQueueItem>>
    // suspend fun acceptConsultation(consultationId: String): Result<Unit>
    // suspend fun completeConsultation(consultationId: String, notes: String): Result<Unit>
}
