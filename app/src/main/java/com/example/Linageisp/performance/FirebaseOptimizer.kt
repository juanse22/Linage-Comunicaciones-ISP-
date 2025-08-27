package com.example.Linageisp.performance

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.perf.performance
import com.google.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.ConcurrentHashMap

/**
 * EXTREMO RENDIMIENTO: Optimizador para Firebase
 * Reduce latencia, mejora batch processing y optimiza memoria
 */
class FirebaseOptimizer private constructor(private val context: Context) {
    
    companion object {
        @Volatile
        private var INSTANCE: FirebaseOptimizer? = null
        
        fun getInstance(context: Context): FirebaseOptimizer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirebaseOptimizer(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val optimizationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Batch analytics events to reduce Firebase calls
    private val analyticsEventQueue = mutableListOf<AnalyticsEvent>()
    private val maxBatchSize = 50
    private var lastBatchSent = System.currentTimeMillis()
    private val batchIntervalMs = 30000L // 30 seconds
    
    // Cache for remote config values
    private val remoteConfigCache = ConcurrentHashMap<String, Any>()
    private var remoteConfigLastFetch = 0L
    private val remoteConfigCacheDuration = 3600000L // 1 hour
    
    init {
        startBatchProcessing()
        initializeFirebaseOptimizations()
    }
    
    /**
     * OPTIMIZACIÓN 1: Analytics batching
     */
    fun logEventOptimized(eventName: String, parameters: Map<String, Any>? = null) {
        synchronized(analyticsEventQueue) {
            analyticsEventQueue.add(AnalyticsEvent(eventName, parameters ?: emptyMap()))
            
            if (analyticsEventQueue.size >= maxBatchSize || 
                System.currentTimeMillis() - lastBatchSent > batchIntervalMs) {
                flushAnalyticsBatch()
            }
        }
    }
    
    private fun flushAnalyticsBatch() {
        if (analyticsEventQueue.isEmpty()) return
        
        optimizationScope.launch {
            try {
                val batch = analyticsEventQueue.toList()
                analyticsEventQueue.clear()
                
                batch.forEach { event ->
                    Firebase.analytics.logEvent(event.name, null) // Simplified for batch processing
                }
                
                lastBatchSent = System.currentTimeMillis()
            } catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
            }
        }
    }
    
    /**
     * OPTIMIZACIÓN 2: Remote Config caching
     */
    fun getRemoteConfigOptimized(key: String, defaultValue: Any): Any {
        // Check cache first
        remoteConfigCache[key]?.let { return it }
        
        // Check if cache is still valid
        if (System.currentTimeMillis() - remoteConfigLastFetch < remoteConfigCacheDuration) {
            return defaultValue
        }
        
        return try {
            val remoteConfig = Firebase.remoteConfig
            val value = when (defaultValue) {
                is String -> remoteConfig.getString(key)
                is Long -> remoteConfig.getLong(key)
                is Boolean -> remoteConfig.getBoolean(key)
                is Double -> remoteConfig.getDouble(key)
                else -> defaultValue
            }
            
            // Cache the value
            remoteConfigCache[key] = value
            value
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            defaultValue
        }
    }
    
    /**
     * OPTIMIZACIÓN 3: Performance tracing con pool
     */
    private val tracePool = mutableListOf<com.google.firebase.perf.metrics.Trace>()
    private val maxPoolSize = 20
    
    fun startOptimizedTrace(traceName: String): OptimizedTrace? {
        return try {
            val trace = Firebase.performance.newTrace(traceName)
            trace.start()
            OptimizedTrace(trace) { returnTraceToPool(trace) }
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            null
        }
    }
    
    private fun returnTraceToPool(trace: com.google.firebase.perf.metrics.Trace) {
        if (tracePool.size < maxPoolSize) {
            tracePool.add(trace)
        }
    }
    
    /**
     * OPTIMIZACIÓN 4: Crashlytics optimizado
     */
    fun logExceptionOptimized(
        throwable: Throwable,
        additionalData: Map<String, String> = emptyMap()
    ) {
        optimizationScope.launch {
            try {
                Firebase.crashlytics.apply {
                    // Add custom keys in batch
                    additionalData.forEach { (key, value) ->
                        setCustomKey(key, value)
                    }
                    recordException(throwable)
                }
            } catch (e: Exception) {
                // Fallback: log to system if Firebase fails
                android.util.Log.e("FirebaseOptimizer", "Failed to log to Crashlytics", e)
            }
        }
    }
    
    /**
     * OPTIMIZACIÓN 5: Background initialization
     */
    private fun initializeFirebaseOptimizations() {
        optimizationScope.launch {
            try {
                // Pre-warm Firebase services
                delay(2000) // Wait for app to stabilize
                
                // Initialize Remote Config with cache
                val remoteConfig = Firebase.remoteConfig
                remoteConfig.fetchAndActivate().addOnCompleteListener {
                    remoteConfigLastFetch = System.currentTimeMillis()
                }
                
                // Pre-warm Crashlytics
                Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
                
                // Pre-warm Performance
                Firebase.performance.isPerformanceCollectionEnabled = true
                
            } catch (e: Exception) {
                android.util.Log.e("FirebaseOptimizer", "Failed to initialize optimizations", e)
            }
        }
    }
    
    /**
     * OPTIMIZACIÓN 6: Batch processing background job
     */
    private fun startBatchProcessing() {
        optimizationScope.launch {
            while (true) {
                delay(batchIntervalMs)
                try {
                    synchronized(analyticsEventQueue) {
                        if (analyticsEventQueue.isNotEmpty()) {
                            flushAnalyticsBatch()
                        }
                    }
                } catch (e: Exception) {
                    Firebase.crashlytics.recordException(e)
                }
            }
        }
    }
    
    /**
     * OPTIMIZACIÓN 7: Cleanup resources
     */
    fun cleanup() {
        synchronized(analyticsEventQueue) {
            if (analyticsEventQueue.isNotEmpty()) {
                flushAnalyticsBatch()
            }
        }
        remoteConfigCache.clear()
        tracePool.clear()
    }
    
    /**
     * OPTIMIZACIÓN 8: Network-aware operations
     */
    fun isNetworkOptimal(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as android.net.ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        
        return activeNetwork?.isConnected == true && 
               (activeNetwork.type == android.net.ConnectivityManager.TYPE_WIFI ||
                activeNetwork.subtype >= android.telephony.TelephonyManager.NETWORK_TYPE_LTE)
    }
    
    fun shouldDeferNonCriticalOperations(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
            as android.net.ConnectivityManager
        
        return !connectivityManager.isActiveNetworkMetered
    }
}

/**
 * Wrapper classes para optimización
 */
data class AnalyticsEvent(
    val name: String,
    val parameters: Map<String, Any>,
    val timestamp: Long = System.currentTimeMillis()
)

class OptimizedTrace(
    private val trace: com.google.firebase.perf.metrics.Trace,
    private val onComplete: () -> Unit
) {
    fun putAttribute(attribute: String, value: String) {
        trace.putAttribute(attribute, value)
    }
    
    fun putMetric(metricName: String, value: Long) {
        trace.putMetric(metricName, value)
    }
    
    fun incrementMetric(metricName: String, incrementBy: Long) {
        trace.incrementMetric(metricName, incrementBy)
    }
    
    fun stop() {
        try {
            trace.stop()
            onComplete()
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
        }
    }
}

/**
 * Extension functions para facilidad de uso
 */
fun Context.optimizedFirebase(): FirebaseOptimizer = FirebaseOptimizer.getInstance(this)

inline fun FirebaseOptimizer.traceOptimized(
    traceName: String, 
    crossinline operation: () -> Unit
) {
    val trace = startOptimizedTrace(traceName)
    try {
        operation()
    } finally {
        trace?.stop()
    }
}

/**
 * Utilidades para logging optimizado
 */
object OptimizedLogging {
    fun logPerformanceMetric(
        optimizer: FirebaseOptimizer,
        metricName: String,
        value: Long,
        additionalData: Map<String, String> = emptyMap()
    ) {
        optimizer.logEventOptimized("performance_metric", mapOf(
            "metric_name" to metricName,
            "value" to value
        ) + additionalData)
    }
    
    fun logUserAction(
        optimizer: FirebaseOptimizer,
        action: String,
        screen: String,
        additionalData: Map<String, Any> = emptyMap()
    ) {
        optimizer.logEventOptimized("user_action", mapOf(
            "action" to action,
            "screen" to screen,
            "timestamp" to System.currentTimeMillis()
        ) + additionalData)
    }
}