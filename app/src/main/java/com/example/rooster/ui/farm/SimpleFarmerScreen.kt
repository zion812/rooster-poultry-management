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

            // Main Actions - Big, Simple Cards
            item {
                Text(
                    text = if (isTeluguMode) "మీ పని" else "Your Work",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Add New Birds
                    SimpleBigActionCard(
                        title = if (isTeluguMode) "కొత్త కోళ్లు చేర్చండి" else "Add New Birds",
                        description = if (isTeluguMode) "మీ కొత్త కోళ్లను నమోదు చేయండి" else "Register your new chickens",
                        icon = Icons.Default.Add,
                        color = Color(0xFF4CAF50),
                        onClick = { navController.navigate("simple_add_birds") },
                    )

                    // View My Birds
                    SimpleBigActionCard(
                        title = if (isTeluguMode) "నా కోళ్లు చూడండి" else "View My Birds",
                        description = if (isTeluguMode) "మీ కోళ్ల జాబితా చూడండి" else "See list of your chickens",
                        icon = Icons.Default.Pets,
                        color = Color(0xFF2196F3),
                        onClick = { navController.navigate("simple_view_birds") },
                    )

                    // Sell Birds
                    SimpleBigActionCard(
                        title = if (isTeluguMode) "కోళ్లు అమ్మండი" else "Sell Birds",
                        description = if (isTeluguMode) "మీ కోళ్లను అమ్మకానికి పెట్టండి" else "Put your birds for sale",
                        icon = Icons.Default.Sell,
                        color = Color(0xFFFF9800),
                        onClick = { navController.navigate("simple_sell_birds") },
                    )
                }
            }

            // Quick Info Section
            item {
                Text(
                    text = if (isTeluguMode) "త్వరిత సమాచారం" else "Quick Info",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SimpleInfoCard(
                        title = if (isTeluguMode) "మొత్తం కోళ్లు" else "Total Birds",
                        value = "25",
                        modifier = Modifier.weight(1f),
                    )
                    SimpleInfoCard(
                        title = if (isTeluguMode) "అమ్మకానికి" else "For Sale",
                        value = "8",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Help Section
            item {
                SimpleHelpCard(
                    isTeluguMode = isTeluguMode,
                    onHelpClick = { navController.navigate("simple_help") },
                )
            }
        }
    }
}

@Composable
fun WelcomeCard(isTeluguMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Agriculture,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (isTeluguMode) "నమస్కారం రైతుగారు!" else "Hello Farmer!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = if (isTeluguMode) "మీ కోళ్ల వ్యాపారాన్ని నిర్వహించండి" else "Manage your poultry business",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
fun SimpleBigActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(100.dp),
        onClick = onClick,
        colors =
            CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.1f),
            ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = color),
                shape = RoundedCornerShape(12.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.White,
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
fun SimpleInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

@Composable
fun SimpleHelpCard(
    isTeluguMode: Boolean,
    onHelpClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onHelpClick,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Help,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.error,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isTeluguMode) "సహాయం కావాలా?" else "Need Help?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                )
                Text(
                    text = if (isTeluguMode) "మేము మీకు సహాయం చేస్తాము" else "We will help you",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}
