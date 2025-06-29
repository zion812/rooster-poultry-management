package com.example.rooster.feature.community.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rooster.core.common.Result as CoreResult
import com.example.rooster.feature.community.domain.repository.CommentRepository
import com.example.rooster.feature.community.domain.repository.CommunityUserProfileRepository
import com.example.rooster.feature.community.domain.repository.PostRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class CommunitySyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val userProfileRepository: CommunityUserProfileRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "CommunitySyncWorker"
        private const val MAX_SYNC_ATTEMPTS = 5
    }

    override suspend fun doWork(): Result {
        Timber.d("CommunitySyncWorker started")
        var overallSuccess = true // True if all items synced or correctly skipped (max attempts)

        // Sync User Profiles
        try {
            val unsyncedProfileEntities = userProfileRepository.getUnsyncedUserProfileEntities()
            if (unsyncedProfileEntities.isNotEmpty()) {
                Timber.d("Found ${unsyncedProfileEntities.size} unsynced user profiles.")
                for (entity in unsyncedProfileEntities) {
                    if (entity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("User profile ${entity.userId} reached max sync attempts (${entity.syncAttempts}). Skipping.")
                        overallSuccess = false // Still needs sync eventually
                        continue
                    }
                    val entityToAttempt = entity.copy(
                        syncAttempts = entity.syncAttempts + 1,
                        lastSyncAttemptTimestamp = System.currentTimeMillis()
                    )
                    userProfileRepository.updateLocalUserProfileEntity(entityToAttempt)
                    val domainProfile = userProfileRepository.mapUserProfileEntityToDomain(entityToAttempt)
                    val syncResult = userProfileRepository.syncUserProfileRemote(domainProfile)

                    if (syncResult is CoreResult.Success) {
                        userProfileRepository.updateLocalUserProfileEntity(entityToAttempt.copy(needsSync = false, syncAttempts = 0))
                        Timber.d("Successfully synced user profile: ${entity.userId}")
                    } else {
                        Timber.e((syncResult as CoreResult.Error).exception, "Failed to sync user profile: ${entity.userId}, attempt: ${entityToAttempt.syncAttempts}")
                        overallSuccess = false
                    }
                }
            } else {
                Timber.d("No unsynced user profiles to sync.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error processing user profiles for sync")
            overallSuccess = false
        }

        // Sync Posts
        try {
            val unsyncedPostEntities = postRepository.getUnsyncedPostEntities()
            if (unsyncedPostEntities.isNotEmpty()) {
                Timber.d("Found ${unsyncedPostEntities.size} unsynced posts.")
                for (entity in unsyncedPostEntities) {
                    if (entity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("Post ${entity.postId} reached max sync attempts (${entity.syncAttempts}). Skipping.")
                        overallSuccess = false
                        continue
                    }
                    val entityToAttempt = entity.copy(
                        syncAttempts = entity.syncAttempts + 1,
                        lastSyncAttemptTimestamp = System.currentTimeMillis()
                    )
                    postRepository.updateLocalPostEntity(entityToAttempt)
                    val domainPost = postRepository.mapPostEntityToDomain(entityToAttempt)
                    val syncResult = postRepository.syncPostRemote(domainPost)

                    if (syncResult is CoreResult.Success) {
                        // The remote sync for post might return a new ID if it was a new post
                        // The current syncPostRemote just returns Result<Unit>, assuming ID is handled by client or embedded.
                        // For robustness, if remote create returns an ID, it should be used to update the local entity.
                        // Assuming remote uses the ID from domainPost if provided, or generates one if blank.
                        // And FirebaseCommunityDataSource.createPost now returns the ID.
                        // The repository's syncPostRemote should ideally return Result<String> (the ID)
                        // For now, we assume the ID in entityToAttempt is correct or becomes correct post-sync.
                        postRepository.updateLocalPostEntity(entityToAttempt.copy(needsSync = false, syncAttempts = 0))
                        Timber.d("Successfully synced post: ${entity.postId}")
                    } else {
                        Timber.e((syncResult as CoreResult.Error).exception, "Failed to sync post: ${entity.postId}, attempt: ${entityToAttempt.syncAttempts}")
                        overallSuccess = false
                    }
                }
            } else {
                Timber.d("No unsynced posts to sync.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error processing posts for sync")
            overallSuccess = false
        }

        // Sync Comments
        try {
            val unsyncedCommentEntities = commentRepository.getUnsyncedCommentEntities()
            if (unsyncedCommentEntities.isNotEmpty()) {
                Timber.d("Found ${unsyncedCommentEntities.size} unsynced comments.")
                for (entity in unsyncedCommentEntities) {
                    if (entity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("Comment ${entity.commentId} reached max sync attempts (${entity.syncAttempts}). Skipping.")
                        overallSuccess = false
                        continue
                    }
                    val entityToAttempt = entity.copy(
                        syncAttempts = entity.syncAttempts + 1,
                        lastSyncAttemptTimestamp = System.currentTimeMillis()
                    )
                    commentRepository.updateLocalCommentEntity(entityToAttempt)
                    val domainComment = commentRepository.mapCommentEntityToDomain(entityToAttempt)
                    val syncResult = commentRepository.syncCommentRemote(domainComment)

                    if (syncResult is CoreResult.Success) {
                        commentRepository.updateLocalCommentEntity(entityToAttempt.copy(needsSync = false, syncAttempts = 0))
                        Timber.d("Successfully synced comment: ${entity.commentId}")
                    } else {
                        Timber.e((syncResult as CoreResult.Error).exception, "Failed to sync comment: ${entity.commentId}, attempt: ${entityToAttempt.syncAttempts}")
                        overallSuccess = false
                    }
                }
            } else {
                Timber.d("No unsynced comments to sync.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error processing comments for sync")
            overallSuccess = false
        }

        return if (overallSuccess) {
            Timber.d("CommunitySyncWorker completed successfully")
            Result.success()
        } else {
            Timber.w("CommunitySyncWorker completed with errors or items still needing sync. Retrying.")
            Result.retry()
        }
    }
}
