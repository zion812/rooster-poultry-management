package com.example.rooster.feature.community.data.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.data.local.dao.PostDao
import com.example.rooster.feature.community.data.local.model.PostEntity
import com.example.rooster.feature.community.data.remote.CommunityRemoteDataSource
import com.example.rooster.feature.community.domain.model.Post
import com.example.rooster.feature.community.domain.repository.FeedType
import com.example.rooster.feature.community.domain.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val localDataSource: PostDao,
    private val remoteDataSource: CommunityRemoteDataSource
) : PostRepository {

    override fun getPosts(feedType: FeedType, userId: String?, forceRefresh: Boolean): Flow<Result<List<Post>>> {
        // This is a simplified implementation. A real feed would involve pagination,
        // complex logic for 'FOLLOWING' feed (fetching followed user IDs, then their posts),
        // and potentially server-side aggregation.
        return localBackedCommunityResourceList(
            localCall = {
                val localPostsFlow = when (feedType) {
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
 main
 main
                    FeedType.GLOBAL_RECENT -> localDataSource.getAllPosts()
                    FeedType.USER_SPECIFIC -> if (userId != null) localDataSource.getPostsByAuthor(userId) else flowOf(emptyList())
                    FeedType.TAG_SPECIFIC -> if (userId != null) localDataSource.getPostsByTag(userId) else flowOf(emptyList()) // Assuming 'userId' here was meant to be 'tag' from interface
                    // FeedType.FOLLOWING would need more complex local querying or rely more on remote
                    else -> localDataSource.getAllPosts() // Fallback for e.g. FOLLOWING
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
=======
                    FeedType.GLOBAL_RECENT -> localDataSource.getAllPosts() // TODO: Add pagination to DAO
                    FeedType.USER_SPECIFIC -> if (userId != null) localDataSource.getPostsByAuthor(userId) else flowOf(emptyList())
                    // FeedType.FOLLOWING and FeedType.TAG_SPECIFIC would need more complex local querying or rely more on remote
                    else -> localDataSource.getAllPosts() // Fallback
 main
 main
 main
                }
                localPostsFlow.map { entities -> entities.map { mapEntityToDomain(it) } }
            },
            remoteCall = {
 jules/arch-assessment-1
                val tagForRemote = if (feedType == FeedType.TAG_SPECIFIC) userId else null // Assuming userId variable here holds the tag for TAG_SPECIFIC
                remoteDataSource.getPostsStream(feedType, if (feedType == FeedType.USER_SPECIFIC) userId else null, tagForRemote).firstOrNull()
                    ?: Result.Success(emptyList())
=======
 jules/arch-assessment-1
                val tagForRemote = if (feedType == FeedType.TAG_SPECIFIC) userId else null // Assuming userId variable here holds the tag for TAG_SPECIFIC
                remoteDataSource.getPostsStream(feedType, if (feedType == FeedType.USER_SPECIFIC) userId else null, tagForRemote).firstOrNull()
                    ?: Result.Success(emptyList())
=======
 jules/arch-assessment-1
                val tagForRemote = if (feedType == FeedType.TAG_SPECIFIC) userId else null // Assuming userId variable here holds the tag for TAG_SPECIFIC
                remoteDataSource.getPostsStream(feedType, if (feedType == FeedType.USER_SPECIFIC) userId else null, tagForRemote).firstOrNull()
                    ?: Result.Success(emptyList())
=======
                // Assuming remote can handle these, or it fetches a general list and client filters.
                // The remoteDataSource.getPostsStream is a Flow, so take first for this pattern.
                remoteDataSource.getPostsStream(feedType, userId, null /* TODO: tag for TAG_SPECIFIC */).firstOrNull()
                    ?: Result.Success(emptyList()) // Provide default if stream is empty or ends fast
 main
 main
 main
            },
            saveRemoteResult = { remotePosts -> // List<Post>
                val entitiesToSave = mutableListOf<PostEntity>()
                var skippedCount = 0
                for (remotePost in remotePosts) {
                    val localUnsyncedEntity = localDataSource.getPostByIdSuspend(remotePost.postId)
                    if (localUnsyncedEntity?.needsSync == true) {
                        Timber.w("Community: Local post ID ${remotePost.postId} has unsynced changes during batch update. Skipping remote overwrite.")
                        skippedCount++
                    } else {
                        entitiesToSave.add(mapDomainToEntity(remotePost, needsSync = false))
                    }
                }
                if (entitiesToSave.isNotEmpty()) {
                    localDataSource.insertPosts(entitiesToSave)
                    Timber.d("Community: Saved/Updated ${entitiesToSave.size} posts in cache from remote.")
                }
                if (skippedCount > 0) {
                    Timber.d("Community: Skipped $skippedCount unsynced local posts during remote update for feed.")
                }
            },
            shouldFetch = { localData -> forceRefresh || localData.isNullOrEmpty() }
        ).flowOn(Dispatchers.IO)
    }

    override fun getPostDetails(postId: String): Flow<Result<Post?>> {
         return localBackedCommunityResource(
            localCall = { localDataSource.getPostById(postId).map { it?.let(::mapEntityToDomain) } },
            remoteCall = { remoteDataSource.getPostDetailsStream(postId).firstOrNull() ?: Result.Success(null) },
            saveRemoteResult = { remotePostDomain -> // S is Post
                if (remotePostDomain != null) {
                    val localEntity = localDataSource.getPostByIdSuspend(remotePostDomain.postId) // Using the already existing suspend fun
                    if (localEntity?.needsSync == true) {
                        Timber.w("Community: Local post ID ${remotePostDomain.postId} has unsynced changes. Remote update from listener/fetch will be ignored.")
                    } else {
                        localDataSource.insertPost(mapDomainToEntity(remotePostDomain, needsSync = false))
                        Timber.d("Community: Cache updated from remote for post ID ${remotePostDomain.postId}.")
                    }
                }
            },
            shouldFetch = { localData -> localData == null } // Fetch if not in cache
        ).flowOn(Dispatchers.IO)
    }

    override suspend fun createPost(post: Post): Result<String> = withContext(Dispatchers.IO) {
        try {
            val postWithId = if (post.postId.isBlank()) post.copy(postId = UUID.randomUUID().toString()) else post
            val entity = mapDomainToEntity(postWithId, needsSync = true)
            localDataSource.insertPost(entity)

            val remoteResult = remoteDataSource.createPost(postWithId)
            if (remoteResult is Result.Success && remoteResult.data.isNotBlank()) {
                localDataSource.insertPost(entity.copy(needsSync = false, postId = remoteResult.data)) // Use ID from remote
                Result.Success(remoteResult.data)
            } else if (remoteResult is Result.Error) {
                Result.Error(remoteResult.exception)
            } else {
                Result.Error(Exception("Remote data source returned invalid ID for created post"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updatePost(post: Post): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = mapDomainToEntity(post, needsSync = true)
            localDataSource.updatePost(entity) // or insertPost with REPLACE
            val remoteResult = remoteDataSource.updatePost(post)
            if (remoteResult is Result.Success) {
                localDataSource.updatePost(entity.copy(needsSync = false))
            }
            remoteResult
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deletePost(postId: String, authorUserId: String): Result<Unit> = withContext(Dispatchers.IO) {
        // TODO: Add local check if authorUserId matches post's author before attempting delete,
        // though server-side rules are the ultimate authority.
        try {
            val remoteResult = remoteDataSource.deletePost(postId)
            if (remoteResult is Result.Success) {
                localDataSource.deletePostById(postId)
            }
            remoteResult
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun likePost(postId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        // This is a simplified version. A real version would:
        // 1. Optimistically update local like count (if stored denormalized on PostEntity).
        // 2. Call remoteDataSource.likePost().
        // 3. On remote success, confirm local update. On failure, revert optimistic update.
        // 4. Handle needsSync for like action if it needs to be robustly offline.
        // For now, just pass through to remote.
        val remoteResult = remoteDataSource.likePost(postId, userId)
        if (remoteResult is Result.Success) {
            // Update local data after successful remote operation
            val currentPostEntity = localDataSource.getPostByIdSuspend(postId)
            if (currentPostEntity != null) {
                val newLikedBy = currentPostEntity.likedBy.toMutableList().apply { add(userId) }.distinct()
                val updatedEntity = currentPostEntity.copy(
                    likeCount = newLikedBy.size, // Recalculate based on distinct list size
                    likedBy = newLikedBy
                )
                localDataSource.updatePost(updatedEntity)
            }
        }
        return@withContext remoteResult
    }

    override suspend fun unlikePost(postId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val remoteResult = remoteDataSource.unlikePost(postId, userId)
        if (remoteResult is Result.Success) {
            // Update local data after successful remote operation
            val currentPostEntity = localDataSource.getPostByIdSuspend(postId)
            if (currentPostEntity != null) {
                val newLikedBy = currentPostEntity.likedBy.toMutableList().apply { remove(userId) }
                val updatedEntity = currentPostEntity.copy(
                    likeCount = newLikedBy.size, // Recalculate
                    likedBy = newLikedBy
                )
                localDataSource.updatePost(updatedEntity)
            }
        }
        return@withContext remoteResult
    }

import timber.log.Timber // Ensure Timber is imported

    override suspend fun getUnsyncedPostEntities(): List<PostEntity> = withContext(Dispatchers.IO) {
        localDataSource.getUnsyncedPostsSuspend()
    }

    override suspend fun syncPostRemote(post: Post): Result<Unit> = withContext(Dispatchers.IO) {
        // This method ONLY attempts the remote synchronization.
        // Assumes post.postId is correctly populated if it's an update,
        // or remoteDataSource.createPost handles ID generation and returns it for new posts.
        try {
            // If post.postId is blank, it's a new post. If not blank, it's an update of an existing one.
            // remoteDataSource.createPost is expected to handle this "upsert" logic or use post.postId.
            val remoteResult = if (post.postId.isNotEmpty() && localDataSource.getPostByIdSuspend(post.postId)?.needsSync == true) {
                 // This implies it was an existing local post that was modified and needs re-syncing (update)
                 // However, createPost in Firebase might just overwrite. For true "update" semantics,
                 // remoteDataSource.updatePost(post) would be better if the post definitely exists remotely.
                 // For now, createPost will serve as upsert.
                 remoteDataSource.createPost(post) // effectively an update if ID exists
            } else {
                 remoteDataSource.createPost(post) // new post
            }

            if (remoteResult is Result.Success && remoteResult.data.isNotBlank()) {
                // The actual remote Post ID is in remoteResult.data, which might be new or same.
                // Worker will use this to update the local entity.
                Result.Success(Unit) // Signal success, worker handles local update with correct ID
            } else if (remoteResult is Result.Error) {
                Timber.e(remoteResult.exception, "Failed to sync post ${post.postId} to remote.")
                Result.Error(remoteResult.exception)
            } else {
                Timber.w("Remote sync for post ${post.postId} did not return a specific error/ID but was not successful.")
                Result.Error(Exception("Unknown error or unsuccessful remote sync for post ${post.postId}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during remote post sync for ${post.postId}")
            Result.Error(e)
        }
    }

    override suspend fun updateLocalPostEntity(postEntity: PostEntity) {
        withContext(Dispatchers.IO) {
            localDataSource.insertPost(postEntity) // OnConflictStrategy.REPLACE
        }
    }

    override fun mapPostEntityToDomain(postEntity: PostEntity): Post {
        return mapEntityToDomain(postEntity)
    }

    // --- Mappers ---
    private fun mapEntityToDomain(entity: PostEntity): Post {
        return Post(
            postId = entity.postId,
            authorUserId = entity.authorUserId,
            authorDisplayName = entity.authorDisplayName,
            authorProfilePictureUrl = entity.authorProfilePictureUrl,
            contentText = entity.contentText,
            imageUrls = entity.imageUrls,
            videoUrl = entity.videoUrl,
            createdTimestamp = entity.createdTimestamp,
            updatedTimestamp = entity.updatedTimestamp,
            likeCount = entity.likeCount,
            commentCount = entity.commentCount,
            shareCount = entity.shareCount,
            tags = entity.tags,
            location = entity.location,
            mentionsUserIds = entity.mentionsUserIds,
            isEdited = entity.isEdited,
            likedBy = entity.likedBy,
            relatedFlockId = entity.relatedFlockId
        )
    }

    private fun mapDomainToEntity(domain: Post, needsSync: Boolean): PostEntity {
        return PostEntity(
            postId = domain.postId.ifBlank { UUID.randomUUID().toString() },
            authorUserId = domain.authorUserId,
            authorDisplayName = domain.authorDisplayName,
            authorProfilePictureUrl = domain.authorProfilePictureUrl,
            contentText = domain.contentText,
            imageUrls = domain.imageUrls,
            videoUrl = domain.videoUrl,
            createdTimestamp = domain.createdTimestamp,
            updatedTimestamp = domain.updatedTimestamp,
            likeCount = domain.likeCount,
            commentCount = domain.commentCount,
            shareCount = domain.shareCount,
            tags = domain.tags,
            location = domain.location,
            mentionsUserIds = domain.mentionsUserIds,
            isEdited = domain.isEdited,
            relatedFlockId = domain.relatedFlockId,
            likedBy = domain.likedBy,
            needsSync = needsSync
        )
    }
}

// Re-define or import generic helper for network-bound resource pattern
private inline fun <D, S> localBackedCommunityResource(
    crossinline localCall: () -> Flow<D?>,
    crossinline remoteCall: suspend () -> Result<S?>,
    crossinline saveRemoteResult: suspend (S) -> Unit,
    crossinline shouldFetch: (D?) -> Boolean = { true }
): Flow<Result<D?>> = flow<Result<D?>> {
    emit(Result.Loading)
    val localData = localCall().firstOrNull()
    if (localData != null) { emit(Result.Success(localData)) }
    if (shouldFetch(localData)) {
        when (val remoteResult = remoteCall()) {
            is Result.Success -> {
                if (remoteResult.data != null) {
                    saveRemoteResult(remoteResult.data); localCall().collect { emit(Result.Success(it)) }
                } else { if (localData == null) emit(Result.Success(null)) }
            }
            is Result.Error -> emit(Result.Error(remoteResult.exception, localData))
            Result.Loading -> {}
        }
    } else if (localData == null) { emit(Result.Success(null)) }
}.catch { e -> emit(Result.Error(e)) }

private inline fun <D, S> localBackedCommunityResourceList(
    crossinline localCall: () -> Flow<List<D>>,
    crossinline remoteCall: suspend () -> Result<List<S>>,
    crossinline saveRemoteResult: suspend (List<S>) -> Unit,
    crossinline shouldFetch: (List<D>?) -> Boolean = { true }
): Flow<Result<List<D>>> = flow<Result<List<D>>> {
    emit(Result.Loading)
    val localData = localCall().firstOrNull()
    if (localData != null && localData.isNotEmpty()) { emit(Result.Success(localData)) }
    if (shouldFetch(localData)) {
        when (val remoteResult = remoteCall()) {
            is Result.Success -> {
                saveRemoteResult(remoteResult.data); localCall().collect { emit(Result.Success(it)) }
            }
            is Result.Error -> emit(Result.Error(remoteResult.exception, localData ?: emptyList()))
            Result.Loading -> {}
        }
    } else if (localData == null || localData.isEmpty()) { emit(Result.Success(emptyList())) }
}.catch { e -> emit(Result.Error(e)) }
