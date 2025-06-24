package com.example.rooster.ui.farm

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rooster.NavigationRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmDashboardScreen(
    navController: NavController,
    isTeluguMode: Boolean = false,
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showSideNavigation by remember { mutableStateOf(false) }
    val tabs = listOf("Dashboard", "Monitoring", "Family Tree", "Analytics")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Dashboard - High Level") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSideNavigation = !showSideNavigation }) {
                        Icon(Icons.Default.Menu, contentDescription = "Side Navigation")
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
            ) {
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) },
                        )
                    }
                }

                when (selectedTab) {
                    0 -> FarmDashboardTab(navController)
                    1 -> MonitoringTab()
                    2 -> FamilyTreeTab()
                    3 -> AnalyticsTab()
                }
            }

            // Side Navigation
            AnimatedVisibility(
                visible = showSideNavigation,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                SideNavigationPanel(
                    onDismiss = { showSideNavigation = false },
                    navController = navController,
                )
            }
        }
    }
}

@Composable
fun FarmDashboardTab(navController: NavController) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Farm Details Section with Badges
        item {
            FarmDetailsSection()
        }

        // Board Section with Listings
        item {
            BoardSection()
        }

        // Action Suggestions
        item {
            ActionSuggestionsSection()
        }

        // Vaccination Section
        item {
            VaccinationSection()
        }
    }
}

@Composable
fun FarmDetailsSection() {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Sunrise Poultry Farm",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Verified Badge
                    Badge(
                        containerColor = Color(0xFF4CAF50),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White,
                            )
                            Text("Verified", color = Color.White)
                        }
                    }
                    // Certified Badge
                    Badge(
                        containerColor = Color(0xFFFF9800),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                Icons.Default.Stars,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White,
                            )
                            Text("Certified", color = Color.White)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Est. 2018 • Organic Certified • ISO 22000",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
fun BoardSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Board - Livestock Listings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    listOf(
                        BoardItem("Listed Fowls", "248", Icons.Default.Pets, Color(0xFF2196F3)),
                        BoardItem("Listed Hens", "186", Icons.Default.Female, Color(0xFFE91E63)),
                        BoardItem("Listed Breeders", "42", Icons.Default.Star, Color(0xFFFF9800)),
                        BoardItem("Listed Chicks", "89", Icons.Default.Eco, Color(0xFF4CAF50)),
                    ),
                ) { item ->
                    BoardItemCard(item)
                }
            }
        }
    }
}

@Composable
fun BoardItemCard(item: BoardItem) {
    Card(
        modifier =
            Modifier
                .width(140.dp)
                .height(120.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = item.color.copy(alpha = 0.1f),
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                item.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = item.color,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = item.color,
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

@Composable
fun MonitoringTab() {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Growth Monitoring
        item {
            GrowthMonitoringSection()
        }

        // Health Monitoring
        item {
            HealthMonitoringSection()
        }

        // Environmental Monitoring
        item {
            EnvironmentalMonitoringSection()
        }
    }
}

@Composable
fun GrowthMonitoringSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Growth Monitoring",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                GrowthMetric("Avg Weight", "2.4 kg", "↗ 12%", Color.Green)
                GrowthMetric("Feed Conversion", "2.1:1", "↗ 8%", Color.Green)
                GrowthMetric("Mortality Rate", "1.2%", "↘ 3%", Color.Red)
            }
        }
    }
}

@Composable
fun GrowthMetric(
    label: String,
    value: String,
    trend: String,
    trendColor: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = trend,
            color = trendColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

@Composable
fun ActionSuggestionsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Action Suggestions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            val suggestions =
                listOf(
                    "Schedule vaccination for Batch A-23",
                    "Increase protein feed for Layer Section",
                    "Check water quality in Pen 4-6",
                    "Review breeding program schedule",
                )

            suggestions.forEach { suggestion ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = suggestion,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
fun VaccinationSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Vaccination Schedule",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                OutlinedButton(onClick = { }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Schedule")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            val vaccinations =
                listOf(
                    VaccinationItem("Newcastle Disease", "Batch A-23", "Due in 2 days", "pending"),
                    VaccinationItem("Infectious Bronchitis", "Batch B-15", "Completed", "completed"),
                    VaccinationItem("Fowl Pox", "Layer Section", "Due in 5 days", "pending"),
                )

            vaccinations.forEach { vaccination ->
                VaccinationCard(vaccination)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun VaccinationCard(vaccination: VaccinationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (vaccination.status == "completed") {
                        Color(0xFF4CAF50).copy(alpha = 0.1f)
                    } else {
                        Color(0xFFFF9800).copy(alpha = 0.1f)
                    },
            ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                if (vaccination.status == "completed") Icons.Default.CheckCircle else Icons.Default.Schedule,
                contentDescription = null,
                tint =
                    if (vaccination.status == "completed") {
                        Color(0xFF4CAF50)
                    } else {
                        Color(
                            0xFFFF9800,
                        )
                    },
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vaccination.vaccine,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "${vaccination.batch} • ${vaccination.schedule}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
fun HealthMonitoringSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Health Monitoring",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                HealthIndicator("Overall Health", "Excellent", Color(0xFF4CAF50))
                HealthIndicator("Disease Risk", "Low", Color(0xFF4CAF50))
                HealthIndicator("Stress Level", "Moderate", Color(0xFFFF9800))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.HealthAndSafety, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Detailed Health Report")
            }
        }
    }
}

@Composable
fun HealthIndicator(
    label: String,
    status: String,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier =
                Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = color,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun EnvironmentalMonitoringSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Environmental Monitoring",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    listOf(
                        EnvironmentalMetric("Temperature", "24°C", "Optimal", Color(0xFF4CAF50)),
                        EnvironmentalMetric("Humidity", "65%", "Good", Color(0xFF4CAF50)),
                        EnvironmentalMetric("Air Quality", "Good", "Acceptable", Color(0xFFFF9800)),
                        EnvironmentalMetric("Lighting", "12hrs", "Optimal", Color(0xFF4CAF50)),
                    ),
                ) { metric ->
                    EnvironmentalCard(metric)
                }
            }
        }
    }
}

@Composable
fun EnvironmentalCard(metric: EnvironmentalMetric) {
    Card(
        modifier =
            Modifier
                .width(100.dp)
                .height(90.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = metric.statusColor.copy(alpha = 0.1f),
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = metric.value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = metric.statusColor,
            )
            Text(
                text = metric.status,
                style = MaterialTheme.typography.bodySmall,
                color = metric.statusColor,
            )
            Text(
                text = metric.parameter,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun FamilyTreeTab() {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Search and Filter Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Lineage Tracking",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    var searchQuery by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search by Bird ID or Batch") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                    )
                }
            }
        }

        // Family Tree Visualization
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Family Tree - Batch A-23",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Generation 1 (Grandparents)
                    FamilyTreeGeneration(
                        generationTitle = "Generation 1 (Grandparents)",
                        birds =
                            listOf(
                                FamilyTreeBird("GP-001", "Male", "Rhode Island Red", "2019"),
                                FamilyTreeBird("GP-002", "Female", "Rhode Island Red", "2019"),
                            ),
                        generationColor = Color(0xFF9C27B0),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Generation 2 (Parents)
                    FamilyTreeGeneration(
                        generationTitle = "Generation 2 (Parents)",
                        birds =
                            listOf(
                                FamilyTreeBird("P-015", "Male", "Rhode Island Red", "2021"),
                                FamilyTreeBird("P-016", "Female", "Rhode Island Red", "2021"),
                                FamilyTreeBird("P-017", "Male", "Rhode Island Red", "2021"),
                            ),
                        generationColor = Color(0xFF2196F3),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Generation 3 (Current)
                    FamilyTreeGeneration(
                        generationTitle = "Generation 3 (Current Batch)",
                        birds =
                            listOf(
                                FamilyTreeBird("A23-001", "Female", "Rhode Island Red", "2023"),
                                FamilyTreeBird("A23-002", "Male", "Rhode Island Red", "2023"),
                                FamilyTreeBird("A23-003", "Female", "Rhode Island Red", "2023"),
                                FamilyTreeBird("A23-004", "Male", "Rhode Island Red", "2023"),
                                FamilyTreeBird("A23-005", "Female", "Rhode Island Red", "2023"),
                            ),
                        generationColor = Color(0xFF4CAF50),
                    )
                }
            }
        }

        // Lineage Statistics
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Lineage Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            listOf(
                                LineageStatItem(
                                    "Total Generations",
                                    "3",
                                    Icons.Default.Timeline,
                                    Color(0xFF9C27B0),
                                ),
                                LineageStatItem(
                                    "Active Birds",
                                    "48",
                                    Icons.Default.Pets,
                                    Color(0xFF4CAF50),
                                ),
                                LineageStatItem(
                                    "Breeding Pairs",
                                    "12",
                                    Icons.Default.Favorite,
                                    Color(0xFFE91E63),
                                ),
                                LineageStatItem(
                                    "Success Rate",
                                    "94%",
                                    Icons.Default.TrendingUp,
                                    Color(0xFF2196F3),
                                ),
                            ),
                        ) { stat ->
                            LineageStatCard(stat)
                        }
                    }
                }
            }
        }

        // Quick Actions
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = { /* Export family tree */ },
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export")
                        }

                        Button(
                            onClick = { /* Add new breeding pair */ },
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Pair")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FamilyTreeGeneration(
    generationTitle: String,
    birds: List<FamilyTreeBird>,
    generationColor: Color,
) {
    Column {
        Text(
            text = generationTitle,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = generationColor,
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(birds) { bird ->
                FamilyTreeBirdCard(bird, generationColor)
            }
        }
    }
}

@Composable
fun FamilyTreeBirdCard(
    bird: FamilyTreeBird,
    color: Color,
) {
    Card(
        modifier =
            Modifier
                .width(120.dp)
                .height(100.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.1f),
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                if (bird.gender == "Male") Icons.Default.Male else Icons.Default.Female,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = bird.id,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = bird.breed,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
            )
            Text(
                text = bird.year,
                style = MaterialTheme.typography.bodySmall,
                color = color,
            )
        }
    }
}

@Composable
fun LineageStatCard(stat: LineageStatItem) {
    Card(
        modifier =
            Modifier
                .width(110.dp)
                .height(80.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = stat.color.copy(alpha = 0.1f),
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                stat.icon,
                contentDescription = null,
                tint = stat.color,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = stat.color,
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

@Composable
fun AnalyticsTab() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Default.Analytics,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Advanced Analytics",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Performance insights and reporting",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
    }
}

@Composable
fun SideNavigationPanel(
    onDismiss: () -> Unit,
    navController: NavController,
) {
    Card(
        modifier =
            Modifier
                .width(280.dp)
                .fillMaxHeight()
                .padding(end = 0.dp),
        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // New Section
            SideNavSection(
                title = "New",
                items =
                    listOf(
                        SideNavItem("New Batch", Icons.Default.Add) {
                            navController.navigate(NavigationRoute.FarmNewBatch.route)
                            onDismiss()
                        },
                        SideNavItem("New Bird", Icons.Default.Egg) {
                            navController.navigate(NavigationRoute.FarmNewBird.route)
                            onDismiss()
                        },
                        SideNavItem("New Eggs", Icons.Default.EggAlt) {
                            navController.navigate(NavigationRoute.FarmNewEggs.route)
                            onDismiss()
                        },
                        SideNavItem("New Breeding", Icons.Default.Favorite) {
                            navController.navigate(NavigationRoute.FarmNewBreeding.route)
                            onDismiss()
                        },
                        SideNavItem("New Chicks", Icons.Default.Pets) {
                            navController.navigate(NavigationRoute.FarmNewChicks.route)
                            onDismiss()
                        },
                        SideNavItem("New Flock", Icons.Default.BugReport) {
                            navController.navigate(NavigationRoute.FarmNewFowl.route)
                            onDismiss()
                        },
                        SideNavItem("New Incubation", Icons.Default.Thermostat) {
                            navController.navigate(NavigationRoute.FarmNewIncubation.route)
                            onDismiss()
                        },
                    ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mortality Section
            SideNavSection(
                title = "Mortality",
                items =
                    listOf(
                        SideNavItem("Report Mortality", Icons.Default.Warning) {
                            navController.navigate(NavigationRoute.FarmReportMortality.route)
                            onDismiss()
                        },
                        SideNavItem("Mortality Records", Icons.Default.List) {
                            navController.navigate(NavigationRoute.FarmMortalityRecords.route)
                            onDismiss()
                        },
                    ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Updates Section
            SideNavSection(
                title = "Updates",
                items =
                    listOf(
                        SideNavItem("Update Chicks", Icons.Default.Update) {
                            navController.navigate(NavigationRoute.FarmUpdateChicks.route)
                            onDismiss()
                        },
                        SideNavItem("Update Adults", Icons.Default.Update) {
                            navController.navigate(NavigationRoute.FarmUpdateAdults.route)
                            onDismiss()
                        },
                        SideNavItem("Update Breeding", Icons.Default.Update) {
                            navController.navigate(NavigationRoute.FarmUpdateBreeding.route)
                            onDismiss()
                        },
                        SideNavItem("Update Incubation", Icons.Default.Update) {
                            navController.navigate(NavigationRoute.FarmUpdateIncubation.route)
                            onDismiss()
                        },
                        SideNavItem("Update Breeders", Icons.Default.Update) {
                            navController.navigate(NavigationRoute.FarmUpdateBreeders.route)
                            onDismiss()
                        },
                        SideNavItem("Update Eggs", Icons.Default.Update) {
                            navController.navigate(NavigationRoute.FarmUpdateEggs.route)
                            onDismiss()
                        },
                    ),
            )
        }
    }
}

@Composable
fun SideNavSection(
    title: String,
    items: List<SideNavItem>,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        items.forEach { item ->
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                onClick = { item.onClick() },
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

// Data Models for Family Tree
data class FamilyTreeBird(
    val id: String,
    val gender: String,
    val breed: String,
    val year: String,
)

data class LineageStatItem(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color,
)

// Data Models
data class BoardItem(
    val title: String,
    val count: String,
    val icon: ImageVector,
    val color: Color,
)

data class VaccinationItem(
    val vaccine: String,
    val batch: String,
    val schedule: String,
    val status: String,
)

data class EnvironmentalMetric(
    val parameter: String,
    val value: String,
    val status: String,
    val statusColor: Color,
)

data class SideNavItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {},
)

data class FlockRegistryItem(
    val id: String,
    val name: String,
    val type: String,
    val ageGroup: String,
    val status: String,
)
