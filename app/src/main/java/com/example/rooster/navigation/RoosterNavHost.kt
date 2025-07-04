package com.example.rooster.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.navigation.*
import com.example.rooster.ui.screen.authentication.CheckEmailScreen
import com.example.rooster.ui.screen.authentication.LoginScreen
import com.example.rooster.ui.screen.authentication.RegisterScreen
// Import other screens as they are created, e.g.:
// import com.example.rooster.ui.screen.farmer.FarmerDashboardScreen
// import com.example.rooster.ui.screen.buyer.BuyerDashboardScreen
// import com.example.rooster.ui.screen.admin.AdminDashboardScreen
// import com.example.rooster.ui.screen.vet.VetDashboardScreen
// import com.example.rooster.ui.screen.SplashScreen // If you have a composable splash

@Composable
fun RoosterNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String, // This will be AUTH_GRAPH_ROUTE or a role-specific graph
    onNavigateToRoleGraphFromAuth: (UserRole) -> Unit, // Callback after successful auth
    // onNavigateToCheckEmail: (String) -> Unit // Callback to navigate to CheckEmailScreen
) {
    NavHost(
        navController = navController,
        startDestination = startDestination, // e.g., AUTH_GRAPH_ROUTE or FARMER_GRAPH_ROUTE
        modifier = modifier,
        route = ROOT_GRAPH_ROUTE // Optional: Define a root graph route name
    ) {
        // Splash Screen (if you have one that's part of navigation)
        // composable(Screen.Splash.route) {
        //     SplashScreen(onTimeout = {
        //         // Logic to navigate to Auth or Main graph based on auth state
        //         // This is often handled by MainActivity's logic that determines `startDestination`
        //     })
        // }

        // Authentication Graph
        navigation(
            startDestination = Screen.Login.route,
            route = AUTH_GRAPH_ROUTE
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = { userRole ->
                        // Navigate to the appropriate role-based graph
                        onNavigateToRoleGraphFromAuth(userRole)
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onNavigateToForgotPassword = {
                        // navController.navigate(Screen.ForgotPassword.route)
                    },
                    onNavigateToCheckEmail = { email ->
                         navController.navigate(Screen.CheckEmail.createRoute(email))
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    navController = navController,
                    onRegisterSuccess = { email -> // After registration, go to check email
                        navController.navigate(Screen.CheckEmail.createRoute(email)) {
                            popUpTo(Screen.Login.route) // Clear login from backstack, stay in auth graph
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Screen.CheckEmail.route,
                arguments = Screen.CheckEmail.navArguments,
                deepLinks = listOf(Screen.CheckEmail.deepLink)
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: "your_email@example.com"
                CheckEmailScreen(
                    email = email,
                    navController = navController,
                    onEmailVerified = {
                        // Option 1: Navigate to login, user logs in again
                        // navController.navigate(Screen.Login.route) {
                        //    popUpTo(AUTH_GRAPH_ROUTE) { inclusive = true } // Clear auth stack
                        //    launchSingleTop = true
                        // }
                        // Option 2: If verification implies login, directly to role graph (needs UserRole)
                        // This might require re-fetching user or having role info passed differently
                        // For now, let's assume they go back to login or a generic message.
                        // Consider what UserRole to pass if direct navigation is desired.
                        // onNavigateToRoleGraphFromAuth(UserRole.FARMER) // Example: Needs actual role
                        // Or pop up to login to re-initiate the login flow which then gets the role
                        navController.popBackStack(Screen.Login.route, inclusive = false)

                    },
                    onGoToLogin = {
                         navController.popBackStack(Screen.Login.route, inclusive = false)
                    }
                )
            }
            // composable(Screen.ForgotPassword.route) {
            //     ForgotPasswordScreen(navController = navController)
            // }
        }

        // Farmer User Graph
        navigation(
            // startDestination = Screen.FarmerDashboard.route, // Example
            startDestination = "farmer_placeholder", // Replace with actual start screen
            route = FARMER_GRAPH_ROUTE
        ) {
            // Example screen, replace with actual Farmer screens
            composable("farmer_placeholder") {
                // FarmerDashboardScreen(navController)
            }
            // composable(Screen.FarmerDashboard.route) { FarmerDashboardScreen(navController) }
            // composable(Screen.AddChicken.route) { AddChickenScreen(navController) }
            // composable(Screen.ChickenList.route) { ChickenListScreen(navController) }
            // composable(Screen.FarmerProfile.route, deepLinks = listOf(Screen.FarmerProfile.deepLink)) { FarmerProfileScreen(navController) }
            // ... other farmer screens
        }

        // General User Graph (Buyer)
        navigation(
            // startDestination = Screen.BuyerDashboard.route, // Example
            startDestination = "buyer_placeholder", // Replace with actual start screen
            route = GENERAL_USER_GRAPH_ROUTE
        ) {
            // Example screen, replace with actual Buyer screens
            composable("buyer_placeholder") {
                // BuyerDashboardScreen(navController)
            }
            // composable(Screen.BuyerDashboard.route) { BuyerDashboardScreen(navController) }
            // composable(Screen.Marketplace.route) { MarketplaceScreen(navController) }
            // composable(
            //     route = Screen.ProductDetails.route,
            //     arguments = Screen.ProductDetails.navArguments,
            //     deepLinks = listOf(Screen.ProductDetails.deepLink)
            // ) { backStackEntry ->
            //     val productId = backStackEntry.arguments?.getString("productId")
            //     ProductDetailsScreen(navController, productId)
            // }
            // ... other buyer screens
        }

        // High-Level User Graph (Admin)
        navigation(
            // startDestination = Screen.AdminDashboard.route, // Example
            startDestination = "admin_placeholder", // Replace with actual start screen
            route = HIGH_LEVEL_USER_GRAPH_ROUTE
        ) {
            // Example screen, replace with actual Admin screens
            composable("admin_placeholder") {
                // AdminDashboardScreen(navController)
            }
            // composable(Screen.AdminDashboard.route) { AdminDashboardScreen(navController) }
            // composable(Screen.UserManagement.route) { UserManagementScreen(navController) }
            // ... other admin screens
        }

        // Vet User Graph
        navigation(
            // startDestination = Screen.VetDashboard.route, // Example
            startDestination = "vet_placeholder", // Replace with actual start screen
            route = VET_USER_GRAPH_ROUTE
        ) {
            // Example screen, replace with actual Vet screens
            composable("vet_placeholder") {
                // VetDashboardScreen(navController)
            }
            // composable(Screen.VetDashboard.route) { VetDashboardScreen(navController) }
            // composable(Screen.AppointmentList.route) { AppointmentListScreen(navController) }
            // ... other vet screens
        }

        // Common screens accessible from multiple graphs (can be defined at root level or within each graph if context specific)
        // Example:
        // composable(Screen.Settings.route) { SettingsScreen(navController) }
        // composable(Screen.Notifications.route, deepLinks = listOf(Screen.Notifications.deepLink)) { NotificationsScreen(navController) }
    }
}

// Placeholder composables for screens not yet created to make NavHost compile.
// Remove these as you implement the actual screens.

@Composable
fun PlaceholderScreen(name: String, navController: NavHostController) {
    // Simple placeholder
    androidx.compose.material3.Text("Placeholder for $name")
}

// Example:
// @Composable fun FarmerDashboardScreen(navController: NavHostController) = PlaceholderScreen("Farmer Dashboard", navController)
// @Composable fun BuyerDashboardScreen(navController: NavHostController) = PlaceholderScreen("Buyer Dashboard", navController)
// @Composable fun AdminDashboardScreen(navController: NavHostController) = PlaceholderScreen("Admin Dashboard", navController)
// @Composable fun VetDashboardScreen(navController: NavHostController) = PlaceholderScreen("Vet Dashboard", navController)

// Ensure you create the actual screen composables:
// LoginScreen.kt, RegisterScreen.kt, CheckEmailScreen.kt
// And then the dashboard/home screens for each user role to serve as start destinations for their respective graphs.
