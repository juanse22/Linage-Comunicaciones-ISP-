package com.example.Linageisp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.Linageisp.performance.PerformanceSystem
import com.example.Linageisp.performance.core.DeviceCapabilityDetector
import com.example.Linageisp.performance.core.GPUMetrics
import com.example.Linageisp.performance.PerformanceMonitor
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.AddTrace
import com.google.firebase.perf.metrics.Trace
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PerformanceIntegration private constructor(
    private val context: Context
) {
    
    companion object {
        @Volatile
        private var INSTANCE: PerformanceIntegration? = null
        
        fun getInstance(context: Context): PerformanceIntegration {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PerformanceIntegration(context.applicationContext).also { INSTANCE = it }
            }
        }
        
        private const val TAG = "PerformanceIntegration"
        
        // Custom metric names
        private const val TRACE_SCREEN_LOAD = "screen_load_time"
        private const val TRACE_SPEED_TEST = "speed_test_execution"
        private const val TRACE_PLAN_SCRAPING = "plan_scraping_time"
        private const val TRACE_APP_STARTUP = "app_startup_time"
        private const val TRACE_MEMORY_CLEANUP = "memory_cleanup_time"
        private const val TRACE_FPS_MONITORING = "fps_monitoring_session"
        
        // Custom metric keys
        private const val METRIC_DEVICE_TIER = "device_tier"
        private const val METRIC_RAM_GB = "ram_gb"
        private const val METRIC_CPU_CORES = "cpu_cores"
        private const val METRIC_THERMAL_STATE = "thermal_state"
        private const val METRIC_MEMORY_USAGE = "memory_usage_percent"
        private const val METRIC_CACHE_HIT_RATE = "cache_hit_rate"
        private const val METRIC_SYSTEM_HEALTH = "system_health"
        private const val METRIC_PERFORMANCE_MODE = "performance_mode"
    }
    
    private var performanceSystem: PerformanceSystem? = null
    private var firebasePerformance: FirebasePerformance? = null
    private val integrationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isInitialized = false
    private var appStartupTrace: Trace? = null
    
    // Active traces tracking
    private val activeTraces = mutableMapOf<String, Trace>()
    
    fun initialize() {
        if (isInitialized) return
        
        try {
            performanceSystem = PerformanceSystem.getInstance(context)
            firebasePerformance = FirebasePerformance.getInstance()
            
            // Initialize Firebase Manager
            FirebaseManager.initialize(context)
            
            // Start app startup trace
            startAppStartupTrace()
            
            // Start system monitoring integration
            startSystemMonitoringIntegration()
            
            isInitialized = true
            Log.d(TAG, "Performance Integration initialized successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Performance Integration", e)
            FirebaseManager.recordException(e)
        }
    }
    
    private fun startAppStartupTrace() {
        appStartupTrace = FirebaseManager.startTrace(TRACE_APP_STARTUP)
        Log.d(TAG, "App startup trace started")
    }
    
    fun completeAppStartup() {
        appStartupTrace?.let { trace ->
            performanceSystem?.let { ps ->
                val summary = ps.getSystemSummary()
                
                // Add device and system attributes
                FirebaseManager.addTraceAttribute(trace, METRIC_DEVICE_TIER, 
                    summary.health.name)
                FirebaseManager.addTraceAttribute(trace, METRIC_SYSTEM_HEALTH, 
                    summary.health.name)
                FirebaseManager.addTraceAttribute(trace, METRIC_PERFORMANCE_MODE, 
                    summary.performanceMode.name)
                
                // Add performance metrics
                FirebaseManager.incrementTraceMetric(trace, "startup_fps", 
                    (summary.averageFps * 100).toLong())
                FirebaseManager.incrementTraceMetric(trace, "startup_memory_usage", 
                    summary.memoryUsagePercent.toLong())
            }
            
            FirebaseManager.stopTrace(trace)
            appStartupTrace = null
            Log.d(TAG, "App startup trace completed")
        }
    }
    
    private fun startSystemMonitoringIntegration() {
        performanceSystem?.let { ps ->
            integrationScope.launch {
                // Monitor system health changes and send to Firebase
                ps.systemHealth.collect { health ->
                    FirebaseManager.logEvent("system_health_changed", Bundle().apply {
                        putString("health_state", health.name)
                        putLong("timestamp", System.currentTimeMillis())
                    })
                }
            }
            
            integrationScope.launch {
                // Monitor performance mode changes
                ps.performanceMode.collect { mode ->
                    FirebaseManager.logEvent("performance_mode_changed", Bundle().apply {
                        putString("performance_mode", mode.name)
                        putLong("timestamp", System.currentTimeMillis())
                    })
                }
            }
            
            integrationScope.launch {
                // Monitor recommendations
                ps.recommendations.collect { recommendations ->
                    if (recommendations.isNotEmpty()) {
                        FirebaseManager.logEvent("performance_recommendations", Bundle().apply {
                            putInt("recommendation_count", recommendations.size)
                            putString("top_recommendation", recommendations.first().id)
                            putString("top_impact", recommendations.first().impact.name)
                        })
                    }
                }
            }
            
            // Start periodic system metrics reporting
            startPeriodicMetricsReporting()
        }
    }
    
    private fun startPeriodicMetricsReporting() {
        integrationScope.launch {
            while (isActive) {
                try {
                    delay(60000) // Report every minute
                    reportSystemMetrics()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in periodic metrics reporting", e)
                    FirebaseManager.recordException(e)
                    delay(120000) // Wait longer on error
                }
            }
        }
    }
    
    private suspend fun reportSystemMetrics() {
        performanceSystem?.let { ps ->
            val summary = ps.getSystemSummary()
            
            // Create a comprehensive system metrics trace
            val trace = FirebaseManager.startTrace("system_metrics_snapshot")
            trace?.let { t ->
                // Add all system attributes
                FirebaseManager.addTraceAttribute(t, METRIC_SYSTEM_HEALTH, summary.health.name)
                FirebaseManager.addTraceAttribute(t, METRIC_PERFORMANCE_MODE, summary.performanceMode.name)
                FirebaseManager.addTraceAttribute(t, METRIC_THERMAL_STATE, summary.thermalState.name)
                
                // Add metrics
                FirebaseManager.incrementTraceMetric(t, "fps_x100", (summary.averageFps * 100).toLong())
                FirebaseManager.incrementTraceMetric(t, "memory_usage_percent", summary.memoryUsagePercent.toLong())
                FirebaseManager.incrementTraceMetric(t, "cache_hit_rate_x100", (summary.cacheHitRate * 100).toLong())
                FirebaseManager.incrementTraceMetric(t, "active_recommendations", summary.totalRecommendations.toLong())
                
                FirebaseManager.stopTrace(t)
            }
        }
    }
    
    // Screen Performance Tracking
    @AddTrace(name = "screen_load_time")
    fun traceScreenLoad(screenName: String): Trace? {
        val trace = FirebaseManager.startTrace("${TRACE_SCREEN_LOAD}_$screenName")
        trace?.let {
            FirebaseManager.addTraceAttribute(it, "screen_name", screenName)
            performanceSystem?.let { ps ->
                val summary = ps.getSystemSummary()
                FirebaseManager.addTraceAttribute(it, METRIC_SYSTEM_HEALTH, summary.health.name)
            }
        }
        return trace
    }
    
    fun completeScreenLoad(trace: Trace?, loadTimeMs: Long) {
        trace?.let {
            FirebaseManager.incrementTraceMetric(it, "load_time_ms", loadTimeMs)
            FirebaseManager.stopTrace(it)
        }
    }
    
    // Speed Test Integration
    fun traceSpeedTest(): Trace? {
        val trace = FirebaseManager.startTrace(TRACE_SPEED_TEST)
        trace?.let {
            performanceSystem?.let { ps ->
                val summary = ps.getSystemSummary()
                FirebaseManager.addTraceAttribute(it, METRIC_SYSTEM_HEALTH, summary.health.name)
                FirebaseManager.addTraceAttribute(it, METRIC_THERMAL_STATE, summary.thermalState.name)
                FirebaseManager.addTraceAttribute(it, "test_environment", "linage_isp")
            }
        }
        return trace
    }
    
    fun completeSpeedTest(trace: Trace?, downloadSpeed: Double, uploadSpeed: Double, 
                         latency: Long, testDurationMs: Long) {
        trace?.let {
            FirebaseManager.addTraceAttribute(it, "speed_classification", 
                classifySpeed(downloadSpeed, uploadSpeed))
            
            FirebaseManager.incrementTraceMetric(it, "download_mbps_x100", (downloadSpeed * 100).toLong())
            FirebaseManager.incrementTraceMetric(it, "upload_mbps_x100", (uploadSpeed * 100).toLong())
            FirebaseManager.incrementTraceMetric(it, "latency_ms", latency)
            FirebaseManager.incrementTraceMetric(it, "test_duration_ms", testDurationMs)
            
            FirebaseManager.stopTrace(it)
        }
        
        // Also log as an analytics event
        FirebaseManager.logSpeedTestEvent(downloadSpeed, uploadSpeed, latency)
    }
    
    private fun classifySpeed(download: Double, upload: Double): String {
        return when {
            download >= 100 && upload >= 50 -> "ultra_fast"
            download >= 50 && upload >= 25 -> "fast"
            download >= 25 && upload >= 10 -> "moderate"
            download >= 10 && upload >= 5 -> "slow"
            else -> "very_slow"
        }
    }
    
    // Plan Scraping Integration
    fun tracePlanScraping(): Trace? {
        return FirebaseManager.startTrace(TRACE_PLAN_SCRAPING)
    }
    
    fun completePlanScraping(trace: Trace?, planCount: Int, scrapingTimeMs: Long, 
                           success: Boolean) {
        trace?.let {
            FirebaseManager.addTraceAttribute(it, "scraping_result", 
                if (success) "success" else "failure")
            FirebaseManager.incrementTraceMetric(it, "plans_scraped", planCount.toLong())
            FirebaseManager.incrementTraceMetric(it, "scraping_duration_ms", scrapingTimeMs)
            
            FirebaseManager.stopTrace(it)
        }
    }
    
    // Memory Management Integration
    fun traceMemoryCleanup(): Trace? {
        return FirebaseManager.startTrace(TRACE_MEMORY_CLEANUP)
    }
    
    fun completeMemoryCleanup(trace: Trace?, memoryFreedMB: Long, cleanupTimeMs: Long) {
        trace?.let {
            FirebaseManager.incrementTraceMetric(it, "memory_freed_mb", memoryFreedMB)
            FirebaseManager.incrementTraceMetric(it, "cleanup_duration_ms", cleanupTimeMs)
            
            performanceSystem?.let { ps ->
                val summary = ps.getSystemSummary()
                FirebaseManager.addTraceAttribute(it, "memory_usage_after", 
                    "${summary.memoryUsagePercent.toInt()}%")
            }
            
            FirebaseManager.stopTrace(it)
        }
    }
    
    // FPS Monitoring Integration
    fun startFPSMonitoringSession(): Trace? {
        val trace = FirebaseManager.startTrace(TRACE_FPS_MONITORING)
        trace?.let {
            performanceSystem?.let { ps ->
                val summary = ps.getSystemSummary()
                FirebaseManager.addTraceAttribute(it, METRIC_SYSTEM_HEALTH, summary.health.name)
                FirebaseManager.addTraceAttribute(it, METRIC_THERMAL_STATE, summary.thermalState.name)
            }
        }
        return trace
    }
    
    fun completeFPSMonitoringSession(trace: Trace?, averageFps: Float, minFps: Float, 
                                   maxFps: Float, jankPercentage: Float, sessionDurationMs: Long) {
        trace?.let {
            FirebaseManager.incrementTraceMetric(it, "avg_fps_x100", (averageFps * 100).toLong())
            FirebaseManager.incrementTraceMetric(it, "min_fps_x100", (minFps * 100).toLong())
            FirebaseManager.incrementTraceMetric(it, "max_fps_x100", (maxFps * 100).toLong())
            FirebaseManager.incrementTraceMetric(it, "jank_percent_x100", (jankPercentage * 100).toLong())
            FirebaseManager.incrementTraceMetric(it, "session_duration_ms", sessionDurationMs)
            
            // Classify performance
            val performanceClass = when {
                averageFps >= 55f && jankPercentage <= 5f -> "excellent"
                averageFps >= 45f && jankPercentage <= 10f -> "good"
                averageFps >= 30f && jankPercentage <= 20f -> "acceptable"
                else -> "poor"
            }
            FirebaseManager.addTraceAttribute(it, "performance_class", performanceClass)
            
            FirebaseManager.stopTrace(it)
        }
    }
    
    // Device Capability Integration
    fun reportDeviceCapabilities(capabilities: DeviceCapabilityDetector.DeviceCapabilities) {
        FirebaseManager.setUserProperty("device_tier", capabilities.tier.name)
        FirebaseManager.setUserProperty("device_ram_gb", capabilities.ramGB.toString())
        FirebaseManager.setUserProperty("device_cpu_cores", capabilities.cpuCores.toString())
        
        FirebaseManager.logEvent("device_capabilities_detected", Bundle().apply {
            putString("device_tier", capabilities.tier.name)
            putInt("ram_gb", capabilities.ramGB)
            putInt("cpu_cores", capabilities.cpuCores)
            putInt("api_level", capabilities.apiLevel)
            putBoolean("hardware_acceleration", capabilities.hasHardwareAcceleration)
        })
    }
    
    // Error and Exception Integration
    fun reportPerformanceException(exception: Throwable, context: String) {
        FirebaseManager.recordException(exception)
        
        FirebaseManager.logEvent("performance_exception", Bundle().apply {
            putString("exception_context", context)
            putString("exception_type", exception.javaClass.simpleName)
            putString("exception_message", exception.message ?: "Unknown")
            performanceSystem?.let { ps ->
                val summary = ps.getSystemSummary()
                putString("system_health", summary.health.name)
                putFloat("memory_usage", summary.memoryUsagePercent)
            }
        })
    }
    
    // Custom Metrics for Specific Use Cases
    fun reportCustomPerformanceMetric(metricName: String, value: Long, attributes: Map<String, String> = emptyMap()) {
        val trace = FirebaseManager.startTrace("custom_metric_$metricName")
        trace?.let {
            attributes.forEach { (key, attr_value) ->
                FirebaseManager.addTraceAttribute(it, key, attr_value)
            }
            FirebaseManager.incrementTraceMetric(it, metricName, value)
            FirebaseManager.stopTrace(it)
        }
    }
    
    fun cleanup() {
        integrationScope.cancel()
        activeTraces.values.forEach { trace ->
            FirebaseManager.stopTrace(trace)
        }
        activeTraces.clear()
        
        appStartupTrace?.let {
            FirebaseManager.stopTrace(it)
            appStartupTrace = null
        }
        
        isInitialized = false
        INSTANCE = null
        
        Log.d(TAG, "Performance Integration cleaned up")
    }
}

// Composable Integration Helper
@Composable
fun PerformanceIntegrationProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val integration = remember { PerformanceIntegration.getInstance(context) }
    
    LaunchedEffect(integration) {
        integration.initialize()
        
        // Complete app startup after a short delay to ensure everything is loaded
        delay(2000)
        integration.completeAppStartup()
    }
    
    DisposableEffect(integration) {
        onDispose {
            // Don't cleanup here as it might be used elsewhere
        }
    }
    
    content()
}

// Helper functions for easy integration
@Composable
fun rememberScreenLoadTrace(screenName: String): Trace? {
    val context = LocalContext.current
    val integration = remember { PerformanceIntegration.getInstance(context) }
    
    return remember(screenName) {
        integration.traceScreenLoad(screenName)
    }
}

@Composable
fun TraceScreenLoad(
    screenName: String,
    content: @Composable () -> Unit
) {
    val startTime = remember { System.currentTimeMillis() }
    val trace = rememberScreenLoadTrace(screenName)
    
    val context = LocalContext.current
    val integration = remember { PerformanceIntegration.getInstance(context) }
    
    DisposableEffect(trace) {
        onDispose {
            val loadTime = System.currentTimeMillis() - startTime
            integration.completeScreenLoad(trace, loadTime)
        }
    }
    
    content()
}