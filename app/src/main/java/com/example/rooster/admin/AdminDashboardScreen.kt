package com.example.rooster.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rooster.admin.analytics.AnalyticsDashboardScreen
import com.example.rooster.admin.contentmoderation.ContentModerationScreen
import com.example.rooster.admin.featureflags.FeatureFlagScreen
import com.example.rooster.admin.systemmonitoring.SystemMonitoringScreen
import com.example.rooster.admin.usermanagement.UserManagementScreen

// --- Navigation Routes ---
object AdminDestinations {
    const val DASHBOARD = "admin_dashboard"
    const val SYSTEM_MONITORING = "admin_system_monitoring"
    const val USER_MANAGEMENT = "admin_user_management"
    const val ANALYTICS_DASHBOARD = "admin_analytics_dashboard"
    const val CONTENT_MODERATION = "admin_content_moderation"
    const val FEATURE_FLAGS = "admin_feature_flags"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.DarkGray)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { AdminDashboardButton(navController, "System Monitoring", AdminDestinations.SYSTEM_MONITORING) }
            item { AdminDashboardButton(navController, "User Management", AdminDestinations.USER_MANAGEMENT) }
            item { AdminDashboardButton(navController, "Analytics Dashboard", AdminDestinations.ANALYTICS_DASHBOARD) }
            item { AdminDashboardButton(navController, "Content Moderation", AdminDestinations.CONTENT_MODERATION) }
            item { AdminDashboardButton(navController, "Feature Flags", AdminDestinations.FEATURE_FLAGS) }
        }
    }
}

@Composable
fun AdminDashboardButton(navController: NavController, text: String, route: String) {
    Button(
        onClick = { navController.navigate(route) },
        modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
    ) {
        Text(text)
    }
}

@Composable
fun AdminFeatureNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AdminDestinations.DASHBOARD) {
        composable(AdminDestinations.DASHBOARD) { AdminDashboardScreen(navController) }
        composable(AdminDestinations.SYSTEM_MONITORING) { SystemMonitoringScreen() }
        composable(AdminDestinations.USER_MANAGEMENT) { UserManagementScreen() }
        composable(AdminDestinations.ANALYTICS_DASHBOARD) { AnalyticsDashboardScreen() }
        composable(AdminDestinations.CONTENT_MODERATION) { ContentModerationScreen() }
        composable(AdminDestinations.FEATURE_FLAGS) { FeatureFlagScreen() }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdminDashboardScreen() {
    MaterialTheme {
        // Previewing the whole navigator to see dashboard
        AdminFeatureNavigator()
    }
}
