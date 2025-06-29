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

import com.example.rooster.feature.community.data.local.dao.PostDao
import timber.log.Timber

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val localDataSource: CommentDao,
    private val postDao: PostDao,
    private val remoteDataSource: CommunityRemoteDataSource
) : CommentRepository {

    override fun getCommentsForPost(postId: String, forceRefresh: Boolean): Flow<Result<List<Comment>>> {
        return localBackedCommunityResourceList(
            localCall = { localDataSource.getCommentsForPost(postId).map { entities -> entities.map { mapEntityToDomain(it) } } },
            remoteCall = { remoteDataSource.getCommentsStream(postId).firstOrNull() ?: Result.Success(emptyList()) },
            saveRemoteResult = { remoteComments -> 
                val entitiesToSave = mutableListOf<CommentEntity>()
                var skippedCount = 0
                for (remoteComment in remoteComments) {
                    val localUnsyncedEntity = localDataSource.getCommentByIdSuspend(remoteComment.commentId)
                    if (localUnsyncedEntity?.needsSync == true) {
                        Timber.w("Community: Local comment ID ${remoteComment.commentId} has unsynced changes. Skipping remote overwrite.")
                        skippedCount++
                    } else {
                        entitiesToSave.add(mapDomainToEntity(remoteComment, needsSync = false))
                    }
                }
                if (entitiesToSave.isNotEmpty()) {
                    localDataSource.insertComments(entitiesToSave)
                    Timber.d("Community: Saved/Updated ${entitiesToSave.size} comments for post $postId in cache.")
                }
                if (skippedCount > 0) {
                    Timber.d("Community: Skipped $skippedCount unsynced local comments for post $postId during remote update.")
                }
            },
            shouldFetch = { localData -> forceRefresh || localData.isNullOrEmpty() }
        ).flowOn(Dispatchers.IO)
    }

    override fun getRepliesForComment(commentId: String, forceRefresh: Boolean): Flow<Result<List<Comment>>> {
        return localBackedCommunityResourceList(
            localCall = { localDataSource.getRepliesForComment(commentId).map { entities -> entities.map { mapEntityToDomain(it) } } },
            remoteCall = { remoteDataSource.getCommentRepliesStream(commentId).firstOrNull() ?: Result.Success(emptyList()) },
            saveRemoteResult = { remoteReplies -> 
                val entitiesToSave = mutableListOf<CommentEntity>()
                var skippedCount = 0
                for (remoteReply in remoteReplies) {
                    val localUnsyncedEntity = localDataSource.getCommentByIdSuspend(remoteReply.commentId)
                    if (localUnsyncedEntity?.needsSync == true) {
                        Timber.w("Community: Local reply ID ${remoteReply.commentId} has unsynced changes. Skipping remote overwrite.")
                        skippedCount++
                    } else {
                        entitiesToSave.add(mapDomainToEntity(remoteReply, needsSync = false))
                    }
                }
                if (entitiesToSave.isNotEmpty()) {
                    localDataSource.insertComments(entitiesToSave)
                    Timber.d("Community: Saved/Updated ${entitiesToSave.size} replies for comment $commentId in cache.")
                }
                if (skippedCount > 0) {
                    Timber.d("Community: Skipped $skippedCount unsynced local replies for comment $commentId during remote update.")
                }
            },
            shouldFetch = { localData -> forceRefresh || localData.isNullOrEmpty() }
        ).flowOn(Dispatchers.IO)
    }

    override suspend fun addComment(comment: Comment): Result<String> = withContext(Dispatchers.IO) {
        try {
            val commentWithId = if (comment.commentId.isBlank()) comment.copy(commentId = UUID.randomUUID().toString()) else comment
            val entity = mapDomainToEntity(commentWithId, needsSync = true)
            localDataSource.insertComment(entity)

            val postEntity = postDao.getPostByIdSuspend(comment.postId)
            if (postEntity != null) {
                postDao.updateCommentCount(comment.postId, postEntity.commentCount + 1)
                Timber.d("Incremented local comment count for post ${comment.postId}")
            }

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
            localDataSource.updateComment(entity)
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
        try {
            val commentEntity = localDataSource.getCommentByIdSuspend(commentId)

            val remoteResult = remoteDataSource.deleteComment(commentId)
            if (remoteResult is Result.Success) {
                localDataSource.deleteCommentById(commentId)
                if (commentEntity != null) {
                    val postEntity = postDao.getPostByIdSuspend(commentEntity.postId)
                    if (postEntity != null) {
                        postDao.updateCommentCount(commentEntity.postId, (postEntity.commentCount - 1).coerceAtLeast(0))
                        Timber.d("Decremented local comment count for post ${commentEntity.postId}")
                    }
                }
            }
            remoteResult
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun likeComment(commentId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val remoteResult = remoteDataSource.likeComment(commentId, userId)
        if (remoteResult is Result.Success) {
            // localDataSource.updateLikeCount(commentId, newCount)
        }
        return@withContext remoteResult
    }

    override suspend fun unlikeComment(commentId: String, userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext remoteDataSource.unlikeComment(commentId, userId)
    }

    override suspend fun getUnsyncedCommentEntities(): List<CommentEntity> = withContext(Dispatchers.IO) {
        localDataSource.getUnsyncedCommentsSuspend()
    }

    override suspend fun syncCommentRemote(comment: Comment): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteResult = remoteDataSource.addComment(comment)
            if (remoteResult is Result.Success && remoteResult.data.isNotBlank()) {
                Result.Success(Unit)
            } else if (remoteResult is Result.Error) {
                Timber.e(remoteResult.exception, "Failed to sync comment ${comment.commentId} to remote.")
                Result.Error(remoteResult.exception)
            } else {
                Timber.w("Remote sync for comment ${comment.commentId} did not return a specific error/ID but was not successful.")
                Result.Error(Exception("Unknown error or unsuccessful remote sync for comment ${comment.commentId}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during remote comment sync for ${comment.commentId}")
            Result.Error(e)
        }
    }

    override suspend fun updateLocalCommentEntity(commentEntity: CommentEntity) {
        withContext(Dispatchers.IO) {
            localDataSource.insertComment(commentEntity)
        }
    }

    override fun mapCommentEntityToDomain(commentEntity: CommentEntity): Comment {
        return mapEntityToDomain(commentEntity)
    }

    private fun mapEntityToDomain(entity: CommentEntity): Comment {
        return Comment(
            commentId = entity.commentId,
            postId = entity.postId,
            authorUserId = entity.authorUserId ?: "unknown_author",
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
