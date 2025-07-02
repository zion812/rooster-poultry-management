package com.example.rooster.feature.farm.ui.board

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.toUserFriendlyMessage
import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.domain.model.FlockType
import com.example.rooster.feature.farm.domain.usecase.GetFlocksByTypeUseCase
import com.example.rooster.feature.farm.data.repository.FarmRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmBoardViewModel @Inject constructor(
    private val getFlocksByType: GetFlocksByTypeUseCase,
    private val farmRepository: FarmRepositoryImpl
) : ViewModel() {

    sealed interface FarmBoardUiState {
        data object Loading : FarmBoardUiState
        data class Success(
            val fowls: List<Flock>,
            val hens: List<Flock>,
            val breeders: List<Flock>,
            val chicks: List<Flock>
        ) : FarmBoardUiState
        data class Error(val message: String) : FarmBoardUiState
    }

    private val _uiState = MutableStateFlow<FarmBoardUiState>(FarmBoardUiState.Loading)
    val uiState: StateFlow<FarmBoardUiState> = _uiState.asStateFlow()

    private val _syncFailedFlocks = MutableStateFlow<List<Flock>>(emptyList())
    val syncFailedFlocks: StateFlow<List<Flock>> = _syncFailedFlocks.asStateFlow()

    init {
        // Observe permanently failed syncs and expose to UI
        viewModelScope.launch(Dispatchers.IO) {
            farmRepository.getSyncFailedFlocks().collect {
                _syncFailedFlocks.value = it
            }
        }
    }

    fun loadBoard(farmId: String, context: Context) {
        _uiState.value = FarmBoardUiState.Loading
        viewModelScope.launch {
            try {
                // Parallel loading for all types is better handled with async/await, but for simplicity, sequential is fine for now.
                val fowls = getFlocksByType(FlockType.FOWL.name).firstOrNull() ?: emptyList<Flock>()
                val hens = getFlocksByType(FlockType.HEN.name).firstOrNull() ?: emptyList<Flock>()
                val breeders = getFlocksByType(FlockType.BREEDER.name).firstOrNull() ?: emptyList<Flock>()
                val chicks = getFlocksByType(FlockType.CHICK.name).firstOrNull() ?: emptyList<Flock>()
                _uiState.value = FarmBoardUiState.Success(fowls, hens, breeders, chicks)
            } catch (e: Exception) {
                val msg = e.toUserFriendlyMessage(context)
                _uiState.value = FarmBoardUiState.Error(msg)
            }
        }
    }
}