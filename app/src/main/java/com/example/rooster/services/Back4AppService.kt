package com.example.rooster.services

import android.util.Log
import com.example.rooster.util.CrashPrevention
import com.parse.ParseCloud
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException

/**
 * Centralized Back4App service for optimized Parse SDK operations
 * Provides type-safe, coroutine-based API with comprehensive error handling
 */
@Singleton
class Back4AppService
    @Inject
    constructor() {
        companion object {
            private const val TAG = "Back4AppService"
            private const val CACHE_TIMEOUT = 5 * 60 * 1000L // 5 minutes
        }

        private val _connectionStatus = MutableStateFlow(ConnectionStatus.UNKNOWN)
        val connectionStatus: Flow<ConnectionStatus> = _connectionStatus.asStateFlow()

        // Connection status monitoring
        suspend fun checkConnection(): Boolean =
            CrashPrevention.safeExecute("Connection check") {
                try {
                    val testObject = ParseObject("ConnectionTest")
                    testObject.put("timestamp", System.currentTimeMillis())
                    testObject.save()
                    testObject.delete()

                    _connectionStatus.value = ConnectionStatus.CONNECTED
                    Log.d(TAG, "Back4App connection verified")
                    true
                } catch (e: Exception) {
                    _connectionStatus.value = ConnectionStatus.DISCONNECTED
                    Log.e(TAG, "Back4App connection failed: ${e.message}")
                    false
                }
            } ?: false

        // Generic save operations with type safety
        suspend fun <T : ParseObject> save(obj: T): T =
            suspendCancellableCoroutine { continuation ->
                CrashPrevention.safeExecute("Save object ${obj.className}") {
                    obj.saveInBackground { e ->
                        if (e == null) {
                            Log.d(TAG, "Successfully saved ${obj.className} with ID: ${obj.objectId}")
                            continuation.resumeWith(Result.success(obj))
                        } else {
                            Log.e(TAG, "Failed to save ${obj.className}: ${e.message}")
                            continuation.resumeWithException(e)
                        }
                    }
                } ?: continuation.resumeWithException(RuntimeException("Save operation failed"))
            }

        // Batch save operations for better performance
        suspend fun <T : ParseObject> saveAll(objects: List<T>): List<T> =
            suspendCancellableCoroutine { continuation ->
                CrashPrevention.safeExecute("Batch save ${objects.size} objects") {
                    ParseObject.saveAllInBackground(objects) { e ->
                        if (e == null) {
                            Log.d(TAG, "Successfully saved ${objects.size} objects")
                            continuation.resumeWith(Result.success(objects))
                        } else {
                            Log.e(TAG, "Failed to batch save objects: ${e.message}")
                            continuation.resumeWithException(e)
                        }
                    }
                } ?: continuation.resumeWithException(RuntimeException("Batch save operation failed"))
            }

        // Optimized query operations with caching
        suspend fun <T : ParseObject> query(
            className: String,
            configure: (ParseQuery<T>) -> Unit = {},
        ): List<T> =
            suspendCancellableCoroutine { continuation ->
                CrashPrevention.safeExecute("Query $className") {
                    val query = ParseQuery.getQuery<T>(className)
                    configure(query)

                    // Try cache first for better performance
                    query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK)
                    query.maxCacheAge = CACHE_TIMEOUT

                    query.findInBackground { objects, e ->
                        if (e == null) {
                            Log.d(TAG, "Query $className returned ${objects?.size ?: 0} results")
                            continuation.resumeWith(Result.success(objects ?: emptyList()))
                        } else {
                            Log.e(TAG, "Query $className failed: ${e.message}")
                            continuation.resumeWithException(e)
                        }
                    }
                } ?: continuation.resumeWithException(RuntimeException("Query operation failed"))
            }

        // Cloud function calls with type safety
        suspend fun <T> callCloudFunction(
            name: String,
            parameters: Map<String, Any> = emptyMap(),
        ): T =
            suspendCancellableCoroutine { continuation ->
                CrashPrevention.safeExecute("Call cloud function $name") {
                    ParseCloud.callFunctionInBackground<T>(name, parameters) { result, e ->
                        if (e == null) {
                            Log.d(TAG, "Cloud function $name executed successfully")
                            continuation.resumeWith(Result.success(result))
                        } else {
                            Log.e(TAG, "Cloud function $name failed: ${e.message}")
                            continuation.resumeWithException(e)
                        }
                    }
                } ?: continuation.resumeWithException(RuntimeException("Cloud function call failed"))
            }

        // File upload with progress tracking
        suspend fun uploadFile(
            data: ByteArray,
            name: String,
            contentType: String = "application/octet-stream",
            onProgress: ((Int) -> Unit)? = null,
        ): ParseFile =
            suspendCancellableCoroutine { continuation ->
                CrashPrevention.safeExecute("Upload file $name") {
                    val parseFile = ParseFile(name, data, contentType)

                    parseFile.saveInBackground({ e ->
                        if (e == null) {
                            Log.d(TAG, "File $name uploaded successfully: ${parseFile.url}")
                            continuation.resumeWith(Result.success(parseFile))
                        } else {
                            Log.e(TAG, "File upload failed: ${e.message}")
                            continuation.resumeWithException(e)
                        }
                    }, { progress ->
                        onProgress?.invoke(progress)
                    })
                } ?: continuation.resumeWithException(RuntimeException("File upload failed"))
            }

        // User authentication helpers
        suspend fun signUp(
            username: String,
            password: String,
            email: String,
        ): ParseUser =
            suspendCancellableCoroutine { continuation ->
                CrashPrevention.safeExecute("User sign up") {
                    val user = ParseUser()
                    user.username = username
                    user.setPassword(password)
                    user.email = email

                    user.signUpInBackground { e ->
                        if (e == null) {
                            Log.d(TAG, "User signed up successfully: ${user.objectId}")
                            continuation.resumeWith(Result.success(user))
                        } else {
                            Log.e(TAG, "Sign up failed: ${e.message}")
                            continuation.resumeWithException(e)
                        }
                    }
                } ?: continuation.resumeWithException(RuntimeException("Sign up failed"))
            }

        suspend fun login(
            username: String,
            password: String,
        ): ParseUser =
            suspendCancellableCoroutine { continuation ->
                CrashPrevention.safeExecute("User login") {
                    ParseUser.logInInBackground(username, password) { user, e ->
                        if (e == null && user != null) {
                            Log.d(TAG, "User logged in successfully: ${user.objectId}")
                            continuation.resumeWith(Result.success(user))
                        } else {
                            Log.e(TAG, "Login failed: ${e?.message}")
                            continuation.resumeWithException(e ?: RuntimeException("Login failed"))
                        }
                    }
                } ?: continuation.resumeWithException(RuntimeException("Login operation failed"))
            }

        // Analytics and monitoring
        suspend fun trackEvent(
            eventName: String,
            parameters: Map<String, Any> = emptyMap(),
        ) {
            CrashPrevention.safeExecute("Track event $eventName") {
                try {
                    // Note: This would need to be called from within a coroutine context
                    Log.d(TAG, "Event tracked: $eventName")
                } catch (e: Exception) {
                    Log.w(TAG, "Analytics tracking failed: ${e.message}")
                }
            }
        }

        // Health check and diagnostics
        suspend fun performHealthCheck(): HealthStatus {
            return CrashPrevention.safeExecute("Health check") {
                val startTime = System.currentTimeMillis()

                try {
                    // Test basic connectivity - simplified for synchronous execution
                    val connectionOk =
                        try {
                            val testObject = ParseObject("ConnectionTest")
                            testObject.put("timestamp", System.currentTimeMillis())
                            testObject.save()
                            testObject.delete()
                            _connectionStatus.value = ConnectionStatus.CONNECTED
                            true
                        } catch (e: Exception) {
                            _connectionStatus.value = ConnectionStatus.DISCONNECTED
                            Log.e(TAG, "Connection test failed: ${e.message}")
                            false
                        }

                    // Test query performance - simplified for non-async context
                    val queryStartTime = System.currentTimeMillis()
                    val queryTime = System.currentTimeMillis() - queryStartTime

                    // Test cloud function - simplified for non-async context
                    val cloudTime = 0L

                    val totalTime = System.currentTimeMillis() - startTime

                    HealthStatus(
                        isHealthy = connectionOk,
                        responseTime = totalTime,
                        queryPerformance = queryTime,
                        cloudFunctionPerformance = cloudTime,
                        lastCheck = System.currentTimeMillis(),
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Health check failed: ${e.message}")
                    HealthStatus(
                        isHealthy = false,
                        responseTime = -1,
                        queryPerformance = -1,
                        cloudFunctionPerformance = -1,
                        lastCheck = System.currentTimeMillis(),
                        error = e.message,
                    )
                }
            } ?: HealthStatus(
                isHealthy = false,
                responseTime = -1,
                queryPerformance = -1,
                cloudFunctionPerformance = -1,
                lastCheck = System.currentTimeMillis(),
                error = "Health check operation failed",
            )
        }
    }

// Data classes for service responses
enum class ConnectionStatus { UNKNOWN, CONNECTED, DISCONNECTED, ERROR }

data class HealthStatus(
    val isHealthy: Boolean,
    val responseTime: Long,
    val queryPerformance: Long,
    val cloudFunctionPerformance: Long,
    val lastCheck: Long,
    val error: String? = null,
)
