package com.example.rooster.feature.farm.domain.usecase

import com.example.rooster.feature.farm.domain.model.FlockRegistrationData

interface RegisterFlockUseCase {
    suspend operator fun invoke(data: FlockRegistrationData): Result<Unit>
}

class RegisterFlockUseCaseImpl(
    private val repository: com.example.rooster.feature.farm.data.repository.FarmRepository
) : RegisterFlockUseCase {
    override suspend fun invoke(data: FlockRegistrationData) = repository.registerFlock(data)
}