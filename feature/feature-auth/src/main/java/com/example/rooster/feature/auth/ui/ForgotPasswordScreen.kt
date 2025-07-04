package com.example.rooster.feature.auth.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1
 main
 main
 main
 main
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rooster.core.common.R
import com.example.rooster.ui.theme.RoosterTheme

 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1

import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.rooster.core.common.R
 main

 main
 main
 main
 main
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1
 main
 main
 main
 main
    // viewModel: ForgotPasswordViewModel = hiltViewModel(), // To be added later
    onNavigateBack: () -> Unit,
    // For UI state, to be driven by ViewModel
    isLoading: Boolean = false,
    emailSentSuccess: Boolean = false,
    errorMesssage: String? = null, // Direct error message from VM
    onSendResetLinkClick: (String) -> Unit = {} // Lambda for button click
) {
    var email by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.forgot_password_title)) },
 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1


    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.forgot_password_title)) }, // Add R.string.forgot_password_title
 main
 main
 main
 main
 main
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.action_back))
                    }
                }
            )
        }
    ) { paddingValues ->
 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1
 main
 main
 main
 main
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.forgot_password_instructions), // New string: "Enter your email to receive a password reset link."
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(id = R.string.email_label)) },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                enabled = !isLoading && !emailSentSuccess, // Disable if loading or success
                modifier = Modifier.fillMaxWidth()
            )

            if (isLoading) {
                CircularProgressIndicator()
            } else if (emailSentSuccess) {
                Text(
                    text = stringResource(id = R.string.forgot_password_success_message), // New string: "Password reset link sent! Please check your email."
                    color = MaterialTheme.colorScheme.primary, // Or a success color
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                errorMesssage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onSendResetLinkClick(email)
                    },
                    enabled = email.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(stringResource(id = R.string.forgot_password_button_send_link)) // New string: "Send Reset Link"
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Push content up

            TextButton(onClick = onNavigateBack, enabled = !isLoading) {
                Text(stringResource(id = R.string.action_back_to_login)) // New string: "Back to Login"
            }
        }
    }
}

@Preview(showBackground = true, name = "Forgot Password Screen")
@Composable
fun ForgotPasswordScreenPreview() {
    RoosterTheme {
        ForgotPasswordScreen(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "Forgot Password Screen - Success")
@Composable
fun ForgotPasswordScreenSuccessPreview() {
    RoosterTheme {
        ForgotPasswordScreen(onNavigateBack = {}, emailSentSuccess = true)
    }
}

@Preview(showBackground = true, name = "Forgot Password Screen - Error")
@Composable
fun ForgotPasswordScreenErrorPreview() {
    RoosterTheme {
        ForgotPasswordScreen(onNavigateBack = {}, errorMesssage = "Invalid email address or user not found.")
    }
}

@Preview(showBackground = true, name = "Forgot Password Screen - Loading")
@Composable
fun ForgotPasswordScreenLoadingPreview() {
    RoosterTheme {
        ForgotPasswordScreen(onNavigateBack = {}, isLoading = true)
    }
}
// New string resources needed:
// R.string.forgot_password_instructions
// R.string.forgot_password_success_message
// R.string.forgot_password_button_send_link
// R.string.action_back_to_login
// R.string.forgot_password_title (already planned)
 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1

 feat/login-screen-v1


        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Forgot Password Screen Placeholder")
            // TODO: Add email input field and "Send Reset Link" button
        }
    }
}
 main
 main
 main
 main
 main
