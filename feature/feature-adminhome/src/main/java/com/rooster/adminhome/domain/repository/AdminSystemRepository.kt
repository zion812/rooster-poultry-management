package com.rooster.adminhome.domain.repository

import com.rooster.adminhome.domain.model.SystemMetric
import kotlinx.coroutines.flow.Flow

interface AdminSystemRepository {
    fun getCurrentSystemMetrics(): Flow<List<SystemMetric>>
}
