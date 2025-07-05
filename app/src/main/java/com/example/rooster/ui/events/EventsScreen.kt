package com.example.rooster.ui.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventsScreen(vm: EventViewModel = viewModel()) {
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        vm.loadEvents()
        isLoading = false
    }
    val events by vm.events.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Community Events") }) },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                ) {
                    items(events) { event ->
                        Card(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(event.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text =
                                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                            .format(Date(event.date)),
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(event.description)
                                Spacer(Modifier.height(12.dp))
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Button(onClick = { vm.joinEvent(event.id) }) {
                                        Text(if (event.isJoined) "Joined" else "Join")
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    if (event.type == "Election") {
                                        Button(onClick = { vm.voteEvent(event.id) }) {
                                            Text("Vote")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
