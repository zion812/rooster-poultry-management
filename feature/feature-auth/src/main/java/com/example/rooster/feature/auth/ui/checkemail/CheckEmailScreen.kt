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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rooster.ui.theme.RoosterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckEmailScreen(
    viewModel: CheckEmailViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onEmailVerified: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isEmailVerified) {
        if (uiState.isEmailVerified) {
            viewModel.onNavigationComplete()
            onEmailVerified()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Check Your Email") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
                text = "Check Your Email",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.email.isNotBlank()) {
                Text(
                    text = "A verification email has been sent to ${uiState.email}",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "A verification email has been sent.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Please also check your spam or junk folder.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading && uiState.countdownSeconds == 0) {
                CircularProgressIndicator()
            }

            uiState.errorResId?.let {
                Text(
                    "Error occurred during verification",
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
                when {
                    uiState.countdownSeconds > 0 -> {
                        Text("Resend in ${uiState.countdownSeconds}s")
                    }

                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    else -> {
                        Text("Resend Verification Email")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("Back to Login")
            }
        }
    }
}

@Preview(showBackground = true, name = "CheckEmailScreen Light")
@Composable
fun CheckEmailScreenPreviewLight() {
    RoosterTheme(darkTheme = false) {
        CheckEmailScreen(
            onNavigateToLogin = {},
            onEmailVerified = {}
        )
    }
}
