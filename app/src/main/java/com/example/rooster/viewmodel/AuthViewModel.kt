package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.domain.repository.AuthRepository
import com.example.rooster.domain.repository.UserRepository
import com.example.rooster.models.UserRole
import com.example.rooster.util.Result
import com.parse.ParseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Data Models
data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showOtpDialog: Boolean = false,
    val phoneNumber: String = "",
    val otpSent: Boolean = false,
    val isVerifyingOtp: Boolean = false,
    val userRole: UserRole = UserRole.UNKNOWN,
)

sealed class AuthState {
    object Idle : AuthState()

    object Processing : AuthState()

    object AwaitingOtp : AuthState()

    object Success : AuthState()

    data class Failed(val message: String) : AuthState()
}

// ViewModel
@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(AuthUiState())
        val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

        val normalizedUserRole: UserRole
            get() = _uiState.value.userRole

        val isAuthenticated: Boolean
            get() = _uiState.value.isAuthenticated

        init {
            viewModelScope.launch {
                authRepository.currentUser.collect {
                    _uiState.value = _uiState.value.copy(isAuthenticated = (it != null))
                }
            }
        }

        fun checkAuthState() {
            viewModelScope.launch {
                try {
                    _uiState.value = _uiState.value.copy(loading = true)
                    val user = getCurrentUser()
                    if (user != null) {
                        val role = getUserRole()
                        _uiState.value =
                            _uiState.value.copy(
                                isAuthenticated = true,
                                userRole = role,
                                loading = false,
                            )
                    } else {
                        _uiState.value =
                            _uiState.value.copy(
                                isAuthenticated = false,
                                userRole = UserRole.UNKNOWN,
                                loading = false,
                            )
                    }
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            loading = false,
                            errorMessage = e.message,
                        )
                }
            }
        }

        private suspend fun getCurrentUser(): ParseUser? {
            return try {
                ParseUser.getCurrentUser()
            } catch (e: Exception) {
                null
            }
        }

        private suspend fun getUserRole(): UserRole {
            return try {
                userRepository.getUserRole()
            } catch (e: Exception) {
                UserRole.UNKNOWN
            }
        }

        fun sendOtp(phoneNumber: String) {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null, successMessage = null)
            viewModelScope.launch {
                when (val result = authRepository.requestOtp(phoneNumber)) {
                    is Result.Success -> {
                        _uiState.value =
                            _uiState.value.copy(
                                loading = false,
                                showOtpDialog = true,
                                phoneNumber = phoneNumber,
                                otpSent = true,
                                successMessage = "OTP sent successfully",
                            )
                    }
                    is Result.Error -> {
                        _uiState.value =
                            _uiState.value.copy(
                                loading = false,
                                errorMessage = result.exception.message ?: "Failed to send OTP",
                            )
                    }
                }
            }
        }

        fun verifyOtp(otp: String) {
            _uiState.value = _uiState.value.copy(isVerifyingOtp = true, errorMessage = null, successMessage = null)
            viewModelScope.launch {
                when (val result = authRepository.verifyOtp(_uiState.value.phoneNumber, otp)) {
                    is Result.Success -> {
                        _uiState.value =
                            _uiState.value.copy(
                                isVerifyingOtp = false,
                                isAuthenticated = true,
                                showOtpDialog = false,
                                successMessage = "OTP verified successfully. Welcome!",
                            )
                    }
                    is Result.Error -> {
                        _uiState.value =
                            _uiState.value.copy(
                                isVerifyingOtp = false,
                                errorMessage = result.exception.message ?: "OTP verification failed",
                            )
                    }
                }
            }
        }

        fun login(
            phoneNumber: String,
            password: String,
        ) {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null, successMessage = null)
            viewModelScope.launch {
                when (val result = authRepository.login(phoneNumber, password)) {
                    is Result.Success -> {
                        _uiState.value =
                            _uiState.value.copy(
                                loading = false,
                                isAuthenticated = true,
                                successMessage = "Login successful. Welcome back!",
                            )
                    }
                    is Result.Error -> {
                        _uiState.value =
                            _uiState.value.copy(
                                loading = false,
                                errorMessage = result.exception.message ?: "Login failed",
                            )
                    }
                }
            }
        }

        fun register(
            name: String,
            phoneNumber: String,
            password: String,
        ) {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null, successMessage = null)
            viewModelScope.launch {
                when (val result = authRepository.register(name, phoneNumber, password)) {
                    is Result.Success -> {
                        _uiState.value =
                            _uiState.value.copy(
                                loading = false,
                                isAuthenticated = true,
                                successMessage = "Registration successful. Welcome!",
                            )
                    }
                    is Result.Error -> {
                        _uiState.value =
                            _uiState.value.copy(
                                loading = false,
                                errorMessage = result.exception.message ?: "Registration failed",
                            )
                    }
                }
            }
        }

        fun logout() {
            viewModelScope.launch {
                authRepository.logout()
                _uiState.value = AuthUiState() // Reset to initial state after logout
            }
        }

        fun clearMessages() {
            _uiState.value =
                _uiState.value.copy(
                    errorMessage = null,
                    successMessage = null,
                )
        }

        fun dismissOtpDialog() {
            _uiState.value =
                _uiState.value.copy(
                    showOtpDialog = false,
                    otpSent = false,
                    isVerifyingOtp = false,
                )
        }

        fun validatePhoneNumber(phoneNumber: String): String? {
            val cleanNumber = phoneNumber.replace("+91", "").replace(" ", "").replace("-", "")
            return when {
                cleanNumber.isBlank() -> "Phone number cannot be empty"
                cleanNumber.length != 10 -> "Phone number must be 10 digits"
                !cleanNumber.all { it.isDigit() } -> "Phone number can only contain digits"
                !cleanNumber.startsWith("6") && !cleanNumber.startsWith("7") &&
                    !cleanNumber.startsWith("8") && !cleanNumber.startsWith("9") ->
                    "Invalid Indian mobile number"
                else -> null
            }
        }

        fun validatePassword(password: String): String? {
            return when {
                password.isBlank() -> "Password cannot be empty"
                password.length < 6 -> "Password must be at least 6 characters"
                else -> null
            }
        }

        fun validateName(name: String): String? {
            return when {
                name.isBlank() -> "Name cannot be empty"
                else -> null
            }
        }
    }
