package com.example.rooster.data.repo

import com.example.rooster.models.VaccinationTemplate

object VaccinationRepository {
    suspend fun uploadTemplate(
        farmId: String,
        template: VaccinationTemplate,
    ) {
        // Stub implementation
    }

    suspend fun fetchTemplates(farmId: String): List<VaccinationTemplate> {
        return emptyList()
    }
}
