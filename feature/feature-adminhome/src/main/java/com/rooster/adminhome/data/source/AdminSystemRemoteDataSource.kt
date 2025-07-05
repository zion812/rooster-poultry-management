package com.rooster.adminhome.data.source

import com.rooster.adminhome.domain.model.SystemMetric
import kotlinx.coroutines.flow.Flow

interface AdminSystemRemoteDataSource {
    fun getCurrentSystemMetrics(): Flow<List<SystemMetric>>
}
