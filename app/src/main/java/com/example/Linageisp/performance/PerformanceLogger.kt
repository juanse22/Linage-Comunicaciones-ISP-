package com.example.Linageisp.performance

import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CORRECCIÓN CRÍTICA: Sistema de logging y debugging de performance
 * Permite identificar problemas específicos en dispositivos como Vivo 2057
 */
@Singleton
class PerformanceLogger @Inject constructor() {
    
    private val _performanceEvents = MutableSharedFlow<PerformanceEvent>()
    val performanceEvents: SharedFlow<PerformanceEvent> = _performanceEvents.asSharedFlow()
    
    private val sessionMetrics = ConcurrentHashMap<String, MutableList<PerformanceMetric>>()
    private val deviceFingerprint = generateDeviceFingerprint()
    private val sessionId = UUID.randomUUID().toString()
    
    companion object {
        private const val TAG = "PerformanceLogger"
        private const val MAX_METRICS_PER_SESSION = 1000
        
        // Umbrales críticos de performance
        private const val CRITICAL_UI_FREEZE_MS = 100L
        private const val WARNING_UI_FREEZE_MS = 50L
        private const val CRITICAL_MEMORY_MB = 50
        private const val WARNING_MEMORY_MB = 30
    }
    
    data class PerformanceEvent(
        val timestamp: Long,
        val deviceFingerprint: String,
        val sessionId: String,
        val eventType: EventType,
        val component: String,
        val duration: Long?,
        val memoryUsage: Long?,
        val metadata: Map<String, Any>,
        val severity: Severity
    )
    
    data class PerformanceMetric(
        val timestamp: Long,
        val metricType: MetricType,
        val value: Long,
        val component: String,
        val metadata: Map<String, Any> = emptyMap()
    )
    
    enum class EventType {
        UI_FREEZE,
        SCROLL_LAG,
        KEYBOARD_OVERLAY_BUG,
        ANIMATION_DROP,
        MEMORY_PRESSURE,
        CHAT_MESSAGE_DELAY,
        AI_RESPONSE_DELAY,
        DEVICE_CAPABILITY_DETECTION
    }
    
    enum class MetricType {
        RENDER_TIME,
        MEMORY_USAGE,
        CPU_USAGE,
        NETWORK_LATENCY,
        USER_INTERACTION_DELAY
    }
    
    enum class Severity {
        INFO,
        WARNING,
        CRITICAL,
        EMERGENCY
    }
    
    private fun generateDeviceFingerprint(): String {
        return "${Build.MANUFACTURER}_${Build.MODEL}_${Build.VERSION.SDK_INT}"
            .replace(" ", "_")
            .replace("[^a-zA-Z0-9_]".toRegex(), "")
    }
    
    /**
     * Log crítico para problemas de UI como keyboard overlay
     */
    fun logKeyboardOverlayBug(details: Map<String, Any>) {
        val event = PerformanceEvent(
            timestamp = System.currentTimeMillis(),
            deviceFingerprint = deviceFingerprint,
            sessionId = sessionId,
            eventType = EventType.KEYBOARD_OVERLAY_BUG,
            component = "ChatScreen",
            duration = null,
            memoryUsage = null,
            metadata = details,
            severity = Severity.CRITICAL
        )
        
        Log.e(TAG, "🚨 KEYBOARD OVERLAY BUG DETECTED:")
        Log.e(TAG, "Device: $deviceFingerprint")
        Log.e(TAG, "Details: $details")
        
        emitEvent(event)
    }
    
    /**
     * Log para problemas de performance específicos de dispositivo
     */
    fun logDeviceSpecificIssue(deviceModel: String, issue: String, severity: Severity) {
        val event = PerformanceEvent(
            timestamp = System.currentTimeMillis(),
            deviceFingerprint = deviceFingerprint,
            sessionId = sessionId,
            eventType = EventType.UI_FREEZE,
            component = "Device_$deviceModel",
            duration = null,
            memoryUsage = null,
            metadata = mapOf(
                "issue" to issue,
                "deviceModel" to deviceModel,
                "isKnownIssue" to isKnownProblematicDevice(deviceModel)
            ),
            severity = severity
        )
        
        Log.w(TAG, "⚠️ DEVICE SPECIFIC ISSUE:")
        Log.w(TAG, "Device: $deviceModel")
        Log.w(TAG, "Issue: $issue")
        Log.w(TAG, "Severity: $severity")
        
        emitEvent(event)
    }
    
    /**
     * Medir tiempo de renderizado de componentes críticos
     */
    fun measureRenderTime(component: String, operation: String, startTime: Long, endTime: Long) {
        val duration = endTime - startTime
        val severity = when {
            duration > CRITICAL_UI_FREEZE_MS -> Severity.CRITICAL
            duration > WARNING_UI_FREEZE_MS -> Severity.WARNING
            else -> Severity.INFO
        }
        
        val event = PerformanceEvent(
            timestamp = System.currentTimeMillis(),
            deviceFingerprint = deviceFingerprint,
            sessionId = sessionId,
            eventType = EventType.UI_FREEZE,
            component = component,
            duration = duration,
            memoryUsage = null,
            metadata = mapOf(
                "operation" to operation,
                "renderTimeMs" to duration
            ),
            severity = severity
        )
        
        if (severity >= Severity.WARNING) {
            Log.w(TAG, "⏱️ SLOW RENDER: $component.$operation took ${duration}ms on $deviceFingerprint")
        }
        
        recordMetric(MetricType.RENDER_TIME, duration, component, mapOf("operation" to operation))
        emitEvent(event)
    }
    
    /**
     * Log específico para problemas de chat
     */
    fun logChatPerformanceIssue(issueType: String, details: Map<String, Any>) {
        val event = PerformanceEvent(
            timestamp = System.currentTimeMillis(),
            deviceFingerprint = deviceFingerprint,
            sessionId = sessionId,
            eventType = EventType.CHAT_MESSAGE_DELAY,
            component = "ChatScreen",
            duration = details["duration"] as? Long,
            memoryUsage = details["memoryMB"] as? Long,
            metadata = details + ("issueType" to issueType),
            severity = when (issueType) {
                "input_field_hidden" -> Severity.CRITICAL
                "keyboard_overlay" -> Severity.CRITICAL
                "slow_scroll" -> Severity.WARNING
                "animation_lag" -> Severity.WARNING
                else -> Severity.INFO
            }
        )
        
        Log.i(TAG, "💬 CHAT PERFORMANCE: $issueType on $deviceFingerprint")
        details.forEach { (key, value) ->
            Log.i(TAG, "  $key: $value")
        }
        
        emitEvent(event)
    }
    
    /**
     * Log de detección de capacidades de dispositivo
     */
    fun logDeviceCapabilityDetection(
        tier: String,
        ramGB: Int,
        cpuCores: Int,
        hasHardwareAccel: Boolean,
        optimizationsApplied: List<String>
    ) {
        val event = PerformanceEvent(
            timestamp = System.currentTimeMillis(),
            deviceFingerprint = deviceFingerprint,
            sessionId = sessionId,
            eventType = EventType.DEVICE_CAPABILITY_DETECTION,
            component = "DeviceCapabilityDetector",
            duration = null,
            memoryUsage = ramGB.toLong() * 1024,
            metadata = mapOf(
                "tier" to tier,
                "ramGB" to ramGB,
                "cpuCores" to cpuCores,
                "hasHardwareAcceleration" to hasHardwareAccel,
                "optimizationsApplied" to optimizationsApplied
            ),
            severity = Severity.INFO
        )
        
        Log.i(TAG, "🔍 DEVICE DETECTION:")
        Log.i(TAG, "Device: $deviceFingerprint")
        Log.i(TAG, "Tier: $tier")
        Log.i(TAG, "RAM: ${ramGB}GB")
        Log.i(TAG, "CPU Cores: $cpuCores")
        Log.i(TAG, "HW Accel: $hasHardwareAccel")
        Log.i(TAG, "Optimizations: $optimizationsApplied")
        
        emitEvent(event)
    }
    
    /**
     * Registrar métricas de memoria
     */
    fun recordMemoryUsage(component: String, memoryMB: Long) {
        val severity = when {
            memoryMB > CRITICAL_MEMORY_MB -> Severity.CRITICAL
            memoryMB > WARNING_MEMORY_MB -> Severity.WARNING
            else -> Severity.INFO
        }
        
        if (severity >= Severity.WARNING) {
            Log.w(TAG, "🧠 HIGH MEMORY: $component using ${memoryMB}MB on $deviceFingerprint")
        }
        
        recordMetric(MetricType.MEMORY_USAGE, memoryMB, component)
        
        if (severity >= Severity.WARNING) {
            val event = PerformanceEvent(
                timestamp = System.currentTimeMillis(),
                deviceFingerprint = deviceFingerprint,
                sessionId = sessionId,
                eventType = EventType.MEMORY_PRESSURE,
                component = component,
                duration = null,
                memoryUsage = memoryMB,
                metadata = mapOf("memoryMB" to memoryMB),
                severity = severity
            )
            
            emitEvent(event)
        }
    }
    
    private fun recordMetric(type: MetricType, value: Long, component: String, metadata: Map<String, Any> = emptyMap()) {
        val metric = PerformanceMetric(
            timestamp = System.currentTimeMillis(),
            metricType = type,
            value = value,
            component = component,
            metadata = metadata
        )
        
        sessionMetrics.getOrPut(component) { mutableListOf() }.apply {
            add(metric)
            // Limitar tamaño para evitar memory leaks
            if (size > MAX_METRICS_PER_SESSION) {
                removeAt(0)
            }
        }
    }
    
    private fun emitEvent(event: PerformanceEvent) {
        CoroutineScope(Dispatchers.Default).launch {
            _performanceEvents.emit(event)
        }
    }
    
    private fun isKnownProblematicDevice(deviceModel: String): Boolean {
        val problematicModels = setOf(
            "Vivo 2057",
            "SM-A205F",
            "Redmi 9A",
            "SM-A102U"
        )
        return problematicModels.contains(deviceModel)
    }
    
    /**
     * Generar reporte de performance para debugging
     */
    fun generatePerformanceReport(): PerformanceReport {
        val now = System.currentTimeMillis()
        val allMetrics = sessionMetrics.values.flatten()
        
        return PerformanceReport(
            sessionId = sessionId,
            deviceFingerprint = deviceFingerprint,
            generatedAt = now,
            totalMetrics = allMetrics.size,
            components = sessionMetrics.keys.toList(),
            criticalIssues = allMetrics.count { it.value > CRITICAL_UI_FREEZE_MS },
            warningIssues = allMetrics.count { it.value > WARNING_UI_FREEZE_MS },
            averageRenderTime = allMetrics
                .filter { it.metricType == MetricType.RENDER_TIME }
                .map { it.value }
                .average()
                .takeIf { !it.isNaN() } ?: 0.0,
            peakMemoryUsage = allMetrics
                .filter { it.metricType == MetricType.MEMORY_USAGE }
                .maxOfOrNull { it.value } ?: 0L,
            recommendations = generateRecommendations(allMetrics)
        )
    }
    
    private fun generateRecommendations(metrics: List<PerformanceMetric>): List<String> {
        val recommendations = mutableListOf<String>()
        
        val slowRenders = metrics.filter { 
            it.metricType == MetricType.RENDER_TIME && it.value > WARNING_UI_FREEZE_MS 
        }
        
        if (slowRenders.isNotEmpty()) {
            recommendations.add("Optimizar componentes con renderizado lento: ${slowRenders.map { it.component }.distinct()}")
        }
        
        val highMemory = metrics.filter { 
            it.metricType == MetricType.MEMORY_USAGE && it.value > WARNING_MEMORY_MB 
        }
        
        if (highMemory.isNotEmpty()) {
            recommendations.add("Reducir uso de memoria en: ${highMemory.map { it.component }.distinct()}")
        }
        
        if (deviceFingerprint.contains("VIVO")) {
            recommendations.add("Dispositivo VIVO detectado - aplicar optimizaciones específicas de ColorOS")
        }
        
        return recommendations
    }
    
    data class PerformanceReport(
        val sessionId: String,
        val deviceFingerprint: String,
        val generatedAt: Long,
        val totalMetrics: Int,
        val components: List<String>,
        val criticalIssues: Int,
        val warningIssues: Int,
        val averageRenderTime: Double,
        val peakMemoryUsage: Long,
        val recommendations: List<String>
    )
}

/**
 * Extensión para logging fácil de performance
 */
inline fun <T> PerformanceLogger.measureTime(
    component: String,
    operation: String,
    block: () -> T
): T {
    val startTime = System.currentTimeMillis()
    return try {
        block()
    } finally {
        val endTime = System.currentTimeMillis()
        measureRenderTime(component, operation, startTime, endTime)
    }
}