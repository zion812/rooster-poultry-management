package com.example.rooster.feature.community.data.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.data.local.dao.CommentDao
import com.example.rooster.feature.community.data.local.model.CommentEntity
import com.example.rooster.feature.community.data.remote.CommunityRemoteDataSource
import com.example.rooster.feature.community.domain.model.Comment
import com.example.rooster.feature.community.domain.repository.CommentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val commentDao: CommentDao,
    private val remoteDataSource: CommunityRemoteDataSource
) : CommentRepository {

    override fun getCommentsForPost(postId: String, forceRefresh: Boolean): Flow<Result<List<Comment>>> {
        return localBackedCommunityResourceList(
            localCall = {
                commentDao.getCommentsForPost(postId)
                    .map { list -> list.map { mapEntityToDomain(it) } }
            },
            shouldFetch = { forceRefresh || it.isNullOrEmpty() },
            remoteCall = {
                remoteDataSource.getCommentsStream(postId).firstOrNull()
                    ?: Result.Success(emptyList())
            },
            saveRemoteResult = { comments ->
                val entities = comments.map { mapDomainToEntity(it, needsSync = false) }
                commentDao.insertComments(entities)
            }
        )
    }

    override fun getRepliesForComment(commentId: String, forceRefresh: Boolean): Flow<Result<List<Comment>>> {
        return localBackedCommunityResourceList(
            localCall = {
                commentDao.getRepliesForComment(commentId)
                    .map { list -> list.map { mapEntityToDomain(it) } }
            },
            shouldFetch = { forceRefresh || it.isNullOrEmpty() },
            remoteCall = {
                remoteDataSource.getCommentRepliesStream(commentId).firstOrNull() ?: Result.Success(
                    emptyList()
                )
            },
            saveRemoteResult = { comments ->
                val entities = comments.map { mapDomainToEntity(it, needsSync = false) }
                commentDao.insertComments(entities)
            }
        )
    }

    override suspend fun createComment(comment: Comment): Result<String> =
        withContext(Dispatchers.IO) {
        try {
            // Save locally first with sync flag
            val commentWithId = if (comment.commentId.isBlank()) {
                comment.copy(commentId = UUID.randomUUID().toString())
            } else {
                comment
            }

            val entity = mapDomainToEntity(commentWithId, needsSync = true)
            commentDao.insertComment(entity)

            // Try to sync with remote
            val remoteResult = remoteDataSource.addComment(commentWithId)
            when (remoteResult) {
                is Result.Success -> {
                    // Update local entity with remote ID and mark as synced
                    val syncedEntity = entity.copy(
                        commentId = remoteResult.data,
                        needsSync = false
                    )
                    commentDao.insertComment(syncedEntity)
                    Result.Success(remoteResult.data)
                }

                is Result.Error -> {
                    Timber.w(
                        remoteResult.exception,
                        "Failed to sync comment to remote, will retry later"
                    )
                    Result.Success(commentWithId.commentId) // Return local ID, will sync later
                }

                Result.Loading -> Result.Success(commentWithId.commentId)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to create comment")
            Result.Error(e)
        }
    }

    override suspend fun updateComment(comment: Comment): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = mapDomainToEntity(comment, needsSync = true)
            commentDao.insertComment(entity)

            val remoteResult = remoteDataSource.updateComment(comment)
            when (remoteResult) {
                is Result.Success -> {
                    commentDao.insertComment(entity.copy(needsSync = false))
                    Result.Success(Unit)
                }

                is Result.Error -> {
                    Timber.w(remoteResult.exception, "Failed to sync comment update to remote")
                    Result.Success(Unit) // Local update succeeded, will sync later
                }

                Result.Loading -> Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteComment(commentId: String, authorUserId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Verify ownership
            val localComment = commentDao.getCommentByIdSuspend(commentId)
            if (localComment?.authorUserId != authorUserId) {
                return@withContext Result.Error(Exception("User can only delete their own comments"))
            }

            val remoteResult = remoteDataSource.deleteComment(commentId)
            when (remoteResult) {
                is Result.Success -> {
                    commentDao.deleteCommentById(commentId)
                    Result.Success(Unit)
                }
                is Result.Error -> {
                    Timber.w(remoteResult.exception, "Failed to delete comment from remote")
                    // Still delete locally for now
                    commentDao.deleteCommentById(commentId)
                    Result.Success(Unit)
                }

                Result.Loading -> Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun likeComment(commentId: String, userId: String): Result<Unit> {
        return try {
            val result = remoteDataSource.likeComment(commentId, userId)
            result
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun unlikeComment(commentId: String, userId: String): Result<Unit> {
        return try {
            val result = remoteDataSource.unlikeComment(commentId, userId)
            result
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUnsyncedCommentEntities(): List<CommentEntity> {
        return commentDao.getUnsyncedCommentsSuspend()
    }

    override suspend fun syncCommentRemote(comment: Comment): Result<Unit> {
        return try {
            val result = remoteDataSource.addComment(comment)
            when (result) {
                is Result.Success -> {
                    // Update local entity to mark as synced
                    val entity =
                        mapDomainToEntity(comment.copy(commentId = result.data), needsSync = false)
                    commentDao.insertComment(entity)
                    Result.Success(Unit)
                }
                is Result.Error -> Result.Error(result.exception)
                Result.Loading -> Result.Success(Unit) // Treat loading as success for now
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateLocalCommentEntity(commentEntity: CommentEntity) {
        commentDao.insertComment(commentEntity)
    }

    override fun mapCommentEntityToDomain(commentEntity: CommentEntity): Comment {
        return Comment(
            commentId = commentEntity.commentId,
            postId = commentEntity.postId,
            parentCommentId = commentEntity.parentCommentId,
            authorUserId = commentEntity.authorUserId ?: "unknown",
            authorDisplayName = commentEntity.authorDisplayName,
            authorProfilePictureUrl = commentEntity.authorProfilePictureUrl,
            contentText = commentEntity.contentText,
            createdTimestamp = commentEntity.createdTimestamp,
            updatedTimestamp = commentEntity.updatedTimestamp,
            likeCount = commentEntity.likeCount,
            replyCount = commentEntity.replyCount,
            isEdited = commentEntity.isEdited,
            mentionsUserIds = commentEntity.mentionsUserIds
        )
    }

    private fun mapEntityToDomain(entity: CommentEntity): Comment {
        return Comment(
            commentId = entity.commentId,
            postId = entity.postId,
            parentCommentId = entity.parentCommentId,
            authorUserId = entity.authorUserId ?: "unknown",
            authorDisplayName = entity.authorDisplayName,
            authorProfilePictureUrl = entity.authorProfilePictureUrl,
            contentText = entity.contentText,
            createdTimestamp = entity.createdTimestamp,
            updatedTimestamp = entity.updatedTimestamp,
            likeCount = entity.likeCount,
            replyCount = entity.replyCount,
            isEdited = entity.isEdited,
            mentionsUserIds = entity.mentionsUserIds
        )
    }

    private fun mapDomainToEntity(domain: Comment, needsSync: Boolean = true): CommentEntity {
        return CommentEntity(
            commentId = domain.commentId.ifBlank { UUID.randomUUID().toString() },
            postId = domain.postId,
            parentCommentId = domain.parentCommentId,
            authorUserId = domain.authorUserId,
            authorDisplayName = domain.authorDisplayName,
            authorProfilePictureUrl = domain.authorProfilePictureUrl,
            contentText = domain.contentText,
            createdTimestamp = domain.createdTimestamp,
            updatedTimestamp = domain.updatedTimestamp,
            likeCount = domain.likeCount,
            replyCount = domain.replyCount,
            isEdited = domain.isEdited,
            mentionsUserIds = domain.mentionsUserIds,
            needsSync = needsSync,
            syncAttempts = 0,
            lastSyncAttemptTimestamp = 0L
        )
    }

    private inline fun <D, S> localBackedCommunityResourceList(
        crossinline localCall: () -> Flow<List<D>>,
        crossinline shouldFetch: (List<D>?) -> Boolean = { true },
        crossinline remoteCall: suspend () -> Result<List<S>>,
        crossinline saveRemoteResult: suspend (List<S>) -> Unit
    ): Flow<Result<List<D>>> = flow {
        emit(Result.Loading)
        val localData = localCall().firstOrNull()

        if (shouldFetch(localData)) {
            when (val remoteResult = remoteCall()) {
                is Result.Success -> {
                    saveRemoteResult(remoteResult.data)
                    localCall().collect { emit(Result.Success(it)) }
                }
                is Result.Error -> emit(Result.Error(remoteResult.exception))
                Result.Loading -> {}
            }
        } else if (localData == null || localData.isEmpty()) {
            emit(Result.Success(emptyList()))
        } else {
            emit(Result.Success(localData))
        }
    }.flowOn(Dispatchers.IO)
}