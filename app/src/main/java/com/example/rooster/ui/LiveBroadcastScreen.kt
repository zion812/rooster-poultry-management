package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.viewmodel.EnthusiastViewModel
import com.parse.ParseUser

@Composable
fun LiveBroadcastScreen(vm: EnthusiastViewModel = viewModel()) {
    val broadcasts by vm.broadcasts.collectAsState()
    val currentUserId = ParseUser.getCurrentUser()?.objectId ?: ""

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Live Broadcast", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        if (broadcasts.any { it.isLive && it.userId == currentUserId }) {
            Button(onClick = {
                val event = broadcasts.first { it.isLive && it.userId == currentUserId }
                vm.stopBroadcast(event.id)
            }) {
                Text("Stop Broadcast")
            }
        } else {
            Button(onClick = { vm.startBroadcast(currentUserId, "video") }) {
                Text("Start Video Broadcast")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { vm.startBroadcast(currentUserId, "audio") }) {
                Text("Start Audio Broadcast")
            }
        }
    }
}
