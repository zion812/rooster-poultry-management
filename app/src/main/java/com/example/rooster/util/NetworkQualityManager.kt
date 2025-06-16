package com.example.rooster.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import com.example.rooster.NetworkQualityLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Production-grade network quality detection with real-time monitoring and bandwidth estimation.
 * Optimized for rural connectivity scenarios with 2G/3G networks.
 */
class NetworkQualityManager(private val context: Context) {
    private val _networkQuality = MutableStateFlow(NetworkQualityLevel.FAIR)
    val networkQuality: StateFlow<NetworkQualityLevel> = _networkQuality.asStateFlow()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        startNetworkMonitoring()
    }

    fun getCurrentNetworkQuality(): NetworkQualityLevel {
        return try {
            val result =
                kotlin.runCatching {
                    getCurrentNetworkQualityModern()
                }

            val quality = result.getOrElse { NetworkQualityLevel.FAIR }
            _networkQuality.value = quality
            quality
        } catch (e: Exception) {
            android.util.Log.w(
                "NetworkQualityManager",
                "Network quality check failed: ${e.message}",
            )
            NetworkQualityLevel.FAIR
        }
    }

    private fun getCurrentNetworkQualityModern(): NetworkQualityLevel {
        val network = connectivityManager.activeNetwork ?: return NetworkQualityLevel.OFFLINE
        val capabilities =
            connectivityManager.getNetworkCapabilities(network)
                ?: return NetworkQualityLevel.OFFLINE

        return when {
            !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> NetworkQualityLevel.OFFLINE
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                // WiFi connection - check signal strength if available
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val signalStrength = capabilities.signalStrength
                    when {
                        signalStrength >= -50 -> NetworkQualityLevel.EXCELLENT
                        signalStrength >= -70 -> NetworkQualityLevel.GOOD
                        else -> NetworkQualityLevel.FAIR
                    }
                } else {
                    NetworkQualityLevel.GOOD // Default for WiFi
                }
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                analyzeCellularQuality(capabilities)
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkQualityLevel.EXCELLENT
            else -> NetworkQualityLevel.FAIR
        }
    }

    private fun getCurrentNetworkQualityLegacy(): NetworkQualityLevel {
        val activeNetwork = connectivityManager.activeNetworkInfo

        return when {
            activeNetwork == null || !activeNetwork.isConnected -> NetworkQualityLevel.OFFLINE
            activeNetwork.type == ConnectivityManager.TYPE_WIFI -> {
                if (activeNetwork.isRoaming) NetworkQualityLevel.FAIR else NetworkQualityLevel.GOOD
            }

            activeNetwork.type == ConnectivityManager.TYPE_MOBILE -> {
                // Removed network type analysis that requires telephony permissions
                NetworkQualityLevel.FAIR
            }

            else -> NetworkQualityLevel.FAIR
        }
    }

    private fun analyzeCellularQuality(capabilities: NetworkCapabilities): NetworkQualityLevel {
        // Check bandwidth if available (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val downlink = capabilities.linkDownstreamBandwidthKbps
            val uplink = capabilities.linkUpstreamBandwidthKbps

            return when {
                downlink >= 25000 && uplink >= 3000 -> NetworkQualityLevel.EXCELLENT // 25+ Mbps down, 3+ Mbps up
                downlink >= 5000 && uplink >= 1000 -> NetworkQualityLevel.GOOD // 5+ Mbps down, 1+ Mbps up
                downlink >= 1000 && uplink >= 500 -> NetworkQualityLevel.FAIR // 1+ Mbps down, 500+ Kbps up
                downlink >= 256 && uplink >= 128 -> NetworkQualityLevel.POOR // 256+ Kbps down, 128+ Kbps up
                else -> NetworkQualityLevel.POOR
            }
        }

        // Removed network type analysis that requires telephony permissions
        return NetworkQualityLevel.FAIR
    }

    private fun startNetworkMonitoring() {
        networkCallback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    getCurrentNetworkQuality()
                }

                override fun onLost(network: Network) {
                    _networkQuality.value = NetworkQualityLevel.OFFLINE
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities,
                ) {
                    getCurrentNetworkQuality()
                }
            }

        val request =
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

        networkCallback?.let { connectivityManager.registerNetworkCallback(request, it) }
    }

    fun stopNetworkMonitoring() {
        networkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        networkCallback = null
    }

    /**
     * Estimates data usage for different content types based on network quality
     */
    fun estimateDataUsage(
        contentType: String,
        networkQuality: NetworkQualityLevel,
    ): Long {
        val compressionRatio =
            when (networkQuality) {
                NetworkQualityLevel.EXCELLENT -> 1.0
                NetworkQualityLevel.GOOD -> 0.8
                NetworkQualityLevel.FAIR -> 0.6
                NetworkQualityLevel.POOR -> 0.4
                NetworkQualityLevel.OFFLINE -> 0.0
            }

        val baseSize =
            when (contentType.lowercase()) {
                "image" -> 500_000L // 500KB
                "video" -> 5_000_000L // 5MB
                "audio" -> 3_000_000L // 3MB
                "text" -> 50_000L // 50KB
                else -> 100_000L // 100KB
            }

        return (baseSize * compressionRatio).toLong()
    }

    /**
     * Checks if current network is suitable for data-intensive operations
     */
    fun isNetworkSuitableForUpload(): Boolean {
        val current = getCurrentNetworkQuality()
        return current != NetworkQualityLevel.OFFLINE && current != NetworkQualityLevel.POOR
    }

    /**
     * Gets recommended timeout for network operations based on quality
     */
    fun getRecommendedTimeout(): Long {
        return when (getCurrentNetworkQuality()) {
            NetworkQualityLevel.EXCELLENT -> 15_000L // 15 seconds
            NetworkQualityLevel.GOOD -> 30_000L // 30 seconds
            NetworkQualityLevel.FAIR -> 45_000L // 45 seconds
            NetworkQualityLevel.POOR -> 60_000L // 60 seconds
            NetworkQualityLevel.OFFLINE -> 10_000L // 10 seconds (for cache)
        }
    }
}
