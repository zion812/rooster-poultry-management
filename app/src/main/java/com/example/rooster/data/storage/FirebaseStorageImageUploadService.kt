package com.example.rooster.data.storage

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.rooster.core.common.Result
import com.example.rooster.core.common.storage.ImageCompressionOptions // Ensure this is imported
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
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
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
                    val fileName = "${UUID.randomUUID()}.${getExtension(contentResolver, uri, compressionOptions) ?: "jpg"}"
                    val imageRef = firebaseStorage.reference.child("$pathPrefix/$fileName")

                    getPossiblyCompressedInputStream(contentResolver, uri, compressionOptions).use { compressedInputStream ->
                        imageRef.putStream(compressedInputStream).await()
                    }
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
            val fileName = "${UUID.randomUUID()}.${getExtension(contentResolver, uri, compressionOptions) ?: "jpg"}"
            val imageRef = firebaseStorage.reference.child("$pathPrefix/$fileName")

            getPossiblyCompressedInputStream(contentResolver, uri, compressionOptions).use { compressedInputStream ->
                 imageRef.putStream(compressedInputStream).await()
            }

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

    private suspend fun getPossiblyCompressedInputStream(
        contentResolver: ContentResolver,
        uri: Uri,
        options: ImageCompressionOptions?
    ): InputStream {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw Exception("Failed to open input stream for URI: $uri for compression")

        if (options == null) {
            return inputStream // Return original if no compression needed
        }

        return withContext(Dispatchers.Default) { // Perform bitmap operations on Default dispatcher
            try {
                var bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close() // Close original stream after decoding

                // Calculate new dimensions while maintaining aspect ratio
                val originalWidth = bitmap.width
                val originalHeight = bitmap.height
                val maxDimension = options.maxWidthOrHeight

                if (originalWidth > maxDimension || originalHeight > maxDimension) {
                    val ratio: Float = if (originalWidth > originalHeight) {
                        maxDimension.toFloat() / originalWidth
                    } else {
                        maxDimension.toFloat() / originalHeight
                    }
                    val newWidth = (originalWidth * ratio).toInt()
                    val newHeight = (originalHeight * ratio).toInt()
                    if (newWidth > 0 && newHeight > 0) { // Ensure dimensions are valid
                         val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                         if (scaledBitmap != bitmap) { // If scaling happened and created a new bitmap
                             bitmap.recycle() // Recycle original if scaled version is different
                         }
                         bitmap = scaledBitmap
                    } else {
                        Timber.w("Calculated new dimensions for bitmap are invalid: $newWidth x $newHeight. Using original.")
                    }
                }

                val outputStream = ByteArrayOutputStream()
                val compressFormat = when (options.format.uppercase()) {
                    "PNG" -> Bitmap.CompressFormat.PNG
                    "WEBP" -> if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSY else Bitmap.CompressFormat.WEBP // Or WEBP_LOSSLESS
                    else -> Bitmap.CompressFormat.JPEG // Default to JPEG
                }
                bitmap.compress(compressFormat, options.quality, outputStream)
                bitmap.recycle() // Recycle the (possibly scaled) bitmap

                ByteArrayInputStream(outputStream.toByteArray())
            } catch (e: OutOfMemoryError) {
                Timber.e(e, "OutOfMemoryError during image compression for URI: $uri")
                // Fallback: try to return original stream, or throw a specific error
                // For simplicity, re-throwing for now, but a more graceful fallback could be attempted.
                throw Exception("OutOfMemoryError during image compression.", e)
            } catch (e: Exception) {
                Timber.e(e, "Exception during image compression for URI: $uri")
                throw e // Re-throw other exceptions
            } finally {
                 // Ensure original inputStream is closed if an early return happened before decodeStream's use block
                try { inputStream.close() } catch (e: Exception) { /* ignore */ }
            }
        }
    }
}
