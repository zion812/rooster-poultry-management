package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.data.EventRepository
import com.example.rooster.models.ParseEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val _events = MutableStateFlow<List<ParseEvent>>(emptyList())
    val events: StateFlow<List<ParseEvent>> = _events
    override fun onCleared() {
        super.onCleared()
        // Clear state flows to prevent memory leaks
    }


    fun loadEvents() =
        viewModelScope.launch {
            try {
                _events.value = EventRepository.fetchEvents()
            } catch (_: Exception) {
            }
        }

    fun joinEvent(eventId: String) =
        viewModelScope.launch {
            try {
                EventRepository.joinEvent(eventId)
                _events.value = EventRepository.fetchEvents()
            } catch (_: Exception) {
            }
        }

    fun voteEvent(eventId: String) =
        viewModelScope.launch {
            try {
                EventRepository.voteEvent(eventId)
                // no data change for voting stub
            } catch (_: Exception) {
            }
        }
}
