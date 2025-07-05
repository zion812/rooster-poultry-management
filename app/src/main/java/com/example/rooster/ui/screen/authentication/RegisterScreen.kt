package com.example.rooster.ui.screen.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.ui.viewmodel.AuthViewModel
import com.example.rooster.ui.viewmodel.RegistrationResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    onRegisterSuccess: (String) -> Unit, // Callback with email for CheckEmailScreen
    onNavigateToLogin: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val registrationResult = uiState.registrationResult

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var roleDropdownExpanded by remember { mutableStateOf(false) }
    val userRoles = UserRole.values() // Assuming UserRole is an enum

    LaunchedEffect(registrationResult) {
        when (registrationResult) {
            is RegistrationResult.Success -> {
                onRegisterSuccess(registrationResult.email)
                authViewModel.resetRegistrationResult() // Reset after handling
            }
            is RegistrationResult.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = registrationResult.message,
                        duration = SnackbarDuration.Short
                    )
                }
                authViewModel.resetRegistrationResult() // Reset after handling
            }
            RegistrationResult.Idle -> {}
            RegistrationResult.Loading -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    // IconButton(onClick = onNavigateToLogin) {
                    //     Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Login")
                    // }
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
            Text("Join Us!", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.registerNameInput,
                onValueChange = { authViewModel.onRegisterNameChanged(it) },
                label = { Text("Full Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.registerEmailInput,
                onValueChange = { authViewModel.onRegisterEmailChanged(it) },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.registerPasswordInput,
                onValueChange = { authViewModel.onRegisterPasswordChanged(it) },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // User Role Selection
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.registerRoleSelected.name.replace("_", " ").lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, // Format for display
                    onValueChange = { /* Read Only */ },
                    label = { Text("I am a...") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { roleDropdownExpanded = true }) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select Role")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = roleDropdownExpanded,
                    onDismissRequest = { roleDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.8f) // Adjust width as needed
                ) {
                    userRoles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.name.replace("_", " ").lowercase()
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) },
                            onClick = {
                                authViewModel.onRegisterRoleChanged(role)
                                roleDropdownExpanded = false
                                focusManager.clearFocus() // Or move to next if there's a confirm password
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    authViewModel.registerUser()
                },
                enabled = registrationResult != RegistrationResult.Loading &&
                          uiState.registerNameInput.isNotBlank() &&
                          uiState.registerEmailInput.isNotBlank() &&
                          uiState.registerPasswordInput.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (registrationResult == RegistrationResult.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Register")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Login")
            }
        }
    }
}
