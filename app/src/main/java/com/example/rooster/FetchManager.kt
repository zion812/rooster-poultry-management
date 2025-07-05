package com.example.rooster

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

object FetchManager {
    private lateinit var downloadManager: DownloadManager
    private lateinit var context: Context
    private var isInitialized = false

    // Track active downloads
    private val _activeDownloads = MutableStateFlow<Map<Long, DownloadInfo>>(emptyMap())
    val activeDownloads: Flow<Map<Long, DownloadInfo>> = _activeDownloads.asStateFlow()

    fun initialize(context: Context) {
        if (isInitialized) return

        try {
            this.context = context.applicationContext
            this.downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            isInitialized = true

            FirebaseCrashlytics.getInstance()
                .log("FetchManager: Initialized successfully with DownloadManager")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            throw RuntimeException("Failed to initialize FetchManager", e)
        }
    }

    fun download(
        url: String,
        filePath: String,
        onProgress: (Int) -> Unit,
        onComplete: () -> Unit,
        onError: (String) -> Unit,
    ): Long {
        if (!isInitialized) {
            onError("FetchManager not initialized")
            return -1
        }

        return try {
            val request =
                DownloadManager.Request(Uri.parse(url)).apply {
                    setTitle(getLocalizedDownloadTitle(filePath))
                    setDescription(getLocalizedDownloadDescription(filePath))
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filePath)
                    setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    setAllowedOverRoaming(false) // Save costs for rural users
                }

            val downloadId = downloadManager.enqueue(request)

            // Store download info
            val currentDownloads = _activeDownloads.value.toMutableMap()
            currentDownloads[downloadId] =
                DownloadInfo(
                    id = downloadId,
                    url = url,
                    fileName = filePath,
                    status = DownloadStatus.QUEUED,
                    progress = 0,
                    onProgress = onProgress,
                    onComplete = onComplete,
                    onError = onError,
                )
            _activeDownloads.value = currentDownloads

            FirebaseCrashlytics.getInstance()
                .log("FetchManager: Started download $downloadId for $filePath")

            // Record data usage estimation
            getEstimatedDataUsage(url) { estimatedSize ->
                PerformanceMonitor.recordNetworkUsage("download_$downloadId", estimatedSize)
            }

            downloadId
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            onError("Failed to start download: ${e.message}")
            -1
        }
    }

    fun pause(downloadId: Long) {
        // Android DownloadManager doesn't support pause/resume directly
        // We can cancel and provide option to restart
        FirebaseCrashlytics.getInstance()
            .log("FetchManager: Pause not supported, use cancel/restart pattern")
    }

    fun resume(downloadId: Long) {
        // Android DownloadManager doesn't support pause/resume directly
        FirebaseCrashlytics.getInstance()
            .log("FetchManager: Resume not supported, use restart download")
    }

    fun cancel(downloadId: Long) {
        if (!isInitialized) return

        try {
            downloadManager.remove(downloadId)

            // Remove from active downloads
            val currentDownloads = _activeDownloads.value.toMutableMap()
            currentDownloads.remove(downloadId)
            _activeDownloads.value = currentDownloads

            FirebaseCrashlytics.getInstance().log("FetchManager: Cancelled download $downloadId")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun getDownloadStatus(downloadId: Long): DownloadStatus {
        if (!isInitialized) return DownloadStatus.FAILED

        return try {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val statusCol = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val status = cursor.getInt(statusCol)

                when (status) {
                    DownloadManager.STATUS_PENDING -> DownloadStatus.QUEUED
                    DownloadManager.STATUS_RUNNING -> DownloadStatus.DOWNLOADING
                    DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.COMPLETED
                    DownloadManager.STATUS_FAILED -> DownloadStatus.FAILED
                    DownloadManager.STATUS_PAUSED -> DownloadStatus.PAUSED
                    else -> DownloadStatus.UNKNOWN
                }
            } else {
                DownloadStatus.UNKNOWN
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            DownloadStatus.FAILED
        }
    }

    fun getEstimatedDataUsage(
        url: String,
        callback: (Long) -> Unit,
    ) {
        try {
            // Estimate based on URL patterns for rural users to plan data usage
            val estimatedSize =
                when {
                    url.contains(".jpg", ignoreCase = true) || url.contains(".jpeg", ignoreCase = true) -> 500_000L // 500KB
                    url.contains(".png", ignoreCase = true) -> 800_000L // 800KB
                    url.contains(".mp4", ignoreCase = true) -> 5_000_000L // 5MB
                    url.contains(".mp3", ignoreCase = true) -> 3_000_000L // 3MB
                    url.contains(".pdf", ignoreCase = true) -> 1_000_000L // 1MB
                    url.contains(".docx", ignoreCase = true) -> 200_000L // 200KB
                    url.contains(".txt", ignoreCase = true) -> 50_000L // 50KB
                    else -> 100_000L // 100KB default
                }

            callback(estimatedSize)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            callback(100_000L) // Default fallback
        }
    }

    private fun getLocalizedDownloadTitle(fileName: String): String {
        val language = Locale.getDefault().language
        return when (language) {
            "te" -> "డౌన్‌లోడ్ చేస్తోంది: $fileName"
            "hi" -> "डाउनलोड हो रहा है: $fileName"
            "ta" -> "பதிவிறக்கம்: $fileName"
            "kn" -> "ಡೌನ್‌ಲೋಡ್: $fileName"
            else -> "Downloading: $fileName"
        }
    }

    private fun getLocalizedDownloadDescription(fileName: String): String {
        val language = Locale.getDefault().language
        return when (language) {
            "te" -> "రూస్టర్ యాప్ ద్వారా $fileName డౌన్‌లోడ్ అవుతోంది"
            "hi" -> "रूस्टर ऐप द्वारा $fileName डाउनलोड हो रहा है"
            "ta" -> "ரூஸ்டர் ஆப் மூலம் $fileName பதிவிறக்கம் செய்யப்படுகிறது"
            "kn" -> "ರೂಸ್ಟರ್ ಅಪ್ ಮೂಲಕ $fileName ಡೌನ್‌ಲೋಡ್ ಆಗುತ್ತಿದೆ"
            else -> "Downloading $fileName via Rooster App"
        }
    }

    fun cleanup() {
        if (isInitialized) {
            _activeDownloads.value = emptyMap()
            isInitialized = false
        }
    }
}

// Data classes for download management
data class DownloadInfo(
    val id: Long,
    val url: String,
    val fileName: String,
    val status: DownloadStatus,
    val progress: Int,
    val onProgress: (Int) -> Unit,
    val onComplete: () -> Unit,
    val onError: (String) -> Unit,
)

enum class DownloadStatus {
    QUEUED,
    DOWNLOADING,
    COMPLETED,
    FAILED,
    PAUSED,
    CANCELLED,
    UNKNOWN,
}

// Simple educational resource data class
data class RuralEducationalResource(
    val id: String,
    val title: String,
    val description: String,
    val contentUrl: String,
    val dataSize: Long,
    val type: RuralResourceType,
    val language: String = "en",
    val teluguTitle: String = "",
    val teluguDescription: String = "",
) {
    enum class RuralResourceType {
        VIDEO,
        ARTICLE,
        PDF,
        AUDIO,
        IMAGE,
    }
}

// Extension function to get file size in human readable format
fun Long.toHumanReadableSize(): String {
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = this.toDouble()
    var unitIndex = 0

    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }

    return String.format("%.1f %s", size, units[unitIndex])
}
