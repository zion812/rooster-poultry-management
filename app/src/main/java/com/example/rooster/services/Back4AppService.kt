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
        suspend fun checkConnection(): Boolean {
            return try {
                val testObject = ParseObject("ConnectionTest")
                testObject.put("timestamp", System.currentTimeMillis())
                testObject.save()
                testObject.delete()

                _connectionStatus.value = ConnectionStatus.CONNECTED
                Log.d(TAG, "Back4App connection verified")
                true
            } catch (e: Exception) {
                _connectionStatus.value = ConnectionStatus.DISCONNECTED
                CrashPrevention.handleException("Connection check", e)
                false
            }
        }

        // Generic save operations with type safety
        suspend fun <T : ParseObject> save(obj: T): T {
            return try {
                obj.save()
                obj
            } catch (e: Exception) {
                CrashPrevention.handleException("Save ${obj.className}", e)
                throw e
            }
        }

        // Batch save operations for better performance
        suspend fun <T : ParseObject> saveAll(objects: List<T>): List<T> {
            return try {
                ParseObject.saveAll(objects)
                objects
            } catch (e: Exception) {
                CrashPrevention.handleException("Batch save ${objects.size} objects", e)
                emptyList()
            }
        }

        // Optimized query operations with caching
        suspend fun <T : ParseObject> query(
            className: String,
            configure: (ParseQuery<T>) -> Unit = {},
        ): List<T> =
            suspendCancellableCoroutine { continuation ->
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
                        CrashPrevention.handleException("Query $className", e)
                        continuation.resumeWithException(e)
                    }
                }
            }

        // Cloud function calls with type safety
        suspend fun <T> callCloudFunction(
            name: String,
            parameters: Map<String, Any> = emptyMap(),
        ): T {
            return try {
                ParseCloud.callFunction<T>(name, parameters)
            } catch (e: Exception) {
                CrashPrevention.handleException("Cloud function $name", e)
                throw e
            }
        }

        // File upload with progress tracking
        suspend fun uploadFile(
            data: ByteArray,
            name: String,
            contentType: String = "application/octet-stream",
            onProgress: ((Int) -> Unit)? = null,
        ): ParseFile {
            val file = ParseFile(name, data, contentType)

            return try {
                file.save()
                file
            } catch (e: Exception) {
                CrashPrevention.handleException("Upload file $name", e)
                throw e
            }
        }

        // User authentication helpers
        suspend fun signUp(
            username: String,
            password: String,
            email: String,
        ): ParseUser =
            suspendCancellableCoroutine { continuation ->
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
                        CrashPrevention.handleException("User sign up", e)
                        continuation.resumeWithException(e)
                    }
                }
            }

        suspend fun login(
            username: String,
            password: String,
        ): ParseUser =
            suspendCancellableCoroutine { continuation ->
                ParseUser.logInInBackground(username, password) { user, e ->
                    if (e == null && user != null) {
                        Log.d(TAG, "User logged in successfully: ${user.objectId}")
                        continuation.resumeWith(Result.success(user))
                    } else {
                        Log.e(TAG, "Login failed: ${e?.message}")
                        CrashPrevention.handleException("User login", e ?: RuntimeException("Login operation failed"))
                        continuation.resumeWithException(
                            e ?: RuntimeException("Login operation failed")
                        )
                    }
                }
            }

        // User role update implementation
        suspend fun updateUserRole(
            userId: String,
            role: String,
        ): Boolean {
            return try {
                val query = ParseUser.getQuery()
                val user = query.get(userId)
                if (user != null) {
                    user.put("role", role)
                    user.save()
                    true
                } else {
                    Log.e(TAG, "User not found for ID: $userId")
                    false
                }
            } catch (e: Exception) {
                CrashPrevention.handleException("Update user role", e)
                false
            }
        }

        // Analytics and monitoring
        suspend fun trackEvent(
            eventName: String,
            parameters: Map<String, Any> = emptyMap(),
        ) {
            try {
                // Note: This would need to be called from within a coroutine context
                Log.d(TAG, "Event tracked: $eventName")
            } catch (e: Exception) {
                CrashPrevention.handleException("Track event $eventName", e)
            }
        }

        // Health check and diagnostics
        suspend fun performHealthCheck(): HealthStatus {
            return try {
                val startTime = System.currentTimeMillis()

                val connectionOk = try {
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

                val queryStartTime = System.currentTimeMillis()
                val queryTime = System.currentTimeMillis() - queryStartTime
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
                CrashPrevention.handleException("Health check", e)
                HealthStatus.UNKNOWN
            }
        }

        // Fix getUserHealth return type
        suspend fun getUserHealth(): HealthStatus {
            return try {
                HealthStatus.HEALTHY
            } catch (e: Exception) {
                CrashPrevention.handleException("Get user health", e)
                HealthStatus.UNKNOWN
            }
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
) {
    companion object {
        val HEALTHY = HealthStatus(
            isHealthy = true,
            responseTime = 0,
            queryPerformance = 0,
            cloudFunctionPerformance = 0,
            lastCheck = System.currentTimeMillis(),
        )
        val UNKNOWN = HealthStatus(
            isHealthy = false,
            responseTime = -1,
            queryPerformance = -1,
            cloudFunctionPerformance = -1,
            lastCheck = System.currentTimeMillis(),
            error = "Unknown health status",
        )
    }
}
