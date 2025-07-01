package com.example.rooster.ui.traceability

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rooster.data.TraceabilityRepository
import com.example.rooster.ui.components.*
import kotlinx.coroutines.launch

/**
 * Fowl Traceability Screen - Demonstrates TraceabilityTimelineItem usage
 *
 * Features:
 * - Uses TraceabilityTimelineItem component with sample data
 * - Rural-optimized UI with clear visual hierarchy
 * - Telugu language support
 * - Visual timeline showing fowl life events
 */
@Composable
fun FowlTraceabilityScreen(
    fowlId: String,
    onEventClick: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onVerificationClick: (String) -> Unit,
    isTeluguMode: Boolean = false,
    modifier: Modifier = Modifier,
    traceabilityRepository: TraceabilityRepository = TraceabilityRepository,
) {
    val scope = rememberCoroutineScope()
    var traceabilityEvents by remember { mutableStateOf<List<TraceabilityEvent>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(fowlId) {
        scope.launch {
            loading = true
            error = null
            try {
                traceabilityEvents = traceabilityRepository.fetchTraceabilityEvents(fowlId)
            } catch (e: Exception) {
                error = "Failed to load traceability events: ${e.message}"
                traceabilityEvents = emptyList()
            } finally {
                loading = false
            }
        }
    }

    StandardScreenLayout(
        topBar = {
            FowlTraceabilityTopBar(
                fowlId = fowlId,
                isTeluguMode = isTeluguMode,
            )
        },
        scrollable = false,
        modifier = modifier,
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.wrapContentSize(Alignment.Center))
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else if (traceabilityEvents.isEmpty()) {
            Text("No traceability events found for this fowl.", modifier = Modifier.wrapContentSize(Alignment.Center))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp), // No spacing for timeline continuity
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(
                    items = traceabilityEvents,
                    key = { _, event -> event.id },
                ) { index, event ->
                    TraceabilityTimelineItem(
                        event = event,
                        isFirst = index == 0,
                        isLast = index == traceabilityEvents.lastIndex,
                        onEventClick = { onEventClick(event.id) },
                        onImageClick =
                            if (event.imageUrl.isNotEmpty()) {
                                { onImageClick(event.imageUrl) }
                            } else {
                                null
                            },
                        onVerificationClick = { onVerificationClick(event.id) },
                        isTeluguMode = isTeluguMode,
                        showFullDetails = true,
                    )
                }
            }
        }
    }
}

/**
 * Custom Top App Bar for Fowl Traceability
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FowlTraceabilityTopBar(
    fowlId: String,
    isTeluguMode: Boolean,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = if (isTeluguMode) "కోడి చరిత్ర" else "Fowl Timeline",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "ID: $fowlId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { /* Handle back navigation */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = if (isTeluguMode) "వెనుకకు" else "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Handle share timeline */ }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = if (isTeluguMode) "పంచుకోండి" else "Share",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        modifier = modifier,
    )
}
