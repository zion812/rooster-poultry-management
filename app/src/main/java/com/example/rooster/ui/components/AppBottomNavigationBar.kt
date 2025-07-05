package com.example.rooster.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.rooster.models.UserRole
import com.example.rooster.ui.theme.RoosterCustomShapes

/**
 * AppBottomNavigationBar - Role-based navigation component
 * Displays different navigation items based on user role
 * Follows the UI Architecture Plan for navigation components
 */

// Navigation item data class
data class NavigationItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val badge: String? = null,
)

/**
 * Main bottom navigation bar with role-based item display
 */
@Composable
fun AppBottomNavigationBar(
    navController: NavHostController,
    userRole: UserRole,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navigationItems = getNavigationItemsForRole(userRole)

    // Don't show navigation bar if no items or for unknown role
    if (navigationItems.isEmpty()) return

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars,
    ) {
        navigationItems.forEach { item ->
            val isSelected =
                currentDestination?.hierarchy?.any {
                    it.route == item.route
                } == true

            NavigationBarItem(
                icon = {
                    NavigationIcon(
                        icon = item.icon,
                        badge = item.badge,
                        isSelected = isSelected,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to avoid
                            // building up a large stack of destinations
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            )
        }
    }
}

/**
 * Navigation icon with optional badge
 */
@Composable
private fun NavigationIcon(
    icon: ImageVector,
    badge: String? = null,
    isSelected: Boolean = false,
) {
    if (badge != null) {
        BadgedBox(
            badge = {
                Badge(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            },
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }
    } else {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
}

/**
 * Get navigation items based on user role
 */
private fun getNavigationItemsForRole(userRole: UserRole): List<NavigationItem> {
    return when (userRole) {
        UserRole.FARMER ->
            listOf(
                NavigationItem(
                    route = "home",
                    icon = Icons.Default.Home,
                    label = "Home",
                ),
                NavigationItem(
                    route = "marketplace",
                    icon = Icons.Default.Store,
                    label = "Market",
                ),
                NavigationItem(
                    route = "community",
                    icon = Icons.Default.People,
                    label = "Community",
                ),
                NavigationItem(
                    route = "transfers",
                    icon = Icons.Default.SwapHoriz,
                    label = "Transfers",
                ),
                NavigationItem(
                    route = "profile",
                    icon = Icons.Default.Person,
                    label = "Profile",
                ),
            )

        UserRole.GENERAL ->
            listOf(
                NavigationItem(
                    route = "marketplace",
                    icon = Icons.Default.Store,
                    label = "Market",
                ),
                NavigationItem(
                    route = "explore",
                    icon = Icons.Default.Explore,
                    label = "Explore",
                ),
                NavigationItem(
                    route = "community",
                    icon = Icons.Default.People,
                    label = "Community",
                ),
                NavigationItem(
                    route = "cart",
                    icon = Icons.Default.ShoppingCart,
                    label = "Cart",
                ),
                NavigationItem(
                    route = "profile",
                    icon = Icons.Default.Person,
                    label = "Profile",
                ),
            )

        UserRole.HIGH_LEVEL ->
            listOf(
                NavigationItem(
                    route = "dashboard",
                    icon = Icons.Default.Dashboard,
                    label = "Dashboard",
                ),
                NavigationItem(
                    route = "analytics",
                    icon = Icons.Default.Analytics,
                    label = "Analytics",
                ),
                NavigationItem(
                    route = "community",
                    icon = Icons.Default.People,
                    label = "Community",
                ),
                NavigationItem(
                    route = "verification",
                    icon = Icons.Default.Verified,
                    label = "Verification",
                ),
                NavigationItem(
                    route = "profile",
                    icon = Icons.Default.Person,
                    label = "Profile",
                ),
                // Custom Admin/Vet sections
                NavigationItem(
                    route = com.example.rooster.NavigationRoute.AdminFeaturesDashboard.route, // Assuming NavigationRoute is accessible
                    icon = Icons.Default.AdminPanelSettings,
                    label = "Admin"
                ),
                NavigationItem(
                    route = com.example.rooster.NavigationRoute.FinancialsFeaturesDashboard.route,
                    icon = Icons.Default.MonetizationOn,
                    label = "Financials"
                ),
                 NavigationItem(
                    route = com.example.rooster.NavigationRoute.VeterinaryFeaturesDashboard.route,
                    icon = Icons.Default.MedicalServices, // Or LocalHospital
                    label = "Veterinary"
                ),
                NavigationItem(
                    route = com.example.rooster.NavigationRoute.ProfessionalToolsDashboard.route,
                    icon = Icons.Default.Build, // Placeholder icon
                    label = "Pro Tools"
                )
            )
        // Add a specific case for VETERINARIAN if its items differ significantly from HIGH_LEVEL
        // For now, VETERINARIAN uses HIGH_LEVEL items as per MainActivity logic.
        // If UserRole.VETERINARIAN is distinct and needs specific items different from ADMIN:
        /*
        UserRole.VETERINARIAN ->
            listOf(
                // Common items perhaps
                NavigationItem(route = "dashboard", icon = Icons.Default.Dashboard, label = "Dashboard"), // Or a vet specific home
                NavigationItem(
                    route = com.example.rooster.NavigationRoute.VeterinaryFeaturesDashboard.route,
                    icon = Icons.Default.MedicalServices,
                    label = "Veterinary"
                ),
                NavigationItem(
                    route = com.example.rooster.NavigationRoute.ProfessionalToolsDashboard.route,
                    icon = Icons.Default.Build,
                    label = "Pro Tools"
                ),
                NavigationItem(route = "profile", icon = Icons.Default.Person, label = "Profile")
            )
        */
        UserRole.UNKNOWN -> emptyList()
    }
}

/**
 * Compact bottom navigation bar for tablets or landscape mode
 */
@Composable
fun CompactBottomNavigationBar(
    navController: NavHostController,
    userRole: UserRole,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navigationItems = getNavigationItemsForRole(userRole).take(3) // Show only first 3 items

    if (navigationItems.isEmpty()) return

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        shape = RoosterCustomShapes.NavigationBarShape,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            navigationItems.forEach { item ->
                val isSelected =
                    currentDestination?.hierarchy?.any {
                        it.route == item.route
                    } == true

                NavigationBarItem(
                    icon = {
                        NavigationIcon(
                            icon = item.icon,
                            badge = item.badge,
                            isSelected = isSelected,
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors =
                        NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
            }
        }
    }
}

/**
 * Helper function to determine if bottom navigation should be shown
 */
fun shouldShowBottomNavigation(currentRoute: String?): Boolean {
    val hiddenRoutes =
        listOf(
            "auth",
            "login",
            "signup",
            "onboarding",
            "payment",
            "settings",
            "help",
        )

    return currentRoute != null &&
        !hiddenRoutes.any { hiddenRoute ->
            currentRoute.startsWith(hiddenRoute)
        }
}
