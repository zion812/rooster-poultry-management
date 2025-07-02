package com.example.rooster.feature.farm.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.feature.farm.domain.model.BadgeType
import com.example.rooster.feature.farm.domain.model.FarmBadge
import com.example.rooster.feature.farm.domain.model.FarmDetails
import com.example.rooster.feature.farm.domain.model.VerificationLevel
import com.example.rooster.core.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FarmMainViewModel @Inject constructor(
    private val getFarmDetailsUseCase: com.example.rooster.feature.farm.domain.usecase.GetFarmDetailsUseCase
) : ViewModel() {

    private val _farmState = MutableStateFlow(
        FarmState(
            farmDetails = FarmDetails(
                id = "farm1",
                ownerId = "owner1",
                name = "Elite Farm",
                location = "Telangana, India",
                establishedDate = Date(),
                registrationNumber = null,
                licenseNumber = null,
                verified = true,
                certified = true,
                verificationLevel = VerificationLevel.PREMIUM,
                certificationAgency = null,
                certificationDate = null,
                certificationExpiryDate = null,
                totalFowls = 100,
                totalHens = 50,
                totalBreeders = 20,
                totalChicks = 30,
                activeFlocks = 4,
                lastHealthCheck = null,
                vaccinationCompliance = 95.0,
                mortalityRate = 5.0,
                eggProductionRate = 200.0,
                hatchingSuccessRate = 85.0,
                feedConversionRatio = 2.5,
                biosecurityScore = 80,
                animalWelfareScore = 75,
                traceabilityScore = 90,
                contactEmail = null,
                contactPhone = null,
                documents = emptyList(),
                photos = emptyList(),
                createdAt = Date(),
                updatedAt = Date()
            ),
            badges = listOf(
                FarmBadge(BadgeType.VERIFIED, "1", "Verified Farm", Date(), null),
                FarmBadge(BadgeType.CERTIFIED, "1", "Certified Farm", Date(), null)
            ),
            isLoading = false
        )
    )
    val farmState: StateFlow<FarmState> = _farmState.asStateFlow()

    private val _flockStats = MutableStateFlow(FlockStats())
    val flockStats: StateFlow<FlockStats> = _flockStats.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    /**
     * Stub: no-op
     */
    fun loadFarmDetails(farmId: String) {
        // Fetch farm details and update UI state
        viewModelScope.launch {
            _farmState.value = _farmState.value.copy(isLoading = true)
            getFarmDetailsUseCase(farmId)
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            val flock = result.data
                            // Map Flock to FarmDetails domain model
                            val details = FarmDetails(
                                id = flock.id,
                                ownerId = flock.ownerId,
                                name = flock.name,
                                location = "",
                                establishedDate = flock.dateOfBirth ?: Date(),
                                registrationNumber = null,
                                licenseNumber = null,
                                verified = flock.verified,
                                certified = flock.certified,
                                verificationLevel = VerificationLevel.BASIC,
                                certificationAgency = null,
                                certificationDate = null,
                                certificationExpiryDate = null,
                                totalFowls = 1,
                                totalHens = 0,
                                totalBreeders = 0,
                                totalChicks = 0,
                                activeFlocks = 1,
                                lastHealthCheck = flock.lastHealthCheck,
                                vaccinationCompliance = if (flock.vaccinationStatus == com.example.rooster.feature.farm.domain.model.VaccinationStatus.UP_TO_DATE) 100.0 else 0.0,
                                mortalityRate = 0.0,
                                eggProductionRate = flock.productivityScore?.toDouble(),
                                hatchingSuccessRate = null,
                                feedConversionRatio = null,
                                biosecurityScore = 0,
                                animalWelfareScore = 0,
                                traceabilityScore = 0,
                                contactEmail = null,
                                contactPhone = null,
                                documents = emptyList(),
                                photos = emptyList(),
                                createdAt = Date(),
                                updatedAt = Date()
                            )
                            _farmState.value =
                                com.example.rooster.feature.farm.ui.navigation.FarmState(
                                    farmDetails = details,
                                    badges = listOf(
                                        FarmBadge(BadgeType.VERIFIED, "1", "Verified", Date(), null)
                                    ),
                                    isLoading = false
                                )
                            _flockStats.value = FlockStats(
                                totalFowls = 1,
                                totalHens = 0,
                                totalBreeders = 0,
                                totalChicks = 0,
                                activeFlocks = 1
                            )
                        }

                        is Result.Error -> {
                            _farmState.value = _farmState.value.copy(isLoading = false)
                        }
                        is Result.Loading -> {
                            // Already handled above
                        }
                    }
                    _isRefreshing.value = false
                }
                .launchIn(viewModelScope)
        }
    }
}
