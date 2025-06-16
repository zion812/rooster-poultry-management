package com.example.rooster.ui.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.models.PollOption
import com.example.rooster.models.PollResponse
import com.example.rooster.viewmodel.PollViewModel

@Composable
fun PollDialog(
    pollId: String,
    options: List<PollOption>,
    onDismiss: () -> Unit,
    pollViewModel: PollViewModel = viewModel(),
) {
    var selectedOptionId by remember { mutableStateOf<String?>(null) }
    var hasVoted by remember { mutableStateOf(false) }
    val results by pollViewModel.results.collectAsState()

    LaunchedEffect(Unit) {
        pollViewModel.loadResults(pollId)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Community Poll") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (!hasVoted) {
                    options.forEach { opt ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = opt.id == selectedOptionId,
                                        onClick = { selectedOptionId = opt.id },
                                    )
                                    .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = opt.id == selectedOptionId,
                                onClick = { selectedOptionId = opt.id },
                            )
                            Text(opt.text)
                        }
                    }
                } else {
                    // Show results
                    results.forEach { (optId, count) ->
                        val optText = options.find { it.id == optId }?.text ?: optId
                        Text("$optText: $count votes")
                    }
                }
            }
        },
        confirmButton = {
            if (!hasVoted) {
                TextButton(
                    onClick = {
                        selectedOptionId?.let {
                            pollViewModel.submitVote(PollResponse(pollId, it))
                            hasVoted = true
                        }
                    },
                    enabled = selectedOptionId != null,
                ) { Text("Submit") }
            } else {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        },
    )
}
