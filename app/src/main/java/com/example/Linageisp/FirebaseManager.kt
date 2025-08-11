package com.example.Linageisp

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FirebaseManager {
    private var analytics: FirebaseAnalytics? = null
    private var crashlytics: FirebaseCrashlytics? = null
    private var performance: FirebasePerformance? = null
    
    fun initialize(context: Context) {
        try {
            analytics = FirebaseAnalytics.getInstance(context)
            crashlytics = FirebaseCrashlytics.getInstance()
            performance = FirebasePerformance.getInstance()
            
            Log.d("FirebaseManager", "Firebase services initialized successfully")
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error initializing Firebase services", e)
        }
    }
    
    // Analytics Functions
    fun logEvent(eventName: String, parameters: Bundle? = null) {
        try {
            analytics?.logEvent(eventName, parameters)
            Log.d("FirebaseAnalytics", "Event logged: $eventName")
        } catch (e: Exception) {
            Log.e("FirebaseAnalytics", "Error logging event: $eventName", e)
            recordException(e)
        }
    }
    
    fun logScreenView(screenName: String, screenClass: String? = null) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            screenClass?.let { putString(FirebaseAnalytics.Param.SCREEN_CLASS, it) }
        }
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
    
    fun logSpeedTestEvent(downloadSpeed: Double, uploadSpeed: Double, latency: Long) {
        val bundle = Bundle().apply {
            putDouble("download_speed_mbps", downloadSpeed)
            putDouble("upload_speed_mbps", uploadSpeed)
            putLong("latency_ms", latency)
        }
        logEvent("speed_test_completed", bundle)
    }
    
    fun logPlanViewEvent(planName: String, planPrice: String, planSpeed: String) {
        val bundle = Bundle().apply {
            putString("plan_name", planName)
            putString("plan_price", planPrice)
            putString("plan_speed", planSpeed)
        }
        logEvent("plan_viewed", bundle)
    }
    
    // Crashlytics Functions
    fun recordException(exception: Throwable) {
        try {
            crashlytics?.recordException(exception)
            Log.d("FirebaseCrashlytics", "Exception recorded: ${exception.message}")
        } catch (e: Exception) {
            Log.e("FirebaseCrashlytics", "Error recording exception", e)
        }
    }
    
    fun setUserProperty(key: String, value: String) {
        try {
            crashlytics?.setCustomKey(key, value)
            analytics?.setUserProperty(key, value)
        } catch (e: Exception) {
            Log.e("Firebase", "Error setting user property", e)
        }
    }
    
    fun setUserId(userId: String) {
        try {
            crashlytics?.setUserId(userId)
            analytics?.setUserId(userId)
        } catch (e: Exception) {
            Log.e("Firebase", "Error setting user ID", e)
        }
    }
    
    // Performance Functions
    fun startTrace(traceName: String): Trace? {
        return try {
            val trace = performance?.newTrace(traceName)
            trace?.start()
            Log.d("FirebasePerformance", "Trace started: $traceName")
            trace
        } catch (e: Exception) {
            Log.e("FirebasePerformance", "Error starting trace: $traceName", e)
            recordException(e)
            null
        }
    }
    
    fun stopTrace(trace: Trace?) {
        try {
            trace?.stop()
            Log.d("FirebasePerformance", "Trace stopped")
        } catch (e: Exception) {
            Log.e("FirebasePerformance", "Error stopping trace", e)
            recordException(e)
        }
    }
    
    fun addTraceAttribute(trace: Trace?, key: String, value: String) {
        try {
            trace?.putAttribute(key, value)
        } catch (e: Exception) {
            Log.e("FirebasePerformance", "Error adding trace attribute", e)
        }
    }
    
    fun incrementTraceMetric(trace: Trace?, metricName: String, value: Long = 1) {
        try {
            trace?.incrementMetric(metricName, value)
        } catch (e: Exception) {
            Log.e("FirebasePerformance", "Error incrementing trace metric", e)
        }
    }
    
    // Custom Performance Traces for Linage ISP
    fun traceScreenLoad(screenName: String, loadTimeMs: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val trace = startTrace("screen_load_$screenName")
            trace?.let {
                addTraceAttribute(it, "screen_name", screenName)
                incrementTraceMetric(it, "load_time_ms", loadTimeMs)
                stopTrace(it)
            }
        }
    }
    
    fun traceSpeedTest(downloadSpeed: Double, uploadSpeed: Double, testDurationMs: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val trace = startTrace("speed_test_execution")
            trace?.let {
                addTraceAttribute(it, "test_type", "full_speed_test")
                incrementTraceMetric(it, "download_mbps", (downloadSpeed * 100).toLong())
                incrementTraceMetric(it, "upload_mbps", (uploadSpeed * 100).toLong())
                incrementTraceMetric(it, "test_duration_ms", testDurationMs)
                stopTrace(it)
            }
        }
    }
    
    fun tracePlanScraping(planCount: Int, scrapingTimeMs: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val trace = startTrace("plan_scraping")
            trace?.let {
                incrementTraceMetric(it, "plans_scraped", planCount.toLong())
                incrementTraceMetric(it, "scraping_duration_ms", scrapingTimeMs)
                stopTrace(it)
            }
        }
    }
}