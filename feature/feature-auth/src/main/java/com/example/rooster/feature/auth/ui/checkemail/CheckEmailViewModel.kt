package com.example.rooster.feature.auth.ui.checkemail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class CheckEmailUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorResId: Int? = null,
    val errorMessage: String? = null,
    val isEmailVerified: Boolean = false,
    val countdownSeconds: Int = 0, // 0 means button is active or not in countdown
    val verificationEmailSentMessage: Int? = null
)

private const val RESEND_DELAY_SECONDS = 60
private const val VERIFICATION_CHECK_INTERVAL_MS = 5000L // 5 seconds

@HiltViewModel
class CheckEmailViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckEmailUiState())
    val uiState: StateFlow<CheckEmailUiState> = _uiState.asStateFlow()

    private var verificationCheckJob: Job? = null
    private var countdownJob: Job? = null

    init {
        val emailArg: String = savedStateHandle["email"] ?: ""
        _uiState.update { it.copy(email = emailArg) }
        if (emailArg.isNotBlank()) {
            sendVerificationEmail(isInitialSend = true)
        } else {
            _uiState.update { it.copy(errorMessage = "Email is missing for verification process.") }
        }
    }

    fun sendVerificationEmail(isInitialSend: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorResId = null, errorMessage = null, verificationEmailSentMessage = null) }
            try {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    user.sendEmailVerification().await()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = if (isInitialSend) "Verification email sent successfully." else "Verification email resent successfully."
                        )
                    }
                    if (!isInitialSend) { // Start countdown only on resend
                        startResendCountdown()
                    }
                    startEmailVerificationCheck() // Always start checking after sending
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No current user found."
                        )
                    }
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to send verification email: ${exception.message}"
                    )
                }
            }
        }
    }

    fun resendVerificationEmail() {
        if (_uiState.value.countdownSeconds > 0) return // Already in countdown

        // Cancel any ongoing countdown before starting a new one
        countdownJob?.cancel()
        sendVerificationEmail(isInitialSend = false)
    }

    private fun startResendCountdown() {
        countdownJob?.cancel() // Cancel any existing countdown
        countdownJob = viewModelScope.launch {
            for (i in RESEND_DELAY_SECONDS downTo 1) {
                _uiState.update { it.copy(countdownSeconds = i) }
                delay(1000)
            }
            _uiState.update { it.copy(countdownSeconds = 0) }
        }
    }

    private fun startEmailVerificationCheck() {
        verificationCheckJob?.cancel() // Cancel any existing job
        verificationCheckJob = viewModelScope.launch {
            while (true) {
                if (_uiState.value.isEmailVerified) break // Stop if already verified

                try {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        user.reload().await()
                        if (user.isEmailVerified) {
                            _uiState.update { it.copy(isEmailVerified = true, isLoading = false) }
                            verificationCheckJob?.cancel() // Stop checking
                            countdownJob?.cancel() // Stop countdown if running
                            break
                        }
                    }
                } catch (exception: Exception) {
                    // Log error but continue checking
                    // Don't break the loop for transient errors
                }
                
                if (_uiState.value.isEmailVerified) break // Check again to exit loop immediately
                delay(VERIFICATION_CHECK_INTERVAL_MS)
            }
        }
    }

    fun onNavigationComplete() {
        _uiState.update { it.copy(isEmailVerified = false, verificationEmailSentMessage = null) } // Reset for next time
    }

    fun clearError() {
        _uiState.update { it.copy(errorResId = null, errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        verificationCheckJob?.cancel()
        countdownJob?.cancel()
    }
}
