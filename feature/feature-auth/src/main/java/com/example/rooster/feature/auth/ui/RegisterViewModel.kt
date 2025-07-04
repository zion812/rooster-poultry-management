package com.example.rooster.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorResId: Int? = null,
    val errorMessage: String? = null,
    val registrationSuccess: Boolean = false,
    val registeredEmail: String? = null // To pass to CheckEmailScreen
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun registerUser(name: String, email: String, phone: String?, pass: String, confirmPass: String, role: UserRole) {
        // Basic local validation
        if (name.isBlank() || email.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            _uiState.update { it.copy(errorResId = R.string.error_all_fields_required_except_phone, isLoading = false) }
            return
        }
        if (pass != confirmPass) {
            _uiState.update { it.copy(errorResId = R.string.error_passwords_do_not_match, isLoading = false) }
            return
        }
        // Add more validation as needed (e.g., email format, password strength)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorResId = null, errorMessage = null) }
            try {
                val result = authRepository.signUp(
                    email = email,
                    password = pass,
                    name = name,
                    role = role,
                    phoneNumber = phone?.takeIf { it.isNotBlank() }
                )

                result.fold(
                    onSuccess = { newUser ->
                        // After successful signup, Firebase automatically signs the user in.
                        // Now send verification email for the current (newly signed up) user.
                        val verificationResult = authRepository.sendCurrentUserEmailVerification()
                        verificationResult.fold(
                            onSuccess = {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        registrationSuccess = true,
                                        registeredEmail = newUser.email
                                    )
                                }
                            },
                            onFailure = { verificationException ->
                                // User created, but sending verification email failed.
                                // This is a tricky state. For now, report error.
                                // Could also allow proceeding to CheckEmailScreen and let user try resend.
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorResId = R.string.error_sending_verification_email_after_registration,
                                        errorMessage = verificationException.message,
                                        // Still set registrationSuccess to true if we want to navigate to CheckEmailScreen
                                        // registrationSuccess = true,
                                        // registeredEmail = newUser.email
                                    )
                                }
                            }
                        )
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorResId = R.string.error_registration_failed, // Generic registration error
                                errorMessage = exception.message // Can be more specific if exception is parsed
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorResId = R.string.error_unexpected,
                        errorMessage = e.message
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
