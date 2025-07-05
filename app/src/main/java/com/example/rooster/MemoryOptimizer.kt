package com.example.rooster

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentHashMap

/**
 * Memory optimization utilities to prevent OutOfMemoryError crashes
 */
object MemoryOptimizer {
    private const val TAG = "MemoryOptimizer"
    private const val MAX_MEMORY_USAGE_PERCENT = 75
    private const val CLEANUP_THRESHOLD_MB = 50L

    // Soft reference cache for bitmaps
    private val bitmapCache = ConcurrentHashMap<String, SoftReference<Bitmap>>()

    // Memory usage tracking
    private var lastMemoryCheck = 0L
    private val memoryCheckInterval = 10000L // 10 seconds

    /**
     * Get cached bitmap or load if not in cache
     */
    suspend fun getCachedBitmap(
        context: Context,
        resourceId: Int,
        reqWidth: Int = 0,
        reqHeight: Int = 0,
    ): Bitmap? =
        withContext(Dispatchers.IO) {
            val key = "${resourceId}_${reqWidth}_$reqHeight"

            // Check cache first
            bitmapCache[key]?.get()?.let { bitmap ->
                if (!bitmap.isRecycled) {
                    return@withContext bitmap
                } else {
                    bitmapCache.remove(key)
                }
            }

            // Load bitmap with memory-conscious options
            try {
                val options =
                    BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }

                BitmapFactory.decodeResource(context.resources, resourceId, options)

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
                options.inJustDecodeBounds = false
                options.inPreferredConfig = Bitmap.Config.RGB_565 // Use less memory

                val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

                // Cache with soft reference
                bitmap?.let {
                    if (shouldCacheBitmap()) {
                        bitmapCache[key] = SoftReference(it)
                    }
                }

                bitmap
            } catch (e: OutOfMemoryError) {
                Log.e(TAG, "OutOfMemoryError loading bitmap, performing cleanup", e)
                performEmergencyCleanup()
                null
            } catch (e: Exception) {
                Log.e(TAG, "Error loading bitmap", e)
                null
            }
        }

    /**
     * Calculate appropriate sample size for bitmap loading
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int,
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (reqWidth > 0 && reqHeight > 0) {
            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2

                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }
        } else {
            // Default downsample for memory efficiency
            val maxDimension = 1024
            if (height > maxDimension || width > maxDimension) {
                inSampleSize = if (height > width) height / maxDimension else width / maxDimension
            }
        }

        return inSampleSize
    }

    /**
     * Check if we should cache more bitmaps based on memory usage
     */
    private fun shouldCacheBitmap(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastMemoryCheck > memoryCheckInterval) {
            lastMemoryCheck = currentTime
            val memoryUsage = getMemoryUsagePercent()
            if (memoryUsage > MAX_MEMORY_USAGE_PERCENT) {
                performMemoryCleanup()
                return false
            }
        }
        return true
    }

    /**
     * Get current memory usage percentage
     */
    private fun getMemoryUsagePercent(): Int {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        return ((usedMemory * 100) / maxMemory).toInt()
    }

    /**
     * Perform memory cleanup
     */
    private fun performMemoryCleanup() {
        Log.d(TAG, "Performing memory cleanup")

        // Clear bitmap cache
        clearBitmapCache()

        // Force garbage collection
        System.gc()

        val memoryUsage = getMemoryUsagePercent()
        Log.d(TAG, "Memory cleanup completed. Usage: $memoryUsage%")
    }

    /**
     * Emergency cleanup when OutOfMemoryError occurs
     */
    private fun performEmergencyCleanup() {
        Log.w(TAG, "Performing emergency memory cleanup")

        // Clear all caches aggressively
        clearBitmapCache()

        // Force garbage collection multiple times
        repeat(3) {
            System.gc()
            System.runFinalization()
        }

        Log.w(TAG, "Emergency memory cleanup completed")
    }

    /**
     * Clear bitmap cache
     */
    private fun clearBitmapCache() {
        val clearedCount = bitmapCache.size
        bitmapCache.clear()
        Log.d(TAG, "Cleared $clearedCount cached bitmaps")
    }

    /**
     * Composable to monitor memory usage
     */
    @Composable
    fun MemoryMonitor(onMemoryWarning: ((Int) -> Unit)? = null) {
        LaunchedEffect(Unit) {
            while (true) {
                try {
                    val memoryUsage = getMemoryUsagePercent()

                    if (memoryUsage > MAX_MEMORY_USAGE_PERCENT) {
                        Log.w(TAG, "High memory usage detected: $memoryUsage%")
                        onMemoryWarning?.invoke(memoryUsage)
                        performMemoryCleanup()
                    }

                    kotlinx.coroutines.delay(memoryCheckInterval)
                } catch (e: Exception) {
                    Log.e(TAG, "Memory monitor error", e)
                    kotlinx.coroutines.delay(memoryCheckInterval * 2)
                }
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                // Cleanup when composable is disposed
                if (getMemoryUsagePercent() > 60) {
                    performMemoryCleanup()
                }
            }
        }
    }

    /**
     * Safe bitmap loading with error handling
     */
    suspend fun loadBitmapSafely(
        context: Context,
        resourceId: Int,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024,
    ): Bitmap? {
        return try {
            getCachedBitmap(context, resourceId, maxWidth, maxHeight)
        } catch (e: OutOfMemoryError) {
            Log.e(TAG, "OutOfMemoryError in loadBitmapSafely", e)
            performEmergencyCleanup()
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error in loadBitmapSafely", e)
            null
        }
    }

    /**
     * Get memory statistics
     */
    fun getMemoryStats(): MemoryStats {
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMemory = runtime.maxMemory() / (1024 * 1024)
        val freeMemory = runtime.freeMemory() / (1024 * 1024)
        val totalMemory = runtime.totalMemory() / (1024 * 1024)

        return MemoryStats(
            usedMemoryMB = usedMemory,
            maxMemoryMB = maxMemory,
            freeMemoryMB = freeMemory,
            totalMemoryMB = totalMemory,
            usagePercent = ((usedMemory * 100) / maxMemory).toInt(),
            cachedBitmaps = bitmapCache.size,
        )
    }

    data class MemoryStats(
        val usedMemoryMB: Long,
        val maxMemoryMB: Long,
        val freeMemoryMB: Long,
        val totalMemoryMB: Long,
        val usagePercent: Int,
        val cachedBitmaps: Int,
    )
}
