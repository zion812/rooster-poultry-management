package com.example.rooster

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.Parse
import com.parse.ParseUser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * Comprehensive stability management system to prevent crashes and app exits
 */
object StabilityManager {
    private const val TAG = "StabilityManager"
    private const val MAX_RETRY_ATTEMPTS = 3
    private const val RETRY_DELAY_MS = 2000L
    private const val MEMORY_THRESHOLD_MB = 50L
    private const val HEALTH_CHECK_INTERVAL_MS = 30000L // 30 seconds

    private var applicationContext: WeakReference<Context>? = null
    private var isInitialized = false
    private var crashCount = 0
    private var lastCrashTime = 0L

    // Coroutine scope with exception handling
    private val exceptionHandler =
        CoroutineExceptionHandler { _, exception ->
            handleGlobalException(exception)
        }
    private val stabilityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main + exceptionHandler)

    // App lifecycle state
    private var isAppInForeground = true

    fun initialize(application: Application) {
        if (isInitialized) return

        applicationContext = WeakReference(application.applicationContext)
        isInitialized = true

        setupGlobalExceptionHandler()
        setupLifecycleObserver()
        startHealthCheck()

        Log.d(TAG, "StabilityManager initialized successfully")
    }

    private fun setupGlobalExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            try {
                handleUncaughtException(thread, exception)
            } catch (e: Exception) {
                Log.e(TAG, "Error in exception handler", e)
            } finally {
                // Call the default handler to ensure proper app termination if needed
                defaultHandler?.uncaughtException(thread, exception)
            }
        }
    }

    private fun setupLifecycleObserver() {
        // Simplified lifecycle tracking without ProcessLifecycleOwner
        // This will be handled by the MainActivity integration
        Log.d(TAG, "Lifecycle observer setup completed")
    }

    private fun startHealthCheck() {
        stabilityScope.launch {
            while (true) {
                try {
                    performHealthCheck()
                    delay(HEALTH_CHECK_INTERVAL_MS)
                } catch (e: Exception) {
                    Log.e(TAG, "Health check failed", e)
                    delay(HEALTH_CHECK_INTERVAL_MS * 2) // Wait longer on error
                }
            }
        }
    }

    private fun handleUncaughtException(
        thread: Thread,
        exception: Throwable,
    ) {
        val currentTime = System.currentTimeMillis()

        // Track crash frequency
        if (currentTime - lastCrashTime < 10000) { // Within 10 seconds
            crashCount++
        } else {
            crashCount = 1
        }
        lastCrashTime = currentTime

        Log.e(TAG, "Uncaught exception in thread ${thread.name}", exception)

        // Report to Crashlytics
        try {
            FirebaseCrashlytics.getInstance().apply {
                setCustomKey("crash_count", crashCount)
                setCustomKey("app_in_foreground", isAppInForeground)
                setCustomKey("thread_name", thread.name)
                recordException(exception)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to report crash to Crashlytics", e)
        }

        // Attempt recovery for certain types of exceptions
        if (shouldAttemptRecovery(exception)) {
            attemptRecovery(exception)
        }
    }

    private fun handleGlobalException(exception: Throwable) {
        Log.e(TAG, "Global coroutine exception", exception)

        try {
            FirebaseCrashlytics.getInstance().apply {
                setCustomKey("exception_type", "coroutine")
                recordException(exception)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to report coroutine exception", e)
        }
    }

    private fun shouldAttemptRecovery(exception: Throwable): Boolean {
        return when {
            crashCount >= 5 -> false // Too many crashes, don't attempt recovery
            exception is OutOfMemoryError -> true
            exception.message?.contains("Parse", ignoreCase = true) == true -> true
            exception.message?.contains("network", ignoreCase = true) == true -> true
            else -> false
        }
    }

    private fun attemptRecovery(exception: Throwable) {
        stabilityScope.launch {
            try {
                when {
                    exception is OutOfMemoryError -> {
                        performMemoryCleanup()
                    }
                    exception.message?.contains("Parse", ignoreCase = true) == true -> {
                        reinitializeParse()
                    }
                    exception.message?.contains("network", ignoreCase = true) == true -> {
                        // Wait and retry network operations
                        delay(RETRY_DELAY_MS)
                    }
                }
                Log.d(TAG, "Recovery attempt completed for: ${exception.javaClass.simpleName}")
            } catch (e: Exception) {
                Log.e(TAG, "Recovery attempt failed", e)
            }
        }
    }

    private fun performHealthCheck() {
        try {
            // Memory check
            val runtime = Runtime.getRuntime()
            val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
            val maxMemory = runtime.maxMemory() / (1024 * 1024)
            val memoryUsage = (usedMemory * 100) / maxMemory

            Log.d(TAG, "Memory usage: ${usedMemory}MB / ${maxMemory}MB ($memoryUsage%)")

            if (usedMemory > maxMemory * 0.8) {
                Log.w(TAG, "High memory usage detected, performing cleanup")
                performMemoryCleanup()
            }

            // Parse connection check
            try {
                ParseUser.getCurrentUser()?.fetch()
            } catch (e: Exception) {
                Log.w(TAG, "Parse connection issue detected", e)
            }

            // Database health check
            try {
                // Simple database existence check
                App.getPhotoUploadDao()
                Log.d(TAG, "Database health check passed")
            } catch (e: Exception) {
                Log.w(TAG, "Database health check failed", e)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Health check error", e)
        }
    }

    private fun performMemoryCleanup() {
        try {
            Log.d(TAG, "Performing memory cleanup")

            // Force garbage collection
            System.gc()

            // Clear any cached images or data
            applicationContext?.get()?.let { context ->
                // Clear cache directory if needed
                context.cacheDir.listFiles()?.forEach { file ->
                    if (file.lastModified() < System.currentTimeMillis() - 3600000L) { // 1 hour old
                        file.delete()
                    }
                }
            }

            Log.d(TAG, "Memory cleanup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Memory cleanup failed", e)
        }
    }

    private fun performBackgroundCleanup() {
        stabilityScope.launch {
            try {
                Log.d(TAG, "Performing background cleanup")

                // Cancel non-essential operations
                // Clear temporary data
                performMemoryCleanup()

                Log.d(TAG, "Background cleanup completed")
            } catch (e: Exception) {
                Log.e(TAG, "Background cleanup failed", e)
            }
        }
    }

    private fun reinitializeParse() {
        stabilityScope.launch {
            try {
                Log.d(TAG, "Reinitializing Parse SDK")

                applicationContext?.get()?.let { context ->
                    Parse.initialize(
                        Parse.Configuration.Builder(context)
                            .applicationId(context.getString(R.string.back4app_app_id))
                            .clientKey(context.getString(R.string.back4app_client_key))
                            .server(context.getString(R.string.back4app_server_url))
                            .build(),
                    )
                }

                Log.d(TAG, "Parse SDK reinitialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Parse reinitialization failed", e)
            }
        }
    }

    // Composable function to monitor app stability
    @Composable
    fun StabilityMonitor(onStabilityIssue: ((String) -> Unit)? = null) {
        var isStable by remember { mutableStateOf(true) }
        var lastCheck by remember { mutableStateOf(0L) }

        LaunchedEffect(Unit) {
            while (true) {
                try {
                    val currentTime = System.currentTimeMillis()

                    // Check if app has been stable for the last check interval
                    if (currentTime - lastCrashTime < 30000L && crashCount > 0) {
                        isStable = false
                        onStabilityIssue?.invoke("App stability issues detected. Crash count: $crashCount")
                    } else {
                        isStable = true
                    }

                    lastCheck = currentTime
                    delay(10000L) // Check every 10 seconds
                } catch (e: Exception) {
                    Log.e(TAG, "Stability monitor error", e)
                    delay(30000L) // Wait longer on error
                }
            }
        }
    }

    // Safe execution wrapper for critical operations
    suspend fun <T> safeExecute(
        operation: suspend () -> T,
        fallback: T,
        retryCount: Int = MAX_RETRY_ATTEMPTS,
    ): T {
        repeat(retryCount) { attempt ->
            try {
                return operation()
            } catch (e: Exception) {
                Log.w(TAG, "Safe execution attempt ${attempt + 1} failed", e)

                if (attempt == retryCount - 1) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    return fallback
                }

                delay(RETRY_DELAY_MS * (attempt + 1))
            }
        }
        return fallback
    }

    // Safe navigation wrapper
    fun safeNavigate(action: () -> Unit) {
        try {
            if (isAppInForeground) {
                action()
            } else {
                Log.w(TAG, "Navigation skipped - app in background")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Safe navigation failed", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun getCrashStats(): Pair<Int, Long> = Pair(crashCount, lastCrashTime)

    fun isAppStable(): Boolean = crashCount < 3 && (System.currentTimeMillis() - lastCrashTime) > 30000L
}
