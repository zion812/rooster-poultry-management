package com.example.rooster.feature.community.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.domain.model.Comment
import com.example.rooster.feature.community.domain.model.CommunityUserProfile
import com.example.rooster.feature.community.domain.model.Post
import com.example.rooster.feature.community.domain.repository.FeedType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf // For placeholder returns
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseCommunityDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommunityRemoteDataSource {

    private val profilesCollection = firestore.collection("community_user_profiles")
    private val postsCollection = firestore.collection("community_posts")
    private val commentsCollection = firestore.collection("community_comments")

    // --- User Profiles ---
    override fun getCommunityUserProfileStream(userId: String): Flow<Result<CommunityUserProfile?>> = callbackFlow {
        val listener = profilesCollection.document(userId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                trySend(Result.Error(e)); channel.close(); return@addSnapshotListener
            }
            trySend(Result.Success(snapshot?.toObject<CommunityUserProfile>())).isSuccess
        }
        awaitClose { listener.remove() }
    }

    override suspend fun createCommunityUserProfile(profile: CommunityUserProfile): Result<Unit> {
        return try {
            profilesCollection.document(profile.userId).set(profile).await()
            Result.Success(Unit)
        } catch (e: Exception) { Result.Error(e) }
    }

    override suspend fun updateCommunityUserProfile(profile: CommunityUserProfile): Result<Unit> {
        return try {
            profilesCollection.document(profile.userId).set(profile).await() // Use set for full object update
            Result.Success(Unit)
        } catch (e: Exception) { Result.Error(e) }
    }

    // --- Posts ---
    override fun getPostsStream(feedType: FeedType, userId: String?, tag: String?): Flow<Result<List<Post>>> = callbackFlow {
        var query: Query = postsCollection.orderBy("createdTimestamp", Query.Direction.DESCENDING)

        // Basic feed type handling - more complex logic for 'FOLLOWING' would require knowing user's follow list
        when (feedType) {
            FeedType.USER_SPECIFIC -> if (userId != null) query = query.whereEqualTo("authorUserId", userId) else { trySend(Result.Error(IllegalArgumentException("UserId needed for USER_SPECIFIC feed"))); channel.close(); return@callbackFlow }
            FeedType.TAG_SPECIFIC -> if (tag != null) query = query.whereArrayContains("tags", tag) else { trySend(Result.Error(IllegalArgumentException("Tag needed for TAG_SPECIFIC feed"))); channel.close(); return@callbackFlow }
            FeedType.FOLLOWING -> { /* TODO: Implement following feed logic, requires fetching followed user IDs first */ trySend(Result.Success(emptyList())); channel.close(); return@callbackFlow }
            FeedType.GLOBAL_RECENT -> { /* No additional filters needed for basic global recent */ }
        }
        // TODO: Add pagination support (e.g., .limit(), .startAfter())

        val listener = query.addSnapshotListener { snapshots, e ->
            if (e != null) { trySend(Result.Error(e)); channel.close(); return@addSnapshotListener }
            trySend(Result.Success(snapshots?.toObjects<Post>() ?: emptyList())).isSuccess
        }
        awaitClose { listener.remove() }
    }

    override fun getPostDetailsStream(postId: String): Flow<Result<Post?>> = callbackFlow {
        val listener = postsCollection.document(postId).addSnapshotListener { snapshot, e ->
            if (e != null) { trySend(Result.Error(e)); channel.close(); return@addSnapshotListener }
            trySend(Result.Success(snapshot?.toObject<Post>())).isSuccess
        }
        awaitClose { listener.remove() }
    }

    override suspend fun createPost(post: Post): Result<String> {
        return try {
            // Firestore can auto-generate ID if document path is not specified
            val docRef = if (post.postId.isBlank()) postsCollection.document() else postsCollection.document(post.postId)
            val finalPost = if (post.postId.isBlank()) post.copy(postId = docRef.id) else post
            docRef.set(finalPost).await()
            Result.Success(docRef.id)
        } catch (e: Exception) { Result.Error(e) }
    }

    override suspend fun updatePost(post: Post): Result<Unit> {
        return try { postsCollection.document(post.postId).set(post).await(); Result.Success(Unit) }
        catch (e: Exception) { Result.Error(e) }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        // Firebase security rules should enforce that only author can delete.
        return try { postsCollection.document(postId).delete().await(); Result.Success(Unit) }
        catch (e: Exception) { Result.Error(e) }
    }

    override suspend fun likePost(postId: String, userId: String): Result<Unit> {
        val postRef = postsCollection.document(postId)
        return try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val post = snapshot.toObject(Post::class.java)
                    ?: throw FirebaseFirestoreException("Post not found", FirebaseFirestoreException.Code.NOT_FOUND)

                if (post.likedBy.contains(userId)) {
                    // User has already liked this post, do nothing or return specific status
                    return@runTransaction // Or throw an exception if this should be an error
                }

                val newLikedBy = post.likedBy.toMutableList().apply { add(userId) }
                val newLikeCount = post.likeCount + 1

                transaction.update(postRef, "likeCount", newLikeCount)
                transaction.update(postRef, "likedBy", newLikedBy)
                // No return value needed for transaction's lambda if it completes
            }.await() // await for the transaction to complete
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun unlikePost(postId: String, userId: String): Result<Unit> {
        val postRef = postsCollection.document(postId)
        return try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val post = snapshot.toObject(Post::class.java)
                    ?: throw FirebaseFirestoreException("Post not found", FirebaseFirestoreException.Code.NOT_FOUND)

                if (!post.likedBy.contains(userId)) {
                    // User hasn't liked this post, or already unliked
                    return@runTransaction // Or throw an exception
                }

                val newLikedBy = post.likedBy.toMutableList().apply { remove(userId) }
                val newLikeCount = (post.likeCount - 1).coerceAtLeast(0)

                transaction.update(postRef, "likeCount", newLikeCount)
                transaction.update(postRef, "likedBy", newLikedBy)
            }.await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // --- Comments ---
    override fun getCommentsStream(postId: String): Flow<Result<List<Comment>>> = callbackFlow {
        val query = commentsCollection.whereEqualTo("postId", postId)
            .whereEqualTo("parentCommentId", null) // Top-level comments
            .orderBy("createdTimestamp", Query.Direction.ASCENDING)
        // TODO: Add pagination

        val listener = query.addSnapshotListener { snapshots, e ->
            if (e != null) { trySend(Result.Error(e)); channel.close(); return@addSnapshotListener }
            trySend(Result.Success(snapshots?.toObjects<Comment>() ?: emptyList())).isSuccess
        }
        awaitClose { listener.remove() }
    }

    override fun getCommentRepliesStream(commentId: String): Flow<Result<List<Comment>>> = callbackFlow {
         val query = commentsCollection.whereEqualTo("parentCommentId", commentId)
            .orderBy("createdTimestamp", Query.Direction.ASCENDING)
        // TODO: Add pagination

        val listener = query.addSnapshotListener { snapshots, e ->
            if (e != null) { trySend(Result.Error(e)); channel.close(); return@addSnapshotListener }
            trySend(Result.Success(snapshots?.toObjects<Comment>() ?: emptyList())).isSuccess
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addComment(comment: Comment): Result<String> {
         return try {
            val docRef = if (comment.commentId.isBlank()) commentsCollection.document() else commentsCollection.document(comment.commentId)
            val finalComment = if (comment.commentId.isBlank()) comment.copy(commentId = docRef.id) else comment
            docRef.set(finalComment).await()
            // TODO: Increment commentCount on the Post (transaction or cloud function recommended)
            Result.Success(docRef.id)
        } catch (e: Exception) { Result.Error(e) }
    }

    override suspend fun updateComment(comment: Comment): Result<Unit> {
        return try { commentsCollection.document(comment.commentId).set(comment).await(); Result.Success(Unit) }
        catch (e: Exception) { Result.Error(e) }
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> {
        // Firebase security rules should enforce that only author can delete.
        // TODO: Decrement commentCount on the Post (transaction or cloud function recommended)
        return try { commentsCollection.document(commentId).delete().await(); Result.Success(Unit) }
        catch (e: Exception) { Result.Error(e) }
    }

    override suspend fun likeComment(commentId: String, userId: String): Result<Unit> {
        // TODO: Implement actual like logic
        return Result.Success(Unit) // Placeholder
    }

    override suspend fun unlikeComment(commentId: String, userId: String): Result<Unit> {
        // TODO: Implement actual unlike logic
        return Result.Success(Unit) // Placeholder
    }
}
