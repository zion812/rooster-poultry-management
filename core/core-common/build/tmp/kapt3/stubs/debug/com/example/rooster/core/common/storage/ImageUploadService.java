package com.example.rooster.core.common.storage;

/**
 * Service interface for uploading images to cloud storage.
 * Provides methods for single and batch image uploads with optional compression.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J0\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00042\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u00a6@\u00a2\u0006\u0002\u0010\nJ<\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\f0\u00032\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\f2\u0006\u0010\u0007\u001a\u00020\u00042\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u00a6@\u00a2\u0006\u0002\u0010\u000e\u00a8\u0006\u000f"}, d2 = {"Lcom/example/rooster/core/common/storage/ImageUploadService;", "", "uploadImage", "Lcom/example/rooster/core/common/Result;", "", "uri", "Landroid/net/Uri;", "pathPrefix", "compressionOptions", "Lcom/example/rooster/core/common/storage/ImageCompressionOptions;", "(Landroid/net/Uri;Ljava/lang/String;Lcom/example/rooster/core/common/storage/ImageCompressionOptions;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadImages", "", "uris", "(Ljava/util/List;Ljava/lang/String;Lcom/example/rooster/core/common/storage/ImageCompressionOptions;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "core-common_debug"})
public abstract interface ImageUploadService {
    
    /**
     * Uploads multiple images from their local URIs to a cloud storage path.
     *
     * @param uris List of content URIs of the images to upload
     * @param pathPrefix The base path in cloud storage where images should be stored
     * @param compressionOptions Optional parameters for image compression and resizing
     * @return A Result containing a list of public download URLs for the uploaded images
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object uploadImages(@org.jetbrains.annotations.NotNull()
    java.util.List<? extends android.net.Uri> uris, @org.jetbrains.annotations.NotNull()
    java.lang.String pathPrefix, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.storage.ImageCompressionOptions compressionOptions, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.rooster.core.common.Result<? extends java.util.List<java.lang.String>>> $completion);
    
    /**
     * Uploads a single image from its local URI to a cloud storage path.
     *
     * @param uri The content URI of the image to upload
     * @param pathPrefix The base path in cloud storage
     * @param compressionOptions Optional parameters for image compression and resizing
     * @return A Result containing the public download URL for the uploaded image
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object uploadImage(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    java.lang.String pathPrefix, @org.jetbrains.annotations.Nullable()
    com.example.rooster.core.common.storage.ImageCompressionOptions compressionOptions, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.rooster.core.common.Result<java.lang.String>> $completion);
    
    /**
     * Service interface for uploading images to cloud storage.
     * Provides methods for single and batch image uploads with optional compression.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}