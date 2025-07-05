package com.example.rooster.util

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

// Memory thresholds (in MB)
private const val LOW_MEMORY_THRESHOLD = 64
private const val CRITICAL_MEMORY_THRESHOLD = 32
private const val TAG = "MemoryOptimizer"

/**
 * Memory and performance optimization utility for Rooster app
 * Designed for rural users with low-end devices
 */
@Singleton
class MemoryOptimizer
    @Inject
    constructor() {
        private val imageCache = mutableMapOf<String, WeakReference<Bitmap>>()
        private val cleanupScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        /**
         * Check if device is running low on memory
         */
        fun isLowMemory(context: Context): Boolean {
            return safeExecute("Memory check", false) {
                val activityManager =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val memoryInfo = ActivityManager.MemoryInfo()
                activityManager.getMemoryInfo(memoryInfo)

                val availableMemoryMB = memoryInfo.availMem / (1024 * 1024)
                availableMemoryMB < LOW_MEMORY_THRESHOLD
            } ?: false
        }

        /**
         * Get memory status for optimization decisions
         */
        fun getMemoryStatus(context: Context): MemoryStatus {
            return safeExecute("Memory status check", MemoryStatus.NORMAL) {
                val activityManager =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val memoryInfo = ActivityManager.MemoryInfo()
                activityManager.getMemoryInfo(memoryInfo)

                val availableMemoryMB = memoryInfo.availMem / (1024 * 1024)

                when {
                    availableMemoryMB < CRITICAL_MEMORY_THRESHOLD -> MemoryStatus.CRITICAL
                    availableMemoryMB < LOW_MEMORY_THRESHOLD -> MemoryStatus.LOW
                    memoryInfo.lowMemory -> MemoryStatus.WARNING
                    else -> MemoryStatus.NORMAL
                }
            } ?: MemoryStatus.NORMAL
        }

        /**
         * Optimize bitmap loading for low-memory devices
         */
        fun loadOptimizedBitmap(
            imagePath: String,
            maxWidth: Int = 800,
            maxHeight: Int = 600,
        ): Bitmap? {
            return safeExecute("Bitmap loading", null as Bitmap?) {
                // Check cache first
                imageCache[imagePath]?.get()?.let { cachedBitmap ->
                    if (!cachedBitmap.isRecycled) {
                        return@safeExecute cachedBitmap
                    } else {
                        imageCache.remove(imagePath)
                    }
                }

                // Calculate sample size to reduce memory usage
                val options =
                    BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                BitmapFactory.decodeFile(imagePath, options)

                val sampleSize =
                    calculateSampleSize(options.outWidth, options.outHeight, maxWidth, maxHeight)

                options.apply {
                    inJustDecodeBounds = false
                    inSampleSize = sampleSize
                    inPreferredConfig = Bitmap.Config.RGB_565 // Use less memory
                }

                val bitmap = BitmapFactory.decodeFile(imagePath, options)

                // Cache with weak reference
                bitmap?.let {
                    imageCache[imagePath] = WeakReference(it)
                }

                bitmap
            }
        }

        /**
         * Calculate appropriate sample size for bitmap loading
         */
        private fun calculateSampleSize(
            width: Int,
            height: Int,
            maxWidth: Int,
            maxHeight: Int,
        ): Int {
            var sampleSize = 1

            if (height > maxHeight || width > maxWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2

                while ((halfHeight / sampleSize) >= maxHeight && (halfWidth / sampleSize) >= maxWidth) {
                    sampleSize *= 2
                }
            }

            return sampleSize
        }

        /**
         * Compress image for storage/transmission
         */
        fun compressImage(
            inputPath: String,
            outputPath: String,
            quality: Int = 70,
            maxDimension: Int = 1024,
        ): Boolean {
            return safeExecute("Image compression", false) {
                val bitmap = loadOptimizedBitmap(inputPath, maxDimension, maxDimension)

                bitmap?.let {
                    val outputFile = File(outputPath)
                    val outputStream = FileOutputStream(outputFile)

                    val success = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                    outputStream.close()

                    // Recycle bitmap to free memory
                    if (!bitmap.isRecycled) {
                        bitmap.recycle()
                    }

                    success
                } ?: false
            } ?: false
        }

        /**
         * Clear image cache to free memory
         */
        fun clearImageCache() {
            safeExecute("Image cache clearing") {
                imageCache.values.forEach { weakRef ->
                    weakRef.get()?.let { bitmap ->
                        if (!bitmap.isRecycled) {
                            bitmap.recycle()
                        }
                    }
                }
                imageCache.clear()

                // Force garbage collection
                System.gc()

                Log.d(TAG, "Image cache cleared")
            }
        }

        /**
         * Periodic memory cleanup
         */
        fun startPeriodicCleanup(intervalMinutes: Long = 10) {
            cleanupScope.launch {
                while (isActive) {
                    delay(intervalMinutes * 60 * 1000) // Convert to milliseconds

                    safeExecute("Periodic cleanup") {
                        // Remove recycled bitmaps from cache
                        val iterator = imageCache.iterator()
                        while (iterator.hasNext()) {
                            val entry = iterator.next()
                            val bitmap = entry.value.get()
                            if (bitmap == null || bitmap.isRecycled) {
                                iterator.remove()
                            }
                        }

                        // Suggest garbage collection
                        System.gc()

                        Log.d(TAG, "Periodic memory cleanup completed")
                    }
                }
            }
        }

        /**
         * Stop periodic cleanup
         */
        fun stopPeriodicCleanup() {
            cleanupScope.cancel()
        }

        /**
         * Get cache size for monitoring
         */
        fun getCacheSize(): Int {
            return imageCache.size
        }

        /**
         * Emergency memory cleanup for critical situations
         */
        fun emergencyCleanup() {
            safeExecute("Emergency cleanup") {
                clearImageCache()

                // Clear any other caches here
                // e.g., network caches, temporary files, etc.

                System.runFinalization()
                System.gc()

                Log.w(TAG, "Emergency memory cleanup executed")
            }
        }

        /**
         * Get recommended image quality based on memory status
         */
        fun getRecommendedImageQuality(context: Context): Int {
            return when (getMemoryStatus(context)) {
                MemoryStatus.CRITICAL -> 40
                MemoryStatus.LOW -> 60
                MemoryStatus.WARNING -> 75
                MemoryStatus.NORMAL -> 85
            }
        }

        /**
         * Get recommended image dimensions based on memory status
         */
        fun getRecommendedImageDimensions(context: Context): Pair<Int, Int> {
            return when (getMemoryStatus(context)) {
                MemoryStatus.CRITICAL -> 400 to 300
                MemoryStatus.LOW -> 600 to 450
                MemoryStatus.WARNING -> 800 to 600
                MemoryStatus.NORMAL -> 1024 to 768
            }
        }

        fun configureMemoryMode(context: Context) {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)

            when {
                isLowRamDevice(context) -> enableUltraLowMemoryMode() // Assuming isLowMemoryDevice was a typo and meant isLowRamDevice
                memoryInfo.availMem < memoryInfo.threshold -> enableLowMemoryMode()
                else -> enableNormalMode()
            }
        }

        fun isLowRamDevice(context: Context): Boolean {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activityManager.isLowRamDevice
            } else {
                val memoryInfo = ActivityManager.MemoryInfo()
                activityManager.getMemoryInfo(memoryInfo)
                memoryInfo.totalMem < 1024 * 1024 * 1024 // Less than 1GB RAM
            }
        }

        private fun enableUltraLowMemoryMode() {
            // Reduce cache sizes
            MemoryCache.maxSize = 10
            ImageCache.quality = ImageQuality.LOW

            // Limit concurrent operations
            ConcurrentOperationManager.maxConcurrentTasks = 2

            // Enable aggressive garbage collection
            System.gc()
        }

        private fun enableLowMemoryMode() {
            // Moderate cache reduction
            MemoryCache.maxSize = 25
            ImageCache.quality = ImageQuality.MEDIUM

            // Moderate concurrent operations
            ConcurrentOperationManager.maxConcurrentTasks = 4
        }

        private fun enableNormalMode() {
            // Full cache sizes
            MemoryCache.maxSize = 50
            ImageCache.quality = ImageQuality.HIGH

            // Normal concurrent operations
            ConcurrentOperationManager.maxConcurrentTasks = 8
        }

        fun onMemoryWarning() {
            // Clear non-essential caches
            MemoryCache.clear()
            ImageCache.clear() // Use clear instead of trimToSize

            // Force garbage collection
            System.gc()
        }

        fun monitorMemoryUsage(
            context: Context,
            onMemoryPressure: () -> Unit,
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                val activityManager =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val memoryInfo = ActivityManager.MemoryInfo()

                while (true) {
                    activityManager.getMemoryInfo(memoryInfo)

                    if (memoryInfo.lowMemory || memoryInfo.availMem < memoryInfo.threshold) {
                        onMemoryPressure()
                    }

                    kotlinx.coroutines.delay(5000) // Check every 5 seconds
                }
            }
        }
    }

// Memory cache configuration
object MemoryCache {
    var maxSize: Int = 50
        set(value) {
            field = value
            // Trim cache if needed
            if (cache.size() > value) {
                cache.evictAll()
            }
        }

    private val cache = androidx.collection.LruCache<String, Any>(maxSize)

    fun put(
        key: String,
        value: Any,
    ) {
        cache.put(key, value)
    }

    fun get(key: String): Any? = cache.get(key)

    fun clear() {
        cache.evictAll()
    }
}

// Image cache configuration
object ImageCache {
    var quality: ImageQuality = ImageQuality.HIGH
    private var currentSize = 0
    private val maxSizeMap =
        mapOf(
            ImageQuality.LOW to 10 * 1024 * 1024, // 10MB
            ImageQuality.MEDIUM to 25 * 1024 * 1024, // 25MB
            ImageQuality.HIGH to 50 * 1024 * 1024, // 50MB
        )

    fun size(): Int = currentSize

    fun clear() {
        // Implementation would clear image cache
        currentSize = 0
    }
}

enum class ImageQuality {
    LOW,
    MEDIUM,
    HIGH,
}

// Concurrent operation management
object ConcurrentOperationManager {
    var maxConcurrentTasks: Int = 8
    private var currentTasks = 0

    fun canExecuteTask(): Boolean {
        return currentTasks < maxConcurrentTasks
    }

    fun startTask() {
        currentTasks++
    }

    fun finishTask() {
        if (currentTasks > 0) {
            currentTasks--
        }
    }
}

/**
 * Memory status enumeration
 */
enum class MemoryStatus {
    NORMAL, // Plenty of memory available
    WARNING, // System reports low memory
    LOW, // Below our threshold
    CRITICAL, // Very low memory, aggressive optimization needed
}

inline fun <T> safeExecute(
    name: String,
    defaultValue: T,
    block: () -> T,
): T? {
    return try {
        block()
    } catch (e: Exception) {
        Log.e("CrashPrevention", "Error in $name: ${e.message}", e)
        defaultValue
    }
}

inline fun safeExecute(
    name: String,
    block: () -> Unit,
) {
    try {
        block()
    } catch (e: Exception) {
        Log.e("CrashPrevention", "Error in $name: ${e.message}", e)
    }
}

object MemoryOptimizerStatic {
    private val instance = MemoryOptimizer()

    @JvmStatic
    fun startPeriodicCleanup(intervalMinutes: Long = 10) {
        instance.startPeriodicCleanup(intervalMinutes)
    }

    @JvmStatic
    fun emergencyCleanup() {
        instance.emergencyCleanup()
    }

    @JvmStatic
    fun clearImageCache() {
        instance.clearImageCache()
    }
}
