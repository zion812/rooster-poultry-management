package com.example.rooster.feature.auth.ui.checkemail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rooster.core.common.R // Assuming R class from core-common
import com.example.rooster.ui.theme.RoosterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckEmailScreen(
    viewModel: CheckEmailViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit, // Or back to wherever appropriate
    onEmailVerified: () -> Unit // Navigate to Home/Dashboard after verification
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isEmailVerified) {
        if (uiState.isEmailVerified) {
            viewModel.onNavigationComplete() // Reset state in VM
            onEmailVerified()
        }
    }

    LaunchedEffect(uiState.verificationEmailSentMessage) {
        uiState.verificationEmailSentMessage?.let {
            // Consider using a SnackbarHost to show this message
            // For now, we'll assume it's handled if needed, or implicitly understood
            // by the screen's content updating.
            // Example: scaffoldState.snackbarHostState.showSnackbar(context.getString(it))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.check_email_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.action_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.check_your_email_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.email.isNotBlank()) {
                Text(
                    text = stringResource(id = R.string.verification_email_sent_to, uiState.email),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = stringResource(id = R.string.verification_email_sent_generic),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.check_spam_folder_instruction),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading && uiState.countdownSeconds == 0) { // Show loading only if not in countdown (initial send)
                CircularProgressIndicator()
            }

            uiState.errorResId?.let {
                Text(
                    stringResource(id = it),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            uiState.errorMessage?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.resendVerificationEmail() },
                enabled = uiState.countdownSeconds == 0 && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.countdownSeconds > 0) {
                    Text(stringResource(id = R.string.resend_email_countdown, uiState.countdownSeconds))
                } else if (uiState.isLoading && uiState.countdownSeconds != 0) { // Loading specifically for resend
                     CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                }
                else {
                    Text(stringResource(id = R.string.resend_verification_email_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(stringResource(id = R.string.action_back_to_login))
            }
        }
    }
}

@Preview(showBackground = true, name = "CheckEmailScreen Light")
@Composable
fun CheckEmailScreenPreviewLight() {
    RoosterTheme(darkTheme = false) {
        // This preview won't have a real ViewModel by default.
        // For a more functional preview, you'd mock the ViewModel or provide preview state.
        CheckEmailScreen(
            viewModel = providePreviewCheckEmailViewModel(
                CheckEmailUiState(email = "user@example.com", countdownSeconds = 0)
            ),
            onNavigateToLogin = {},
            onEmailVerified = {}
        )
    }
}

@Preview(showBackground = true, name = "CheckEmailScreen Dark Countdown")
@Composable
fun CheckEmailScreenPreviewDarkCountdown() {
    RoosterTheme(darkTheme = true) {
        CheckEmailScreen(
            viewModel = providePreviewCheckEmailViewModel(
                CheckEmailUiState(email = "user@example.com", countdownSeconds = 45)
            ),
            onNavigateToLogin = {},
            onEmailVerified = {}
        )
    }
}

@Preview(showBackground = true, name = "CheckEmailScreen Error")
@Composable
fun CheckEmailScreenPreviewError() {
    RoosterTheme(darkTheme = false) {
        CheckEmailScreen(
            viewModel = providePreviewCheckEmailViewModel(
                CheckEmailUiState(
                    email = "user@example.com",
                    errorResId = R.string.error_sending_verification_email
                )
            ),
            onNavigateToLogin = {},
            onEmailVerified = {}
        )
    }
}

// Helper for previewing with a fake ViewModel state
@Composable
private fun providePreviewCheckEmailViewModel(initialState: CheckEmailUiState): CheckEmailViewModel {
    // This is a simplified way to provide a ViewModel for preview.
    // In a real app, you might use a more sophisticated preview ViewModel setup.
    val mockAuthRepo = object : AuthRepository { // Basic mock, implement methods as needed for preview
        override suspend fun signIn(email: String, password: String) = TODO()
        override suspend fun signUp(email: String, password: String, name: String, role: com.example.rooster.core.auth.domain.model.UserRole, phoneNumber: String?) = TODO()
        override suspend fun signOut()= TODO()
        override suspend fun resetPassword(email: String)= TODO()
        override fun getCurrentUser()= TODO()
        override suspend fun updateProfile(user: com.example.rooster.core.auth.domain.model.User)= TODO()
        override suspend fun isUserSignedIn()= TODO()
        override suspend fun sendCurrentUserEmailVerification() = Result.success(Unit) // Make it succeed for preview if needed
        override suspend fun reloadCurrentUser() = TODO()
    }
    val savedStateHandle = SavedStateHandle(mapOf("email" to initialState.email))

    // Create a real ViewModel instance but it won't make real calls in preview
    // This specific ViewModel's init block triggers sendVerificationEmail.
    // For a non-interactive preview of a state, this might be too much.
    // Consider a fake ViewModel class for previews if more control is needed.
    // For now, this will show the screen in its initial state after `sendVerificationEmail` is called.

    // Let's return a simpler ViewModel for preview that just holds state
    return object : CheckEmailViewModel(mockAuthRepo, savedStateHandle) {
        private val _previewUiState = MutableStateFlow(initialState)
        override val uiState: StateFlow<CheckEmailUiState> = _previewUiState.asStateFlow()

        override fun sendVerificationEmail(isInitialSend: Boolean) {
            // No-op for this preview specific VM to avoid side effects from init
        }
         override fun resendVerificationEmail() {
            // No-op
        }
    }
}

// Needed String resources:
// R.string.check_email_screen_title = "Check Your Email"
// R.string.action_back = "Back" (likely exists)
// R.string.check_your_email_title = "Check Your Email"
// R.string.verification_email_sent_to = "A verification email has been sent to %1$s."
// R.string.verification_email_sent_generic = "A verification email has been sent."
// R.string.check_spam_folder_instruction = "Please also check your spam or junk folder."
// R.string.resend_email_countdown = "Resend in %1$d s"
// R.string.resend_verification_email_button = "Resend Verification Email"
// R.string.action_back_to_login = "Back to Login"
// R.string.error_sending_verification_email (from ViewModel)
// R.string.error_email_missing_for_verification (from ViewModel)
// R.string.verification_email_sent (from ViewModel)
// R.string.verification_email_resent (from ViewModel)
// R.string.error_checking_verification_status (from ViewModel, if shown)
```
