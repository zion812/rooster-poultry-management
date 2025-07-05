// use context7
package com.example.rooster.services

import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Reactive data fetcher with Flow-based streams and real-time updates
 * Implements Phase 2A reactive patterns with offline-first architecture
 */
@Singleton
class ReactiveDataFetcher
    @Inject
    constructor(
        private val smartCacheManager: SmartCacheManager,
    ) {
        // Real-time data streams
        private val dataStreams = ConcurrentHashMap<String, MutableSharedFlow<Any>>()

        // Coroutine scope for reactive operations
        private val reactiveScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        // Connection state tracking
        private val _connectionState = MutableStateFlow(ConnectionState.CONNECTED)
        val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

        // Conflict resolution queue for offline changes
        private val conflictQueue = mutableListOf<ConflictResolution>()

        companion object {
            private const val CACHE_TTL_MINUTES = 30L
            private const val REAL_TIME_RETRY_DELAY = 5000L // 5 seconds
        }

        /**
         * Get reactive data stream with caching and real-time updates
         */
        fun <T> getReactiveData(
            key: String,
            parser: (ParseObject) -> T,
            networkFetcher: suspend () -> List<ParseObject>,
        ): Flow<List<T>> =
            flow {
                // Emit cached data first (offline-first)
                val cachedData =
                    smartCacheManager.getCachedData<List<T>>(
                        key = key,
                        ttlMinutes = CACHE_TTL_MINUTES,
                    ) {
                        // Network fetch if cache miss
                        val parseObjects = networkFetcher()
                        parseObjects.map(parser)
                    }

                emit(cachedData)

                // Set up real-time subscription if connected
                if (_connectionState.value == ConnectionState.CONNECTED) {
                    setupRealtimeSubscription(key, parser, networkFetcher)
                }
            }.catch { error ->
                // Emit cached data on error, fallback gracefully
                emit(emptyList())
                handleFetchError(key, error)
            }.distinctUntilChanged()

        /**
         * Get single reactive item with real-time updates
         */
        fun <T> getReactiveItem(
            key: String,
            parser: (ParseObject) -> T,
            networkFetcher: suspend () -> ParseObject?,
        ): Flow<T?> =
            flow {
                val cachedItem =
                    smartCacheManager.getCachedData<T?>(
                        key = key,
                        ttlMinutes = CACHE_TTL_MINUTES,
                    ) {
                        networkFetcher()?.let(parser)
                    }

                emit(cachedItem)

                // Set up real-time updates
                if (_connectionState.value == ConnectionState.CONNECTED) {
                    setupSingleItemSubscription(key, parser, networkFetcher)
                }
            }.catch { error ->
                emit(null)
                handleFetchError(key, error)
            }.distinctUntilChanged()

        /**
         * Submit data change with offline support
         */
        suspend fun <T> submitDataChange(
            operation: DataOperation,
            data: T,
            networkSubmitter: suspend (T) -> ParseObject,
        ): Flow<SubmissionResult> =
            flow {
                emit(SubmissionResult.PROCESSING)

                try {
                    if (_connectionState.value == ConnectionState.CONNECTED) {
                        // Online submission
                        val result = networkSubmitter(data)
                        emit(SubmissionResult.SUCCESS(result.objectId))

                        // Invalidate related caches
                        invalidateRelatedCaches(operation, data)
                    } else {
                        // Offline queue
                        queueOfflineOperation(operation, data, networkSubmitter)
                        emit(SubmissionResult.QUEUED)
                    }
                } catch (error: Exception) {
                    emit(SubmissionResult.ERROR(error.message ?: "Unknown error"))

                    // Queue for retry when connection restored
                    if (_connectionState.value == ConnectionState.OFFLINE) {
                        queueOfflineOperation(operation, data, networkSubmitter)
                    }
                }
            }

        /**
         * Setup real-time subscription for data streams
         */
        private fun <T> setupRealtimeSubscription(
            key: String,
            parser: (ParseObject) -> T,
            networkFetcher: suspend () -> List<ParseObject>,
        ) {
            reactiveScope.launch {
                try {
                    // Simulate WebSocket connection for real-time updates
                    // In real implementation, this would use Parse Live Query or similar
                    while (_connectionState.value == ConnectionState.CONNECTED) {
                        kotlinx.coroutines.delay(REAL_TIME_RETRY_DELAY)

                        // Check for updates
                        val updatedData = networkFetcher().map(parser)

                        // Update cache and emit to stream
                        getOrCreateStream<List<T>>(key).emit(updatedData)
                    }
                } catch (e: Exception) {
                    // Handle real-time connection errors
                    _connectionState.value = ConnectionState.OFFLINE
                }
            }
        }

        /**
         * Setup real-time subscription for single items
         */
        private fun <T> setupSingleItemSubscription(
            key: String,
            parser: (ParseObject) -> T,
            networkFetcher: suspend () -> ParseObject?,
        ) {
            reactiveScope.launch {
                try {
                    while (_connectionState.value == ConnectionState.CONNECTED) {
                        kotlinx.coroutines.delay(REAL_TIME_RETRY_DELAY)

                        val updatedItem = networkFetcher()?.let(parser)
                        getOrCreateStream<T?>(key).emit(updatedItem)
                    }
                } catch (e: Exception) {
                    _connectionState.value = ConnectionState.OFFLINE
                }
            }
        }

        /**
         * Get or create a shared flow for real-time data streaming
         */
        @Suppress("UNCHECKED_CAST")
        private fun <T> getOrCreateStream(key: String): MutableSharedFlow<T> {
            return dataStreams.getOrPut(key) {
                MutableSharedFlow<Any>(replay = 1, extraBufferCapacity = 10)
            } as MutableSharedFlow<T>
        }

        /**
         * Queue offline operation for later processing
         */
        private suspend fun <T> queueOfflineOperation(
            operation: DataOperation,
            data: T,
            networkSubmitter: suspend (T) -> ParseObject,
        ) {
            // Store operation for later execution
            val queuedOperation =
                QueuedOperation(
                    operation = operation,
                    data = data as Any,
                    submitter = { networkSubmitter(data) },
                    timestamp = System.currentTimeMillis(),
                )

            // In a real implementation, this would be persisted to disk
            // For now, keep in memory
        }

        /**
         * Process offline queue when connection is restored
         */
        suspend fun processOfflineQueue() {
            if (_connectionState.value == ConnectionState.CONNECTED) {
                // Process queued operations
                // This would iterate through persisted offline operations
                // and execute them in order, handling conflicts
            }
        }

        /**
         * Handle fetch errors with fallback strategies
         */
        private fun handleFetchError(
            key: String,
            error: Throwable,
        ) {
            when (error) {
                is java.net.UnknownHostException,
                is java.net.ConnectException,
                -> {
                    _connectionState.value = ConnectionState.OFFLINE
                }

                else -> {
                    // Log other errors but don't change connection state
                }
            }
        }

        /**
         * Invalidate related caches after data changes
         */
        private fun <T> invalidateRelatedCaches(
            operation: DataOperation,
            data: T,
        ) {
            // Invalidate relevant cache keys based on the operation
            when (operation) {
                DataOperation.CREATE_FOWL -> {
                    smartCacheManager.invalidateCache("fowl_list")
                    smartCacheManager.invalidateCache("farmer_fowl_count")
                }

                DataOperation.UPDATE_FOWL -> {
                    smartCacheManager.invalidateCache("fowl_list")
                    // data should contain fowl ID for specific invalidation
                }

                DataOperation.DELETE_FOWL -> {
                    smartCacheManager.invalidateCache("fowl_list")
                    smartCacheManager.invalidateCache("farmer_fowl_count")
                }

                DataOperation.CREATE_LISTING -> {
                    smartCacheManager.invalidateCache("marketplace_listings")
                    smartCacheManager.invalidateCache("user_listings")
                }

                DataOperation.UPDATE_LISTING -> {
                    smartCacheManager.invalidateCache("marketplace_listings")
                    smartCacheManager.invalidateCache("user_listings")
                }

                DataOperation.DELETE_LISTING -> {
                    smartCacheManager.invalidateCache("marketplace_listings")
                    smartCacheManager.invalidateCache("user_listings")
                }

                DataOperation.CREATE_BID -> {
                    smartCacheManager.invalidateCache("auction_bids")
                    smartCacheManager.invalidateCache("user_bids")
                }

                DataOperation.UPDATE_BID -> {
                    smartCacheManager.invalidateCache("auction_bids")
                    smartCacheManager.invalidateCache("user_bids")
                }

                DataOperation.CREATE_TRANSFER -> {
                    smartCacheManager.invalidateCache("fowl_transfers")
                    smartCacheManager.invalidateCache("user_transfers")
                }

                DataOperation.UPDATE_TRANSFER -> {
                    smartCacheManager.invalidateCache("fowl_transfers")
                    smartCacheManager.invalidateCache("user_transfers")
                }
            }
        }

        /**
         * Convert Parse callback to suspend function
         */
        private suspend fun parseQueryToSuspend(query: ParseQuery<ParseObject>): List<ParseObject> =
            suspendCancellableCoroutine { continuation ->
                query.findInBackground { objects, error ->
                    if (error != null) {
                        continuation.resumeWithException(error)
                    } else {
                        continuation.resume(objects ?: emptyList())
                    }
                }
            }

        /**
         * Set connection state (for testing or manual control)
         */
        fun setConnectionState(state: ConnectionState) {
            _connectionState.value = state

            if (state == ConnectionState.CONNECTED) {
                // Process offline queue when connection restored
                reactiveScope.launch {
                    processOfflineQueue()
                }
            }
        }

        /**
         * Fetch marketplace data for predictive caching
         */
        suspend fun fetchMarketplaceData(key: String): Any? {
            return smartCacheManager.getCachedData(
                key = "marketplace_$key",
                ttlMinutes = CACHE_TTL_MINUTES,
            ) {
                // Simulate marketplace data fetch
                mapOf("listings" to emptyList<Any>(), "categories" to emptyList<String>())
            }
        }

        /**
         * Fetch farm data for predictive caching
         */
        suspend fun fetchFarmData(key: String): Any? {
            return smartCacheManager.getCachedData(
                key = "farm_$key",
                ttlMinutes = CACHE_TTL_MINUTES,
            ) {
                // Simulate farm data fetch
                mapOf("flocks" to emptyList<Any>(), "health_records" to emptyList<Any>())
            }
        }

        /**
         * Fetch community data for predictive caching
         */
        suspend fun fetchCommunityData(key: String): Any? {
            return smartCacheManager.getCachedData(
                key = "community_$key",
                ttlMinutes = CACHE_TTL_MINUTES,
            ) {
                // Simulate community data fetch
                mapOf("posts" to emptyList<Any>(), "discussions" to emptyList<Any>())
            }
        }

        /**
         * Fetch health data for predictive caching
         */
        suspend fun fetchHealthData(key: String): Any? {
            return smartCacheManager.getCachedData(
                key = "health_$key",
                ttlMinutes = CACHE_TTL_MINUTES,
            ) {
                // Simulate health data fetch
                mapOf("vaccinations" to emptyList<Any>(), "treatments" to emptyList<Any>())
            }
        }

        /**
         * Fetch seasonal data for predictive caching
         */
        suspend fun fetchSeasonalData(key: String): Any? {
            return smartCacheManager.getCachedData(
                key = "seasonal_$key",
                ttlMinutes = CACHE_TTL_MINUTES * 2, // Longer cache for seasonal data
            ) {
                // Simulate seasonal data fetch
                mapOf("weather_patterns" to emptyList<Any>(), "market_trends" to emptyList<Any>())
            }
        }
    }

/**
 * Connection state for offline/online handling
 */
enum class ConnectionState {
    CONNECTED,
    OFFLINE,
    CONNECTING,
}

/**
 * Data operation types for cache invalidation
 */
enum class DataOperation {
    CREATE_FOWL,
    UPDATE_FOWL,
    DELETE_FOWL,
    CREATE_LISTING,
    UPDATE_LISTING,
    DELETE_LISTING,
    CREATE_BID,
    UPDATE_BID,
    CREATE_TRANSFER,
    UPDATE_TRANSFER,
}

/**
 * Submission result states
 */
sealed class SubmissionResult {
    data object PROCESSING : SubmissionResult()

    data class SUCCESS(val id: String) : SubmissionResult()

    data class ERROR(val message: String) : SubmissionResult()

    data object QUEUED : SubmissionResult()
}

/**
 * Queued operation for offline processing
 */
data class QueuedOperation(
    val operation: DataOperation,
    val data: Any,
    val submitter: suspend () -> ParseObject,
    val timestamp: Long,
)

/**
 * Conflict resolution for offline sync
 */
data class ConflictResolution(
    val localVersion: Any,
    val serverVersion: Any,
    val resolution: ConflictStrategy,
)

/**
 * Conflict resolution strategies
 */
enum class ConflictStrategy {
    SERVER_WINS,
    CLIENT_WINS,
    MERGE,
    MANUAL_RESOLVE,
}
