package com.example.rooster.core.common.storage

import android.net.Uri
import com.example.rooster.core.common.Result // Assuming this is your Result class

interface ImageUploadService {
    /**
     * Uploads a list of images from their local URIs to a cloud storage path.
     *
     * @param uris List of content URIs of the images to upload.
     * @param pathPrefix The base path in cloud storage where images should be stored (e.g., "listings_images/user123").
     *                   Individual files will typically get unique names (like UUIDs) under this prefix.
     * @param compressionOptions Optional parameters for image compression and resizing.
     * @return A Result containing a list of public download URLs for the uploaded images, or an error.
     *         The order of URLs in the list should correspond to the order of input URIs.
     */
    suspend fun uploadImages(
        uris: List<Uri>,
        pathPrefix: String,
        compressionOptions: ImageCompressionOptions? = null
    ): Result<List<String>>

    /**
     * Uploads a single image from its local URI to a cloud storage path.
     *
     * @param uri The content URI of the image to upload.
     * @param pathPrefix The base path in cloud storage.
     * @param compressionOptions Optional parameters for image compression and resizing.
     * @return A Result containing the public download URL for the uploaded image, or an error.
     */
    suspend fun uploadImage(
        uri: Uri,
        pathPrefix: String,
        compressionOptions: ImageCompressionOptions? = null
    ): Result<String>
}

data class ImageCompressionOptions(
    val maxWidthOrHeight: Int = 1024, // Max dimension (width or height)
    val quality: Int = 80, // JPEG quality (0-100)
    val format: String = "JPEG" // Could be "PNG", "WEBP" - though compression mostly for JPEG/WEBP
)
