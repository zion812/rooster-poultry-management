package com.example.rooster

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.example.rooster.util.CrashPrevention
import com.example.rooster.util.MemoryOptimizerStatic
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.Parse

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

        // Initialize crash prevention system first
        // CrashPrevention system initialized automatically

        // Initialize memory optimizer for low-end devices
        CrashPrevention.safeExecute("Memory optimizer initialization") {
            MemoryOptimizerStatic.startPeriodicCleanup(intervalMinutes = 15)
            Log.d("App", "Memory optimizer initialized")
        }

        // Parse initialization with crash protection
        CrashPrevention.safeExecute("Parse initialization") {
            try {
                Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG)
                val configuration =
                    Parse.Configuration.Builder(this)
                        .applicationId(BuildConfig.PARSE_APP_ID)
                        .clientKey(BuildConfig.PARSE_CLIENT_KEY)
                        .server(BuildConfig.PARSE_SERVER_URL)
                        .enableLocalDataStore()
                        .build()

                Parse.initialize(configuration)
                Log.d(
                    "App",
                    "Parse initialized successfully with App ID: ${BuildConfig.PARSE_APP_ID}",
                )
            } catch (e: Exception) {
                Log.e("App", "Parse initialization failed", e)
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
        }

        // Initialize Firebase Crashlytics
        CrashPrevention.safeExecute("Firebase Crashlytics initialization") {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.setCrashlyticsCollectionEnabled(true)
            val analytics = FirebaseAnalytics.getInstance(this)
            analytics.setAnalyticsCollectionEnabled(true)
        }

        // Register Parse subclasses (commented out problematic ones for now)
        CrashPrevention.safeExecute("Parse subclasses registration") {
            // ParseObject.registerSubclass(com.example.rooster.data.model.UATFeedback::class.java)
            // ParseObject.registerSubclass(com.example.rooster.data.model.GrowthRecord::class.java)
            // ParseObject.registerSubclass(com.example.rooster.data.model.BreedingCycle::class.java)
            // ParseObject.registerSubclass(com.example.rooster.data.model.EggBatch::class.java)
            // ParseObject.registerSubclass(com.example.rooster.data.model.ChickBatch::class.java)
            // ParseObject.registerSubclass(com.example.rooster.data.model.MortalityLog::class.java)
            // ParseObject.registerSubclass(com.example.rooster.data.model.QuarantineLog::class.java)
        }

        // Schedule background workers
        CrashPrevention.safeExecute("Background workers scheduling") {
            // LifecycleWorker.schedule(this)
            // DataSyncWorker.schedule(this)
        }

        Log.d("RoosterApp", "=== APP INITIALIZATION COMPLETED ===")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        CrashPrevention.safeExecute("Low memory handling") {
            MemoryOptimizerStatic.emergencyCleanup()
            Log.w("App", "Low memory detected, emergency cleanup executed")
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        CrashPrevention.safeExecute("Memory trim handling") {
            when (level) {
                TRIM_MEMORY_RUNNING_CRITICAL,
                TRIM_MEMORY_COMPLETE,
                -> {
                    MemoryOptimizerStatic.emergencyCleanup()
                    Log.w("App", "Critical memory trim, emergency cleanup executed")
                }

                TRIM_MEMORY_RUNNING_LOW,
                TRIM_MEMORY_BACKGROUND,
                -> {
                    MemoryOptimizerStatic.clearImageCache()
                    Log.i("App", "Memory trim, image cache cleared")
                }

                else -> {
                    // Other memory trim levels, no specific action needed
                    Log.d("App", "Memory trim level: $level")
                }
            }
        }
    }
}
