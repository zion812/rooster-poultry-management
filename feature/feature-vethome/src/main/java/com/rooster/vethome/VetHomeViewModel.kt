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

// Define UI State for VetHomeScreen
data class VetHomeUiState(
    val consultationQueue: List<ConsultationQueueItem> = emptyList(),
    val isLoadingConsultationQueue: Boolean = false,
    val consultationQueueError: String? = null,

    val recentPatients: List<PatientHistorySummary> = emptyList(),
    val isLoadingRecentPatients: Boolean = false,
    val recentPatientsError: String? = null,

    val healthAlerts: List<VetHealthAlert> = emptyList(),
    val isLoadingHealthAlerts: Boolean = false,
    val healthAlertsError: String? = null
)

@HiltViewModel
class VetHomeViewModel @Inject constructor(
    private val consultationRepository: VetConsultationRepository,
    private val patientRepository: VetPatientRepository,
    private val healthAlertRepository: VetHealthAlertRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VetHomeUiState())
    val uiState: StateFlow<VetHomeUiState> = _uiState.asStateFlow()

    // Assume a vetId is available, e.g., from user session
    private val currentVetId = "vet123" // Placeholder

    init {
        fetchConsultationQueue()
        fetchRecentPatientSummaries()
        fetchActiveHealthAlerts()
    }

    fun fetchConsultationQueue() {
        viewModelScope.launch {
            consultationRepository.getConsultationQueue(currentVetId)
                .onStart { _uiState.value = _uiState.value.copy(isLoadingConsultationQueue = true, consultationQueueError = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoadingConsultationQueue = false, consultationQueueError = e.message) }
                .collect { data -> _uiState.value = _uiState.value.copy(isLoadingConsultationQueue = false, consultationQueue = data) }
        }
    }

    fun fetchRecentPatientSummaries(count: Int = 5) {
        viewModelScope.launch {
            patientRepository.getRecentPatientSummaries(currentVetId, count)
                .onStart { _uiState.value = _uiState.value.copy(isLoadingRecentPatients = true, recentPatientsError = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoadingRecentPatients = false, recentPatientsError = e.message) }
                .collect { data -> _uiState.value = _uiState.value.copy(isLoadingRecentPatients = false, recentPatients = data) }
        }
    }

    fun fetchActiveHealthAlerts(count: Int = 3) {
        viewModelScope.launch {
            healthAlertRepository.getActiveHealthAlerts(currentVetId, count)
                .onStart { _uiState.value = _uiState.value.copy(isLoadingHealthAlerts = true, healthAlertsError = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoadingHealthAlerts = false, healthAlertsError = e.message) }
                .collect { data -> _uiState.value = _uiState.value.copy(isLoadingHealthAlerts = false, healthAlerts = data) }
        }
    }
}
