package com.example.rooster.ui.farm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rooster.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleFarmerScreen(
    navController: NavController,
    isTeluguMode: Boolean = false,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTeluguMode) "రైతు పేజీ" else "Farmer Page",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Welcome Section
            item {
                WelcomeCard(isTeluguMode)
            }

            // Quick Actions Section
            item {
                Text(
                    text = if (isTeluguMode) "త్వరిత చర్యలు" else "Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionCard(
                        icon = Icons.Default.Add,
                        title = if (isTeluguMode) "పక్షులను జోడించండి" else "Add Birds",
                        description = if (isTeluguMode) "కొత్త పక్షులను నమోదు చేయండి" else "Register new birds to your flock",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onClick = { navController.navigate(NavigationRoute.SimpleAddBirds.route) },
                    )
                    ActionCard(
                        icon = Icons.Default.List,
                        title = if (isTeluguMode) "పక్షులను వీక్షించండి" else "View Birds",
                        description = if (isTeluguMode) "మీ పక్షుల జాబితాను చూడండి" else "See details of your current birds",
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = { navController.navigate(NavigationRoute.SimpleViewBirds.route) },
                    )
                    ActionCard(
                        icon = Icons.Default.Sell,
                        title = if (isTeluguMode) "పక్షులను అమ్మండి" else "Sell Birds",
                        description = if (isTeluguMode) "పక్షులను మార్కెట్లో అమ్మకానికి ఉంచండి" else "List birds for sale on the marketplace",
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = { navController.navigate(NavigationRoute.SimpleSellBirds.route) },
                    )
                }
            }

            // Health and Diagnostics Section
            item {
                Text(
                    text = if (isTeluguMode) "ఆరోగ్యం & విశ్లేషణలు" else "Health & Diagnostics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionCard(
                        icon = Icons.Default.HealthAndSafety,
                        title = if (isTeluguMode) "ఆరోగ్య నిర్వహణ" else "Health Management",
                        description = if (isTeluguMode) "పక్షుల ఆరోగ్య రికార్డులను ట్రాక్ చేయండి" else "Track and manage bird health records",
                        color = MaterialTheme.colorScheme.errorContainer,
                        onClick = { navController.navigate(NavigationRoute.HealthManagement.route) },
                    )
                    ActionCard(
                        icon = Icons.Default.Analytics,
                        title = if (isTeluguMode) "రోగనిర్ధారణ" else "Diagnostics",
                        description = if (isTeluguMode) "సాధారణ వ్యాధుల కోసం విశ్లేషించండి" else "Analyze for common poultry diseases",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onClick = { navController.navigate(NavigationRoute.Diagnostics.route) },
                    )
                }
            }

            // Marketplace and Community Section
            item {
                Text(
                    text = if (isTeluguMode) "మార్కెట్‌ప్లేస్ & కమ్యూనిటీ" else "Marketplace & Community",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionCard(
                        icon = Icons.Default.Store,
                        title = if (isTeluguMode) "మార్కెట్‌ప్లేస్" else "Marketplace",
                        description = if (isTeluguMode) "పక్షులను కొనండి లేదా అమ్మండి" else "Buy or sell poultry and related products",
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = { navController.navigate(NavigationRoute.Marketplace.route) },
                    )
                    ActionCard(
                        icon = Icons.Default.Group,
                        title = if (isTeluguMode) "కమ్యూనిటీ ఫోరమ్" else "Community Forum",
                        description = if (isTeluguMode) "ఇతర రైతులతో కనెక్ట్ అవ్వండి" else "Connect with other farmers and share insights",
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = { navController.navigate(NavigationRoute.Community.route) },
                    )
                }
            }

            // Help Section
            item {
                SimpleHelpCard(
                    isTeluguMode = isTeluguMode,
                    onHelpClick = { navController.navigate(NavigationRoute.SimpleHelp.route) },
                )
            }
        }
    }
}

@Composable
private fun WelcomeCard(isTeluguMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.Waving,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isTeluguMode) "స్వాగతం!" else "Welcome!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = if (isTeluguMode) "మీ పంట మరియు వ్యాపారాన్ని మెరుగుపరచండి" else "Manage your poultry and grow your business",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SimpleHelpCard(
    isTeluguMode: Boolean,
    onHelpClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = onHelpClick,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Help,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isTeluguMode) "సహాయం అవసరమా?" else "Need Help?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (isTeluguMode) "మార్గదర్శకాలు మరియు మద్దతు కోసం ఇక్కడ క్లిక్ చేయండి" else "Click here for guides and support",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
