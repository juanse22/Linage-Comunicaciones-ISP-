package com.example.Linageisp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.Linageisp.performance.CrossPlatformOptimizer
import com.example.Linageisp.performance.PerformanceLogger
import com.example.Linageisp.performance.core.DeviceCapabilityDetector
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import kotlinx.coroutines.*

/**
 * CORRECCIÓN CRÍTICA: Integración simplificada de performance
 * Trabaja con los nuevos componentes CrossPlatformOptimizer y PerformanceLogger
 */
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
    }
    
    private lateinit var crossPlatformOptimizer: CrossPlatformOptimizer
    private lateinit var performanceLogger: PerformanceLogger
    private var firebasePerformance: FirebasePerformance? = null
    private var isInitialized = false
    private var appStartupTrace: Trace? = null
    
    fun initialize() {
        if (isInitialized) return
        
        try {
            firebasePerformance = FirebasePerformance.getInstance()
            isInitialized = true
            Log.d(TAG, "Performance Integration initialized successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Performance Integration", e)
        }
    }
    
    fun startAppStartupTrace(): Trace? {
        return try {
            firebasePerformance?.newTrace("app_startup")?.also {
                it.start()
                appStartupTrace = it
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting app startup trace", e)
            null
        }
    }
    
    fun completeAppStartup() {
        appStartupTrace?.let { trace ->
            try {
                trace.stop()
                appStartupTrace = null
                Log.d(TAG, "App startup trace completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error completing app startup trace", e)
            }
        }
    }
    
    fun traceScreenLoad(screenName: String): Trace? {
        return try {
            firebasePerformance?.newTrace("screen_load_$screenName")?.also {
                it.start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting screen load trace", e)
            null
        }
    }
    
    fun completeScreenLoad(trace: Trace?, loadTimeMs: Long) {
        trace?.let {
            try {
                it.putMetric("load_time_ms", loadTimeMs)
                it.stop()
            } catch (e: Exception) {
                Log.e(TAG, "Error completing screen load trace", e)
            }
        }
    }
    
    fun traceSpeedTest(): Trace? {
        return try {
            firebasePerformance?.newTrace("speed_test")?.also {
                it.start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speed test trace", e)
            null
        }
    }
    
    fun completeSpeedTest(trace: Trace?, downloadSpeed: Double, uploadSpeed: Double, 
                         latency: Long, testDurationMs: Long) {
        trace?.let {
            try {
                it.putMetric("download_mbps", (downloadSpeed * 100).toLong())
                it.putMetric("upload_mbps", (uploadSpeed * 100).toLong()) 
                it.putMetric("latency_ms", latency)
                it.putMetric("test_duration_ms", testDurationMs)
                it.stop()
            } catch (e: Exception) {
                Log.e(TAG, "Error completing speed test trace", e)
            }
        }
    }

    fun reportDeviceCapabilities(capabilities: DeviceCapabilityDetector.DeviceCapabilities) {
        try {
            val trace = firebasePerformance?.newTrace("device_capabilities")
            trace?.let { t ->
                t.putAttribute("device_tier", capabilities.tier.displayName)
                t.putAttribute("ram_gb", capabilities.ramGB.toString())
                t.putAttribute("cpu_cores", capabilities.cpuCores.toString())
                t.putAttribute("has_hw_accel", capabilities.hasHardwareAcceleration.toString())
                t.start()
                t.stop()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reporting device capabilities", e)
        }
    }
    
    fun cleanup() {
        appStartupTrace?.let {
            try {
                it.stop()
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping startup trace", e)
            }
            appStartupTrace = null
        }
        
        isInitialized = false
        INSTANCE = null
        Log.d(TAG, "Performance Integration cleaned up")
    }
}

// Composable helpers simplificados
@Composable
fun PerformanceIntegrationProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val integration = remember { PerformanceIntegration.getInstance(context) }
    
    LaunchedEffect(integration) {
        integration.initialize()
        delay(2000)
        integration.completeAppStartup()
    }
    
    content()
}

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