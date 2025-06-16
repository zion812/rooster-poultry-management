package com.example.rooster.feature.farm.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.domain.usecase.GetFarmDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FarmDetailsViewModel @Inject constructor(
    private val getFarmDetails: GetFarmDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FarmDetailsUiState>(FarmDetailsUiState.Loading)
    val uiState: StateFlow<FarmDetailsUiState> = _uiState

    fun loadDetails(farmId: String) {
        getFarmDetails(farmId)
            .onEach { result ->
                _uiState.value = when {
                    result.isSuccess -> {
                        FarmDetailsUiState.Success(result.getOrThrow())
                    }

                    result.isFailure -> {
                        FarmDetailsUiState.Error(
                            result.exceptionOrNull()?.message ?: "Unknown Error"
                        )
                    }

                    else -> FarmDetailsUiState.Loading
                }
            }
            .launchIn(viewModelScope)
    }
}

sealed interface FarmDetailsUiState {
    object Loading : FarmDetailsUiState
    data class Success(val flock: Flock) : FarmDetailsUiState
    data class Error(val message: String) : FarmDetailsUiState
}