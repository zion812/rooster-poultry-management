package com.rooster.vethome.domain.repository

import com.rooster.vethome.domain.model.VetHealthAlert
import kotlinx.coroutines.flow.Flow

interface VetHealthAlertRepository {
    fun getActiveHealthAlerts(vetId: String, count: Int = 5): Flow<List<VetHealthAlert>>
    // suspend fun acknowledgeAlert(alertId: String): Result<Unit>
}
