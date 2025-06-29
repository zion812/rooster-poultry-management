package com.example.rooster.feature.farm.ui.registry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.feature.farm.domain.model.*
import com.example.rooster.feature.farm.domain.usecase.RegisterFlockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import android.content.Context 
import com.example.rooster.core.common.toUserFriendlyMessage 
import dagger.hilt.android.qualifiers.ApplicationContext 

@HiltViewModel
class FlockRegistryViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context, 
    private val registerFlockUseCase: RegisterFlockUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlockRegistryUiState())
    val uiState: StateFlow<FlockRegistryUiState> = _uiState.asStateFlow()

    fun updateRegistryType(registryType: RegistryType) {
        _uiState.value = _uiState.value.copy(
            registryType = registryType,
            requiresVerification = registryType == RegistryType.TRACEABLE
        )
    }

    fun updateAgeGroup(ageGroup: AgeGroup) {
        _uiState.value = _uiState.value.copy(ageGroup = ageGroup)
    }

    fun updateField(fieldName: String, value: String) {
        val currentState = _uiState.value
        val currentErrors = _uiState.value.fieldErrors.toMutableMap()
        currentErrors.remove(fieldName) // Clear error for this field upon modification

        _uiState.value = when (fieldName) {
            "breed" -> currentState.copy(breed = value, fieldErrors = currentErrors)
            "colors" -> currentState.copy(colorsText = value, fieldErrors = currentErrors)
            "weight" -> currentState.copy(weightText = value, fieldErrors = currentErrors)
            "height" -> currentState.copy(heightText = value, fieldErrors = currentErrors)
            "fatherId" -> currentState.copy(fatherId = value, fieldErrors = currentErrors)
            "motherId" -> currentState.copy(motherId = value, fieldErrors = currentErrors)
            "placeOfBirth" -> currentState.copy(placeOfBirth = value, fieldErrors = currentErrors)
            "identification" -> currentState.copy(identification = value, fieldErrors = currentErrors)
            "specialty" -> currentState.copy(specialty = value, fieldErrors = currentErrors)
            else -> currentState.copy(fieldErrors = currentErrors)
        }
        validateFormAndSetCanSubmit()
    }

    fun updateDateField(fieldName: String, date: Date) {
        val currentErrors = _uiState.value.fieldErrors.toMutableMap()
        currentErrors.remove(fieldName)

        when (fieldName) {
            "dateOfBirth" -> _uiState.value = _uiState.value.copy(dateOfBirth = date, fieldErrors = currentErrors)
        }
        validateFormAndSetCanSubmit()
    }

    fun addProofPhoto(photoUrl: String) {
        val currentPhotos = _uiState.value.proofPhotos
        // Assuming "proofPhotos" is a field name for potential validation
        val currentErrors = _uiState.value.fieldErrors.toMutableMap()
        currentErrors.remove("proofPhotos")

        _uiState.value = _uiState.value.copy(
            proofPhotos = currentPhotos + photoUrl,
            fieldErrors = currentErrors
        )
        validateFormAndSetCanSubmit()
    }

    private fun validateFormAndSetCanSubmit(): Boolean {
        val state = _uiState.value
        val errors = mutableMapOf<String, String>()

        if (state.registryType == null) {
            errors["registryType"] = "Registry type is required."
        }
        if (state.ageGroup == null) {
            errors["ageGroup"] = "Age group is required."
        }
        if (state.breed.isNullOrBlank()) {
            errors["breed"] = "Breed is required."
        }
        // Basic validation for weight (must be a number if not blank)
        if (!state.weightText.isNullOrBlank() && state.weightText.toDoubleOrNull() == null) {
            errors["weight"] = "Invalid weight format."
        } else if (!state.weightText.isNullOrBlank() && (state.weightText.toDoubleOrNull() ?: 0.0) <= 0) {
            errors["weight"] = "Weight must be positive."
        }
        // Basic validation for height
        if (!state.heightText.isNullOrBlank() && state.heightText.toDoubleOrNull() == null) {
            errors["height"] = "Invalid height format."
        } else if (!state.heightText.isNullOrBlank() && (state.heightText.toDoubleOrNull() ?: 0.0) <= 0) {
            errors["height"] = "Height must be positive."
        }

        // Add more validations as needed for other fields:
        // e.g., fatherId/motherId format if they are supposed to be existing flock IDs
        // dateOfBirth not in future, etc.

        val canSubmit = errors.isEmpty() && state.registryType != null && state.ageGroup != null && !state.breed.isNullOrBlank()

        _uiState.value = state.copy(fieldErrors = errors, canSubmit = canSubmit)
        return canSubmit
    }

    fun submitRegistration(farmId: String) {
        if (!validateFormAndSetCanSubmit()) { // Validate before submission
            return
        }
        // val state = _uiState.value // state is already captured by validateFormAndSetCanSubmit
        _uiState.value = _uiState.value.copy(isLoading = true, error = null) // Clear general error

        viewModelScope.launch {
            try {
                val registrationData = FlockRegistrationData(
                    ownerId = farmId,
                    registryType = _uiState.value.registryType!!,
                    ageGroup = _uiState.value.ageGroup!!,
                    breed = _uiState.value.breed,
                    weight = _uiState.value.weightText?.toDoubleOrNull(),
                    colors = _uiState.value.colorsText?.split(",")?.map { it.trim() },
                    gender = null, // Can be added later
                    identification = _uiState.value.identification,
                    size = null, // Can be added later
                    specialty = _uiState.value.specialty,
                    proofs = _uiState.value.proofPhotos,
                    fatherId = _uiState.value.fatherId,
                    motherId = _uiState.value.motherId,
                    placeOfBirth = _uiState.value.placeOfBirth,
                    dateOfBirth = _uiState.value.dateOfBirth,
                    vaccinationRecords = null, // Can be added later
                    height = _uiState.value.heightText?.toDoubleOrNull(),
                    requiresVerification = _uiState.value.requiresVerification,
                    verificationNotes = null
                )

                // Now registerFlockUseCase returns com.example.rooster.core.common.Result
                when (val result = registerFlockUseCase(registrationData)) {
                    is com.example.rooster.core.common.Result.Success -> {
                        _uiState.value = _uiState.value.copy( // Use _uiState.value to get latest state
                            isLoading = false,
                            isSubmitted = true,
                            error = null
                        )
                    }
                    is com.example.rooster.core.common.Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.exception.toUserFriendlyMessage(appContext)
                        )
                    }
                    is com.example.rooster.core.common.Result.Loading -> {
                        // This state is handled by isLoading = true at the start of submitRegistration
                        // If use case were a Flow emitting Loading, this would be more relevant here.
                        // For a suspend fun that implies loading before it returns, this branch might not be hit
                        // if the use case itself doesn't emit Result.Loading.
                        // The outer isLoading flag is the primary indicator here.
                    }
                }
            } catch (e: Exception) { // Catch any unexpected exceptions from the launch block
                _uiState.value = _uiState.value.copy( // Use _uiState.value
                    isLoading = false,
                    error = e.toUserFriendlyMessage(appContext)
                )
            }
        }
    }

    private fun updateCanSubmit() {
        val state = _uiState.value
        val canSubmit = state.registryType != null &&
                state.ageGroup != null &&
                !state.breed.isNullOrBlank()

        _uiState.value = state.copy(canSubmit = canSubmit)
    }
}

/**
 * UI State for Flock Registry Screen
 */
data class FlockRegistryUiState(
    val registryType: RegistryType? = null,
    val ageGroup: AgeGroup? = null,
    val breed: String? = null,
    val colorsText: String? = null,
    val weightText: String? = null,
    val heightText: String? = null,
    val fatherId: String? = null,
    val motherId: String? = null,
    val placeOfBirth: String? = null,
    val dateOfBirth: Date? = null,
    val identification: String? = null,
    val specialty: String? = null,
    val proofPhotos: List<String> = emptyList(),
    val requiresVerification: Boolean = false,
    val canSubmit: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null, // For general submission errors
    val fieldErrors: Map<String, String> = emptyMap() // For field-specific validation errors
)
