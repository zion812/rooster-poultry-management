package com.example.rooster.feature.auth.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val isEmailVerified: Boolean,
    val role: UserRole? = null
)

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
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, role: UserRole) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Email and password cannot be empty.",
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorResId = null, errorMessage = null) }
            try {
                println("Attempting login for role: $role")

                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    val user = User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email,
                        displayName = firebaseUser.displayName,
                        isEmailVerified = firebaseUser.isEmailVerified,
                        role = role // Use the selected role for now
                    )

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
                                errorMessage = "Your email address is not verified. Please check your inbox."
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Login failed. Please try again."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Login failed: ${e.message}"
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