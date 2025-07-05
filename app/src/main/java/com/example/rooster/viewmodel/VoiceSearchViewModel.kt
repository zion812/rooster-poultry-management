package com.example.rooster.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.services.localization.AgriculturalContext
import com.example.rooster.services.localization.IntelligentLocalizationEngine
import com.example.rooster.services.optimized.IntelligentSearchFetcher
import com.example.rooster.services.optimized.SearchResult
import com.example.rooster.services.optimized.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
        @ApplicationContext private val context: Context,
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

        // Location filtering
        private val _selectedDistrict = MutableStateFlow<String?>(null)
        val selectedDistrict: StateFlow<String?> = _selectedDistrict.asStateFlow()

        // Speech recognizer
        private var speechRecognizer: SpeechRecognizer? = null

        // Telugu districts for location filtering
        private val teluguDistricts =
            listOf(
                "విజయవాడ", "హైదరాబాద్", "వరంగల్", "నెల్లూర్", "కర్నూల్",
                "గుంటూర్", "రాజమహేంద్రవరం", "తిరుపతి", "విశాఖపట్నం", "చిత్తూర్",
                "అనంతపురం", "కడప", "ప్రకాశం", "నిజాంబాద్", "మహబూబ్‌నగర్",
                "రంగారెడ్డి", "మేడక్", "జగిత్యాల", "సిరిసిల్ల", "కామారెడ్డి",
            )

        /**
         * Update search query and get suggestions
         */
        fun updateSearchQuery(
            query: String,
            language: String,
        ) {
            if (query.length >= 2) {
                viewModelScope.launch {
                    // Get localized suggestions based on district
                    val localizedSuggestions =
                        getLocalizedSuggestions(query, language, _selectedDistrict.value)
                    _suggestions.value = localizedSuggestions

                    intelligentSearchFetcher.getPredictiveSuggestions(query, language)
                        .collect { suggestions ->
                            _suggestions.value = suggestions + localizedSuggestions
                        }
                }
            } else {
                _suggestions.value = emptyList()
            }
        }

        /**
         * Execute semantic search with location filtering
         */
        fun executeSemanticSearch(
            query: String,
            language: String,
        ) {
            _searchState.value = SearchState.Loading

            viewModelScope.launch {
                try {
                    // Enhance query with district context if selected
                    val enhancedQuery =
                        _selectedDistrict.value?.let { district ->
                            "$query $district రైతులకు"
                        } ?: query

                    intelligentSearchFetcher.semanticSearch(
                        query = enhancedQuery,
                        searchType = SearchType.ALL,
                        language = language,
                    ).collect { results ->
                        // Filter results by district if selected
                        val filteredResults =
                            filterResultsByDistrict(results, _selectedDistrict.value)
                        _searchState.value = SearchState.Success(filteredResults)
                    }
                } catch (e: Exception) {
                    _searchState.value = SearchState.Error(e.message ?: "వెతుకులు విఫలమైంది")
                }
            }
        }

        /**
         * Start real Android voice search
         */
        fun startVoiceSearch(language: String) {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                _voiceSearchState.value =
                    VoiceSearchState.Error("వాయిస్ రికగ్నిషన్ అందుబాటులో లేదు")
                return
            }

            _voiceSearchState.value = VoiceSearchState.Recording

            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

                val intent =
                    Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                        )
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE,
                            if (language == "te") "te-IN" else "en-US",
                        )
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                            if (language == "te") "te-IN" else "en-US",
                        )
                        putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
                        putExtra(
                            RecognizerIntent.EXTRA_PROMPT,
                            if (language == "te") "మాట్లాడండి..." else "Speak now...",
                        )
                        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    }

                speechRecognizer?.setRecognitionListener(
                    object :
                        android.speech.RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            _voiceSearchState.value = VoiceSearchState.Recording
                        }

                        override fun onBeginningOfSpeech() {}

                        override fun onRmsChanged(rmsdB: Float) {}

                        override fun onBufferReceived(buffer: ByteArray?) {}

                        override fun onEndOfSpeech() {}

                        override fun onError(error: Int) {
                            val errorMessage =
                                when (error) {
                                    SpeechRecognizer.ERROR_AUDIO -> "ఆడియో లోపం"
                                    SpeechRecognizer.ERROR_CLIENT -> "క్లయింట్ లోపం"
                                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "అనుమతులు లేవు"
                                    SpeechRecognizer.ERROR_NETWORK -> "నెట్‌వర్క్ లోపం"
                                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "నెట్‌వర్క్ టైమ్‌అవుట్"
                                    SpeechRecognizer.ERROR_NO_MATCH -> "మ్యాచ్ కనుగొనబడలేదు"
                                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "రికగ్నైజర్ బిజీగా ఉంది"
                                    SpeechRecognizer.ERROR_SERVER -> "సర్వర్ లోపం"
                                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "స్పీచ్ టైమ్‌అవుట్"
                                    else -> "వాయిస్ రికగ్నిషన్ లోపం"
                                }
                            _voiceSearchState.value = VoiceSearchState.Error(errorMessage)
                        }

                        override fun onResults(results: Bundle?) {
                            val matches =
                                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            if (!matches.isNullOrEmpty()) {
                                val bestMatch = matches[0]
                                _voiceSearchState.value = VoiceSearchState.Success(bestMatch)
                            } else {
                                _voiceSearchState.value = VoiceSearchState.Error("ఏమీ వినిపించలేదు")
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            // Handle partial results if needed
                        }

                        override fun onEvent(
                            eventType: Int,
                            params: Bundle?,
                        ) {}
                    },
                )

                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                _voiceSearchState.value =
                    VoiceSearchState.Error("వాయిస్ రికగ్నిషన్ ప్రారంభించడంలో లోపం: ${e.message}")
            }
        }

        /**
         * Stop voice search
         */
        fun stopVoiceSearch() {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
            _voiceSearchState.value = VoiceSearchState.Idle
        }

        /**
         * Set district for location-based filtering
         */
        fun setSelectedDistrict(district: String?) {
            _selectedDistrict.value = district
            // Re-run search if there's an active query
            if (_searchState.value is SearchState.Success) {
                // Trigger re-search with new district filter
                val currentQuery =
                    (_searchState.value as? SearchState.Success)?.results?.firstOrNull()?.title ?: ""
                if (currentQuery.isNotEmpty()) {
                    executeSemanticSearch(currentQuery, "te")
                }
            }
        }

        /**
         * Get available Telugu districts
         */
        fun getTeluguDistricts(): List<String> = teluguDistricts

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
            // Could also track analytics here
        }

        /**
         * Get AI-powered recommendations based on district and context
         */
        fun getRecommendations(): List<SearchResult> {
            val district = _selectedDistrict.value
            val context = _selectedContext.value

            return when (context) {
                AgriculturalContext.HEALTH -> getHealthRecommendations(district)
                AgriculturalContext.FEED -> getFeedRecommendations(district)
                AgriculturalContext.BREEDING -> getBreedingRecommendations(district)
                else -> getGeneralRecommendations(district)
            }
        }

        // Private helper methods
        private fun getLocalizedSuggestions(
            query: String,
            language: String,
            district: String?,
        ): List<String> {
            val baseSuggestions =
                if (language == "te") {
                    listOf(
                        "కోడి వ్యాధులు",
                        "మేత ధరలు",
                        "నాట్టు కోడి",
                        "కడక్నాథ్ కోడి",
                        "గిరిరాజ కోడి",
                        "వ్యాక్సినేషన్",
                        "కోడిగుడ్లు",
                        "పోల్ట్రీ ఫీడ్",
                    )
                } else {
                    listOf(
                        "chicken diseases",
                        "feed prices",
                        "desi chicken",
                        "kadaknath chicken",
                        "giriraj chicken",
                        "vaccination",
                        "eggs",
                        "poultry feed",
                    )
                }

            return baseSuggestions.filter {
                it.contains(query, ignoreCase = true)
            }.take(5)
        }

        private fun filterResultsByDistrict(
            results: List<SearchResult>,
            district: String?,
        ): List<SearchResult> {
            if (district == null) return results

            return results.map { result ->
                // Boost relevance for district-specific results
                if (result.description.contains(district) || result.tags.contains(district.lowercase())) {
                    result.copy(relevanceScore = result.relevanceScore * 1.2f)
                } else {
                    result
                }
            }.sortedByDescending { it.relevanceScore }
        }

        private fun getHealthRecommendations(district: String?): List<SearchResult> {
            return listOf(
                SearchResult(
                    id = "health_1",
                    title = if (district != null) "$district లో కోడి వ్యాధుల నివారణ" else "కోడి వ్యాధుల నివారణ",
                    description = "సాధారణ కోడి వ్యాధులను ఎలా నివారించాలి",
                    type = com.example.rooster.services.optimized.ResultType.GUIDE,
                    relevanceScore = 0.95f,
                    tags = listOf("ఆరోగ్యం", "నివారణ", district?.lowercase() ?: "సాధారణ"),
                ),
            )
        }

        private fun getFeedRecommendations(district: String?): List<SearchResult> {
            return listOf(
                SearchResult(
                    id = "feed_1",
                    title = if (district != null) "$district లో కోడి మేత ధరలు" else "కోడి మేత ధరలు",
                    description = "నాణయమైన కోడి మేత మరియు ధరల సమాచారం",
                    type = com.example.rooster.services.optimized.ResultType.MARKETPLACE_ITEM,
                    relevanceScore = 0.90f,
                    tags = listOf("మేత", "ధరలు", district?.lowercase() ?: "సాధారణ"),
                ),
            )
        }

        private fun getBreedingRecommendations(district: String?): List<SearchResult> {
            return listOf(
                SearchResult(
                    id = "breeding_1",
                    title = if (district != null) "$district లో కోడి సంతానోత్పత్తి" else "కోడి సంతానోత్పత్తి",
                    description = "మంచి కోడి జాతుల సంతానోత్పత్తి మార్గదర్శకాలు",
                    type = com.example.rooster.services.optimized.ResultType.GUIDE,
                    relevanceScore = 0.85f,
                    tags = listOf("సంతానోత్పత్తి", "జాతులు", district?.lowercase() ?: "సాధారణ"),
                ),
            )
        }

        private fun getGeneralRecommendations(district: String?): List<SearchResult> {
            return listOf(
                SearchResult(
                    id = "general_1",
                    title = if (district != null) "$district లో కోడి పెంపకం" else "కోడి పెంపకం",
                    description = "కోడి పెంపకం యొక్క మొత్తం గైడ్",
                    type = com.example.rooster.services.optimized.ResultType.ARTICLE,
                    relevanceScore = 0.80f,
                    tags = listOf("పెంపకం", "గైడ్", district?.lowercase() ?: "సాధారణ"),
                ),
            )
        }

        override fun onCleared() {
            super.onCleared()
            stopVoiceSearch()
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
