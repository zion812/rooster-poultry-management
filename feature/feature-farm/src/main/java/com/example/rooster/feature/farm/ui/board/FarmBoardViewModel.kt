package com.example.rooster.feature.farm.ui.board

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.rooster.core.common.toUserFriendlyMessage
import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.domain.model.FlockType
import com.example.rooster.feature.farm.domain.usecase.GetFlocksByTypeUseCase
import com.example.rooster.feature.farm.data.repository.FarmRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.getOrNull
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.collect
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
    val uiState: StateFlow<FarmBoardUiState> = _uiState

    private val _syncFailedFlocks = MutableStateFlow<List<Flock>>(emptyList())
    val syncFailedFlocks: StateFlow<List<Flock>> = _syncFailedFlocks.asStateFlow()

    init {
        // Observe permanently failed syncs and expose to UI
        CoroutineScope(Dispatchers.IO).launch {
            farmRepository.getSyncFailedFlocks().collect {
                _syncFailedFlocks.value = it
            }
        }
    }

    fun loadBoard(farmId: String, context: Context) {
        _uiState.value = FarmBoardUiState.Loading
        try {
            // Parallel loading for all types
            GlobalScope.launch {
                try {
                    val fowls = getFlocksByType(FlockType.FOWL.name).firstOrNull()?.getOrNull() ?: emptyList()
                    val hens = getFlocksByType(FlockType.HEN.name).firstOrNull()?.getOrNull() ?: emptyList()
                    val breeders = getFlocksByType(FlockType.BREEDER.name).firstOrNull()?.getOrNull() ?: emptyList()
                    val chicks = getFlocksByType(FlockType.CHICK.name).firstOrNull()?.getOrNull() ?: emptyList()
                    _uiState.value = FarmBoardUiState.Success(fowls, hens, breeders, chicks)
                } catch (e: Exception) {
                    val msg = toUserFriendlyMessage(e, context)
                    _uiState.value = FarmBoardUiState.Error(msg)
                }
            }
        } catch (e: Exception) {
            val msg = toUserFriendlyMessage(e, context)
            _uiState.value = FarmBoardUiState.Error(msg)
        }
    }
}