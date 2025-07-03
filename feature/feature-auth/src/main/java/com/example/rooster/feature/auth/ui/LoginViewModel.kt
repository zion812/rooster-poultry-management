package com.example.rooster.feature.auth.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Remove AuthState import if not directly used, or ensure it's relevant for future use.
// import com.example.rooster.core.auth.domain.model.AuthState
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.auth.domain.repository.AuthRepository
import com.example.rooster.core.common.R // Import R class for string resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
 feat/login-screen-v1
import com.example.rooster.core.auth.domain.model.User // Import User model

main
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    @StringRes val errorResId: Int? = null, // For R.string resource IDs
    val errorMessage: String? = null,       // For direct error messages (e.g., from backend)
    val isAuthenticated: Boolean = false,
    val navigateToHome: Boolean = false,
    val navigateToRegister: Boolean = false
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
                    errorResId = R.string.error_email_password_empty, // Use string resource ID
                    errorMessage = null,
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorResId = null, errorMessage = null) }
            try {
                // Role usage note from previous step still applies.
                println("Attempting login for role: $role")

                val result = authRepository.signIn(email, password)

                result.fold(
                    onSuccess = { user ->
                        // Optional: Validate user.role against selected 'role' from UI
                        // if (user.role != role) {
                        //     _uiState.update {
                        //         it.copy(isLoading = false, errorResId = R.string.error_role_mismatch)
                        //     }
                        //     // Consider signing out if role mismatch is critical
                        //     // authRepository.signOut()
                        //     return@launch
                        // }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isAuthenticated = true,
                                navigateToHome = true
                            )
                        }
                    },
                    onFailure = { exception ->
                        // Prefer specific error messages from backend if available, else fallback
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                // Pass backend message if useful, otherwise generic error
                                errorMessage = exception.message, // This could be too technical
                                errorResId = R.string.error_login_failed // Fallback generic message
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                // Catch-all for unexpected errors during the process
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorResId = R.string.error_unexpected, // Generic unexpected error
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun onNavigateToRegister() {
        _uiState.update { it.copy(navigateToRegister = true) }
    }

    fun navigationToHomeComplete() {
        _uiState.update { it.copy(navigateToHome = false) }
    }

    fun navigationToRegisterComplete() {
        _uiState.update { it.copy(navigateToRegister = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorResId = null, errorMessage = null) }
    }
}
// Notes:
// 1. `LoginUiState` now has `errorResId: Int?` for string resource IDs and
//    `errorMessage: String?` for direct messages. This provides flexibility.
// 2. Imported `com.example.rooster.core.common.R` to access the string resources.
// 3. `login` function updated:
//    - For blank email/password, it now sets `errorResId = R.string.error_email_password_empty`.
//    - On successful login, clears any previous errors.
//    - On failure from `authRepository.signIn`:
//        - It sets `errorMessage = exception.message` (which might be a technical backend message).
//        - It also sets `errorResId = R.string.error_login_failed` as a fallback or primary display message.
//          The UI can decide to show the `errorMessage` if it's user-friendly, or default to the `errorResId`.
//          Current LoginScreen implementation prioritizes errorResId then errorMessage.
//    - For other exceptions, it sets `errorResId = R.string.error_unexpected`.
// 4. `clearError` now clears both `errorResId` and `errorMessage`.
// 5. The logic for role mismatch (commented out) also uses `errorResId = R.string.error_role_mismatch`.
// 6. Removed unused `AuthState` import for now to keep it clean.
