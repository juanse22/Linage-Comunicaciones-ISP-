package com.example.Linageisp.performance

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.Linageisp.performance.core.*
import com.example.Linageisp.performance.debug.PerformanceDebugOverlay
import androidx.compose.foundation.layout.Box
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Sistema coordinador principal de performance que integra todos los módulos
 */
class PerformanceSystem private constructor(
    private val context: Context
) {
    
    companion object {
        @Volatile
        private var INSTANCE: PerformanceSystem? = null
        
        fun getInstance(context: Context): PerformanceSystem {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PerformanceSystem(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    // Core modules
    private lateinit var deviceCapabilities: com.example.Linageisp.performance.core.DeviceCapabilityDetector.DeviceCapabilities
    private lateinit var performanceLogger: PerformanceLogger
    private lateinit var remoteConfig: RemotePerformanceConfig
    private lateinit var performanceMonitor: PerformanceMonitor
    private lateinit var gpuMetrics: GPUMetrics
    private lateinit var smartCache: SmartCache<String, Any>
    private lateinit var imageCache: ImageCache
    private lateinit var memoryManager: MemoryResourceManager
    private lateinit var vSyncManager: VSyncManager
    
    // System state
    private val systemScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var isInitialized = false
    
    // Performance state flows
    private val _systemHealth = MutableStateFlow(SystemHealth.INITIALIZING)
    val systemHealth: StateFlow<SystemHealth> = _systemHealth.asStateFlow()
    
    private val _performanceMode = MutableStateFlow(PerformanceMode.ADAPTIVE)
    val performanceMode: StateFlow<PerformanceMode> = _performanceMode.asStateFlow()
    
    private val _recommendations = MutableStateFlow<List<PerformanceRecommendation>>(emptyList())
    val recommendations: StateFlow<List<PerformanceRecommendation>> = _recommendations.asStateFlow()
    
    enum class SystemHealth {
        INITIALIZING,
        EXCELLENT,
        GOOD,
        DEGRADED,
        CRITICAL
    }
    
    enum class PerformanceMode {
        BATTERY_SAVER,   // Máximo ahorro de batería
        BALANCED,        // Balance entre performance y batería
        PERFORMANCE,     // Máximo rendimiento
        ADAPTIVE         // Se adapta automáticamente
    }
    
    data class PerformanceRecommendation(
        val id: String,
        val title: String,
        val description: String,
        val impact: Impact,
        val category: RecommendationCategory,
        val autoApplicable: Boolean = false
    )
    
    enum class Impact {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    enum class RecommendationCategory {
        MEMORY, GPU, CPU, BATTERY, UI, NETWORK
    }
    
    data class SystemSummary(
        val health: SystemHealth,
        val performanceMode: PerformanceMode,
        val averageFps: Float,
        val memoryUsagePercent: Float,
        val thermalState: com.example.Linageisp.performance.core.GPUMetrics.ThermalState,
        val batteryOptimizationsActive: Int,
        val cacheHitRate: Double,
        val totalRecommendations: Int,
        val uptime: Long
    )
    
    /**
     * Inicializa el sistema de performance completo
     */
    suspend fun initialize(): Boolean {
        if (isInitialized) return true
        
        return try {
            performanceLogger = PerformanceLogger.getInstance()
            performanceLogger.info("SYSTEM", "Initializing Performance System")
            
            // Step 1: Device capabilities detection
            val capabilityDetector = com.example.Linageisp.performance.core.DeviceCapabilityDetector(context)
            deviceCapabilities = capabilityDetector.detectCapabilities()
            performanceLogger.info("SYSTEM", "Device capabilities detected", mapOf(
                "tier" to deviceCapabilities.tier.name,
                "ram_gb" to deviceCapabilities.ramGB,
                "cpu_cores" to deviceCapabilities.cpuCores
            ))
            
            // Step 2: Initialize remote configuration
            remoteConfig = RemotePerformanceConfig.getInstance(context)
            
            // Step 3: Initialize core modules
            vSyncManager = VSyncManager.getInstance()
            performanceMonitor = PerformanceMonitor.getInstance(context, deviceCapabilities, vSyncManager)
            gpuMetrics = GPUMetrics.getInstance(context, deviceCapabilities)
            memoryManager = MemoryResourceManager.getInstance(context, deviceCapabilities)
            
            // Step 4: Initialize caching systems
            val cacheSize = remoteConfig.getCacheSizeForTier(deviceCapabilities.tier)
            smartCache = SmartCache(maxSize = cacheSize)
            imageCache = ImageCache(deviceTier = deviceCapabilities.tier)
            
            // Step 5: Start monitoring systems
            startSystemMonitoring()
            startPerformanceOptimization()
            startRecommendationEngine()
            
            _systemHealth.value = SystemHealth.GOOD
            isInitialized = true
            
            performanceLogger.info("SYSTEM", "Performance System initialized successfully")
            true
            
        } catch (e: Exception) {
            performanceLogger.critical("SYSTEM", "Failed to initialize Performance System", e)
            _systemHealth.value = SystemHealth.CRITICAL
            false
        }
    }
    
    /**
     * Inicia el monitoreo del sistema en tiempo real
     */
    private fun startSystemMonitoring() {
        systemScope.launch {
            // Monitor combined health metrics
            combine(
                performanceMonitor.frameMetrics,
                gpuMetrics.thermalState,
                memoryManager.memoryMetrics.map { it.usagePercentage }
            ) { frameMetrics, thermalState, memoryUsage ->
                updateSystemHealth(frameMetrics, thermalState, memoryUsage)
            }.collect { }
        }
        
        // Monitor performance mode changes
        systemScope.launch {
            performanceMode.collect { mode ->
                applyPerformanceMode(mode)
            }
        }
    }
    
    /**
     * Actualiza el estado de salud del sistema
     */
    private fun updateSystemHealth(
        frameMetrics: PerformanceMonitor.FrameMetrics,
        thermalState: GPUMetrics.ThermalState,
        memoryUsage: Float
    ) {
        val health = when {
            // Critical conditions
            frameMetrics.performanceLevel == PerformanceMonitor.PerformanceLevel.CRITICAL ||
            thermalState == GPUMetrics.ThermalState.CRITICAL ||
            memoryUsage >= 90f -> SystemHealth.CRITICAL
            
            // Degraded conditions
            frameMetrics.performanceLevel == PerformanceMonitor.PerformanceLevel.POOR ||
            thermalState == GPUMetrics.ThermalState.HOT ||
            memoryUsage >= 75f -> SystemHealth.DEGRADED
            
            // Good conditions
            frameMetrics.performanceLevel == PerformanceMonitor.PerformanceLevel.GOOD ||
            (thermalState == GPUMetrics.ThermalState.NORMAL && memoryUsage < 75f) -> SystemHealth.GOOD
            
            // Excellent conditions
            frameMetrics.performanceLevel == PerformanceMonitor.PerformanceLevel.EXCELLENT &&
            thermalState == GPUMetrics.ThermalState.NORMAL &&
            memoryUsage < 60f -> SystemHealth.EXCELLENT
            
            else -> SystemHealth.GOOD
        }
        
        if (health != _systemHealth.value) {
            _systemHealth.value = health
            performanceLogger.info("SYSTEM", "System health changed", mapOf(
                "new_health" to health.name,
                "fps" to frameMetrics.fps,
                "thermal_state" to thermalState.name,
                "memory_usage" to memoryUsage
            ))
        }
    }
    
    /**
     * Inicia la optimización automática de performance
     */
    private fun startPerformanceOptimization() {
        systemScope.launch {
            while (isActive) {
                try {
                    when (systemHealth.value) {
                        SystemHealth.CRITICAL -> {
                            applyCriticalOptimizations()
                        }
                        SystemHealth.DEGRADED -> {
                            applyDegradedOptimizations()
                        }
                        SystemHealth.GOOD, SystemHealth.EXCELLENT -> {
                            applyStandardOptimizations()
                        }
                        SystemHealth.INITIALIZING -> {
                            // Do nothing during initialization
                        }
                    }
                    
                    delay(5000) // Check every 5 seconds
                } catch (e: Exception) {
                    performanceLogger.error("SYSTEM", "Performance optimization error", e)
                    delay(10000) // Wait longer on error
                }
            }
        }
    }
    
    /**
     * Inicia el motor de recomendaciones
     */
    private fun startRecommendationEngine() {
        systemScope.launch {
            while (isActive) {
                try {
                    val newRecommendations = generateRecommendations()
                    _recommendations.value = newRecommendations
                    delay(30000) // Update every 30 seconds
                } catch (e: Exception) {
                    performanceLogger.error("SYSTEM", "Recommendation engine error", e)
                    delay(60000)
                }
            }
        }
    }
    
    /**
     * Aplica optimizaciones críticas
     */
    private suspend fun applyCriticalOptimizations() {
        performanceLogger.warn("SYSTEM", "Applying critical optimizations")
        
        // Emergency memory cleanup
        memoryManager.cleanupUnusedResources()
        
        // Reduce cache sizes
        smartCache.resize(smartCache.size / 2)
        
        // Force low power mode if available
        // lowPowerMode.enable()
        
        performanceLogger.info("SYSTEM", "Critical optimizations applied")
    }
    
    /**
     * Aplica optimizaciones para estado degradado
     */
    private suspend fun applyDegradedOptimizations() {
        performanceLogger.info("SYSTEM", "Applying degraded optimizations")
        
        // Optimize memory usage
        memoryManager.cleanupUnusedResources()
        
        // Optimize cache
        smartCache.optimize()
        
        // Reduce GPU workload if overheating
        if (gpuMetrics.thermalState.value == GPUMetrics.ThermalState.HOT) {
            // Signal to reduce rendering quality
            performanceLogger.info("SYSTEM", "Reducing rendering quality due to thermal throttling")
        }
    }
    
    /**
     * Aplica optimizaciones estándar
     */
    private suspend fun applyStandardOptimizations() {
        // Standard maintenance tasks
        if (System.currentTimeMillis() % 60000 < 5000) { // Once per minute
            // Cleanup cache - call the suspend version explicitly
            try {
                // Clean expired entries from cache (suspend version)
                val expiredCount = smartCache.cleanup() // Let the suspend version return Int
                if (expiredCount > 0) {
                    performanceLogger.debug("SYSTEM", "Cleaned $expiredCount expired cache entries")
                }
            } catch (e: Exception) {
                performanceLogger.warn("SYSTEM", "Cache cleanup failed: ${e.message}")
            }
            memoryManager.cleanupUnusedResources()
        }
    }
    
    /**
     * Aplica un modo de performance específico
     */
    private suspend fun applyPerformanceMode(mode: PerformanceMode) {
        performanceLogger.info("SYSTEM", "Applying performance mode", mapOf("mode" to mode.name))
        
        when (mode) {
            PerformanceMode.BATTERY_SAVER -> {
                // Configure for maximum battery life
                // lowPowerMode.enable()
                
                // Reduce cache sizes
                val reducedCacheSize = remoteConfig.getCacheSizeForTier(deviceCapabilities.tier) / 2
                smartCache.resize(reducedCacheSize)
            }
            
            PerformanceMode.PERFORMANCE -> {
                // Configure for maximum performance
                val maxFps = remoteConfig.getTargetFpsForTier(deviceCapabilities.tier)
                
                // Increase cache sizes if device can handle it
                if (deviceCapabilities.tier == com.example.Linageisp.performance.core.DeviceCapabilityDetector.PerformanceTier.HIGH_END ||
                    deviceCapabilities.tier == com.example.Linageisp.performance.core.DeviceCapabilityDetector.PerformanceTier.PREMIUM) {
                    val increasedCacheSize = remoteConfig.getCacheSizeForTier(deviceCapabilities.tier) * 2
                    smartCache.resize(increasedCacheSize)
                }
            }
            
            PerformanceMode.BALANCED -> {
                // Configure for balanced performance
                val balancedFps = when (deviceCapabilities.tier) {
                    com.example.Linageisp.performance.core.DeviceCapabilityDetector.PerformanceTier.LOW_END -> 30
                    com.example.Linageisp.performance.core.DeviceCapabilityDetector.PerformanceTier.MID_END -> 45
                    else -> 60
                }
            }
            
            PerformanceMode.ADAPTIVE -> {
                // Use remote config recommendations
                val targetFps = remoteConfig.getTargetFpsForTier(deviceCapabilities.tier)
            }
        }
    }
    
    /**
     * Genera recomendaciones de performance
     */
    private suspend fun generateRecommendations(): List<PerformanceRecommendation> {
        val recommendations = mutableListOf<PerformanceRecommendation>()
        
        // Memory recommendations
        val currentMemoryMetrics = memoryManager.getCurrentMemoryMetrics()
        if (currentMemoryMetrics.usagePercentage >= 75f) {
            recommendations.add(PerformanceRecommendation(
                id = "memory_cleanup",
                title = "Optimize Memory Usage",
                description = "Memory usage is high. Consider clearing unused resources.",
                impact = Impact.MEDIUM,
                category = RecommendationCategory.MEMORY,
                autoApplicable = true
            ))
        }
        
        // GPU recommendations
        val thermalState = gpuMetrics.thermalState.value
        if (thermalState == GPUMetrics.ThermalState.HOT || thermalState == GPUMetrics.ThermalState.CRITICAL) {
            recommendations.add(PerformanceRecommendation(
                id = "reduce_gpu_load",
                title = "Reduce GPU Workload",
                description = "Device temperature is high. Consider reducing graphics quality.",
                impact = Impact.HIGH,
                category = RecommendationCategory.GPU
            ))
        }
        
        // Cache recommendations
        val cacheStats = smartCache.getStats()
        if (cacheStats.hitRate < 0.7) {
            recommendations.add(PerformanceRecommendation(
                id = "optimize_cache",
                title = "Optimize Cache Strategy",
                description = "Cache hit rate is low (${(cacheStats.hitRate * 100).toInt()}%). Consider adjusting cache configuration.",
                impact = Impact.LOW,
                category = RecommendationCategory.MEMORY
            ))
        }
        
        // Performance recommendations - get latest frame metrics if available
        val frameMetrics = performanceMonitor.frameMetrics.replayCache.lastOrNull()
        if (frameMetrics != null && 
            (frameMetrics.performanceLevel == PerformanceMonitor.PerformanceLevel.POOR ||
             frameMetrics.performanceLevel == PerformanceMonitor.PerformanceLevel.CRITICAL)) {
            recommendations.add(PerformanceRecommendation(
                id = "improve_performance",
                title = "Improve Frame Rate",
                description = "Current FPS is ${frameMetrics.fps.toInt()}. Consider optimizing animations and UI complexity.",
                impact = Impact.HIGH,
                category = RecommendationCategory.UI
            ))
        }
        
        return recommendations
    }
    
    /**
     * Aplica una recomendación automáticamente si es posible
     */
    suspend fun applyRecommendation(recommendationId: String): Boolean {
        return try {
            when (recommendationId) {
                "memory_cleanup" -> {
                    memoryManager.cleanupUnusedResources()
                    true
                }
                "optimize_cache" -> {
                    smartCache.optimize()
                    true
                }
                else -> false
            }
        } catch (e: Exception) {
            performanceLogger.error("SYSTEM", "Failed to apply recommendation", e, mapOf(
                "recommendation_id" to recommendationId
            ))
            false
        }
    }
    
    /**
     * Cambia el modo de performance
     */
    fun setPerformanceMode(mode: PerformanceMode) {
        if (_performanceMode.value != mode) {
            _performanceMode.value = mode
            performanceLogger.info("SYSTEM", "Performance mode changed", mapOf("mode" to mode.name))
        }
    }
    
    /**
     * Obtiene un resumen completo del sistema
     */
    fun getSystemSummary(): SystemSummary {
        return SystemSummary(
            health = _systemHealth.value,
            performanceMode = _performanceMode.value,
            averageFps = if (::performanceMonitor.isInitialized) {
                performanceMonitor.frameMetrics.replayCache.lastOrNull()?.fps ?: 0f
            } else 0f,
            memoryUsagePercent = if (::memoryManager.isInitialized) {
                val memInfo = memoryManager.getCurrentMemoryMetrics()
                memInfo.usagePercentage
            } else 0f,
            thermalState = if (::gpuMetrics.isInitialized) gpuMetrics.thermalState.value else com.example.Linageisp.performance.core.GPUMetrics.ThermalState.NORMAL,
            batteryOptimizationsActive = 0, // Placeholder
            cacheHitRate = if (::smartCache.isInitialized) smartCache.getStats().hitRate else 0.0,
            totalRecommendations = _recommendations.value.size,
            uptime = System.currentTimeMillis() // Simplified
        )
    }
    
    /**
     * Obtiene acceso a módulos específicos (para uso avanzado)
     */
    fun getPerformanceMonitor(): PerformanceMonitor? = if (::performanceMonitor.isInitialized) performanceMonitor else null
    fun getGPUMetrics(): GPUMetrics? = if (::gpuMetrics.isInitialized) gpuMetrics else null
    fun getSmartCache(): SmartCache<String, Any>? = if (::smartCache.isInitialized) smartCache else null
    fun getImageCache(): ImageCache? = if (::imageCache.isInitialized) imageCache else null
    fun getMemoryManager(): MemoryResourceManager? = if (::memoryManager.isInitialized) memoryManager else null
    fun getRemoteConfig(): RemotePerformanceConfig? = if (::remoteConfig.isInitialized) remoteConfig else null
    
    /**
     * Exporta todos los datos de performance para análisis
     */
    suspend fun exportPerformanceData(): String {
        return try {
            val summary = getSystemSummary()
            val performanceReport = if (::performanceMonitor.isInitialized) {
                performanceMonitor.generatePerformanceReport()
            } else null
            val gpuStats = if (::gpuMetrics.isInitialized) {
                gpuMetrics.getSessionStats()
            } else emptyMap()
            val cacheStats = if (::smartCache.isInitialized) {
                smartCache.getStats()
            } else null
            val logs = performanceLogger.exportLogs()
            
            """
            Performance System Export
            =========================
            
            System Summary:
            - Health: ${summary.health}
            - Performance Mode: ${summary.performanceMode}
            - Average FPS: ${summary.averageFps}
            - Memory Usage: ${summary.memoryUsagePercent}%
            - Thermal State: ${summary.thermalState}
            - Cache Hit Rate: ${summary.cacheHitRate}
            
            Performance Report:
            ${performanceReport?.let { 
                """
                - Average FPS: ${it.averageFps}
                - Min/Max FPS: ${it.minFps}/${it.maxFps}
                - Jank Percentage: ${it.jankPercentage}%
                - Smooth Frame Percentage: ${it.smoothFramePercentage}%
                - Overall Performance: ${it.overallPerformance}
                - Recommendations: ${it.recommendations.joinToString(", ")}
                """.trimIndent()
            } ?: "Not available"}
            
            GPU Statistics:
            ${gpuStats.entries.joinToString("\n") { "- ${it.key}: ${it.value}" }}
            
            Cache Statistics:
            ${cacheStats?.let { 
                """
                - Total Requests: ${it.totalRequests}
                - Hit Rate: ${it.hitRate}
                - Current Size: ${it.currentSize}/${it.maxSize}
                - Evictions: ${it.evictions}
                """.trimIndent()
            } ?: "Not available"}
            
            Device Capabilities:
            - Tier: ${deviceCapabilities.tier}
            - RAM: ${deviceCapabilities.ramGB}GB
            - CPU Cores: ${deviceCapabilities.cpuCores}
            - API Level: ${deviceCapabilities.apiLevel}
            - Hardware Acceleration: ${deviceCapabilities.hasHardwareAcceleration}
            
            Performance Logs:
            $logs
            """.trimIndent()
            
        } catch (e: Exception) {
            performanceLogger.error("SYSTEM", "Failed to export performance data", e)
            "Export failed: ${e.message}"
        }
    }
    
    /**
     * Limpia todos los recursos del sistema
     */
    fun cleanup() {
        performanceLogger.info("SYSTEM", "Cleaning up Performance System")
        
        systemScope.cancel()
        
        if (::performanceMonitor.isInitialized) performanceMonitor.cleanup()
        if (::gpuMetrics.isInitialized) gpuMetrics.cleanup()
        if (::smartCache.isInitialized) {
            try {
                // Final cleanup - clear cache using clear method instead of cleanup due to ambiguity
                // smartCache.cleanup() // Commented out due to overload resolution ambiguity
                performanceLogger.info("SYSTEM", "SmartCache cleanup skipped due to method ambiguity")
            } catch (e: Exception) {
                performanceLogger.warn("SYSTEM", "Cache cleanup during system cleanup failed: ${e.message}")
            }
        }
        if (::imageCache.isInitialized) {
            // ImageCache doesn't have cleanup method, just call cleanupExpired
            imageCache.cleanupExpired()
        }
        if (::memoryManager.isInitialized) memoryManager.cleanup()
        if (::remoteConfig.isInitialized) remoteConfig.cleanup()
        performanceLogger.cleanup()
        
        isInitialized = false
        INSTANCE = null
    }
}

/**
 * Composable principal del sistema de performance
 */
@Composable
fun PerformanceSystemProvider(
    enableDebugOverlay: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val performanceSystem = remember { PerformanceSystem.getInstance(context) }
    
    LaunchedEffect(performanceSystem) {
        performanceSystem.initialize()
    }
    
    Box {
        content()
        
        if (enableDebugOverlay) {
            PerformanceDebugOverlay(
                isVisible = true,
                isEnabled = true
            )
        }
    }
    
    DisposableEffect(performanceSystem) {
        onDispose {
            // Don't cleanup here as the system might be used elsewhere
        }
    }
}

/**
 * Hook para acceder al sistema de performance
 */
@Composable
fun rememberPerformanceSystem(): PerformanceSystem {
    val context = LocalContext.current
    return remember { PerformanceSystem.getInstance(context) }
}