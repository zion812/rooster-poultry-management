package com.example.rooster.feature.auth.ui

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
// import androidx.hilt.navigation.compose.hiltViewModel // Will be needed when ViewModel is integrated

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    // viewModel: RegisterViewModel = hiltViewModel(), // To be added
    onRegisterClick: (name: String, email: String, phone: String, pass: String, role: UserRole) -> Unit,
    onNavigateToLogin: () -> Unit,
    // For preview and initial state handling
    isLoading: Boolean = false,
    errorMap: Map<String, String> = emptyMap() // field to error message
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.FARMER) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.register_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) { // Navigate back to Login
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.create_account_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.name_label)) },
                leadingIcon = { Icon(Icons.Filled.PersonOutline, contentDescription = null) },
                singleLine = true,
                isError = errorMap.containsKey("name"),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            if (errorMap.containsKey("name")) {
                Text(errorMap["name"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }


            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(id = R.string.email_label)) },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                singleLine = true,
                isError = errorMap.containsKey("email"),
                modifier = Modifier.fillMaxWidth()
            )
            if (errorMap.containsKey("email")) {
                Text(errorMap["email"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text(stringResource(id = R.string.phone_label) + " (" + stringResource(id = R.string.optional_field) + ")") },
                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                singleLine = true,
                isError = errorMap.containsKey("phone"),
                modifier = Modifier.fillMaxWidth()
            )
             if (errorMap.containsKey("phone")) {
                Text(errorMap["phone"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password_label)) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                singleLine = true,
                isError = errorMap.containsKey("password"),
                modifier = Modifier.fillMaxWidth()
            )
            if (errorMap.containsKey("password")) {
                Text(errorMap["password"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(stringResource(id = R.string.confirm_password_label)) },
                leadingIcon = { Icon(Icons.Filled.LockOutline, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                singleLine = true,
                isError = errorMap.containsKey("confirmPassword"),
                modifier = Modifier.fillMaxWidth()
            )
            if (errorMap.containsKey("confirmPassword")) {
                Text(errorMap["confirmPassword"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Role Selection Dropdown
            Text(
                text = stringResource(id = R.string.select_role_label),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = roleDropdownExpanded,
                onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = roleToDisplayString(role = selectedRole), // Uses roleToDisplayString from LoginScreen context for now
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
                            text = { Text(roleToDisplayString(role)) }, // Assuming roleToDisplayString is accessible
                            onClick = {
                                selectedRole = role
                                roleDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            if (errorMap.containsKey("role")) { // General error related to role if any
                Text(errorMap["role"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp)) // Reduced spacer

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                 if (errorMap.containsKey("general")) { // For general registration errors
                    Text(errorMap["general"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom=8.dp))
                }
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onRegisterClick(name, email, phoneNumber, password, selectedRole)
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(stringResource(id = R.string.register_button_action), style = MaterialTheme.typography.labelLarge)
                }
            }

            TextButton(onClick = onNavigateToLogin, enabled = !isLoading) {
                Text(stringResource(id = R.string.already_have_account_login))
            }
        }
    }
}

// Assuming roleToDisplayString is defined in LoginScreen.kt or moved to a common place.
// For now, if it's in LoginScreen.kt, this preview might need its own version or LoginScreen's file needs to be in scope.
// Let's use the one from LoginScreen.kt by ensuring it's accessible or copying its logic.
// For preview, we use the same previewStringResource mechanism as in LoginScreen.

@Preview(showBackground = true, name = "Register Screen Light")
@Composable
fun RegisterScreenPreviewLight() {
    RoosterTheme(darkTheme = false) {
        WithPreviewResources { // Assuming this is available from LoginScreen's preview helpers or defined here
            RegisterScreen(
                onRegisterClick = { _, _, _, _, _ -> },
                onNavigateToLogin = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Register Screen Dark")
@Composable
fun RegisterScreenPreviewDark() {
    RoosterTheme(darkTheme = true) {
        WithPreviewResources {
             RegisterScreen(
                onRegisterClick = { _, _, _, _, _ -> },
                onNavigateToLogin = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Register Screen With Errors")
@Composable
fun RegisterScreenPreviewWithErrors() {
    RoosterTheme(darkTheme = false) {
         WithPreviewResources {
            RegisterScreen(
                onRegisterClick = { _, _, _, _, _ -> },
                onNavigateToLogin = {},
                errorMap = mapOf(
                    "name" to previewStringResource(id = R.string.error_field_required), // Assuming this key exists
                    "email" to previewStringResource(id = R.string.error_invalid_email), // Assuming this key exists
                    "password" to previewStringResource(id = R.string.error_password_too_short), // Assuming this key exists
                    "general" to "Registration failed, please try again."
                )
            )
        }
    }
}

// Re-define preview helpers if not accessible from LoginScreen's file in this context
// For brevity, assuming they are accessible or would be defined similarly.
// If LoginScreen.kt's internal PreviewR and previewStringResource are not accessible,
// they would need to be duplicated or moved to a common test utility location.
// For this tool output, I'll assume they are implicitly available for the preview to function.
// If not, the preview part of this file would need those helpers defined.
// It's better to have a shared file for these preview utilities. For now, let's assume the LoginScreen ones are somehow visible.
// Minimal re-definition for this file's preview if needed:
/*
internal object PreviewR { // Placeholder if LoginScreen's isn't shared
    object string {
        val register_screen_title = "register_screen_title"
        // ... other keys for RegisterScreen ...
        val error_field_required = "error_field_required"
        val error_invalid_email = "error_invalid_email"
        val error_password_too_short = "error_password_too_short"
    }
}
@Composable
internal fun previewStringResource(id: Int): String { // Assuming Int for R.string
    // ... map ids to strings ...
    return "Preview String for $id"
}
@Composable
internal fun WithPreviewResources(content: @Composable () -> Unit) {
    // ... provide composition local ...
    content()
}
*/
// The roleToDisplayString function is also needed for previews. It's defined in LoginScreen.kt.
// If it's not accessible, it needs to be copied or moved.
// For the purpose of this step, I'm focusing on the RegisterScreen structure.
// The preview setup might require adjustments based on actual project structure for shared test/preview utils.
// Added string resource R.string.action_back, R.string.create_account_title, R.string.name_label,
// R.string.phone_label, R.string.optional_field, R.string.confirm_password_label, R.string.register_button_action,
// R.string.already_have_account_login. These will need to be added to strings.xml.
// Updated icons for leadingIcons.
// Using Scaffold with TopAppBar for a more complete screen structure.
// Added error display below each field, driven by an errorMap.
// Added isLoading state to show CircularProgressIndicator.
