package com.example.rooster.feature.auth.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.auth.domain.model.User
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.auth.domain.repository.AuthRepository
import com.example.rooster.core.common.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    @StringRes val errorResId: Int? = null,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val loggedInUserRole: UserRole? = null,
    val navigateToRegister: Boolean = false,
    val requiresEmailVerification: Boolean = false,
    val unverifiedEmail: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, role: UserRole) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(
                    errorResId = R.string.error_email_password_empty,
                    errorMessage = null,
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorResId = null, errorMessage = null) }
            try {
                println("Attempting login for role: $role")

                val result = authRepository.signIn(email, password)

                result.fold(
                    onSuccess = { user ->
                        if (user.isEmailVerified) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isAuthenticated = true,
                                    loggedInUserRole = user.role,
                                    requiresEmailVerification = false,
                                    unverifiedEmail = null
                                )
                            }
                        } else {
                            // User authenticated but email not verified
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isAuthenticated = false,
                                    loggedInUserRole = null,
                                    requiresEmailVerification = true,
                                    unverifiedEmail = user.email,
                                    errorResId = R.string.error_email_not_verified
                                )
                            }
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = exception.message,
                                errorResId = R.string.error_login_failed
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorResId = R.string.error_unexpected,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun onNavigateToRegister() {
        _uiState.update { it.copy(navigateToRegister = true) }
    }

    fun navigationToRoleGraphComplete() {
        _uiState.update { it.copy(loggedInUserRole = null) }
    }

    fun navigationToEmailVerificationScreenComplete() {
        _uiState.update { it.copy(requiresEmailVerification = false, unverifiedEmail = null) }
    }

    fun navigationToRegisterComplete() {
        _uiState.update { it.copy(navigateToRegister = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorResId = null, errorMessage = null) }
    }
}