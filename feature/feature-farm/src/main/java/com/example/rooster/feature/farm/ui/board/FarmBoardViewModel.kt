package com.example.rooster.feature.farm.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.domain.model.FlockType
import com.example.rooster.feature.farm.domain.usecase.GetFlocksByTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FarmBoardViewModel @Inject constructor(
    private val getFlocksByType: GetFlocksByTypeUseCase
) : ViewModel() {

    private val _fowls = MutableStateFlow<List<Flock>>(emptyList())
    val fowls: StateFlow<List<Flock>> = _fowls

    private val _hens = MutableStateFlow<List<Flock>>(emptyList())
    val hens: StateFlow<List<Flock>> = _hens

    private val _breeders = MutableStateFlow<List<Flock>>(emptyList())
    val breeders: StateFlow<List<Flock>> = _breeders

    private val _chicks = MutableStateFlow<List<Flock>>(emptyList())
    val chicks: StateFlow<List<Flock>> = _chicks

    fun loadBoard(farmId: String) {
        // farmId currently unused; repository could filter by ownerId if implemented
        getFlocksByType(FlockType.FOWL.name)
            .onEach { result -> if (result.isSuccess) _fowls.value = result.getOrThrow() }
            .launchIn(viewModelScope)

        getFlocksByType(FlockType.HEN.name)
            .onEach { result -> if (result.isSuccess) _hens.value = result.getOrThrow() }
            .launchIn(viewModelScope)

        getFlocksByType(FlockType.BREEDER.name)
            .onEach { result -> if (result.isSuccess) _breeders.value = result.getOrThrow() }
            .launchIn(viewModelScope)

        getFlocksByType(FlockType.CHICK.name)
            .onEach { result -> if (result.isSuccess) _chicks.value = result.getOrThrow() }
            .launchIn(viewModelScope)
    }
}