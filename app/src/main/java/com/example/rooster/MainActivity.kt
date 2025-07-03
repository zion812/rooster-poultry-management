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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.auth.domain.repository.AuthRepository
import com.example.rooster.core.navigation.*
import com.example.rooster.navigation.RoosterNavHost
import com.example.rooster.ui.main.FarmerUserBottomBar
import com.example.rooster.ui.main.GeneralUserBottomBar
import com.example.rooster.ui.main.HighLevelUserBottomBar
import com.example.rooster.ui.theme.RoosterTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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
                    UserRole.VETERINARIAN -> HIGH_LEVEL_USER_GRAPH_ROUTE
                }
                MainUiState(isLoading = false, startGraphRoute = roleGraph, currentRole = user.role)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState() // Initial state with isLoading = true
        )
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen() // Keep a reference to the splash screen
        super.onCreate(savedInstanceState)
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
