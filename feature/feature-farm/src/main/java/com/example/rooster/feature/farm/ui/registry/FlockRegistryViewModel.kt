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

@HiltViewModel
class FlockRegistryViewModel @Inject constructor(
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
        _uiState.value = when (fieldName) {
            "breed" -> currentState.copy(breed = value)
            "colors" -> currentState.copy(colorsText = value)
            "weight" -> currentState.copy(weightText = value)
            "height" -> currentState.copy(heightText = value)
            "fatherId" -> currentState.copy(fatherId = value)
            "motherId" -> currentState.copy(motherId = value)
            "placeOfBirth" -> currentState.copy(placeOfBirth = value)
            "identification" -> currentState.copy(identification = value)
            "specialty" -> currentState.copy(specialty = value)
            else -> currentState
        }

        updateCanSubmit()
    }

    fun updateDateField(fieldName: String, date: Date) {
        when (fieldName) {
            "dateOfBirth" -> _uiState.value = _uiState.value.copy(dateOfBirth = date)
        }
        updateCanSubmit()
    }

    fun addProofPhoto(photoUrl: String) {
        val currentPhotos = _uiState.value.proofPhotos
        _uiState.value = _uiState.value.copy(proofPhotos = currentPhotos + photoUrl)
        updateCanSubmit()
    }

    fun submitRegistration(farmId: String) {
        val state = _uiState.value
        if (!state.canSubmit) return

        _uiState.value = state.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val registrationData = FlockRegistrationData(
                    ownerId = farmId,
                    registryType = state.registryType!!,
                    ageGroup = state.ageGroup!!,
                    breed = state.breed,
                    weight = state.weightText?.toDoubleOrNull(),
                    colors = state.colorsText?.split(",")?.map { it.trim() },
                    gender = null, // Can be added later
                    identification = state.identification,
                    size = null, // Can be added later
                    specialty = state.specialty,
                    proofs = state.proofPhotos,
                    fatherId = state.fatherId,
                    motherId = state.motherId,
                    placeOfBirth = state.placeOfBirth,
                    dateOfBirth = state.dateOfBirth,
                    vaccinationRecords = null, // Can be added later
                    height = state.heightText?.toDoubleOrNull(),
                    requiresVerification = state.requiresVerification,
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
                            error = result.exception.localizedMessage ?: "Registration failed"
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
                    error = e.localizedMessage ?: "An unexpected error occurred"
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
    val error: String? = null
)
