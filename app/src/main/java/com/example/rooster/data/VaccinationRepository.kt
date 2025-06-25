package com.example.rooster.data

import com.example.rooster.models.VaccinationTemplate
import com.example.rooster.models.VaccinationTemplateParse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseException
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Repository for vaccination templates, integrated with Parse backend.
 */
object VaccinationRepository {
    // No longer needed: private val templates = ConcurrentHashMap<String, MutableList<VaccinationTemplate>>()

    /** Uploads a new vaccination template for a farm. */
    suspend fun uploadTemplate(
        farmId: String,
        template: VaccinationTemplate,
    ) {
        withContext(Dispatchers.IO) {
            try {
                val parseTemplate = VaccinationTemplateParse()
                parseTemplate.farmId = farmId
                parseTemplate.name = template.name
                parseTemplate.schedule = template.schedule
                parseTemplate.uploadedAt = Date(template.uploadedAt)
                parseTemplate.save()
                FirebaseCrashlytics.getInstance().log("Vaccination template uploaded for $farmId: ${parseTemplate.objectId}")
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                throw e
            }
        }
    }

    /** Fetches all vaccination templates for a farm. */
    suspend fun fetchTemplates(farmId: String): List<VaccinationTemplate> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(VaccinationTemplateParse::class.java)
                query.whereEqualTo("farmId", farmId)
                query.orderByDescending("uploadedAt")
                val parseTemplates = query.find()

                parseTemplates.map { parseTemplate ->
                    VaccinationTemplate(
                        farmId = parseTemplate.farmId ?: "",
                        templateId = parseTemplate.objectId ?: "",
                        name = parseTemplate.name ?: "",
                        schedule = parseTemplate.schedule ?: emptyList(),
                        uploadedAt = parseTemplate.uploadedAt?.time ?: 0L,
                    )
                }.also { list ->
                    FirebaseCrashlytics.getInstance().log("Fetched ${list.size} vaccination templates for $farmId")
                }
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                emptyList() // Return empty list on error
            }
        }
    }
}
