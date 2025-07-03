package com.example.rooster

import android.app.Application
import android.content.ComponentCallbacks2
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory // Import HiltWorkerFactory
import androidx.room.Room
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.rooster.config.Constants
import com.example.rooster.core.common.model.BroadcastEventParse
import com.example.rooster.core.common.model.CertificationRequestParse
import com.example.rooster.core.common.model.ChatParse
import com.example.rooster.core.common.model.CommunityGroupParse
import com.example.rooster.core.common.model.EventItemParse
import com.example.rooster.core.common.model.MarketplaceListingParse
import com.example.rooster.core.common.model.MessageParse
import com.example.rooster.core.common.model.SuggestionItemParse
import com.example.rooster.core.common.model.TraceabilityEventParse
import com.example.rooster.core.common.model.VaccinationTemplateParse
import com.example.rooster.data.sync.DataSyncWorker
import com.example.rooster.feature.community.worker.CommunitySyncWorker
import com.example.rooster.feature.farm.worker.FarmDataSyncWorker
import com.example.rooster.feature.marketplace.worker.MarketplaceSyncWorker
import com.example.rooster.util.CrashPrevention
import com.example.rooster.util.MemoryOptimizerStatic
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.Parse
import com.parse.ParseACL
import com.parse.ParseInstallation
import com.parse.ParseObject
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // For release builds, you might want to plant a different tree
            // that doesn't log sensitive information
            Timber.plant(
                object : Timber.Tree() {
                    override fun log(
                        priority: Int,
                        tag: String?,
                        message: String,
                        t: Throwable?,
                    ) {
                        if (priority >= Log.WARN) {
                            // Only log warnings and errors in release builds
                            FirebaseCrashlytics.getInstance().log("$tag: $message")
                            t?.let { FirebaseCrashlytics.getInstance().recordException(it) }
                        }
                    }
                },
            )
        }

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

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        // Initialize Parse with custom classes
        initializeParse()

        // Initialize photo upload database with crash protection
        CrashPrevention.safeExecute("PhotoUpload database initialization") {
            val photoUploadDatabase =
                Room.databaseBuilder(
                    applicationContext,
                    PhotoUploadDatabase::class.java,
                    "rooster_photo_uploads.db",
                ).fallbackToDestructiveMigration().build()
            Log.d("App", "Photo upload database initialized")
        }

        // Schedule background workers for data sync
        CrashPrevention.safeExecute("Background workers scheduling") {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            // Schedule farm data sync
            val farmSyncRequest = PeriodicWorkRequestBuilder<FarmDataSyncWorker>(
                6, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                FarmDataSyncWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                farmSyncRequest
            )

            // Schedule marketplace sync
            val marketplaceSyncRequest = PeriodicWorkRequestBuilder<MarketplaceSyncWorker>(
                4, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                MarketplaceSyncWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                marketplaceSyncRequest
            )

            // Schedule community sync
            val communitySyncRequest = PeriodicWorkRequestBuilder<CommunitySyncWorker>(
                3, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                CommunitySyncWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                communitySyncRequest
            )

            // Schedule data sync
            val syncRequest =
                PeriodicWorkRequestBuilder<DataSyncWorker>(15, TimeUnit.MINUTES)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                    .build()
            WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                    "data_sync",
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest,
                )
            Log.d("App", "Background data sync worker scheduled")

            Log.d("App", "Background workers scheduled")
        }

        Log.d("RoosterApp", "=== APP INITIALIZATION COMPLETED SUCCESSFULLY ===")
    }

    private fun initializeParse() {
        // Register Parse subclasses before Parse.initialize()
        ParseObject.registerSubclass(CertificationRequestParse::class.java)
        ParseObject.registerSubclass(EventItemParse::class.java)
        ParseObject.registerSubclass(VaccinationTemplateParse::class.java)
        ParseObject.registerSubclass(BroadcastEventParse::class.java)
        ParseObject.registerSubclass(SuggestionItemParse::class.java)
        ParseObject.registerSubclass(TraceabilityEventParse::class.java)
        ParseObject.registerSubclass(ChatParse::class.java)
        ParseObject.registerSubclass(MessageParse::class.java)
        ParseObject.registerSubclass(CommunityGroupParse::class.java)
        ParseObject.registerSubclass(MarketplaceListingParse::class.java)

        // Initialize Parse
        Parse.setLogLevel(if (BuildConfig.DEBUG) Parse.LOG_LEVEL_DEBUG else Parse.LOG_LEVEL_ERROR)

        val configuration =
            Parse.Configuration.Builder(this)
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
