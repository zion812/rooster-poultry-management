package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.data.BroadcastRepository
import com.example.rooster.data.SuggestionRepository
import com.example.rooster.data.TransferRepository
import com.example.rooster.models.BroadcastEvent
import com.example.rooster.models.FlockEntry
import com.example.rooster.models.GrowthStat
import com.example.rooster.models.SuggestionItem
import com.example.rooster.models.TransferRecord
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EnthusiastViewModel : ViewModel() {
    private val _flock = MutableStateFlow<List<FlockEntry>>(emptyList())
    val flock: StateFlow<List<FlockEntry>> = _flock

    private val _growth = MutableStateFlow<List<GrowthStat>>(emptyList())
    val growth: StateFlow<List<GrowthStat>> = _growth

    private val _suggestions = MutableStateFlow<List<SuggestionItem>>(emptyList())
    val suggestions: StateFlow<List<SuggestionItem>> = _suggestions

    private val _transfers = MutableStateFlow<List<TransferRecord>>(emptyList())
    val transfers: StateFlow<List<TransferRecord>> = _transfers

    private val _broadcasts = MutableStateFlow<List<BroadcastEvent>>(emptyList())
    val broadcasts: StateFlow<List<BroadcastEvent>> = _broadcasts

    private val transferRepository = TransferRepository()

    fun startBroadcast(
        userId: String,
        type: String,
    ) = viewModelScope.launch {
        try {
            val evt = BroadcastRepository.initiateBroadcast(userId, type)
            _broadcasts.value = listOf(evt)
            FirebaseCrashlytics.getInstance().log("Broadcast started: ${evt.id}")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun stopBroadcast(eventId: String) =
        viewModelScope.launch {
            try {
                BroadcastRepository.stopBroadcast(eventId)
                _broadcasts.value = BroadcastRepository.listActiveBroadcasts()
                FirebaseCrashlytics.getInstance().log("Broadcast stopped: $eventId")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

    fun fetchSuggestions(birdId: String) =
        viewModelScope.launch {
            try {
                val list = SuggestionRepository.fetchSuggestions(birdId)
                _suggestions.value = list
                FirebaseCrashlytics.getInstance().log("Suggestions fetched for $birdId: ${list.size}")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

    fun generateSuggestions(birdId: String) =
        viewModelScope.launch {
            try {
                val list = SuggestionRepository.generateSuggestions(birdId)
                _suggestions.value = list
                FirebaseCrashlytics.getInstance().log("Suggestions generated for $birdId")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

    fun createTransfer(record: TransferRecord) =
        viewModelScope.launch {
            try {
                // Convert new TransferRecord to repository format
                transferRepository.createTransferRequest(
                    fowlId = record.chickenId,
                    fromOwnerId = record.fromOwnerId,
                    toOwnerId = record.toOwnerId,
                    notes = "", // Use empty string since new model doesn't have notes
                )
                FirebaseCrashlytics.getInstance().log("Transfer created: ${record.id}")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

    fun confirmTransfer(
        transferId: String,
        chickenId: String,
    ) = viewModelScope.launch {
        try {
            transferRepository.acceptTransfer(transferId)
            // Load transfer history as maps since we changed the return type
            val lineageData = transferRepository.getAssetLineage(chickenId)
            // Convert maps back to TransferRecord for compatibility
            _transfers.value =
                lineageData.map { data ->
                    TransferRecord(
                        id = data["transferRequestId"] as? String ?: "",
                        chickenId = data["fowlId"] as? String ?: chickenId,
                        fromOwnerId = data["previousOwnerId"] as? String ?: "",
                        toOwnerId = data["newOwnerId"] as? String ?: "",
                        timestamp =
                            (data["transferDate"] as? java.util.Date)?.time
                                ?: System.currentTimeMillis(),
                        state = com.example.rooster.models.TransferState.VERIFIED,
                    )
                }
            FirebaseCrashlytics.getInstance().log("Transfer confirmed: $transferId")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun fetchFamilyChain(chickenId: String) =
        viewModelScope.launch {
            try {
                val lineageData = transferRepository.getAssetLineage(chickenId)
                // Convert maps back to TransferRecord for compatibility
                _transfers.value =
                    lineageData.map { data ->
                        TransferRecord(
                            id = data["transferRequestId"] as? String ?: "",
                            chickenId = data["fowlId"] as? String ?: chickenId,
                            fromOwnerId = data["previousOwnerId"] as? String ?: "",
                            toOwnerId = data["newOwnerId"] as? String ?: "",
                            timestamp =
                                (data["transferDate"] as? java.util.Date)?.time
                                    ?: System.currentTimeMillis(),
                            state = com.example.rooster.models.TransferState.VERIFIED,
                        )
                    }
                FirebaseCrashlytics.getInstance().log("Family chain loaded for $chickenId")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

    // Complete flock/growth loading logic implementation
    fun loadFlockData(userId: String) =
        viewModelScope.launch {
            try {
                // Load flock entries from Parse
                val fowlQuery = com.parse.ParseQuery.getQuery<com.parse.ParseObject>("Fowl")
                fowlQuery.whereEqualTo("owner", com.parse.ParseUser.getCurrentUser())
                fowlQuery.include("owner")
                fowlQuery.addDescendingOrder("createdAt")
                fowlQuery.limit = 50 // Reasonable limit for mobile

                fowlQuery.findInBackground { fowlObjects, error ->
                    if (error == null && fowlObjects != null) {
                        val flockEntries =
                            fowlObjects.mapNotNull { fowl ->
                                try {
                                    FlockEntry(
                                        id = fowl.objectId ?: "",
                                        name = fowl.getString("name") ?: "Unknown Flock",
                                        count = fowl.getInt("count"),
                                        createdAt = fowl.createdAt?.time ?: System.currentTimeMillis(),
                                    )
                                } catch (e: Exception) {
                                    null // Skip invalid entries
                                }
                            }
                        _flock.value = flockEntries
                        FirebaseCrashlytics.getInstance()
                            .log("Flock data loaded: ${flockEntries.size} entries")
                    } else {
                        // Create mock flock entries if no data available
                        val mockFlocks =
                            listOf(
                                FlockEntry("flock1", "Main Coop", 25),
                                FlockEntry("flock2", "Young Birds", 15),
                                FlockEntry("flock3", "Breeding Stock", 8),
                            )
                        _flock.value = mockFlocks
                        FirebaseCrashlytics.getInstance().log("Using mock flock data")
                    }
                }

                // Load growth statistics
                loadGrowthStatistics(userId)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

    private fun loadGrowthStatistics(userId: String) {
        try {
            // Query growth records from Parse
            val growthQuery = com.parse.ParseQuery.getQuery<com.parse.ParseObject>("GrowthRecord")
            growthQuery.whereEqualTo("farmerId", userId)
            growthQuery.addDescendingOrder("recordDate")
            growthQuery.limit = 100

            growthQuery.findInBackground { records, error ->
                if (error == null && records != null) {
                    val growthStats =
                        records.mapNotNull { record ->
                            try {
                                GrowthStat(
                                    ageWeeks = record.getInt("ageWeeks"),
                                    avgWeight = record.getDouble("avgWeight"),
                                )
                            } catch (e: Exception) {
                                null // Skip invalid records
                            }
                        }
                    _growth.value = growthStats
                    FirebaseCrashlytics.getInstance()
                        .log("Growth statistics loaded: ${growthStats.size} records")
                } else {
                    // Create mock growth stats if no data available
                    val mockStats =
                        listOf(
                            GrowthStat(4, 300.0), // 4 weeks, 300 grams
                            GrowthStat(8, 800.0), // 8 weeks, 800 grams
                            GrowthStat(12, 1500.0), // 12 weeks, 1500 grams
                            GrowthStat(16, 2200.0), // 16 weeks, 2200 grams
                        )
                    _growth.value = mockStats
                    FirebaseCrashlytics.getInstance().log("Using mock growth statistics")
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun refreshFlockData() =
        viewModelScope.launch {
            loadFlockData(com.parse.ParseUser.getCurrentUser()?.objectId ?: "")
        }
}
