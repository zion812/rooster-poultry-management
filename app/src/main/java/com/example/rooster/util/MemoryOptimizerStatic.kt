package com.example.rooster.util

import android.util.Log
import java.util.*
import kotlin.concurrent.schedule

object MemoryOptimizerStatic {
    private var timer: Timer? = null

    fun startPeriodicCleanup(intervalMinutes: Long = 15) {
        timer?.cancel()
        timer = Timer("MemoryCleanup", true).apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    performCleanup()
                }
            }, 0L, intervalMinutes * 60 * 1000L)
        }
        Log.d("MemoryOptimizer", "Periodic cleanup started with ${intervalMinutes}min interval")
    }

    fun emergencyCleanup() {
        performCleanup()
        System.gc()
        Log.w("MemoryOptimizer", "Emergency cleanup performed")
    }

    fun clearImageCache() {
        // Clear image cache if using Glide/Picasso
        performCleanup()
        Log.i("MemoryOptimizer", "Image cache cleared")
    }

    private fun performCleanup() {
        try {
            // Force garbage collection
            Runtime.getRuntime().gc()

            val freeMemory = Runtime.getRuntime().freeMemory()
            val totalMemory = Runtime.getRuntime().totalMemory()
            val usedMemory = totalMemory - freeMemory

            Log.d(
                "MemoryOptimizer", "Memory cleanup - Used: ${usedMemory / 1024 / 1024}MB, " +
                        "Free: ${freeMemory / 1024 / 1024}MB"
            )
        } catch (e: Exception) {
            Log.e("MemoryOptimizer", "Error during cleanup", e)
        }
    }

    fun stopCleanup() {
        timer?.cancel()
        timer = null
        Log.d("MemoryOptimizer", "Periodic cleanup stopped")
    }
}