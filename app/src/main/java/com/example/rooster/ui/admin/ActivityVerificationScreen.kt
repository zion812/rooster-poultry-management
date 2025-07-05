package com.example.rooster.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parse.ParseCloud
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Data class representing farmer activity status
data class FarmerActivityStatus(
    val userId: String,
    val username: String,
    val firstListingAt: Date?,
    val totalListings: Int,
    val last30DayListings: Int,
    val isActivityVerified: Boolean,
    val eligible: Boolean,
)

class ActivityVerificationViewModel : ViewModel() {
    var statuses by mutableStateOf<List<FarmerActivityStatus>>(emptyList())
        private set

    fun loadStatuses() {
        viewModelScope.launch {
            try {
                @Suppress("UNCHECKED_CAST")
                val result =
                    ParseCloud.callFunction<List<Map<String, Any>>>(
                        "getActivityStatus",
                        emptyMap<String, Any>(),
                    )
                statuses =
                    result.map { map ->
                        // Null-safe extraction and Date handling
                        val rawFirst = map["firstListingAt"]
                        val firstDate: Date? =
                            when (rawFirst) {
                                is Date -> rawFirst
                                is String ->
                                    SimpleDateFormat(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                        Locale.US,
                                    ).parse(rawFirst)

                                else -> null
                            }
                        FarmerActivityStatus(
                            userId = map["userId"] as? String ?: "",
                            username = map["username"] as? String ?: "",
                            firstListingAt = firstDate,
                            totalListings = (map["totalListings"] as? Number)?.toInt() ?: 0,
                            last30DayListings = (map["last30DayListings"] as? Number)?.toInt() ?: 0,
                            isActivityVerified = map["isActivityVerified"] as? Boolean ?: false,
                            eligible = map["eligible"] as? Boolean ?: false,
                        )
                    }
            } catch (e: Exception) {
                statuses = emptyList()
            }
        }
    }

    fun approve(userId: String) {
        viewModelScope.launch {
            try {
                ParseCloud.callFunction<Any>(
                    "approveActivityVerification",
                    mapOf("userId" to userId),
                )
                loadStatuses()
            } catch (e: Exception) {
                // ignore
            }
        }
    }
}

@Composable
fun ActivityVerificationScreen(viewModel: ActivityVerificationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    LaunchedEffect(Unit) { viewModel.loadStatuses() }
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Text("Farmer Activity Verification", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        if (viewModel.statuses.isEmpty()) {
            Text("Loading or no data available...")
        } else {
            LazyColumn {
                items(viewModel.statuses) { status ->
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(status.username, style = MaterialTheme.typography.titleMedium)
                                Text("Total: ${'$'}{status.totalListings}, Recent: ${'$'}{status.last30DayListings}")
                                Text("Eligible: ${'$'}{status.eligible}")
                            }
                            if (!status.isActivityVerified && status.eligible) {
                                Button(onClick = { viewModel.approve(status.userId) }) { Text("Approve") }
                            } else {
                                Text(if (status.isActivityVerified) "Verified" else "Not Eligible")
                            }
                        }
                    }
                }
            }
        }
    }
}
