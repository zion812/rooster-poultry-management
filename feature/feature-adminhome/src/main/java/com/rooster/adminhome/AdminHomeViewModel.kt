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

// Define UI State for AdminHomeScreen
data class AdminHomeUiState(
    val systemMetrics: List<SystemMetric> = emptyList(),
    val isLoadingSystemMetrics: Boolean = false,
    val systemMetricsError: String? = null,

    val userManagementInfo: UserManagementInfo? = null,
    val isLoadingUserManagementInfo: Boolean = false,
    val userManagementInfoError: String? = null,

    val financialHighlights: List<FinancialAnalyticHighlight> = emptyList(),
    val isLoadingFinancialHighlights: Boolean = false,
    val financialHighlightsError: String? = null,

    val moderationQueue: List<ContentModerationItem> = emptyList(),
    val isLoadingModerationQueue: Boolean = false,
    val moderationQueueError: String? = null
)

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val systemRepository: AdminSystemRepository,
    private val userRepository: AdminUserRepository,
    private val financialRepository: AdminFinancialRepository,
    private val moderationRepository: AdminContentModerationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminHomeUiState())
    val uiState: StateFlow<AdminHomeUiState> = _uiState.asStateFlow()

    init {
        fetchSystemMetrics()
        fetchUserManagementSummary()
        fetchFinancialHighlights()
        fetchModerationQueue()
    }

    fun fetchSystemMetrics() {
        viewModelScope.launch {
            systemRepository.getCurrentSystemMetrics()
                .onStart { _uiState.value = _uiState.value.copy(isLoadingSystemMetrics = true, systemMetricsError = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoadingSystemMetrics = false, systemMetricsError = e.message) }
                .collect { data -> _uiState.value = _uiState.value.copy(isLoadingSystemMetrics = false, systemMetrics = data) }
        }
    }

    fun fetchUserManagementSummary() {
        viewModelScope.launch {
            userRepository.getUserManagementSummary()
                .onStart { _uiState.value = _uiState.value.copy(isLoadingUserManagementInfo = true, userManagementInfoError = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoadingUserManagementInfo = false, userManagementInfoError = e.message) }
                .collect { data -> _uiState.value = _uiState.value.copy(isLoadingUserManagementInfo = false, userManagementInfo = data) }
        }
    }

    fun fetchFinancialHighlights() {
        viewModelScope.launch {
            financialRepository.getFinancialHighlights()
                .onStart { _uiState.value = _uiState.value.copy(isLoadingFinancialHighlights = true, financialHighlightsError = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoadingFinancialHighlights = false, financialHighlightsError = e.message) }
                .collect { data -> _uiState.value = _uiState.value.copy(isLoadingFinancialHighlights = false, financialHighlights = data) }
        }
    }

    fun fetchModerationQueue(count: Int = 5) { // Fetch a small number for home screen summary
        viewModelScope.launch {
            moderationRepository.getPendingModerationItems(count)
                .onStart { _uiState.value = _uiState.value.copy(isLoadingModerationQueue = true, moderationQueueError = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoadingModerationQueue = false, moderationQueueError = e.message) }
                .collect { data -> _uiState.value = _uiState.value.copy(isLoadingModerationQueue = false, moderationQueue = data) }
        }
    }
}
