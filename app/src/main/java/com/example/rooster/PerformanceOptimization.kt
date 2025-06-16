package com.example.rooster

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

// Enhanced Performance Monitor with 2G optimizations
class PerformanceMonitor {
    companion object {
        private var memoryUsage = mutableMapOf<String, Long>()
        private var networkUsage = mutableMapOf<String, Long>()
        private var loadTimes = mutableMapOf<String, Long>()
        private var dataUsageToday = 0L
        private var offlineEventCount = 0
        private var compressionSavings = 0L

        fun startTimer(operation: String): Long {
            return System.currentTimeMillis()
        }

        fun endTimer(
            operation: String,
            startTime: Long,
        ) {
            val endTime = System.currentTimeMillis()
            loadTimes[operation] = endTime - startTime
        }

        fun recordMemoryUsage(
            operation: String,
            bytes: Long,
        ) {
            memoryUsage[operation] = bytes
        }

        fun recordNetworkUsage(
            operation: String,
            bytes: Long,
        ) {
            networkUsage[operation] = (networkUsage[operation] ?: 0) + bytes
            dataUsageToday += bytes
        }

        fun recordOfflineEvent() {
            offlineEventCount++
        }

        fun recordCompressionSaving(savedBytes: Long) {
            compressionSavings += savedBytes
        }

        fun getDataUsageToday(): Long = dataUsageToday

        fun getCompressionSavings(): Long = compressionSavings

        fun getRuralOptimizationStats(): Map<String, Any> {
            return mapOf(
                "dataUsageToday" to dataUsageToday,
                "offlineEvents" to offlineEventCount,
                "compressionSavings" to compressionSavings,
                "avgLoadTime" to loadTimes.values.average(),
                "totalNetworkUsage" to networkUsage.values.sum(),
            )
        }

        fun getMetrics(): Map<String, Any> {
            return mapOf(
                "loadTimes" to loadTimes.toMap(),
                "memoryUsage" to memoryUsage.toMap(),
                "networkUsage" to networkUsage.toMap(),
                "ruralStats" to getRuralOptimizationStats(),
            )
        }

        fun clearMetrics() {
            memoryUsage.clear()
            networkUsage.clear()
            loadTimes.clear()
        }

        fun resetDailyUsage() {
            dataUsageToday = 0L
            offlineEventCount = 0
        }
    }
}

// Network Quality Manager
class NetworkQualityManager(private val context: Context) {
    fun getCurrentNetworkQuality(): NetworkQualityLevel {
        return try {
            // Use runCatching to safely handle system connectivity calls
            val result =
                kotlin.runCatching {
                    val connectivityManager =
                        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                    // Try new API first (API 23+)
                    val network =
                        connectivityManager.activeNetwork
                            ?: return@runCatching NetworkQualityLevel.OFFLINE
                    val networkCapabilities =
                        connectivityManager.getNetworkCapabilities(network)
                            ?: return@runCatching NetworkQualityLevel.OFFLINE

                    when {
                        networkCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) -> {
                            if (networkCapabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                                NetworkQualityLevel.GOOD
                            } else {
                                NetworkQualityLevel.FAIR
                            }
                        }

                        networkCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            // Estimate quality based on bandwidth
                            val downstreamBandwidth =
                                networkCapabilities.linkDownstreamBandwidthKbps
                            when {
                                downstreamBandwidth > 5000 -> NetworkQualityLevel.EXCELLENT // 5+ Mbps
                                downstreamBandwidth > 1000 -> NetworkQualityLevel.GOOD // 1+ Mbps
                                downstreamBandwidth > 256 -> NetworkQualityLevel.FAIR // 256+ Kbps
                                else -> NetworkQualityLevel.POOR
                            }
                        }

                        else -> NetworkQualityLevel.FAIR
                    }
                }

            // If new API fails, try fallback or return safe default
            result.getOrElse {
                // Fallback for older devices or system issues
                getLegacyNetworkQuality(context)
            }
        } catch (e: Exception) {
            android.util.Log.w("NetworkQuality", "Network quality check failed: ${e.message}")
            NetworkQualityLevel.FAIR // Safe fallback for system connectivity issues
        }
    }

    fun getOptimalCompressionLevel(): ImageCompressionLevel {
        return when (getCurrentNetworkQuality()) {
            NetworkQualityLevel.EXCELLENT -> ImageCompressionLevel.LOW
            NetworkQualityLevel.GOOD -> ImageCompressionLevel.MEDIUM
            NetworkQualityLevel.FAIR -> ImageCompressionLevel.HIGH
            NetworkQualityLevel.POOR, NetworkQualityLevel.OFFLINE -> ImageCompressionLevel.ULTRA
        }
    }

    private fun getLegacyNetworkQuality(context: Context): NetworkQualityLevel {
        return try {
            // Wrapped legacy network check with additional safety
            kotlin.runCatching {
                val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

                when {
                    activeNetwork == null || !activeNetwork.isConnectedOrConnecting -> NetworkQualityLevel.OFFLINE
                    activeNetwork.type == ConnectivityManager.TYPE_WIFI -> NetworkQualityLevel.GOOD
                    activeNetwork.type == ConnectivityManager.TYPE_MOBILE -> {
                        when (activeNetwork.subtype) {
                            android.telephony.TelephonyManager.NETWORK_TYPE_LTE -> NetworkQualityLevel.EXCELLENT
                            android.telephony.TelephonyManager.NETWORK_TYPE_HSPAP,
                            android.telephony.TelephonyManager.NETWORK_TYPE_HSPA,
                            -> NetworkQualityLevel.GOOD

                            android.telephony.TelephonyManager.NETWORK_TYPE_UMTS -> NetworkQualityLevel.FAIR
                            else -> NetworkQualityLevel.POOR
                        }
                    }

                    else -> NetworkQualityLevel.FAIR
                }
            }.getOrElse { NetworkQualityLevel.FAIR }
        } catch (e: Exception) {
            android.util.Log.w("NetworkQuality", "Legacy network check failed: ${e.message}")
            NetworkQualityLevel.FAIR // Safe fallback
        }
    }
}

// Advanced 2G Network Optimizer
class Advanced2GOptimizer(private val context: Context) {
    private val networkQualityManager = NetworkQualityManager(context)
    private val dataUsageThreshold = 5 * 1024 * 1024L // 5MB daily limit for 2G users
    private val prefetchQueue = mutableListOf<String>()

    fun shouldLoadContent(): Boolean {
        val todayUsage = PerformanceMonitor.getDataUsageToday()
        return todayUsage < dataUsageThreshold
    }

    fun getOptimizedQueryLimit(): Int {
        val networkQuality = networkQualityManager.getCurrentNetworkQuality()
        val todayUsage = PerformanceMonitor.getDataUsageToday()
        val usagePercentage = (todayUsage.toFloat() / dataUsageThreshold) * 100

        return when {
            networkQuality == NetworkQualityLevel.OFFLINE -> 0
            networkQuality == NetworkQualityLevel.POOR && usagePercentage > 80 -> 3
            networkQuality == NetworkQualityLevel.POOR -> 5
            networkQuality == NetworkQualityLevel.FAIR && usagePercentage > 60 -> 8
            networkQuality == NetworkQualityLevel.FAIR -> 15
            networkQuality == NetworkQualityLevel.GOOD -> 25
            else -> 50
        }
    }

    fun getSmartCompressionLevel(): ImageCompressionLevel {
        val networkQuality = networkQualityManager.getCurrentNetworkQuality()
        val todayUsage = PerformanceMonitor.getDataUsageToday()
        val usagePercentage = (todayUsage.toFloat() / dataUsageThreshold) * 100

        return when {
            usagePercentage > 90 -> ImageCompressionLevel.ULTRA // Emergency compression
            networkQuality == NetworkQualityLevel.POOR -> ImageCompressionLevel.ULTRA
            networkQuality == NetworkQualityLevel.FAIR && usagePercentage > 50 -> ImageCompressionLevel.HIGH
            networkQuality == NetworkQualityLevel.FAIR -> ImageCompressionLevel.HIGH
            else -> ImageCompressionLevel.MEDIUM
        }
    }

    suspend fun smartPrefetch(criticalUrls: List<String>) {
        if (!shouldLoadContent()) return

        val networkQuality = networkQualityManager.getCurrentNetworkQuality()
        if (networkQuality in listOf(NetworkQualityLevel.POOR, NetworkQualityLevel.OFFLINE)) {
            return // Skip prefetching on poor networks
        }

        // Prefetch only during good network conditions
        val prefetchLimit =
            when (networkQuality) {
                NetworkQualityLevel.EXCELLENT -> 10
                NetworkQualityLevel.GOOD -> 5
                else -> 2
            }

        criticalUrls.take(prefetchLimit).forEach { url ->
            try {
                // Add to prefetch queue for background loading
                if (!prefetchQueue.contains(url)) {
                    prefetchQueue.add(url)
                }
            } catch (e: Exception) {
                // Silent fail for prefetch
            }
        }
    }

    fun getDataUsageWarning(): String? {
        val todayUsage = PerformanceMonitor.getDataUsageToday()
        val usagePercentage = (todayUsage.toFloat() / dataUsageThreshold) * 100

        return when {
            usagePercentage > 90 -> "Data limit almost reached! Only essential features available."
            usagePercentage > 75 -> "High data usage today. Consider using offline mode."
            usagePercentage > 50 -> "Moderate data usage. Images will be compressed more."
            else -> null
        }
    }

    fun enableUltraLowBandwidthMode(): Map<String, Any> {
        return mapOf(
            "imageQuality" to 20,
            "textOnly" to true,
            "maxQueryLimit" to 3,
            "cacheEverything" to true,
            "disablePrefetch" to true,
            "compressText" to true,
        )
    }
}

// Image Compression Utility Function
internal fun getCompressedImageBytes(
    originalBitmap: Bitmap,
    compressionLevel: ImageCompressionLevel,
): ByteArray {
    val quality = compressionLevel.quality
    val maxDimension = compressionLevel.maxDimension

    // Resize if necessary
    val resizedBitmap =
        if (originalBitmap.width > maxDimension || originalBitmap.height > maxDimension) {
            val ratio =
                minOf(
                    maxDimension.toFloat() / originalBitmap.width,
                    maxDimension.toFloat() / originalBitmap.height,
                )
            val newWidth = (originalBitmap.width * ratio).toInt()
            val newHeight = (originalBitmap.height * ratio).toInt()
            Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        } else {
            originalBitmap
        }

    val outputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    val byteArray = outputStream.toByteArray()

    // Clean up bitmaps if they were modified (resizedBitmap might be different from originalBitmap)
    if (resizedBitmap != originalBitmap) {
        resizedBitmap.recycle()
    }
    // It's generally safer for the caller to handle recycling of the originalBitmap
    // if it's loaded and passed into this utility, especially if it might be used elsewhere.
    // However, if this utility is always the last consumer of a freshly decoded bitmap that won't be reused,
    // originalBitmap.recycle() could be called here too.
    // For now, let the caller manage originalBitmap's lifecycle outside this utility.

    return byteArray
}

// Optimized Image Manager
class OptimizedImageManager(private val context: Context) {
    private val imageCache = ConcurrentHashMap<String, WeakReference<ImageBitmap>>()
    private val networkQualityManager = NetworkQualityManager(context)
    private val cacheDir = File(context.cacheDir, "optimized_images")

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    suspend fun loadOptimizedImage(
        url: String,
        compressionLevel: ImageCompressionLevel? = null,
    ): ImageBitmap? =
        withContext(Dispatchers.IO) {
            val startTime = PerformanceMonitor.startTimer("image_load_$url")

            try {
                imageCache[url]?.get()?.let {
                    PerformanceMonitor.endTimer("image_load_$url", startTime)
                    return@withContext it
                }

                val actualCompressionLevel =
                    compressionLevel ?: networkQualityManager.getOptimalCompressionLevel()
                val cacheKey = "${url.hashCode()}_${actualCompressionLevel.name}"
                val cacheFile = File(cacheDir, "$cacheKey.jpg")

                if (cacheFile.exists()) {
                    BitmapFactory.decodeFile(cacheFile.absolutePath)?.let {
                        val imageBitmap = it.asImageBitmap()
                        imageCache[url] = WeakReference(imageBitmap)
                        PerformanceMonitor.recordMemoryUsage(
                            "image_cache_load_disk",
                            it.byteCount.toLong(),
                        )
                        PerformanceMonitor.endTimer("image_load_$url", startTime)
                        it.recycle() // Recycle bitmap after converting to ImageBitmap and caching
                        return@withContext imageBitmap
                    }
                }

                downloadImage(url)?.let { originalBitmap ->
                    val compressedBytes =
                        getCompressedImageBytes(originalBitmap, actualCompressionLevel)
                    // originalBitmap is not recycled by getCompressedImageBytes by default, so recycle it here
                    originalBitmap.recycle()

                    cacheFile.outputStream().use { it.write(compressedBytes) }
                    PerformanceMonitor.recordNetworkUsage(
                        "image_save_disk_cache",
                        compressedBytes.size.toLong(),
                    )

                    // Now load the just-saved compressed bytes into an ImageBitmap for the cache
                    BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)?.let {
                        val imageBitmap = it.asImageBitmap()
                        imageCache[url] = WeakReference(imageBitmap)
                        PerformanceMonitor.recordMemoryUsage(
                            "image_cache_save_memory",
                            it.byteCount.toLong(),
                        )
                        it.recycle()
                        PerformanceMonitor.endTimer("image_load_$url", startTime)
                        return@withContext imageBitmap
                    }
                }
            } catch (e: Exception) {
                PerformanceMonitor.endTimer("image_load_$url", startTime)
                // Log error e
            }
            null
        }

    private suspend fun downloadImage(url: String): Bitmap? =
        withContext(Dispatchers.IO) {
            try {
                val inputStream = java.net.URL(url).openConnection().getInputStream()
                // It's important to use a buffered input stream for efficiency with decodeStream
                // val bufferedInputStream = java.io.BufferedInputStream(inputStream)
                val bitmap = BitmapFactory.decodeStream(inputStream) // Using inputStream directly
                inputStream.close()
                // bufferedInputStream.close() if used

                bitmap?.let {
                    PerformanceMonitor.recordNetworkUsage("image_download", it.byteCount.toLong())
                }
                bitmap
            } catch (e: Exception) {
                // Log error e
                null
            }
        }

    // This private compressImage is no longer needed if getCompressedImageBytes is used internally.
    // For now, keeping it as it was, but it's a candidate for removal/refactor.
    private fun compressImage(
        bitmap: Bitmap,
        compression: ImageCompressionLevel,
    ): Bitmap {
        val maxDimension = compression.maxDimension
        val resized =
            if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                val ratio =
                    minOf(
                        maxDimension.toFloat() / bitmap.width,
                        maxDimension.toFloat() / bitmap.height,
                    )
                val newWidth = (bitmap.width * ratio).toInt()
                val newHeight = (bitmap.height * ratio).toInt()
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }
        return resized
    }

    fun clearCache() {
        imageCache.clear()
        cacheDir.listFiles()
            ?.forEach { it.deleteRecursively() } // Ensure recursive delete for directories
    }
}

// Optimized Parse Query Manager
class OptimizedParseQueryManager(private val context: Context) {
    private val networkQualityManager = NetworkQualityManager(context)

    fun <T : ParseObject> createOptimizedQuery(className: String): ParseQuery<T> {
        val query = ParseQuery.getQuery<T>(className)
        val networkQuality = networkQualityManager.getCurrentNetworkQuality()

        // Adjust query parameters based on network quality
        when (networkQuality) {
            NetworkQualityLevel.EXCELLENT -> {
                query.limit = 50
                query.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
            }

            NetworkQualityLevel.GOOD -> {
                query.limit = 30
                query.cachePolicy = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
            }

            NetworkQualityLevel.FAIR -> {
                query.limit = 20
                query.cachePolicy = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                query.setMaxCacheAge(300000) // 5 minutes
            }

            NetworkQualityLevel.POOR -> {
                query.limit = 10
                query.cachePolicy = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                query.setMaxCacheAge(600000) // 10 minutes
            }

            NetworkQualityLevel.OFFLINE -> {
                query.limit = 10
                query.cachePolicy = ParseQuery.CachePolicy.CACHE_ONLY
            }
        }

        // Enable local datastore for offline capability
        query.fromLocalDatastore()

        return query
    }

    suspend fun <T : ParseObject> executeOptimizedQuery(
        query: ParseQuery<T>,
        operation: String,
    ): List<T> =
        withContext(Dispatchers.IO) {
            val startTime = PerformanceMonitor.startTimer("query_$operation")

            try {
                val results = query.find()
                PerformanceMonitor.recordNetworkUsage("query_$operation", results.size * 1024L)
                PerformanceMonitor.endTimer("query_$operation", startTime)
                results
            } catch (e: Exception) {
                PerformanceMonitor.endTimer("query_$operation", startTime)
                // Fallback to cache
                try {
                    query.cachePolicy = ParseQuery.CachePolicy.CACHE_ONLY
                    query.find()
                } catch (cacheException: Exception) {
                    emptyList()
                }
            }
        }
}

// Progressive Loading Manager
class ProgressiveLoadingManager {
    data class LoadingState(
        val isLoading: Boolean = false,
        val progress: Float = 0f,
        val error: String? = null,
        val loadedItems: Int = 0,
        val totalItems: Int = 0,
    )

    suspend fun <T> loadProgressively(
        items: List<T>,
        batchSize: Int = 5,
        loadDelay: Long = 100,
        onProgress: (LoadingState) -> Unit,
        loader: suspend (T) -> Unit,
    ) {
        onProgress(LoadingState(isLoading = true, totalItems = items.size))

        items.chunked(batchSize).forEachIndexed { chunkIndex, chunk ->
            try {
                chunk.forEach { item ->
                    loader(item)
                }

                val loadedCount = (chunkIndex + 1) * batchSize
                val actualLoaded = minOf(loadedCount, items.size)
                val progress = actualLoaded.toFloat() / items.size

                onProgress(
                    LoadingState(
                        isLoading = actualLoaded < items.size,
                        progress = progress,
                        loadedItems = actualLoaded,
                        totalItems = items.size,
                    ),
                )

                if (actualLoaded < items.size) {
                    delay(loadDelay)
                }
            } catch (e: Exception) {
                onProgress(
                    LoadingState(
                        isLoading = false,
                        error = e.localizedMessage,
                        loadedItems = chunkIndex * batchSize,
                        totalItems = items.size,
                    ),
                )
                return@loadProgressively
            }
        }
    }
}

// Offline Mode Manager
class OfflineModeManager(private val context: Context) {
    fun enableOfflineMode() {
        com.parse.Parse.enableLocalDatastore(context)
    }

    suspend fun cacheEssentialData() =
        withContext(Dispatchers.IO) {
            try {
                // Cache user's fowl data
                val fowlQuery = ParseQuery.getQuery<ParseObject>("Fowl")
                fowlQuery.whereEqualTo("owner", com.parse.ParseUser.getCurrentUser())
                fowlQuery.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
                val fowlData = fowlQuery.find()
                ParseObject.pinAllInBackground("offline_fowl", fowlData)

                // Cache recent marketplace listings
                val marketplaceQuery = ParseQuery.getQuery<ParseObject>("Listing")
                marketplaceQuery.orderByDescending("createdAt")
                marketplaceQuery.limit = 20
                marketplaceQuery.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
                val marketplaceData = marketplaceQuery.find()
                ParseObject.pinAllInBackground("offline_marketplace", marketplaceData)

                // Cache traditional markets
                val marketsQuery = ParseQuery.getQuery<ParseObject>("TraditionalMarket")
                marketsQuery.whereEqualTo("isActive", true)
                marketsQuery.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
                val marketsData = marketsQuery.find()
                ParseObject.pinAllInBackground("offline_markets", marketsData)
            } catch (e: Exception) {
                // Offline caching failed, but app should still work
            }
        }

    suspend fun getOfflineData(
        className: String,
        pinName: String,
    ): List<ParseObject> =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>(className)
                query.fromPin(pinName)
                query.find()
            } catch (e: Exception) {
                emptyList()
            }
        }

    fun clearOfflineCache() {
        ParseObject.unpinAllInBackground()
    }
}

// Background Sync Manager
class BackgroundSyncManager(private val context: Context) {
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val networkQualityManager = NetworkQualityManager(context)

    fun startPeriodicSync() {
        syncScope.launch {
            while (true) {
                val networkQuality = networkQualityManager.getCurrentNetworkQuality()

                val syncInterval =
                    when (networkQuality) {
                        NetworkQualityLevel.EXCELLENT -> 30_000L // 30 seconds
                        NetworkQualityLevel.GOOD -> 60_000L // 1 minute
                        NetworkQualityLevel.FAIR -> 300_000L // 5 minutes
                        NetworkQualityLevel.POOR -> 600_000L // 10 minutes
                        NetworkQualityLevel.OFFLINE -> 1800_000L // 30 minutes
                    }

                if (networkQuality != NetworkQualityLevel.OFFLINE) {
                    performSync()
                }

                delay(syncInterval)
            }
        }
    }

    private suspend fun performSync() {
        try {
            syncCriticalData()

            if (networkQualityManager.getCurrentNetworkQuality() in
                listOf(
                    NetworkQualityLevel.EXCELLENT,
                    NetworkQualityLevel.GOOD,
                )
            ) {
                syncNonCriticalData()
            }
        } catch (e: Exception) {
            // Log error but continue
        }
    }

    private suspend fun syncCriticalData() {
        val fowlQuery =
            OptimizedParseQueryManager(context)
                .createOptimizedQuery<ParseObject>("Fowl")
        fowlQuery.whereEqualTo("owner", com.parse.ParseUser.getCurrentUser())

        val fowlData =
            OptimizedParseQueryManager(context)
                .executeOptimizedQuery(fowlQuery, "sync_fowl")

        ParseObject.pinAllInBackground("fowl_cache", fowlData)
    }

    private suspend fun syncNonCriticalData() {
        val marketplaceQuery =
            OptimizedParseQueryManager(context)
                .createOptimizedQuery<ParseObject>("Listing")
        marketplaceQuery.orderByDescending("createdAt")

        val marketplaceData =
            OptimizedParseQueryManager(context)
                .executeOptimizedQuery(marketplaceQuery, "sync_marketplace")

        ParseObject.pinAllInBackground("marketplace_cache", marketplaceData)
    }

    fun stopSync() {
        syncScope.cancel()
    }
}

// Composable for network-aware loading
@Composable
fun NetworkAwareLoader(
    context: Context,
    content: @Composable (NetworkQualityLevel) -> Unit,
) {
    val networkQualityManager = remember { NetworkQualityManager(context) }
    var networkQuality by remember { mutableStateOf(NetworkQualityLevel.GOOD) }

    LaunchedEffect(Unit) {
        while (true) {
            networkQuality = networkQualityManager.getCurrentNetworkQuality()
            delay(5000) // Check every 5 seconds
        }
    }

    content(networkQuality)
}

// Rural Data Usage Indicator for Farmers
@Composable
fun RuralDataUsageIndicator(
    context: Context,
    isTeluguMode: Boolean = false,
) {
    val optimizer = remember { Advanced2GOptimizer(context) }
    var dataUsage by remember { mutableStateOf(0L) }
    var warning by remember { mutableStateOf<String?>(null) }
    var networkQuality by remember { mutableStateOf(NetworkQualityLevel.GOOD) }

    LaunchedEffect(Unit) {
        while (true) {
            dataUsage = PerformanceMonitor.getDataUsageToday()
            warning = optimizer.getDataUsageWarning()
            networkQuality = NetworkQualityManager(context).getCurrentNetworkQuality()
            delay(10000) // Update every 10 seconds
        }
    }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    when {
                        dataUsage > 4.5 * 1024 * 1024 -> Color.Red.copy(alpha = 0.1f)
                        dataUsage > 2.5 * 1024 * 1024 -> Color(0xFFFF9800).copy(alpha = 0.1f)
                        else -> Color.Green.copy(alpha = 0.1f)
                    },
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = if (isTeluguMode) "నేటి డేటా వినియోగం" else "Today's Data Usage",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Data usage bar
            val usagePercentage = (dataUsage.toFloat() / (5 * 1024 * 1024)) * 100
            LinearProgressIndicator(
                progress = (usagePercentage / 100).coerceAtMost(1f),
                modifier = Modifier.fillMaxWidth(),
                color =
                    when {
                        usagePercentage > 90 -> Color.Red
                        usagePercentage > 75 -> Color(0xFFFF9800)
                        else -> Color.Green
                    },
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${dataUsage / 1024}KB",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = if (isTeluguMode) "5MB పరిమితి" else "5MB limit",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            // Network status
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val networkIcon =
                    when (networkQuality) {
                        NetworkQualityLevel.EXCELLENT -> "📶"
                        NetworkQualityLevel.GOOD -> "📶"
                        NetworkQualityLevel.FAIR -> "📶"
                        NetworkQualityLevel.POOR -> "📵"
                        NetworkQualityLevel.OFFLINE -> "📵"
                    }

                Text(
                    text = "$networkIcon ${if (isTeluguMode) {
                        when (networkQuality) {
                            NetworkQualityLevel.EXCELLENT -> "అద్భుతం"
                            NetworkQualityLevel.GOOD -> "మంచిది"
                            NetworkQualityLevel.FAIR -> "సాధారణం"
                            NetworkQualityLevel.POOR -> "దీనం"
                            NetworkQualityLevel.OFFLINE -> "ఆఫ్లైన్"
                        }
                    } else {
                        networkQuality.name
                    }}",
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        when (networkQuality) {
                            NetworkQualityLevel.EXCELLENT, NetworkQualityLevel.GOOD -> Color(0xFF4CAF50)
                            NetworkQualityLevel.FAIR -> Color(0xFFFF9800)
                            else -> Color(0xFFE53935)
                        },
                )
            }

            // Warning message
            warning?.let { warningText ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        if (isTeluguMode) {
                            when {
                                warningText.contains("almost reached") -> "డేటా పరిమితి దాదాపు చేరుకుంది!"
                                warningText.contains("High data usage") -> "అధిక డేటా వినియోగం. ఆఫ్లైన్ మోడ్ ఉపయోగించండి."
                                warningText.contains("Moderate") -> "మధ్యస్థ డేటా వినియోగం. చిత్రాలు ఎక్కువ కుదించబడతాయి."
                                else -> warningText
                            }
                        } else {
                            warningText
                        },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE53935),
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}
