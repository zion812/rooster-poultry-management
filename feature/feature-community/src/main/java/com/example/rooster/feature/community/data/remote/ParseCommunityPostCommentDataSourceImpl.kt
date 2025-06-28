package com.example.rooster.feature.community.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.domain.model.Comment
import com.example.rooster.feature.community.domain.model.CommunityUserProfile // For author mapping
import com.example.rooster.feature.community.domain.model.Post
import com.example.rooster.feature.community.domain.repository.FeedType
import com.parse.*
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class ParseCommunityPostCommentDataSourceImpl @Inject constructor() : PostCommentParseDataSource {

    companion object {
        const val CLASS_COMMUNITY_POST = "CommunityPost"
        const val CLASS_COMMUNITY_COMMENT = "CommunityComment"

        // Common Fields
        const val F_AUTHOR = "author" // Pointer to _User
        const val F_AUTHOR_DISPLAY_NAME = "authorDisplayName" // Denormalized
        const val F_AUTHOR_PROFILE_PIC_URL = "authorProfilePictureUrl" // Denormalized
        const val F_CONTENT_TEXT = "contentText"
        const val F_CREATED_AT = "createdAt" // Built-in
        const val F_UPDATED_AT = "updatedAt" // Built-in
        const val F_LIKE_COUNT = "likeCount" // Number, ideally Cloud Code managed

        // Post Specific Fields
        const val P_IMAGE_URLS = "imageUrls" // Array of String
        const val P_VIDEO_URL = "videoUrl" // String
        const val P_COMMENT_COUNT = "commentCount" // Number, ideally Cloud Code managed
        const val P_SHARE_COUNT = "shareCount" // Number
        const val P_TAGS = "tags" // Array of String
        const val P_LOCATION = "location" // String
        const val P_MENTIONS_USER_IDS = "mentionsUserIds" // Array of String (User objectIds)
        const val P_IS_EDITED = "isEdited" // Boolean
        const val P_RELATED_FLOCK = "relatedFlock" // Pointer to Flock (Parse class)

        // Comment Specific Fields
        const val C_POST = "post" // Pointer to CommunityPost
        const val C_PARENT_COMMENT = "parentComment" // Pointer to CommunityComment (self)
        const val C_REPLY_COUNT = "replyCount" // Number, Cloud Code managed
    }

    override suspend fun getPosts(
        feedType: FeedType,
        userId: String?,
        tag: String?,
        pageSize: Int,
        page: Int
    ): Result<List<Post>> {
        return try {
            val query = ParseQuery.getQuery<ParseObject>(CLASS_COMMUNITY_POST)
            query.include(F_AUTHOR) // Include author details
            query.orderByDescending(F_CREATED_AT)
            query.limit = pageSize
            query.skip = (page - 1) * pageSize

            when (feedType) {
                FeedType.USER_SPECIFIC -> {
                    if (userId == null) return Result.Error(IllegalArgumentException("UserId required for USER_SPECIFIC feed"))
                    query.whereEqualTo(F_AUTHOR, ParseObject.createWithoutData("_User", userId))
                }
                FeedType.TAG_SPECIFIC -> {
                    if (tag == null) return Result.Error(IllegalArgumentException("Tag required for TAG_SPECIFIC feed"))
                    query.whereEqualTo(P_TAGS, tag) // or whereContainsAll for multiple tags
                }
                FeedType.FOLLOWING -> {
                    // TODO: Complex logic: Fetch current user's following list, then query posts where author is in that list.
                    // This usually requires multiple queries or Cloud Code for efficiency.
                    Timber.w("Parse: FOLLOWING feed type not fully implemented yet in getPosts.")
                    return Result.Success(emptyList()) // Placeholder
                }
                FeedType.GLOBAL_RECENT -> { /* No additional server-side filters beyond ordering/pagination */ }
            }

            val parseObjects = query.find()
            Result.Success(parseObjects.mapNotNull { mapParseObjectToPost(it) })
        } catch (e: ParseException) {
            Timber.e(e, "Parse: Error fetching posts (feedType: $feedType, userId: $userId, tag: $tag)")
            Result.Error(e)
        }
    }

    override suspend fun getPostDetails(postId: String): Result<Post?> {
        return try {
            val query = ParseQuery.getQuery<ParseObject>(CLASS_COMMUNITY_POST)
            query.include(F_AUTHOR)
            val parseObject = query.get(postId)
            Result.Success(mapParseObjectToPost(parseObject))
        } catch (e: ParseException) {
            if (e.code == ParseException.OBJECT_NOT_FOUND) Result.Success(null)
            else { Timber.e(e, "Parse: Error fetching post details $postId"); Result.Error(e) }
        }
    }

    override suspend fun createPost(post: Post): Result<String> = suspendCancellableCoroutine { cont ->
        val parsePost = mapPostToParseObject(post)
        parsePost.saveInBackground { e ->
            if (e == null) cont.resume(Result.Success(parsePost.objectId))
            else { Timber.e(e, "Parse: Error creating post"); cont.resume(Result.Error(e)) }
        }
    }

    override suspend fun updatePost(post: Post): Result<Unit> = suspendCancellableCoroutine { cont ->
        val parsePost = mapPostToParseObject(post) // This will set objectId if post.postId is valid
        parsePost.saveInBackground { e ->
            if (e == null) cont.resume(Result.Success(Unit))
            else { Timber.e(e, "Parse: Error updating post ${post.postId}"); cont.resume(Result.Error(e)) }
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> = suspendCancellableCoroutine { cont ->
        val postObj = ParseObject.createWithoutData(CLASS_COMMUNITY_POST, postId)
        postObj.deleteInBackground { e ->
            if (e == null) cont.resume(Result.Success(Unit))
            else { Timber.e(e, "Parse: Error deleting post $postId"); cont.resume(Result.Error(e)) }
        }
    }

    // --- Comments ---
    override suspend fun getCommentsForPost(postId: String, pageSize: Int, page: Int): Result<List<Comment>> {
         return try {
            val query = ParseQuery.getQuery<ParseObject>(CLASS_COMMUNITY_COMMENT)
            query.whereEqualTo(C_POST, ParseObject.createWithoutData(CLASS_COMMUNITY_POST, postId))
            query.whereDoesNotExist(C_PARENT_COMMENT) // Top-level comments
            query.include(F_AUTHOR)
            query.orderByAscending(F_CREATED_AT) // Or descending, depending on desired display
            query.limit = pageSize
            query.skip = (page - 1) * pageSize
            val parseObjects = query.find()
            Result.Success(parseObjects.mapNotNull { mapParseObjectToComment(it) })
        } catch (e: ParseException) {
            Timber.e(e, "Parse: Error fetching comments for post $postId")
            Result.Error(e)
        }
    }

    override suspend fun getRepliesForComment(commentId: String, pageSize: Int, page: Int): Result<List<Comment>> {
        return try {
            val query = ParseQuery.getQuery<ParseObject>(CLASS_COMMUNITY_COMMENT)
            query.whereEqualTo(C_PARENT_COMMENT, ParseObject.createWithoutData(CLASS_COMMUNITY_COMMENT, commentId))
            query.include(F_AUTHOR)
            query.orderByAscending(F_CREATED_AT)
            query.limit = pageSize
            query.skip = (page - 1) * pageSize
            val parseObjects = query.find()
            Result.Success(parseObjects.mapNotNull { mapParseObjectToComment(it) })
        } catch (e: ParseException) {
            Timber.e(e, "Parse: Error fetching replies for comment $commentId")
            Result.Error(e)
        }
    }

    override suspend fun addComment(comment: Comment): Result<String> = suspendCancellableCoroutine { cont ->
        val parseComment = mapCommentToParseObject(comment)
        parseComment.saveInBackground { e ->
            if (e == null) {
                // TODO: Trigger Cloud Code to increment commentCount on parent Post
                cont.resume(Result.Success(parseComment.objectId))
            } else {
                Timber.e(e, "Parse: Error adding comment"); cont.resume(Result.Error(e))
            }
        }
    }

    override suspend fun updateComment(comment: Comment): Result<Unit> = suspendCancellableCoroutine { cont ->
        val parseComment = mapCommentToParseObject(comment)
        parseComment.saveInBackground { e ->
            if (e == null) cont.resume(Result.Success(Unit))
            else { Timber.e(e, "Parse: Error updating comment ${comment.commentId}"); cont.resume(Result.Error(e)) }
        }
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> = suspendCancellableCoroutine { cont ->
        val commentObj = ParseObject.createWithoutData(CLASS_COMMUNITY_COMMENT, commentId)
        commentObj.deleteInBackground { e ->
            if (e == null) {
                // TODO: Trigger Cloud Code to decrement commentCount on parent Post
                cont.resume(Result.Success(Unit))
            } else {
                Timber.e(e, "Parse: Error deleting comment $commentId"); cont.resume(Result.Error(e))
            }
        }
    }

    // --- Like/Unlike (Placeholders - Ideally use Cloud Code for atomicity) ---
    override suspend fun likePost(postId: String, userId: String): Result<Unit> {
        Timber.d("Parse: likePost called for $postId by $userId. Use Cloud Code for atomic counter.")
        // Example: ParseCloud.callFunctionInBackground("likePost", mapOf("postId" to postId), callback)
        return Result.Success(Unit) // Placeholder
    }
    override suspend fun unlikePost(postId: String, userId: String): Result<Unit> {
         Timber.d("Parse: unlikePost called for $postId by $userId. Use Cloud Code for atomic counter.")
        return Result.Success(Unit) // Placeholder
    }
    override suspend fun likeComment(commentId: String, userId: String): Result<Unit> {
        Timber.d("Parse: likeComment called for $commentId by $userId. Use Cloud Code for atomic counter.")
        return Result.Success(Unit) // Placeholder
    }
    override suspend fun unlikeComment(commentId: String, userId: String): Result<Unit> {
        Timber.d("Parse: unlikeComment called for $commentId by $userId. Use Cloud Code for atomic counter.")
        return Result.Success(Unit) // Placeholder
    }

    // --- Mappers ---
    private fun mapParseObjectToPost(obj: ParseObject): Post? {
        return try {
            val authorObj = obj.getParseUser(F_AUTHOR)
            Post(
                postId = obj.objectId,
                authorUserId = authorObj?.objectId ?: obj.getString(F_AUTHOR) ?: "", // getString if storing ID
                authorDisplayName = authorObj?.getString(ParseUserDataSource.KEY_DISPLAY_NAME) ?: obj.getString(F_AUTHOR_DISPLAY_NAME) ?: "Unknown User",
                authorProfilePictureUrl = authorObj?.getString(ParseUserDataSource.KEY_PROFILE_PICTURE_URL) ?: obj.getString(F_AUTHOR_PROFILE_PIC_URL),
                contentText = obj.getString(F_CONTENT_TEXT),
                imageUrls = obj.getList<String>(P_IMAGE_URLS)?.toList(),
                videoUrl = obj.getString(P_VIDEO_URL),
                createdTimestamp = obj.createdAt?.time ?: 0L,
                updatedTimestamp = obj.updatedAt?.time,
                likeCount = obj.getInt(F_LIKE_COUNT),
                commentCount = obj.getInt(P_COMMENT_COUNT),
                shareCount = obj.getInt(P_SHARE_COUNT),
                tags = obj.getList<String>(P_TAGS)?.toList(),
                location = obj.getString(P_LOCATION),
                mentionsUserIds = obj.getList<String>(P_MENTIONS_USER_IDS)?.toList(),
                isEdited = obj.getBoolean(P_IS_EDITED),
                relatedFlockId = obj.getParseObject(P_RELATED_FLOCK)?.objectId
            )
        } catch (e: Exception) {
            Timber.e(e, "Error mapping ParseObject to Post: ${obj.objectId}")
            null
        }
    }

    private fun mapPostToParseObject(post: Post): ParseObject {
        val obj = if (post.postId.isNotBlank() && post.postId.length == 10 && post.postId.all { it.isLetterOrDigit() }) {
            ParseObject.createWithoutData(CLASS_COMMUNITY_POST, post.postId)
        } else {
            ParseObject(CLASS_COMMUNITY_POST)
        }
        obj.put(F_AUTHOR, ParseObject.createWithoutData("_User", post.authorUserId))
        obj.put(F_AUTHOR_DISPLAY_NAME, post.authorDisplayName) // Denormalized
        post.authorProfilePictureUrl?.let { obj.put(F_AUTHOR_PROFILE_PIC_URL, it) } // Denormalized
        post.contentText?.let { obj.put(F_CONTENT_TEXT, it) }
        post.imageUrls?.let { obj.put(P_IMAGE_URLS, it) }
        post.videoUrl?.let { obj.put(P_VIDEO_URL, it) }
        post.tags?.let { obj.put(P_TAGS, it) }
        post.location?.let { obj.put(P_LOCATION, it) }
        post.mentionsUserIds?.let { obj.put(P_MENTIONS_USER_IDS, it) }
        obj.put(P_IS_EDITED, post.isEdited)
        post.relatedFlockId?.let { obj.put(P_RELATED_FLOCK, ParseObject.createWithoutData(ParseFarmDataSource.CLASS_FLOCK, it)) }
        // Counts are typically managed by Cloud Code, not set directly by client on create/update
        // obj.put(F_LIKE_COUNT, post.likeCount)
        // obj.put(P_COMMENT_COUNT, post.commentCount)
        // obj.put(P_SHARE_COUNT, post.shareCount)
        // createdTimestamp and updatedTimestamp are handled by Parse
        return obj
    }

    private fun mapParseObjectToComment(obj: ParseObject): Comment? {
         return try {
            val authorObj = obj.getParseUser(F_AUTHOR)
            Comment(
                commentId = obj.objectId,
                postId = obj.getParseObject(C_POST)?.objectId ?: "",
                authorUserId = authorObj?.objectId ?: obj.getString(F_AUTHOR) ?: "",
                authorDisplayName = authorObj?.getString(ParseUserDataSource.KEY_DISPLAY_NAME) ?: obj.getString(F_AUTHOR_DISPLAY_NAME) ?: "Unknown User",
                authorProfilePictureUrl = authorObj?.getString(ParseUserDataSource.KEY_PROFILE_PICTURE_URL) ?: obj.getString(F_AUTHOR_PROFILE_PIC_URL),
                contentText = obj.getString(F_CONTENT_TEXT) ?: "",
                createdTimestamp = obj.createdAt?.time ?: 0L,
                updatedTimestamp = obj.updatedAt?.time,
                likeCount = obj.getInt(F_LIKE_COUNT),
                parentCommentId = obj.getParseObject(C_PARENT_COMMENT)?.objectId,
                replyCount = obj.getInt(C_REPLY_COUNT),
                isEdited = obj.getBoolean(P_IS_EDITED), // Assuming P_IS_EDITED is also used for comments
                mentionsUserIds = obj.getList<String>(P_MENTIONS_USER_IDS)?.toList()
            )
        } catch (e: Exception) {
            Timber.e(e, "Error mapping ParseObject to Comment: ${obj.objectId}")
            null
        }
    }

    private fun mapCommentToParseObject(comment: Comment): ParseObject {
        val obj = if (comment.commentId.isNotBlank() && comment.commentId.length == 10 && comment.commentId.all { it.isLetterOrDigit() }) {
            ParseObject.createWithoutData(CLASS_COMMUNITY_COMMENT, comment.commentId)
        } else {
            ParseObject(CLASS_COMMUNITY_COMMENT)
        }
        obj.put(C_POST, ParseObject.createWithoutData(CLASS_COMMUNITY_POST, comment.postId))
        obj.put(F_AUTHOR, ParseObject.createWithoutData("_User", comment.authorUserId))
        obj.put(F_AUTHOR_DISPLAY_NAME, comment.authorDisplayName)
        comment.authorProfilePictureUrl?.let { obj.put(F_AUTHOR_PROFILE_PIC_URL, it) }
        obj.put(F_CONTENT_TEXT, comment.contentText)
        comment.parentCommentId?.let { obj.put(C_PARENT_COMMENT, ParseObject.createWithoutData(CLASS_COMMUNITY_COMMENT, it)) }
        obj.put(P_IS_EDITED, comment.isEdited)
        comment.mentionsUserIds?.let { obj.put(P_MENTIONS_USER_IDS, it) }
        // likeCount, replyCount managed by Cloud Code
        return obj
    }
}
