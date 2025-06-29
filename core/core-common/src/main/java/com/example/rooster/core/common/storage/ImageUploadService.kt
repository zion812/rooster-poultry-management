package com.example.rooster.core.common.storage

import android.net.Uri
import com.example.rooster.core.common.Result

/**
 * Service interface for uploading images to cloud storage.
 * Provides methods for single and batch image uploads with optional compression.
 */
interface ImageUploadService {

    /**
     * Uploads multiple images from their local URIs to a cloud storage path.
     *
     * @param uris List of content URIs of the images to upload
     * @param pathPrefix The base path in cloud storage where images should be stored
     * @param compressionOptions Optional parameters for image compression and resizing
     * @return A Result containing a list of public download URLs for the uploaded images
     */
    suspend fun uploadImages(
        uris: List<Uri>,
        pathPrefix: String,
        compressionOptions: ImageCompressionOptions? = null
    ): Result<List<String>>

    /**
     * Uploads a single image from its local URI to a cloud storage path.
     *
     * @param uri The content URI of the image to upload
     * @param pathPrefix The base path in cloud storage
     * @param compressionOptions Optional parameters for image compression and resizing
     * @return A Result containing the public download URL for the uploaded image
     */
    suspend fun uploadImage(
        uri: Uri,
        pathPrefix: String,
        compressionOptions: ImageCompressionOptions? = null
    ): Result<String>
}

/**
 * Configuration options for image compression during upload.
 */
data class ImageCompressionOptions(
    val maxWidthOrHeight: Int = 1024,
    val quality: Int = 80,
    val format: String = "JPEG"
)