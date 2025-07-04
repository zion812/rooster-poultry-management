package com.rooster.adminhome.data.source

import com.rooster.adminhome.domain.model.SystemMetric
import com.rooster.adminhome.domain.model.SystemStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject
import kotlin.random.Random

class MockAdminSystemRemoteDataSource @Inject constructor() : AdminSystemRemoteDataSource {
    override fun getCurrentSystemMetrics(): Flow<List<SystemMetric>> = flow {
        delay(500) // Simulate delay
        val metrics = listOf(
            SystemMetric(
                id = "api_latency",
                name = "API Gateway Latency",
                value = "${Random.nextInt(50, 300)} ms",
                status = if (Random.nextInt(10) > 1) SystemStatus.OPERATIONAL else SystemStatus.DEGRADED,
                lastUpdated = Date()
            ),
            SystemMetric(
                id = "db_connections",
                name = "Database Connections",
                value = "${Random.nextInt(20, 85)}/100",
                status = SystemStatus.OPERATIONAL,
                lastUpdated = Date(System.currentTimeMillis() - 60000) // 1 min ago
            ),
            SystemMetric(
                id = "server_cpu",
                name = "Main Server CPU Usage",
                value = "${Random.nextInt(10, 75)}%",
                status = SystemStatus.OPERATIONAL,
                lastUpdated = Date()
            ),
            SystemMetric(
                id = "payment_gateway",
                name = "Payment Gateway Status",
                value = "Online",
                status = if (Random.nextInt(20) > 0) SystemStatus.OPERATIONAL else SystemStatus.MAINTENANCE,
                lastUpdated = Date(System.currentTimeMillis() - 300000) // 5 mins ago
            )
        )
        emit(metrics)
    }
}
