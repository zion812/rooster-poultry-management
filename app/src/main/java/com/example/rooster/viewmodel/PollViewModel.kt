package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.data.PollRepository
import com.example.rooster.models.PollResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PollViewModel : ViewModel() {
    private val _results = MutableStateFlow<Map<String, Int>>(emptyMap())
    val results: StateFlow<Map<String, Int>> = _results

    fun loadResults(pollId: String) =
        viewModelScope.launch {
            try {
                _results.value = PollRepository.fetchResults(pollId)
            } catch (_: Exception) {
            }
        }

    fun submitVote(response: PollResponse) =
        viewModelScope.launch {
            try {
                PollRepository.submitPollResponse(response)
                _results.value = PollRepository.fetchResults(response.pollId)
            } catch (_: Exception) {
            }
        }
}
