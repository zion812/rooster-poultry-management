package com.example.rooster.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.rooster.core.database.util.CommonTypeConverters
import com.example.rooster.core.database.util.OrderItemListConverter
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            // Add type converters that are registered with AppDatabase
            .addTypeConverters(CommonTypeConverters())
            .addTypeConverters(OrderItemListConverter())
            .build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun buildDatabase_createsInstance() {
        // Simple test to ensure the database can be built and is not null.
        // As AppDatabase has no DAOs or entities yet, there's not much more to test
        // at this level for AppDatabase itself.
        assertNotNull(db)

        // We can also check if it's open
        assertTrue(db.isOpen)

        // Since there are no DAOs, we can't test them here.
        // Actual functionality with DAOs and entities would be tested in feature modules
        // that define their own databases (which might or might not use this AppDatabase as a base).
    }

    // Future tests when AppDatabase might have entities or DAOs:
    // - Test insertion and retrieval if common entities are added.
    // - Test DAOs if common DAOs are added.
    // - True migration tests would require schema changes and version increments.
}
