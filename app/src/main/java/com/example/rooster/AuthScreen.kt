package com.example.rooster

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rooster.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun AuthScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("farmer") }
    var isLogin by remember { mutableStateOf(true) }
    var resetEmail by remember { mutableStateOf("") }

    // Collect UI state from ViewModel
    val uiState by authViewModel.uiState.collectAsState()

    // Check authentication state on screen load
    LaunchedEffect(Unit) {
        authViewModel.checkAuthState()
    }

    // Handle successful authentication
    LaunchedEffect(uiState.isAuthenticated, uiState.userRole) {
        if (uiState.isAuthenticated && uiState.userRole.isNotEmpty()) {
            val destination =
                when (uiState.userRole.lowercase()) {
                    "farmer" -> NavigationRoute.FARMER_HOME.route
                    "highlevel", "high_level" -> NavigationRoute.HIGH_LEVEL_HOME.route
                    "general" -> NavigationRoute.MARKETPLACE.route
                    else -> NavigationRoute.MARKETPLACE.route
                }

            Log.d("AuthScreen", "Navigating to $destination for role: ${uiState.userRole}")

            // Safe navigation with proper error handling
            delay(100)
            withContext(Dispatchers.Main) {
                try {
                    navController.navigate(destination) {
                        popUpTo(NavigationRoute.AUTH.route) {
                            inclusive = true
                        }
                    }
                } catch (navEx: Exception) {
                    Log.e("AuthScreen", "Navigation failed", navEx)
                    // Try simple navigation without popUpTo
                    navController.navigate(destination)
                }
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "🐓 Rooster App",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isLogin) "Welcome Back!" else "Join Our Community",
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it.trim()
                authViewModel.clearMessages()
            },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.loading && !uiState.resetLoading,
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                authViewModel.clearMessages()
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrectEnabled = false,
                ),
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.loading && !uiState.resetLoading,
        )

        if (!isLogin) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    authViewModel.clearMessages()
                },
                label = { Text("Email Address") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.loading && !uiState.resetLoading,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Select Your Role:", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "farmer",
                        onClick = { role = "farmer" },
                        enabled = !uiState.loading && !uiState.resetLoading,
                    )
                    Text("🌾 Farmer - Manage fowl and participate in markets")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "general",
                        onClick = { role = "general" },
                        enabled = !uiState.loading && !uiState.resetLoading,
                    )
                    Text("🛒 Consumer - Buy fowl and explore markets")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "highLevel",
                        onClick = { role = "highLevel" },
                        enabled = !uiState.loading && !uiState.resetLoading,
                    )
                    Text("📊 Manager - Oversee operations and analytics")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.errorMessage.isNotEmpty()) {
            Text(
                text = "⚠️ ${uiState.errorMessage}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.successMessage.isNotEmpty()) {
            Text(
                text = "✅ ${uiState.successMessage}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                // Validate input using ViewModel
                val validationError =
                    authViewModel.validateInput(
                        username = username,
                        password = password,
                        email = email,
                        isLogin = isLogin,
                    )

                if (validationError != null) {
                    // Show validation error (this would be handled by the ViewModel in a real implementation)
                    Log.e("AuthScreen", "Validation error: $validationError")
                    return@Button
                }

                // Call ViewModel methods instead of direct Parse calls
                if (isLogin) {
                    authViewModel.login(username, password)
                } else {
                    authViewModel.register(username, email, password, role)
                }
            },
            enabled = !uiState.loading && !uiState.resetLoading && username.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(end = 8.dp),
                    strokeWidth = 2.dp,
                )
            }
            Text(if (isLogin) "Sign In" else "Create Account")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Test Parse connection button
        if (!isLogin && !uiState.showPasswordReset) {
            Button(
                onClick = {
                    authViewModel.testParseConnection()
                },
                enabled = !uiState.loading && !uiState.resetLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp,
                    )
                }
                Text("Test Parse & User Creation")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Diagnostic information
        if (!isLogin && !uiState.showPasswordReset) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Diagnostic Information:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Text(
                    text = "Parse App ID: ${BuildConfig.PARSE_APP_ID.take(8)}...${
                        BuildConfig.PARSE_APP_ID.takeLast(
                            8,
                        )
                    }",
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    text = "Parse Server URL: ${BuildConfig.PARSE_SERVER_URL}",
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    text = "Parse SDK Version: 1.26.0",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Forgot Password Dialog
        if (uiState.showPasswordReset) {
            Column {
                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = resetEmail,
                    onValueChange = {
                        resetEmail = it.trim()
                        authViewModel.clearMessages()
                    },
                    label = { Text("Email Address") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.resetLoading,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(
                        onClick = {
                            if (resetEmail.isBlank()) {
                                Log.e("AuthScreen", "Please enter your email address")
                                return@Button
                            }
                            authViewModel.resetPassword(resetEmail)
                        },
                        enabled = !uiState.resetLoading && resetEmail.isNotBlank(),
                        modifier = Modifier.weight(1f),
                    ) {
                        if (uiState.resetLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp),
                                strokeWidth = 2.dp,
                            )
                        }
                        Text("Send Reset Email")
                    }

                    TextButton(
                        onClick = {
                            authViewModel.togglePasswordReset(false)
                            resetEmail = ""
                        },
                        enabled = !uiState.resetLoading,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Cancel")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        TextButton(
            onClick = {
                if (!uiState.loading && !uiState.resetLoading) {
                    if (uiState.showPasswordReset) {
                        authViewModel.togglePasswordReset(false)
                        resetEmail = ""
                    } else {
                        isLogin = !isLogin
                        authViewModel.clearMessages()
                    }
                }
            },
            enabled = !uiState.loading && !uiState.resetLoading,
        ) {
            Text(
                if (uiState.showPasswordReset) {
                    "Back to Login"
                } else if (isLogin) {
                    "Don't have an account? Sign Up"
                } else {
                    "Already have an account? Sign In"
                },
            )
        }

        // Forgot Password Button (only show during login)
        if (isLogin && !uiState.showPasswordReset) {
            TextButton(
                onClick = {
                    if (!uiState.loading && !uiState.resetLoading) {
                        authViewModel.togglePasswordReset(true)
                    }
                },
                enabled = !uiState.loading && !uiState.resetLoading,
            ) {
                Text("Forgot Password?")
            }
        }

        if (uiState.loading || uiState.resetLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "⏳ ${if (isLogin) "Signing you in" else "Creating your account"}... This may take up to 60 seconds on slow connections.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
