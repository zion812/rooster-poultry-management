package com.example.rooster.core.auth.data.repository

import com.example.rooster.core.auth.domain.model.User
import com.example.rooster.core.auth.domain.model.UserRole
import com.example.rooster.core.common.Result
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult as FirebaseAuthResult // Alias to avoid confusion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.*

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryImplTest {

    @Mock
    private lateinit var mockFirebaseAuth: FirebaseAuth
    @Mock
    private lateinit var mockFirebaseFirestore: FirebaseFirestore
    @Mock
    private lateinit var mockFirebaseUser: FirebaseUser
    @Mock
    private lateinit var mockAuthResult: FirebaseAuthResult
    @Mock
    private lateinit var mockDocumentReference: DocumentReference
    @Mock
    private lateinit var mockCollectionReference: CollectionReference
    @Mock
    private lateinit var mockDocumentSnapshot: DocumentSnapshot
    @Mock
    private lateinit var mockVoidTask: Task<Void>
    @Mock
    private lateinit var mockAuthResultTask: Task<FirebaseAuthResult>
    @Mock
    private lateinit var mockDocumentSnapshotTask: Task<DocumentSnapshot>


    private lateinit var authRepository: AuthRepositoryImpl

    private val testDispatcher = UnconfinedTestDispatcher() // For test coroutines

    @Before
    fun setUp() {
        // MockitoAnnotations.openMocks(this) // Already using MockitoJUnitRunner
        authRepository = AuthRepositoryImpl(mockFirebaseAuth, mockFirebaseFirestore)

        // Common mocks for Firebase tasks
        `when`(mockVoidTask.isSuccessful).thenReturn(true)
        `when`(mockVoidTask.isComplete).thenReturn(true)
        `when`(mockVoidTask.exception).thenReturn(null)
        `when`(mockVoidTask.result).thenReturn(null)


        `when`(mockAuthResultTask.isSuccessful).thenReturn(true)
        `when`(mockAuthResultTask.isComplete).thenReturn(true)
        `when`(mockAuthResultTask.exception).thenReturn(null)
        `when`(mockAuthResultTask.result).thenReturn(mockAuthResult)

        `when`(mockDocumentSnapshotTask.isSuccessful).thenReturn(true)
        `when`(mockDocumentSnapshotTask.isComplete).thenReturn(true)
        `when`(mockDocumentSnapshotTask.exception).thenReturn(null)
        `when`(mockDocumentSnapshotTask.result).thenReturn(mockDocumentSnapshot)

        // Common Firestore collection/document mocking
        `when`(mockFirebaseFirestore.collection("users")).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    }

    private fun <T> Task<T>.setupForAwait(result: T? = null, exception: Exception? = null) {
        `when`(this.isComplete).thenReturn(true)
        `when`(this.isSuccessful).thenReturn(exception == null)
        `when`(this.result).thenReturn(result)
        `when`(this.exception).thenReturn(exception)
    }

    @Test
    fun `signIn with valid credentials returns success with user profile`() = runTest(testDispatcher) {
        val email = "test@example.com"
        val password = "password"
        val uid = "testUid"
        val userDomainModel = User(id = uid, email = email, displayName = "Test User", isEmailVerified = true)

        `when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockAuthResultTask)
        mockAuthResultTask.setupForAwait(mockAuthResult)
        `when`(mockAuthResult.user).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn(uid)
        `when`(mockFirebaseUser.isEmailVerified).thenReturn(true)
        `when`(mockFirebaseUser.phoneNumber).thenReturn("")


        `when`(mockDocumentReference.get()).thenReturn(mockDocumentSnapshotTask)
        mockDocumentSnapshotTask.setupForAwait(mockDocumentSnapshot)
        `when`(mockDocumentSnapshot.exists()).thenReturn(true)
        `when`(mockDocumentSnapshot.toObject(User::class.java)).thenReturn(userDomainModel)

        val result = authRepository.signIn(email, password)

        assertTrue(result is Result.Success)
        assertEquals(userDomainModel.id, (result as Result.Success).data.id)
        verify(mockFirebaseAuth).signInWithEmailAndPassword(email, password)
        verify(mockDocumentReference).get()
    }

    @Test
    fun `signIn with invalid credentials returns error`() = runTest(testDispatcher) {
        val email = "test@example.com"
        val password = "wrongpassword"
        val authException = FirebaseAuthException("ERROR_WRONG_PASSWORD", "Invalid credentials")

        `when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockAuthResultTask)
        mockAuthResultTask.setupForAwait(exception = authException)


        val result = authRepository.signIn(email, password)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception.message!!.contains("Incorrect password"))
        verify(mockFirebaseAuth).signInWithEmailAndPassword(email, password)
    }

    @Test
    fun `signIn when user profile does not exist in Firestore returns error and signs out`() = runTest(testDispatcher) {
        val email = "test@example.com"
        val password = "password"
        val uid = "testUid"

        `when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockAuthResultTask)
        mockAuthResultTask.setupForAwait(mockAuthResult)
        `when`(mockAuthResult.user).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn(uid)

        `when`(mockDocumentReference.get()).thenReturn(mockDocumentSnapshotTask)
        mockDocumentSnapshotTask.setupForAwait(mockDocumentSnapshot)
        `when`(mockDocumentSnapshot.exists()).thenReturn(false) // Profile doesn't exist

        val result = authRepository.signIn(email, password)

        assertTrue(result is Result.Error)
        assertEquals("User profile not found in database.", (result as Result.Error).exception.message)
        verify(mockFirebaseAuth).signOut() // Verify user is signed out due to inconsistent state
    }


    @Test
    fun `signUp with valid details creates user and returns success`() = runTest(testDispatcher) {
        val email = "newuser@example.com"
        val password = "newpassword"
        val displayName = "New User"
        val uid = "newUid"

        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockAuthResultTask)
        mockAuthResultTask.setupForAwait(mockAuthResult)
        `when`(mockAuthResult.user).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn(uid)
        `when`(mockFirebaseUser.isEmailVerified).thenReturn(false)
        `when`(mockFirebaseUser.sendEmailVerification()).thenReturn(mockVoidTask)
        mockVoidTask.setupForAwait()

        `when`(mockDocumentReference.set(any(User::class.java))).thenReturn(mockVoidTask)

        val result = authRepository.signUp(email, password, displayName, UserRole.FARMER, null)

        assertTrue(result is Result.Success)
        assertEquals(uid, (result as Result.Success).data.id)
        assertEquals(email, result.data.email)
        verify(mockFirebaseAuth).createUserWithEmailAndPassword(email, password)
        verify(mockFirebaseUser).sendEmailVerification()
        verify(mockDocumentReference).set(any(User::class.java))
    }

    @Test
    fun `signUp with email already in use returns error`() = runTest(testDispatcher) {
        val email = "existing@example.com"
        val password = "password"
        val displayName = "Existing User"
        val authException = FirebaseAuthException("ERROR_EMAIL_ALREADY_IN_USE", "Email already exists")

        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockAuthResultTask)
        mockAuthResultTask.setupForAwait(exception = authException)

        val result = authRepository.signUp(email, password, displayName, UserRole.FARMER, null)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception.message!!.contains("This email is already registered"))
        verify(mockFirebaseAuth).createUserWithEmailAndPassword(email, password)
    }

    @Test
    fun `signOut calls firebaseAuth signOut`() = runTest(testDispatcher) {
        authRepository.signOut()
        verify(mockFirebaseAuth).signOut()
    }

    @Test
    fun `resetPassword with valid email returns success`() = runTest(testDispatcher) {
        val email = "test@example.com"
        `when`(mockFirebaseAuth.sendPasswordResetEmail(email)).thenReturn(mockVoidTask)
        mockVoidTask.setupForAwait()

        val result = authRepository.resetPassword(email)

        assertTrue(result is Result.Success)
        verify(mockFirebaseAuth).sendPasswordResetEmail(email)
    }

    @Test
    fun `resetPassword with non-existent email still returns success (Firebase behavior)`() = runTest(testDispatcher) {
        val email = "nonexistent@example.com"
        // Firebase sendPasswordResetEmail usually succeeds even if email doesn't exist to prevent account enumeration.
        // It might throw for malformed emails, but not typically for non-existence.
        `when`(mockFirebaseAuth.sendPasswordResetEmail(email)).thenReturn(mockVoidTask)
        mockVoidTask.setupForAwait()

        val result = authRepository.resetPassword(email)
        assertTrue(result is Result.Success)
    }


    @Test
    fun `resetPassword with invalid email format returns error`() = runTest(testDispatcher) {
        val email = "invalid-email"
        val authException = FirebaseAuthException("ERROR_INVALID_EMAIL", "Invalid email format")
        `when`(mockFirebaseAuth.sendPasswordResetEmail(email)).thenReturn(mockVoidTask)
        mockVoidTask.setupForAwait(exception = authException)


        val result = authRepository.resetPassword(email)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception.message!!.contains("Invalid email format"))
        verify(mockFirebaseAuth).sendPasswordResetEmail(email)
    }

    @Test
    fun `isUserSignedIn returns true when firebaseUser is not null`() = runTest(testDispatcher) {
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        assertTrue(authRepository.isUserSignedIn())
    }

    @Test
    fun `isUserSignedIn returns false when firebaseUser is null`() = runTest(testDispatcher) {
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        assertFalse(authRepository.isUserSignedIn())
    }

    @Test
    fun `sendCurrentUserEmailVerification returns success when user exists`() = runTest(testDispatcher) {
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.sendEmailVerification()).thenReturn(mockVoidTask)
        mockVoidTask.setupForAwait()

        val result = authRepository.sendCurrentUserEmailVerification()
        assertTrue(result is Result.Success)
        verify(mockFirebaseUser).sendEmailVerification()
    }

    @Test
    fun `sendCurrentUserEmailVerification returns error when no user`() = runTest(testDispatcher) {
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        val result = authRepository.sendCurrentUserEmailVerification()
        assertTrue(result is Result.Error)
        assertEquals("No current user to send verification email.", (result as Result.Error).exception.message)
    }

    @Test
    fun `reloadCurrentUser returns success with updated user when user exists`() = runTest(testDispatcher) {
        val uid = "testUid"
        val email = "test@example.com"
        val updatedUserDomainModel = User(id = uid, email = email, displayName = "Test User Reloaded", isEmailVerified = true)

        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser) // Initial currentUser
        `when`(mockFirebaseUser.reload()).thenReturn(mockVoidTask)       // reload() call
        mockVoidTask.setupForAwait()
        // After reload(), mockFirebaseAuth.currentUser should return the (potentially) updated mockFirebaseUser
        // For simplicity, we assume mockFirebaseUser instance itself is "updated" by mocks later if needed,
        // or that fetchUserDetails will get new data.

        `when`(mockFirebaseUser.uid).thenReturn(uid)
        `when`(mockFirebaseUser.isEmailVerified).thenReturn(true) // Assume email verified after reload
        `when`(mockFirebaseUser.phoneNumber).thenReturn("12345")

        `when`(mockDocumentReference.get()).thenReturn(mockDocumentSnapshotTask)
        mockDocumentSnapshotTask.setupForAwait(mockDocumentSnapshot)
        `when`(mockDocumentSnapshot.exists()).thenReturn(true)
        `when`(mockDocumentSnapshot.toObject(User::class.java)).thenReturn(updatedUserDomainModel)


        val result = authRepository.reloadCurrentUser()

        assertTrue(result is Result.Success)
        assertEquals(updatedUserDomainModel.displayName, (result as Result.Success).data?.displayName)
        assertTrue(result.data!!.isEmailVerified)
        verify(mockFirebaseUser).reload()
        verify(mockDocumentReference).get()
    }

    @Test
    fun `reloadCurrentUser returns success with null when no user`() = runTest(testDispatcher) {
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)
        val result = authRepository.reloadCurrentUser()
        assertTrue(result is Result.Success)
        assertNull((result as Result.Success).data)
    }

    @Test
    fun `updateProfile successfully updates user in Firestore and returns success`() = runTest(testDispatcher) {
        val uid = "testUid"
        val userToUpdate = User(id = uid, email = "test@example.com", displayName = "Updated Name")

        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn(uid)
        `when`(mockFirebaseUser.displayName).thenReturn("Old Name") // Current display name

        // Mock Firestore set operation
        `when`(mockDocumentReference.set(any(User::class.java), any(com.google.firebase.firestore.SetOptions::class.java))).thenReturn(mockVoidTask)
        mockVoidTask.setupForAwait()

        // Mock FirebaseUser updateProfile operation
        `when`(mockFirebaseUser.updateProfile(any())).thenReturn(mockVoidTask)

        val result = authRepository.updateProfile(userToUpdate)

        assertTrue(result is Result.Success)
        assertEquals(userToUpdate.displayName, (result as Result.Success).data.displayName)
        verify(mockDocumentReference).set(
            org.mockito.kotlin.check<User> {
                assertEquals(userToUpdate.displayName, it.displayName)
                assertTrue(it.updatedAt > userToUpdate.updatedAt) // Check updatedAt is modified
            },
            any(com.google.firebase.firestore.SetOptions::class.java)
        )
        verify(mockFirebaseUser).updateProfile(any())
    }

    @Test
    fun `updateProfile for different user ID returns error`() = runTest(testDispatcher) {
        val userToUpdate = User(id = "differentUid", displayName = "Should Not Update")
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn("testUid")

        val result = authRepository.updateProfile(userToUpdate)

        assertTrue(result is Result.Error)
        assertEquals("Cannot update profile for a different user", (result as Result.Error).exception.message)
        verify(mockDocumentReference, never()).set(any(), any())
    }

    // Test for getCurrentUser() is more complex due to callbackFlow.
    // It would involve testing emissions over time.
    // Here's a basic test for the null (unauthenticated) case.
    @Test
    fun `getCurrentUser emits null when no firebase user initially`() = runTest(testDispatcher) {
        // Simulate no user initially by making addAuthStateListener immediately call with null user
        whenever(mockFirebaseAuth.addAuthStateListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument(0) as FirebaseAuth.AuthStateListener
            listener.onAuthStateChanged(mockFirebaseAuth) // mockFirebaseAuth.currentUser is null by default here
            mock() // Return a mock AuthStateListenerRegistration if needed by awaitClose
        }
        `when`(mockFirebaseAuth.currentUser).thenReturn(null)


        val user = authRepository.getCurrentUser().first() // Collect the first emission

        assertNull(user)
    }

    // Further tests for getCurrentUser could involve:
    // - User logs in: emits User
    // - User logs out: emits null
    // - User profile changes in Firestore: emits updated User (if listener is set up for that, which current impl doesn't directly do beyond initial fetch)
}
```
