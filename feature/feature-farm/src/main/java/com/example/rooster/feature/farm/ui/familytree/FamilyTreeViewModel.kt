package com.example.rooster.feature.farm.ui.familytree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.feature.farm.domain.model.Flock
import com.example.rooster.feature.farm.domain.usecase.GetFamilyTreeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FamilyTreeViewModel @Inject constructor(
    private val getFamilyTree: GetFamilyTreeUseCase
) : ViewModel() {

    private val _ancestors = MutableStateFlow<List<Flock>>(emptyList())
    val ancestors: StateFlow<List<Flock>> = _ancestors

    fun loadFamilyTree(fowlId: String) {
        getFamilyTree(fowlId)
            .onEach { result -> if (result.isSuccess) _ancestors.value = result.getOrThrow() }
            .launchIn(viewModelScope)
    }
}