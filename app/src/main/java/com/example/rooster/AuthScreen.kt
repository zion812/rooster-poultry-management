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
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Select Your Role:", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "farmer",
                        onClick = { role = "farmer" },
                    )
                    Text("🌾 Farmer - Manage fowl and participate in markets")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "general",
                        onClick = { role = "general" },
                    )
                    Text("🛒 Consumer - Buy fowl and explore markets")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "highLevel",
                        onClick = { role = "highLevel" },
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
            enabled = username.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (isLogin) "Sign In" else "Create Account")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Forgot Password Button (only show during login)
        if (isLogin) {
            TextButton(
                onClick = {
                    if (!uiState.loading && !uiState.resetLoading) {
                        authViewModel.togglePasswordReset(true)
                    }
                },
            ) {
                Text("Forgot Password?")
            }
        }
    }
}
