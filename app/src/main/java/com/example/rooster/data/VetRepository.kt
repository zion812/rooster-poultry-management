package com.example.rooster.data

import com.example.rooster.models.VetConsultationRequest
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap

/**
 * Stub repository for veterinary consultation requests.
 */
object VetRepository {
    // requestId -> request
    private val records = ConcurrentHashMap<String, VetConsultationRequest>()

    /** Submit a new vet consultation request */
    suspend fun submitRequest(request: VetConsultationRequest): VetConsultationRequest {
        delay(200)
        records[request.id] = request
        return request
    }

    /** Fetch all requests */
    suspend fun fetchRequests(): List<VetConsultationRequest> {
        delay(200)
        return records.values.toList()
    }
}
