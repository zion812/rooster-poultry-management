package com.example.rooster.core.common.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {
    fun observeNetworkStatus(): Flow<NetworkStatus>
}

enum class NetworkStatus {
    Available, Unavailable, Losing, Lost
}
