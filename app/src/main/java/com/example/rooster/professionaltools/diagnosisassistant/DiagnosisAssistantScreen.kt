package com.example.rooster.professionaltools.diagnosisassistant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

// --- Data Classes ---
data class PotentialCondition(
    val condition: String,
    val confidence: Double,
    val urgency: String,
    val nextSteps: String,
    val matchedSymptoms: List<String>
)

data class DiagnosticSessionResult(
    val sessionId: String,
    val species: String,
    val primarySymptoms: List<String>,
    val potentialConditions: List<PotentialCondition>,
    val timestamp: Long,
    val error: String? = null
)

data class ConditionDetails(
    val name: String,
    val information: String
)

// --- ViewModel ---
class DiagnosisAssistantViewModel : ViewModel() {
    private val _sessionResult = MutableStateFlow<DiagnosticSessionResult?>(null)
    val sessionResult: StateFlow<DiagnosticSessionResult?> = _sessionResult

    private val _conditionDetails = MutableStateFlow<ConditionDetails?>(null)
    val conditionDetails: StateFlow<ConditionDetails?> = _conditionDetails

    // Simplified Knowledge Base (mirrors Python structure)
    private val knowledgeBase: Map<String, Map<String, Any>> = mapOf(
        "Cow" to mapOf(
            "symptoms" to mapOf(
                "coughing" to listOf(
                    mapOf("condition" to "Respiratory Infection (BRD Complex)", "confidence" to 0.7, "urgency" to "High", "next_steps" to "Isolate, check temp, consult vet for antibiotics/anti-inflammatory. Good ventilation."),
                    mapOf("condition" to "Lungworm", "confidence" to 0.4, "urgency" to "Medium", "next_steps" to "Fecal test, consult vet for dewormer. Pasture hygiene.")
                ),
                "lameness" to listOf(
                    mapOf("condition" to "Foot Rot", "confidence" to 0.6, "urgency" to "Medium", "next_steps" to "Clean foot, topical treatment, dry footing. Consult vet if severe."),
                    mapOf("condition" to "Injury (Sprain/Fracture)", "confidence" to 0.3, "urgency" to "High", "next_steps" to "Immobilize, consult vet immediately.")
                )
            ),
            "common_conditions_info" to mapOf(
                "Respiratory Infection (BRD Complex)" to "Common in stressed/young cattle. Bacterial/viral. Signs: cough, fever, nasal discharge.",
                "Foot Rot" to "Bacterial infection of the foot, causes severe lameness."
            )
        ),
        "Dog" to mapOf(
            "symptoms" to mapOf(
                "vomiting" to listOf(
                    mapOf("condition" to "Dietary Indiscretion", "confidence" to 0.7, "urgency" to "Low-Medium", "next_steps" to "Withhold food 12-24h, then bland diet. Vet if persistent."),
                    mapOf("condition" to "Gastroenteritis", "confidence" to 0.5, "urgency" to "Medium", "next_steps" to "Consult vet for supportive care."),
                    mapOf("condition" to "Foreign Body Obstruction", "confidence" to 0.3, "urgency" to "Critical", "next_steps" to "IMMEDIATE vet attention if suspected.")
                ),
                "itching_scratching" to listOf(
                    mapOf("condition" to "Flea Allergy Dermatitis (FAD)", "confidence" to 0.7, "urgency" to "Medium", "next_steps" to "Strict flea control. Consult vet for relief."),
                    mapOf("condition" to "Atopic Dermatitis", "confidence" to 0.6, "urgency" to "Medium", "next_steps" to "Consult vet for diagnosis and management plan.")
                )
            ),
            "common_conditions_info" to mapOf(
                "Dietary Indiscretion" to "Caused by eating inappropriate items. Usually self-limiting.",
                "Flea Allergy Dermatitis (FAD)" to "Allergic reaction to flea saliva. Intense itching."
            )
        )
    )

    val availableSpecies: List<String> = knowledgeBase.keys.toList()
    val availableSymptomsBySpecies: Map<String, List<String>> = knowledgeBase.mapValues { entry ->
        ((entry.value["symptoms"] as? Map<String, List<Any>>)?.keys?.toList() ?: emptyList())
    }


    @Suppress("UNCHECKED_CAST")
    fun startDiagnosticSession(species: String, symptoms: List<String>) {
        val speciesKb = knowledgeBase[species]
        if (speciesKb == null) {
            _sessionResult.value = DiagnosticSessionResult("error_${System.currentTimeMillis()}", species, symptoms, emptyList(), System.currentTimeMillis(), "Knowledge base for $species not available.")
            return
        }

        val symptomMap = speciesKb["symptoms"] as? Map<String, List<Map<String, Any>>> ?: emptyMap()
        val possibleMatches = mutableListOf<PotentialCondition>()

        symptoms.forEach { symptomKey ->
            symptomMap[symptomKey]?.forEach { conditionInfo ->
                val conditionName = conditionInfo["condition"] as String
                val existingMatchIndex = possibleMatches.indexOfFirst { it.condition == conditionName }

                if (existingMatchIndex != -1) {
                    val existing = possibleMatches[existingMatchIndex]
                    possibleMatches[existingMatchIndex] = existing.copy(
                        confidence = minOf(1.0, existing.confidence + 0.1 * (conditionInfo["confidence"] as Double)),
                        matchedSymptoms = (existing.matchedSymptoms + symptomKey).distinct()
                    )
                } else {
                    possibleMatches.add(
                        PotentialCondition(
                            condition = conditionName,
                            confidence = conditionInfo["confidence"] as Double,
                            urgency = conditionInfo["urgency"] as String,
                            nextSteps = conditionInfo["next_steps"] as String,
                            matchedSymptoms = listOf(symptomKey)
                        )
                    )
                }
            }
        }
        possibleMatches.sortByDescending { it.confidence }
        _sessionResult.value = DiagnosticSessionResult("diag_${System.currentTimeMillis()}", species, symptoms, possibleMatches, System.currentTimeMillis())
        _conditionDetails.value = null // Clear previous details
    }

    @Suppress("UNCHECKED_CAST")
    fun getConditionDetails(species: String, conditionName: String) {
        val info = (knowledgeBase[species]?.get("common_conditions_info") as? Map<String, String>)?.get(conditionName)
        _conditionDetails.value = ConditionDetails(conditionName, info ?: "No detailed information available.")
    }

    fun clearSession() {
        _sessionResult.value = null
        _conditionDetails.value = null
    }
}

// --- Composable Screen ---
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DiagnosisAssistantScreen(viewModel: DiagnosisAssistantViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val sessionResult by viewModel.sessionResult.collectAsState()
    val conditionDetails by viewModel.conditionDetails.collectAsState()

    var selectedSpecies by remember { mutableStateOf(viewModel.availableSpecies.firstOrNull() ?: "") }
    var symptomsInput by remember { mutableStateOf("") } // Comma-separated
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Diagnosis Assistant") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF5C6BC0))) // Indigo
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize()) {
            Text("Disclaimer: This is a mock assistant and NOT a substitute for professional veterinary diagnosis.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(Modifier.height(8.dp))

            // Inputs
            ExposedDropdownMenuBoxForOptions("Select Species", viewModel.availableSpecies, selectedSpecies) { selectedSpecies = it }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = symptomsInput,
                onValueChange = { symptomsInput = it },
                label = { Text("Enter Symptoms (comma-separated)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    val symptomsList = symptomsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    if (selectedSpecies.isNotBlank() && symptomsList.isNotEmpty()) {
                        viewModel.startDiagnosticSession(selectedSpecies, symptomsList)
                    }
                    keyboardController?.hide()
                })
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                 Button(onClick = {
                    val symptomsList = symptomsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    if (selectedSpecies.isNotBlank() && symptomsList.isNotEmpty()) {
                        viewModel.startDiagnosticSession(selectedSpecies, symptomsList)
                    }
                    keyboardController?.hide()
                }) { Icon(Icons.Filled.Search, "Diagnose"); Spacer(Modifier.width(4.dp)); Text("Get Diagnosis") }
                Button(onClick = { viewModel.clearSession(); symptomsInput = "" }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Text("Clear") }
            }


            Spacer(Modifier.height(16.dp))

            // Results
            sessionResult?.let { result ->
                if (result.error != null) {
                    Text("Error: ${result.error}", color = MaterialTheme.colorScheme.error)
                } else {
                    Text("Results for ${result.species} with symptoms: ${result.primarySymptoms.joinToString()}", style = MaterialTheme.typography.titleMedium)
                    if (result.potentialConditions.isEmpty()) {
                        Text("No potential conditions found for the given symptoms.")
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(result.potentialConditions) { pc ->
                                PotentialConditionCard(pc) {
                                    viewModel.getConditionDetails(result.species, pc.condition)
                                }
                            }
                        }
                    }
                }
            }

            conditionDetails?.let { details ->
                ConditionDetailsDialog(details = details, onDismiss = { viewModel._conditionDetails.value = null /* Hacky way to clear for now */ })
            }
        }
    }
}

@Composable
fun PotentialConditionCard(pc: PotentialCondition, onViewDetails: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable(onClick = onViewDetails), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("${pc.condition} (Confidence: %.2f)".format(pc.confidence), style = MaterialTheme.typography.titleMedium)
            Text("Urgency: ${pc.urgency}", color = when(pc.urgency){ "High" -> Color.Red; "Critical" -> Color.Red; "Medium" -> Color(0xFFFFA000); else -> Color.Green })
            Text("Matched Symptoms: ${pc.matchedSymptoms.joinToString()}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text("Next Steps: ${pc.nextSteps}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ConditionDetailsDialog(details: ConditionDetails, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(details.name) },
        text = { Text(details.information) },
        confirmButton = { Button(onClick = onDismiss) { Text("Close") } }
    )
}

// Re-using ExposedDropdownMenuForOptions
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuForOptions(
    label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedOption, onValueChange = {}, readOnly = true, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onOptionSelected(option); expanded = false })
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun PreviewDiagnosisAssistantScreen() {
    MaterialTheme {
        DiagnosisAssistantScreen(viewModel = DiagnosisAssistantViewModel())
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun PreviewDiagnosisAssistantScreen_WithResults() {
    val viewModel = DiagnosisAssistantViewModel()
    // Simulate a session for preview
    LaunchedEffect(Unit) {
        viewModel.startDiagnosticSession("Dog", listOf("vomiting", "lethargy"))
    }
    MaterialTheme {
        DiagnosisAssistantScreen(viewModel = viewModel)
    }
}
