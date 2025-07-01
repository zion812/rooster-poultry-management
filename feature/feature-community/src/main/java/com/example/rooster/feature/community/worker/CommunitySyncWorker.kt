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
import dagger.hilt.android.worker.HiltWorker
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import kotlinx.coroutines.delay

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
        private const val SYNC_FAILED_STATUS = "SYNC_FAILED"
    }

    override suspend fun doWork(): Result {
        Timber.d("CommunitySyncWorker started")
        var overallSuccess = true
        val syncMutex = Mutex() // Prevent concurrent syncs

        // Sync User Profiles
        try {
            val unsyncedProfiles = userProfileRepository.getUnsyncedUserProfileEntities()
            if (unsyncedProfiles.isNotEmpty()) {
                Timber.d("Found ${unsyncedProfiles.size} unsynced user profiles.")
                for (profileEntity in unsyncedProfiles) {
                    if (profileEntity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("UserProfile ${profileEntity.userId} reached max sync attempts. Marking as SYNC_FAILED.")
                        userProfileRepository.updateSyncStatus(profileEntity.userId, SYNC_FAILED_STATUS)
                        overallSuccess = false
                        continue
                    }
                    var attempt = 0
                    var success = false
                    var lastError: Exception? = null
                    while (attempt < MAX_SYNC_ATTEMPTS && !success) {
                        try {
                            syncMutex.withLock {
                                val domainProfile = userProfileRepository.mapUserProfileEntityToDomain(profileEntity)
                                if (!validateUserProfile(domainProfile)) throw IllegalArgumentException("Invalid user profile data")
                                val syncResult = userProfileRepository.syncUserProfileRemote(domainProfile)
                                if (syncResult is CoreResult.Success) {
                                    userProfileRepository.updateSyncStatus(profileEntity.userId, "SYNCED")
                                    Timber.d("Successfully synced user profile: ${profileEntity.userId}")
                                    success = true
                                } else if (syncResult is CoreResult.Error) {
                                    lastError = syncResult.exception
                                    if (isTransientError(syncResult.exception)) {
                                        attempt++
                                        val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                                        Timber.w(syncResult.exception, "Sync attempt $attempt failed for user profile ${profileEntity.userId}, backing off $backoff ms (transient error)")
                                        delay(backoff)
                                    } else {
                                        Timber.e(syncResult.exception, "Permanent sync failure for user profile ${profileEntity.userId}, not retrying.")
                                        break
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            lastError = e
                            attempt++
                            val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                            Timber.w(e, "Sync attempt $attempt failed for user profile ${profileEntity.userId}, backing off $backoff ms")
                            delay(backoff)
                        }
                    }
                    if (!success) {
                        Timber.e(lastError, "All sync attempts failed for user profile ${profileEntity.userId}; marking as SYNC_FAILED.")
                        userProfileRepository.updateSyncStatus(profileEntity.userId, SYNC_FAILED_STATUS)
                        overallSuccess = false
                    }
                }
            } else {
                Timber.d("No unsynced user profiles to sync.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error syncing user profiles")
            overallSuccess = false
        }

        // Sync Posts
        try {
            val unsyncedPosts = postRepository.getUnsyncedPostEntities()
            if (unsyncedPosts.isNotEmpty()) {
                Timber.d("Found ${unsyncedPosts.size} unsynced posts.")
                for (postEntity in unsyncedPosts) {
                    if (postEntity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("Post ${postEntity.id} reached max sync attempts. Marking as SYNC_FAILED.")
                        postRepository.updateSyncStatus(postEntity.id, SYNC_FAILED_STATUS)
                        overallSuccess = false
                        continue
                    }
                    var attempt = 0
                    var success = false
                    var lastError: Exception? = null
                    while (attempt < MAX_SYNC_ATTEMPTS && !success) {
                        try {
                            syncMutex.withLock {
                                val domainPost = postRepository.mapPostEntityToDomain(postEntity)
                                if (!validatePost(domainPost)) throw IllegalArgumentException("Invalid post data")
                                val syncResult = postRepository.syncPostRemote(domainPost)
                                if (syncResult is CoreResult.Success) {
                                    postRepository.updateSyncStatus(postEntity.id, "SYNCED")
                                    Timber.d("Successfully synced post: ${postEntity.id}")
                                    success = true
                                } else if (syncResult is CoreResult.Error) {
                                    lastError = syncResult.exception
                                    if (isTransientError(syncResult.exception)) {
                                        attempt++
                                        val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                                        Timber.w(syncResult.exception, "Sync attempt $attempt failed for post ${postEntity.id}, backing off $backoff ms (transient error)")
                                        delay(backoff)
                                    } else {
                                        Timber.e(syncResult.exception, "Permanent sync failure for post ${postEntity.id}, not retrying.")
                                        break
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            lastError = e
                            attempt++
                            val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                            Timber.w(e, "Sync attempt $attempt failed for post ${postEntity.id}, backing off $backoff ms")
                            delay(backoff)
                        }
                    }
                    if (!success) {
                        Timber.e(lastError, "All sync attempts failed for post ${postEntity.id}; marking as SYNC_FAILED.")
                        postRepository.updateSyncStatus(postEntity.id, SYNC_FAILED_STATUS)
                        overallSuccess = false
                    }
                }
            } else {
                Timber.d("No unsynced posts to sync.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error syncing posts")
            overallSuccess = false
        }

        // Sync Comments
        try {
            val unsyncedComments = commentRepository.getUnsyncedCommentEntities()
            if (unsyncedComments.isNotEmpty()) {
                Timber.d("Found ${unsyncedComments.size} unsynced comments.")
                for (commentEntity in unsyncedComments) {
                    if (commentEntity.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("Comment ${commentEntity.commentId} reached max sync attempts. Marking as SYNC_FAILED.")
                        commentRepository.updateSyncStatus(commentEntity.commentId, SYNC_FAILED_STATUS)
                        overallSuccess = false
                        continue
                    }
                    var attempt = 0
                    var success = false
                    var lastError: Exception? = null
                    while (attempt < MAX_SYNC_ATTEMPTS && !success) {
                        try {
                            syncMutex.withLock {
                                val domainComment = commentRepository.mapCommentEntityToDomain(commentEntity)
                                if (!validateComment(domainComment)) throw IllegalArgumentException("Invalid comment data")
                                val syncResult = commentRepository.syncCommentRemote(domainComment)
                                if (syncResult is CoreResult.Success) {
                                    commentRepository.updateSyncStatus(commentEntity.commentId, "SYNCED")
                                    Timber.d("Successfully synced comment: ${commentEntity.commentId}")
                                    success = true
                                } else if (syncResult is CoreResult.Error) {
                                    lastError = syncResult.exception
                                    if (isTransientError(syncResult.exception)) {
                                        attempt++
                                        val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                                        Timber.w(syncResult.exception, "Sync attempt $attempt failed for comment ${commentEntity.commentId}, backing off $backoff ms (transient error)")
                                        delay(backoff)
                                    } else {
                                        Timber.e(syncResult.exception, "Permanent sync failure for comment ${commentEntity.commentId}, not retrying.")
                                        break
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            lastError = e
                            attempt++
                            val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                            Timber.w(e, "Sync attempt $attempt failed for comment ${commentEntity.commentId}, backing off $backoff ms")
                            delay(backoff)
                        }
                    }
                    if (!success) {
                        Timber.e(lastError, "All sync attempts failed for comment ${commentEntity.commentId}; marking as SYNC_FAILED.")
                        commentRepository.updateSyncStatus(commentEntity.commentId, SYNC_FAILED_STATUS)
                        overallSuccess = false
                    }
                }
            } else {
                Timber.d("No unsynced comments to sync.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error syncing comments")
            overallSuccess = false
        }

        if (overallSuccess) {
            Timber.d("CommunitySyncWorker completed successfully")
            return Result.success()
        } else {
            Timber.w("CommunitySyncWorker completed with errors or items still needing sync. Retrying.")
            return Result.retry()
        }
    }

    /**
     * Helper to classify transient errors (network, timeouts, Firebase exceptions).
     */
    private fun isTransientError(e: Throwable?): Boolean {
        return e is java.io.IOException ||
               (e?.message?.contains("timeout", ignoreCase = true) == true) ||
               (e?.javaClass?.simpleName?.contains("Firebase", ignoreCase = true) == true)
    }

    // Example validators (customize as needed)
    private fun validateUserProfile(profile: com.example.rooster.feature.community.domain.model.CommunityUserProfile): Boolean {
        return profile.userId.isNotBlank() && profile.displayName.isNotBlank()
    }
    private fun validatePost(post: com.example.rooster.feature.community.domain.model.Post): Boolean {
        return post.id.isNotBlank() && post.authorUserId.isNotBlank() && post.content.isNotBlank()
    }
    private fun validateComment(comment: com.example.rooster.feature.community.domain.model.Comment): Boolean {
        return comment.commentId.isNotBlank() && comment.authorUserId.isNotBlank() && comment.content.isNotBlank()
    }
}
