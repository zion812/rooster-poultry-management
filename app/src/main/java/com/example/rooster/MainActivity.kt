package com.example.rooster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

// A simple ViewModel to observe auth state.
// In a real app, this might be more complex or part of a dedicated AuthViewModel.
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
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
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen() // Handles splash screen display
        enableEdgeToEdge()

        setContent {
            RoosterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RoosterApp()
                }
            }
        }
    }
}

@Composable
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


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom bar only for the main user graphs
            when (currentGraphRoute) {
                GENERAL_USER_GRAPH_ROUTE -> GeneralUserBottomBar(navController, currentRoute)
                FARMER_USER_GRAPH_ROUTE -> FarmerUserBottomBar(navController, currentRoute)
                HIGH_LEVEL_USER_GRAPH_ROUTE -> HighLevelUserBottomBar(navController, currentRoute)
                // No bottom bar for AUTH_GRAPH_ROUTE or other screens like Splash
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
