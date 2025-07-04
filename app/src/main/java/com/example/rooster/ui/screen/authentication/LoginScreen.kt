package com.example.rooster.ui.screen.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.rooster.ui.viewmodel.LoginResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: (UserRole) -> Unit, // Callback with UserRole for navigation to role graph
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToCheckEmail: (String) -> Unit // Callback to navigate to CheckEmailScreen
) {
    val uiState by authViewModel.uiState.collectAsState()
    val loginResult = uiState.loginResult

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(loginResult) {
        when (loginResult) {
            is LoginResult.Success -> {
                if (loginResult.isEmailVerified) {
                    onLoginSuccess(loginResult.userRole)
                } else {
                    // Email not verified, navigate to CheckEmailScreen
                    onNavigateToCheckEmail(loginResult.email)
                }
                authViewModel.resetLoginResult() // Reset after handling
            }
            is LoginResult.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = loginResult.message,
                        duration = SnackbarDuration.Short
                    )
                }
                authViewModel.resetLoginResult() // Reset after handling
            }
            LoginResult.Idle -> {}
            LoginResult.Loading -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Login") })
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
            Text("Welcome Back!", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.loginEmailInput,
                onValueChange = { authViewModel.onLoginEmailChanged(it) },
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
                value = uiState.loginPasswordInput,
                onValueChange = { authViewModel.onLoginPasswordChanged(it) },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (uiState.loginEmailInput.isNotBlank() && uiState.loginPasswordInput.isNotBlank()) {
                        authViewModel.loginUser()
                    }
                }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onNavigateToForgotPassword, modifier = Modifier.align(Alignment.End)) {
                Text("Forgot Password?")
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    authViewModel.loginUser()
                },
                enabled = loginResult != LoginResult.Loading && uiState.loginEmailInput.isNotBlank() && uiState.loginPasswordInput.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loginResult == LoginResult.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Register")
            }
        }
    }
}
