package com.example.rooster.core.data.fetcher

import com.example.rooster.core.data.model.VetDashboardSummary

interface VeterinarianDashboardDataFetcher {
    /**
     * Fetches a summary of dashboard data for a veterinarian.
     * @param vetId The ID of the veterinarian.
     * @return Result containing VetDashboardSummary on success, or an exception on failure.
     */
    suspend fun getDashboardSummary(vetId: String): Result<VetDashboardSummary>

    // Potential future methods:
    // suspend fun getConsultationDetails(requestId: String): Result<ConsultationDetails>
    // suspend fun updateAppointmentStatus(appointmentId: String, status: String): Result<Unit>
}
