package com.example.rooster.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

data class MediaCacheEntry(
    val url: String,
    val localPath: String?,
    val cacheTime: Long,
    val size: String, // "thumbnail", "medium", "full"
    val isLoaded: Boolean = false,
)

@Singleton
class ProfileMediaOptimizer
    @Inject
    constructor(
        private val context: Context,
    ) {
        private val TAG = "ProfileMediaOptimizer"

        // Memory cache for thumbnails (max 50MB)
        private val memoryCache = LruCache<String, Bitmap>(50 * 1024 * 1024)

        // Media cache state
        private val _mediaCacheState = MutableStateFlow<Map<String, MediaCacheEntry>>(emptyMap())
        val mediaCacheState: StateFlow<Map<String, MediaCacheEntry>> = _mediaCacheState.asStateFlow()

        // Loading states for different media
        private val _loadingStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
        val loadingStates: StateFlow<Map<String, Boolean>> = _loadingStates.asStateFlow()

        // Cache directory for profile media
        private val profileCacheDir: File by lazy {
            File(context.cacheDir, "profile_media").apply {
                if (!exists()) mkdirs()
            }
        }

        /**
         * Optimize and cache profile picture with lazy loading
         */
        suspend fun optimizeProfilePicture(
            imageUrl: String,
            userId: String,
            size: String = "medium",
        ): Result<String?> =
            withContext(Dispatchers.IO) {
                try {
                    // Check if already cached
                    val cacheKey = "${userId}_$size"
                    val cachedEntry = _mediaCacheState.value[cacheKey]

                    if (cachedEntry?.isLoaded == true && cachedEntry.localPath?.let { File(it).exists() } == true) {
                        Log.d(TAG, "Profile picture served from cache: $cacheKey")
                        return@withContext Result.success(cachedEntry.localPath)
                    }

                    // Set loading state
                    updateLoadingState(cacheKey, true)

                    // Create optimized versions
                    val optimizedPath =
                        when (size) {
                            "thumbnail" -> createThumbnail(imageUrl, userId)
                            "medium" -> createMediumSize(imageUrl, userId)
                            "full" -> downloadAndCache(imageUrl, userId, "full")
                            else -> createMediumSize(imageUrl, userId)
                        }

                    // Update cache state
                    val newEntry =
                        MediaCacheEntry(
                            url = imageUrl,
                            localPath = optimizedPath,
                            cacheTime = System.currentTimeMillis(),
                            size = size,
                            isLoaded = true,
                        )

                    val currentCache = _mediaCacheState.value.toMutableMap()
                    currentCache[cacheKey] = newEntry
                    _mediaCacheState.value = currentCache

                    // Clear loading state
                    updateLoadingState(cacheKey, false)

                    Log.d(TAG, "Profile picture optimized: $size -> $optimizedPath")
                    Result.success(optimizedPath)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to optimize profile picture", e)
                    updateLoadingState("${userId}_$size", false)
                    Result.failure(e)
                }
            }

        /**
         * Lazy load profile cover photo with progressive enhancement
         */
        suspend fun lazyLoadCoverPhoto(
            imageUrl: String,
            userId: String,
            onProgress: (Float) -> Unit = {},
        ): Result<String?> =
            withContext(Dispatchers.IO) {
                try {
                    val cacheKey = "${userId}_cover"

                    // First load low-quality placeholder
                    onProgress(0.1f)
                    val thumbnailResult = optimizeProfilePicture(imageUrl, userId, "thumbnail")

                    onProgress(0.3f)

                    // Then load medium quality
                    val mediumResult = optimizeProfilePicture(imageUrl, userId, "medium")

                    onProgress(0.7f)

                    // Finally load full quality in background
                    optimizeProfilePicture(imageUrl, userId, "full")

                    onProgress(1.0f)

                    // Return medium quality for immediate display
                    mediumResult
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to lazy load cover photo", e)
                    Result.failure(e)
                }
            }

        /**
         * Preload profile media for better performance
         */
        suspend fun preloadProfileMedia(userIds: List<String>) {
            try {
                Log.d(TAG, "Preloading profile media for ${userIds.size} users")

                userIds.forEach { userId ->
                    // This would typically fetch user's profile image URL from Parse
                    // For now, we'll simulate the preloading process
                    val mockImageUrl = "https://example.com/profile/$userId.jpg"

                    // Preload thumbnail for quick display
                    optimizeProfilePicture(mockImageUrl, userId, "thumbnail")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to preload profile media", e)
            }
        }

        /**
         * Clean up old cached media files
         */
        suspend fun cleanupOldCache(maxAgeMs: Long = 7 * 24 * 60 * 60 * 1000L) =
            withContext(Dispatchers.IO) {
                try {
                    val currentTime = System.currentTimeMillis()
                    val currentCache = _mediaCacheState.value.toMutableMap()
                    var cleanedCount = 0

                    currentCache.entries.removeAll { (key, entry) ->
                        val isOld = (currentTime - entry.cacheTime) > maxAgeMs
                        if (isOld) {
                            entry.localPath?.let { path ->
                                File(path).delete()
                                cleanedCount++
                            }
                        }
                        isOld
                    }

                    _mediaCacheState.value = currentCache

                    // Clean up orphaned files
                    profileCacheDir.listFiles()?.forEach { file ->
                        if ((currentTime - file.lastModified()) > maxAgeMs) {
                            file.delete()
                            cleanedCount++
                        }
                    }

                    Log.d(TAG, "Cleaned up $cleanedCount old cache files")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to cleanup old cache", e)
                }
            }

        /**
         * Get cache statistics
         */
        fun getCacheStats(): Map<String, Any> {
            val cache = _mediaCacheState.value
            val totalFiles = cache.size
            val totalSizeBytes =
                cache.values.sumOf { entry ->
                    entry.localPath?.let { File(it).length() } ?: 0L
                }

            return mapOf(
                "total_files" to totalFiles,
                "total_size_mb" to (totalSizeBytes / (1024 * 1024)),
                "memory_cache_size" to memoryCache.size(),
                "memory_cache_max_size" to memoryCache.maxSize(),
                "cache_hit_ratio" to calculateCacheHitRatio(),
            )
        }

        /**
         * Check if media is cached and ready
         */
        fun isMediaCached(
            userId: String,
            size: String = "medium",
        ): Boolean {
            val cacheKey = "${userId}_$size"
            val entry = _mediaCacheState.value[cacheKey]
            return entry?.isLoaded == true && entry.localPath?.let { File(it).exists() } == true
        }

        private suspend fun createThumbnail(
            imageUrl: String,
            userId: String,
        ): String? =
            withContext(Dispatchers.IO) {
                try {
                    val bitmap = loadBitmapFromUrl(imageUrl)
                    bitmap?.let {
                        val thumbnail = Bitmap.createScaledBitmap(it, 150, 150, true)
                        val file = File(profileCacheDir, "${userId}_thumbnail.jpg")
                        FileOutputStream(file).use { out ->
                            thumbnail.compress(Bitmap.CompressFormat.JPEG, 80, out)
                        }

                        // Cache in memory too
                        memoryCache.put("${userId}_thumbnail", thumbnail)

                        file.absolutePath
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to create thumbnail", e)
                    null
                }
            }

        private suspend fun createMediumSize(
            imageUrl: String,
            userId: String,
        ): String? =
            withContext(Dispatchers.IO) {
                try {
                    val bitmap = loadBitmapFromUrl(imageUrl)
                    bitmap?.let {
                        val maxDimension = 500
                        val ratio =
                            minOf(maxDimension.toFloat() / it.width, maxDimension.toFloat() / it.height)
                        val newWidth = (it.width * ratio).toInt()
                        val newHeight = (it.height * ratio).toInt()

                        val mediumBitmap = Bitmap.createScaledBitmap(it, newWidth, newHeight, true)
                        val file = File(profileCacheDir, "${userId}_medium.jpg")
                        FileOutputStream(file).use { out ->
                            mediumBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                        }

                        file.absolutePath
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to create medium size", e)
                    null
                }
            }

        private suspend fun downloadAndCache(
            imageUrl: String,
            userId: String,
            size: String,
        ): String? =
            withContext(Dispatchers.IO) {
                try {
                    val bitmap = loadBitmapFromUrl(imageUrl)
                    bitmap?.let {
                        val file = File(profileCacheDir, "${userId}_$size.jpg")
                        FileOutputStream(file).use { out ->
                            it.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        }
                        file.absolutePath
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to download and cache", e)
                    null
                }
            }

        private fun loadBitmapFromUrl(imageUrl: String): Bitmap? {
            return try {
                // This is a simplified version - in practice, you'd use Coil or similar
                val request =
                    ImageRequest.Builder(context)
                        .data(imageUrl)
                        .build()

                // For this implementation, we'll use a placeholder
                BitmapFactory.decodeResource(context.resources, android.R.drawable.ic_menu_gallery)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load bitmap from URL", e)
                null
            }
        }

        private fun updateLoadingState(
            key: String,
            isLoading: Boolean,
        ) {
            val currentStates = _loadingStates.value.toMutableMap()
            if (isLoading) {
                currentStates[key] = true
            } else {
                currentStates.remove(key)
            }
            _loadingStates.value = currentStates
        }

        private fun calculateCacheHitRatio(): Float {
            val cache = _mediaCacheState.value
            if (cache.isEmpty()) return 0f

            val hitCount = cache.values.count { it.isLoaded }
            return hitCount.toFloat() / cache.size
        }
    }
