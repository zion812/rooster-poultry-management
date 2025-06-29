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

        // Sync User Profiles
        try {
            val unsyncedProfiles = userProfileRepository.getUnsyncedUserProfiles()
            if (unsyncedProfiles.isNotEmpty()) {
                Timber.d("Found ${unsyncedProfiles.size} unsynced user profiles.")
                for (profile in unsyncedProfiles) {
                    if (profile.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("UserProfile ${profile.userId} reached max sync attempts. Marking as SYNC_FAILED.")
                        userProfileRepository.updateSyncStatus(profile.userId, SYNC_FAILED_STATUS)
                        overallSuccess = false
                        continue
                    }
                    var attempt = 0
                    var success = false
                    var lastError: Exception? = null
                    while (attempt < MAX_SYNC_ATTEMPTS && !success) {
                        try {
                            val syncResult = userProfileRepository.syncUserProfile(profile)
                            if (syncResult is CoreResult.Success) {
                                userProfileRepository.markAsSynced(profile.userId)
                                Timber.d("Successfully synced user profile: ${profile.userId}")
                                success = true
                            } else if (syncResult is CoreResult.Error) {
                                throw syncResult.exception
                            }
                        } catch (e: Exception) {
                            lastError = e
                            attempt++
                            val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                            Timber.w(e, "Sync attempt $attempt failed for user profile ${profile.userId}, backing off $backoff ms")
                            delay(backoff)
                        }
                    }
                    if (!success) {
                        Timber.e(lastError, "All sync attempts failed for user profile ${profile.userId}; marking as SYNC_FAILED.")
                        userProfileRepository.updateSyncStatus(profile.userId, SYNC_FAILED_STATUS)
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
            val unsyncedPosts = postRepository.getUnsyncedPosts()
            if (unsyncedPosts.isNotEmpty()) {
                Timber.d("Found ${unsyncedPosts.size} unsynced posts.")
                for (post in unsyncedPosts) {
                    if (post.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("Post ${post.id} reached max sync attempts. Marking as SYNC_FAILED.")
                        postRepository.updateSyncStatus(post.id, SYNC_FAILED_STATUS)
                        overallSuccess = false
                        continue
                    }
                    var attempt = 0
                    var success = false
                    var lastError: Exception? = null
                    while (attempt < MAX_SYNC_ATTEMPTS && !success) {
                        try {
                            val syncResult = postRepository.syncPost(post)
                            if (syncResult is CoreResult.Success) {
                                postRepository.markAsSynced(post.id)
                                Timber.d("Successfully synced post: ${post.id}")
                                success = true
                            } else if (syncResult is CoreResult.Error) {
                                throw syncResult.exception
                            }
                        } catch (e: Exception) {
                            lastError = e
                            attempt++
                            val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                            Timber.w(e, "Sync attempt $attempt failed for post ${post.id}, backing off $backoff ms")
                            delay(backoff)
                        }
                    }
                    if (!success) {
                        Timber.e(lastError, "All sync attempts failed for post ${post.id}; marking as SYNC_FAILED.")
                        postRepository.updateSyncStatus(post.id, SYNC_FAILED_STATUS)
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
            val unsyncedComments = commentRepository.getUnsyncedComments()
            if (unsyncedComments.isNotEmpty()) {
                Timber.d("Found ${unsyncedComments.size} unsynced comments.")
                for (comment in unsyncedComments) {
                    if (comment.syncAttempts >= MAX_SYNC_ATTEMPTS) {
                        Timber.w("Comment ${comment.commentId} reached max sync attempts. Marking as SYNC_FAILED.")
                        commentRepository.updateSyncStatus(comment.commentId, SYNC_FAILED_STATUS)
                        overallSuccess = false
                        continue
                    }
                    var attempt = 0
                    var success = false
                    var lastError: Exception? = null
                    while (attempt < MAX_SYNC_ATTEMPTS && !success) {
                        try {
                            val syncResult = commentRepository.syncComment(comment)
                            if (syncResult is CoreResult.Success) {
                                commentRepository.markAsSynced(comment.commentId)
                                Timber.d("Successfully synced comment: ${comment.commentId}")
                                success = true
                            } else if (syncResult is CoreResult.Error) {
                                throw syncResult.exception
                            }
                        } catch (e: Exception) {
                            lastError = e
                            attempt++
                            val backoff = Math.pow(2.0, attempt.toDouble()).toLong() * 500L
                            Timber.w(e, "Sync attempt $attempt failed for comment ${comment.commentId}, backing off $backoff ms")
                            delay(backoff)
                        }
                    }
                    if (!success) {
                        Timber.e(lastError, "All sync attempts failed for comment ${comment.commentId}; marking as SYNC_FAILED.")
                        commentRepository.updateSyncStatus(comment.commentId, SYNC_FAILED_STATUS)
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
}
