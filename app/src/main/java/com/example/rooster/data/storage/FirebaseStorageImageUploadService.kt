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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Singleton
class FirebaseStorageImageUploadService @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    @ApplicationContext private val appContext: Context // Using ApplicationContext
) : ImageUploadService {

 jules/arch-assessment-1
    override suspend fun uploadImages(
        uris: List<Uri>,
        pathPrefix: String,
        compressionOptions: ImageCompressionOptions? // Added parameter
    ): Result<List<String>> = withContext(Dispatchers.IO) {
=======
    override suspend fun uploadImages(uris: List<Uri>, pathPrefix: String): Result<List<String>> = withContext(Dispatchers.IO) {
 main
        if (uris.isEmpty()) {
            return@withContext Result.Success(emptyList())
        }
        try {
            val contentResolver = appContext.contentResolver
            val uploadTasks = uris.map { uri ->
                async { // Launch each upload concurrently
 jules/arch-assessment-1
                    // TODO: Implement actual image compression based on compressionOptions before upload
                    // For now, it still uploads the original stream.
                    // InputStream will need to be from the compressed bitmap.

                    val fileName = "${UUID.randomUUID()}.${getExtension(contentResolver, uri, compressionOptions) ?: "jpg"}"
                    val imageRef = firebaseStorage.reference.child("$pathPrefix/$fileName")

                    getCompressedInputStream(contentResolver, uri, compressionOptions).use { compressedInputStream ->
                        imageRef.putStream(compressedInputStream).await()
                    }

                    imageRef.downloadUrl.await().toString()
                }
            }
            val downloadUrls = uploadTasks.awaitAll()
            Result.Success(downloadUrls)
        } catch (e: Exception) {
            Timber.e(e, "Error uploading images to Firebase Storage with options: $compressionOptions")
=======
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
 main
            Result.Error(e)
        }
    }

 jules/arch-assessment-1
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

            getCompressedInputStream(contentResolver, uri, compressionOptions).use { compressedInputStream ->
                imageRef.putStream(compressedInputStream).await()
            }

            val downloadUrl = imageRef.downloadUrl.await().toString()
            Result.Success(downloadUrl)
        } catch (e: StorageException) {
            Timber.e(e, "Firebase Storage specific error uploading image with options $compressionOptions")
            Result.Error(Exception("Storage error: ${e.message}", e))
        } catch (e: Exception) {
            Timber.e(e, "Error uploading image to Firebase Storage with options $compressionOptions")
=======
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
 main
            Result.Error(e)
        }
    }

 jules/arch-assessment-1
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
=======
    private fun getExtension(contentResolver: ContentResolver, uri: Uri): String? {
 main
        return contentResolver.getType(uri)?.let { mimeType ->
            android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }
    }
 jules/arch-assessment-1

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

    private fun getCompressedInputStream(
        contentResolver: ContentResolver,
        uri: Uri,
        options: ImageCompressionOptions?
    ): InputStream {
        val originalInputStream = contentResolver.openInputStream(uri)
            ?: throw Exception("Failed to open input stream for URI for compression: $uri")

        if (options == null) {
            return originalInputStream
        }

        originalInputStream.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
                ?: throw Exception("Failed to decode bitmap from URI: $uri")

            // Calculate new dimensions
            val originalWidth = bitmap.width
            val originalHeight = bitmap.height
            val scaleFactor = if (originalWidth > originalHeight && originalWidth > options.maxWidthOrHeight) {
                options.maxWidthOrHeight.toFloat() / originalWidth
            } else if (originalHeight > originalWidth && originalHeight > options.maxWidthOrHeight) {
                options.maxWidthOrHeight.toFloat() / originalHeight
            } else if (originalWidth == originalHeight && originalWidth > options.maxWidthOrHeight) { // Square or already smaller
                 options.maxWidthOrHeight.toFloat() / originalWidth
            }
            else {
                1.0f // No scaling needed
            }

            val targetWidth = (originalWidth * scaleFactor).toInt().coerceAtLeast(1)
            val targetHeight = (originalHeight * scaleFactor).toInt().coerceAtLeast(1)

            val scaledBitmap = if (scaleFactor != 1.0f) {
                Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
            } else {
                bitmap
            }

            val outputStream = ByteArrayOutputStream()
            val compressFormat = try {
                Bitmap.CompressFormat.valueOf(options.format.uppercase())
            } catch (e: IllegalArgumentException) {
                Timber.w("Invalid compression format: ${options.format}. Defaulting to JPEG.")
                Bitmap.CompressFormat.JPEG // Default to JPEG if format is unknown
            }

            scaledBitmap.compress(compressFormat, options.quality.coerceIn(0, 100), outputStream)

            if (scaledBitmap != bitmap) { // Recycle scaledBitmap if it's a new instance
                scaledBitmap.recycle()
            }
            bitmap.recycle() // Always recycle the original bitmap

            return ByteArrayInputStream(outputStream.toByteArray())
        }
    }
=======
 main
}
