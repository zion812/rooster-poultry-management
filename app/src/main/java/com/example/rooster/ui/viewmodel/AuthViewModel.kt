package com.example.rooster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.auth.domain.model.AuthState
import com.example.rooster.core.auth.domain.model.User // Ensure this is the correct User model
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- UI State Definitions ---
sealed class LoginResult {
    data class Success(val userRole: UserRole, val isEmailVerified: Boolean, val email: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
    object Loading : LoginResult()
    object Idle : LoginResult()
}

sealed class RegistrationResult {
    data class Success(val email: String) : RegistrationResult() // Contains email for verification step
    data class Error(val message: String) : RegistrationResult()
    object Loading : RegistrationResult()
    object Idle : RegistrationResult()
}

sealed class EmailVerificationResult {
    object Success : EmailVerificationResult()
    data class Error(val message: String) : EmailVerificationResult()
    object Loading : EmailVerificationResult()
    object Idle : EmailVerificationResult()
}

data class AuthScreenUiState(
    val currentAuthState: AuthState = AuthState.Loading, // Overall auth state (from repo)
    val currentUser: User? = null, // Current user from repo
    val isOverallLoading: Boolean = true, // Initial loading from repo

    val loginEmailInput: String = "",
    val loginPasswordInput: String = "",
    val loginResult: LoginResult = LoginResult.Idle,

    val registerNameInput: String = "",
    val registerEmailInput: String = "",
    val registerPasswordInput: String = "",
    val registerRoleSelected: UserRole = UserRole.FARMER, // Default role
    val registrationResult: RegistrationResult = RegistrationResult.Idle,

    val emailForVerification: String = "", // Email to show on CheckEmailScreen
    val emailVerificationResult: EmailVerificationResult = EmailVerificationResult.Idle,

    val globalErrorMessage: String? = null // For general errors not tied to a specific process
)


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthScreenUiState())
    val uiState: StateFlow<AuthScreenUiState> = _uiState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _uiState.update { currentState ->
                    currentState.copy(
                        currentAuthState = if (user != null) AuthState.Authenticated else AuthState.Unauthenticated,
                        currentUser = user,
                        isOverallLoading = false,
                        emailForVerification = user?.email ?: currentState.emailForVerification // Persist email if user exists
                    )
                }
            }
        }
    }

    fun onLoginEmailChanged(email: String) {
        _uiState.update { it.copy(loginEmailInput = email) }
    }

    fun onLoginPasswordChanged(password: String) {
        _uiState.update { it.copy(loginPasswordInput = password) }
    }

    fun loginUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(loginResult = LoginResult.Loading) }
            try {
                val email = _uiState.value.loginEmailInput
                val password = _uiState.value.loginPasswordInput
                // Assuming authRepository.signIn returns a User object or throws an exception
                val user = authRepository.signIn(email, password) // This needs to exist in AuthRepository

                if (user != null) {
                    // Here, you need to determine if email is verified.
                    // This logic might be part of the User model or a separate call.
                    // For now, let's assume a property `isEmailVerified` on the User model.
                    // val isEmailVerified = user.isEmailVerified // Example
                    val isEmailVerified = authRepository.isUserEmailVerified() // Prefer this if available

                    _uiState.update {
                        it.copy(
                            loginResult = LoginResult.Success(user.role, isEmailVerified, user.email),
                            currentUser = user, // Update current user in state
                            emailForVerification = user.email // Set for potential navigation to CheckEmail
                        )
                    }
                } else {
                    // This case might not be reached if signIn throws an exception on failure
                    _uiState.update { it.copy(loginResult = LoginResult.Error("Login failed. User not found.")) }
                }
            } catch (e: Exception) { // Catch specific exceptions if possible
                _uiState.update { it.copy(loginResult = LoginResult.Error(e.message ?: "Login failed")) }
            }
        }
    }

    fun onRegisterNameChanged(name: String) {
        _uiState.update { it.copy(registerNameInput = name) }
    }

    fun onRegisterEmailChanged(email: String) {
        _uiState.update { it.copy(registerEmailInput = email) }
    }

    fun onRegisterPasswordChanged(password: String) {
        _uiState.update { it.copy(registerPasswordInput = password) }
    }

    fun onRegisterRoleChanged(role: UserRole) {
        _uiState.update { it.copy(registerRoleSelected = role) }
    }

    fun registerUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(registrationResult = RegistrationResult.Loading) }
            try {
                val name = _uiState.value.registerNameInput
                val email = _uiState.value.registerEmailInput
                val password = _uiState.value.registerPasswordInput
                val role = _uiState.value.registerRoleSelected

                // Assuming authRepository.registerUser sends a verification email internally
                // and returns a User object or similar indication of success.
                authRepository.signUp(name, email, password, role) // This needs to exist

                // After successful registration, backend likely sends a verification email.
                // Navigate to CheckEmailScreen.
                _uiState.update {
                    it.copy(
                        registrationResult = RegistrationResult.Success(email),
                        emailForVerification = email // Set for CheckEmailScreen
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(registrationResult = RegistrationResult.Error(e.message ?: "Registration failed")) }
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            // _uiState.update { it.copy(passwordResetResult = Loading) }
            try {
                authRepository.sendPasswordResetEmail(email)
                // _uiState.update { it.copy(passwordResetResult = Success) }
            } catch (e: Exception) {
                // _uiState.update { it.copy(passwordResetResult = Error(e.message)) }
            }
        }
    }

    fun resendVerificationEmail() {
        val email = _uiState.value.emailForVerification
        if (email.isBlank()) {
            _uiState.update { it.copy(emailVerificationResult = EmailVerificationResult.Error("Email not available to resend verification.")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(emailVerificationResult = EmailVerificationResult.Loading) }
            try {
                // Assuming your authRepository has a method to resend the verification email
                authRepository.sendVerificationEmail(email) // This needs to exist
                _uiState.update { it.copy(emailVerificationResult = EmailVerificationResult.Success) } // Or Idle if it's just a fire-and-forget
            } catch (e: Exception) {
                _uiState.update { it.copy(emailVerificationResult = EmailVerificationResult.Error(e.message ?: "Failed to resend email.")) }
            }
        }
    }

    fun checkEmailVerificationStatus() {
        val email = _uiState.value.emailForVerification
        if (email.isBlank()) return // Should not happen if navigated correctly

        viewModelScope.launch {
            _uiState.update { it.copy(emailVerificationResult = EmailVerificationResult.Loading) }
            try {
                // This typically involves re-fetching the user or calling a specific check endpoint
                val isVerified = authRepository.isUserEmailVerified() // Needs to refresh user state
                if (isVerified) {
                    _uiState.update { it.copy(emailVerificationResult = EmailVerificationResult.Success) }
                } else {
                    // Stay on screen, maybe show a subtle message "Still pending" or rely on user action
                    _uiState.update { it.copy(emailVerificationResult = EmailVerificationResult.Idle) } // Or a specific "NotVerifiedYet" state
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(emailVerificationResult = EmailVerificationResult.Error(e.message ?: "Failed to check status."))}
            }
        }
    }


    fun resetLoginResult() {
        _uiState.update { it.copy(loginResult = LoginResult.Idle) }
    }

    fun resetRegistrationResult() {
        _uiState.update { it.copy(registrationResult = RegistrationResult.Idle) }
    }

    fun resetEmailVerificationResult() {
        _uiState.update { it.copy(emailVerificationResult = EmailVerificationResult.Idle) }
    }


    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            // Auth state observer will update currentAuthState and currentUser
            // Reset specific UI states if necessary
            _uiState.update { currentState ->
                currentState.copy(
                    loginEmailInput = "",
                    loginPasswordInput = "",
                    loginResult = LoginResult.Idle,
                    // Reset other relevant fields if needed
                )
            }
        }
    }
}