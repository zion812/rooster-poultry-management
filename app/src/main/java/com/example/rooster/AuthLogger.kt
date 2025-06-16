package com.example.rooster

import android.util.Log
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Authentication Logger to track user login/logout activities
 * Stores logs in Parse Server for administrative monitoring
 */
object AuthLogger {
    private const val TAG = "AuthLogger"
    private const val AUTH_LOG_CLASS = "AuthenticationLog"

    /**
     * Log user login attempt
     */
    suspend fun logLoginAttempt(
        username: String,
        success: Boolean,
        errorMessage: String? = null,
        userRole: String? = null,
        ipAddress: String? = null,
    ) {
        withContext(Dispatchers.IO) {
            try {
                val authLog = ParseObject(AUTH_LOG_CLASS)
                authLog.put("username", username)
                authLog.put("action", "LOGIN")
                authLog.put("success", success)
                authLog.put("timestamp", Date())
                authLog.put("deviceInfo", getDeviceInfo())

                if (success) {
                    userRole?.let { authLog.put("userRole", it) }
                    val currentUser = ParseUser.getCurrentUser()
                    currentUser?.objectId?.let { authLog.put("userId", it) }
                    Log.d(TAG, "Login successful for user: $username with role: $userRole")
                } else {
                    errorMessage?.let { authLog.put("errorMessage", it) }
                    Log.w(TAG, "Login failed for user: $username - $errorMessage")
                }

                ipAddress?.let { authLog.put("ipAddress", it) }

                authLog.saveInBackground { e ->
                    if (e == null) {
                        Log.d(TAG, "Authentication log saved successfully")
                    } else {
                        Log.e(TAG, "Failed to save authentication log", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error logging login attempt", e)
            }
        }
    }

    /**
     * Log user registration attempt
     */
    suspend fun logRegistrationAttempt(
        username: String,
        success: Boolean,
        errorMessage: String? = null,
        userRole: String? = null,
    ) {
        withContext(Dispatchers.IO) {
            try {
                val authLog = ParseObject(AUTH_LOG_CLASS)
                authLog.put("username", username)
                authLog.put("action", "REGISTRATION")
                authLog.put("success", success)
                authLog.put("timestamp", Date())
                authLog.put("deviceInfo", getDeviceInfo())

                if (success) {
                    userRole?.let { authLog.put("userRole", it) }
                    val currentUser = ParseUser.getCurrentUser()
                    currentUser?.objectId?.let { authLog.put("userId", it) }
                    Log.d(TAG, "Registration successful for user: $username with role: $userRole")
                } else {
                    errorMessage?.let { authLog.put("errorMessage", it) }
                    Log.w(TAG, "Registration failed for user: $username - $errorMessage")
                }

                authLog.saveInBackground { e ->
                    if (e == null) {
                        Log.d(TAG, "Registration log saved successfully")
                    } else {
                        Log.e(TAG, "Failed to save registration log", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error logging registration attempt", e)
            }
        }
    }

    /**
     * Log user logout
     */
    suspend fun logLogout(
        username: String? = null,
        userId: String? = null,
    ) {
        withContext(Dispatchers.IO) {
            try {
                val authLog = ParseObject(AUTH_LOG_CLASS)
                authLog.put("username", username ?: "Unknown")
                authLog.put("action", "LOGOUT")
                authLog.put("success", true)
                authLog.put("timestamp", Date())
                authLog.put("deviceInfo", getDeviceInfo())

                userId?.let { authLog.put("userId", it) }

                authLog.saveInBackground { e ->
                    if (e == null) {
                        Log.d(TAG, "Logout log saved successfully for user: $username")
                    } else {
                        Log.e(TAG, "Failed to save logout log", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error logging logout", e)
            }
        }
    }

    /**
     * Log password reset request
     */
    suspend fun logPasswordReset(
        email: String,
        success: Boolean,
        errorMessage: String? = null,
    ) {
        withContext(Dispatchers.IO) {
            try {
                val authLog = ParseObject(AUTH_LOG_CLASS)
                authLog.put("username", email)
                authLog.put("action", "PASSWORD_RESET")
                authLog.put("success", success)
                authLog.put("timestamp", Date())
                authLog.put("deviceInfo", getDeviceInfo())

                if (!success) {
                    errorMessage?.let { authLog.put("errorMessage", it) }
                    Log.w(TAG, "Password reset failed for email: $email - $errorMessage")
                } else {
                    Log.d(TAG, "Password reset requested for email: $email")
                }

                authLog.saveInBackground { e ->
                    if (e == null) {
                        Log.d(TAG, "Password reset log saved successfully")
                    } else {
                        Log.e(TAG, "Failed to save password reset log", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error logging password reset", e)
            }
        }
    }

    /**
     * Get basic device information for logging
     */
    private fun getDeviceInfo(): String {
        return try {
            "Android ${android.os.Build.VERSION.RELEASE} - ${android.os.Build.MODEL}"
        } catch (e: Exception) {
            "Unknown Device"
        }
    }

    /**
     * Get authentication logs for admin dashboard (requires admin role)
     */
    suspend fun getAuthenticationLogs(
        limitValue: Int = 50,
        username: String? = null,
        action: String? = null,
        fromDate: Date? = null,
    ): List<Map<String, Any?>> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>(AUTH_LOG_CLASS)
                query.orderByDescending("timestamp")
                query.setLimit(limitValue)

                username?.let { query.whereEqualTo("username", it) }
                action?.let { query.whereEqualTo("action", it) }
                fromDate?.let { query.whereGreaterThanOrEqualTo("timestamp", it) }

                val results = query.find()
                results.map { log ->
                    mapOf(
                        "id" to log.objectId,
                        "username" to log.getString("username"),
                        "action" to log.getString("action"),
                        "success" to log.getBoolean("success"),
                        "timestamp" to log.getDate("timestamp"),
                        "deviceInfo" to log.getString("deviceInfo"),
                        "errorMessage" to log.getString("errorMessage"),
                        "userRole" to log.getString("userRole"),
                        "ipAddress" to log.getString("ipAddress"),
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching authentication logs", e)
                emptyList()
            }
        }
    }
}
