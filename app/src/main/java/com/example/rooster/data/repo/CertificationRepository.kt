package com.example.rooster.data.repo

import com.example.rooster.models.CertificationRequest

object CertificationRepository {
    suspend fun submitKYC(
        farmerId: String,
        docs: List<String>,
    ) {
        // Stub implementation
    }

    suspend fun getRequests(farmerId: String): List<CertificationRequest> {
        return emptyList()
    }
}
