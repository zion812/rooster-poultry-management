package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.models.EventItem
import com.example.rooster.viewmodel.FarmerDashboardViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics

@Composable
fun EventsAndElectsScreen(vm: FarmerDashboardViewModel = viewModel()) {
    var isLoading by remember { mutableStateOf(true) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<EventItem?>(null) }
    var joinedEvents by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        vm.loadEvents()
        isLoading = false
    }
    val events by vm.events.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Events & Elections") },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
        ) {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Community Events & Elections",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Join community events, competitions, and participate in elections",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (events.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = "No Events",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No events available",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Check back later for community events and elections",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(events) { event ->
                        EventCard(
                            event = event,
                            isJoined = joinedEvents.contains(event.eventId),
                            onJoinClick = {
                                selectedEvent = event
                                showJoinDialog = true
                            },
                        )
                    }
                }
            }

            // Success Message
            if (showSuccessMessage) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(3000)
                    showSuccessMessage = false
                }

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                ) {
                    Text(
                        text = "Successfully joined the event!",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            // Error Message
            errorMessage?.let { error ->
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(
                            onClick = { errorMessage = null },
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
    }

    // Join Event Dialog
    if (showJoinDialog && selectedEvent != null) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = {
                Text("Join Event")
            },
            text = {
                Column {
                    Text("Do you want to join this event?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selectedEvent!!.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Type: ${selectedEvent!!.type}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Join the event
                        try {
                            joinedEvents = joinedEvents + selectedEvent!!.eventId
                            showSuccessMessage = true
                            showJoinDialog = false

                            FirebaseCrashlytics.getInstance()
                                .log("User joined event: ${selectedEvent!!.eventId} - ${selectedEvent!!.title}")

                            // Here you would typically call a repository method
                            // vm.joinEvent(selectedEvent!!.id)
                        } catch (e: Exception) {
                            errorMessage = "Failed to join event. Please try again."
                            showJoinDialog = false
                            FirebaseCrashlytics.getInstance()
                                .recordException(Exception("Event join failed: ${e.message}"))
                        }
                        selectedEvent = null
                    },
                ) {
                    Text("Join")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showJoinDialog = false
                        selectedEvent = null
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
fun EventCard(
    event: EventItem,
    isJoined: Boolean,
    onJoinClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
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
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = "Event Type",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.type,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    // Additional event details (if available in EventItem)
                    if (event.description?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Join Button
                if (isJoined) {
                    OutlinedButton(
                        onClick = { },
                        enabled = false,
                    ) {
                        Icon(
                            Icons.Default.Group,
                            contentDescription = "Joined",
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Joined")
                    }
                } else {
                    Button(
                        onClick = onJoinClick,
                    ) {
                        Text("Join")
                    }
                }
            }
        }
    }
}
