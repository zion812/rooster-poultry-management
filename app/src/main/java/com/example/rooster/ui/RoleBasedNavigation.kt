// use context7
package com.example.rooster.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.rooster.NavigationRoute
import com.example.rooster.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleBasedNavigationDrawer(
    navController: NavController,
    authViewModel: AuthViewModel,
    currentUserRole: String,
    content: @Composable () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            RoleBasedDrawerContent(
                currentUserRole = currentUserRole,
                onNavigate = { route ->
                    scope.launch {
                        drawerState.close()
                        navController.navigate(route)
                    }
                },
                onLogout = {
                    scope.launch {
                        authViewModel.logout()
                        drawerState.close()
                    }
                },
            )
        },
        content = content,
    )
}

@Composable
private fun RoleBasedDrawerContent(
    currentUserRole: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
) {
    ModalDrawerSheet {
        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
        ) {
            // Header
            Text(
                text = "🐓 Rooster App",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = getRoleDisplayName(currentUserRole),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Role-specific navigation items
            getRoleBasedNavigationItems(currentUserRole).forEach { item ->
                NavigationDrawerItem(
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(item.label) },
                    selected = false,
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Common items
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                label = { Text("Settings") },
                selected = false,
                onClick = { onNavigate(NavigationRoute.SETTINGS.route) },
            )

            NavigationDrawerItem(
                icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                label = { Text("Logout") },
                selected = false,
                onClick = onLogout,
            )
        }
    }
}

@Composable
fun RoleBasedBottomNavigation(
    navController: NavController,
    currentUserRole: String,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        getRoleBasedBottomNavItems(currentUserRole).forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
            )
        }
    }
}

data class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val requiresVerification: Boolean = false,
)

private fun getRoleBasedNavigationItems(role: String): List<NavigationItem> {
    return when (role.lowercase()) {
        "farmer" ->
            listOf(
                NavigationItem(NavigationRoute.FARMER_HOME.route, "Dashboard", Icons.Default.Dashboard),
                NavigationItem(NavigationRoute.MARKETPLACE.route, "Marketplace", Icons.Default.Store),
                NavigationItem(NavigationRoute.COMMUNITY.route, "Community", Icons.Default.Group),
                NavigationItem(NavigationRoute.TRANSFERS.route, "Transfers", Icons.Default.Transform),
                NavigationItem(
                    NavigationRoute.VET_CONSULTATION.route,
                    "Vet Consultation",
                    Icons.Default.MedicalServices,
                ),
                NavigationItem(
                    NavigationRoute.IOT_DASHBOARD.route,
                    "IoT Dashboard",
                    Icons.Default.DeviceHub,
                ),
                NavigationItem(NavigationRoute.AUCTIONS.route, "Auctions", Icons.Default.Gavel),
            )

        "general" ->
            listOf(
                NavigationItem(NavigationRoute.MARKETPLACE.route, "Marketplace", Icons.Default.Store),
                NavigationItem(NavigationRoute.COMMUNITY.route, "Community", Icons.Default.Group),
                NavigationItem(NavigationRoute.AUCTIONS.route, "Auctions", Icons.Default.Gavel),
                NavigationItem(NavigationRoute.TRANSFERS.route, "My Orders", Icons.Default.Receipt),
            )

        "highlevel", "high_level" ->
            listOf(
                NavigationItem(
                    NavigationRoute.HIGH_LEVEL_HOME.route,
                    "Dashboard",
                    Icons.Default.Dashboard,
                ),
                NavigationItem(NavigationRoute.MARKETPLACE.route, "Marketplace", Icons.Default.Store),
                NavigationItem(NavigationRoute.COMMUNITY.route, "Community", Icons.Default.Group),
                NavigationItem(NavigationRoute.TRANSFERS.route, "Transfers", Icons.Default.Transform),
                NavigationItem(
                    NavigationRoute.ACTIVITY_VERIFICATION.route,
                    "Verification",
                    Icons.Default.VerifiedUser,
                ),
                NavigationItem(NavigationRoute.AUCTIONS.route, "Auctions", Icons.Default.Gavel),
            )

        else ->
            listOf(
                NavigationItem(NavigationRoute.MARKETPLACE.route, "Marketplace", Icons.Default.Store),
                NavigationItem(NavigationRoute.COMMUNITY.route, "Community", Icons.Default.Group),
            )
    }
}

private fun getRoleBasedBottomNavItems(role: String): List<NavigationItem> {
    return when (role.lowercase()) {
        "farmer" ->
            listOf(
                NavigationItem(NavigationRoute.FARMER_HOME.route, "Home", Icons.Default.Home),
                NavigationItem(NavigationRoute.MARKETPLACE.route, "Market", Icons.Default.Store),
                NavigationItem(NavigationRoute.COMMUNITY.route, "Community", Icons.Default.Group),
                NavigationItem(NavigationRoute.TRANSFERS.route, "Transfers", Icons.Default.Transform),
                NavigationItem(NavigationRoute.PROFILE.route, "Profile", Icons.Default.Person),
            )

        "general" ->
            listOf(
                NavigationItem(NavigationRoute.MARKETPLACE.route, "Market", Icons.Default.Store),
                NavigationItem(NavigationRoute.COMMUNITY.route, "Community", Icons.Default.Group),
                NavigationItem(NavigationRoute.AUCTIONS.route, "Auctions", Icons.Default.Gavel),
                NavigationItem(NavigationRoute.TRANSFERS.route, "Orders", Icons.Default.Receipt),
                NavigationItem(NavigationRoute.PROFILE.route, "Profile", Icons.Default.Person),
            )

        "highlevel", "high_level" ->
            listOf(
                NavigationItem(
                    NavigationRoute.HIGH_LEVEL_HOME.route,
                    "Dashboard",
                    Icons.Default.Dashboard,
                ),
                NavigationItem(NavigationRoute.MARKETPLACE.route, "Market", Icons.Default.Store),
                NavigationItem(
                    NavigationRoute.ACTIVITY_VERIFICATION.route,
                    "Verify",
                    Icons.Default.VerifiedUser,
                ),
                NavigationItem(NavigationRoute.COMMUNITY.route, "Community", Icons.Default.Group),
                NavigationItem(NavigationRoute.PROFILE.route, "Profile", Icons.Default.Person),
            )

        else ->
            listOf(
                NavigationItem(NavigationRoute.MARKETPLACE.route, "Market", Icons.Default.Store),
                NavigationItem(NavigationRoute.COMMUNITY.route, "Community", Icons.Default.Group),
                NavigationItem(NavigationRoute.PROFILE.route, "Profile", Icons.Default.Person),
            )
    }
}

private fun getRoleDisplayName(role: String): String {
    return when (role.lowercase()) {
        "farmer" -> "🌾 Farmer Account"
        "general" -> "🛒 Consumer Account"
        "highlevel", "high_level" -> "📊 Manager Account"
        else -> "👤 User Account"
    }
}

@Composable
fun RoleBasedFAB(
    currentUserRole: String,
    onFabClick: () -> Unit,
) {
    when (currentUserRole.lowercase()) {
        "farmer" -> {
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Fowl")
            }
        }

        "highlevel", "high_level" -> {
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Actions")
            }
        }

        else -> {
            // No FAB for general users or other roles
        }
    }
}
