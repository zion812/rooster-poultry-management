package com.example.rooster.feature.farm.ui.growth

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
import java.text.SimpleDateFormat
import java.util.*

@HiltViewModel
class GrowthViewModel @Inject constructor(
    private val getAllSensorData: GetAllSensorDataUseCase
) : ViewModel() {

    private val _growthData = MutableStateFlow<Map<String, List<Pair<String, Double>>>>(emptyMap())
    val growthData: StateFlow<Map<String, List<Pair<String, Double>>>> = _growthData

    private val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    fun loadGrowthMetrics() {
        getAllSensorData()
            .onEach { result ->
                if (result.isSuccess) {
                    val list = result.getOrThrow()

                    // Convert sensor data to chart-friendly format
                    val dataMap = mutableMapOf<String, List<Pair<String, Double>>>()

                    // Group temperature data
                    val temperatureData = list.mapNotNull { sensor ->
                        sensor.temperature?.let { temp ->
                            dateFormat.format(sensor.timestamp) to temp.toDouble()
                        }
                    }
                    if (temperatureData.isNotEmpty()) {
                        dataMap["Temperature"] = temperatureData
                    }

                    // Group humidity data
                    val humidityData = list.mapNotNull { sensor ->
                        sensor.humidity?.let { humidity ->
                            dateFormat.format(sensor.timestamp) to humidity.toDouble()
                        }
                    }
                    if (humidityData.isNotEmpty()) {
                        dataMap["Humidity"] = humidityData
                    }

                    _growthData.value = dataMap
                }
            }
            .launchIn(viewModelScope)
    }
}
