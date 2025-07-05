package com.example.rooster.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.navigation.*
import com.example.rooster.feature.auth.ui.checkemail.CheckEmailScreen
import com.example.rooster.feature.auth.ui.checkemail.CheckEmailViewModel

// Import real feature screens instead of using placeholders
import com.example.rooster.feature.auth.ui.LoginScreen as RealLoginScreen
// Note: RegisterScreen may need to be imported when available

// Import feature navigation graphs with correct function names
import com.example.rooster.feature.auctions.navigation.auctionsFeatureGraph
import com.example.rooster.feature.marketplace.ui.navigation.MarketplaceNavGraph
import com.example.rooster.feature.farm.ui.navigation.FarmNavGraph

@Composable
fun RoosterNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AUTH_GRAPH_ROUTE,
    onNavigateToRoleGraphFromAuth: (UserRole) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Splash Screen
        composable(AppScreens.Splash.route) {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate(AUTH_GRAPH_ROUTE) {
                        popUpTo(AppScreens.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = { userRole: UserRole ->
                    val destination = when (userRole) {
                        UserRole.FARMER -> FARMER_USER_GRAPH_ROUTE
                        UserRole.BUYER -> GENERAL_USER_GRAPH_ROUTE
                        UserRole.ADMIN -> HIGH_LEVEL_USER_GRAPH_ROUTE
                        UserRole.VETERINARIAN -> VET_USER_GRAPH_ROUTE
                        else -> GENERAL_USER_GRAPH_ROUTE // Default fallback
                    }
                    navController.navigate(destination) {
                        popUpTo(AppScreens.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Authentication Graph - Using Real Screens
        navigation(
            startDestination = AppScreens.Login.route,
            route = AUTH_GRAPH_ROUTE
        ) {
            composable(AppScreens.Login.route) {
                RealLoginScreen(
                    onLoginSuccessAndVerified = { userRole: UserRole ->
                        onNavigateToRoleGraphFromAuth(userRole)
                    },
                    onNavigateToRegister = {
                        navController.navigate(AppScreens.Register.route)
                    },
                    onNavigateToCheckEmail = { email: String ->
                        navController.navigate(AppScreens.CheckEmail.createRoute(email))
                    }
                )
            }

            composable(AppScreens.Register.route) {
                RealRegisterScreen(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onNavigateToEmailVerification = { email: String ->
                        navController.navigate(AppScreens.CheckEmail.createRoute(email))
                    },
                    onRegistrationSuccess = { userRole: UserRole ->
                        onNavigateToRoleGraphFromAuth(userRole)
                    }
                )
            }

            composable(
                route = AppScreens.CheckEmail.route,
                arguments = listOf(
                    navArgument("email") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                CheckEmailScreen(
                    onNavigateToLogin = {
                        navController.navigate(AppScreens.Login.route) {
                            popUpTo(AUTH_GRAPH_ROUTE) { inclusive = false }
                        }
                    },
                    onEmailVerified = {
                        // Email verification complete - let the CheckEmailScreen handle role navigation
                    }
                )
            }

            composable(AppScreens.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onPasswordResetSent = { email: String ->
                        navController.navigate(AppScreens.CheckEmail.createRoute(email))
                    }
                )
            }
        }

        // FARM FEATURE INTEGRATION - Replace placeholder with real farm navigation graph
        navigation(
            startDestination = "farm_main",
            route = FARMER_USER_GRAPH_ROUTE
        ) {
            composable("farm_main") {
                // Create a farm main screen that integrates the FarmNavGraph
                FarmMainScreen(
                    onNavigateBack = {
                        navController.navigate(AUTH_GRAPH_ROUTE) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        // MARKETPLACE FEATURE INTEGRATION - Replace placeholder with real marketplace navigation
        navigation(
            startDestination = "marketplace_main",  
            route = GENERAL_USER_GRAPH_ROUTE
        ) {
            composable("marketplace_main") {
                // Create a marketplace main screen that integrates the MarketplaceNavGraph
                MarketplaceMainScreen(
                    onNavigateBack = {
                        navController.navigate(AUTH_GRAPH_ROUTE) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        // AUCTIONS FEATURE INTEGRATION - Replace placeholder with real auctions navigation
        auctionsFeatureGraph(
            navController = navController,
            isTeluguMode = false // Configure based on user preference
        )

        // Veterinarian User Graph - Keep placeholder for now if no feature module exists
        navigation(
            startDestination = "vet_home",
            route = VET_USER_GRAPH_ROUTE
        ) {
            composable("vet_home") {
                VetHomeScreen(
                    onNavigateToConsultations = {
                        navController.navigate("vet_consultations")
                    },
                    onNavigateToPatients = {
                        navController.navigate("vet_patients")
                    },
                    onNavigateToProfile = {
                        navController.navigate("vet_profile")
                    }
                )
            }

            composable("vet_consultations") {
                VetConsultationsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("vet_patients") {
                VetPatientsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("vet_profile") {
                VetProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        navController.navigate(AUTH_GRAPH_ROUTE) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

// Farm Main Screen - integrates the farm navigation graph
@Composable  
private fun FarmMainScreen(
    onNavigateBack: () -> Unit
) {
    // For now, create a simple integration of the farm navigation
    FarmNavGraph(
        startFarmId = "default_farm",
        onBack = onNavigateBack,
        onError = { error ->
            // Handle error - for now just log it
            println("Farm error: $error")
        }
    )
}

// Marketplace Main Screen - integrates the marketplace navigation graph
@Composable
private fun MarketplaceMainScreen(
    onNavigateBack: () -> Unit
) {
    // For now, create a simple integration of the marketplace navigation
    MarketplaceNavGraph(
        onNavigateToAuth = onNavigateBack,
        showSnackbar = { message ->
            // Handle snackbar - for now just log it
            println("Marketplace message: $message")  
        }
    )
}

// Keep only essential placeholder screens that are still needed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToMain: (UserRole) -> Unit
) {
    // Simple splash screen implementation
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🐓 Rooster",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Poultry Management System",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    // Auto-navigate after delay
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onNavigateToAuth()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToEmailVerification: (String) -> Unit,
    onRegistrationSuccess: (UserRole) -> Unit
) {
    // Placeholder register screen - to be replaced with real implementation
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                text = "Register Screen",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onRegistrationSuccess(UserRole.FARMER) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register as Farmer")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onPasswordResetSent: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forgot Password") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                text = "Forgot Password Screen",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onPasswordResetSent("test@example.com") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Reset Email")
            }
        }
    }
}

// Generic placeholder screen for vet features that may not have dedicated modules yet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenericPlaceholderScreen(
    title: String,
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "This feature is under development",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Vet feature placeholder screens - only keeping these since no dedicated vet feature module found
@Composable
private fun VetHomeScreen(
    onNavigateToConsultations: () -> Unit,
    onNavigateToPatients: () -> Unit,
    onNavigateToProfile: () -> Unit
) = GenericPlaceholderScreen("Veterinarian Home")

@Composable
private fun VetConsultationsScreen(onNavigateBack: () -> Unit) =
    GenericPlaceholderScreen("Consultations", onNavigateBack)

@Composable
private fun VetPatientsScreen(onNavigateBack: () -> Unit) =
    GenericPlaceholderScreen("Patients", onNavigateBack)

@Composable
private fun VetProfileScreen(onNavigateBack: () -> Unit, onLogout: () -> Unit) =
    GenericPlaceholderScreen("Vet Profile", onNavigateBack)