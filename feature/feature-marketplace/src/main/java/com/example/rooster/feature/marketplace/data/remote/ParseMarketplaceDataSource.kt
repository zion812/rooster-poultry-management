package com.example.rooster.feature.marketplace.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.model.Order
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.example.rooster.feature.marketplace.domain.model.ProductCategory
import com.example.rooster.feature.marketplace.domain.model.ListingStatus
// Import domain models for mapping if necessary, e.g. enums
import com.parse.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class ParseMarketplaceDataSource @Inject constructor() : MarketplaceRemoteDataSource {

    companion object {
        const val CLASS_PRODUCT_LISTING = "ProductListing"
        const val CLASS_ORDER = "MarketOrder" // Renamed to avoid conflict with Parse "Order" if it exists
        const val CLASS_ORDER_ITEM = "MarketOrderItem"


        // ProductListing Fields (align with domain model ProductListing)
        const val PL_SELLER = "seller" // Pointer to _User
        const val PL_TITLE = "title"
        const val PL_DESCRIPTION = "description"
        const val PL_CATEGORY = "category" // String (enum name)
        const val PL_BREED = "breed"
        const val PL_AGE_IN_WEEKS = "ageInWeeks"
        const val PL_WEIGHT_IN_KG = "weightInKg"
        const val PL_PRICE = "price"
        const val PL_CURRENCY = "currency"
        const val PL_QUANTITY_AVAILABLE = "quantityAvailable"
        const val PL_IMAGE_URLS = "imageUrls" // Array of String
        const val PL_LOCATION_CITY = "locationCity"
        const val PL_LOCATION_DISTRICT = "locationDistrict"
        const val PL_LOCATION_STATE = "locationState"
        const val PL_IS_ORGANIC = "isOrganic"
        const val PL_IS_VACCINATED = "isVaccinated"
        const val PL_POSTED_DATE_TIMESTAMP = "postedDateTimestamp" // Number (Long) for sorting
        const val PL_STATUS = "status" // String (enum name)
        const val PL_ADDITIONAL_PROPERTIES = "additionalProperties" // Object/Map
        const val PL_KEYWORDS = "keywords" // Array of String for search
    }

    override fun getProductListingsStream(
        category: String?,
        sellerId: String?,
        searchTerm: String?,
        pageSize: Int,
        page: Int
    ): Flow<Result<List<ProductListing>>> = flow { // Implemented as a one-shot fetch within a flow
        try {
            val query = ParseQuery.getQuery<ParseObject>(CLASS_PRODUCT_LISTING)
            query.orderByDescending(PL_POSTED_DATE_TIMESTAMP) // Or "createdAt" / "updatedAt"

            category?.let { query.whereEqualTo(PL_CATEGORY, it) }
            sellerId?.let { query.whereEqualTo(PL_SELLER, ParseObject.createWithoutData("_User", it)) }

            searchTerm?.let {
                // Basic search: create a list of keywords from the search term
                val terms = it.lowercase().split(" ").filter { term -> term.isNotBlank() }
                if (terms.isNotEmpty()) {
                    query.whereContainsAll(PL_KEYWORDS, terms)
                }
            }

            query.limit = pageSize
            query.skip = (page - 1) * pageSize

            val parseObjects = query.find() // Synchronous find in coroutine context
            val listings = parseObjects.mapNotNull { mapParseObjectToProductListing(it) }
            emit(Result.Success(listings))
        } catch (e: ParseException) {
            Timber.e(e, "Parse: Error fetching product listings page $page")
            emit(Result.Error(e))
        } catch (e: Exception) {
            Timber.e(e, "Error fetching product listings page $page")
            emit(Result.Error(e))
        }
    }


    override suspend fun getProductListingDetails(listingId: String): Result<ProductListing?> {
        return try {
            val query = ParseQuery.getQuery<ParseObject>(CLASS_PRODUCT_LISTING)
            // query.include(PL_SELLER) // To fetch seller ParseUser details if needed for mapping
            val parseObject = query.get(listingId) // Synchronous get in coroutine
            Result.Success(mapParseObjectToProductListing(parseObject))
        } catch (e: ParseException) {
            if (e.code == ParseException.OBJECT_NOT_FOUND) {
                Result.Success(null)
            } else {
                Timber.e(e, "Parse: Error fetching product listing details $listingId")
                Result.Error(e)
            }
        } catch (e: Exception) {
             Timber.e(e, "Error fetching product listing details $listingId")
             Result.Error(e)
        }
    }

    override suspend fun createProductListing(listingData: ProductListing): Result<String> = suspendCancellableCoroutine { continuation ->
        val parseListing = mapProductListingToParseObject(listingData)
        parseListing.saveInBackground { e ->
            if (e == null) {
                continuation.resume(Result.Success(parseListing.objectId))
            } else {
                continuation.resume(Result.Error(e))
            }
        }
    }

    override suspend fun updateProductListing(listingData: ProductListing): Result<Unit> = suspendCancellableCoroutine { continuation ->
        // For update, we need to fetch the existing object first, then apply changes, or use objectId.
        val parseListing = mapProductListingToParseObject(listingData)
        // Ensure objectId is set for updates if it's not a new object
        if (listingData.id.isNotBlank()) { // Assuming domain 'id' maps to Parse 'objectId'
            parseListing.objectId = listingData.id
        } // Else, if ID is blank, this effectively becomes a create, Parse will assign an ID.

        parseListing.saveInBackground { e ->
            if (e == null) {
                continuation.resume(Result.Success(Unit))
            } else {
                continuation.resume(Result.Error(e))
            }
        }
    }


    override suspend fun deleteProductListing(listingId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val parseListing = ParseObject.createWithoutData(CLASS_PRODUCT_LISTING, listingId)
        parseListing.deleteInBackground { e ->
            if (e == null) {
                continuation.resume(Result.Success(Unit))
            } else {
                continuation.resume(Result.Error(e))
            }
        }
    }

    // --- Order methods ---
    override suspend fun createOrder(orderData: Order): Result<String> = suspendCancellableCoroutine { continuation ->
        val parseOrder = mapOrderToParseObject(orderData)
        parseOrder.saveInBackground { e ->
            if (e == null) {
                continuation.resume(Result.Success(parseOrder.objectId))
            } else {
                Timber.e(e, "Parse: Error creating order")
                continuation.resume(Result.Error(e))
            }
        }
    }

    override fun getOrderDetailsStream(orderId: String): Flow<Result<Order?>> = flow {
        // One-shot fetch for now, similar to getProductListingDetails
        try {
            val query = ParseQuery.getQuery<ParseObject>(CLASS_ORDER)
            // query.include("buyer") // If buyer details beyond ID are needed directly
            // query.include("items.listing") // Not possible if items are embedded maps.
            val parseObject = query.get(orderId)
            emit(Result.Success(mapParseObjectToOrder(parseObject)))
        } catch (e: ParseException) {
            if (e.code == ParseException.OBJECT_NOT_FOUND) {
                emit(Result.Success(null))
            } else {
                Timber.e(e, "Parse: Error fetching order details $orderId")
                emit(Result.Error(e))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching order details $orderId")
            emit(Result.Error(e))
        }
    }


    override fun getOrdersForUserStream(
        userId: String,
        pageSize: Int,
        page: Int // Changed from lastOrderTimestamp/lastOrderId for Parse skip/limit
    ): Flow<Result<List<Order>>> = flow {
        try {
            val query = ParseQuery.getQuery<ParseObject>(CLASS_ORDER)
            query.whereEqualTo(OrderFields.BUYER, ParseObject.createWithoutData("_User", userId))
            query.orderByDescending(OrderFields.ORDER_TIMESTAMP)
            query.limit = pageSize
            query.skip = (page - 1) * pageSize
            // query.include("items") // Not needed if items are embedded maps

            val parseObjects = query.find()
            val orders = parseObjects.mapNotNull { mapParseObjectToOrder(it) }
            emit(Result.Success(orders))
        } catch (e: ParseException) {
            Timber.e(e, "Parse: Error fetching orders for user $userId, page $page")
            emit(Result.Error(e))
        } catch (e: Exception) {
            Timber.e(e, "Error fetching orders for user $userId, page $page")
            emit(Result.Error(e))
        }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val query = ParseQuery.getQuery<ParseObject>(CLASS_ORDER)
        query.getInBackground(orderId) { orderObj, e ->
            if (e == null && orderObj != null) {
                orderObj.put(OrderFields.STATUS, newStatus)
                orderObj.put(OrderFields.LAST_UPDATED_TIMESTAMP, System.currentTimeMillis())
                orderObj.saveInBackground { saveException ->
                    if (saveException == null) {
                        continuation.resume(Result.Success(Unit))
                    } else {
                        continuation.resume(Result.Error(saveException))
                    }
                }
            } else {
                continuation.resume(Result.Error(e ?: ParseException(ParseException.OBJECT_NOT_FOUND, "Order not found")))
            }
        }
    }

    override suspend fun cancelOrder(orderId: String): Result<Unit> {
        // For simplicity, using updateOrderStatus with a specific status.
        // In a real app, cancelOrder might have more specific logic (e.g. checking if cancellable).
        return updateOrderStatus(orderId, OrderStatus.CANCELLED_BY_USER.name)
    }

    // --- Mappers for ProductListing ---
    private fun mapParseObjectToProductListing(obj: ParseObject): ProductListing? {
        return try {
            ProductListing(
                id = obj.objectId,
                sellerId = obj.getParseUser(PL_SELLER)?.objectId ?: obj.getString(PL_SELLER) ?: "",
                title = obj.getString(PL_TITLE) ?: "",
                description = obj.getString(PL_DESCRIPTION) ?: "",
                category = ProductCategory.valueOf(obj.getString(PL_CATEGORY) ?: ProductCategory.LIVE_BIRD_CHICKEN.name),
                breed = obj.getString(PL_BREED),
                ageInWeeks = obj.getInt(PL_AGE_IN_WEEKS).takeIf { it != 0 }, // Parse getInt returns 0 if not found
                weightInKg = obj.getDouble(PL_WEIGHT_IN_KG).takeIf { it != 0.0 },
                price = obj.getDouble(PL_PRICE),
                currency = obj.getString(PL_CURRENCY) ?: "INR",
                quantityAvailable = obj.getInt(PL_QUANTITY_AVAILABLE),
                imageUrls = obj.getList<String>(PL_IMAGE_URLS)?.toList() ?: emptyList(),
                locationCity = obj.getString(PL_LOCATION_CITY),
                locationDistrict = obj.getString(PL_LOCATION_DISTRICT),
                locationState = obj.getString(PL_LOCATION_STATE) ?: "Andhra Pradesh",
                isOrganic = obj.getBoolean(PL_IS_ORGANIC), // getBoolean is fine
                isVaccinated = obj.getBoolean(PL_IS_VACCINATED),
            postedDateTimestamp = obj.getLong(PL_POSTED_DATE_TIMESTAMP),
            updatedDateTimestamp = obj.updatedAt?.time ?: obj.getLong(PL_POSTED_DATE_TIMESTAMP), // Fallback for updatedAt
                status = ListingStatus.valueOf(obj.getString(PL_STATUS) ?: ListingStatus.ACTIVE.name),
                additionalProperties = obj.getMap<String>(PL_ADDITIONAL_PROPERTIES)
            )
        } catch (e: Exception) {
            Timber.e(e, "Error mapping ParseObject to ProductListing: ${obj.objectId}")
            null
        }
    }

    private fun mapProductListingToParseObject(listing: ProductListing): ParseObject {
        val obj = ParseObject(CLASS_PRODUCT_LISTING)
    if (listing.id.isNotBlank() && listing.id.length == 10 && listing.id.all { it.isLetterOrDigit() }) { // Basic check for Parse ID
             obj.objectId = listing.id
        }

        obj.put(PL_SELLER, ParseObject.createWithoutData("_User", listing.sellerId))
        obj.put(PL_TITLE, listing.title)
        obj.put(PL_DESCRIPTION, listing.description)
        obj.put(PL_CATEGORY, listing.category.name)
        listing.breed?.let { obj.put(PL_BREED, it) }
        listing.ageInWeeks?.let { obj.put(PL_AGE_IN_WEEKS, it) }
        listing.weightInKg?.let { obj.put(PL_WEIGHT_IN_KG, it) }
        obj.put(PL_PRICE, listing.price)
        obj.put(PL_CURRENCY, listing.currency)
        obj.put(PL_QUANTITY_AVAILABLE, listing.quantityAvailable)
        obj.put(PL_IMAGE_URLS, listing.imageUrls)
        listing.locationCity?.let { obj.put(PL_LOCATION_CITY, it) }
        listing.locationDistrict?.let { obj.put(PL_LOCATION_DISTRICT, it) }
        obj.put(PL_LOCATION_STATE, listing.locationState)
        listing.isOrganic?.let { obj.put(PL_IS_ORGANIC, it) }
        listing.isVaccinated?.let { obj.put(PL_IS_VACCINATED, it) }
        obj.put(PL_POSTED_DATE_TIMESTAMP, listing.postedDateTimestamp) // Store as number (Long)
        obj.put(PL_STATUS, listing.status.name)
        listing.additionalProperties?.let { obj.put(PL_ADDITIONAL_PROPERTIES, it) }

        // Create keywords for basic search
        val keywords = mutableListOf<String>()
        keywords.addAll(listing.title.lowercase().split(" ").filter { it.length > 2 })
        keywords.addAll(listing.description.lowercase().split(" ").filter { it.length > 2 })
        keywords.add(listing.category.name.lowercase())
        listing.breed?.let { keywords.add(it.lowercase()) }
        obj.put(PL_KEYWORDS, keywords.distinct())

        return obj
    }
}
