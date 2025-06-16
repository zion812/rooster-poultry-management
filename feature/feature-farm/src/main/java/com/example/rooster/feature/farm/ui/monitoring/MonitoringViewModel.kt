package com.example.rooster.feature.farm.ui.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.feature.farm.domain.model.SensorData
import com.example.rooster.feature.farm.domain.usecase.GetAllSensorDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val getAllSensorData: GetAllSensorDataUseCase
) : ViewModel() {

    private val _sensorData = MutableStateFlow<List<SensorData>>(emptyList())
    val sensorData: StateFlow<List<SensorData>> = _sensorData

    fun loadSensorData() {
        getAllSensorData()
            .onEach { result ->
                if (result.isSuccess) _sensorData.value = result.getOrThrow()
            }
            .launchIn(viewModelScope)
    }
}