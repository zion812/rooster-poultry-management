package com.example.rooster.feature.auth.ui

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

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorResId: Int? = null,
    val errorMessage: String? = null,
    val registrationSuccess: Boolean = false,
    val registeredEmail: String? = null // To pass to CheckEmailScreen
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun registerUser(name: String, email: String, phone: String?, pass: String, confirmPass: String, role: UserRole) {
        // Basic local validation
        if (name.isBlank() || email.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All fields marked as required must be filled.", isLoading = false) }
            return
        }
        if (pass != confirmPass) {
            _uiState.update { it.copy(errorMessage = "The passwords you entered do not match.", isLoading = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorResId = null, errorMessage = null) }
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    // Send verification email
                    try {
                        firebaseUser.sendEmailVerification().await()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                registrationSuccess = true,
                                registeredEmail = firebaseUser.email
                            )
                        }
                    } catch (verificationException: Exception) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Your account was created, but we failed to send a verification email. You can try resending it from the next screen.",
                                registrationSuccess = true,
                                registeredEmail = firebaseUser.email
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Registration failed. Please check your details and try again."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Registration failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun navigationToEmailVerificationComplete() {
        _uiState.update { it.copy(registrationSuccess = false, registeredEmail = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorResId = null, errorMessage = null) }
    }
}
