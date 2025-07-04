package com.rooster.vethome.domain.repository

import com.rooster.vethome.domain.model.PatientHistorySummary
import kotlinx.coroutines.flow.Flow

interface VetPatientRepository {
    fun getRecentPatientSummaries(vetId: String, count: Int = 5): Flow<List<PatientHistorySummary>>
    // fun searchPatients(vetId: String, query: String): Flow<List<PatientHistorySummary>>
    // fun getFullPatientHistory(patientId: String): Flow<FullPatientHistory?> // More detailed model
}
