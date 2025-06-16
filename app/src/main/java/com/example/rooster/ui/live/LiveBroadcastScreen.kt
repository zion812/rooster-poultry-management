package com.example.rooster.ui.live

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.viewmodel.LiveViewModel

@Composable
fun LiveBroadcastScreen(
    sessionId: String,
    liveViewModel: LiveViewModel = viewModel(),
) {
    val context = LocalContext.current
    var isLive by remember { mutableStateOf(false) }
    var inviteId by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "Live Broadcast", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = inviteId,
            onValueChange = { inviteId = it },
            label = { Text("Invite User ID or Role") },
            modifier = Modifier.fillMaxWidth(),
        )

        if (isLive) {
            Button(
                onClick = {
                    liveViewModel.stopBroadcast()
                    isLive = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            ) {
                Text("Stop Broadcast")
            }
        } else {
            Button(
                onClick = {
                    liveViewModel.startBroadcast(sessionId, isVideo = true)
                    liveViewModel.notifyLiveNow(context, "Live session started: $sessionId")
                    isLive = true
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Start Video Broadcast")
            }
            Button(
                onClick = {
                    liveViewModel.startBroadcast(sessionId, isVideo = false)
                    liveViewModel.notifyLiveNow(context, "Audio session started: $sessionId")
                    isLive = true
                },
            ) {
                Text("Start Audio Broadcast")
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = { liveViewModel.inviteUser(inviteId.text) }) {
            Text("Invite")
        }
    }
}
