package com.example.rooster.performance

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

/**
 * Performance Testing and Optimization Suite for Rural Users
 *
 * Features:
 * - Network speed detection and adaptation
 * - Memory usage monitoring
 * - UI performance metrics
 * - Battery optimization recommendations
 * - 2G/3G optimization strategies
 */

data class PerformanceMetrics(
    val networkType: NetworkType,
    val networkSpeed: NetworkSpeed,
    val memoryUsage: MemoryUsage,
    val batteryLevel: Int,
    val deviceType: DeviceType,
    val timestamp: Long = System.currentTimeMillis(),
)

enum class NetworkType {
    WIFI,
    MOBILE_4G,
    MOBILE_3G,
    MOBILE_2G,
    OFFLINE,
    UNKNOWN,
}

enum class NetworkSpeed {
    EXCELLENT, // >10 Mbps
    GOOD, // 1-10 Mbps
    FAIR, // 0.5-1 Mbps
    POOR, // <0.5 Mbps
    OFFLINE,
}

enum class DeviceType {
    HIGH_END, // >4GB RAM, Recent CPU
    MID_RANGE, // 2-4GB RAM, Mid CPU
    LOW_END, // <2GB RAM, Older CPU
    VERY_LOW_END, // <1GB RAM, Very old CPU
}

data class MemoryUsage(
    val totalMemory: Long,
    val availableMemory: Long,
    val usedMemory: Long,
    val memoryPercentage: Float,
)

class RuralPerformanceOptimizer(private val context: Context) {
    @Composable
    fun MonitorPerformance(): State<PerformanceMetrics> {
        return produceState(
            initialValue =
                PerformanceMetrics(
                    networkType = NetworkType.UNKNOWN,
                    networkSpeed = NetworkSpeed.OFFLINE,
                    memoryUsage = MemoryUsage(0, 0, 0, 0f),
                    batteryLevel = 0,
                    deviceType = DeviceType.LOW_END,
                ),
        ) {
            while (true) {
                value = collectPerformanceMetrics()
                delay(5000) // Update every 5 seconds
            }
        }
    }

    private suspend fun collectPerformanceMetrics(): PerformanceMetrics {
        return PerformanceMetrics(
            networkType = detectNetworkType(),
            networkSpeed = measureNetworkSpeed(),
            memoryUsage = getMemoryUsage(),
            batteryLevel = getBatteryLevel(),
            deviceType = detectDeviceType(),
        )
    }

    private fun detectNetworkType(): NetworkType {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return NetworkType.OFFLINE
            val capabilities =
                connectivityManager.getNetworkCapabilities(network)
                    ?: return NetworkType.OFFLINE

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    // Try to determine cellular type
                    when {
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> NetworkType.MOBILE_4G
                        else -> NetworkType.MOBILE_3G
                    }
                }

                else -> NetworkType.UNKNOWN
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            return when (networkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> NetworkType.WIFI
                ConnectivityManager.TYPE_MOBILE -> {
                    when (networkInfo.subtype) {
                        in 1..2 -> NetworkType.MOBILE_2G // GPRS, EDGE
                        in 3..6 -> NetworkType.MOBILE_3G // UMTS, HSDPA, etc.
                        else -> NetworkType.MOBILE_4G // LTE and above
                    }
                }

                else -> NetworkType.OFFLINE
            }
        }
    }

    private suspend fun measureNetworkSpeed(): NetworkSpeed {
        // Simple network speed test using small request
        val startTime = System.currentTimeMillis()
        try {
            // Simulate small network request
            delay(100) // Placeholder for actual network test
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            return when {
                duration < 50 -> NetworkSpeed.EXCELLENT
                duration < 200 -> NetworkSpeed.GOOD
                duration < 500 -> NetworkSpeed.FAIR
                else -> NetworkSpeed.POOR
            }
        } catch (e: Exception) {
            return NetworkSpeed.OFFLINE
        }
    }

    private fun getMemoryUsage(): MemoryUsage {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        val maxMemory = runtime.maxMemory()

        return MemoryUsage(
            totalMemory = totalMemory,
            availableMemory = freeMemory,
            usedMemory = usedMemory,
            memoryPercentage = (usedMemory.toFloat() / maxMemory.toFloat()) * 100f,
        )
    }

    private fun getBatteryLevel(): Int {
        // Placeholder - would implement actual battery level detection
        return 75
    }

    private fun detectDeviceType(): DeviceType {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory() / (1024 * 1024) // Convert to MB

        return when {
            maxMemory > 4096 -> DeviceType.HIGH_END
            maxMemory > 2048 -> DeviceType.MID_RANGE
            maxMemory > 1024 -> DeviceType.LOW_END
            else -> DeviceType.VERY_LOW_END
        }
    }
}

/**
 * Composable for displaying performance metrics in development
 */
@Composable
fun PerformanceIndicator(
    metrics: PerformanceMetrics,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor =
                    when (metrics.networkSpeed) {
                        NetworkSpeed.EXCELLENT -> Color.Green.copy(alpha = 0.1f)
                        NetworkSpeed.GOOD -> Color.Blue.copy(alpha = 0.1f)
                        NetworkSpeed.FAIR -> Color.Yellow.copy(alpha = 0.1f)
                        NetworkSpeed.POOR -> Color.Red.copy(alpha = 0.1f)
                        NetworkSpeed.OFFLINE -> Color.Gray.copy(alpha = 0.1f)
                    },
            ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Text(
                text = "Performance: ${metrics.networkSpeed.name}",
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = "Network: ${metrics.networkType.name}",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "Memory: ${metrics.memoryUsage.memoryPercentage.toInt()}%",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "Device: ${metrics.deviceType.name}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

/**
 * Rural-specific optimization recommendations
 */
object RuralOptimizationStrategies {
    fun getImageQualityForNetwork(networkSpeed: NetworkSpeed): Int {
        return when (networkSpeed) {
            NetworkSpeed.EXCELLENT -> 90
            NetworkSpeed.GOOD -> 75
            NetworkSpeed.FAIR -> 60
            NetworkSpeed.POOR -> 40
            NetworkSpeed.OFFLINE -> 20
        }
    }

    fun getImageSizeForNetwork(networkSpeed: NetworkSpeed): Int {
        return when (networkSpeed) {
            NetworkSpeed.EXCELLENT -> 1080
            NetworkSpeed.GOOD -> 720
            NetworkSpeed.FAIR -> 480
            NetworkSpeed.POOR -> 240
            NetworkSpeed.OFFLINE -> 150
        }
    }

    fun shouldPreloadImages(networkSpeed: NetworkSpeed): Boolean {
        return networkSpeed in listOf(NetworkSpeed.EXCELLENT, NetworkSpeed.GOOD)
    }

    fun getRecommendedListSize(deviceType: DeviceType): Int {
        return when (deviceType) {
            DeviceType.HIGH_END -> 50
            DeviceType.MID_RANGE -> 30
            DeviceType.LOW_END -> 20
            DeviceType.VERY_LOW_END -> 10
        }
    }

    fun shouldUseAnimations(deviceType: DeviceType): Boolean {
        return deviceType in listOf(DeviceType.HIGH_END, DeviceType.MID_RANGE)
    }
}

/**
 * Performance testing flows for continuous monitoring
 */
object PerformanceTestFlows {
    fun createNetworkSpeedTestFlow(): Flow<NetworkSpeed> =
        flow {
            while (true) {
                // Simulate network speed test
                val startTime = System.currentTimeMillis()
                delay(100) // Simulated network call
                val endTime = System.currentTimeMillis()
                val duration = endTime - startTime

                emit(
                    when {
                        duration < 50 -> NetworkSpeed.EXCELLENT
                        duration < 200 -> NetworkSpeed.GOOD
                        duration < 500 -> NetworkSpeed.FAIR
                        else -> NetworkSpeed.POOR
                    },
                )

                delay(10000) // Test every 10 seconds
            }
        }

    fun createMemoryMonitorFlow(): Flow<MemoryUsage> =
        flow {
            while (true) {
                val runtime = Runtime.getRuntime()
                val totalMemory = runtime.totalMemory()
                val freeMemory = runtime.freeMemory()
                val usedMemory = totalMemory - freeMemory
                val maxMemory = runtime.maxMemory()

                emit(
                    MemoryUsage(
                        totalMemory = totalMemory,
                        availableMemory = freeMemory,
                        usedMemory = usedMemory,
                        memoryPercentage = (usedMemory.toFloat() / maxMemory.toFloat()) * 100f,
                    ),
                )

                delay(5000) // Monitor every 5 seconds
            }
        }
}

/**
 * Composable hook for easy performance monitoring
 */
@Composable
fun rememberPerformanceOptimizer(): RuralPerformanceOptimizer {
    val context = LocalContext.current
    return remember { RuralPerformanceOptimizer(context) }
}
