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
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.Verified
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
                        icon = { Icon(Icons.Filled.ExitToApp, contentDescription = "Logout") },
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
                NavigationItem(NavigationRoute.HOME, "Home", Icons.Filled.Home),
                NavigationItem(NavigationRoute.FARMER_HOME, "Farm", Icons.Filled.Dashboard),
                NavigationItem(NavigationRoute.MARKETPLACE, "Market", Icons.Filled.Store),
                NavigationItem(NavigationRoute.ACTIVITY_VERIFICATION, "Verify", Icons.Filled.Verified),
                NavigationItem(NavigationRoute.COMMUNITY, "Community", Icons.Filled.Group),
                NavigationItem(NavigationRoute.PROFILE, "Profile", Icons.Filled.Person),
                NavigationItem(NavigationRoute.TRANSFERS, "Transfers", Icons.Filled.Transform),
                NavigationItem(NavigationRoute.SETTINGS, "Settings", Icons.Filled.Settings),
                NavigationItem(NavigationRoute.HELP, "Help", Icons.Filled.MedicalServices),
                NavigationItem(NavigationRoute.VET_CONSULTATION, "Vet", Icons.Filled.LocalHospital),
                NavigationItem(NavigationRoute.IOT_DASHBOARD, "IoT", Icons.Filled.DeviceHub),
                NavigationItem(NavigationRoute.AUCTIONS, "Auctions", Icons.Filled.Gavel),
                NavigationItem(NavigationRoute.HIGH_LEVEL_HOME, "Manager", Icons.Filled.AdminPanelSettings),
                NavigationItem(NavigationRoute.AUTH, "Auth", Icons.Filled.Person),
                NavigationItem(NavigationRoute.CART, "Cart", Icons.Filled.Receipt),
                NavigationItem(NavigationRoute.ORDER_HISTORY, "Orders", Icons.Filled.Receipt),
                NavigationItem(NavigationRoute.PAYMENT, "Payment", Icons.Filled.Receipt),
                NavigationItem(NavigationRoute.PROFILE_EDIT, "Edit Profile", Icons.Filled.Person),
                NavigationItem(NavigationRoute.HELP_SUPPORT, "Help & Support", Icons.Filled.MedicalServices),
                NavigationItem(NavigationRoute.COMPREHENSIVE_MESSAGING, "Messages", Icons.Filled.Receipt),
                NavigationItem(NavigationRoute.FOWL_TRACEABILITY, "Fowl", Icons.Filled.Receipt),
                NavigationItem(NavigationRoute.DIAGNOSIS_HELP, "Diagnostics", Icons.Filled.Receipt),
                NavigationItem(NavigationRoute.HEALTH_RECORDS, "Health", Icons.Filled.Receipt),
                NavigationItem(NavigationRoute.MARKETPLACE_LISTING_CREATE, "Create Listing", Icons.Filled.Add),
            )

        "farmer" ->
            listOf(
                NavigationItem(NavigationRoute.FARMER_HOME, "Farm", Icons.Filled.Home),
                NavigationItem(NavigationRoute.MARKETPLACE, "Market", Icons.Filled.Store),
                NavigationItem(NavigationRoute.TRANSFERS, "Transfers", Icons.Filled.Transform),
                NavigationItem(NavigationRoute.SETTINGS, "Settings", Icons.Filled.Settings),
                NavigationItem(NavigationRoute.HELP, "Help", Icons.Filled.MedicalServices),
                NavigationItem(NavigationRoute.VET_CONSULTATION, "Vet", Icons.Filled.LocalHospital),
                NavigationItem(NavigationRoute.IOT_DASHBOARD, "IoT", Icons.Filled.DeviceHub),
                NavigationItem(NavigationRoute.AUCTIONS, "Auctions", Icons.Filled.Gavel),
                NavigationItem(NavigationRoute.COMMUNITY, "Community", Icons.Filled.Group),
                NavigationItem(NavigationRoute.PROFILE, "Profile", Icons.Filled.Person),
            )

        "highlevel", "high_level" ->
            listOf(
                NavigationItem(NavigationRoute.HIGH_LEVEL_HOME, "Dashboard", Icons.Filled.Dashboard),
                NavigationItem(NavigationRoute.MARKETPLACE, "Market", Icons.Filled.Store),
                NavigationItem(NavigationRoute.ACTIVITY_VERIFICATION, "Verify", Icons.Filled.Verified),
                NavigationItem(NavigationRoute.COMMUNITY, "Community", Icons.Filled.Group),
                NavigationItem(NavigationRoute.PROFILE, "Profile", Icons.Filled.Person),
            )

        else ->
            listOf(
                NavigationItem(NavigationRoute.MARKETPLACE, "Market", Icons.Filled.Store),
                NavigationItem(NavigationRoute.COMMUNITY, "Community", Icons.Filled.Group),
                NavigationItem(NavigationRoute.PROFILE, "Profile", Icons.Filled.Person),
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
                Icon(Icons.Filled.Add, contentDescription = "Add Fowl")
            }
        }

        "highlevel", "high_level" -> {
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(Icons.Filled.AdminPanelSettings, contentDescription = "Admin Actions")
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
