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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.rooster.NavigationRoute
import com.example.rooster.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleBasedNavigationDrawer(
    navController: NavController,
    currentUserRole: String,
    content: @Composable () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.fillMaxHeight()) {
                    Text(
                        stringResource(R.string.app_name),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    HorizontalDivider()
                    Spacer(Modifier.padding(top = 10.dp))

                    getNavigationItems(currentUserRole).forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = navController.currentDestination?.route == item.route.route,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(item.route.route)
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
                        label = { Text("Logout") },
                        selected = false,
                        onClick = {
                            // Handle logout
                            scope.launch {
                                drawerState.close()
                                // Example: navController.navigate("logout_route")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        },
        content = content,
    )
}

@Composable
fun RoleBasedBottomNavigation(
    navController: NavController,
    currentUserRole: String,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        getNavigationItems(currentUserRole).forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route.route,
                onClick = {
                    if (currentRoute != item.route.route) {
                        navController.navigate(item.route.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
            )
        }
    }
}

private fun getNavigationItems(currentUserRole: String): List<NavigationItem> {
    return when (currentUserRole.lowercase()) {
        "general" ->
            listOf(
                NavigationItem(NavigationRoute.Home, "Home", Icons.Default.Home),
                NavigationItem(NavigationRoute.FarmerHome, "Farm", Icons.Default.Dashboard),
                NavigationItem(NavigationRoute.Marketplace, "Market", Icons.Default.Store),
                NavigationItem(
                    NavigationRoute.ActivityVerification,
                    "Verify",
                    Icons.Default.VerifiedUser,
                ),
                NavigationItem(NavigationRoute.Community, "Community", Icons.Default.Group),
                NavigationItem(NavigationRoute.Profile, "Profile", Icons.Default.Person),
                NavigationItem(NavigationRoute.Transfers, "Transfers", Icons.Default.Transform),
                NavigationItem(NavigationRoute.Settings, "Settings", Icons.Default.Settings),
                NavigationItem(NavigationRoute.Help, "Help", Icons.Default.MedicalServices),
                NavigationItem(NavigationRoute.VetConsultation, "Vet", Icons.Default.MedicalServices),
                NavigationItem(NavigationRoute.IoTDashboard, "IoT", Icons.Default.DeviceHub),
                NavigationItem(NavigationRoute.Auctions, "Auctions", Icons.Default.Gavel),
                NavigationItem(NavigationRoute.HighLevelHome, "Manager", Icons.Default.AdminPanelSettings),
                NavigationItem(NavigationRoute.Auth, "Auth", Icons.Default.Person),
                NavigationItem(NavigationRoute.Cart, "Cart", Icons.Default.Receipt),
                NavigationItem(NavigationRoute.Feedback, "Feedback", Icons.Default.Receipt),
                NavigationItem(NavigationRoute.OrderHistory, "Orders", Icons.Default.Receipt),
                NavigationItem(NavigationRoute.OrderDetail, "Order Detail", Icons.Default.Receipt),
                NavigationItem(NavigationRoute.Payment, "Payment", Icons.Default.Receipt),
                NavigationItem(NavigationRoute.ProfileEdit, "Edit Profile", Icons.Default.Person),
                NavigationItem(NavigationRoute.HelpSupport, "Help & Support", Icons.Default.MedicalServices),
                NavigationItem(NavigationRoute.ComprehensiveMessaging, "Messages", Icons.Default.Receipt),
                NavigationItem(NavigationRoute.Chat(""), "Chat", Icons.Default.Receipt),
                NavigationItem(NavigationRoute.Fowl, "Fowl", Icons.Default.Receipt),
                NavigationItem(NavigationRoute.Diagnostics, "Diagnostics", Icons.Default.Receipt),
                NavigationItem(NavigationRoute.HealthManagement, "Health", Icons.Default.Receipt),
                NavigationItem(NavigationRoute.FarmerHome, "Farmer Home", Icons.Default.Home),
                NavigationItem(NavigationRoute.MarketplaceListingCreate, "Create Listing", Icons.Default.Add),
                NavigationItem(NavigationRoute.MarketplaceListingEdit(""), "Edit Listing", Icons.Default.Add),
                NavigationItem(NavigationRoute.MarketplaceListingDetail(""), "Listing Detail", Icons.Default.Store)

            )

        "farmer" ->
            listOf(
                NavigationItem(NavigationRoute.FarmerHome, "Farm", Icons.Default.Home),
                NavigationItem(NavigationRoute.Marketplace, "Market", Icons.Default.Store),
                NavigationItem(NavigationRoute.Transfers, "Transfers", Icons.Default.Transform),
                NavigationItem(NavigationRoute.Settings, "Settings", Icons.Default.Settings),
                NavigationItem(NavigationRoute.Help, "Help", Icons.Default.MedicalServices),
                NavigationItem(NavigationRoute.VetConsultation, "Vet", Icons.Default.MedicalServices),
                NavigationItem(NavigationRoute.IoTDashboard, "IoT", Icons.Default.DeviceHub),
                NavigationItem(NavigationRoute.Auctions, "Auctions", Icons.Default.Gavel),
                NavigationItem(NavigationRoute.Community, "Community", Icons.Default.Group),
                NavigationItem(NavigationRoute.Profile, "Profile", Icons.Default.Person),
            )

        "highlevel", "high_level" ->
            listOf(
                NavigationItem(NavigationRoute.HighLevelHome, "Dashboard", Icons.Default.Dashboard),
                NavigationItem(NavigationRoute.Marketplace, "Market", Icons.Default.Store),
                NavigationItem(
                    NavigationRoute.ActivityVerification,
                    "Verify",
                    Icons.Default.VerifiedUser,
                ),
                NavigationItem(NavigationRoute.Community, "Community", Icons.Default.Group),
                NavigationItem(NavigationRoute.Profile, "Profile", Icons.Default.Person),
            )

        else ->
            listOf(
                NavigationItem(NavigationRoute.Marketplace, "Market", Icons.Default.Store),
                NavigationItem(NavigationRoute.Community, "Community", Icons.Default.Group),
                NavigationItem(NavigationRoute.Profile, "Profile", Icons.Default.Person),
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

data class NavigationItem(
    val route: NavigationRoute,
    val label: String,
    val icon: ImageVector,
    val requiresVerification: Boolean = false,
)