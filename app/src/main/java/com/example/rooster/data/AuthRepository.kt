package com.example.rooster.data

import android.util.Log
import com.example.rooster.AuthLogger
import com.parse.ParseException
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Date

/**
 * Repository for authentication operations
 * Provides clean API for authentication with proper error handling
 */
class AuthRepository {
    companion object {
        private const val TAG = "AuthRepository"
        private const val AUTH_TIMEOUT_MS = 60000L // 60 seconds for rural networks
    }

    /**
     * Authenticate user with username and password
     */
    suspend fun login(
        username: String,
        password: String,
    ): Result<AuthResult> {
        return withContext(Dispatchers.IO) {
            try {
                // Validate Parse configuration first
                if (!isParseConfigured()) {
                    return@withContext Result.failure(
                        AuthException("Parse server not properly configured"),
                    )
                }

                val loginResult =
                    withTimeoutOrNull(AUTH_TIMEOUT_MS) {
                        kotlin.runCatching {
                            var loginSuccess = false
                            var loginError: ParseException? = null
                            var loggedInUser: ParseUser? = null

                            ParseUser.logInInBackground(username, password) { user, e ->
                                loginSuccess = user != null
                                loginError = e
                                loggedInUser = user
                            }

                            // Wait for the callback to complete
                            var attempts = 0
                            while (!loginSuccess && loginError == null && attempts < 600) { // 60 seconds max
                                delay(100)
                                attempts++
                            }

                            if (loginSuccess && loggedInUser != null) {
                                loggedInUser
                            } else {
                                throw loginError ?: Exception("Login timeout after 60 seconds")
                            }
                        }
                    }

                loginResult?.fold(
                    onSuccess = { user ->
                        val userRole = user?.getString("userRole") ?: "general"
                        Log.d(TAG, "Login successful for user: $username with role: $userRole")

                        // Log successful login
                        AuthLogger.logLoginAttempt(
                            username = username,
                            success = true,
                            userRole = userRole,
                        )

                        Result.success(
                            AuthResult(
                                user = user,
                                userRole = userRole,
                                isSuccess = true,
                            ),
                        )
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Login failed for user: $username", exception)

                        // Log failed login
                        AuthLogger.logLoginAttempt(
                            username = username,
                            success = false,
                            errorMessage = exception.localizedMessage ?: exception.message,
                        )

                        Result.failure(
                            AuthException(
                                message = getErrorMessage(exception),
                                originalException = exception,
                            ),
                        )
                    },
                ) ?: run {
                    // Timeout occurred
                    val timeoutMessage = "Login timed out after 60 seconds"
                    AuthLogger.logLoginAttempt(
                        username = username,
                        success = false,
                        errorMessage = timeoutMessage,
                    )
                    Result.failure(AuthException(timeoutMessage))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login operation failed", e)
                Result.failure(AuthException("Authentication operation failed: ${e.message}", e))
            }
        }
    }

    /**
     * Register new user account
     */
    suspend fun register(
        username: String,
        email: String,
        password: String,
        role: String,
    ): Result<AuthResult> {
        return withContext(Dispatchers.IO) {
            try {
                // Validate Parse configuration first
                if (!isParseConfigured()) {
                    return@withContext Result.failure(
                        AuthException("Parse server not properly configured"),
                    )
                }

                val registrationResult =
                    withTimeoutOrNull(AUTH_TIMEOUT_MS) {
                        kotlin.runCatching {
                            var registrationSuccess = false
                            var registrationError: ParseException? = null

                            val user = ParseUser()
                            user.username = username
                            user.setPassword(password)
                            user.setEmail(email)
                            user.put("userRole", role)
                            user.put("displayName", username)
                            user.put("registrationDate", Date())
                            user.put("appVersion", "1.0")

                            user.signUpInBackground { e ->
                                registrationSuccess = e == null
                                registrationError = e
                            }

                            // Wait for the callback to complete
                            var attempts = 0
                            while (!registrationSuccess && registrationError == null && attempts < 600) {
                                delay(100)
                                attempts++
                            }

                            if (registrationSuccess) {
                                user
                            } else {
                                throw registrationError
                                    ?: Exception("Registration timeout after 60 seconds")
                            }
                        }
                    }

                registrationResult?.fold(
                    onSuccess = { user ->
                        Log.d(TAG, "Registration successful for user: $username with role: $role")

                        // Log successful registration
                        AuthLogger.logRegistrationAttempt(
                            username = username,
                            success = true,
                            userRole = role,
                        )

                        Result.success(
                            AuthResult(
                                user = user,
                                userRole = role,
                                isSuccess = true,
                            ),
                        )
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Registration failed for user: $username", exception)

                        // Log failed registration
                        AuthLogger.logRegistrationAttempt(
                            username = username,
                            success = false,
                            errorMessage = exception.localizedMessage ?: exception.message,
                            userRole = role,
                        )

                        Result.failure(
                            AuthException(
                                message = getErrorMessage(exception),
                                originalException = exception,
                            ),
                        )
                    },
                ) ?: run {
                    // Timeout occurred
                    val timeoutMessage = "Registration timed out after 60 seconds"
                    AuthLogger.logRegistrationAttempt(
                        username = username,
                        success = false,
                        errorMessage = timeoutMessage,
                        userRole = role,
                    )
                    Result.failure(AuthException(timeoutMessage))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Registration operation failed", e)
                Result.failure(AuthException("Registration operation failed: ${e.message}", e))
            }
        }
    }

    /**
     * Request password reset for email
     */
    suspend fun resetPassword(email: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Validate Parse configuration first
                if (!isParseConfigured()) {
                    return@withContext Result.failure(
                        AuthException("Parse server not properly configured"),
                    )
                }

                val resetResult =
                    withTimeoutOrNull(AUTH_TIMEOUT_MS) {
                        kotlin.runCatching {
                            var resetSuccess = false
                            var resetError: ParseException? = null

                            ParseUser.requestPasswordResetInBackground(email) { e ->
                                resetSuccess = e == null
                                resetError = e
                            }

                            // Wait for callback
                            var attempts = 0
                            while (!resetSuccess && resetError == null && attempts < 300) {
                                delay(100)
                                attempts++
                            }

                            if (resetSuccess) {
                                Unit
                            } else {
                                throw resetError ?: Exception("Password reset timeout")
                            }
                        }
                    }

                resetResult?.fold(
                    onSuccess = {
                        AuthLogger.logPasswordReset(email = email, success = true)
                        Result.success(Unit)
                    },
                    onFailure = { exception ->
                        AuthLogger.logPasswordReset(
                            email = email,
                            success = false,
                            errorMessage = exception.localizedMessage ?: exception.message,
                        )
                        Result.failure(AuthException(getErrorMessage(exception), exception))
                    },
                ) ?: run {
                    AuthLogger.logPasswordReset(
                        email = email,
                        success = false,
                        errorMessage = "Password reset timeout",
                    )
                    Result.failure(AuthException("Password reset timed out"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Password reset operation failed", e)
                Result.failure(AuthException("Password reset failed: ${e.message}", e))
            }
        }
    }

    /**
     * Get current authenticated user
     */
    fun getCurrentUser(): ParseUser? {
        return try {
            val user = ParseUser.getCurrentUser()
            if (user != null && user.isAuthenticated) {
                Log.d(TAG, "Current user: ${user.username}, role: ${user.getString("userRole")}")
                user
            } else {
                Log.d(TAG, "No authenticated user found")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user", e)
            null
        }
    }

    /**
     * Get current user role
     */
    fun getCurrentUserRole(): String {
        return try {
            val user = getCurrentUser()
            user?.getString("userRole") ?: "unknown"
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user role", e)
            "unknown"
        }
    }

    /**
     * Logout current user
     */
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = ParseUser.getCurrentUser()
                val username = currentUser?.username
                val userId = currentUser?.objectId

                ParseUser.logOut()

                // Log logout
                AuthLogger.logLogout(username = username, userId = userId)

                Log.d(TAG, "User logged out successfully")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Logout failed", e)
                Result.failure(AuthException("Logout failed: ${e.message}", e))
            }
        }
    }

    /**
     * Test Parse connection and user creation
     */
    suspend fun testParseConnection(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Validate Parse configuration first
                if (!isParseConfigured()) {
                    return@withContext Result.failure(
                        AuthException("Parse server not properly configured"),
                    )
                }

                // Test basic object creation
                val testObject = com.parse.ParseObject("TestObject")
                testObject.put("testKey", "testValue")
                testObject.put("timestamp", Date())

                val saveResult =
                    withTimeoutOrNull(30000) {
                        kotlin.runCatching {
                            testObject.save()
                            testObject.objectId
                        }.getOrElse { null }
                    }

                if (saveResult != null) {
                    // Test user creation
                    val testUsername = "test_${System.currentTimeMillis()}"
                    val testUser = ParseUser()
                    testUser.username = testUsername
                    testUser.setPassword("testpass123")
                    testUser.setEmail("testuser@example.com")
                    testUser.put("userRole", "farmer")
                    testUser.put("isTestUser", true)

                    val userResult =
                        withTimeoutOrNull(30000) {
                            kotlin.runCatching {
                                var userSuccess = false
                                var userError: ParseException? = null

                                testUser.signUpInBackground { e ->
                                    userSuccess = e == null
                                    userError = e
                                }

                                // Wait for callback
                                var attempts = 0
                                while (!userSuccess && userError == null && attempts < 300) {
                                    delay(100)
                                    attempts++
                                }

                                if (userSuccess) {
                                    // Clean up test user
                                    testUser.delete()
                                    true
                                } else {
                                    throw userError ?: Exception("User creation timeout")
                                }
                            }.getOrElse { false }
                        }

                    // Clean up test object
                    try {
                        testObject.delete()
                    } catch (cleanup: Exception) {
                        Log.w(TAG, "Failed to cleanup test object", cleanup)
                    }

                    if (userResult == true) {
                        Result.success("✅ Parse connection & user creation tests successful!")
                    } else {
                        Result.failure(AuthException("❌ User creation test failed"))
                    }
                } else {
                    Result.failure(AuthException("❌ Basic Parse connection failed"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Parse test failed", e)
                Result.failure(AuthException("Parse test failed: ${e.message}", e))
            }
        }
    }

    /**
     * Convert Parse exceptions to user-friendly error messages
     */
    private fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is ParseException ->
                when (exception.code) {
                    101 -> "Invalid username or password" // ParseException.OBJECT_NOT_FOUND
                    100 -> "Connection to server failed. Please check your internet connection."
                    202 -> "Username already taken. Please choose another." // ParseException.USERNAME_TAKEN
                    203 -> "Email already taken. Please use a different email address."
                    205 -> "Email address is invalid."
                    ParseException.CONNECTION_FAILED -> "Network connection failed. Please check your internet connection and try again."
                    ParseException.TIMEOUT -> "Connection timed out. Please check your internet speed and try again."
                    ParseException.EMAIL_NOT_FOUND -> "Email address not found"
                    ParseException.INVALID_SESSION_TOKEN -> "Your session has expired. Please log in again."
                    ParseException.INVALID_ACL -> "Permission denied. Please contact support."
                    else -> "Authentication failed: ${exception.localizedMessage}"
                }

            else ->
                when {
                    exception.message?.contains("timeout", true) == true ->
                        "Operation timed out after 60 seconds. Please check your internet connection and try again."

                    exception.message?.contains("network", true) == true ||
                        exception.message?.contains("connection", true) == true ->
                        "Network error. Please check your internet connection and try again."

                    exception.message?.contains("I/O", true) == true ->
                        "Network I/O error. Please check your connection and try again."

                    else -> "Authentication failed: ${exception.localizedMessage ?: exception.message ?: "Unknown error"}"
                }
        }
    }

    /**
     * Check if Parse is properly configured
     */
    private fun isParseConfigured(): Boolean {
        return try {
            // Check if Parse is initialized and configured
            val testObject = com.parse.ParseObject("TestConnection")
            testObject != null
        } catch (e: Exception) {
            Log.e(TAG, "Parse configuration check failed", e)
            false
        }
    }
}

/**
 * Authentication result data class
 */
data class AuthResult(
    val user: ParseUser?,
    val userRole: String,
    val isSuccess: Boolean,
)

/**
 * Custom authentication exception
 */
class AuthException(
    message: String,
    val originalException: Throwable? = null,
) : Exception(message, originalException)
