package com.example.rooster.feature.farm.ui.navigation

// imports

// Alias icon imports

// Extension icon mappings moved to separate file, remove any mid-file import lines

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

// Add computed extension properties for missing fields on FarmDetails
private val com.example.rooster.feature.farm.domain.model.FarmDetails.healthScore: Double
    get() = 100.0 - (mortalityRate)

private val com.example.rooster.feature.farm.domain.model.FarmDetails.productivityScore: Double
    get() = eggProductionRate ?: 0.0

private val com.example.rooster.feature.farm.domain.model.FarmDetails.establishedYear: Int
    get() = java.util.Calendar.getInstance().apply { time = establishedDate }.get(java.util.Calendar.YEAR)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmMainScreen(
    startFarmId: String,
    viewModel: FarmMainViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val farmState by viewModel.farmState.collectAsState()
    val flockStats by viewModel.flockStats.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(startFarmId) {
        viewModel.loadFarmDetails(startFarmId)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ExtraordinaryFarmNavigationDrawer(
                startFarmId = startFarmId,
                farmDetails = farmState.farmDetails,
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    navController.navigate(route)
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                ExtraordinaryFarmTopAppBar(
                    farmName = farmState.farmDetails?.name ?: "Elite Farm Management",
                    isVerified = farmState.farmDetails?.verified ?: false,
                    isCertified = farmState.farmDetails?.certified ?: false,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onNotificationClick = { /* Handle notifications */ },
                    onBackClick = onBack
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                ExtraordinaryFloatingActionButton(
                    startFarmId = startFarmId,
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Elite Farm Details Section with Advanced Badges
                    item {
                        ExtraordinaryFarmDetailsCard(
                            farmDetails = farmState.farmDetails,
                            badges = farmState.badges,
                            isLoading = farmState.isLoading
                        )
                    }

                    // Advanced Board Section - Comprehensive Livestock Stats
                    item {
                        ExtraordinaryBoardStatsCard(
                            stats = flockStats,
                            onViewAll = { type ->
                                navController.navigate("farm_board/$startFarmId?filter=$type")
                            }
                        )
                    }

                    // Revolutionary Monitoring Dashboard
                    item {
                        ExtraordinaryMonitoringCard(
                            onMonitoringClick = { navController.navigate("farm_monitoring/$startFarmId") },
                            onGrowthClick = { navController.navigate("farm_growth/$startFarmId") },
                            onSuggestionsClick = { navController.navigate("farm_suggestions/$startFarmId") },
                            onAIInsightsClick = { navController.navigate("farm_ai_insights/$startFarmId") }
                        )
                    }

                    // Elite Health & Vaccination Management
                    item {
                        ExtraordinaryHealthVaccinationCard(
                            vaccinationCompliance = farmState.farmDetails?.vaccinationCompliance ?: 0.0,
                            healthScore = farmState.farmDetails?.healthScore ?: 0.0,
                            onVaccinationClick = { navController.navigate("farm_vaccination/$startFarmId") },
                            onFamilyTreeClick = { navController.navigate("farm_familytree/$startFarmId") },
                            onHealthAnalyticsClick = { navController.navigate("farm_health_analytics/$startFarmId") }
                        )
                    }

                    // Extraordinary Quick Actions Hub
                    item {
                        ExtraordinaryQuickActionsCard(
                            startFarmId = startFarmId,
                            onNavigate = { route -> navController.navigate(route) }
                        )
                    }

                    // Revolutionary Flock Registry Management
                    item {
                        ExtraordinaryFlockRegistryCard(
                            startFarmId = startFarmId,
                            onNavigate = { route -> navController.navigate(route) }
                        )
                    }

                    // Advanced Analytics & Insights
                    item {
                        ExtraordinaryAnalyticsCard(
                            startFarmId = startFarmId,
                            onNavigate = { route -> navController.navigate(route) }
                        )
                    }
                }

                // Loading Overlay
                if (isRefreshing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.padding(32.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    strokeWidth = 4.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Loading farm data...",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExtraordinaryFarmDetailsCard(
    farmDetails: com.example.rooster.feature.farm.domain.model.FarmDetails?,
    badges: List<com.example.rooster.feature.farm.domain.model.FarmBadge>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Box {
            // Gradient Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = farmDetails?.name ?: "Elite Farm",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = farmDetails?.location ?: "Premium Location",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Est. ${farmDetails?.establishedYear ?: "2024"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Elite Verification & Certification Badges
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (farmDetails?.verified == true) {
                            ExtraordinaryVerificationBadge(
                                text = "VERIFIED",
                                color = Color(0xFF00C853),
                                icon = Icons.Filled.CheckCircle
                            )
                        }
                        if (farmDetails?.certified == true) {
                            ExtraordinaryVerificationBadge(
                                text = "CERTIFIED",
                                color = Color(0xFF2196F3),
                                icon = Icons.Filled.Star
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Elite Achievement Badges
                if (badges.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(badges.take(6)) { badge ->
                            ExtraordinaryBadgeChip(badge = badge)
                        }
                        if (badges.size > 6) {
                            item {
                                AssistChip(
                                    onClick = { },
                                    label = {
                                        Text(
                                            text = "+${badges.size - 6} more",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Farm Stats Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FarmStatIndicator(
                        label = "Health Score",
                        value = "${farmDetails?.healthScore?.toInt() ?: 0}%",
                        color = if ((farmDetails?.healthScore ?: 0.0) >= 90) Color(0xFF4CAF50) else Color(0xFFFF9800)
                    )
                    FarmStatIndicator(
                        label = "Productivity",
                        value = "${farmDetails?.productivityScore?.toInt() ?: 0}%",
                        color = MaterialTheme.colorScheme.primary
                    )
                    FarmStatIndicator(
                        label = "Compliance",
                        value = "${farmDetails?.vaccinationCompliance?.toInt() ?: 0}%",
                        color = Color(0xFF9C27B0)
                    )
                }
            }
        }
    }
}

@Composable
private fun ExtraordinaryVerificationBadge(
    text: String,
    color: Color,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ExtraordinaryBadgeChip(
    badge: com.example.rooster.feature.farm.domain.model.FarmBadge
) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = badge.type.name,
                style = MaterialTheme.typography.labelSmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = when (badge.type) {
                    com.example.rooster.feature.farm.domain.model.BadgeType.VERIFIED -> Icons.Filled.CheckCircle
                    com.example.rooster.feature.farm.domain.model.BadgeType.CERTIFIED -> Icons.Filled.Star
                    com.example.rooster.feature.farm.domain.model.BadgeType.ORGANIC -> Icons.Filled.Home
                    com.example.rooster.feature.farm.domain.model.BadgeType.BIOSECURE -> Icons.Filled.Lock
                    com.example.rooster.feature.farm.domain.model.BadgeType.ANIMAL_WELFARE -> Icons.Filled.Favorite
                    com.example.rooster.feature.farm.domain.model.BadgeType.TRACEABILITY -> Icons.Filled.Info
                    com.example.rooster.feature.farm.domain.model.BadgeType.PRODUCTIVITY -> Icons.Filled.Settings
                    com.example.rooster.feature.farm.domain.model.BadgeType.SUSTAINABILITY -> Icons.Filled.Home
                },
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    )
}

@Composable
private fun FarmStatIndicator(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ExtraordinaryBoardStatsCard(
    stats: FlockStats?,
    onViewAll: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Board - Livestock Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Fowls",
                    count = stats?.totalFowls ?: 0,
                    icon = Icons.Default.Home,
                    onClick = { onViewAll("fowls") }
                )
                StatItem(
                    label = "Hens",
                    count = stats?.totalHens ?: 0,
                    icon = Icons.Default.Home,
                    onClick = { onViewAll("hens") }
                )
                StatItem(
                    label = "Breeders",
                    count = stats?.totalBreeders ?: 0,
                    icon = Icons.Default.Home,
                    onClick = { onViewAll("breeders") }
                )
                StatItem(
                    label = "Chicks",
                    count = stats?.totalChicks ?: 0,
                    icon = Icons.Default.Home,
                    onClick = { onViewAll("chicks") }
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    count: Int,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ExtraordinaryFarmNavigationDrawer(
    startFarmId: String,
    farmDetails: com.example.rooster.feature.farm.domain.model.FarmDetails?,
    onNavigate: (String) -> Unit
) {
    ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Farm Management",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            val navigationItems = listOf(
                "Details" to Icons.Filled.Info,
                "Board" to Icons.Filled.Settings,
                "Monitoring" to Icons.Filled.Search,
                "Growth" to Icons.Filled.Settings,
                "Vaccination" to Icons.Filled.Add,
                "Family Tree" to Icons.Filled.Share,
                "Registry" to Icons.Filled.List,
                "Mortality" to Icons.Filled.Warning,
                "Updates" to Icons.Filled.Refresh
            )

            navigationItems.forEach { (label, icon) ->
                NavigationDrawerItem(
                    label = { Text(label) },
                    selected = false,
                    onClick = {
                        val route = when (label) {
                            "Details" -> "farm_details/$startFarmId"
                            "Board" -> "farm_board/$startFarmId"
                            "Monitoring" -> "farm_monitoring/$startFarmId"
                            "Growth" -> "farm_growth/$startFarmId"
                            "Vaccination" -> "farm_vaccination/$startFarmId"
                            "Family Tree" -> "farm_familytree/$startFarmId"
                            "Registry" -> "farm_registry/$startFarmId"
                            "Mortality" -> "farm_mortality/$startFarmId"
                            "Updates" -> "farm_updates/$startFarmId"
                            else -> "farm_details/$startFarmId"
                        }
                        onNavigate(route)
                    },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ExtraordinaryFarmTopAppBar(
    farmName: String,
    isVerified: Boolean,
    isCertified: Boolean,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = farmName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications"
                )
            }
        }
    )
}

@Composable
private fun ExtraordinaryFloatingActionButton(
    startFarmId: String,
    onNavigate: (String) -> Unit
) {
    FloatingActionButton(
        onClick = { onNavigate("farm_new_batch/$startFarmId") },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "New Batch"
        )
    }
}

@Composable
private fun ExtraordinaryMonitoringCard(
    onMonitoringClick: () -> Unit,
    onGrowthClick: () -> Unit,
    onSuggestionsClick: () -> Unit,
    onAIInsightsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Monitoring & Growth",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    text = "Monitoring",
                    icon = Icons.Filled.Search,
                    onClick = onMonitoringClick
                )
                ActionButton(
                    text = "Growth",
                    icon = Icons.Filled.Settings,
                    onClick = onGrowthClick
                )
                ActionButton(
                    text = "Suggestions",
                    icon = Icons.Filled.Info,
                    onClick = onSuggestionsClick
                )
                ActionButton(
                    text = "AI Insights",
                    icon = Icons.Filled.Settings,
                    onClick = onAIInsightsClick
                )
            }
        }
    }
}

@Composable
private fun ExtraordinaryHealthVaccinationCard(
    vaccinationCompliance: Double,
    healthScore: Double,
    onVaccinationClick: () -> Unit,
    onFamilyTreeClick: () -> Unit,
    onHealthAnalyticsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Health & Vaccination",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${vaccinationCompliance.toInt()}% Compliance",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (vaccinationCompliance >= 90) Color(0xFF4CAF50) else Color(0xFFFF9800)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { (vaccinationCompliance / 100).toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    text = "Vaccination",
                    icon = Icons.Filled.Add,
                    onClick = onVaccinationClick
                )
                ActionButton(
                    text = "Family Tree",
                    icon = Icons.Filled.Share,
                    onClick = onFamilyTreeClick
                )
                ActionButton(
                    text = "Analytics",
                    icon = Icons.Filled.Info,
                    onClick = onHealthAnalyticsClick
                )
            }
        }
    }
}

@Composable
private fun ExtraordinaryQuickActionsCard(
    startFarmId: String,
    onNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // New Section Actions
            Text(
                text = "New Entries",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    text = "Batch",
                    icon = Icons.Filled.Add,
                    onClick = { onNavigate("farm_new_batch/$startFarmId") }
                )
                ActionButton(
                    text = "Bird",
                    icon = Icons.Filled.Home,
                    onClick = { onNavigate("farm_new_bird/$startFarmId") }
                )
                ActionButton(
                    text = "Eggs",
                    icon = Icons.Filled.Settings,
                    onClick = { onNavigate("farm_new_eggs/$startFarmId") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    text = "Breeding",
                    icon = Icons.Filled.Home,
                    onClick = { onNavigate("farm_new_breeding/$startFarmId") }
                )
                ActionButton(
                    text = "Chicks",
                    icon = Icons.Filled.Home,
                    onClick = { onNavigate("farm_new_chicks/$startFarmId") }
                )
                ActionButton(
                    text = "Incubation",
                    icon = Icons.Filled.Home,
                    onClick = { onNavigate("farm_new_incubation/$startFarmId") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Side Navigation Actions
            Text(
                text = "Management",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    text = "Registry",
                    icon = Icons.Filled.List,
                    onClick = { onNavigate("farm_registry/$startFarmId") }
                )
                ActionButton(
                    text = "New Listing",
                    icon = Icons.Filled.Add,
                    onClick = { onNavigate("farm_new_listing/$startFarmId") }
                )
                ActionButton(
                    text = "Mortality",
                    icon = Icons.Filled.Warning,
                    onClick = { onNavigate("farm_mortality/$startFarmId") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    text = "Updates",
                    icon = Icons.Filled.Refresh,
                    onClick = { onNavigate("farm_updates/$startFarmId") }
                )
                // Add empty buttons for spacing
                ActionButton(
                    text = "",
                    icon = Icons.Filled.Home,
                    onClick = { }
                )
                ActionButton(
                    text = "",
                    icon = Icons.Filled.Home,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun ExtraordinaryFlockRegistryCard(
    startFarmId: String,
    onNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Flock Registry",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    text = "Add Flock",
                    icon = Icons.Filled.Add,
                    onClick = { onNavigate("farm_add_flock_registry/$startFarmId") }
                )
                ActionButton(
                    text = "View All",
                    icon = Icons.Filled.List,
                    onClick = { onNavigate("farm_flock_registry/$startFarmId") }
                )
                ActionButton(
                    text = "Analytics",
                    icon = Icons.Filled.Info,
                    onClick = { onNavigate("farm_flock_analytics/$startFarmId") }
                )
            }
        }
    }
}

@Composable
private fun ExtraordinaryAnalyticsCard(
    startFarmId: String,
    onNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Analytics & Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    text = "Performance",
                    icon = Icons.Filled.Info,
                    onClick = { onNavigate("farm_performance_analytics/$startFarmId") }
                )
                ActionButton(
                    text = "Health",
                    icon = Icons.Filled.Info,
                    onClick = { onNavigate("farm_health_analytics/$startFarmId") }
                )
                ActionButton(
                    text = "Vaccination",
                    icon = Icons.Filled.Info,
                    onClick = { onNavigate("farm_vaccination_analytics/$startFarmId") }
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
