package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.services.localization.AgriculturalContext
import com.example.rooster.services.localization.IntelligentLocalizationEngine
import com.example.rooster.services.optimized.IntelligentSearchFetcher
import com.example.rooster.services.optimized.SearchResult
import com.example.rooster.services.optimized.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceSearchViewModel
    @Inject
    constructor(
        private val intelligentSearchFetcher: IntelligentSearchFetcher,
        private val localizationEngine: IntelligentLocalizationEngine,
    ) : ViewModel() {
        // Search state
        private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
        val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

        // Voice search state
        private val _voiceSearchState = MutableStateFlow<VoiceSearchState>(VoiceSearchState.Idle)
        val voiceSearchState: StateFlow<VoiceSearchState> = _voiceSearchState.asStateFlow()

        // Suggestions
        private val _suggestions = MutableStateFlow<List<String>>(emptyList())
        val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

        // Selected context
        private val _selectedContext = MutableStateFlow(AgriculturalContext.GENERAL)
        val selectedContext: StateFlow<AgriculturalContext> = _selectedContext.asStateFlow()

        /**
         * Update search query and get suggestions
         */
        fun updateSearchQuery(
            query: String,
            language: String,
        ) {
            if (query.length >= 2) {
                viewModelScope.launch {
                    intelligentSearchFetcher.getPredictiveSuggestions(query, language)
                        .collect { suggestions ->
                            _suggestions.value = suggestions
                        }
                }
            } else {
                _suggestions.value = emptyList()
            }
        }

        /**
         * Execute semantic search
         */
        fun executeSemanticSearch(
            query: String,
            language: String,
        ) {
            _searchState.value = SearchState.Loading

            viewModelScope.launch {
                try {
                    intelligentSearchFetcher.semanticSearch(
                        query = query,
                        searchType = SearchType.ALL,
                        language = language,
                    ).collect { results ->
                        _searchState.value = SearchState.Success(results)
                    }
                } catch (e: Exception) {
                    _searchState.value = SearchState.Error(e.message ?: "Search failed")
                }
            }
        }

        /**
         * Start voice search
         */
        fun startVoiceSearch(language: String) {
            _voiceSearchState.value = VoiceSearchState.Recording

            viewModelScope.launch {
                try {
                    // Simulate voice recording - in real implementation, use speech recognition
                    kotlinx.coroutines.delay(3000)

                    val mockTranscript =
                        if (language == "te") {
                            "కోడిలకు వ్యాధి వచ్చింది"
                        } else {
                            "chickens have disease"
                        }

                    _voiceSearchState.value = VoiceSearchState.Success(mockTranscript)
                } catch (e: Exception) {
                    _voiceSearchState.value = VoiceSearchState.Error(e.message ?: "Voice search failed")
                }
            }
        }

        /**
         * Set search context
         */
        fun setSearchContext(context: AgriculturalContext) {
            _selectedContext.value = context
        }

        /**
         * Handle search result click
         */
        fun onSearchResultClick(result: SearchResult) {
            // In real implementation, navigate to result details
        }
    }

// Search States
sealed class SearchState {
    object Idle : SearchState()

    object Loading : SearchState()

    data class Success(val results: List<SearchResult>) : SearchState()

    data class Error(val message: String) : SearchState()
}

// Voice Search States
sealed class VoiceSearchState {
    object Idle : VoiceSearchState()

    object Recording : VoiceSearchState()

    data class Success(val transcript: String) : VoiceSearchState()

    data class Error(val message: String) : VoiceSearchState()
}
