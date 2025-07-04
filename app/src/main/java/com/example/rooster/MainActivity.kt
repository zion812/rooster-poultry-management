package com.example.rooster

import android.app.Activity
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.auth.domain.repository.AuthRepository
import com.example.rooster.core.navigation.* // Import route definitions
import com.example.rooster.navigation.RoosterNavHost
import com.example.rooster.ui.main.FarmerUserBottomBar
import com.example.rooster.ui.main.GeneralUserBottomBar
import com.example.rooster.ui.main.HighLevelUserBottomBar
import com.example.rooster.ui.theme.RoosterTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject


data class MainUiState(
    val isLoading: Boolean = true,
    val startGraphRoute: String = Screen.Splash.route, // Default to Splash
    val currentRole: UserRole? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = authRepository.getCurrentUser()
        .distinctUntilChanged()
        .map { user ->
            if (user == null) {
                MainUiState(isLoading = false, startGraphRoute = AUTH_GRAPH_ROUTE, currentRole = null)
            } else {
                // In a real app, you'd get isEmailVerified from the user object or another call
                // For this example, let's assume a method in authRepository or property on user
                val isEmailVerified = authRepository.isUserEmailVerified() // Or user.isEmailVerified

                if (!isEmailVerified) {
                    // If email not verified, always start in AUTH_GRAPH to show CheckEmailScreen or Login
                    MainUiState(isLoading = false, startGraphRoute = AUTH_GRAPH_ROUTE, currentRole = user.role)
                } else {
                    val roleGraph = when (user.role) {
                        UserRole.FARMER -> FARMER_GRAPH_ROUTE
                        UserRole.BUYER -> GENERAL_USER_GRAPH_ROUTE
                        UserRole.ADMIN -> HIGH_LEVEL_USER_GRAPH_ROUTE
                        UserRole.VETERINARIAN -> VET_USER_GRAPH_ROUTE // Ensure this graph is defined
                    }
                    MainUiState(isLoading = false, startGraphRoute = roleGraph, currentRole = user.role)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState() // Initial state: loading, splash
        )
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val uiState by mainViewModel.uiState.collectAsState()

            splashScreen.setKeepOnScreenCondition { uiState.isLoading }

            if (!uiState.isLoading && uiState.startGraphRoute != Screen.Splash.route) {
                RoosterTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        RoosterApp(mainUiState = uiState)
                    }
                }
            }
            // Optional: else if (uiState.isLoading) { FullScreenLoader() }
            // If startGraphRoute is Splash, it implies Splash is a composable in NavHost (not typical for AndroidX Splash)
        }
    }
}

@Composable
fun RoosterApp(mainUiState: MainUiState) {
    val navController = rememberNavController()

    // Basic deep link handling:
    // This captures the intent when RoosterApp is first composed.
    // For more robust handling (e.g., new intents while app is running),
    // you might need to use `rememberUpdatedState` for the intent or handle it in MainActivity's onNewIntent.
    val activity = LocalContext.current as? Activity
    LaunchedEffect(activity?.intent?.data) { // React if intent data changes
        activity?.intent?.data?.let { deepLinkUri ->
            // Check if the NavController's graph can handle this deep link.
            // This prevents crashes if a non-app URI is received.
            if (navController.graph.hasDeepLink(deepLinkUri)) {
                navController.navigate(deepLinkUri)
                // It's good practice to clear the intent's data after handling
                // to prevent re-processing on configuration changes or activity recreation.
                activity.intent.data = null
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine current top-level graph for bottom bar visibility by finding the route of the parent graph
    var currentTopLevelGraphRoute: String? = currentRoute
    var tempNode = navController.graph.findNode(currentRoute ?: "")
    while (tempNode?.parent != null && tempNode.parent != navController.graph) {
        tempNode = tempNode.parent
    }
    currentTopLevelGraphRoute = tempNode?.route


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            when (currentTopLevelGraphRoute) {
                GENERAL_USER_GRAPH_ROUTE -> GeneralUserBottomBar(navController, currentRoute)
                FARMER_GRAPH_ROUTE -> FarmerUserBottomBar(navController, currentRoute)
                HIGH_LEVEL_USER_GRAPH_ROUTE -> HighLevelUserBottomBar(navController, currentRoute) // Admin
                VET_USER_GRAPH_ROUTE -> HighLevelUserBottomBar(navController, currentRoute) // Vet (can use same as Admin or a custom one)
            }
        }
    ) { paddingValues ->
        RoosterNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            startDestination = mainUiState.startGraphRoute,
            onNavigateToRoleGraphFromAuth = { authenticatedUserRole ->
                val graphRoute = when (authenticatedUserRole) {
                    UserRole.FARMER -> FARMER_GRAPH_ROUTE
                    UserRole.BUYER -> GENERAL_USER_GRAPH_ROUTE
                    UserRole.ADMIN -> HIGH_LEVEL_USER_GRAPH_ROUTE
                    UserRole.VETERINARIAN -> VET_USER_GRAPH_ROUTE
                }
                navController.navigate(graphRoute) {
                    popUpTo(AUTH_GRAPH_ROUTE) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }
}
