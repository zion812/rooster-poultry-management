package com.rooster.vethome.data.source

import com.rooster.vethome.domain.model.PatientHistorySummary
import kotlinx.coroutines.flow.Flow

interface VetPatientRemoteDataSource {
    fun getRecentPatientSummaries(vetId: String, count: Int): Flow<List<PatientHistorySummary>>
}
