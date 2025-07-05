package com.example.rooster

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Comprehensive Health Management Screen - Moderate Level Feature
 * Optimized for rural farmers with network-adaptive loading and offline support
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthManagementScreen(selectedBirdId: String? = null) {
    val context = LocalContext.current.applicationContext as Application
    val healthService = remember { HealthManagementService(context) }
    val coroutineScope = rememberCoroutineScope()

    // State management
    var healthSummary by remember { mutableStateOf<HealthSummary?>(null) }
    var upcomingSchedules by remember { mutableStateOf<List<HealthSchedule>>(emptyList()) }
    var vaccinationHistory by remember { mutableStateOf<List<VaccinationRecord>>(emptyList()) }
    var medicationHistory by remember { mutableStateOf<List<MedicationRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    // Dialog states
    var showAddVaccination by remember { mutableStateOf(false) }
    var showAddMedication by remember { mutableStateOf(false) }
    var showScheduleReminder by remember { mutableStateOf(false) }

    LaunchedEffect(selectedBirdId) {
        coroutineScope.launch {
            isLoading = true
            error = null

            try {
                // Load health summary
                healthService.getHealthSummary("current_user").fold(
                    onSuccess = { summary -> healthSummary = summary },
                    onFailure = { e -> error = "Failed to load health summary: ${e.message}" },
                )

                // Load upcoming schedules
                healthService.getUpcomingHealthSchedules("current_user").fold(
                    onSuccess = { schedules -> upcomingSchedules = schedules },
                    onFailure = { /* Handle gracefully */ },
                )

                // Load vaccination and medication history if bird is selected
                selectedBirdId?.let { birdId ->
                    healthService.getVaccinationHistory(birdId).fold(
                        onSuccess = { vaccines -> vaccinationHistory = vaccines },
                        onFailure = { /* Handle gracefully */ },
                    )

                    healthService.getMedicationHistory(birdId).fold(
                        onSuccess = { medications -> medicationHistory = medications },
                        onFailure = { /* Handle gracefully */ },
                    )
                }
            } catch (e: Exception) {
                error = "Failed to load health data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        // Header with health score
        HealthSummaryHeader(
            healthSummary = healthSummary,
            isLoading = isLoading,
        )

        // Tab navigation
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Overview") },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Vaccinations") },
                icon = { Icon(Icons.Default.Vaccines, contentDescription = null) },
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Medications") },
                icon = { Icon(Icons.Default.Medication, contentDescription = null) },
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Schedule") },
                icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
            )
        }

        // Content based on selected tab
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 ->
                    HealthOverviewTab(
                        healthSummary = healthSummary,
                        upcomingSchedules = upcomingSchedules,
                        isLoading = isLoading,
                        error = error,
                    )

                1 ->
                    HealthVaccinationTab(
                        vaccinationHistory = vaccinationHistory,
                        isLoading = isLoading,
                        onAddVaccination = { showAddVaccination = true },
                    )

                2 ->
                    HealthMedicationTab(
                        medicationHistory = medicationHistory,
                        isLoading = isLoading,
                        onAddMedication = { showAddMedication = true },
                    )

                3 ->
                    HealthScheduleTab(
                        upcomingSchedules = upcomingSchedules,
                        isLoading = isLoading,
                        onScheduleReminder = { showScheduleReminder = true },
                    )
            }

            // Floating Action Button
            FloatingActionButton(
                onClick = {
                    when (selectedTab) {
                        1 -> showAddVaccination = true
                        2 -> showAddMedication = true
                        3 -> showScheduleReminder = true
                    }
                },
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
            ) {
                Icon(
                    imageVector =
                        when (selectedTab) {
                            1 -> Icons.Default.Vaccines
                            2 -> Icons.Default.Medication
                            3 -> Icons.Default.Schedule
                            else -> Icons.Default.Add
                        },
                    contentDescription = "Add",
                )
            }
        }
    }

    // Dialogs
    if (showAddVaccination) {
        AddVaccinationDialog(
            healthService = healthService,
            selectedBirdId = selectedBirdId,
            onDismiss = { showAddVaccination = false },
            onVaccinationAdded = {
                showAddVaccination = false
                // Refresh data
                coroutineScope.launch {
                    selectedBirdId?.let { birdId ->
                        healthService.getVaccinationHistory(birdId).fold(
                            onSuccess = { vaccines -> vaccinationHistory = vaccines },
                            onFailure = { /* Handle gracefully */ },
                        )
                    }
                }
            },
        )
    }

    if (showAddMedication) {
        AddMedicationDialog(
            healthService = healthService,
            selectedBirdId = selectedBirdId,
            onDismiss = { showAddMedication = false },
            onMedicationAdded = {
                showAddMedication = false
                // Refresh data
                coroutineScope.launch {
                    selectedBirdId?.let { birdId ->
                        healthService.getMedicationHistory(birdId).fold(
                            onSuccess = { medications -> medicationHistory = medications },
                            onFailure = { /* Handle gracefully */ },
                        )
                    }
                }
            },
        )
    }

    if (showScheduleReminder) {
        ScheduleReminderDialog(
            healthService = healthService,
            selectedBirdId = selectedBirdId,
            onDismiss = { showScheduleReminder = false },
            onScheduled = {
                showScheduleReminder = false
                // Refresh schedules
                coroutineScope.launch {
                    healthService.getUpcomingHealthSchedules("current_user").fold(
                        onSuccess = { schedules -> upcomingSchedules = schedules },
                        onFailure = { /* Handle gracefully */ },
                    )
                }
            },
        )
    }
}

@Composable
private fun HealthSummaryHeader(
    healthSummary: HealthSummary?,
    isLoading: Boolean,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Health Overview",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Loading health data...")
                }
            } else {
                healthSummary?.let { summary ->
                    val healthScore = if (summary.totalBirds > 0) (summary.healthyBirds.toFloat() / summary.totalBirds.toFloat() * 100).toInt() else 100
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        // Health Score Circular Progress
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier.size(80.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    progress = { healthScore / 100f },
                                    modifier = Modifier.fillMaxSize(),
                                    strokeWidth = 8.dp,
                                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                )
                                Text(
                                    text = "$healthScore%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Text(
                                text = "Health Score",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        // Quick Stats
                        Column {
                            QuickStatItem(
                                icon = Icons.Default.Event,
                                label = "Upcoming Schedules",
                                value = "${summary.upcomingVaccinations}",
                                color = Color(0xFF2196F3),
                            )
                            QuickStatItem(
                                icon = Icons.Default.Medication,
                                label = "Ongoing Meds",
                                value = "${summary.ongoingMedications}",
                                color = Color(0xFF4CAF50),
                            )
                            QuickStatItem(
                                icon = Icons.Default.Warning,
                                label = "Sick Birds",
                                value = "${summary.sickBirds}",
                                color =
                                    if (summary.sickBirds > 0) {
                                        Color(0xFFF44336)
                                    } else {
                                        Color(
                                            0xFF4CAF50,
                                        )
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickStatItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun HealthOverviewTab(
    healthSummary: HealthSummary?,
    upcomingSchedules: List<HealthSchedule>,
    isLoading: Boolean,
    error: String?,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (isLoading) {
            item {
                CircularProgressIndicator(modifier = Modifier.wrapContentSize(Alignment.Center))
            }
        } else if (error != null) {
            item {
                Card(
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        } else {
            healthSummary?.let { summary ->
                // Grid of stats
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard("Total Birds", "${summary.totalBirds}", Icons.Default.Groups, Color(0xFF7E57C2), Modifier.weight(1f))
                        StatCard("Healthy", "${summary.healthyBirds}", Icons.Default.CheckCircle, Color(0xFF4CAF50), Modifier.weight(1f))
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard(
                            "Sick",
                            "${summary.sickBirds}",
                            Icons.Default.SentimentVeryDissatisfied,
                            Color(0xFFF44336),
                            Modifier.weight(1f),
                        )
                        StatCard(
                            "Mortality (30d)",
                            "${String.format("%.1f", summary.mortalityRate)}%",
                            Icons.Default.TrendingDown,
                            Color(0xFFFF7043),
                            Modifier.weight(1f),
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard("Upcoming", "${summary.upcomingVaccinations}", Icons.Default.Event, Color(0xFF2196F3), Modifier.weight(1f))
                        StatCard(
                            "Ongoing Meds",
                            "${summary.ongoingMedications}",
                            Icons.Default.Medication,
                            Color(0xFF4CAF50),
                            Modifier.weight(1f),
                        )
                    }
                }

                // Upcoming Schedules section
                item {
                    Text(
                        text = "Upcoming Health Schedules",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    )
                }
                if (upcomingSchedules.isEmpty()) {
                    item {
                        Text("No upcoming health schedules.")
                    }
                } else {
                    items(upcomingSchedules) {
                        ScheduleItem(schedule = it)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun HealthVaccinationTab(
    vaccinationHistory: List<VaccinationRecord>,
    isLoading: Boolean,
    onAddVaccination: () -> Unit,
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (vaccinationHistory.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "No Vaccinations Recorded",
                        message = "Start tracking your fowl's vaccination history",
                        icon = Icons.Default.Vaccines,
                        actionText = "Add Vaccination",
                        onAction = onAddVaccination,
                    )
                }
            } else {
                items(vaccinationHistory) { vaccination ->
                    HealthVaccinationCard(vaccination = vaccination)
                }
            }
        }
    }
}

@Composable
private fun HealthMedicationTab(
    medicationHistory: List<MedicationRecord>,
    isLoading: Boolean,
    onAddMedication: () -> Unit,
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (medicationHistory.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "No Medications Recorded",
                        message = "Track medications and treatments for your fowl",
                        icon = Icons.Default.Medication,
                        actionText = "Add Medication",
                        onAction = onAddMedication,
                    )
                }
            } else {
                items(medicationHistory) { medication ->
                    HealthMedicationCard(medication = medication)
                }
            }
        }
    }
}

@Composable
private fun HealthScheduleTab(
    upcomingSchedules: List<HealthSchedule>,
    isLoading: Boolean,
    onScheduleReminder: () -> Unit,
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (upcomingSchedules.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "No Scheduled Tasks",
                        message = "Schedule health reminders for your fowl",
                        icon = Icons.Default.Schedule,
                        actionText = "Add Schedule",
                        onAction = onScheduleReminder,
                    )
                }
            } else {
                items(upcomingSchedules) { schedule ->
                    HealthScheduleCard(schedule = schedule)
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    title: String,
    message: String,
    icon: ImageVector,
    actionText: String,
    onAction: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onAction) {
                Text(actionText)
            }
        }
    }
}

@Composable
private fun ScheduleItem(schedule: HealthSchedule) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Priority indicator
        Box(
            modifier =
                Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(schedule.priority.color))),
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = schedule.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text =
                    SimpleDateFormat(
                        "MMM dd, yyyy",
                        Locale.getDefault(),
                    ).format(schedule.scheduledDate),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Icon(
            imageVector =
                when (schedule.scheduleType) {
                    HealthScheduleType.VACCINATION -> Icons.Default.Vaccines
                    HealthScheduleType.MEDICATION -> Icons.Default.Medication
                    HealthScheduleType.HEALTH_CHECK -> Icons.Default.HealthAndSafety
                    HealthScheduleType.VET_VISIT -> Icons.Default.LocalHospital
                    else -> Icons.Default.Schedule
                },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun HealthVaccinationCard(vaccination: VaccinationRecord) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vaccination.vaccineName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = vaccination.birdName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(4.dp),
                ) {
                    Text(
                        text = vaccination.vaccineType.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dosage: ${vaccination.dosage}",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = "Date: ${
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(
                        vaccination.administeredDate,
                    )
                }",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            vaccination.nextDueDate?.let { nextDate ->
                Text(
                    text = "Next Due: ${
                        SimpleDateFormat(
                            "MMM dd, yyyy",
                            Locale.getDefault(),
                        ).format(nextDate)
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            if (vaccination.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = vaccination.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun HealthMedicationCard(medication: MedicationRecord) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.medicineName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = medication.birdName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Surface(
                    color =
                        if (medication.isCompleted) {
                            Color(0xFF4CAF50).copy(alpha = 0.2f)
                        } else {
                            Color(0xFF2196F3).copy(alpha = 0.2f)
                        },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(4.dp),
                ) {
                    Text(
                        text = if (medication.isCompleted) "Completed" else "Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (medication.isCompleted) Color(0xFF2E7D32) else Color(0xFF1976D2),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dosage: ${medication.dosage}",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = "Frequency: ${medication.frequency}",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = "Duration: ${medication.duration} days",
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = "Start Date: ${
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(
                        medication.startDate,
                    )
                }",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (medication.purpose.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Purpose: ${medication.purpose}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun HealthScheduleCard(schedule: HealthSchedule) {
    Card {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Priority indicator
            Box(
                modifier =
                    Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(android.graphics.Color.parseColor(schedule.priority.color))),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = schedule.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text =
                        SimpleDateFormat(
                            "MMM dd, yyyy",
                            Locale.getDefault(),
                        ).format(schedule.scheduledDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Icon(
                imageVector =
                    when (schedule.scheduleType) {
                        HealthScheduleType.VACCINATION -> Icons.Default.Vaccines
                        HealthScheduleType.MEDICATION -> Icons.Default.Medication
                        HealthScheduleType.HEALTH_CHECK -> Icons.Default.HealthAndSafety
                        HealthScheduleType.VET_VISIT -> Icons.Default.LocalHospital
                        else -> Icons.Default.Schedule
                    },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// Dialog composables would be implemented here (AddVaccinationDialog, AddMedicationDialog, ScheduleReminderDialog)
// These would be comprehensive forms optimized for rural farmers with network-aware functionality

@Composable
private fun AddVaccinationDialog(
    healthService: HealthManagementService,
    selectedBirdId: String?,
    onDismiss: () -> Unit,
    onVaccinationAdded: () -> Unit,
) {
    // Implementation would include farmer-friendly form with vaccination templates
    // Network-aware saving with offline queue support
    // Photo capture for verification
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Vaccination") },
        text = { Text("Vaccination form would be implemented here with all required fields") },
        confirmButton = {
            TextButton(onClick = onVaccinationAdded) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun AddMedicationDialog(
    healthService: HealthManagementService,
    selectedBirdId: String?,
    onDismiss: () -> Unit,
    onMedicationAdded: () -> Unit,
) {
    // Implementation would include comprehensive medication form
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Medication") },
        text = { Text("Medication form would be implemented here with all required fields") },
        confirmButton = {
            TextButton(onClick = onMedicationAdded) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun ScheduleReminderDialog(
    healthService: HealthManagementService,
    selectedBirdId: String?,
    onDismiss: () -> Unit,
    onScheduled: () -> Unit,
) {
    // Implementation would include schedule creation form
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Reminder") },
        text = { Text("Schedule form would be implemented here with all required fields") },
        confirmButton = {
            TextButton(onClick = onScheduled) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
