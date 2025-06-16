package com.example.rooster.feature.farm.ui.vaccination

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.feature.farm.domain.model.VaccinationRecord
import com.example.rooster.feature.farm.domain.usecase.GetVaccinationRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class VaccinationViewModel @Inject constructor(
    private val getVaccinationRecords: GetVaccinationRecordsUseCase
) : ViewModel() {

    private val _records = MutableStateFlow<List<VaccinationRecord>>(emptyList())
    val records: StateFlow<List<VaccinationRecord>> = _records

    fun loadRecords(fowlId: String) {
        getVaccinationRecords(fowlId)
            .onEach { result -> if (result.isSuccess) _records.value = result.getOrThrow() }
            .launchIn(viewModelScope)
    }
}