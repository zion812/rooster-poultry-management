package com.example.rooster.feature.farm.ui.lineage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.feature.farm.domain.model.LineageInfo
import com.example.rooster.feature.farm.data.repository.FarmRepository // Corrected import if repository is in data layer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

sealed interface LineageUiState {
    data object Loading : LineageUiState
    data class Success(val lineageInfo: LineageInfo) : LineageUiState
    data class Error(val message: String) : LineageUiState
    data object NoLineageData : LineageUiState // For when lineage info is null but no error
}

@HiltViewModel
class FlockLineageViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<LineageUiState>(LineageUiState.Loading)
    val uiState: StateFlow<LineageUiState> = _uiState.asStateFlow()

    private val flockId: String = savedStateHandle.get<String>("flockId") ?: ""
    private val depthUp: Int = savedStateHandle.get<Int>("depthUp") ?: 2
    private val depthDown: Int = savedStateHandle.get<Int>("depthDown") ?: 1


    init {
        if (flockId.isNotBlank()) {
            fetchLineage()
        } else {
            _uiState.value = LineageUiState.Error("Flock ID not provided for lineage.")
        }
    }

    fun fetchLineage(forceRefresh: Boolean = false) { // forceRefresh might not be applicable if lineage is always re-calculated
        if (flockId.isBlank()) return

        farmRepository.getLineageInfo(flockId, depthUp, depthDown)
            .onEach { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> LineageUiState.Loading
                    is Result.Success -> {
                        result.data?.let { info -> LineageUiState.Success(info) }
                            ?: LineageUiState.NoLineageData // Or Error("Lineage data not found")
                    }
                    is Result.Error -> LineageUiState.Error(result.exception.message ?: "Unknown error fetching lineage")
                }
            }.launchIn(viewModelScope)
    }
}
