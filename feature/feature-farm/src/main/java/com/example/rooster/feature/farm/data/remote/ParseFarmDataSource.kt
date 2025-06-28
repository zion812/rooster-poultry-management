package com.example.rooster.feature.farm.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.feature.farm.data.local.LineageLinkEntity
import com.example.rooster.feature.farm.data.local.RelationshipType
import com.example.rooster.feature.farm.domain.model.*
import com.parse.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class ParseFarmDataSource @Inject constructor() : IFarmRemoteDataSource {

    companion object {
        const val CLASS_FLOCK = "Flock"
        const val CLASS_LINEAGE_LINK = "LineageLink"

        // Flock Fields
        const val F_OWNER = "owner"
        const val F_FATHER = "father" // Pointer to Flock
        const val F_MOTHER = "mother" // Pointer to Flock
        const val F_TYPE = "type"
        const val F_NAME = "name"
        const val F_BREED = "breed"
        const val F_WEIGHT = "weight"
        const val F_HEIGHT = "height"
        const val F_COLOR = "color"
        const val F_GENDER = "gender"
        const val F_CERTIFIED = "certified"
        const val F_VERIFIED = "verified"
        const val F_VERIFICATION_LEVEL = "verificationLevel"
        const val F_TRACEABLE = "traceable"
        const val F_AGE_GROUP = "ageGroup"
        const val F_DATE_OF_BIRTH = "dateOfBirth"
        const val F_PLACE_OF_BIRTH = "placeOfBirth"
        // currentAge is calculated, not stored typically
        const val F_VACCINATION_STATUS = "vaccinationStatus"
        const val F_LAST_VACCINATION_DATE = "lastVaccinationDate"
        const val F_HEALTH_STATUS = "healthStatus"
        const val F_LAST_HEALTH_CHECK = "lastHealthCheck"
        const val F_IDENTIFICATION_TAG = "identificationTag"
        const val F_REGISTRY_NUMBER = "registryNumber"
        const val F_PROOFS = "proofs" // Array of Strings (URLs)
        const val F_SPECIALTY = "specialty"
        const val F_PRODUCTIVITY_SCORE = "productivityScore"
        const val F_GROWTH_RATE = "growthRate"
        const val F_FEED_CONVERSION_RATIO = "feedConversionRatio"
        const val F_STATUS = "status"
        const val F_FOR_SALE = "forSale"
        const val F_PRICE = "price"
        const val F_PURPOSE = "purpose" // Array of Strings

        // LineageLink Fields
        const val L_CHILD_FLOCK = "childFlock" // Pointer to Flock
        const val L_PARENT_FLOCK = "parentFlock" // Pointer to Flock
        const val L_RELATIONSHIP_TYPE = "relationshipType"
    }

    override fun getFlockStream(flockId: String): Flow<Result<Flock?>> = callbackFlow {
        val query = ParseQuery.getQuery<ParseObject>(CLASS_FLOCK)
        query.whereEqualTo("objectId", flockId)
        query.include(F_OWNER) // Include owner User object if needed for mapping directly
        // query.include(F_FATHER) // Include pointer data if needed directly
        // query.include(F_MOTHER)

        // For real-time, a LiveQuery subscription would be set up here.
        // For now, simulating a one-time fetch then potentially periodic refresh via the Flow.
        // This is a simplified stream that emits once or on change if LiveQuery is used.
        // For this example, it's a one-time fetch wrapped in a flow.
        query.getFirstInBackground { obj, e ->
            if (e == null && obj != null) {
                trySend(Result.Success(mapParseObjectToFlock(obj)))
            } else if (e is ParseException && e.code == ParseException.OBJECT_NOT_FOUND) {
                trySend(Result.Success(null))
            } else {
                Timber.e(e, "Parse: Error fetching flock $flockId")
                trySend(Result.Error(e ?: Exception("Failed to fetch flock $flockId")))
            }
            channel.close() // Close after one emit for non-LiveQuery
        }
        awaitClose { Timber.d("Parse: Closing getFlockStream for $flockId") }
    }

    override fun getFlocksByOwnerStream(ownerId: String): Flow<Result<List<Flock>>> = callbackFlow {
        val ownerPointer = ParseUser.createWithoutData("_User", ownerId)
        val query = ParseQuery.getQuery<ParseObject>(CLASS_FLOCK)
        query.whereEqualTo(F_OWNER, ownerPointer)
        query.orderByDescending("createdAt")
        // query.include(F_FATHER)
        // query.include(F_MOTHER)

        // Simplified one-time fetch for now
        query.findInBackground { objects, e ->
            if (e == null) {
                trySend(Result.Success(objects.mapNotNull { mapParseObjectToFlock(it) }))
            } else {
                Timber.e(e, "Parse: Error fetching flocks for owner $ownerId")
                trySend(Result.Error(e))
            }
            channel.close()
        }
        awaitClose { Timber.d("Parse: Closing getFlocksByOwnerStream for $ownerId") }
    }


    override suspend fun saveFlock(flock: Flock): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val parseFlock = mapFlockToParseObject(flock)
        parseFlock.saveInBackground { e ->
            if (e == null) {
                Timber.d("Parse: Saved flock ${flock.id}")
                continuation.resume(Result.Success(Unit))
            } else {
                Timber.e(e, "Parse: Error saving flock ${flock.id}")
                continuation.resume(Result.Error(e))
            }
        }
    }

    override suspend fun deleteFlock(flockId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val query = ParseQuery.getQuery<ParseObject>(CLASS_FLOCK)
        query.getInBackground(flockId) { parseObject, e ->
            if (e == null && parseObject != null) {
                parseObject.deleteInBackground { deleteException ->
                    if (deleteException == null) {
                        Timber.d("Parse: Deleted flock $flockId")
                        continuation.resume(Result.Success(Unit))
                    } else {
                        Timber.e(deleteException, "Parse: Error deleting flock $flockId")
                        continuation.resume(Result.Error(deleteException))
                    }
                }
            } else if (e != null && e.code == ParseException.OBJECT_NOT_FOUND) {
                 Timber.w("Parse: Flock $flockId not found for deletion, considering success.")
                 continuation.resume(Result.Success(Unit)) // Object already gone
            }
            else {
                Timber.e(e, "Parse: Error finding flock $flockId for deletion")
                continuation.resume(Result.Error(e ?: Exception("Failed to find flock for deletion")))
            }
        }
    }

    // --- Lineage ---
    override suspend fun saveLineageLink(link: LineageLinkEntity): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val parseLink = ParseObject(CLASS_LINEAGE_LINK)
        parseLink.put(L_CHILD_FLOCK, ParseObject.createWithoutData(CLASS_FLOCK, link.childFlockId))
        parseLink.put(L_PARENT_FLOCK, ParseObject.createWithoutData(CLASS_FLOCK, link.parentFlockId))
        parseLink.put(L_RELATIONSHIP_TYPE, link.relationshipType.name)
        // Unique ID for links can be childId_parentId_type if needed, set as objectId or another field.
        // For now, rely on Parse auto objectId. If using custom ID, use that for get/delete.

        parseLink.saveInBackground { e->
            if (e == null) {
                continuation.resume(Result.Success(Unit))
            } else {
                continuation.resume(Result.Error(e))
            }
        }
    }

    override suspend fun deleteLineageLink(childFlockId: String, parentFlockId: String, relationshipTypeName: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val query = ParseQuery.getQuery<ParseObject>(CLASS_LINEAGE_LINK)
        query.whereEqualTo(L_CHILD_FLOCK, ParseObject.createWithoutData(CLASS_FLOCK, childFlockId))
        query.whereEqualTo(L_PARENT_FLOCK, ParseObject.createWithoutData(CLASS_FLOCK, parentFlockId))
        query.whereEqualTo(L_RELATIONSHIP_TYPE, relationshipTypeName)
        query.getFirstInBackground { obj, e ->
            if (e == null && obj != null) {
                obj.deleteInBackground { deleteException ->
                    if (deleteException == null) { continuation.resume(Result.Success(Unit)) }
                    else { continuation.resume(Result.Error(deleteException)) }
                }
            } else if (e != null && e.code == ParseException.OBJECT_NOT_FOUND) {
                 continuation.resume(Result.Success(Unit)) // Link already gone
            } else {
                continuation.resume(Result.Error(e ?: Exception("Failed to find lineage link for deletion")))
            }
        }
    }

    override fun getLineageLinksForChildStream(childFlockId: String): Flow<Result<List<LineageLinkEntity>>> = callbackFlow {
        val childPointer = ParseObject.createWithoutData(CLASS_FLOCK, childFlockId)
        val query = ParseQuery.getQuery<ParseObject>(CLASS_LINEAGE_LINK)
        query.whereEqualTo(L_CHILD_FLOCK, childPointer)
        query.include(L_PARENT_FLOCK) // Include parent flock data for mapping

        query.findInBackground { objects, e ->
            if (e == null) {
                trySend(Result.Success(objects.mapNotNull { mapParseObjectToLineageLinkEntity(it) }))
            } else {
                trySend(Result.Error(e))
            }
            channel.close()
        }
        awaitClose { }
    }

    override fun getLineageLinksForParentStream(parentFlockId: String): Flow<Result<List<LineageLinkEntity>>> = callbackFlow {
        val parentPointer = ParseObject.createWithoutData(CLASS_FLOCK, parentFlockId)
        val query = ParseQuery.getQuery<ParseObject>(CLASS_LINEAGE_LINK)
        query.whereEqualTo(L_PARENT_FLOCK, parentPointer)
        query.include(L_CHILD_FLOCK) // Include child flock data

        query.findInBackground { objects, e ->
            if (e == null) {
                trySend(Result.Success(objects.mapNotNull { mapParseObjectToLineageLinkEntity(it) }))
            } else {
                trySend(Result.Error(e))
            }
            channel.close()
        }
        awaitClose { }
    }


    // --- Mappers ---
    private fun mapParseObjectToFlock(obj: ParseObject): Flock? {
        return try {
            Flock(
                id = obj.objectId,
                ownerId = obj.getParseUser(F_OWNER)?.objectId ?: obj.getString(F_OWNER) ?: "unknown_owner", // getString if ownerId stored as string
                fatherId = obj.getParseObject(F_FATHER)?.objectId,
                motherId = obj.getParseObject(F_MOTHER)?.objectId,
                type = FlockType.valueOf(obj.getString(F_TYPE) ?: FlockType.FOWL.name),
                name = obj.getString(F_NAME) ?: "Unnamed Flock",
                breed = obj.getString(F_BREED),
                weight = obj.getNumber(F_WEIGHT)?.toFloat(),
                height = obj.getNumber(F_HEIGHT)?.toFloat(),
                color = obj.getString(F_COLOR),
                gender = obj.getString(F_GENDER)?.let { Gender.valueOf(it) },
                certified = obj.getBoolean(F_CERTIFIED),
                verified = obj.getBoolean(F_VERIFIED),
                verificationLevel = VerificationLevel.valueOf(obj.getString(F_VERIFICATION_LEVEL) ?: VerificationLevel.BASIC.name),
                traceable = obj.getBoolean(F_TRACEABLE),
                ageGroup = AgeGroup.valueOf(obj.getString(F_AGE_GROUP) ?: AgeGroup.CHICKS.name),
                dateOfBirth = obj.getDate(F_DATE_OF_BIRTH),
                placeOfBirth = obj.getString(F_PLACE_OF_BIRTH),
                currentAge = null, // Calculated field
                vaccinationStatus = VaccinationStatus.valueOf(obj.getString(F_VACCINATION_STATUS) ?: VaccinationStatus.NOT_STARTED.name),
                lastVaccinationDate = obj.getDate(F_LAST_VACCINATION_DATE),
                healthStatus = HealthStatus.valueOf(obj.getString(F_HEALTH_STATUS) ?: HealthStatus.GOOD.name),
                lastHealthCheck = obj.getDate(F_LAST_HEALTH_CHECK),
                identification = obj.getString(F_IDENTIFICATION_TAG),
                registryNumber = obj.getString(F_REGISTRY_NUMBER),
                proofs = obj.getList<String>(F_PROOFS)?.toList() ?: emptyList(),
                specialty = obj.getString(F_SPECIALTY),
                productivityScore = obj.getInt(F_PRODUCTIVITY_SCORE),
                growthRate = obj.getDouble(F_GROWTH_RATE),
                feedConversionRatio = obj.getDouble(F_FEED_CONVERSION_RATIO),
                status = FlockStatus.valueOf(obj.getString(F_STATUS) ?: FlockStatus.ACTIVE.name),
                forSale = obj.getBoolean(F_FOR_SALE),
                price = obj.getDouble(F_PRICE),
                purpose = obj.getList<String>(F_PURPOSE)?.mapNotNull { Purpose.valueOf(it) } ?: emptyList(),
                createdAt = obj.createdAt ?: Date(),
                updatedAt = obj.updatedAt ?: Date()
            )
        } catch (e: Exception) {
            Timber.e(e, "Error mapping ParseObject to Flock: ${obj.objectId}")
            null
        }
    }

    private fun mapFlockToParseObject(flock: Flock): ParseObject {
        val parseFlock = ParseObject(CLASS_FLOCK)
        if (flock.id.isNotBlank() && flock.id != "null") { // Guard against "null" string ID if it's a new object
             try {
                // This is to update an existing object. For new object, Parse generates objectId.
                // If flock.id is a valid Parse objectId, this sets it for update.
                // If it's a client-generated UUID for a new object, Parse will ignore this
                // and generate its own objectId upon first save.
                if (flock.id.length == 10 && flock.id.all { it.isLetterOrDigit() }) { // Basic check for Parse ID format
                     parseFlock.objectId = flock.id
                }
            } catch (e: Exception) { Timber.w("Could not set objectId on ParseFlock for id: ${flock.id}")}
        }

        ParseUser.getCurrentUser()?.let { parseFlock.put(F_OWNER, it) }
            ?: flock.ownerId.let { ownerId -> // Fallback if ownerId string is available
                 if(ownerId != "unknown_owner") parseFlock.put(F_OWNER, ParseObject.createWithoutData("_User", ownerId))
            }

        flock.fatherId?.let { parseFlock.put(F_FATHER, ParseObject.createWithoutData(CLASS_FLOCK, it)) }
        flock.motherId?.let { parseFlock.put(F_MOTHER, ParseObject.createWithoutData(CLASS_FLOCK, it)) }
        parseFlock.put(F_TYPE, flock.type.name)
        parseFlock.put(F_NAME, flock.name)
        flock.breed?.let { parseFlock.put(F_BREED, it) }
        flock.weight?.let { parseFlock.put(F_WEIGHT, it) }
        flock.height?.let { parseFlock.put(F_HEIGHT, it) }
        flock.color?.let { parseFlock.put(F_COLOR, it) }
        flock.gender?.let { parseFlock.put(F_GENDER, it.name) }
        parseFlock.put(F_CERTIFIED, flock.certified)
        parseFlock.put(F_VERIFIED, flock.verified)
        parseFlock.put(F_VERIFICATION_LEVEL, flock.verificationLevel.name)
        parseFlock.put(F_TRACEABLE, flock.traceable)
        parseFlock.put(F_AGE_GROUP, flock.ageGroup.name)
        flock.dateOfBirth?.let { parseFlock.put(F_DATE_OF_BIRTH, it) }
        flock.placeOfBirth?.let { parseFlock.put(F_PLACE_OF_BIRTH, it) }
        parseFlock.put(F_VACCINATION_STATUS, flock.vaccinationStatus.name)
        flock.lastVaccinationDate?.let { parseFlock.put(F_LAST_VACCINATION_DATE, it) }
        parseFlock.put(F_HEALTH_STATUS, flock.healthStatus.name)
        flock.lastHealthCheck?.let { parseFlock.put(F_LAST_HEALTH_CHECK, it) }
        flock.identification?.let { parseFlock.put(F_IDENTIFICATION_TAG, it) }
        flock.registryNumber?.let { parseFlock.put(F_REGISTRY_NUMBER, it) }
        flock.proofs?.let { parseFlock.put(F_PROOFS, it) }
        flock.specialty?.let { parseFlock.put(F_SPECIALTY, it) }
        flock.productivityScore?.let { parseFlock.put(F_PRODUCTIVITY_SCORE, it) }
        flock.growthRate?.let { parseFlock.put(F_GROWTH_RATE, it) }
        flock.feedConversionRatio?.let { parseFlock.put(F_FEED_CONVERSION_RATIO, it) }
        parseFlock.put(F_STATUS, flock.status.name)
        parseFlock.put(F_FOR_SALE, flock.forSale)
        flock.price?.let { parseFlock.put(F_PRICE, it) }
        flock.purpose?.let { parseFlock.put(F_PURPOSE, it.map { p -> p.name }) }
        // createdAt and updatedAt are handled by Parse
        return parseFlock
    }

    private fun mapParseObjectToLineageLinkEntity(obj: ParseObject): LineageLinkEntity? {
        return try {
            LineageLinkEntity(
                childFlockId = obj.getParseObject(L_CHILD_FLOCK)?.objectId ?: return null,
                parentFlockId = obj.getParseObject(L_PARENT_FLOCK)?.objectId ?: return null,
                relationshipType = RelationshipType.valueOf(obj.getString(L_RELATIONSHIP_TYPE) ?: return null),
                needsSync = false // Data from remote is considered synced
            )
        } catch (e: Exception) {
            Timber.e(e, "Error mapping ParseObject to LineageLinkEntity: ${obj.objectId}")
            null
        }
    }
}
