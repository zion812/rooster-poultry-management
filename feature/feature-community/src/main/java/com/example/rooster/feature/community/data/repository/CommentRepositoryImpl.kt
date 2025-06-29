package com.example.rooster.feature.community.data.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.data.local.dao.CommentDao
import com.example.rooster.feature.community.data.local.model.CommentEntity
import com.example.rooster.feature.community.data.remote.CommunityRemoteDataSource
import com.example.rooster.feature.community.domain.model.Comment
import com.example.rooster.feature.community.domain.repository.CommentRepository
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
class CommentRepositoryImpl @Inject constructor(
    private val localDataSource: CommentDao,
    private val remoteDataSource: CommunityRemoteDataSource
) : CommentRepository {

    override fun getCommentsForPost(postId: String, forceRefresh: Boolean): Flow<Result<List<Comment>>> {
        return localBackedCommunityResourceList(
            localCall = { localDataSource.getCommentsForPost(postId).map { entities -> entities.map { mapEntityToDomain(it) } } },
            remoteCall = { remoteDataSource.getCommentsStream(postId).firstOrNull() ?: Result.Success(emptyList()) },
            saveRemoteResult = { comments ->
                val entities = comments.map { mapDomainToEntity(it, needsSync = false) }
                localDataSource.insertComments(entities) // Assumes OnConflictStrategy.REPLACE
            },
            shouldFetch = { localData -> forceRefresh || localData.isNullOrEmpty() }
        ).flowOn(Dispatchers.IO)
    }

    override fun getRepliesForComment(commentId: String, forceRefresh: Boolean): Flow<Result<List<Comment>>> {
        return localBackedCommunityResourceList(
            localCall = { localDataSource.getRepliesForComment(commentId).map { entities -> entities.map { mapEntityToDomain(it) } } },
            remoteCall = { remoteDataSource.getCommentRepliesStream(commentId).firstOrNull() ?: Result.Success(emptyList()) },
            saveRemoteResult = { replies ->
                val entities = replies.map { mapDomainToEntity(it, needsSync = false) }
                localDataSource.insertComments(entities)
            },
            shouldFetch = { localData -> forceRefresh || localData.isNullOrEmpty() }
        ).flowOn(Dispatchers.IO)
    }

    override suspend fun addComment(comment: Comment): Result<String> = withContext(Dispatchers.IO) {
        try {
            val commentWithId = if (comment.commentId.isBlank()) comment.copy(commentId = UUID.randomUUID().toString()) else comment
            val entity = mapDomainToEntity(commentWithId, needsSync = true)
            localDataSource.insertComment(entity)

            // TODO: Increment commentCount on Post locally (optimistic) and remotely (transaction/function)

            val remoteResult = remoteDataSource.addComment(commentWithId)
            if (remoteResult is Result.Success && remoteResult.data.isNotBlank()) {
                localDataSource.insertComment(entity.copy(needsSync = false, commentId = remoteResult.data))
                Result.Success(remoteResult.data)
            } else if (remoteResult is Result.Error) {
                Result.Error(remoteResult.exception)
            } else {
                Result.Error(Exception("Remote data source returned invalid ID for created comment"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateComment(comment: Comment): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = mapDomainToEntity(comment, needsSync = true)
            localDataSource.updateComment(entity) // or insertComment with REPLACE
            val remoteResult = remoteDataSource.updateComment(comment)
            if (remoteResult is Result.Success) {
                localDataSource.updateComment(entity.copy(needsSync = false))
            }
            remoteResult
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteComment(commentId: String, authorUserId: String): Result<Unit> = withContext(Dispatchers.IO) {
        // TODO: Local author check, server-side rules are primary.
        // TODO: Decrement commentCount on Post.
        try {
            val remoteResult = remoteDataSource.deleteComment(commentId)
            if (remoteResult is Result.Success) {
                localDataSource.deleteCommentById(commentId)
            }
            remoteResult
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun likeComment(commentId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        // Simplified: pass-through. Needs optimistic update + robust offline handling.
        val remoteResult = remoteDataSource.likeComment(commentId, userId)
        if (remoteResult is Result.Success) {
            // localDataSource.updateLikeCount(commentId, newCount) // Needs method to get current comment and update
        }
        return@withContext remoteResult
    }

    override suspend fun unlikeComment(commentId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        // Simplified: pass-through.
        return@withContext remoteDataSource.unlikeComment(commentId, userId)
    }

    override suspend fun getUnsyncedComments(): List<Comment> = withContext(Dispatchers.IO) {
        localDataSource.getUnsyncedCommentsSuspend().map { mapEntityToDomain(it) }
    }

    override suspend fun syncComment(comment: Comment): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Assuming remoteDataSource.addComment can handle if comment already exists
            // or a specific remoteDataSource.updateComment exists.
            // Using addComment for new unsynced items.
            val remoteResult = remoteDataSource.addComment(comment) // Or updateComment(comment)

            if (remoteResult is Result.Success && remoteResult.data.isNotBlank()) {
                // Update local entity to set needsSync = false and use remote ID
                val entity = mapDomainToEntity(comment.copy(commentId = remoteResult.data), needsSync = false)
                localDataSource.insertComment(entity) // REPLACE will update it
                Result.Success(Unit)
            } else if (remoteResult is Result.Error) {
                Result.Error(remoteResult.exception)
            } else {
                Result.Error(Exception("Remote data source returned invalid ID or unknown error during comment sync"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // --- Mappers ---
    private fun mapEntityToDomain(entity: CommentEntity): Comment {
        return Comment(
            commentId = entity.commentId,
            postId = entity.postId,
            authorUserId = entity.authorUserId ?: "unknown_author", // Handle nullable author if user deleted
            authorDisplayName = entity.authorDisplayName,
            authorProfilePictureUrl = entity.authorProfilePictureUrl,
            contentText = entity.contentText,
            createdTimestamp = entity.createdTimestamp,
            updatedTimestamp = entity.updatedTimestamp,
            likeCount = entity.likeCount,
            parentCommentId = entity.parentCommentId,
            replyCount = entity.replyCount,
            isEdited = entity.isEdited,
            mentionsUserIds = entity.mentionsUserIds
        )
    }

    private fun mapDomainToEntity(domain: Comment, needsSync: Boolean): CommentEntity {
        return CommentEntity(
            commentId = domain.commentId.ifBlank { UUID.randomUUID().toString() },
            postId = domain.postId,
            authorUserId = domain.authorUserId,
            authorDisplayName = domain.authorDisplayName,
            authorProfilePictureUrl = domain.authorProfilePictureUrl,
            contentText = domain.contentText,
            createdTimestamp = domain.createdTimestamp,
            updatedTimestamp = domain.updatedTimestamp,
            likeCount = domain.likeCount,
            parentCommentId = domain.parentCommentId,
            replyCount = domain.replyCount,
            isEdited = domain.isEdited,
            mentionsUserIds = domain.mentionsUserIds,
            needsSync = needsSync
        )
    }
}

// Re-define or import generic helper for network-bound resource pattern
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
