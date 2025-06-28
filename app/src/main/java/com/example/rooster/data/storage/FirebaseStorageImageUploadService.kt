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

    override suspend fun uploadImages(
        uris: List<Uri>,
        pathPrefix: String,
        compressionOptions: ImageCompressionOptions? // Added parameter
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        if (uris.isEmpty()) {
            return@withContext Result.Success(emptyList())
        }
        try {
            val contentResolver = appContext.contentResolver
            val uploadTasks = uris.map { uri ->
                async { // Launch each upload concurrently
                    // TODO: Implement actual image compression based on compressionOptions before upload
                    // For now, it still uploads the original stream.
                    // InputStream will need to be from the compressed bitmap.

                    val fileName = "${UUID.randomUUID()}.${getExtension(contentResolver, uri, compressionOptions) ?: "jpg"}"
                    val imageRef = firebaseStorage.reference.child("$pathPrefix/$fileName")

                    contentResolver.openInputStream(uri)?.use { inputStream -> // This is original stream
                        // val compressedInputStream = applyCompression(inputStream, compressionOptions) // Hypothetical
                        imageRef.putStream(inputStream).await()
                    } ?: throw Exception("Failed to open input stream for URI: $uri")

                    imageRef.downloadUrl.await().toString()
                }
            }
            val downloadUrls = uploadTasks.awaitAll()
            Result.Success(downloadUrls)
        } catch (e: Exception) {
            Timber.e(e, "Error uploading images to Firebase Storage with options: $compressionOptions")
            Result.Error(e)
        }
    }

    override suspend fun uploadImage(
        uri: Uri,
        pathPrefix: String,
        compressionOptions: ImageCompressionOptions? // Added parameter
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val contentResolver = appContext.contentResolver
            // TODO: Implement actual image compression based on compressionOptions here too.
            // val processedInputStream = getPossiblyCompressedInputStream(uri, compressionOptions)

            val fileName = "${UUID.randomUUID()}.${getExtension(contentResolver, uri, compressionOptions) ?: "jpg"}"
            val imageRef = firebaseStorage.reference.child("$pathPrefix/$fileName")

            contentResolver.openInputStream(uri)?.use { inputStream -> // This is original stream
                imageRef.putStream(inputStream).await()
            } ?: throw Exception("Failed to open input stream for URI: $uri")

            val downloadUrl = imageRef.downloadUrl.await().toString()
            Result.Success(downloadUrl)
        } catch (e: StorageException) {
            Timber.e(e, "Firebase Storage specific error uploading image with options $compressionOptions")
            Result.Error(Exception("Storage error: ${e.message}", e))
        } catch (e: Exception) {
            Timber.e(e, "Error uploading image to Firebase Storage with options $compressionOptions")
            Result.Error(e)
        }
    }

    private fun getExtension(contentResolver: ContentResolver, uri: Uri, compressionOptions: ImageCompressionOptions? = null): String? {
        if (compressionOptions != null) {
            return when (compressionOptions.format.uppercase()) {
                "JPEG" -> "jpg"
                "PNG" -> "png"
                "WEBP" -> "webp"
                else -> contentResolver.getType(uri)?.let { mimeType ->
                            android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                         }
            }
        }
        return contentResolver.getType(uri)?.let { mimeType ->
            android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }
    }

    // TODO: Placeholder for actual compression logic
    // private suspend fun getPossiblyCompressedInputStream(uri: Uri, options: ImageCompressionOptions?): InputStream {
    //     val originalStream = appContext.contentResolver.openInputStream(uri) ?: throw Exception("Cannot open stream")
    //     if (options == null) return originalStream
    //
    //     // Decode, resize, compress bitmap
    //     // val bitmap = BitmapFactory.decodeStream(originalStream)
    //     // val scaledBitmap = ... scale bitmap using options.maxWidthOrHeight ...
    //     // val outputStream = ByteArrayOutputStream()
    //     // scaledBitmap.compress(Bitmap.CompressFormat.valueOf(options.format.uppercase()), options.quality, outputStream)
    //     // return ByteArrayInputStream(outputStream.toByteArray())
    //     return originalStream // Placeholder
    // }
}
