package com.example.rooster.data.storage

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.rooster.core.common.Result
import com.example.rooster.core.common.storage.ImageUploadService
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageImageUploadService @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    @ApplicationContext private val appContext: Context // Using ApplicationContext
) : ImageUploadService {

    override suspend fun uploadImages(uris: List<Uri>, pathPrefix: String): Result<List<String>> = withContext(Dispatchers.IO) {
        if (uris.isEmpty()) {
            return@withContext Result.Success(emptyList())
        }
        try {
            val contentResolver = appContext.contentResolver
            val uploadTasks = uris.map { uri ->
                async { // Launch each upload concurrently
                    val fileName = "${UUID.randomUUID()}.${getExtension(contentResolver, uri) ?: "jpg"}"
                    val imageRef = firebaseStorage.reference.child("$pathPrefix/$fileName")

                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        imageRef.putStream(inputStream).await() // UploadTask
                    } ?: throw Exception("Failed to open input stream for URI: $uri")

                    imageRef.downloadUrl.await().toString() // Get download URL
                }
            }
            val downloadUrls = uploadTasks.awaitAll() // Wait for all uploads to complete
            Result.Success(downloadUrls)
        } catch (e: Exception) {
            Timber.e(e, "Error uploading images to Firebase Storage")
            Result.Error(e)
        }
    }

    override suspend fun uploadImage(uri: Uri, pathPrefix: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val contentResolver = appContext.contentResolver
            val fileName = "${UUID.randomUUID()}.${getExtension(contentResolver, uri) ?: "jpg"}"
            val imageRef = firebaseStorage.reference.child("$pathPrefix/$fileName")

            contentResolver.openInputStream(uri)?.use { inputStream ->
                imageRef.putStream(inputStream).await() // UploadTask
            } ?: throw Exception("Failed to open input stream for URI: $uri")

            val downloadUrl = imageRef.downloadUrl.await().toString() // Get download URL
            Result.Success(downloadUrl)
        } catch (e: StorageException) {
            Timber.e(e, "Firebase Storage specific error uploading image")
            Result.Error(Exception("Storage error: ${e.message}", e))
        }
        catch (e: Exception) {
            Timber.e(e, "Error uploading image to Firebase Storage")
            Result.Error(e)
        }
    }

    private fun getExtension(contentResolver: ContentResolver, uri: Uri): String? {
        return contentResolver.getType(uri)?.let { mimeType ->
            android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }
    }
}
