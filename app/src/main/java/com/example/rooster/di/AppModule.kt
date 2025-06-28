package com.example.rooster.di

import com.example.rooster.data.repositories.AuthRepositoryImpl
import com.example.rooster.data.repositories.ChatRepositoryImpl
import com.example.rooster.data.repositories.PaymentRepository
import com.example.rooster.data.repositories.UserRepositoryImpl
import com.example.rooster.domain.repository.AuthRepository
import com.example.rooster.domain.repository.ChatRepository
import com.example.rooster.domain.repository.PostRepository
import com.example.rooster.domain.repository.UserRepository
import com.example.rooster.util.ShoppingCartManager
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
 main
import com.example.rooster.core.common.user.UserIdProvider
import com.example.rooster.core.common.storage.ImageUploadService // Import interface
import com.example.rooster.data.authprovider.FirebaseUserIdProvider
import com.example.rooster.data.storage.FirebaseStorageImageUploadService // Import impl
import com.google.firebase.storage.FirebaseStorage // Import FirebaseStorage
import dagger.Binds
 jules/arch-assessment-1
=======
=======
import com.example.rooster.core.common.user.UserIdProvider // Import interface
import com.example.rooster.data.authprovider.FirebaseUserIdProvider // Import impl
import dagger.Binds // Import Binds
 main
 main
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.rooster.data.AuthRepository as ConcreteAuthRepository
import com.example.rooster.data.repositories.PostRepository as ConcretePostRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideConcreteAuthRepository(): ConcreteAuthRepository {
        return ConcreteAuthRepository()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideChatRepository(impl: ChatRepositoryImpl): ChatRepository = impl

    @Provides
    @Singleton
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl

    @Provides
    @Singleton
    fun providePaymentRepository(): PaymentRepository = PaymentRepository()

    @Provides
    @Singleton
    fun providePostRepository(impl: ConcretePostRepository): PostRepository = impl

    @Provides
    @Singleton
    fun provideShoppingCartManager(): ShoppingCartManager = ShoppingCartManager()

    @Provides
    @com.example.rooster.core.network.qualifiers.PaymentApiBaseUrl // Fully qualify if not imported
    @Singleton
    fun providePaymentApiBaseUrl(): String {
        return com.example.rooster.BuildConfig.PAYMENT_API_BASE_URL // Fully qualify BuildConfig
    }
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
 main

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage { // Provide FirebaseStorage
        return FirebaseStorage.getInstance()
    }
}

// AuthBindsModule remains separate for @Binds methods
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindsModule {
    @Binds
    @Singleton
    abstract fun bindUserIdProvider(impl: FirebaseUserIdProvider): UserIdProvider

    @Binds
    @Singleton
    abstract fun bindImageUploadService(impl: FirebaseStorageImageUploadService): ImageUploadService
 jules/arch-assessment-1
=======
=======
 main
 main
}

// Separate module for Binds is cleaner, or can be added to AppModule if it's an abstract class.
// For simplicity, if AppModule remains an 'object', we can't use @Binds here.
// Let's create a new AuthBindsModule or similar in the app.di package.
// However, to keep it simple for now, I'll modify AppModule to be abstract and add @Binds.
// This is a common pattern change if a module needs both @Provides and @Binds.

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthBindsModule { // New module for bindings related to auth or user providers
    @Binds
    @Singleton
    abstract fun bindUserIdProvider(impl: FirebaseUserIdProvider): UserIdProvider
}

// If AppModule needs to stay an 'object' and cannot be abstract:
// We would need to provide FirebaseUserIdProvider via @Provides in AppModule,
// and then also provide UserIdProvider by taking FirebaseUserIdProvider as a parameter.
// e.g. in AppModule:
// @Provides
// @Singleton
// fun provideFirebaseUserIdProvider(firebaseAuth: FirebaseAuth): FirebaseUserIdProvider {
//     return FirebaseUserIdProvider(firebaseAuth)
// }
// @Provides
// @Singleton
// fun provideUserIdProvider(impl: FirebaseUserIdProvider): UserIdProvider = impl
// But since FirebaseUserIdProvider is @Singleton and @Inject constructor, Hilt can create it.
// So, just binding is enough if we use a module that supports @Binds.
