package com.example.rooster.data.storage

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.rooster.core.common.Result
import com.example.rooster.core.common.storage.ImageUploadService
import com.example.rooster.core.common.storage.ImageCompressionOptions
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
import java.util.Collections.emptyList

@Singleton
class FirebaseStorageImageUploadService @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    @ApplicationContext private val appContext: Context
) : ImageUploadService {

    override suspend fun uploadImages(
        uris: List<Uri>,
        pathPrefix: String,
        compressionOptions: ImageCompressionOptions?
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        if (uris.isEmpty()) {
            return@withContext Result.Success(emptyList())
        }
        try {
            val contentResolver = appContext.contentResolver
            val uploadTasks = uris.map { uri ->
                async {
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
            Result.Error(e)
        }
    }

    override suspend fun uploadImage(
        uri: Uri,
        pathPrefix: String,
        compressionOptions: ImageCompressionOptions?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val contentResolver = appContext.contentResolver
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
            } else if (originalWidth == originalHeight && originalWidth > options.maxWidthOrHeight) {
                options.maxWidthOrHeight.toFloat() / originalWidth
            } else {
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
                Bitmap.CompressFormat.JPEG
            }

            scaledBitmap.compress(compressFormat, options.quality.coerceIn(0, 100), outputStream)

            if (scaledBitmap != bitmap) {
                scaledBitmap.recycle()
            }
            bitmap.recycle()

            return ByteArrayInputStream(outputStream.toByteArray())
        }
    }
}
