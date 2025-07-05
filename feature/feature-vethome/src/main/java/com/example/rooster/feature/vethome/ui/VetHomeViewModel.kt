package com.example.rooster.feature.vethome.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.auth.domain.repository.AuthRepository // To get current vetId
import com.example.rooster.core.data.fetcher.VeterinarianDashboardDataFetcher
import com.example.rooster.core.data.model.ConsultationRequestTeaser
import com.example.rooster.core.data.model.FarmHealthAlertTeaser
import com.example.rooster.core.data.model.ScheduledAppointmentTeaser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VetHomeUiState(
    val isLoading: Boolean = true,
    val vetName: String = "",
    val pendingConsultationRequests: List<ConsultationRequestTeaser> = emptyList(),
    val upcomingAppointments: List<ScheduledAppointmentTeaser> = emptyList(),
    val recentFarmHealthAlerts: List<FarmHealthAlertTeaser> = emptyList(),
    val unreadMessages: Int = 0,
    val errorMessage: String? = null
    // Add navigation triggers if needed
)

@HiltViewModel
class VetHomeViewModel @Inject constructor(
    private val vetDashboardDataFetcher: VeterinarianDashboardDataFetcher,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VetHomeUiState())
    val uiState: StateFlow<VetHomeUiState> = _uiState.asStateFlow()

    init {
        loadVetDashboardData()
    }

    fun loadVetDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val currentUser = authRepository.getCurrentUser().firstOrNull()
            val vetId = currentUser?.id // Assuming the vet's ID is the standard user ID

            if (vetId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in.") } // TODO: String resource
                return@launch
            }

            val result = vetDashboardDataFetcher.getDashboardSummary(vetId)
            result.fold(
                onSuccess = { summary ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            vetName = summary.vetName,
                            pendingConsultationRequests = summary.pendingConsultationRequests,
                            upcomingAppointments = summary.upcomingAppointments,
                            recentFarmHealthAlerts = summary.recentFarmHealthAlerts,
                            unreadMessages = summary.unreadMessages
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to load vet dashboard data." // TODO: String resource
                        )
                    }
                }
            )
        }
    }
    // Placeholder for actions
    // fun onConsultationClicked(requestId: String) { /* TODO: navigation */ }
    // fun onAppointmentClicked(appointmentId: String) { /* TODO: navigation */ }
}
```

**Notes on `VetHomeViewModel.kt`:**
*   Defines `VetHomeUiState`.
*   Injects `VeterinarianDashboardDataFetcher` and `AuthRepository`.
*   Loads dashboard data for the current vet.
*   Updates UI state.

Next, I'll create the UI for `VeterinarianDashboardScreen.kt`.
