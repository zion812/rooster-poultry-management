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
                    FeedType.GLOBAL_RECENT -> localDataSource.getAllPosts()
                    FeedType.USER_SPECIFIC -> if (userId != null) localDataSource.getPostsByAuthor(userId) else flowOf(emptyList())
                    FeedType.TAG_SPECIFIC -> if (userId != null) localDataSource.getPostsByTag(userId) else flowOf(emptyList()) // Assuming 'userId' here was meant to be 'tag' from interface
                    // FeedType.FOLLOWING would need more complex local querying or rely more on remote
                    else -> localDataSource.getAllPosts() // Fallback for e.g. FOLLOWING
=======
                    FeedType.GLOBAL_RECENT -> localDataSource.getAllPosts() // TODO: Add pagination to DAO
                    FeedType.USER_SPECIFIC -> if (userId != null) localDataSource.getPostsByAuthor(userId) else flowOf(emptyList())
                    // FeedType.FOLLOWING and FeedType.TAG_SPECIFIC would need more complex local querying or rely more on remote
                    else -> localDataSource.getAllPosts() // Fallback
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
                // Assuming remote can handle these, or it fetches a general list and client filters.
                // The remoteDataSource.getPostsStream is a Flow, so take first for this pattern.
                remoteDataSource.getPostsStream(feedType, userId, null /* TODO: tag for TAG_SPECIFIC */).firstOrNull()
                    ?: Result.Success(emptyList()) // Provide default if stream is empty or ends fast
 main
            },
            saveRemoteResult = { posts ->
                val entities = posts.map { mapDomainToEntity(it, needsSync = false) }
                localDataSource.insertPosts(entities) // Assumes OnConflictStrategy.REPLACE
            },
            shouldFetch = { localData -> forceRefresh || localData.isNullOrEmpty() }
        ).flowOn(Dispatchers.IO)
    }

    override fun getPostDetails(postId: String): Flow<Result<Post?>> {
         return localBackedCommunityResource(
            localCall = { localDataSource.getPostById(postId).map { it?.let(::mapEntityToDomain) } },
            remoteCall = { remoteDataSource.getPostDetailsStream(postId).firstOrNull() ?: Result.Success(null) },
            saveRemoteResult = { post ->
                if (post != null) {
                    localDataSource.insertPost(mapDomainToEntity(post, needsSync = false))
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
            // Optionally update local PostEntity's likeCount here for immediate UI feedback
            // val post = localDataSource.getPostById(postId) ... (this is a Flow, need suspend fun)
            // localDataSource.updateLikeCount(postId, newCount)
        }
        return@withContext remoteResult
    }

    override suspend fun unlikePost(postId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        // Similar to likePost, needs more robust implementation.
        return@withContext remoteDataSource.unlikePost(postId, userId)
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
