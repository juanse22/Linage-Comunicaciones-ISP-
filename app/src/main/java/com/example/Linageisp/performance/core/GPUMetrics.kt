package com.example.Linageisp.performance.core

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import kotlin.math.roundToInt

/**
 * Sistema de monitoreo de métricas GPU con detección de throttling térmico
 */
class GPUMetrics private constructor(
    private val context: Context,
    private val deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities
) {
    
    companion object {
        @Volatile
        private var INSTANCE: GPUMetrics? = null
        
        fun getInstance(
            context: Context,
            deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities
        ): GPUMetrics {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GPUMetrics(
                    context.applicationContext,
                    deviceCapabilities
                ).also { INSTANCE = it }
            }
        }
        
        // Archivos del sistema para leer métricas GPU
        private val GPU_FREQ_PATHS = listOf(
            "/sys/class/kgsl/kgsl-3d0/gpufreq",                    // Adreno
            "/sys/class/kgsl/kgsl-3d0/freq",                      // Adreno alternativo
            "/sys/devices/platform/mali/cur_freq",                // Mali
            "/sys/devices/platform/gpusysfs/gpu_freq_table",      // Mali alternativo
            "/sys/class/devfreq/mali0/cur_freq",                  // Mali devfreq
            "/sys/kernel/debug/clk/gpu_cc_gx_gfx3d_clk/clk_rate" // Snapdragon
        )
        
        private val GPU_LOAD_PATHS = listOf(
            "/sys/class/kgsl/kgsl-3d0/gpu_busy_percentage",       // Adreno
            "/sys/class/kgsl/kgsl-3d0/devfreq/gpu_busy",          // Adreno alternativo
            "/sys/devices/platform/mali/utilization",             // Mali
            "/sys/class/devfreq/mali0/device/utilization",        // Mali devfreq
            "/sys/kernel/debug/kgsl/proc/*/mem/gpu",              // GPU memory usage
        )
        
        private val THERMAL_PATHS = listOf(
            "/sys/class/thermal/thermal_zone*/temp",               // Thermal zones
            "/sys/devices/virtual/thermal/thermal_zone*/temp",    // Alternative thermal
        )
    }
    
    data class GPUInfo(
        val vendor: String = "Unknown",
        val renderer: String = "Unknown", 
        val version: String = "Unknown",
        val maxTextureSize: Int = 0,
        val supportedExtensions: List<String> = emptyList(),
        val supportsVulkan: Boolean = false,
        val supportsOpenGLES31: Boolean = false
    )
    
    data class GPUMetricsSnapshot(
        val timestamp: Long = System.currentTimeMillis(),
        val gpuFrequencyMhz: Float = 0f,
        val gpuUtilizationPercent: Float = 0f,
        val thermalTemperature: Float = 0f,
        val isThrottling: Boolean = false,
        val memoryUsageMB: Long = 0L,
        val frameRenderTimeMs: Float = 0f,
        val trianglesPerSecond: Long = 0L,
        val pixelFillRate: Float = 0f,
        val vertexThroughput: Float = 0f
    )
    
    data class GPUPerformanceProfile(
        val tier: GPUTier,
        val baseFrequencyMhz: Float,
        val maxFrequencyMhz: Float,
        val thermalThrottleTemp: Float,
        val recommendedTargetFps: Int,
        val maxTextureResolution: Int,
        val supportsAdvancedShading: Boolean
    )
    
    enum class GPUTier {
        INTEGRATED_LOW,      // Mali-400, Adreno 505
        INTEGRATED_MID,      // Mali-G52, Adreno 610  
        INTEGRATED_HIGH,     // Mali-G76, Adreno 640
        DISCRETE_GAMING,     // High-end gaming GPUs
        DISCRETE_WORKSTATION // Professional GPUs
    }
    
    enum class RenderingQuality {
        ULTRA_LOW,    // Mínima calidad para dispositivos muy limitados
        LOW,          // Calidad básica
        MEDIUM,       // Calidad estándar
        HIGH,         // Alta calidad
        ULTRA         // Máxima calidad
    }
    
    private val logger = PerformanceLogger.getInstance()
    private val metricsScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // GPU Information (detectada al inicio)
    private var gpuInfo: GPUInfo = GPUInfo()
    private var performanceProfile: GPUPerformanceProfile? = null
    
    // Flows para métricas en tiempo real
    private val _gpuMetrics = MutableStateFlow(GPUMetricsSnapshot())
    val gpuMetrics: StateFlow<GPUMetricsSnapshot> = _gpuMetrics.asStateFlow()
    
    private val _thermalState = MutableStateFlow(ThermalState.NORMAL)
    val thermalState: StateFlow<ThermalState> = _thermalState.asStateFlow()
    
    private val _renderingQuality = MutableStateFlow(RenderingQuality.MEDIUM)
    val renderingQuality: StateFlow<RenderingQuality> = _renderingQuality.asStateFlow()
    
    enum class ThermalState {
        NORMAL,         // < 50°C
        WARM,           // 50-65°C  
        HOT,            // 65-80°C
        CRITICAL        // > 80°C
    }
    
    // Métricas acumulativas
    private val frameRenderTimes = mutableListOf<Float>()
    private var totalFramesRendered = 0L
    private var thermalThrottleEvents = 0L
    
    init {
        initializeGPUDetection()
        startMetricsCollection()
        startThermalMonitoring()
    }
    
    /**
     * Inicializa la detección de GPU y capacidades
     */
    private fun initializeGPUDetection() {
        metricsScope.launch {
            try {
                gpuInfo = detectGPUInfo()
                performanceProfile = classifyGPUPerformance()
                
                logger.info("GPU_METRICS", "GPU detection completed", mapOf(
                    "vendor" to gpuInfo.vendor,
                    "renderer" to gpuInfo.renderer,
                    "tier" to (performanceProfile?.tier?.name ?: "UNKNOWN")
                ))
                
            } catch (e: Exception) {
                logger.error("GPU_METRICS", "GPU detection failed", e)
            }
        }
    }
    
    /**
     * Detecta información de la GPU
     */
    private suspend fun detectGPUInfo(): GPUInfo = withContext(Dispatchers.IO) {
        return@withContext try {
            // En un entorno real, esto usaría OpenGL context para obtener info
            // Por ahora, simulamos basándose en las capacidades del dispositivo
            val pm = context.packageManager
            
            GPUInfo(
                vendor = detectGPUVendor(),
                renderer = "Unknown Renderer",
                version = "Unknown Version",
                maxTextureSize = 4096, // Valor por defecto conservador
                supportedExtensions = emptyList(),
                supportsVulkan = pm.hasSystemFeature("android.hardware.vulkan.level"),
                supportsOpenGLES31 = pm.hasSystemFeature("android.hardware.opengles.aep")
            )
        } catch (e: Exception) {
            logger.error("GPU_METRICS", "Failed to detect GPU info", e)
            GPUInfo()
        }
    }
    
    /**
     * Detecta el vendor de GPU basándose en archivos del sistema
     */
    private fun detectGPUVendor(): String {
        return try {
            // Verificar Adreno (Qualcomm)
            if (File("/sys/class/kgsl/kgsl-3d0").exists()) {
                "Qualcomm Adreno"
            }
            // Verificar Mali (ARM)
            else if (File("/sys/devices/platform/mali").exists() || 
                     File("/sys/class/devfreq/mali0").exists()) {
                "ARM Mali"
            }
            // Verificar PowerVR (Imagination Technologies)
            else if (Build.HARDWARE.contains("pvr", ignoreCase = true)) {
                "PowerVR"
            }
            else {
                "Unknown"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }
    
    /**
     * Clasifica el rendimiento de la GPU
     */
    private fun classifyGPUPerformance(): GPUPerformanceProfile {
        val vendor = gpuInfo.vendor.lowercase()
        val deviceTier = deviceCapabilities.tier
        
        return when {
            vendor.contains("adreno") -> classifyAdrenoGPU(deviceTier)
            vendor.contains("mali") -> classifyMaliGPU(deviceTier) 
            vendor.contains("powervr") -> classifyPowerVRGPU(deviceTier)
            else -> getDefaultProfile(deviceTier)
        }
    }
    
    private fun classifyAdrenoGPU(deviceTier: DeviceCapabilityDetector.PerformanceTier): GPUPerformanceProfile {
        return when (deviceTier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> GPUPerformanceProfile(
                tier = GPUTier.INTEGRATED_LOW,
                baseFrequencyMhz = 400f,
                maxFrequencyMhz = 600f,
                thermalThrottleTemp = 65f,
                recommendedTargetFps = 30,
                maxTextureResolution = 2048,
                supportsAdvancedShading = false
            )
            DeviceCapabilityDetector.PerformanceTier.MID_END -> GPUPerformanceProfile(
                tier = GPUTier.INTEGRATED_MID,
                baseFrequencyMhz = 500f,
                maxFrequencyMhz = 800f,
                thermalThrottleTemp = 70f,
                recommendedTargetFps = 45,
                maxTextureResolution = 4096,
                supportsAdvancedShading = true
            )
            DeviceCapabilityDetector.PerformanceTier.HIGH_END -> GPUPerformanceProfile(
                tier = GPUTier.INTEGRATED_HIGH,
                baseFrequencyMhz = 600f,
                maxFrequencyMhz = 1000f,
                thermalThrottleTemp = 75f,
                recommendedTargetFps = 60,
                maxTextureResolution = 4096,
                supportsAdvancedShading = true
            )
            DeviceCapabilityDetector.PerformanceTier.PREMIUM -> GPUPerformanceProfile(
                tier = GPUTier.DISCRETE_GAMING,
                baseFrequencyMhz = 800f,
                maxFrequencyMhz = 1200f,
                thermalThrottleTemp = 80f,
                recommendedTargetFps = 90,
                maxTextureResolution = 8192,
                supportsAdvancedShading = true
            )
        }
    }
    
    private fun classifyMaliGPU(deviceTier: DeviceCapabilityDetector.PerformanceTier): GPUPerformanceProfile {
        // Configuraciones similares pero ajustadas para Mali
        return classifyAdrenoGPU(deviceTier).copy(
            baseFrequencyMhz = classifyAdrenoGPU(deviceTier).baseFrequencyMhz * 0.8f,
            maxFrequencyMhz = classifyAdrenoGPU(deviceTier).maxFrequencyMhz * 0.8f
        )
    }
    
    private fun classifyPowerVRGPU(deviceTier: DeviceCapabilityDetector.PerformanceTier): GPUPerformanceProfile {
        return classifyAdrenoGPU(deviceTier).copy(
            baseFrequencyMhz = classifyAdrenoGPU(deviceTier).baseFrequencyMhz * 0.9f,
            maxFrequencyMhz = classifyAdrenoGPU(deviceTier).maxFrequencyMhz * 0.9f
        )
    }
    
    private fun getDefaultProfile(deviceTier: DeviceCapabilityDetector.PerformanceTier): GPUPerformanceProfile {
        return classifyAdrenoGPU(deviceTier)
    }
    
    /**
     * Inicia la recolección de métricas en tiempo real
     */
    private fun startMetricsCollection() {
        metricsScope.launch {
            while (isActive) {
                try {
                    val metrics = collectCurrentMetrics()
                    _gpuMetrics.value = metrics
                    
                    // Actualizar calidad de rendering basada en métricas
                    updateRenderingQuality(metrics)
                    
                    delay(1000) // Actualizar cada segundo
                } catch (e: Exception) {
                    logger.error("GPU_METRICS", "Metrics collection error", e)
                    delay(5000) // Esperar más tiempo si hay error
                }
            }
        }
    }
    
    /**
     * Recolecta métricas actuales de la GPU
     */
    private suspend fun collectCurrentMetrics(): GPUMetricsSnapshot = withContext(Dispatchers.IO) {
        return@withContext GPUMetricsSnapshot(
            gpuFrequencyMhz = readGPUFrequency(),
            gpuUtilizationPercent = readGPUUtilization(),
            thermalTemperature = readThermalTemperature(),
            isThrottling = isGPUThrottling(),
            memoryUsageMB = readGPUMemoryUsage(),
            frameRenderTimeMs = getAverageFrameRenderTime()
        )
    }
    
    /**
     * Lee la frecuencia actual de la GPU
     */
    private fun readGPUFrequency(): Float {
        GPU_FREQ_PATHS.forEach { path ->
            try {
                val file = File(path)
                if (file.exists()) {
                    val freqString = file.readText().trim()
                    val freqHz = freqString.toLongOrNull() ?: return@forEach
                    return (freqHz / 1_000_000f) // Convertir Hz a MHz
                }
            } catch (e: Exception) {
                // Continuar con el siguiente path
            }
        }
        
        // Si no se puede leer, retornar frecuencia base estimada
        return performanceProfile?.baseFrequencyMhz ?: 500f
    }
    
    /**
     * Lee la utilización actual de la GPU
     */
    private fun readGPUUtilization(): Float {
        GPU_LOAD_PATHS.forEach { path ->
            try {
                val file = File(path)
                if (file.exists()) {
                    val utilizationString = file.readText().trim()
                    return utilizationString.toFloatOrNull() ?: 0f
                }
            } catch (e: Exception) {
                // Continuar con el siguiente path
            }
        }
        
        // Estimar basándose en métricas disponibles
        return estimateGPUUtilization()
    }
    
    /**
     * Lee la temperatura térmica
     */
    private fun readThermalTemperature(): Float {
        THERMAL_PATHS.forEach { pathPattern ->
            try {
                // Expandir wildcards manualmente para thermal zones
                if (pathPattern.contains("*")) {
                    for (i in 0..20) { // Verificar thermal_zone0 a thermal_zone20
                        val actualPath = pathPattern.replace("*", i.toString())
                        val file = File(actualPath)
                        if (file.exists()) {
                            val tempString = file.readText().trim()
                            val tempMilliC = tempString.toLongOrNull() ?: return@forEach
                            return tempMilliC / 1000f // Convertir milicelsius a celsius
                        }
                    }
                } else {
                    val file = File(pathPattern)
                    if (file.exists()) {
                        val tempString = file.readText().trim()
                        val tempMilliC = tempString.toLongOrNull() ?: return@forEach
                        return tempMilliC / 1000f
                    }
                }
            } catch (e: Exception) {
                // Continuar con el siguiente path
            }
        }
        
        return 45f // Temperatura por defecto conservadora
    }
    
    /**
     * Detecta si la GPU está siendo throttled térmicamente
     */
    private fun isGPUThrottling(): Boolean {
        val currentTemp = readThermalTemperature()
        val profile = performanceProfile ?: return false
        
        return currentTemp > profile.thermalThrottleTemp
    }
    
    /**
     * Estima el uso de memoria de la GPU
     */
    private fun readGPUMemoryUsage(): Long {
        // En Android, esto sería complejo de obtener sin root
        // Retornamos una estimación basada en el tier del dispositivo
        return when (deviceCapabilities.tier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> 128L
            DeviceCapabilityDetector.PerformanceTier.MID_END -> 256L
            DeviceCapabilityDetector.PerformanceTier.HIGH_END -> 512L
            DeviceCapabilityDetector.PerformanceTier.PREMIUM -> 1024L
        }
    }
    
    /**
     * Estima la utilización de GPU basándose en métricas disponibles
     */
    private fun estimateGPUUtilization(): Float {
        // Estimación simple basada en el frame render time promedio
        val avgFrameTime = getAverageFrameRenderTime()
        val targetFrameTime = 1000f / (performanceProfile?.recommendedTargetFps ?: 60)
        
        return ((avgFrameTime / targetFrameTime) * 100f).coerceIn(0f, 100f)
    }
    
    /**
     * Obtiene el tiempo promedio de renderizado de frames
     */
    private fun getAverageFrameRenderTime(): Float {
        synchronized(frameRenderTimes) {
            return if (frameRenderTimes.isNotEmpty()) {
                frameRenderTimes.average().toFloat()
            } else {
                16.67f // 60 FPS por defecto
            }
        }
    }
    
    /**
     * Monitorea el estado térmico
     */
    private fun startThermalMonitoring() {
        metricsScope.launch {
            while (isActive) {
                try {
                    val temp = readThermalTemperature()
                    val newState = when {
                        temp < 50f -> ThermalState.NORMAL
                        temp < 65f -> ThermalState.WARM
                        temp < 80f -> ThermalState.HOT
                        else -> ThermalState.CRITICAL
                    }
                    
                    if (newState != _thermalState.value) {
                        _thermalState.value = newState
                        
                        if (newState == ThermalState.HOT || newState == ThermalState.CRITICAL) {
                            thermalThrottleEvents++
                            logger.warn("GPU_METRICS", "Thermal throttling detected", mapOf(
                                "temperature" to temp,
                                "state" to newState.name
                            ))
                        }
                    }
                    
                    delay(2000) // Verificar cada 2 segundos
                } catch (e: Exception) {
                    logger.error("GPU_METRICS", "Thermal monitoring error", e)
                    delay(5000)
                }
            }
        }
    }
    
    /**
     * Actualiza la calidad de rendering basándose en métricas actuales
     */
    private fun updateRenderingQuality(metrics: GPUMetricsSnapshot) {
        val currentQuality = _renderingQuality.value
        val newQuality = when {
            metrics.isThrottling || metrics.thermalTemperature > 75f -> {
                // Reducir calidad si hay throttling térmico
                when (currentQuality) {
                    RenderingQuality.ULTRA -> RenderingQuality.HIGH
                    RenderingQuality.HIGH -> RenderingQuality.MEDIUM
                    RenderingQuality.MEDIUM -> RenderingQuality.LOW
                    RenderingQuality.LOW -> RenderingQuality.ULTRA_LOW
                    RenderingQuality.ULTRA_LOW -> RenderingQuality.ULTRA_LOW
                }
            }
            metrics.gpuUtilizationPercent > 90f -> {
                // GPU muy cargada, reducir calidad
                when (currentQuality) {
                    RenderingQuality.ULTRA -> RenderingQuality.HIGH
                    RenderingQuality.HIGH -> RenderingQuality.MEDIUM
                    else -> currentQuality
                }
            }
            metrics.gpuUtilizationPercent < 50f && metrics.thermalTemperature < 60f -> {
                // GPU con capacidad disponible, posible aumentar calidad
                when (currentQuality) {
                    RenderingQuality.LOW -> RenderingQuality.MEDIUM
                    RenderingQuality.MEDIUM -> RenderingQuality.HIGH
                    RenderingQuality.HIGH -> if (deviceCapabilities.tier == DeviceCapabilityDetector.PerformanceTier.PREMIUM) RenderingQuality.ULTRA else currentQuality
                    else -> currentQuality
                }
            }
            else -> currentQuality
        }
        
        if (newQuality != currentQuality) {
            _renderingQuality.value = newQuality
            logger.info("GPU_METRICS", "Rendering quality adjusted", mapOf(
                "old_quality" to currentQuality.name,
                "new_quality" to newQuality.name,
                "gpu_utilization" to metrics.gpuUtilizationPercent,
                "temperature" to metrics.thermalTemperature
            ))
        }
    }
    
    /**
     * Registra el tiempo de renderizado de un frame
     */
    fun recordFrameRenderTime(renderTimeMs: Float) {
        synchronized(frameRenderTimes) {
            frameRenderTimes.add(renderTimeMs)
            totalFramesRendered++
            
            // Mantener solo los últimos 60 frames
            if (frameRenderTimes.size > 60) {
                frameRenderTimes.removeAt(0)
            }
        }
    }
    
    /**
     * Obtiene recomendaciones de configuración GPU
     */
    fun getGPURecommendations(): List<String> {
        val metrics = _gpuMetrics.value
        val thermalState = _thermalState.value
        val recommendations = mutableListOf<String>()
        
        when (thermalState) {
            ThermalState.CRITICAL -> {
                recommendations.add("Reducir resolución de texturas")
                recommendations.add("Desactivar efectos avanzados")
                recommendations.add("Limitar FPS target")
            }
            ThermalState.HOT -> {
                recommendations.add("Reducir calidad de shaders")
                recommendations.add("Activar VSync adaptive")
            }
            ThermalState.WARM -> {
                recommendations.add("Monitorear temperatura")
            }
            ThermalState.NORMAL -> {
                if (metrics.gpuUtilizationPercent < 50f) {
                    recommendations.add("Posible aumentar calidad gráfica")
                }
            }
        }
        
        return recommendations
    }
    
    /**
     * Obtiene el perfil de rendimiento actual
     */
    fun getPerformanceProfile(): GPUPerformanceProfile? = performanceProfile
    
    /**
     * Obtiene información de la GPU
     */
    fun getGPUInfo(): GPUInfo = gpuInfo
    
    /**
     * Obtiene estadísticas de sesión
     */
    fun getSessionStats(): Map<String, Any> {
        return mapOf(
            "total_frames_rendered" to totalFramesRendered,
            "thermal_throttle_events" to thermalThrottleEvents,
            "average_frame_time_ms" to getAverageFrameRenderTime(),
            "current_gpu_utilization" to _gpuMetrics.value.gpuUtilizationPercent,
            "current_temperature" to _gpuMetrics.value.thermalTemperature,
            "current_quality" to _renderingQuality.value.name
        )
    }
    
    /**
     * Limpia recursos
     */
    fun cleanup() {
        metricsScope.cancel()
        synchronized(frameRenderTimes) {
            frameRenderTimes.clear()
        }
    }
}

/**
 * Composable para acceder a GPU metrics
 */
@Composable
fun rememberGPUMetrics(): GPUMetrics {
    val context = LocalContext.current
    val deviceCapabilities = rememberDeviceCapabilities()
    
    return remember(deviceCapabilities) {
        if (deviceCapabilities != null) {
            GPUMetrics.getInstance(context, deviceCapabilities)
        } else {
            // Crear con configuración por defecto si no se tienen capacidades
            val defaultCapabilities = DeviceCapabilityDetector.DeviceCapabilities(
                tier = DeviceCapabilityDetector.PerformanceTier.MID_END,
                ramGB = 4,
                cpuCores = 4,
                apiLevel = Build.VERSION.SDK_INT,
                hasHardwareAcceleration = true,
                supportedFeatures = emptySet(),
                thermalCapabilities = DeviceCapabilityDetector.ThermalCapabilities(
                    supportsThermalAPI = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q,
                    maxSustainedPerformance = 1.0f,
                    thermalThrottlingTemp = 70f
                ),
                displayMetrics = DeviceCapabilityDetector.DisplayMetrics(
                    widthPx = 1080,
                    heightPx = 1920,
                    densityDpi = 480,
                    refreshRate = 60f
                ),
                recommendedSettings = DeviceCapabilityDetector.PerformanceSettings(
                    targetFps = 60,
                    recommendedAnimationScale = 1.0f,
                    recommendedImageQuality = DeviceCapabilityDetector.ImageQuality.STANDARD,
                    maxConcurrentOperations = 4,
                    enableAdvancedEffects = true,
                    cacheSize = 64
                )
            )
            GPUMetrics.getInstance(context, defaultCapabilities)
        }
    }
}