package com.example.rooster

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rooster.NavigationRoute
import com.example.rooster.ui.components.StandardScreenLayout

/**
 * Simplified FarmerHomeScreen with working UI components
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerHomeScreen(navController: NavHostController) {
    var isTeluguMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTeluguMode) "రైతు హోమ్" else "Farmer Home",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    TextButton(
                        onClick = { isTeluguMode = !isTeluguMode },
                    ) {
                        Text(if (isTeluguMode) "EN" else "తె")
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply innerPadding from Scaffold
                .padding(16.dp), // Add standard screen padding
            verticalArrangement = Arrangement.spacedBy(16.dp) // Spacing between items
        ) {
            // Welcome Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = if (isTeluguMode) "స్వాగతం!" else "Welcome!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text =
                                if (isTeluguMode) {
                                    "మీ వ్యవసాయ కార్యకలాపాలను నిర్వహించండి"
                                } else {
                                    "Manage your farming activities"
                                },
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            // Quick Actions
            item {
                Text(
                    text = if (isTeluguMode) "త్వరిత చర్యలు" else "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ActionCard(
                        title = if (isTeluguMode) "నా కోళ్లు" else "My Fowls",
                        icon = Icons.Default.Pets,
                        modifier = Modifier.weight(1f),
                    ) { /* Navigate to fowls */ }

                    ActionCard(
                        title = if (isTeluguMode) "మార్కెట్‌ప్లేస్" else "Marketplace",
                        icon = Icons.Default.Store,
                        modifier = Modifier.weight(1f),
                    ) { navController.navigate(NavigationRoute.MARKETPLACE.route) }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ActionCard(
                        title = if (isTeluguMode) "ఆరోగ్యం" else "Health",
                        icon = Icons.Default.HealthAndSafety,
                        modifier = Modifier.weight(1f),
                    ) { /* Navigate to health */ }

                    ActionCard(
                        title = if (isTeluguMode) "కమ్యూనిటీ" else "Community",
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f),
                    ) { /* Navigate to community */ }
                }
            }

            // Analytics Quick Action
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    ActionCard(
                        title = if (isTeluguMode) "విశ్లేషణలు" else "Analytics",
                        icon = Icons.Default.Analytics,
                        modifier = Modifier.weight(1f),
                    ) { navController.navigate(NavigationRoute.FarmAnalytics.route) }
                }
            }

            // Farm Dashboard and Simple Farmer Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ActionCard(
                        title = "Farm Dashboard",
                        icon = Icons.Default.Dashboard,
                        modifier = Modifier.weight(1f),
                    ) { navController.navigate(NavigationRoute.FarmDashboard.route) }

                    ActionCard(
                        title = if (isTeluguMode) "సరళ వ్యవసాయ" else "Simple Farm",
                        icon = Icons.Default.Agriculture,
                        modifier = Modifier.weight(1f),
                    ) { navController.navigate(NavigationRoute.SimpleFarmer.route) }
                }
            }

            // My Auctions Quick Action
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ActionCard(
                        title = if (isTeluguMode) "నా వేలం" else "My Auctions",
                        icon = Icons.Default.Gavel,
                        modifier = Modifier.weight(1f),
                    ) { navController.navigate(NavigationRoute.AUCTIONS.route) }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Top Farmers Section using our component
            item {
                Text(
                    text = if (isTeluguMode) "అగ్రశ్రేణి రైతులు" else "Top Farmers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            items(getSampleFarmers()) { farmer ->
                // Removed the FarmerProfileCard component as it was causing import issues
                // You can re-add it once the import is fixed
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { /* Navigate to profile */ }
                            .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (isTeluguMode) farmer.teluguName else farmer.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f),
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

// Sample data
private data class SampleFarmer(
    val id: String,
    val name: String,
    val teluguName: String,
    val imageUrl: String,
    val experience: Int,
    val livestock: Int,
    val rating: Float,
    val isVerified: Boolean,
    val isOnline: Boolean,
    val specialties: List<String>,
)

private fun getSampleFarmers(): List<SampleFarmer> {
    return listOf(
        SampleFarmer(
            id = "farmer1",
            name = "Ramu",
            teluguName = "రాము గారు",
            imageUrl = "",
            experience = 15,
            livestock = 150,
            rating = 4.8f,
            isVerified = true,
            isOnline = true,
            specialties = listOf("Kadaknath", "Aseel"),
        ),
        SampleFarmer(
            id = "farmer2",
            name = "Lakshmi",
            teluguName = "లక్ష్మి అక్క",
            imageUrl = "",
            experience = 10,
            livestock = 80,
            rating = 4.6f,
            isVerified = true,
            isOnline = false,
            specialties = listOf("Country Chicken", "Eggs"),
        ),
    )
}
