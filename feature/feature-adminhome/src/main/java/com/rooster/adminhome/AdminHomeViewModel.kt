package com.rooster.adminhome

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

package com.rooster.adminhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooster.adminhome.domain.model.ContentModerationItem
import com.rooster.adminhome.domain.model.FinancialAnalyticHighlight
import com.rooster.adminhome.domain.model.SystemMetric
import com.rooster.adminhome.domain.model.UserManagementInfo
import com.rooster.adminhome.domain.repository.AdminContentModerationRepository
import com.rooster.adminhome.domain.repository.AdminFinancialRepository
import com.rooster.adminhome.domain.repository.AdminSystemRepository
import com.rooster.adminhome.domain.repository.AdminUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.rooster.core.common.util.DataState

// Define UI State for AdminHomeScreen
data class AdminHomeUiState(
    val systemMetricsState: DataState<List<SystemMetric>> = DataState.Loading(null),
    val userManagementInfoState: DataState<UserManagementInfo?> = DataState.Loading(null),
    val financialHighlightsState: DataState<List<FinancialAnalyticHighlight>> = DataState.Loading(null),
    val moderationQueueState: DataState<List<ContentModerationItem>> = DataState.Loading(null),
    val transientUserMessage: String? = null,
    val messageId: java.util.UUID? = null,
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false
)

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val systemRepository: AdminSystemRepository,
    private val userRepository: AdminUserRepository,
    private val financialRepository: AdminFinancialRepository,
    private val moderationRepository: AdminContentModerationRepository,
    private val connectivityRepository: com.example.rooster.core.common.connectivity.ConnectivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminHomeUiState())
    val uiState: StateFlow<AdminHomeUiState> = _uiState.asStateFlow()

    init {
        fetchAllData()
        observeNetworkStatus()
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

    private fun fetchAllData() {
        fetchSystemMetrics()
        fetchUserManagementSummary()
        fetchFinancialHighlights()
        fetchModerationQueue()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            fetchAllData()
            // Consider a more robust way to set isRefreshing = false
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    fun fetchSystemMetrics() {
        viewModelScope.launch {
            // Assuming repository now returns Flow<DataState<List<SystemMetric>>>
            systemRepository.getCurrentSystemMetrics()
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(systemMetricsState = dataState)
                }
        }
    }

    fun fetchUserManagementSummary() {
        viewModelScope.launch {
            // Assuming repository now returns Flow<DataState<UserManagementInfo?>>
            userRepository.getUserManagementSummary()
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(userManagementInfoState = dataState)
                }
        }
    }

    fun fetchFinancialHighlights() {
        viewModelScope.launch {
            // Assuming repository now returns Flow<DataState<List<FinancialAnalyticHighlight>>>
            financialRepository.getFinancialHighlights()
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(financialHighlightsState = dataState)
                }
        }
    }

    fun fetchModerationQueue(count: Int = 5) { // Fetch a small number for home screen summary
        viewModelScope.launch {
            // Assuming repository now returns Flow<DataState<List<ContentModerationItem>>>
            moderationRepository.getPendingModerationItems(count)
                .collect { dataState ->
                    _uiState.value = _uiState.value.copy(moderationQueueState = dataState)
                }
        }
    }

    fun clearTransientMessage() {
        _uiState.value = _uiState.value.copy(transientUserMessage = null, messageId = null)
    }
}
