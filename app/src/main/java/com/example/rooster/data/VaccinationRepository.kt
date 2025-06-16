package com.example.rooster.data

import com.example.rooster.models.VaccinationTemplate
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap

/**
 * Stub repository for vaccination templates. Replace with real backend.
 */
object VaccinationRepository {
    private val templates = ConcurrentHashMap<String, MutableList<VaccinationTemplate>>()

    /** Uploads a new vaccination template for a farm. */
    suspend fun uploadTemplate(
        farmId: String,
        template: VaccinationTemplate,
    ) {
        delay(300)
        templates.computeIfAbsent(farmId) { mutableListOf() }.add(template)
    }

    /** Fetches all vaccination templates for a farm. */
    suspend fun fetchTemplates(farmId: String): List<VaccinationTemplate> {
        delay(300)
        return templates[farmId]?.toList() ?: emptyList()
    }
}
