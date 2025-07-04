package com.example.rooster.feature.marketplace.data.repository

import com.example.rooster.core.common.Result
import com.example.rooster.core.common.model.Product
import com.example.rooster.core.common.model.Review
import com.example.rooster.core.common.model.Supplier
import com.example.rooster.feature.marketplace.data.local.db.ProductDao
import com.example.rooster.feature.marketplace.data.local.db.ReviewDao
import com.example.rooster.feature.marketplace.data.local.db.SupplierDao
import com.example.rooster.feature.marketplace.data.remote.MarketplaceRemoteDataSource
import com.example.rooster.feature.marketplace.domain.repository.MarketplaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import java.io.IOException


@Singleton
class MarketplaceRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val supplierDao: SupplierDao,
    private val reviewDao: ReviewDao,
    private val remoteDataSource: MarketplaceRemoteDataSource
) : MarketplaceRepository {

    // Product Operations
    override fun getProductDetails(productId: String): Flow<Result<Product?>> = flow {
        emit(Result.Loading)
        // Try fetching from remote first
        try {
            when (val remoteResult = remoteDataSource.getProductDetails(productId)) {
                is Result.Success -> {
                    remoteResult.data?.let {
                        productDao.insertProduct(it.copy(needsSync = false)) // Cache it
                        emit(Result.Success(it))
                    } ?: emit(Result.Success(null)) // Not found remotely
                }
                is Result.Error -> {
                    // Remote failed, try local cache
                    productDao.getProductById(productId).collect { localProduct ->
                        if (localProduct != null) {
                            emit(Result.Success(localProduct))
                        } else {
                            emit(Result.Error(remoteResult.exception ?: Exception("Product not found and remote fetch failed")))
                        }
                    }
                }
                Result.Loading -> { /* Already emitted */ }
            }
        } catch (e: Exception) {
            // Fallback to local cache if any exception during remote fetch process
            productDao.getProductById(productId).collect { localProduct ->
                 emit(Result.Success(localProduct)) // Emit local data, could be null
            }
        }
    }.flowOn(Dispatchers.IO)


    override fun getProductsByCategory(categoryId: String): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        // Simplified: Fetch from local cache first, then try remote.
        // A more robust solution would involve NetworkBoundResource pattern.
        productDao.getProductsByCategory(categoryId).collect { localProducts ->
            if (localProducts.isNotEmpty()) {
                emit(Result.Success(localProducts))
            }
            // Try fetching from remote
            try {
                remoteDataSource.getProductListingsStream(category = categoryId).collect { remoteResult ->
                    if (remoteResult is Result.Success) {
                        productDao.insertProducts(remoteResult.data.map { it.copy(needsSync = false) })
                        // Emit the latest from DB after remote insert
                        emit(Result.Success(productDao.getProductsByCategory(categoryId).kotlinx.coroutines.flow.first()))
                    } else if (remoteResult is Result.Error && localProducts.isEmpty()) {
                        emit(Result.Error(remoteResult.exception ?: Exception("Error fetching products by category")))
                    }
                    // If local had data, we already emitted it. This emission updates it or confirms it.
                }
            } catch (e: Exception) {
                if (localProducts.isEmpty()) emit(Result.Error(e))
            }
        }
    }.flowOn(Dispatchers.IO)


    override fun getAllProducts(page: Int?, pageSize: Int?): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        // Simplified: Fetch from local cache first. Remote fetch can be triggered by UI / ViewModel explicitly or with NetworkBoundResource.
        // This example prioritizes local cache for "get all"
        productDao.getAllProducts().collect { localProducts ->
            emit(Result.Success(localProducts))
            // Optionally, trigger a background remote fetch here to update cache
            // For now, remote fetch is separate or part of a more complex strategy
             try {
                remoteDataSource.getProductListingsStream(pageSize = pageSize ?: 20 /* provide default */).collect { remoteResult ->
                    if (remoteResult is Result.Success) {
                        productDao.insertProducts(remoteResult.data.map { it.copy(needsSync = false) })
                        // emit(Result.Success(productDao.getAllProducts().kotlinx.coroutines.flow.first())) // Re-emit from DB
                    }
                    // Don't emit error if local already provided data
                }
            } catch (e: Exception) {
                // Log error, don't necessarily propagate if local data was served
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun searchProducts(query: String, filters: Map<String, String>?): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)
        // Local search first
        productDao.searchProducts(query).collect { localResults ->
            emit(Result.Success(localResults)) // Emit local results immediately

            // Attempt remote search to augment/update
            try {
                 // Convert filters map to appropriate remote call params
                val category = filters?.get("categoryId")
                val sellerId = filters?.get("sellerId")
                remoteDataSource.getProductListingsStream(category = category, sellerId = sellerId, searchTerm = query).collect { remoteResult ->
                    if (remoteResult is Result.Success) {
                        productDao.insertProducts(remoteResult.data.map { it.copy(needsSync = false) })
                        // Re-query from DB to get merged results if strategy is to merge
                        // For simplicity, we'll let UI decide if it wants to re-fetch or rely on separate sync
                         emit(Result.Success(productDao.searchProducts(query).kotlinx.coroutines.flow.first()))
                    }
                    // Don't emit error if local already provided results
                }
            } catch (e: Exception) {
                 // Log error
            }
        }
    }.flowOn(Dispatchers.IO)


    override suspend fun createProductListing(product: Product): Result<String> = withContext(Dispatchers.IO) {
        try {
            val productWithId = if (product.id.isBlank()) product.copy(id = UUID.randomUUID().toString()) else product
            val productToSave = productWithId.copy(needsSync = true)
            productDao.insertProduct(productToSave)

            when (val remoteResult = remoteDataSource.createProductListing(productToSave)) {
                is Result.Success -> {
                    productDao.insertProduct(productToSave.copy(needsSync = false, id = remoteResult.data)) // Ensure ID from remote is used
                    Result.Success(remoteResult.data)
                }
                is Result.Error -> {
                    Result.Error(remoteResult.exception ?: Exception("Failed to create product remotely"))
                }
                Result.Loading -> Result.Error(Exception("Remote operation still loading, unexpected state"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateProductListing(product: Product): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val productToUpdate = product.copy(needsSync = true)
            productDao.updateProduct(productToUpdate) // Use updateProduct for existing items

            when (val remoteResult = remoteDataSource.updateProductListing(productToUpdate)) {
                is Result.Success -> {
                    productDao.updateProduct(productToUpdate.copy(needsSync = false))
                    Result.Success(Unit)
                }
                is Result.Error -> {
                    Result.Error(remoteResult.exception ?: Exception("Failed to update product remotely"))
                }
                 Result.Loading -> Result.Error(Exception("Remote operation still loading, unexpected state"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Supplier Operations
    override fun getSupplierProfile(supplierId: String): Flow<Result<Supplier?>> = flow {
        emit(Result.Loading)
        try {
            when (val remoteResult = remoteDataSource.getSupplierDetails(supplierId)) {
                is Result.Success -> {
                    remoteResult.data?.let {
                        supplierDao.insertSupplier(it.copy(needsSync = false))
                        emit(Result.Success(it))
                    } ?: emit(Result.Success(null))
                }
                is Result.Error -> {
                    supplierDao.getSupplierById(supplierId).collect { localSupplier ->
                        if (localSupplier != null) emit(Result.Success(localSupplier))
                        else emit(Result.Error(remoteResult.exception ?: Exception("Supplier not found")))
                    }
                }
                Result.Loading -> { /* Emitted */ }
            }
        } catch (e: Exception) {
             supplierDao.getSupplierById(supplierId).collect { localSupplier ->
                emit(Result.Success(localSupplier))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getProductsBySupplier(supplierId: String): Flow<Result<List<Product>>> = flow<Result<List<Product>>> {
        emit(Result.Loading)
        productDao.getProductsBySupplier(supplierId).collect { localProducts ->
            emit(Result.Success(localProducts)) // Emit local results

            try {
                remoteDataSource.getProductListingsStream(sellerId = supplierId).collect { remoteResult ->
                    if (remoteResult is Result.Success) {
                        productDao.insertProducts(remoteResult.data.map { it.copy(needsSync = false) })
                         // emit(Result.Success(productDao.getProductsBySupplier(supplierId).kotlinx.coroutines.flow.first())) // Re-emit
                    }
                }
            } catch (e: Exception) {
                // Log error
            }
        }
    }.flowOn(Dispatchers.IO)


    // Review Operations
    override fun getProductReviews(productId: String): Flow<Result<List<Review>>> = flow {
        emit(Result.Loading)
        reviewDao.getReviewsForProduct(productId).collect { localReviews ->
            emit(Result.Success(localReviews))
            try {
                remoteDataSource.getReviewsForProduct(productId).collect { remoteResult ->
                    if (remoteResult is Result.Success) {
                        reviewDao.insertReviews(remoteResult.data.map { it.copy(needsSync = false) })
                        // emit(Result.Success(reviewDao.getReviewsForProduct(productId).kotlinx.coroutines.flow.first())) // Re-emit
                    }
                }
            } catch (e: Exception) { /* Log error */ }
        }
    }.flowOn(Dispatchers.IO)

    override fun getSupplierReviews(supplierId: String): Flow<Result<List<Review>>> = flow {
        emit(Result.Loading)
        reviewDao.getReviewsForSupplier(supplierId).collect { localReviews ->
            emit(Result.Success(localReviews))
            try {
                remoteDataSource.getReviewsForSupplier(supplierId).collect { remoteResult ->
                    if (remoteResult is Result.Success) {
                        reviewDao.insertReviews(remoteResult.data.map { it.copy(needsSync = false) })
                        // emit(Result.Success(reviewDao.getReviewsForSupplier(supplierId).kotlinx.coroutines.flow.first())) // Re-emit
                    }
                }
            } catch (e: Exception) { /* Log error */ }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun submitReview(review: Review): Result<String> = withContext(Dispatchers.IO) {
        try {
            val reviewWithId = if (review.id.isBlank()) review.copy(id = UUID.randomUUID().toString()) else review
            val reviewToSave = reviewWithId.copy(needsSync = true)
            reviewDao.insertReview(reviewToSave)

            when (val remoteResult = remoteDataSource.submitReview(reviewToSave)) {
                is Result.Success -> {
                    reviewDao.insertReview(reviewToSave.copy(needsSync = false, id = remoteResult.data))
                    Result.Success(remoteResult.data)
                }
                is Result.Error -> Result.Error(remoteResult.exception ?: Exception("Failed to submit review remotely"))
                Result.Loading -> Result.Error(Exception("Remote operation still loading, unexpected state"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateReview(review: Review): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val reviewToUpdate = review.copy(needsSync = true)
            reviewDao.updateReview(reviewToUpdate)
            // Assume remoteDataSource.updateReview(review) exists and works similarly
            // For now, this is a placeholder for remote update
            // val remoteResult = remoteDataSource.updateReview(reviewToUpdate)
            // if (remoteResult is Result.Success) {
            //    reviewDao.updateReview(reviewToUpdate.copy(needsSync = false))
            //    Result.Success(Unit)
            // } else { ... }
            Result.Success(Unit) // Placeholder success
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteReview(reviewId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            reviewDao.deleteReviewById(reviewId)
            // Assume remoteDataSource.deleteReview(reviewId) exists
            // val remoteResult = remoteDataSource.deleteReview(reviewId)
            // if (remoteResult is Result.Success) { ... }
            Result.Success(Unit) // Placeholder success
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    // Sync Operations (Placeholders - to be implemented with WorkManager or similar)
    override suspend fun syncPendingProducts(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val unsyncedProducts = productDao.getProductsForSync()
            for (product in unsyncedProducts) {
                // Attempt to sync with remoteDataSource.createProductListing or updateProductListing
                // If successful, update product.needsSync = false in DAO
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun syncPendingReviews(): Result<Unit> = withContext(Dispatchers.IO) {
         try {
            val unsyncedReviews = reviewDao.getReviewsForSync()
            for (review in unsyncedReviews) {
                // Attempt to sync with remoteDataSource.submitReview or update
                // If successful, update review.needsSync = false in DAO
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
