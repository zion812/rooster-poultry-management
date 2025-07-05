package com.example.rooster.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.ui.theme.RoosterTheme
// Correctly import R class from the app module or a common resources module
// Updated R class import to point to core.common where strings.xml were created.
// This assumes that the build system makes com.example.rooster.core.common.R available
// to the feature-auth module. This typically requires feature-auth to have a dependency
// on core-common in its build.gradle file (e.g., implementation(project(":core:core-common"))).
import com.example.rooster.core.common.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccessAndVerified: (userRole: UserRole) -> Unit, // Renamed for clarity
    onNavigateToRegister: () -> Unit,
    onNavigateToCheckEmail: (email: String) -> Unit // New callback
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.FARMER) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation to role-specific graph
    LaunchedEffect(key1 = uiState.loggedInUserRole) {
        uiState.loggedInUserRole?.let { authenticatedRole ->
            onLoginSuccessAndVerified(authenticatedRole)
            viewModel.navigationToRoleGraphComplete()
        }
    }

    LaunchedEffect(key1 = uiState.navigateToRegister) {
        if (uiState.navigateToRegister) {
            onNavigateToRegister()
            viewModel.navigationToRegisterComplete()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.login_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(id = R.string.email_label)) },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = uiState.errorResId != null || uiState.errorMessage != null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password_label)) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = uiState.errorResId != null || uiState.errorMessage != null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.select_role_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = roleDropdownExpanded,
                onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = roleToDisplayString(role = selectedRole),
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = roleDropdownExpanded,
                    onDismissRequest = { roleDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UserRole.values().forEach { role ->
                        DropdownMenuItem(
                            text = { Text(roleToDisplayString(role)) },
                            onClick = {
                                selectedRole = role
                                roleDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val errorText = when {
                uiState.errorResId != null -> stringResource(id = uiState.errorResId!!)
                uiState.errorMessage != null -> uiState.errorMessage
                else -> null
            }

            errorText?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(if (errorText == null) 16.dp else 0.dp))

            Button(
                onClick = {
                    viewModel.clearError()
                    viewModel.login(email, password, selectedRole)
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(stringResource(id = R.string.login_button_text), style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(id = R.string.register_prompt_text))
                TextButton(
                    onClick = { viewModel.onNavigateToRegister() },
                    enabled = !uiState.isLoading
                ) {
                    Text(stringResource(id = R.string.register_button_text))
                }
            }
        }
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

// Helper function to convert UserRole to display string
@Composable
private fun roleToDisplayString(role: UserRole): String {
    return when (role) {
        UserRole.FARMER -> stringResource(id = R.string.role_farmer)
        UserRole.BUYER -> stringResource(id = R.string.role_buyer)
        UserRole.ADMIN -> stringResource(id = R.string.role_admin)
        UserRole.VETERINARIAN -> stringResource(id = R.string.role_veterinarian)
        else -> role.name
    }
}

// Preview components remain the same as they were working
@Preview(showBackground = true, name = "Login Screen Light")
@Composable
fun LoginScreenPreviewLight() {
    RoosterTheme(darkTheme = false) {
        Surface {
            LoginScreen(
                onLoginSuccessAndVerified = {},
                onNavigateToRegister = {},
                onNavigateToCheckEmail = {}
            )
        }
    }
}