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
    }

    override suspend fun doWork(): Result {
        Timber.d("CommunitySyncWorker started")
        var overallSuccess = true

        // Sync User Profiles
        try {
            val unsyncedProfiles = userProfileRepository.getUnsyncedUserProfiles() // Add to repo
            if (unsyncedProfiles.isNotEmpty()) {
                Timber.d("Found ${unsyncedProfiles.size} unsynced user profiles.")
                for (profile in unsyncedProfiles) {
                    val syncResult = userProfileRepository.syncUserProfile(profile) // Add to repo
                    if (syncResult is CoreResult.Error) {
                        Timber.e(syncResult.exception, "Failed to sync user profile: ${profile.userId}")
                        overallSuccess = false
                    } else {
                        Timber.d("Successfully synced user profile: ${profile.userId}")
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
            val unsyncedPosts = postRepository.getUnsyncedPosts() // Add to repo
            if (unsyncedPosts.isNotEmpty()) {
                Timber.d("Found ${unsyncedPosts.size} unsynced posts.")
                for (post in unsyncedPosts) {
                    val syncResult = postRepository.syncPost(post) // Add to repo
                    if (syncResult is CoreResult.Error) {
                        Timber.e(syncResult.exception, "Failed to sync post: ${post.postId}")
                        overallSuccess = false
                    } else {
                        Timber.d("Successfully synced post: ${post.postId}")
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
            val unsyncedComments = commentRepository.getUnsyncedComments() // Add to repo
            if (unsyncedComments.isNotEmpty()) {
                Timber.d("Found ${unsyncedComments.size} unsynced comments.")
                for (comment in unsyncedComments) {
                    val syncResult = commentRepository.syncComment(comment) // Add to repo
                    if (syncResult is CoreResult.Error) {
                        Timber.e(syncResult.exception, "Failed to sync comment: ${comment.commentId}")
                        overallSuccess = false
                    } else {
                        Timber.d("Successfully synced comment: ${comment.commentId}")
                    }
                }
            } else {
                Timber.d("No unsynced comments to sync.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error syncing comments")
            overallSuccess = false
        }

        return if (overallSuccess) {
            Timber.d("CommunitySyncWorker completed successfully")
            Result.success()
        } else {
            Timber.w("CommunitySyncWorker completed with errors, retrying.")
            Result.retry()
        }
    }
}
