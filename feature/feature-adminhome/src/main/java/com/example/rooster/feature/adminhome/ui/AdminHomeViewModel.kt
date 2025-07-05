package com.example.rooster.feature.adminhome.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.auth.domain.repository.AuthRepository // To get current adminId if needed
import com.example.rooster.core.data.fetcher.AdminDashboardDataFetcher
import com.example.rooster.core.data.model.AdminDashboardSummary
import com.example.rooster.core.data.model.ContentModerationInfo
import com.example.rooster.core.data.model.SystemStat
import com.example.rooster.core.data.model.UserActivitySummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminHomeUiState(
    val isLoading: Boolean = true,
    val adminUserName: String = "",
    val systemStats: List<SystemStat> = emptyList(),
    val userActivity: UserActivitySummary? = null,
    val contentModeration: ContentModerationInfo? = null,
    val criticalAlerts: List<String> = emptyList(),
    val errorMessage: String? = null
    // Add navigation triggers if needed
)

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val adminDashboardDataFetcher: AdminDashboardDataFetcher,
    private val authRepository: AuthRepository // May or may not be needed depending on how adminId is passed
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminHomeUiState())
    val uiState: StateFlow<AdminHomeUiState> = _uiState.asStateFlow()

    init {
        loadAdminDashboardData()
    }

    fun loadAdminDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Admin ID might be globally available or passed, for now, passing null
            // as getDashboardSummary in fetcher takes adminId: String?
            val currentUser = authRepository.getCurrentUser().firstOrNull() // Example: get admin user details
            val adminId = currentUser?.id // Or a specific known admin ID if not generic

            val result = adminDashboardDataFetcher.getDashboardSummary(adminId)
            result.fold(
                onSuccess = { summary ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            adminUserName = summary.adminUserName,
                            systemStats = summary.systemStats,
                            userActivity = summary.userActivity,
                            contentModeration = summary.contentModeration,
                            criticalAlerts = summary.criticalAlerts
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to load admin dashboard data." // TODO: String resource
                        )
                    }
                }
            )
        }
    }
    // Placeholder for actions
    // fun onUserManagementClicked() { /* TODO: navigation */ }
    // fun onSystemAlertClicked(alertId: String) { /* TODO: navigation or dialog */ }
}
```

**Notes on `AdminHomeViewModel.kt`:**
*   Defines `AdminHomeUiState`.
*   Injects `AdminDashboardDataFetcher` and `AuthRepository`.
*   Loads dashboard data, passing the current user's ID (or null) as `adminId` to the fetcher.
*   Updates UI state based on success or failure.

Next, I'll create the UI for `AdminDashboardScreen.kt`.
