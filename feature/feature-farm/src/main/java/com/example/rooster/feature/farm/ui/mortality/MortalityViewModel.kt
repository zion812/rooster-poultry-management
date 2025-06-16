package com.example.rooster.feature.farm.ui.mortality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.feature.farm.domain.model.MortalityRecord
import com.example.rooster.feature.farm.domain.usecase.DeleteMortalityRecordUseCase
import com.example.rooster.feature.farm.domain.usecase.GetMortalityRecordsUseCase
import com.example.rooster.feature.farm.domain.usecase.SaveMortalityRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for managing mortality records following MVVM architecture.
 * Handles complex business logic, error states, and data persistence
 * with proper dependency injection and clean architecture patterns.
 */
@HiltViewModel
class MortalityViewModel @Inject constructor(
    private val getMortalityRecordsUseCase: GetMortalityRecordsUseCase,
    private val saveMortalityRecordsUseCase: SaveMortalityRecordsUseCase,
    private val deleteMortalityRecordUseCase: DeleteMortalityRecordUseCase
) : ViewModel() {

    private val _records = MutableStateFlow<List<MortalityRecord>>(emptyList())
    val records: StateFlow<List<MortalityRecord>> = _records.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Load mortality records for a specific fowl with comprehensive error handling
     */
    fun loadMortality(fowlId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                getMortalityRecordsUseCase(fowlId).collect { result ->
                    if (result.isSuccess) {
                        _records.value = result.getOrThrow().sortedByDescending { it.recordedAt }
                    } else {
                        _error.value =
                            "మరణ రికార్డులను లోడ్ చేయడంలో వైఫల్యం: ${result.exceptionOrNull()?.localizedMessage}"
                    }
                }
            } catch (e: Exception) {
                _error.value = "మరణ రికార్డులను లోడ్ చేయడంలో వైఫల్యం: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Add new mortality record with validation and error handling
     */
    fun addRecord(fowlId: String, cause: String, description: String, attachment: String) {
        if (cause.isBlank()) {
            _error.value = "మరణ కారణం తప్పనిసరి"
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val record = MortalityRecord(
                    id = UUID.randomUUID().toString(),
                    fowlId = fowlId,
                    cause = cause.trim(),
                    description = description.trim().takeIf { it.isNotBlank() },
                    weight = null, // Weight can be added later if needed
                    photos = if (attachment.trim()
                            .isNotBlank()
                    ) listOf(attachment.trim()) else null,
                    recordedAt = Date(),
                    createdAt = Date()
                )

                val result = saveMortalityRecordsUseCase(listOf(record))
                if (result.isFailure) {
                    _error.value =
                        "మరణ రికార్డు జోడించడంలో వైఫల్యం: ${result.exceptionOrNull()?.localizedMessage}"
                } else {
                    // Refresh records after successful save
                    loadMortality(fowlId)
                }
            } catch (e: Exception) {
                _error.value = "మరణ రికార్డు జోడించడంలో వైఫల్యం: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Remove mortality record with confirmation and error handling
     */
    fun removeRecord(recordId: String, fowlId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val result = deleteMortalityRecordUseCase(recordId)
                if (result.isFailure) {
                    _error.value =
                        "మరణ రికార్డు తొలగించడంలో వైఫల్యం: ${result.exceptionOrNull()?.localizedMessage}"
                } else {
                    // Refresh records after successful deletion
                    loadMortality(fowlId)
                }
            } catch (e: Exception) {
                _error.value = "మరణ రికార్డు తొలగించడంలో వైఫల్యం: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear current error state
     */
    fun clearError() {
        _error.value = null
    }
}
