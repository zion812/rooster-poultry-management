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
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.FARMER) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    // val context = LocalContext.current // Not used for now, but kept for potential future use (e.g. Toasts)

 feat/login-screen-v1

 feat/login-screen-v1
 main
    // Handle navigation to role-specific graph
    LaunchedEffect(key1 = uiState.loggedInUserRole) {
        uiState.loggedInUserRole?.let { authenticatedRole ->
            onNavigateToHome(authenticatedRole) // Pass the role to the navigation callback
            viewModel.navigationToRoleGraphComplete() // Reset the trigger in ViewModel
 feat/login-screen-v1


    LaunchedEffect(key1 = uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            onNavigateToHome()
            viewModel.navigationToHomeComplete()
 main
 main
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

@Composable
fun roleToDisplayString(role: UserRole): String {
    return when (role) {
        UserRole.FARMER -> stringResource(id = R.string.role_farmer)
        UserRole.BUYER -> stringResource(id = R.string.role_buyer)
        UserRole.ADMIN -> stringResource(id = R.string.role_admin)
        UserRole.VETERINARIAN -> stringResource(id = R.string.role_veterinarian)
    }
}

// --- Preview Helper ---
internal object PreviewR {
    object string {
        // Copied keys from the actual strings.xml for preview consistency
        const val login_title = "login_title"
        const val email_label = "email_label"
        const val password_label = "password_label"
        const val select_role_label = "select_role_label"
        const val login_button_text = "login_button_text"
        const val register_prompt_text = "register_prompt_text"
        const val register_button_text = "register_button_text"
        const val role_farmer = "role_farmer"
        const val role_buyer = "role_buyer"
        const val role_admin = "role_admin"
        const val role_veterinarian = "role_veterinarian"
        const val error_email_password_empty = "error_email_password_empty"
        const val error_login_failed = "error_login_failed"
        const val error_unexpected = "error_unexpected"
        // Add any other keys used by LoginScreen directly or indirectly via ViewModel state for previews
    }
}

@Composable
internal fun previewStringResource(id: String): String {
    // Provides English strings for preview based on the keys
    return when (id) {
        PreviewR.string.login_title -> "Login to Rooster (Preview)"
        PreviewR.string.email_label -> "Email Address (Preview)"
        PreviewR.string.password_label -> "Password (Preview)"
        PreviewR.string.select_role_label -> "Select Your Role (Preview)"
        PreviewR.string.login_button_text -> "Login (Preview)"
        PreviewR.string.register_prompt_text -> "Don't have an account? (Preview)"
        PreviewR.string.register_button_text -> "Register Here (Preview)"
        PreviewR.string.role_farmer -> "Farmer (Preview)"
        PreviewR.string.role_buyer -> "Buyer (Preview)"
        PreviewR.string.role_admin -> "Admin (Preview)"
        PreviewR.string.role_veterinarian -> "Veterinarian (Preview)"
        PreviewR.string.error_email_password_empty -> "Email and password cannot be empty. (Preview)"
        PreviewR.string.error_login_failed -> "Login failed. Please try again. (Preview)"
        PreviewR.string.error_unexpected -> "An unexpected error occurred. (Preview)"
        else -> "PREVIEW_STR: $id"
    }
}

internal val AmbientStringResourceProvider = compositionLocalOf<@Composable (id: String) -> String> {
    { id -> "Missing preview provider for $id" }
}

@Composable
internal fun WithPreviewResources(content: @Composable () -> Unit) {
    CompositionLocalProvider(AmbientStringResourceProvider provides { id -> previewStringResource(id) }) {
        content()
    }
}

@Composable
fun LoginScreenForPreview(
    initialUiState: LoginUiState = LoginUiState(),
    onLoginClick: (String, String, UserRole) -> Unit = { _, _, _ -> },
    onNavigateToRegisterClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("preview@example.com") }
    var password by remember { mutableStateOf("password") }
    var selectedRole by remember { mutableStateOf(UserRole.FARMER) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }
    val uiState by remember { mutableStateOf(initialUiState) }
    val str = AmbientStringResourceProvider.current

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
                text = str(PreviewR.string.login_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            OutlinedTextField(value = email, onValueChange = {email = it}, label = { Text(str(PreviewR.string.email_label)) }, leadingIcon = { Icon(Icons.Filled.Email, null)}, modifier = Modifier.fillMaxWidth(), isError = uiState.errorResId != null || uiState.errorMessage != null)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = password, onValueChange = {password = it}, label = { Text(str(PreviewR.string.password_label)) }, leadingIcon = { Icon(Icons.Filled.Lock, null)}, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), isError = uiState.errorResId != null || uiState.errorMessage != null)
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = str(PreviewR.string.select_role_label), style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            ExposedDropdownMenuBox(expanded = roleDropdownExpanded, onExpandedChange = {roleDropdownExpanded = !roleDropdownExpanded}, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = str(when(selectedRole){ UserRole.FARMER -> PreviewR.string.role_farmer; UserRole.BUYER -> PreviewR.string.role_buyer; UserRole.ADMIN -> PreviewR.string.role_admin; UserRole.VETERINARIAN -> PreviewR.string.role_veterinarian; else -> "" }), onValueChange = {}, readOnly = true, leadingIcon = { Icon(Icons.Filled.Person, null)}, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded)}, modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = roleDropdownExpanded, onDismissRequest = { roleDropdownExpanded = false}) {
                    UserRole.values().forEach { role ->
                        DropdownMenuItem(text = { Text(str(when(role){ UserRole.FARMER -> PreviewR.string.role_farmer; UserRole.BUYER -> PreviewR.string.role_buyer; UserRole.ADMIN -> PreviewR.string.role_admin; UserRole.VETERINARIAN -> PreviewR.string.role_veterinarian; else -> ""})) }, onClick = { selectedRole = role; roleDropdownExpanded = false})
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val errorText = when {
                uiState.errorResId != null -> str(uiState.errorResId.toString()) // This needs mapping for preview
                uiState.errorMessage != null -> uiState.errorMessage
                else -> null
            }
            errorText?.let { Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(), textAlign = TextAlign.Center) }

            Spacer(modifier = Modifier.height(if (errorText == null) 16.dp else 0.dp))
            Button(onClick = { onLoginClick(email, password, selectedRole) }, enabled = !uiState.isLoading, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text(str(PreviewR.string.login_button_text), style = MaterialTheme.typography.labelLarge) }
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(str(PreviewR.string.register_prompt_text))
                TextButton(onClick = onNavigateToRegisterClick, enabled = !uiState.isLoading) { Text(str(PreviewR.string.register_button_text)) }
            }
        }
        if (uiState.isLoading) { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) }
    }
}


@Preview(showBackground = true, name = "Login Screen Light")
@Composable
fun LoginScreenPreviewLight() {
    RoosterTheme(darkTheme = false) {
        Surface {
            WithPreviewResources {
                LoginScreenForPreview()
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Light - Loading")
@Composable
fun LoginScreenPreviewLoading() {
    RoosterTheme(darkTheme = false) {
        Surface {
            WithPreviewResources {
                LoginScreenForPreview(initialUiState = LoginUiState(isLoading = true))
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Light - Error (ResId)")
@Composable
fun LoginScreenPreviewErrorResId() {
    RoosterTheme(darkTheme = false) {
        Surface {
            WithPreviewResources {
                // For preview, we pass a string key that previewStringResource can understand
                // The actual LoginUiState would hold an Int (R.string.xxx)
                // This mapping is a bit tricky for previews if we want to show specific error messages
                // that come from resource IDs. The PreviewR object helps bridge this.
                LoginScreenForPreview(initialUiState = LoginUiState(errorResId = 0, errorMessage = previewStringResource(
                    PreviewR.string.error_login_failed)))
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Light - Error (Message)")
@Composable
fun LoginScreenPreviewErrorMessage() {
    RoosterTheme(darkTheme = false) {
        Surface {
            WithPreviewResources {
                LoginScreenForPreview(initialUiState = LoginUiState(errorMessage = "A custom error message from ViewModel (Preview)."))
            }
        }
    }
}


@Preview(showBackground = true, name = "Login Screen Dark")
@Composable
fun LoginScreenPreviewDark() {
    RoosterTheme(darkTheme = true) {
        Surface {
            WithPreviewResources {
                LoginScreenForPreview()
            }
        }
    }
}

// Notes:
// - R class import updated to `com.example.rooster.core.common.R`. This relies on the
//   `feature-auth` module having a dependency on `core-common`.
// - LoginScreen now displays error messages sourced from `uiState.errorResId` (by resolving the string resource)
//   or `uiState.errorMessage` (direct string).
// - Preview helpers (`PreviewR`, `previewStringResource`, `LoginScreenForPreview`) updated to align
//   with the new string keys and the ViewModel's way of providing errors (ResId or direct message).
//   The mapping for errorResId in previews is simplified to use a known preview string.
// - TextField `isError` state is driven by whether any error (ResId or message) is present.
// - LocalContext import was present but not used; commented out to keep imports clean for now.
// - Removed the unused MaterialR import.
// - Ensured all stringResource calls in the main LoginScreen and roleToDisplayString use the new R class.
