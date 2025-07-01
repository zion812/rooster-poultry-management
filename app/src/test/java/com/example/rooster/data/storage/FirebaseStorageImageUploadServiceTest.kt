package com.example.rooster.data.storage

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.rooster.core.common.Result
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.InputStream

@ExperimentalCoroutinesApi
class FirebaseStorageImageUploadServiceTest {
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var mockContext: Context
    private lateinit var mockContentResolver: ContentResolver
    private lateinit var service: FirebaseStorageImageUploadService

    // Mocks for Firebase Storage interactions
    private lateinit var mockStorageRef: StorageReference
    private lateinit var mockChildRef: StorageReference
    private lateinit var mockUploadTask: UploadTask
    private lateinit var mockUploadTaskSnapshot: UploadTask.TaskSnapshot
    private lateinit var mockDownloadUrlTask: Task<Uri>
    private lateinit var mockDownloadUri: Uri

    @Before
    fun setUp() {
        firebaseStorage = mockk()
        mockContext = mockk()
        mockContentResolver = mockk()

        mockStorageRef = mockk()
        mockChildRef = mockk()
        mockUploadTask = mockk()
        mockUploadTaskSnapshot = mockk() // Mock the snapshot if needed for await()
        mockDownloadUrlTask = mockk()
        mockDownloadUri = mockk()

        every { mockContext.contentResolver } returns mockContentResolver
        every { firebaseStorage.reference } returns mockStorageRef
        every { mockStorageRef.child(any()) } returns mockChildRef
        every { mockChildRef.putStream(any()) } returns mockUploadTask

        // Mocking the await() behavior for UploadTask
        // This can be tricky. For simplicity, assume putStream().await() returns a snapshot.
        // Or directly mock the chained calls.
        coEvery { mockUploadTask.await() } returns mockUploadTaskSnapshot // If putStream().await() is used

        every { mockChildRef.downloadUrl } returns mockDownloadUrlTask
        coEvery { mockDownloadUrlTask.await() } returns mockDownloadUri
        every { mockDownloadUri.toString() } returns "http://fake.url/image.jpg"

        service = FirebaseStorageImageUploadService(firebaseStorage, mockContext)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `uploadImage success`() =
        runTest {
            val mockUri = mockk<Uri>()
            val mockInputStream = mockk<InputStream>(relaxed = true) // relaxed so close() doesn't need stubbing unless tested

            every { mockContentResolver.openInputStream(mockUri) } returns mockInputStream
            every { mockContentResolver.getType(mockUri) } returns "image/jpeg"
            // Assuming getExtensionFromMimeType is static or part of Android framework, may not need direct mock for MimeTypeMap

            val result = service.uploadImage(mockUri, "test_path")

            assertTrue(result is Result.Success)
            assertEquals("http://fake.url/image.jpg", (result as Result.Success).data)
            coVerify { mockChildRef.putStream(mockInputStream) }
            coVerify { mockDownloadUrlTask.await() }
        }

    @Test
    fun `uploadImage openInputStream fails`() =
        runTest {
            val mockUri = mockk<Uri>()
            every { mockContentResolver.openInputStream(mockUri) } returns null

            val result = service.uploadImage(mockUri, "test_path")

            assertTrue(result is Result.Error)
            assertTrue((result as Result.Error).exception.message?.contains("Failed to open input stream") == true)
        }

    @Test
    fun `uploadImage putStream fails`() =
        runTest {
            val mockUri = mockk<Uri>()
            val mockInputStream = mockk<InputStream>(relaxed = true)
            val storageException = mockk<com.google.firebase.storage.StorageException>()

            every { mockContentResolver.openInputStream(mockUri) } returns mockInputStream
            every { mockContentResolver.getType(mockUri) } returns "image/jpeg"
            coEvery { mockUploadTask.await() } throws storageException // Simulate failure during upload

            val result = service.uploadImage(mockUri, "test_path")

            assertTrue(result is Result.Error)
            coVerify { mockChildRef.putStream(mockInputStream) }
        }

    @Test
    fun `uploadImages success multiple URIs`() =
        runTest {
            val uri1 = mockk<Uri>("uri1")
            val uri2 = mockk<Uri>("uri2")
            val uris = listOf(uri1, uri2)
            val inputStream1 = mockk<InputStream>(relaxed = true)
            val inputStream2 = mockk<InputStream>(relaxed = true)

            every { mockContentResolver.openInputStream(uri1) } returns inputStream1
            every { mockContentResolver.getType(uri1) } returns "image/jpeg"
            every { mockContentResolver.openInputStream(uri2) } returns inputStream2
            every { mockContentResolver.getType(uri2) } returns "image/png"

            // For multiple calls to child() that return different StorageReference if names are different
            val childRef1 = mockk<StorageReference>()
            val childRef2 = mockk<StorageReference>()
            val uploadTask1 = mockk<UploadTask>()
            val uploadTask2 = mockk<UploadTask>()
            val downloadUrlTask1 = mockk<Task<Uri>>()
            val downloadUrlTask2 = mockk<Task<Uri>>()
            val downloadUri1String = "http://fake.url/image1.jpg"
            val downloadUri2String = "http://fake.url/image2.png"

            every { mockStorageRef.child(match { it.endsWith(".jpg") }) } returns childRef1
            every { mockStorageRef.child(match { it.endsWith(".png") }) } returns childRef2
            every { childRef1.putStream(inputStream1) } returns uploadTask1
            every { childRef2.putStream(inputStream2) } returns uploadTask2
            coEvery { uploadTask1.await() } returns mockk() // snapshot
            coEvery { uploadTask2.await() } returns mockk() // snapshot
            every { childRef1.downloadUrl } returns downloadUrlTask1
            every { childRef2.downloadUrl } returns downloadUrlTask2
            coEvery { downloadUrlTask1.await().toString() } returns downloadUri1String
            coEvery { downloadUrlTask2.await().toString() } returns downloadUri2String

            val result = service.uploadImages(uris, "test_path_multiple")

            assertTrue(result is Result.Success)
            assertEquals(listOf(downloadUri1String, downloadUri2String), (result as Result.Success).data)
        }

    @Test
    fun `uploadImages empty list returns success empty list`() =
        runTest {
            val result = service.uploadImages(emptyList(), "test_path")
            assertTrue(result is Result.Success)
            assertTrue((result as Result.Success).data.isEmpty())
        }

    // Add tests for specific StorageExceptions, general exceptions during awaitAll, etc.
}
