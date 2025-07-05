package com.rooster.vethome

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

package com.rooster.vethome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooster.vethome.domain.model.ConsultationQueueItem
import com.rooster.vethome.domain.model.PatientHistorySummary
import com.rooster.vethome.domain.model.VetHealthAlert
import com.rooster.vethome.domain.repository.VetConsultationRepository
import com.rooster.vethome.domain.repository.VetHealthAlertRepository
import com.rooster.vethome.domain.repository.VetPatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.rooster.core.common.util.DataState

// Define UI State for VetHomeScreen
data class VetHomeUiState(
    val consultationQueueState: DataState<List<ConsultationQueueItem>> = DataState.Loading(null),
    val recentPatientsState: DataState<List<PatientHistorySummary>> = DataState.Loading(null),
    val healthAlertsState: DataState<List<VetHealthAlert>> = DataState.Loading(null),
    val transientUserMessage: String? = null,
    val messageId: java.util.UUID? = null,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false // Added for network status
)

@HiltViewModel
class VetHomeViewModel @Inject constructor(
    private val consultationRepository: VetConsultationRepository,
    private val patientRepository: VetPatientRepository,
    private val healthAlertRepository: VetHealthAlertRepository,
    private val connectivityRepository: com.example.rooster.core.common.connectivity.ConnectivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VetHomeUiState())
    val uiState: StateFlow<VetHomeUiState> = _uiState.asStateFlow()

    // Assume a vetId is available, e.g., from user session
    private val currentVetId = "vet123" // Placeholder

    init {
        fetchAllData()
        observeNetworkStatus()
    }

    private fun fetchAllData() {
        fetchConsultationQueue()
        fetchRecentPatientSummaries()
        fetchActiveHealthAlerts()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            fetchAllData()
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            connectivityRepository.observeNetworkStatus().collect { status ->
                _uiState.value = _uiState.value.copy(
                    isOffline = status != com.example.rooster.core.common.connectivity.NetworkStatus.Available
                )
            }
        }
    }

    fun fetchConsultationQueue() {
        viewModelScope.launch {
            // Assuming repository now returns Flow<DataState<List<ConsultationQueueItem>>>
            consultationRepository.getConsultationQueue(currentVetId)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(consultationQueueState = dataState)
                }
        }
    }

    fun fetchRecentPatientSummaries(count: Int = 5) {
        viewModelScope.launch {
            // Assuming repository now returns Flow<DataState<List<PatientHistorySummary>>>
            patientRepository.getRecentPatientSummaries(currentVetId, count)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(recentPatientsState = dataState)
                }
        }
    }

    fun fetchActiveHealthAlerts(count: Int = 3) {
        viewModelScope.launch {
            // Assuming repository now returns Flow<DataState<List<VetHealthAlert>>>
            healthAlertRepository.getActiveHealthAlerts(currentVetId, count)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(healthAlertsState = dataState)
                }
        }
    }

    fun clearTransientMessage() {
        _uiState.value = _uiState.value.copy(transientUserMessage = null, messageId = null)
    }
}
