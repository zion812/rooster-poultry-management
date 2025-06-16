package com.example.rooster.feature.farm.ui.updates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.feature.farm.domain.model.UpdateRecord
import com.example.rooster.feature.farm.domain.usecase.DeleteUpdateRecordUseCase
import com.example.rooster.feature.farm.domain.usecase.GetUpdateRecordsUseCase
import com.example.rooster.feature.farm.domain.usecase.SaveUpdateRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val getUpdateRecords: GetUpdateRecordsUseCase,
    private val saveUpdateRecords: SaveUpdateRecordsUseCase,
    private val deleteUpdateRecord: DeleteUpdateRecordUseCase
) : ViewModel() {

    private val _records = MutableStateFlow<List<UpdateRecord>>(emptyList())
    val records: StateFlow<List<UpdateRecord>> = _records

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadUpdates(fowlId: String) {
        getUpdateRecords(fowlId)
            .onEach { result ->
                if (result.isSuccess) _records.value = result.getOrThrow()
                else _error.value = result.exceptionOrNull()?.localizedMessage
            }
            .launchIn(viewModelScope)
    }

    fun addUpdate(
        fowlId: String,
        type: com.example.rooster.feature.farm.domain.model.UpdateType,
        date: Date,
        details: String,
        attachmentUrl: String?
    ) {
        val record = UpdateRecord(
            id = UUID.randomUUID().toString(),
            fowlId = fowlId,
            type = type,
            date = date,
            details = details,
            attachmentUrl = attachmentUrl
        )
        viewModelScope.launch {
            val res = saveUpdateRecords(listOf(record))
            if (res.isFailure) _error.value = res.exceptionOrNull()?.localizedMessage
            loadUpdates(fowlId)
        }
    }

    fun removeUpdate(id: String, fowlId: String) {
        viewModelScope.launch {
            val res = deleteUpdateRecord(id)
            if (res.isFailure) _error.value = res.exceptionOrNull()?.localizedMessage
            loadUpdates(fowlId)
        }
    }
}