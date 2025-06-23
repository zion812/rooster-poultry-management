package com.example.rooster

import android.app.Application
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parse.ParseUser
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val context = LocalContext.current.applicationContext as Application
    val eggProductionService = remember { EggProductionService(context) }
    val coroutineScope = rememberCoroutineScope()

    var monthlySummaries by remember { mutableStateOf<List<EggProductionMonthlySummary>>(emptyList()) }
    var leaderboardEntries by remember {
        mutableStateOf<List<EggProductionLeaderboardEntry>>(
            emptyList(),
        )
    }
    var breedingSuccessRate by remember { mutableStateOf<BreedingSuccessRate?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAddEggRecordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentUser = ParseUser.getCurrentUser()

        // Fetch monthly summaries
        eggProductionService.getMonthlySummaries(currentYear).let { result ->
            if (result.isSuccess) {
                monthlySummaries = result.getOrNull() ?: emptyList()
            } else {
                error = "Failed to load monthly summaries: ${result.exceptionOrNull()?.message}"
            }
        }

        // Fetch leaderboard
        eggProductionService.getEggProductionLeaderboard(LeaderboardPeriod.LAST_30_DAYS)
            .let { result ->
                if (result.isSuccess) {
                    leaderboardEntries = result.getOrNull() ?: emptyList()
                } else {
                    error = (
                        error
                            ?: ""
                    ) + "\nFailed to load leaderboard: ${result.exceptionOrNull()?.message}"
                }
            }

        // Fetch breeding success rate
        currentUser?.objectId?.let {
            eggProductionService.getBreedingSuccessRate(it, LeaderboardPeriod.LAST_90_DAYS)
                .let { result ->
                    if (result.isSuccess) {
                        breedingSuccessRate = result.getOrNull()
                    } else {
                        error = (
                            error
                                ?: ""
                        ) + "\nFailed to load breeding success: ${result.exceptionOrNull()?.message}"
                    }
                }
        }
        isLoading = false
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddEggRecordDialog = true }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add Egg Record")
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            Text(text = "Farm Dashboard", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                // Monthly Egg Production Chart
                SectionCard(
                    title = "Monthly Egg Production (${
                        Calendar.getInstance().get(Calendar.YEAR)
                    })",
                    icon = Icons.Default.BarChart,
                ) {
                    if (monthlySummaries.isNotEmpty()) {
                        EggProductionLineChart(monthlySummaries)
                    } else {
                        Text("No egg production data for this year yet.")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Breeding Success Rate
                breedingSuccessRate?.let { bsr ->
                    SectionCard(
                        title = "Breeding Success (${bsr.period})",
                        icon = Icons.AutoMirrored.Filled.ShowChart,
                    ) {
                        Text("Total Eggs Set: ${bsr.totalEggsSet}")
                        Text("Chicks Hatched: ${bsr.totalChicksHatched}")
                        Text(
                            "Success Rate: ${String.format("%.2f", bsr.successRate)}%",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Egg Production Leaderboard
                SectionCard(
                    title = "Top Egg Producers (Last 30 Days)",
                    icon = Icons.Default.Leaderboard,
                ) {
                    if (leaderboardEntries.isNotEmpty()) {
                        leaderboardEntries.forEach { entry ->
                            LeaderboardItem(entry)
                            HorizontalDivider()
                        }
                    } else {
                        Text("Leaderboard is currently empty.")
                    }
                }
            }

            error?.let {
                Text(
                    "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        }
    }

    if (showAddEggRecordDialog) {
        AddEggProductionRecordDialog(
            onDismiss = { showAddEggRecordDialog = false },
            onRecordAdded = {
                showAddEggRecordDialog = false
                // Refresh data after adding a record
                coroutineScope.launch {
                    isLoading = true
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    val currentUser = ParseUser.getCurrentUser()
                    eggProductionService.getMonthlySummaries(currentYear).let { result ->
                        if (result.isSuccess) monthlySummaries = result.getOrNull() ?: emptyList()
                    }
                    eggProductionService.getEggProductionLeaderboard(LeaderboardPeriod.LAST_30_DAYS)
                        .let { result ->
                            if (result.isSuccess) {
                                leaderboardEntries =
                                    result.getOrNull() ?: emptyList()
                            }
                        }
                    currentUser?.objectId?.let {
                        eggProductionService.getBreedingSuccessRate(
                            it,
                            LeaderboardPeriod.LAST_90_DAYS,
                        ).let { result ->
                            if (result.isSuccess) breedingSuccessRate = result.getOrNull()
                        }
                    }
                    isLoading = false
                }
            },
            eggProductionService = eggProductionService,
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun EggProductionLineChart(summaries: List<EggProductionMonthlySummary>) {
    if (summaries.isEmpty()) return

    val maxEggs = summaries.maxOfOrNull { it.totalEggs }?.toFloat() ?: 0f
    val monthLabels =
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 8.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val _width = size.width
            val _height = size.height
            val _padding = 20.dp.toPx()

            // Draw Y-axis labels and lines
            (0..4).forEach { i ->
                val y = _height - _padding - (i * (_height - 2 * _padding) / 4f)
                drawLine(
                    color = Color.LightGray,
                    start = Offset(_padding, y),
                    end = Offset(_width - _padding, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
                )
            }

            // Draw X-axis line
            drawLine(
                color = Color.Gray,
                start = Offset(_padding, _height - _padding),
                end = Offset(_width - _padding, _height - _padding),
                strokeWidth = 2f,
            )

            // Prepare data points for the chart
            val dataPoints = FloatArray(12) { 0f } // Initialize for 12 months
            summaries.forEach { summary ->
                val monthIndex = summary.monthYear.substring(5).toIntOrNull()?.minus(1) ?: -1
                if (monthIndex in 0..11) {
                    dataPoints[monthIndex] = summary.totalEggs.toFloat()
                }
            }

            // Draw lines and points
            val pointSpacing = (_width - 2 * _padding) / 11f // For 12 points

            for (i in 0 until 11) {
                val x1 = _padding + i * pointSpacing
                val y1 = _height - _padding - (dataPoints[i] / maxEggs * (_height - 2 * _padding))
                val x2 = _padding + (i + 1) * pointSpacing
                val y2 =
                    _height - _padding - (dataPoints[i + 1] / maxEggs * (_height - 2 * _padding))

                drawLine(
                    color = Color.Blue,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = 3f,
                )
                drawCircle(
                    color = Color.Blue,
                    radius = 6f,
                    center = Offset(x1, y1),
                )
            }
            // Draw last point
            drawCircle(
                color = Color.Blue,
                radius = 6f,
                center =
                    Offset(
                        _padding + 11 * pointSpacing,
                        _height - _padding - (dataPoints[11] / maxEggs * (_height - 2 * _padding)),
                    ),
            )
        }
        // Simple Text labels for X-axis below the canvas
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            monthLabels.forEach {
                Text(it, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun LeaderboardItem(entry: EggProductionLeaderboardEntry) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                entry.farmerName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            entry.region?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
        }
        Text(
            "${entry.totalEggs} eggs",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEggProductionRecordDialog(
    onDismiss: () -> Unit,
    onRecordAdded: () -> Unit,
    eggProductionService: EggProductionService,
) {
    var eggCount by remember { mutableStateOf("") }
    var dateString by remember {
        mutableStateOf(
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault(),
            ).format(Date()),
        )
    }
    var breed by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Egg Production") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = eggCount,
                    onValueChange = { eggCount = it },
                    label = { Text("Number of Eggs Collected") },
                    keyboardOptions =
                        androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                        ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dateString,
                    onValueChange = { dateString = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Breed (Optional)") },
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    maxLines = 3,
                )
                saveError?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isSaving = true
                        saveError = null
                        val parsedDate =
                            try {
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
                            } catch (e: Exception) {
                                null
                            }
                        if (parsedDate == null) {
                            saveError = "Invalid date format. Use YYYY-MM-DD."
                            isSaving = false
                            return@launch
                        }
                        val count = eggCount.toIntOrNull()
                        if (count == null || count < 0) {
                            saveError = "Invalid egg count."
                            isSaving = false
                            return@launch
                        }

                        val record =
                            EggProductionRecord(
                                breederId = ParseUser.getCurrentUser()?.objectId ?: "",
                                date = parsedDate,
                                eggCount = count,
                                breed = breed.takeIf { it.isNotBlank() },
                                notes = notes.takeIf { it.isNotBlank() },
                                region = ParseUser.getCurrentUser()?.getString("region"),
                            )
                        val result = eggProductionService.addEggProductionRecord(record)
                        if (result.isSuccess) {
                            onRecordAdded()
                        } else {
                            saveError =
                                result.exceptionOrNull()?.message ?: "Failed to save record."
                        }
                        isSaving = false
                    }
                },
                enabled = !isSaving && eggCount.isNotBlank() && dateString.isNotBlank(),
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp))
                } else {
                    Text("Save Record")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancel")
            }
        },
    )
}
