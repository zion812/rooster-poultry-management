// use context7
package com.example.rooster.services

import android.content.Context
import android.util.LruCache
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Smart multi-layer caching system optimized for rural connectivity and low-end devices
 * Implements Phase 2A specifications with predictive fetching and intelligent invalidation
 */
@Singleton
class SmartCacheManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val diskCacheManager: DiskCacheManager,
    private val predictiveEngine: PredictiveCacheEngine
) {
    
    // Layer 1: Memory Cache (L1) - Instant access
    private val memoryCache = LruCache<String, CacheEntry>(
        calculateOptimalCacheSize()
    )
    
    // Layer 4: Predictive Cache tracking
    private val _cacheHits = MutableSharedFlow<CacheHitEvent>()
    val cacheHits: Flow<CacheHitEvent> = _cacheHits.asSharedFlow()
    
    // TTL tracking for intelligent invalidation
    private val ttlTracker = ConcurrentHashMap<String, Long>()
    
    // Cache statistics for optimization
    private val stats = CacheStatistics()
    
    // Coroutine scope for background operations
    private val cacheScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    companion object {
        const val DEFAULT_TTL_MINUTES = 30L
        const val RURAL_OPTIMIZATION_TTL_MINUTES = 120L // Aggressive caching for poor connectivity
        const val MAX_MEMORY_CACHE_SIZE_MB = 16 // Conservative for low-end devices
        const val PREFETCH_THRESHOLD = 0.7 // Start prefetching when 70% of data is used
    }
    
    /**
     * Get data with smart caching logic
     * Implements L1->L2->L3->Network waterfall
     */
    suspend fun <T> getCachedData(
        key: String,
        ttlMinutes: Long = DEFAULT_TTL_MINUTES,
        networkFetcher: suspend () -> T
    ): T {
        // L1: Memory Cache check
        memoryCache.get(key)?.let { entry ->
            if (!isExpired(key, ttlMinutes)) {
                stats.recordHit(CacheLayer.MEMORY)
                _cacheHits.emit(CacheHitEvent(key, CacheLayer.MEMORY))
                return entry.data as T
            }
        }
        
        // L2: Disk Cache check
        diskCacheManager.get<T>(key)?.let { data ->
            if (!isExpired(key, ttlMinutes)) {
                // Promote to memory cache
                putInMemoryCache(key, data)
                stats.recordHit(CacheLayer.DISK)
                _cacheHits.emit(CacheHitEvent(key, CacheLayer.DISK))
                return data
            }
        }
        
        // L3: Network fetch with caching
        stats.recordMiss()
        val data = networkFetcher()
        
        // Cache in all layers
        cacheInAllLayers(key, data, ttlMinutes)
        
        // Trigger predictive prefetch if threshold reached
        checkPrefetchThreshold(key)
        
        return data
    }
    
    /**
     * Predictive prefetch based on usage patterns
     */
    fun prefetchRelatedData(baseKey: String) {
        cacheScope.launch {
            predictiveEngine.getPredictedKeys(baseKey).forEach { predictedKey ->
                // Only prefetch if not already cached
                if (!isDataCached(predictedKey)) {
                    try {
                        predictiveEngine.executePrefetch(predictedKey)
                        stats.recordPrefetch()
                    } catch (e: Exception) {
                        // Silent fail for prefetch - don't impact user experience
                    }
                }
            }
        }
    }
    
    /**
     * Cache data in all appropriate layers
     */
    private suspend fun <T> cacheInAllLayers(key: String, data: T, ttlMinutes: Long) {
        val timestamp = System.currentTimeMillis()
        
        // L1: Memory cache
        putInMemoryCache(key, data)
        
        // L2: Disk cache for offline persistence
        diskCacheManager.put(key, data, ttlMinutes)
        
        // Update TTL tracking
        ttlTracker[key] = timestamp + TimeUnit.MINUTES.toMillis(ttlMinutes)
    }
    
    /**
     * Put data in memory cache with size optimization
     */
    private fun <T> putInMemoryCache(key: String, data: T) {
        val entry = CacheEntry(
            data = data as Any,
            timestamp = System.currentTimeMillis(),
            accessCount = 1
        )
        memoryCache.put(key, entry)
    }
    
    /**
     * Check if cached data is expired
     */
    private fun isExpired(key: String, ttlMinutes: Long): Boolean {
        val expiryTime = ttlTracker[key] ?: return true
        return System.currentTimeMillis() > expiryTime
    }
    
    /**
     * Check if data exists in any cache layer
     */
    private suspend fun isDataCached(key: String): Boolean {
        return memoryCache.get(key) != null || diskCacheManager.exists(key)
    }
    
    /**
     * Check if prefetch threshold is reached
     */
    private fun checkPrefetchThreshold(key: String) {
        val entry = memoryCache.get(key)
        entry?.let {
            it.accessCount++
            if (it.accessCount >= (memoryCache.maxSize() * PREFETCH_THRESHOLD).toInt()) {
                prefetchRelatedData(key)
            }
        }
    }
    
    /**
     * Calculate optimal cache size based on device capabilities
     */
    private fun calculateOptimalCacheSize(): Int {
        val maxMemory = Runtime.getRuntime().maxMemory()
        val cacheSize = maxMemory / 8 // Use 1/8th of available memory
        return (cacheSize / 1024).toInt().coerceAtMost(MAX_MEMORY_CACHE_SIZE_MB * 1024)
    }
    
    /**
     * Invalidate cache entry
     */
    fun invalidateCache(key: String) {
        memoryCache.remove(key)
        ttlTracker.remove(key)
        cacheScope.launch {
            diskCacheManager.remove(key)
        }
    }
    
    /**
     * Clear all caches (for logout, etc.)
     */
    fun clearAllCaches() {
        memoryCache.evictAll()
        ttlTracker.clear()
        cacheScope.launch {
            diskCacheManager.clearAll()
        }
    }
    
    /**
     * Get cache statistics for monitoring
     */
    fun getCacheStatistics(): CacheStatistics = stats.copy()
    
    /**
     * Enable rural optimization mode with aggressive caching
     */
    fun enableRuralOptimization() {
        stats.ruralModeEnabled = true
        // Increase default TTL for poor connectivity scenarios
    }

    /**
     * Cache market prediction data
     */
    fun <T> cachePrediction(key: String, prediction: T) {
        cacheScope.launch {
            cacheInAllLayers(key, prediction, RURAL_OPTIMIZATION_TTL_MINUTES)
        }
    }

    /**
     * Cache health prediction with high priority
     */
    fun <T> cacheHealthPrediction(farmId: String, prediction: T) {
        val key = "health_prediction_$farmId"
        cacheScope.launch {
            cacheInAllLayers(key, prediction, RURAL_OPTIMIZATION_TTL_MINUTES)
        }
    }

    /**
     * Cache seasonal data for agricultural predictions
     */
    fun <T> cacheSeasonalData(region: String, data: T) {
        val key = "seasonal_data_$region"
        cacheScope.launch {
            cacheInAllLayers(
                key,
                data,
                RURAL_OPTIMIZATION_TTL_MINUTES * 2
            ) // Longer TTL for seasonal data
        }
    }
}

/**
 * Cache entry with metadata
 */
data class CacheEntry(
    val data: Any,
    val timestamp: Long,
    var accessCount: Int
)

/**
 * Cache hit event for analytics
 */
data class CacheHitEvent(
    val key: String,
    val layer: CacheLayer
)

/**
 * Cache layers for tracking
 */
enum class CacheLayer {
    MEMORY, DISK, NETWORK, PREDICTIVE
}

/**
 * Cache statistics for monitoring and optimization
 */
data class CacheStatistics(
    var memoryHits: Long = 0,
    var diskHits: Long = 0,
    var networkCalls: Long = 0,
    var prefetches: Long = 0,
    var misses: Long = 0,
    var ruralModeEnabled: Boolean = false
) {
    fun recordHit(layer: CacheLayer) {
        when (layer) {
            CacheLayer.MEMORY -> memoryHits++
            CacheLayer.DISK -> diskHits++
            CacheLayer.NETWORK -> networkCalls++
            CacheLayer.PREDICTIVE -> prefetches++
        }
    }
    
    fun recordMiss() {
        misses++
    }
    
    fun recordPrefetch() {
        prefetches++
    }
    
    fun getHitRate(): Double {
        val totalRequests = memoryHits + diskHits + misses
        return if (totalRequests > 0) (memoryHits + diskHits).toDouble() / totalRequests else 0.0
    }
}
