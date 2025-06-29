package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.core.common.Result // Import common Result
import com.example.rooster.feature.farm.domain.model.FlockRegistrationData
import javax.inject.Inject // Added for Hilt

interface RegisterFlockUseCase {
    suspend operator fun invoke(data: FlockRegistrationData): Result<Unit> // Ensure this is core.common.Result
}

// Add @Inject for Hilt if this use case is injected into ViewModels
class RegisterFlockUseCaseImpl @Inject constructor(
    private val repository: com.example.rooster.feature.farm.data.repository.FarmRepository
) : RegisterFlockUseCase {
    override suspend operator fun invoke(data: FlockRegistrationData): Result<Unit> = repository.registerFlock(data)
}