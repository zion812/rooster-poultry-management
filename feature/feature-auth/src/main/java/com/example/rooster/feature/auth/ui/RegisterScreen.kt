package com.example.rooster.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.common.R // Assuming R class from core-common
import com.example.rooster.ui.theme.RoosterTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.text.style.TextAlign

// Assuming roleToDisplayString from LoginScreen is made accessible,
// e.g., by moving it to a common file within feature_auth/ui or a utils package.
// If not, this import or the call to it will fail.
// For now, we proceed assuming it can be resolved.
import com.example.rooster.feature.auth.ui.roleToDisplayString


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToCheckEmail: (email: String) -> Unit // New callback
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.FARMER) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Handle navigation after successful registration and email verification send attempt
    LaunchedEffect(key1 = uiState.registrationSuccess, key2 = uiState.registeredEmail) {
        if (uiState.registrationSuccess && uiState.registeredEmail != null) {
            onNavigateToCheckEmail(uiState.registeredEmail!!)
            viewModel.navigationToEmailVerificationComplete() // Reset the trigger
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.register_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!uiState.isLoading) onNavigateToLogin()
                    }) {
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
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp) // Slightly reduced spacing
        ) {
            Text(
                text = stringResource(id = R.string.create_account_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; viewModel.clearError() },
                label = { Text(stringResource(id = R.string.name_label)) },
                leadingIcon = { Icon(Icons.Filled.PersonOutline, contentDescription = null) },
                singleLine = true,
                isError = uiState.errorResId != null || uiState.errorMessage != null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; viewModel.clearError() },
                label = { Text(stringResource(id = R.string.email_label)) },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                singleLine = true,
                isError = uiState.errorResId != null || uiState.errorMessage != null,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it; viewModel.clearError() },
                label = { Text(stringResource(id = R.string.phone_label) + " (" + stringResource(id = R.string.optional_field) + ")") },
                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; viewModel.clearError() },
                label = { Text(stringResource(id = R.string.password_label)) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                singleLine = true,
                isError = uiState.errorResId != null || uiState.errorMessage != null,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; viewModel.clearError() },
                label = { Text(stringResource(id = R.string.confirm_password_label)) },
                leadingIcon = { Icon(Icons.Filled.LockOutline, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                singleLine = true,
                isError = uiState.errorResId != null || uiState.errorMessage != null,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(id = R.string.select_role_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )
            ExposedDropdownMenuBox(
                expanded = roleDropdownExpanded,
                onExpandedChange = { if (!uiState.isLoading) roleDropdownExpanded = !roleDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = roleToDisplayString(role = selectedRole), // Call the (assumed) accessible function
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Filled.PersonPin, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = roleDropdownExpanded,
                    onDismissRequest = { roleDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UserRole.values().forEach { role ->
                        DropdownMenuItem(
                            text = { Text(roleToDisplayString(role)) }, // Call the (assumed) accessible function
                            onClick = {
                                selectedRole = role
                                roleDropdownExpanded = false
                                viewModel.clearError()
                            }
                        )
                    }
                }
            }

            val errorText = when {
                uiState.errorResId != null -> stringResource(id = uiState.errorResId!!)
                uiState.errorMessage != null -> uiState.errorMessage
                else -> null
            }

            if (errorText != null) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.registerUser(
                            name = name.trim(),
                            email = email.trim(),
                            phone = phoneNumber.trim().takeIf { it.isNotBlank() },
                            pass = password,
                            confirmPass = confirmPassword,
                            role = selectedRole
                        )
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(stringResource(id = R.string.register_button_action), style = MaterialTheme.typography.labelLarge)
                }
            }

            TextButton(
                onClick = { if (!uiState.isLoading) onNavigateToLogin() },
                enabled = !uiState.isLoading
            ) {
                Text(stringResource(id = R.string.already_have_account_login))
            }
        }
    }
}

// Preview-related code might need adjustment if roleToDisplayString is not directly accessible
// or if a more complex preview ViewModel setup is desired.
// For now, retaining the existing preview structure.
// If roleToDisplayString was in LoginScreen.kt, it must be moved to a common place for this to compile.
// E.g., feature-auth/src/main/java/com/example/rooster/feature/auth/ui/AuthUiUtils.kt
/*
package com.example.rooster.feature.auth.ui // Or a more common util package

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.common.R

@Composable
fun roleToDisplayString(role: UserRole): String {
    return when (role) {
        UserRole.FARMER -> stringResource(id = R.string.role_farmer)
        UserRole.BUYER -> stringResource(id = R.string.role_buyer)
        UserRole.ADMIN -> stringResource(id = R.string.role_admin)
        UserRole.VETERINARIAN -> stringResource(id = R.string.role_veterinarian)
    }
}
*/

@Preview(showBackground = true, name = "Register Screen Light")
@Composable
fun RegisterScreenPreviewLight() {
    RoosterTheme(darkTheme = false) {
        WithPreviewResources {
            RegisterScreenForPreview(
                onNavigateToLogin = {},
                onNavigateToCheckEmail = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Register Screen Dark")
@Composable
fun RegisterScreenPreviewDark() {
    RoosterTheme(darkTheme = true) {
        WithPreviewResources {
             RegisterScreenForPreview(
                onNavigateToLogin = {},
                onNavigateToCheckEmail = {}
             )
        }
    }
}

@Preview(showBackground = true, name = "Register Screen With Errors")
@Composable
fun RegisterScreenPreviewWithErrors() {
    RoosterTheme(darkTheme = false) {
         WithPreviewResources {
            RegisterScreenForPreview(
                initialUiState = RegisterUiState(errorResId = R.string.error_registration_failed),
                onNavigateToLogin = {},
                onNavigateToCheckEmail = {}
            )
        }
    }
}


// --- Preview Helper ---
// This section is for Compose Preview. It uses local string resources for simplicity.
// Ensure `roleToDisplayString` is accessible for these previews.
// If `roleToDisplayString` is moved, update its call here.
@Composable
internal fun RegisterScreenForPreview(
    initialUiState: RegisterUiState = RegisterUiState(),
    onNavigateToLogin: () -> Unit,
    onNavigateToCheckEmail: (email: String) -> Unit
    // Not passing a real ViewModel to preview, just its state
) {
    var name by remember { mutableStateOf("Preview User") }
    var email by remember { mutableStateOf("preview@example.com") }
    var phoneNumber by remember { mutableStateOf("1234567890") }
    var password by remember { mutableStateOf("password123") }
    var confirmPassword by remember { mutableStateOf("password123") }
    var selectedRole by remember { mutableStateOf(UserRole.FARMER) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }
    val uiState by remember { mutableStateOf(initialUiState) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val str = AmbientStringResourceProvider.current // Using the preview string provider

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(str(PreviewR.string.register_screen_title)) }, // Using PreviewR
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = str(PreviewR.string.action_back))
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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(str(PreviewR.string.create_account_title), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(str(PreviewR.string.name_label)) }, leadingIcon = { Icon(Icons.Filled.PersonOutline, null)}, singleLine = true, isError = uiState.errorResId!=null, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(str(PreviewR.string.email_label)) }, leadingIcon = { Icon(Icons.Filled.Email, null)}, singleLine = true, isError = uiState.errorResId!=null, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text(str(PreviewR.string.phone_label) + " (" + str(PreviewR.string.optional_field) + ")") }, leadingIcon = { Icon(Icons.Filled.Phone, null)}, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text(str(PreviewR.string.password_label)) }, leadingIcon = { Icon(Icons.Filled.Lock, null)}, visualTransformation = PasswordVisualTransformation(), singleLine = true, isError = uiState.errorResId!=null, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text(str(PreviewR.string.confirm_password_label)) }, leadingIcon = { Icon(Icons.Filled.LockOutline, null)}, visualTransformation = PasswordVisualTransformation(), singleLine = true, isError = uiState.errorResId!=null, modifier = Modifier.fillMaxWidth())

            Text(str(PreviewR.string.select_role_label), style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
            ExposedDropdownMenuBox(expanded = roleDropdownExpanded, onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded}, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = str(when(selectedRole){ UserRole.FARMER -> PreviewR.string.role_farmer; UserRole.BUYER -> PreviewR.string.role_buyer; UserRole.ADMIN -> PreviewR.string.role_admin; UserRole.VETERINARIAN -> PreviewR.string.role_veterinarian; else -> "" }),
                    onValueChange = {}, readOnly = true, leadingIcon = { Icon(Icons.Filled.PersonPin, null)},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded)},
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = roleDropdownExpanded, onDismissRequest = { roleDropdownExpanded = false}) {
                    UserRole.values().forEach { role ->
                        DropdownMenuItem(
                            text = { Text(str(when(role){ UserRole.FARMER -> PreviewR.string.role_farmer; UserRole.BUYER -> PreviewR.string.role_buyer; UserRole.ADMIN -> PreviewR.string.role_admin; UserRole.VETERINARIAN -> PreviewR.string.role_veterinarian; else -> ""})) },
                            onClick = { selectedRole = role; roleDropdownExpanded = false }
                        )
                    }
                }
            }

            val errorText = uiState.errorResId?.let { str(it.toString()) } ?: uiState.errorMessage
            if (errorText != null) {
                Text(text = errorText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp).fillMaxWidth())
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(onClick = { /* Preview: Call VM function */ }, enabled = !uiState.isLoading, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                    Text(str(PreviewR.string.register_button_action), style = MaterialTheme.typography.labelLarge)
                }
            }
            TextButton(onClick = onNavigateToLogin, enabled = !uiState.isLoading) {
                Text(str(PreviewR.string.already_have_account_login))
            }
        }
    }
}

// Assuming WithPreviewResources and AmbientStringResourceProvider are defined (e.g. copied from LoginScreen.kt or common preview utils)
// Add relevant string keys to PreviewR.string for RegisterScreen
internal object PreviewR { // Copied from LoginScreen and extended
    object string {
        val login_title = "login_title" // from LoginScreen, reused if roleToDisplayString is shared
        val email_label = "email_label"
        val password_label = "password_label"
        val select_role_label = "select_role_label"
        val login_button_text = "login_button_text"
        val register_prompt_text = "register_prompt_text"
        val register_button_text = "register_button_text" // from LoginScreen, but might be different key for RegisterScreen's main button
        val role_farmer = "role_farmer"
        val role_buyer = "role_buyer"
        val role_admin = "role_admin"
        val role_veterinarian = "role_veterinarian"
        val error_email_password_empty = "error_email_password_empty"
        val error_login_failed = "error_login_failed"
        val error_unexpected = "error_unexpected"

        // RegisterScreen specific (ensure these match actual string keys you'll add)
        val register_screen_title = "register_screen_title"
        val action_back = "action_back"
        val create_account_title = "create_account_title"
        val name_label = "name_label"
        val phone_label = "phone_label"
        val optional_field = "optional_field"
        val confirm_password_label = "confirm_password_label"
        val register_button_action = "register_button_action" // Main button for register screen
        val already_have_account_login = "already_have_account_login"
        val error_field_required = "error_field_required" // Example error
        val error_invalid_email = "error_invalid_email" // Example error
        val error_password_too_short = "error_password_too_short" // Example error
        val error_registration_failed = "error_registration_failed" // From RegisterViewModel
    }
}

@Composable
internal fun previewStringResource(id: String): String { // Copied from LoginScreen and extended
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

        PreviewR.string.register_screen_title -> "Create Account (Preview)"
        PreviewR.string.action_back -> "Back (Preview)"
        PreviewR.string.create_account_title -> "Join Rooster (Preview)"
        PreviewR.string.name_label -> "Full Name (Preview)"
        PreviewR.string.phone_label -> "Phone Number (Preview)"
        PreviewR.string.optional_field -> "Optional (Preview)"
        PreviewR.string.confirm_password_label -> "Confirm Password (Preview)"
        PreviewR.string.register_button_action -> "Register (Preview)"
        PreviewR.string.already_have_account_login -> "Already have an account? Login (Preview)"
        PreviewR.string.error_field_required -> "This field is required. (Preview)"
        PreviewR.string.error_invalid_email -> "Invalid email format. (Preview)"
        PreviewR.string.error_password_too_short -> "Password is too short. (Preview)"
        PreviewR.string.error_registration_failed -> "Registration Failed. Try Again. (Preview)"
        // Map R.string.xxx (actual Int resource IDs) to string keys for preview error mapping
        // This is tricky; RegisterScreenForPreview's errorText logic needs care.
        // For simplicity, errorResId in preview state can use one of the above string keys.
        R.string.error_registration_failed.toString() -> "Registration Failed. Try Again. (Preview)"
        else -> "PREVIEW_STR: $id"
    }
}

// Assuming AmbientStringResourceProvider and WithPreviewResources are available (e.g. from LoginScreen.kt or common file)
internal val AmbientStringResourceProvider = compositionLocalOf<@Composable (id: String) -> String> {
    { id -> "Missing preview provider for $id" }
}

@Composable
internal fun WithPreviewResources(content: @Composable () -> Unit) {
    CompositionLocalProvider(AmbientStringResourceProvider provides { id -> previewStringResource(id) }) {
        content()
    }
}
```
