package com.example.rooster.feature.farmdetails.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.R
import com.example.rooster.core.common.model.Farm
import com.example.rooster.core.common.model.Flock
import com.example.rooster.core.data.repository.FarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val FARM_ID_ARG = "farmId" // Navigation argument key

data class FarmDetailsUiState(
    val isLoadingFarm: Boolean = true,
    val isLoadingFlocks: Boolean = true,
    val farm: Farm? = null,
    val flocks: List<Flock> = emptyList(),
    val error: String? = null, // General error message for the screen
    val navigateToFlockManagementFarmId: String? = null // farmId to pass for flock management
) {
    val isLoading: Boolean get() = isLoadingFarm || isLoadingFlocks
}

@HiltViewModel
class FarmDetailsViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmDetailsUiState())
    val uiState: StateFlow<FarmDetailsUiState> = _uiState.asStateFlow()

    private val farmId: String? = savedStateHandle[FARM_ID_ARG]

    init {
        if (farmId == null || farmId == "{farmId}") { // Handle placeholder from NavHost if not replaced
            _uiState.update { it.copy(isLoadingFarm = false, isLoadingFlocks = false, error = "Farm ID not provided.") } // TODO: String resource
        } else {
            loadFarmDetailsAndFlocks(farmId)
        }
    }

    fun loadFarmDetailsAndFlocks(currentFarmId: String = farmId ?: "") {
        if (currentFarmId.isBlank() || currentFarmId == "{farmId}") {
             _uiState.update { it.copy(isLoadingFarm = false, isLoadingFlocks = false, error = "Invalid Farm ID.") } // TODO: String resource
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingFarm = true, isLoadingFlocks = true, error = null) }

            try {
                // Fetch farm details and flocks concurrently
                coroutineScope {
                    val farmDetailsResultDeferred = async { farmRepository.getFarmDetails(currentFarmId) }
                    val flocksResultDeferred = async { farmRepository.getFlocksForFarm(currentFarmId) }

                    val farmDetailsResult = farmDetailsResultDeferred.await()
                    val flocksResult = flocksResultDeferred.await()

                    var farmData: Farm? = null
                    var farmError: String? = null
                    var flocksData: List<Flock> = emptyList()
                    var flocksError: String? = null

                    farmDetailsResult.fold(
                        onSuccess = { farm -> farmData = farm },
                        onFailure = { e -> farmError = e.message ?: "Failed to load farm details." } // TODO: SR
                    )
                    flocksResult.fold(
                        onSuccess = {flocks -> flocksData = flocks },
                        onFailure = {e -> flocksError = e.message ?: "Failed to load flocks."} // TODO: SR
                    )

                    val combinedError = listOfNotNull(farmError, flocksError).joinToString("\n").ifBlank { null }

                    _uiState.update {
                        it.copy(
                            isLoadingFarm = false,
                            isLoadingFlocks = false,
                            farm = farmData,
                            flocks = flocksData,
                            error = combinedError
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingFarm = false,
                        isLoadingFlocks = false,
                        error = e.message ?: "An unexpected error occurred." // TODO: SR
                    )
                }
            }
        }
    }

    fun onManageFlocksClicked() {
        farmId?.let {
            _uiState.update { it.copy(navigateToFlockManagementFarmId = it) }
        }
    }

    fun navigationToFlockManagementComplete() {
        _uiState.update { it.copy(navigateToFlockManagementFarmId = null) }
    }
}
```

**Key aspects of `FarmDetailsViewModel.kt`:**

*   **`FARM_ID_ARG`:** Constant for the navigation argument key.
*   **`FarmDetailsUiState`:** Manages loading states for farm details and flocks separately, the `Farm` object, list of `Flock`s, error messages, and a navigation trigger for flock management.
*   **`SavedStateHandle`:** Used to retrieve the `farmId` passed via navigation.
*   **`init` block:** Checks for a valid `farmId` and calls `loadFarmDetailsAndFlocks`.
*   **`loadFarmDetailsAndFlocks()`:**
    *   Fetches farm details and its flocks concurrently using `async`.
    *   Updates the UI state with the fetched data or any errors encountered.
*   **Navigation Logic:** `onManageFlocksClicked()` and `navigationToFlockManagementComplete()` manage navigation state to a (future) full flock management screen.

This completes sub-step C. Next is implementing the UI in `FarmDetailsScreen.kt`.
