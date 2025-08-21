package com.example.Linageisp.performance

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
// import androidx.metrics.performance.JankStats // REMOVIDO POR ERROR 16KB
import com.example.Linageisp.performance.core.DeviceCapabilityDetector
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.roundToInt

/**
 * Monitor integral de rendimiento con métricas de FPS, jank detection y análisis de performance
 * Integra AndroidX Metrics API con métricas personalizadas
 */
class PerformanceMonitor private constructor(
    private val context: Context,
    private val deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities,
    private val vSyncManager: VSyncManager
) {
    
    companion object {
        @Volatile
        private var INSTANCE: PerformanceMonitor? = null
        
        fun getInstance(
            context: Context,
            deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities,
            vSyncManager: VSyncManager
        ): PerformanceMonitor {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PerformanceMonitor(
                    context.applicationContext,
                    deviceCapabilities,
                    vSyncManager
                ).also { INSTANCE = it }
            }
        }
        
        // Configuración de umbrales por dispositivo
        private fun getPerformanceThresholds(tier: DeviceCapabilityDetector.PerformanceTier) = when (tier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> PerformanceThresholds(
                targetFps = 30f,
                acceptableJankRate = 15f,
                criticalJankRate = 25f,
                frameDeadlineMs = 33.33f // 30 FPS
            )
            DeviceCapabilityDetector.PerformanceTier.MID_END -> PerformanceThresholds(
                targetFps = 45f,
                acceptableJankRate = 10f,
                criticalJankRate = 20f,
                frameDeadlineMs = 22.22f // 45 FPS
            )
            DeviceCapabilityDetector.PerformanceTier.HIGH_END -> PerformanceThresholds(
                targetFps = 60f,
                acceptableJankRate = 5f,
                criticalJankRate = 12f,
                frameDeadlineMs = 16.67f // 60 FPS
            )
            DeviceCapabilityDetector.PerformanceTier.PREMIUM -> PerformanceThresholds(
                targetFps = 90f,
                acceptableJankRate = 3f,
                criticalJankRate = 8f,
                frameDeadlineMs = 11.11f // 90 FPS
            )
        }
    }
    
    data class PerformanceThresholds(
        val targetFps: Float = 60f,
        val acceptableJankRate: Float = 5f,
        val criticalJankRate: Float = 15f,
        val frameDeadlineMs: Float = 16.67f
    )
    
    data class FrameMetrics(
        val fps: Float,
        val frameTime: Float,
        val jankCount: Int,
        val jankRate: Float,
        val isSmooth: Boolean,
        val performanceLevel: PerformanceLevel,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    data class PerformanceReport(
        val averageFps: Float,
        val minFps: Float,
        val maxFps: Float,
        val jankPercentage: Float,
        val totalJanks: Int,
        val smoothFramePercentage: Float,
        val overallPerformance: PerformanceLevel,
        val recommendations: List<String>,
        val sessionDurationMs: Long,
        val totalFrames: Int
    )
    
    enum class PerformanceLevel {
        EXCELLENT,  // > 85% del target FPS, < 3% jank
        GOOD,       // > 70% del target FPS, < 5% jank  
        FAIR,       // > 50% del target FPS, < 15% jank
        POOR,       // > 30% del target FPS, < 25% jank
        CRITICAL    // < 30% del target FPS, > 25% jank
    }
    
    private val thresholds = getPerformanceThresholds(deviceCapabilities.tier)
    private val monitoringScope = CoroutineScope(
        SupervisorJob() + 
        Dispatchers.Default + 
        CoroutineName("PerformanceMonitor")
    )
    
    // Contadores y métricas
    private val frameCount = AtomicLong(0)
    private val jankCount = AtomicLong(0)
    private val sessionStartTime = System.currentTimeMillis()
    private val fpsHistory = mutableListOf<Float>()
    private val frameTimeHistory = mutableListOf<Float>()
    
    // JankStats para Android 12+ (REMOVIDO POR ERROR 16KB)
    // private var jankStats: JankStats? = null
    
    // Flows para métricas en tiempo real
    private val _frameMetrics = MutableSharedFlow<FrameMetrics>(
        replay = 1,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val frameMetrics: SharedFlow<FrameMetrics> = _frameMetrics.asSharedFlow()
    
    private val _performanceAlerts = MutableSharedFlow<PerformanceAlert>(
        replay = 0,
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val performanceAlerts: SharedFlow<PerformanceAlert> = _performanceAlerts.asSharedFlow()
    
    data class PerformanceAlert(
        val level: AlertLevel,
        val message: String,
        val metric: String,
        val value: Float,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    enum class AlertLevel {
        INFO, WARNING, CRITICAL
    }
    
    init {
        initializeMonitoring()
    }
    
    /**
     * Inicializa el monitoreo de rendimiento
     */
    private fun initializeMonitoring() {
        // Inicializar JankStats en Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            initializeJankStats()
        }
        
        // Monitorear VSync frames
        monitoringScope.launch {
            vSyncManager.frameEvents.collect { frameInfo ->
                processFrameInfo(frameInfo)
            }
        }
        
        // Generar reportes periódicos
        monitoringScope.launch {
            while (isActive) {
                delay(5000) // Cada 5 segundos
                generatePerformanceMetrics()
            }
        }
    }
    
    /**
     * Inicializa JankStats para Android 12+
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun initializeJankStats() {
        try {
            // JankStats se inicializará con el contexto disponible
            // Se simplifica para evitar problemas de compatibilidad
            // Las métricas se obtendrán principalmente del VSyncManager
        } catch (e: Exception) {
            // JankStats no disponible, continuar con métricas básicas
        }
    }
    
    /**
     * Procesa información de frames del VSync
     */
    private suspend fun processFrameInfo(frameInfo: VSyncManager.FrameInfo) = withContext(Dispatchers.Default) {
        val currentFrameCount = frameCount.incrementAndGet()
        val fps = frameInfo.actualFps
        val frameTimeMs = frameInfo.frameDurationNanos / 1_000_000f
        
        // Detectar jank
        val isJank = frameTimeMs > thresholds.frameDeadlineMs * 1.5f
        if (isJank) {
            jankCount.incrementAndGet()
        }
        
        // Actualizar historial (mantener últimos 60 frames)
        synchronized(fpsHistory) {
            fpsHistory.add(fps)
            frameTimeHistory.add(frameTimeMs)
            
            if (fpsHistory.size > 60) {
                fpsHistory.removeAt(0)
                frameTimeHistory.removeAt(0)
            }
        }
        
        // Crear métricas del frame
        val jankRate = (jankCount.get().toFloat() / currentFrameCount) * 100f
        val performanceLevel = calculatePerformanceLevel(fps, jankRate)
        val isSmooth = !isJank && fps >= thresholds.targetFps * 0.9f
        
        val metrics = FrameMetrics(
            fps = fps,
            frameTime = frameTimeMs,
            jankCount = jankCount.get().toInt(),
            jankRate = jankRate,
            isSmooth = isSmooth,
            performanceLevel = performanceLevel
        )
        
        _frameMetrics.tryEmit(metrics)
        
        // Generar alertas si es necesario
        checkPerformanceAlerts(metrics)
    }
    
    
    /**
     * Calcula el nivel de rendimiento basado en FPS y jank rate
     */
    private fun calculatePerformanceLevel(fps: Float, jankRate: Float): PerformanceLevel {
        val fpsPercentage = (fps / thresholds.targetFps) * 100f
        
        return when {
            fpsPercentage >= 85f && jankRate <= 3f -> PerformanceLevel.EXCELLENT
            fpsPercentage >= 70f && jankRate <= 5f -> PerformanceLevel.GOOD
            fpsPercentage >= 50f && jankRate <= 15f -> PerformanceLevel.FAIR
            fpsPercentage >= 30f && jankRate <= 25f -> PerformanceLevel.POOR
            else -> PerformanceLevel.CRITICAL
        }
    }
    
    /**
     * Verifica y emite alertas de rendimiento
     */
    private suspend fun checkPerformanceAlerts(metrics: FrameMetrics) {
        when {
            metrics.jankRate > thresholds.criticalJankRate -> {
                _performanceAlerts.tryEmit(
                    PerformanceAlert(
                        level = AlertLevel.CRITICAL,
                        message = "Jank rate crítico: ${metrics.jankRate.roundToInt()}%",
                        metric = "jank_rate",
                        value = metrics.jankRate
                    )
                )
            }
            metrics.fps < thresholds.targetFps * 0.5f -> {
                _performanceAlerts.tryEmit(
                    PerformanceAlert(
                        level = AlertLevel.CRITICAL,
                        message = "FPS muy bajo: ${metrics.fps.roundToInt()}",
                        metric = "fps",
                        value = metrics.fps
                    )
                )
            }
            metrics.jankRate > thresholds.acceptableJankRate -> {
                _performanceAlerts.tryEmit(
                    PerformanceAlert(
                        level = AlertLevel.WARNING,
                        message = "Jank rate elevado: ${metrics.jankRate.roundToInt()}%",
                        metric = "jank_rate",
                        value = metrics.jankRate
                    )
                )
            }
        }
    }
    
    /**
     * Genera métricas de rendimiento periódicas
     */
    private suspend fun generatePerformanceMetrics() = withContext(Dispatchers.Default) {
        if (fpsHistory.isNotEmpty()) {
            val currentMetrics = FrameMetrics(
                fps = fpsHistory.average().toFloat(),
                frameTime = frameTimeHistory.average().toFloat(),
                jankCount = jankCount.get().toInt(),
                jankRate = (jankCount.get().toFloat() / frameCount.get()) * 100f,
                isSmooth = fpsHistory.average() >= thresholds.targetFps * 0.9f,
                performanceLevel = calculatePerformanceLevel(
                    fpsHistory.average().toFloat(),
                    (jankCount.get().toFloat() / frameCount.get()) * 100f
                )
            )
            
            _frameMetrics.tryEmit(currentMetrics)
        }
    }
    
    /**
     * Genera reporte completo de rendimiento
     */
    fun generatePerformanceReport(): PerformanceReport {
        val currentTime = System.currentTimeMillis()
        val sessionDuration = currentTime - sessionStartTime
        val totalFramesCount = frameCount.get().toInt()
        val totalJanksCount = jankCount.get().toInt()
        
        val averageFps = if (fpsHistory.isNotEmpty()) fpsHistory.average().toFloat() else 0f
        val minFps = fpsHistory.minOrNull() ?: 0f
        val maxFps = fpsHistory.maxOrNull() ?: 0f
        
        val jankPercentage = if (totalFramesCount > 0) {
            (totalJanksCount.toFloat() / totalFramesCount) * 100f
        } else 0f
        
        val smoothFrames = fpsHistory.count { it >= thresholds.targetFps * 0.9f }
        val smoothPercentage = if (fpsHistory.isNotEmpty()) {
            (smoothFrames.toFloat() / fpsHistory.size) * 100f
        } else 0f
        
        val overallPerformance = calculatePerformanceLevel(averageFps, jankPercentage)
        val recommendations = generateRecommendations(overallPerformance, averageFps, jankPercentage)
        
        return PerformanceReport(
            averageFps = averageFps,
            minFps = minFps,
            maxFps = maxFps,
            jankPercentage = jankPercentage,
            totalJanks = totalJanksCount,
            smoothFramePercentage = smoothPercentage,
            overallPerformance = overallPerformance,
            recommendations = recommendations,
            sessionDurationMs = sessionDuration,
            totalFrames = totalFramesCount
        )
    }
    
    /**
     * Genera recomendaciones basadas en el rendimiento
     */
    private fun generateRecommendations(
        performance: PerformanceLevel,
        averageFps: Float,
        jankRate: Float
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        when (performance) {
            PerformanceLevel.CRITICAL, PerformanceLevel.POOR -> {
                recommendations.add("Reducir calidad de animaciones")
                recommendations.add("Activar modo bajo consumo")
                recommendations.add("Limitar operaciones en UI thread")
                if (jankRate > 20f) {
                    recommendations.add("Optimizar recomposiciones en Compose")
                }
            }
            PerformanceLevel.FAIR -> {
                recommendations.add("Considerar reducir calidad de imágenes")
                if (averageFps < thresholds.targetFps * 0.8f) {
                    recommendations.add("Optimizar lazy loading")
                }
            }
            PerformanceLevel.GOOD -> {
                recommendations.add("Rendimiento aceptable")
                if (jankRate > 5f) {
                    recommendations.add("Pequeñas optimizaciones en animaciones")
                }
            }
            PerformanceLevel.EXCELLENT -> {
                recommendations.add("Rendimiento excelente")
                recommendations.add("Configuración óptima para este dispositivo")
            }
        }
        
        return recommendations
    }
    
    /**
     * Reinicia todas las métricas
     */
    fun resetMetrics() {
        frameCount.set(0)
        jankCount.set(0)
        fpsHistory.clear()
        frameTimeHistory.clear()
    }
    
    /**
     * Limpia recursos y detiene monitoreo
     */
    fun cleanup() {
        // jankStats?.isTrackingEnabled = false // REMOVIDO POR ERROR 16KB
        monitoringScope.cancel()
    }
}

/**
 * Composable para mostrar métricas de rendimiento en tiempo real
 */
@Composable
fun PerformanceMetricsDisplay(
    performanceMonitor: PerformanceMonitor,
    modifier: Modifier = Modifier,
    showDetailed: Boolean = false
) {
    val metrics by performanceMonitor.frameMetrics.collectAsState(
        initial = PerformanceMonitor.FrameMetrics(0f, 0f, 0, 0f, false, PerformanceMonitor.PerformanceLevel.FAIR)
    )
    val alerts by performanceMonitor.performanceAlerts.collectAsState(initial = null)
    
    // Solo mostrar en modo debug o si se solicita explícitamente
    if (showDetailed) {
        androidx.compose.material3.Card(
            modifier = modifier,
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = when (metrics.performanceLevel) {
                    PerformanceMonitor.PerformanceLevel.EXCELLENT -> androidx.compose.ui.graphics.Color.Green.copy(alpha = 0.1f)
                    PerformanceMonitor.PerformanceLevel.GOOD -> androidx.compose.ui.graphics.Color.Blue.copy(alpha = 0.1f)
                    PerformanceMonitor.PerformanceLevel.FAIR -> androidx.compose.ui.graphics.Color.Yellow.copy(alpha = 0.1f)
                    PerformanceMonitor.PerformanceLevel.POOR -> androidx.compose.ui.graphics.Color(0xFFFFA500).copy(alpha = 0.1f)
                    PerformanceMonitor.PerformanceLevel.CRITICAL -> androidx.compose.ui.graphics.Color.Red.copy(alpha = 0.1f)
                }
            )
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier.padding(16.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "Performance: ${metrics.performanceLevel}",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )
                androidx.compose.material3.Text(
                    text = "FPS: ${metrics.fps.roundToInt()} | Jank: ${metrics.jankRate.roundToInt()}%",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
                
                alerts?.let { alert ->
                    androidx.compose.material3.Text(
                        text = "⚠️ ${alert.message}",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = when (alert.level) {
                            PerformanceMonitor.AlertLevel.CRITICAL -> androidx.compose.ui.graphics.Color.Red
                            PerformanceMonitor.AlertLevel.WARNING -> androidx.compose.ui.graphics.Color(0xFFFFA500)
                            PerformanceMonitor.AlertLevel.INFO -> androidx.compose.ui.graphics.Color.Blue
                        }
                    )
                }
            }
        }
    }
}

/**
 * Hook para usar el performance monitor en Compose
 */
@Composable
fun rememberPerformanceMonitor(): PerformanceMonitor? {
    val context = LocalContext.current
    val vSyncManager = rememberVSyncManager()
    var deviceCapabilities by remember { mutableStateOf<DeviceCapabilityDetector.DeviceCapabilities?>(null) }
    
    LaunchedEffect(Unit) {
        val detector = DeviceCapabilityDetector(context)
        deviceCapabilities = detector.detectCapabilities()
    }
    
    return remember(deviceCapabilities) {
        deviceCapabilities?.let { caps ->
            PerformanceMonitor.getInstance(context, caps, vSyncManager)
        }
    }
}