package com.rooster.vethome.data.source

import com.rooster.vethome.domain.model.VetHealthAlert
import kotlinx.coroutines.flow.Flow

interface VetHealthAlertRemoteDataSource {
    fun getActiveHealthAlerts(vetId: String, count: Int): Flow<List<VetHealthAlert>>
}
