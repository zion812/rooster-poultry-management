package com.example.rooster.ui.screen.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rooster.ui.viewmodel.AuthViewModel
import com.example.rooster.ui.viewmodel.EmailVerificationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckEmailScreen(
    email: String,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    onEmailVerified: () -> Unit, // Callback when email is confirmed verified
    onGoToLogin: () -> Unit // Callback to navigate back to login screen
) {
    val uiState by authViewModel.uiState.collectAsState()
    val verificationResult = uiState.emailVerificationResult
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Set the email in ViewModel if it's not already set or different
    LaunchedEffect(email) {
        if (uiState.emailForVerification != email) {
            // This might be redundant if navigation always sets it via ViewModel action before navigating
            // authViewModel.setEmailForVerification(email) // Add this method to ViewModel if needed
        }
    }

    // Periodically check email verification status
    LaunchedEffect(Unit) { // Runs once when the screen enters composition
        while (true) {
            authViewModel.checkEmailVerificationStatus()
            delay(10000) // Check every 10 seconds
            if (uiState.emailVerificationResult is EmailVerificationResult.Success) break // Stop polling if verified
        }
    }


    LaunchedEffect(verificationResult) {
        when (verificationResult) {
            is EmailVerificationResult.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Email successfully verified!",
                        duration = SnackbarDuration.Short
                    )
                }
                // Delay slightly to let user see message, then navigate
                delay(1500)
                onEmailVerified()
                authViewModel.resetEmailVerificationResult()
            }
            is EmailVerificationResult.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = verificationResult.message,
                        duration = SnackbarDuration.Long // Show longer for errors
                    )
                }
                authViewModel.resetEmailVerificationResult()
            }
            else -> { /* Idle or Loading, do nothing here */ }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Verify Your Email") },
                navigationIcon = {
                    IconButton(onClick = onGoToLogin) { // Navigate back to login
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Login")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "We've sent a verification link to:",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = email, // Display the email passed as argument
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Please check your inbox (and spam folder) and click the link to complete your registration.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (verificationResult == EmailVerificationResult.Loading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Checking status...", style = MaterialTheme.typography.bodySmall)
            } else {
                 Button(
                    onClick = { authViewModel.checkEmailVerificationStatus() },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("I've Verified My Email (Refresh Status)")
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { authViewModel.resendVerificationEmail() },
                    modifier = Modifier.fillMaxWidth(0.8f),
                     enabled = verificationResult != EmailVerificationResult.Loading // Disable while another action is loading
                ) {
                    Text("Resend Verification Email")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            TextButton(onClick = onGoToLogin) {
                Text("Back to Login")
            }
        }
    }
}
