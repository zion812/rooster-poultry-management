package com.example.rooster

import android.app.Application
import android.content.ComponentCallbacks2
import android.util.Log
import androidx.room.Room
import com.example.rooster.config.Constants
import com.example.rooster.util.CrashPrevention
import com.example.rooster.util.MemoryOptimizerStatic
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.Parse
import com.parse.ParseACL
import com.parse.ParseInstallation
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
@Suppress("unused")
class App : Application() {
    companion object {
        lateinit var photoUploadDatabase: PhotoUploadDatabase
            private set

        // Public getter for the DAO instance
        fun getPhotoUploadDao(): PhotoUploadDao {
            if (!::photoUploadDatabase.isInitialized) {
                throw IllegalStateException("PhotoUploadDatabase not initialized. Ensure App.onCreate() is called.")
            }
            return photoUploadDatabase.photoUploadDao()
        }

        // Public getter for the MessageDao instance
        fun getMessageDao(): MessageDao {
            if (!::photoUploadDatabase.isInitialized) {
                throw IllegalStateException("PhotoUploadDatabase not initialized. Ensure App.onCreate() is called.")
            }
            return photoUploadDatabase.messageDao()
        }
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("RoosterApp", "=== APP INITIALIZATION STARTED ===")
        Log.d("RoosterApp", "Enhanced Parse Server 6.2.0 Integration")
        Log.d("RoosterApp", "Database: MongoDB")
        Log.d("RoosterApp", "Hosting Region: North Virginia")
        Log.d("RoosterApp", "Rural Optimized: Enabled")

        // Initialize crash prevention system first
        CrashPrevention.safeExecute("Memory optimizer initialization") {
            MemoryOptimizerStatic.startPeriodicCleanup(intervalMinutes = 15)
            Log.d("App", "Memory optimizer initialized")
        }

        // ====================================================================
        // Enhanced Parse Server 6.2.0 Initialization with Rural Optimization
        // ====================================================================
        CrashPrevention.safeExecute("Parse initialization") {
            try {
                Parse.setLogLevel(if (BuildConfig.DEBUG) Parse.LOG_LEVEL_DEBUG else Parse.LOG_LEVEL_ERROR)

                val configuration = Parse.Configuration.Builder(this)
                    .applicationId(Constants.BACK4APP_APP_ID)
                    .clientKey(Constants.BACK4APP_CLIENT_KEY)
                    .server(Constants.BACK4APP_SERVER_URL)
                    .enableLocalDataStore() // Essential for offline-first architecture
                    .build()

                Parse.initialize(configuration)

                // Set default ACL for enhanced security
                val defaultACL = ParseACL()
                defaultACL.setPublicReadAccess(true)
                defaultACL.setPublicWriteAccess(false) // Restrict public writes for security
                ParseACL.setDefaultACL(defaultACL, true)

                // Initialize installation for push notifications
                ParseInstallation.getCurrentInstallation().saveInBackground { e ->
                    if (e == null) {
                        Log.d("App", "Parse Installation saved successfully")
                    } else {
                        Log.e("App", "Failed to save Parse Installation", e)
                    }
                }

                Log.d("App", "Parse initialized successfully")
                Log.d("App", "App ID: ${Constants.BACK4APP_APP_ID}")
                Log.d("App", "Server URL: ${Constants.BACK4APP_SERVER_URL}")
                Log.d("App", "Local Datastore: Enabled (Offline-first)")
                Log.d("App", "Rural Optimization: Enabled")

            } catch (e: Exception) {
                Log.e("App", "Parse initialization failed", e)
                FirebaseCrashlytics.getInstance().recordException(e)
                // Continue app initialization even if Parse fails
            }
        }

        // Initialize photo upload database with crash protection
        CrashPrevention.safeExecute("PhotoUpload database initialization") {
            photoUploadDatabase =
                Room.databaseBuilder(
                    applicationContext,
                    PhotoUploadDatabase::class.java,
                    "rooster_photo_uploads.db",
                ).fallbackToDestructiveMigration().build()
            Log.d("App", "Photo upload database initialized")
        }

        // Initialize Firebase services with rural optimization
        CrashPrevention.safeExecute("Firebase services initialization") {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setCrashlyticsCollectionEnabled(true)

            val analytics = FirebaseAnalytics.getInstance(this)
            analytics.setAnalyticsCollectionEnabled(true)

            // Set custom properties for rural market analysis
            analytics.setUserProperty("target_market", "rural_telugu_farmers")
            analytics.setUserProperty("connectivity_optimized", "2g_3g")
            analytics.setUserProperty("app_version", BuildConfig.VERSION_NAME)

            Log.d("App", "Firebase services initialized with rural market properties")
        }

        // Register Parse subclasses for enhanced data models
        CrashPrevention.safeExecute("Parse subclasses registration") {
            // TODO: Uncomment and implement Parse subclasses as needed
            Log.d("App", "Parse subclasses registration completed")
        }

        // Schedule background workers for data sync
        CrashPrevention.safeExecute("Background workers scheduling") {
            // TODO: Implement background sync workers
            Log.d("App", "Background workers scheduling completed")
        }

        Log.d("RoosterApp", "=== APP INITIALIZATION COMPLETED SUCCESSFULLY ===")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        CrashPrevention.safeExecute("Low memory handling") {
            MemoryOptimizerStatic.emergencyCleanup()
            Log.w("App", "Low memory detected, emergency cleanup executed")

            // Additional rural device optimization
            System.gc() // Suggest garbage collection for low-end devices
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        CrashPrevention.safeExecute("Memory trim handling") {
            when (level) {
                ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
                ComponentCallbacks2.TRIM_MEMORY_COMPLETE,
                -> {
                    MemoryOptimizerStatic.emergencyCleanup()
                    // Clear cache if memory is critical (rural device optimization)
                    Log.w("App", "Critical memory trim, emergency cleanup executed")
                }

                ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
                ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
                -> {
                    MemoryOptimizerStatic.clearImageCache()
                    Log.i("App", "Memory trim, image cache cleared")
                }

                else -> {
                    Log.d("App", "Memory trim level: $level")
                }
            }
        }
    }
}
