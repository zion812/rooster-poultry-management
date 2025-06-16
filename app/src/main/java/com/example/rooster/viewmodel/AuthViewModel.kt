package com.example.rooster.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.AnalyticsTracker
import com.example.rooster.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for authentication operations
 * Manages UI state and coordinates with AuthRepository
 */
class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Attempt user login
     */
    fun login(
        username: String,
        password: String,
    ) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting login process for user: $username")

                _uiState.value =
                    _uiState.value.copy(
                        loading = true,
                        errorMessage = "",
                        successMessage = "",
                    )

                val result = authRepository.login(username.trim(), password)

                result.fold(
                    onSuccess = { authResult ->
                        Log.d(
                            TAG,
                            "Login successful for user: $username with role: ${authResult.userRole}",
                        )

                        // Track successful login
                        AnalyticsTracker.trackLogin(authResult.userRole)

                        _uiState.value =
                            _uiState.value.copy(
                                loading = false,
                                isAuthenticated = true,
                                userRole = authResult.userRole,
                                successMessage = "Login successful!",
                                errorMessage = "",
                            )
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Login failed for user: $username", exception)

                        _uiState.value =
                            _uiState.value.copy(
                                loading = false,
                                isAuthenticated = false,
                                errorMessage = exception.message ?: "Login failed",
                                successMessage = "",
                            )
                    },
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during login", e)
                _uiState.value =
                    _uiState.value.copy(
                        loading = false,
                        isAuthenticated = false,
                        errorMessage = "Unexpected error occurred. Please try again.",
                        successMessage = "",
                    )
            }
        }
    }

    /**
     * Register new user account
     */
    fun register(
        username: String,
        email: String,
        password: String,
        role: String,
    ) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting registration process for user: $username with role: $role")

                _uiState.value =
                    _uiState.value.copy(
                        loading = true,
                        errorMessage = "",
                        successMessage = "",
                    )

                val result = authRepository.register(username.trim(), email.trim(), password, role)

                result.fold(
                    onSuccess = { authResult ->
                        Log.d(
                            TAG,
                            "Registration successful for user: $username with role: ${authResult.userRole}",
                        )

                        // Track successful registration
                        AnalyticsTracker.trackLogin(authResult.userRole)

                        _uiState.value =
                            _uiState.value.copy(
                                loading = false,
                                isAuthenticated = true,
                                userRole = authResult.userRole,
                                successMessage = "Account created successfully!",
                                errorMessage = "",
                            )
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Registration failed for user: $username", exception)

                        _uiState.value =
                            _uiState.value.copy(
                                loading = false,
                                isAuthenticated = false,
                                errorMessage = exception.message ?: "Registration failed",
                                successMessage = "",
                            )
                    },
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during registration", e)
                _uiState.value =
                    _uiState.value.copy(
                        loading = false,
                        isAuthenticated = false,
                        errorMessage = "Unexpected error occurred. Please try again.",
                        successMessage = "",
                    )
            }
        }
    }

    /**
     * Request password reset
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting password reset for email: $email")

                _uiState.value =
                    _uiState.value.copy(
                        resetLoading = true,
                        errorMessage = "",
                        successMessage = "",
                    )

                val result = authRepository.resetPassword(email.trim())

                result.fold(
                    onSuccess = {
                        Log.d(TAG, "Password reset email sent successfully")

                        _uiState.value =
                            _uiState.value.copy(
                                resetLoading = false,
                                successMessage = "Password reset email sent! Check your inbox.",
                                errorMessage = "",
                                showPasswordReset = false,
                            )
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Password reset failed for email: $email", exception)

                        _uiState.value =
                            _uiState.value.copy(
                                resetLoading = false,
                                errorMessage = exception.message ?: "Password reset failed",
                                successMessage = "",
                            )
                    },
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during password reset", e)
                _uiState.value =
                    _uiState.value.copy(
                        resetLoading = false,
                        errorMessage = "Unexpected error occurred. Please try again.",
                        successMessage = "",
                    )
            }
        }
    }

    /**
     * Test Parse connection
     */
    fun testParseConnection() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting Parse connection test")

                _uiState.value =
                    _uiState.value.copy(
                        loading = true,
                        errorMessage = "",
                        successMessage = "",
                    )

                val result = authRepository.testParseConnection()

                result.fold(
                    onSuccess = { message ->
                        Log.d(TAG, "Parse connection test successful")

                        _uiState.value =
                            _uiState.value.copy(
                                loading = false,
                                successMessage = message,
                                errorMessage = "",
                            )
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Parse connection test failed", exception)

                        _uiState.value =
                            _uiState.value.copy(
                                loading = false,
                                errorMessage = exception.message ?: "Parse connection test failed",
                                successMessage = "",
                            )
                    },
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during Parse test", e)
                _uiState.value =
                    _uiState.value.copy(
                        loading = false,
                        errorMessage = "Parse test failed: ${e.message}",
                        successMessage = "",
                    )
            }
        }
    }

    /**
     * Logout current user
     */
    fun logout() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting logout process")

                val result = authRepository.logout()

                result.fold(
                    onSuccess = {
                        Log.d(TAG, "Logout successful")

                        _uiState.value = AuthUiState() // Reset to initial state
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Logout failed", exception)

                        // Force reset state even if logout failed
                        _uiState.value = AuthUiState()
                    },
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during logout", e)

                // Force reset state on any error
                _uiState.value = AuthUiState()
            }
        }
    }

    /**
     * Clear error messages
     */
    fun clearMessages() {
        _uiState.value =
            _uiState.value.copy(
                errorMessage = "",
                successMessage = "",
            )
    }

    /**
     * Toggle password reset dialog
     */
    fun togglePasswordReset(show: Boolean) {
        _uiState.value =
            _uiState.value.copy(
                showPasswordReset = show,
            )
        if (!show) {
            clearMessages()
        }
    }

    /**
     * Check current authentication state
     */
    fun checkAuthState() {
        try {
            val currentUser = authRepository.getCurrentUser()
            val currentRole = authRepository.getCurrentUserRole()

            if (currentUser != null && currentRole != "unknown") {
                Log.d(
                    TAG,
                    "User already authenticated: ${currentUser.username} with role: $currentRole",
                )

                _uiState.value =
                    _uiState.value.copy(
                        isAuthenticated = true,
                        userRole = currentRole,
                    )
            } else {
                Log.d(TAG, "No authenticated user found")

                _uiState.value =
                    _uiState.value.copy(
                        isAuthenticated = false,
                        userRole = "",
                    )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking auth state", e)

            _uiState.value =
                _uiState.value.copy(
                    isAuthenticated = false,
                    userRole = "",
                )
        }
    }

    /**
     * Validate input fields
     */
    fun validateInput(
        username: String,
        password: String,
        email: String = "",
        isLogin: Boolean = true,
    ): String? {
        return when {
            username.isBlank() -> "Please enter a username"
            password.length < 4 -> "Password must be at least 4 characters"
            !isLogin && email.isBlank() -> "Please enter an email address"
            !isLogin && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Please enter a valid email address"

            else -> null
        }
    }
}

/**
 * UI State for authentication screens
 */
data class AuthUiState(
    val loading: Boolean = false,
    val resetLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userRole: String = "",
    val errorMessage: String = "",
    val successMessage: String = "",
    val showPasswordReset: Boolean = false,
)
