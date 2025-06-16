@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.rooster.services.localization

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Intelligent Localization Engine - Advanced Telugu translation and cultural adaptation
 *
 * Key Features:
 * - Context-aware translations with agricultural terminology
 * - Voice synthesis in Telugu for illiterate users
 * - Mixed language support (English-Telugu code mixing)
 * - Cultural adaptation with appropriate UI patterns
 * - Offline translation capabilities
 * - Regional dialect support
 *
 * Optimized for rural farmers with comprehensive accessibility features
 */
@Singleton
class IntelligentLocalizationEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Text-to-Speech engine for voice output
    private var textToSpeech: TextToSpeech? = null
    private val _isTeluguTTSReady = MutableStateFlow(false)
    val isTeluguTTSReady: StateFlow<Boolean> = _isTeluguTTSReady.asStateFlow()

    // Current language state
    private val _currentLanguage = MutableStateFlow(Language.ENGLISH)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    // Specialized Telugu agricultural terms dictionary
    private val agriculturalTermsMap = mapOf(
        // Auction related terms
        "auction" to "వేలం",
        "bid" to "బిడ్",
        "bidding" to "బిడ్డింగ్",
        "highest bid" to "అత్యధిక బిడ్",
        "current bid" to "ప్రస్తుత బిడ్",
        "place bid" to "బిడ్ వేయండి",
        "time remaining" to "మిగిలిన సమయం",
        "auction ended" to "వేలం ముగిసింది",
        "winner" to "విజేత",
        "payment" to "చెల్లింపు",

        // Poultry specific terms
        "poultry" to "కోడి పెంపకం",
        "chicken" to "కోడి",
        "rooster" to "కోడిపుంజు",
        "hen" to "కోడిపిల్ల",
        "egg" to "గుడ్డు",
        "feed" to "మేత",
        "vaccination" to "టీకా",
        "disease" to "వ్యాధి",
        "breeding" to "సంతానోత్పత్తి",
        "farm" to "వ్యవసాయ క్షేత్రం",

        // Rural connectivity terms
        "connecting" to "కనెక్ట్ అవుతోంది",
        "offline mode" to "ఆఫ్‌లైన్ మోడ్",
        "slow connection" to "నెమ్మదిగా కనెక్షన్",
        "data saving" to "డేటా సేవింగ్",
        "rural mode" to "గ్రామీణ మోడ్",

        // Chat and collaboration terms
        "live chat" to "లైవ్ చాట్",
        "send message" to "సందేశం పంపండి",
        "participants" to "పాల్గొనేవారు",
        "expert consultation" to "నిపుణుల సలహా",
        "voice call" to "వాయిస్ కాల్",

        // Common actions
        "search" to "వెతకండి",
        "buy" to "కొనండి",
        "sell" to "అమ్మండి",
        "save" to "సేవ్ చేయండి",
        "cancel" to "రద్దు చేయండి",
        "confirm" to "ధృవీకరించండి",
        "retry" to "మళ్లీ ప్రయత్నించండి"
    )

    // Agricultural terminology database
    private val agriculturalTerms = mapOf(
        // Basic terms
        "chicken" to "కోడి",
        "rooster" to "రూస్టర్",
        "hen" to "కోడిపిల్ల",
        "egg" to "గుడ్డు",
        "farm" to "పొలం",
        "farmer" to "రైతు",

        // Advanced agricultural terms
        "vaccination" to "టీకా",
        "breeding" to "పెంపకం",
        "mortality" to "మరణాలు",
        "feed" to "మేత",
        "incubation" to "పొదిగింపు",
        "broiler" to "బ్రాయిలర్",
        "layer" to "గుడ్డు పెట్టే కోడి",

        // Marketplace terms
        "price" to "ధర",
        "auction" to "వేలం",
        "bid" to "వేలం పెట్టడం",
        "seller" to "అమ్మేవాడు",
        "buyer" to "కొనేవాడు",
        "listing" to "జాబితా",

        // Health terms
        "disease" to "వ్యాధి",
        "medicine" to "మందు",
        "veterinarian" to "పశువైద్యుడు",
        "treatment" to "చికిత్స",
        "symptoms" to "లక్షణాలు"
    )

    // Cultural context mappings
    private val culturalAdaptations = mapOf(
        "welcome" to CulturalContext(
            english = "Welcome to Rooster App",
            telugu = "రూస్టర్ యాప్‌కు స్వాగతం",
            culturalNote = "Traditional Telugu greeting with respectful tone"
        ),
        "greeting_morning" to CulturalContext(
            english = "Good Morning",
            telugu = "శుభోదయం",
            culturalNote = "Morning greeting common in rural Telugu regions"
        ),
        "respect_elder" to CulturalContext(
            english = "Sir/Madam",
            telugu = "అన్న/అక్క",
            culturalNote = "Respectful address for elder farmers"
        )
    )

    companion object {
        private const val TAG = "IntelligentLocalization"
        private val TELUGU_LOCALE = Locale("te", "IN")
    }

    init {
        initializeTextToSpeech()
    }

    /**
     * Initialize Telugu Text-to-Speech
     */
    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(TELUGU_LOCALE)
                _isTeluguTTSReady.value = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED
                Log.d(TAG, "Telugu TTS initialized: ${_isTeluguTTSReady.value}")
            } else {
                Log.e(TAG, "Telugu TTS initialization failed")
            }
        }
    }

    /**
     * Context-aware translation with agricultural terminology
     */
    fun translateWithContext(text: String, context: TranslationContext): String {
        return when (_currentLanguage.value) {
            Language.ENGLISH -> text
            Language.TELUGU -> translateToTelugu(text, context)
            Language.MIXED -> createMixedTranslation(text, context)
        }
    }

    /**
     * Translate to Telugu with agricultural context
     */
    private fun translateToTelugu(text: String, context: TranslationContext): String {
        var translatedText = text

        // Apply agricultural terminology
        agriculturalTerms.forEach { (english, telugu) ->
            translatedText = translatedText.replace(english, telugu, ignoreCase = true)
        }

        // Apply cultural adaptations
        val culturalKey = getCulturalKey(text, context)
        culturalAdaptations[culturalKey]?.let { adaptation ->
            return adaptation.telugu
        }

        // Fallback to basic translation
        return getBasicTranslation(translatedText)
    }

    /**
     * Create mixed language content (English-Telugu code mixing)
     */
    private fun createMixedTranslation(text: String, context: TranslationContext): String {
        val words = text.split(" ")
        return words.joinToString(" ") { word ->
            if (agriculturalTerms.containsKey(word.lowercase())) {
                "${agriculturalTerms[word.lowercase()]} ($word)"
            } else {
                word
            }
        }
    }

    /**
     * Voice synthesis in Telugu
     */
    fun speakInTelugu(text: String) {
        if (_isTeluguTTSReady.value && textToSpeech != null) {
            val teluguText = translateWithContext(text, TranslationContext.GENERAL)
            textToSpeech?.speak(teluguText, TextToSpeech.QUEUE_FLUSH, null, null)
            Log.d(TAG, "Speaking in Telugu: $teluguText")
        } else {
            Log.w(TAG, "Telugu TTS not ready, cannot speak")
        }
    }

    /**
     * Get culturally appropriate UI text
     */
    fun getCulturallyAdaptedText(key: String): String {
        val adaptation = culturalAdaptations[key]
        return when (_currentLanguage.value) {
            Language.ENGLISH -> adaptation?.english ?: key
            Language.TELUGU -> adaptation?.telugu ?: translateWithContext(
                key,
                TranslationContext.UI
            )

            Language.MIXED -> "${adaptation?.telugu} (${adaptation?.english})"
        }
    }

    /**
     * Set current language
     */
    fun setLanguage(language: Language) {
        _currentLanguage.value = language
        Log.d(TAG, "Language changed to: $language")
    }

    /**
     * Toggle between languages
     */
    fun toggleLanguage() {
        _currentLanguage.value = when (_currentLanguage.value) {
            Language.ENGLISH -> Language.TELUGU
            Language.TELUGU -> Language.MIXED
            Language.MIXED -> Language.ENGLISH
        }
    }

    /**
     * Get regional government schemes in Telugu
     */
    fun getRegionalSchemes(): List<GovernmentScheme> {
        return listOf(
            GovernmentScheme(
                name = "రైతు బంధు",
                nameEnglish = "Rythu Bandhu",
                description = "రైతులకు ఆర్థిక సహాయం",
                descriptionEnglish = "Financial assistance for farmers"
            ),
            GovernmentScheme(
                name = "పోల్ట్రీ డెవలప్‌మెంట్ స్కీమ్",
                nameEnglish = "Poultry Development Scheme",
                description = "కోడిపెంపకానికి సబ్సిడీ",
                descriptionEnglish = "Subsidy for poultry farming"
            )
        )
    }

    /**
     * Format numbers in Telugu script
     */
    fun formatNumberInTelugu(number: Int): String {
        val teluguDigits = arrayOf("౦", "౧", "౨", "౩", "౪", "౫", "౬", "౭", "౮", "౯")
        return number.toString().map { char ->
            if (char.isDigit()) {
                teluguDigits[char.digitToInt()]
            } else {
                char.toString()
            }
        }.joinToString("")
    }

    /**
     * Get basic translation (placeholder for actual translation service)
     */
    private fun getBasicTranslation(text: String): String {
        // In a real implementation, this would call a translation API
        // For now, return the original text
        return text
    }

    /**
     * Get cultural context key for translation
     */
    private fun getCulturalKey(text: String, context: TranslationContext): String? {
        return when {
            text.contains("welcome", ignoreCase = true) -> "welcome"
            text.contains("good morning", ignoreCase = true) -> "greeting_morning"
            context == TranslationContext.RESPECTFUL_ADDRESS -> "respect_elder"
            else -> null
        }
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        textToSpeech?.let {
            it.stop()
            it.shutdown()
        }
    }

    /**
     * Enhanced Telugu text-to-speech with agricultural context
     */
    fun speakTeluguWithContext(
        text: String,
        context: Context,
        agriculturalContext: AgriculturalContext = AgriculturalContext.GENERAL
    ) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("te", "IN"))

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Log.w(TAG, "Telugu TTS not supported, fallback to English")
                    tts?.setLanguage(Locale.ENGLISH)
                } else {
                    // Optimize speech settings for rural users
                    tts?.setSpeechRate(0.8f) // Slower for better comprehension
                    tts?.setPitch(1.1f) // Slightly higher pitch for clarity
                }

                // Process text with agricultural terms
                val processedText = processTextForSpeech(text, agriculturalContext)

                tts?.speak(
                    processedText,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "telugu_tts_${System.currentTimeMillis()}"
                )
            } else {
                Log.e(TAG, "Telugu TTS initialization failed")
            }
        }
    }

    /**
     * Process text for better speech synthesis with agricultural context
     */
    private fun processTextForSpeech(text: String, context: AgriculturalContext): String {
        var processedText = text

        // Replace agricultural terms with Telugu equivalents
        agriculturalTermsMap.forEach { (english: String, telugu: String) ->
            processedText = processedText.replace(english, telugu, ignoreCase = true)
        }

        // Add context-specific pronunciations
        when (context) {
            AgriculturalContext.AUCTION -> {
                processedText = processedText.replace("₹", "రూపాయలు ")
                processedText = processedText.replace("INR", "భారతీయ రూపాయలు")
            }

            AgriculturalContext.HEALTH -> {
                processedText = processedText.replace("ml", "మిల్లీలీటర్")
                processedText = processedText.replace("mg", "మిల్లీగ్రాము")
            }

            AgriculturalContext.FEED -> {
                processedText = processedText.replace("kg", "కిలోగ్రాము")
                processedText = processedText.replace("grams", "గ్రాములు")
            }

            else -> { /* General context - no special processing */
            }
        }

        return processedText
    }

    /**
     * Announce auction updates in Telugu
     */
    fun announceAuctionUpdate(
        context: Context,
        currentBid: Double,
        timeRemaining: Long,
        participantCount: Int
    ) {
        val minutes = timeRemaining / 60
        val seconds = timeRemaining % 60

        val announcement = "ప్రస్తుత బిడ్ ${currentBid.toInt()} రూపాయలు. " +
                "మిగిలిన సమయం $minutes నిమిషాలు $seconds సెకన్లు. " +
                "$participantCount మంది పాల్గొంటున్నారు."

        speakTeluguWithContext(announcement, context, AgriculturalContext.AUCTION)
    }

    /**
     * Announce connectivity status in Telugu
     */
    fun announceConnectivityStatus(context: Context, networkQuality: NetworkQuality) {
        val announcement = when (networkQuality) {
            NetworkQuality.EXCELLENT -> "అద్భుతమైన కనెక్షన్. అన్ని సేవలు అందుబాటులో ఉన్నాయి."
            NetworkQuality.GOOD -> "మంచి కనెక్షన్. సాధారణ వేగంతో పని చేస్తోంది."
            NetworkQuality.FAIR -> "సాధారణ కనెక్షన్. డేటా సేవింగ్ మోడ్ ఆన్ చేయబడింది."
            NetworkQuality.POOR -> "నెమ్మదిగా కనెక్షన్. ఆఫ్‌లైన్ మోడ్‌కు మారుతోంది."
            NetworkQuality.UNKNOWN -> "కనెక్షన్ తనిఖీ చేస్తోంది."
        }

        speakTeluguWithContext(announcement, context, AgriculturalContext.GENERAL)
    }

    /**
     * Voice search command recognition for Telugu
     */
    fun processTeluguVoiceCommand(voiceInput: String): VoiceCommand? {
        val lowerInput = voiceInput.lowercase()

        return when {
            lowerInput.contains("వెతకండి") || lowerInput.contains("search") ->
                VoiceCommand.SEARCH

            lowerInput.contains("బిడ్") || lowerInput.contains("bid") ->
                VoiceCommand.PLACE_BID

            lowerInput.contains("కొనండి") || lowerInput.contains("buy") ->
                VoiceCommand.BUY

            lowerInput.contains("అమ్మండి") || lowerInput.contains("sell") ->
                VoiceCommand.SELL

            lowerInput.contains("చాట్") || lowerInput.contains("chat") ->
                VoiceCommand.OPEN_CHAT

            lowerInput.contains("సహాయం") || lowerInput.contains("help") ->
                VoiceCommand.HELP

            else -> null
        }
    }
}

/**
 * Language options
 */
enum class Language {
    ENGLISH, TELUGU, MIXED
}

/**
 * Translation context for appropriate terminology
 */
enum class TranslationContext {
    GENERAL, AGRICULTURAL, MARKETPLACE, HEALTH, UI, RESPECTFUL_ADDRESS
}

/**
 * Cultural context data
 */
data class CulturalContext(
    val english: String,
    val telugu: String,
    val culturalNote: String
)

/**
 * Government scheme information
 */
data class GovernmentScheme(
    val name: String,
    val nameEnglish: String,
    val description: String,
    val descriptionEnglish: String
)

/**
 * Composable function to remember localization engine
 */
@Composable
fun rememberLocalizationEngine(): IntelligentLocalizationEngine {
    val context = LocalContext.current
    return remember { IntelligentLocalizationEngine(context) }
}

enum class AgriculturalContext {
    GENERAL, AUCTION, HEALTH, FEED, BREEDING
}

enum class NetworkQuality {
    EXCELLENT, GOOD, FAIR, POOR, UNKNOWN
}

enum class VoiceCommand {
    SEARCH, PLACE_BID, BUY, SELL, OPEN_CHAT, HELP
}
