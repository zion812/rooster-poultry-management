package com.example.rooster.data

import com.example.rooster.models.CertificationRequest
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap

/**
 * Stub repository for certification requests. Replace with real backend.
 */
object CertificationRepository {
    private val requests = ConcurrentHashMap<String, MutableList<CertificationRequest>>()

    /** Submit a new KYC certification request for a farmer. */
    suspend fun submitKYC(
        farmerId: String,
        docs: List<String>,
    ) {
        delay(300)
        val req = CertificationRequest(farmerId = farmerId, docs = docs)
        requests.computeIfAbsent(farmerId) { mutableListOf() }.add(req)
    }

    /** Fetch all KYC requests for a farmer. */
    suspend fun getRequests(farmerId: String): List<CertificationRequest> {
        delay(300)
        return requests[farmerId]?.toList() ?: emptyList()
    }
}
