package com.example.rooster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
 feat/login-screen-v1

 feat/login-screen-v1
 main
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rooster.core.auth.domain.model.UserRole // Assuming UserRole is accessible
import com.example.rooster.core.auth.domain.repository.AuthRepository
import com.example.rooster.core.navigation.* // Import route definitions
import com.example.rooster.navigation.RoosterNavHost
import com.example.rooster.ui.main.FarmerUserBottomBar
import com.example.rooster.ui.main.GeneralUserBottomBar
import com.example.rooster.ui.main.HighLevelUserBottomBar
import com.example.rooster.ui.theme.RoosterTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

import androidx.compose.foundation.layout.Box

 main
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
 feat/login-screen-v1
import androidx.compose.ui.Alignment

 feat/login-screen-v1
import androidx.compose.ui.Alignment

 main
 main
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
 feat/login-screen-v1

 feat/login-screen-v1
 main
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.auth.domain.repository.AuthRepository
import com.example.rooster.core.navigation.*
 feat/login-screen-v1


import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rooster.core.auth.domain.model.UserRole // Assuming UserRole is accessible
import com.example.rooster.core.auth.domain.repository.AuthRepository
import com.example.rooster.core.navigation.* // Import route definitions
 main
 main
import com.example.rooster.navigation.RoosterNavHost
import com.example.rooster.ui.main.FarmerUserBottomBar
import com.example.rooster.ui.main.GeneralUserBottomBar
import com.example.rooster.ui.main.HighLevelUserBottomBar
import com.example.rooster.ui.theme.RoosterTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
 feat/login-screen-v1

 feat/login-screen-v1
 main
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = true,
    // Default to Splash, then Auth or Role-Specific Graph once auth state is known
    val startGraphRoute: String = Screen.Splash.route,
    val currentRole: UserRole? = null // Current role of the logged-in user
)

 feat/login-screen-v1


import kotlinx.coroutines.flow.map
import javax.inject.Inject

// A simple ViewModel to observe auth state.
// In a real app, this might be more complex or part of a dedicated AuthViewModel.
 main
 main
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
 feat/login-screen-v1

 feat/login-screen-v1
 main

    val uiState: StateFlow<MainUiState> = authRepository.getCurrentUser()
        .distinctUntilChanged() // Only react to actual changes in user state
        .map { user ->
            if (user == null) {
                MainUiState(isLoading = false, startGraphRoute = AUTH_GRAPH_ROUTE, currentRole = null)
            } else {
                val roleGraph = when (user.role) {
                    UserRole.FARMER -> FARMER_USER_GRAPH_ROUTE
                    UserRole.BUYER -> GENERAL_USER_GRAPH_ROUTE
                    UserRole.ADMIN -> HIGH_LEVEL_USER_GRAPH_ROUTE
 feat/login-screen-v1
                    UserRole.VETERINARIAN -> VET_USER_GRAPH_ROUTE // Changed to VET_USER_GRAPH_ROUTE

                    UserRole.VETERINARIAN -> HIGH_LEVEL_USER_GRAPH_ROUTE
 main
                }
                MainUiState(isLoading = false, startGraphRoute = roleGraph, currentRole = user.role)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState() // Initial state with isLoading = true
        )
 feat/login-screen-v1


    // In a real app, observe authRepository.getCurrentUser() or similar
    // For now, simulate different initial states for testing navigation.
    // val initialAuthState: StateFlow<Pair<Boolean, UserRole?>> = authRepository.getCurrentUser().map { user ->
    //     Pair(user != null, user?.role)
    // }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(false, null))

    // Let's use a mutable state for easier simulation in this step
    // false = logged out, true = logged in (default to FARMER for simulation)
    var mockIsLoggedIn by mutableStateOf(false)
    var mockUserRole by mutableStateOf(UserRole.FARMER) // Default role for logged-in mock

    fun getStartDestination(): String {
        return if (mockIsLoggedIn) {
            when (mockUserRole) {
                UserRole.FARMER -> FARMER_USER_GRAPH_ROUTE
                UserRole.BUYER -> GENERAL_USER_GRAPH_ROUTE // Buyer uses General graph
                UserRole.ADMIN -> HIGH_LEVEL_USER_GRAPH_ROUTE
                UserRole.VETERINARIAN -> HIGH_LEVEL_USER_GRAPH_ROUTE // Vet uses High-Level graph
                // else -> GENERAL_USER_GRAPH_ROUTE // Should not happen if role is always set
            }
        } else {
            AUTH_GRAPH_ROUTE // If not logged in, start with Auth graph
            // Or Screen.Splash.route if you have a splash screen logic to determine this
        }
    }
 main
 main
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen() // Keep a reference to the splash screen
        super.onCreate(savedInstanceState)
 feat/login-screen-v1

 feat/login-screen-v1

        installSplashScreen() // Handles splash screen display
 main
 main
        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val uiState by mainViewModel.uiState.collectAsState()

            // Keep splash screen visible until isLoading is false (auth state determined)
            splashScreen.setKeepOnScreenCondition { uiState.isLoading }

            if (!uiState.isLoading) { // Only render app content once auth state is resolved
                RoosterTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        RoosterApp(mainUiState = uiState)
                    }
                }
            }
            // Optionally, display a full-screen loader here if !uiState.isLoading check is removed
            // and splashScreen.setKeepOnScreenCondition is not used, but using AndroidX Splash is cleaner.
        }
    }
}

@Composable
 feat/login-screen-v1
fun RoosterApp(mainUiState: MainUiState) { // Pass MainUiState directly
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    // Determine current top-level graph for bottom bar visibility
    // This logic might need refinement if routes can be part of multiple parent graphs or no graph.
    var currentTopLevelGraphRoute: String? = currentRoute
    while (navController.graph.findNode(currentTopLevelGraphRoute ?: "")?.parent != null &&
           navController.graph.findNode(currentTopLevelGraphRoute ?: "") != navController.graph) {
        currentTopLevelGraphRoute = navController.graph.findNode(currentTopLevelGraphRoute ?: "")?.parent?.route
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom bar only for the main user graphs
            when (currentTopLevelGraphRoute) {
                GENERAL_USER_GRAPH_ROUTE -> GeneralUserBottomBar(navController, currentRoute)
                FARMER_USER_GRAPH_ROUTE -> FarmerUserBottomBar(navController, currentRoute)
                HIGH_LEVEL_USER_GRAPH_ROUTE -> HighLevelUserBottomBar(navController, currentRoute) // For Admin
                VET_USER_GRAPH_ROUTE -> HighLevelUserBottomBar(navController, currentRoute) // Vet uses HighLevel Bottom Bar for now
                                         // Or create VetUserBottomBar if tabs are different
                // No bottom bar for AUTH_GRAPH_ROUTE or other screens like Splash

 feat/login-screen-v1
fun RoosterApp(mainUiState: MainUiState) { // Pass MainUiState directly
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    // Determine current top-level graph for bottom bar visibility
    // This logic might need refinement if routes can be part of multiple parent graphs or no graph.
    var currentTopLevelGraphRoute: String? = currentRoute
    while (navController.graph.findNode(currentTopLevelGraphRoute ?: "")?.parent != null &&
           navController.graph.findNode(currentTopLevelGraphRoute ?: "") != navController.graph) {
        currentTopLevelGraphRoute = navController.graph.findNode(currentTopLevelGraphRoute ?: "")?.parent?.route
    }



fun RoosterApp(mainViewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    // Observe current route for bottom bar visibility and selection
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentGraphRoute = navBackStackEntry?.destination?.parent?.route

    // Determine start destination based on (simulated) auth state
    // In a real app, observe mainViewModel.initialAuthState or similar Flow
    // For this step, we use the mock values from MainViewModel directly.
    // To test different roles, you'd change mockIsLoggedIn and mockUserRole in MainViewModel
    // (e.g. via a debug menu or test setup).
    // mainViewModel.mockIsLoggedIn = false // Simulate logged out
    // mainViewModel.mockIsLoggedIn = true; mainViewModel.mockUserRole = UserRole.BUYER // Simulate logged in as Buyer

    val startDestination = mainViewModel.getStartDestination()


 main
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom bar only for the main user graphs
            when (currentGraphRoute) {
                GENERAL_USER_GRAPH_ROUTE -> GeneralUserBottomBar(navController, currentRoute)
                FARMER_USER_GRAPH_ROUTE -> FarmerUserBottomBar(navController, currentRoute)
                HIGH_LEVEL_USER_GRAPH_ROUTE -> HighLevelUserBottomBar(navController, currentRoute)
                // No bottom bar for AUTH_GRAPH_ROUTE or other screens like Splash
 feat/login-screen-v1
            }
        }
    ) { paddingValues ->
        RoosterNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            startDestination = startDestination,
            onNavigateToRoleGraphFromAuth = { authenticatedUserRole -> // Renamed for clarity, accepts UserRole
                val graphRoute = when (authenticatedUserRole) {
                    UserRole.FARMER -> FARMER_USER_GRAPH_ROUTE
                    UserRole.BUYER -> GENERAL_USER_GRAPH_ROUTE
                    UserRole.ADMIN -> HIGH_LEVEL_USER_GRAPH_ROUTE
                    UserRole.VETERINARIAN -> HIGH_LEVEL_USER_GRAPH_ROUTE
                    // No else needed if UserRole is a sealed class and all cases are handled,
                    // or if there's a defined default/fallback.
                }
                navController.navigate(graphRoute) {
                    popUpTo(AUTH_GRAPH_ROUTE) { inclusive = true } // Clear auth stack
                    launchSingleTop = true
                }
                // Update simulated logged-in state for next RoosterApp recomposition
                mainViewModel.mockIsLoggedIn = true
                mainViewModel.mockUserRole = authenticatedUserRole // Store the actual role
            }

 main
            }
        }
    ) { paddingValues ->
        RoosterNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            startDestination = startDestination,
            onNavigateToRoleGraphFromAuth = { authenticatedUserRole -> // Renamed for clarity, accepts UserRole
                val graphRoute = when (authenticatedUserRole) {
                    UserRole.FARMER -> FARMER_USER_GRAPH_ROUTE
                    UserRole.BUYER -> GENERAL_USER_GRAPH_ROUTE
                    UserRole.ADMIN -> HIGH_LEVEL_USER_GRAPH_ROUTE
                    UserRole.VETERINARIAN -> HIGH_LEVEL_USER_GRAPH_ROUTE
                    // No else needed if UserRole is a sealed class and all cases are handled,
                    // or if there's a defined default/fallback.
                }
                navController.navigate(graphRoute) {
                    popUpTo(AUTH_GRAPH_ROUTE) { inclusive = true } // Clear auth stack
                    launchSingleTop = true
                }
                // Update simulated logged-in state for next RoosterApp recomposition
                mainViewModel.mockIsLoggedIn = true
                mainViewModel.mockUserRole = authenticatedUserRole // Store the actual role
            }
 feat/login-screen-v1

 main
 main
        )
    }
}
// Notes:
// - Introduced a MainViewModel to (eventually) observe real auth state. For now, it has mock flags
//   `mockIsLoggedIn` and `mockUserRole` to simulate different startup scenarios.
// - `getStartDestination()` in MainViewModel determines the initial graph for RoosterNavHost.
// - `RoosterApp` composable:
//   - Remembers a NavController.
//   - Observes `navController.currentBackStackEntryAsState()` to get `currentRoute` and `currentGraphRoute`.
//   - Uses `currentGraphRoute` to conditionally display the correct role-specific bottom navigation bar.
//     The bottom bar is hidden for `AUTH_GRAPH_ROUTE` or other non-tabbed top-level screens.
//   - Calls `RoosterNavHost`, passing the navController, startDestination, and paddingValues.
//   - Implements `onNavigateToRoleGraph`: This lambda is passed to RoosterNavHost (and then to LoginScreen).
//     When called after successful login, it navigates to the specified role graph and clears the auth graph
//     from the back stack. It also updates the mock auth state in MainViewModel for this simulation.
// - The actual UserRole enum needs to be accessible here (e.g. from core.auth.domain.model).
// - This setup provides the main navigation structure and conditional UI based on auth state and current route.
// - The actual LoginScreen and RegisterScreen imports in RoosterNavHost.kt will need to be resolved
//   once module dependencies allow it.
// - The `installSplashScreen()` is kept, implying that splash screen logic might eventually
//   determine the initial route (e.g., by checking auth state and then navigating). For now,
//   `startDestination` in `RoosterNavHost` is driven by `MainViewModel`'s mock state.
