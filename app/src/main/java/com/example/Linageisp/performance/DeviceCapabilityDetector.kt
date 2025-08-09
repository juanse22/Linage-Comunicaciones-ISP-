package com.example.Linageisp.performance

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Detector de capacidades del dispositivo para optimización adaptativa
 * Compatible con dispositivos desde Samsung J5 Prime hasta Galaxy S25 Ultra
 */
class DeviceCapabilityDetector(private val context: Context) {
    
    companion object {
        // Umbrales para clasificación de dispositivos
        private const val LOW_END_RAM_MB = 3072 // 3GB
        private const val MID_END_RAM_MB = 6144 // 6GB
        private const val LOW_END_CPU_CORES = 4
        private const val MID_END_CPU_CORES = 6
        
        // Modelos específicos conocidos por su rendimiento
        private val LOW_END_MODELS = setOf(
            "sm-j530f", "sm-j530fm", "sm-j530y", // J5 Prime series
            "sm-j730f", "sm-j730fm", // J7 Prime series  
            "sm-a105f", "sm-a105fn", // A10 series
            "sm-a205f", "sm-a205fn"  // A20 series
        )
        
        private val HIGH_END_MODELS = setOf(
            "sm-s928", "sm-s926", "sm-s921", // S24 Ultra, S24+, S24
            "sm-s918", "sm-s916", "sm-s911", // S23 Ultra, S23+, S23
            "sm-n986", "sm-n981", "sm-n980"  // Note20 Ultra, Note20 5G, Note20
        )
    }
    
    data class DeviceCapabilities(
        val tier: PerformanceTier,
        val ramMB: Long,
        val cpuCores: Int,
        val screenDensity: Float,
        val screenSizePx: Pair<Int, Int>,
        val screenSizeInches: Float,
        val supportsVulkan: Boolean,
        val batteryOptimizationNeeded: Boolean,
        val recommendedAnimationScale: Float,
        val recommendedImageQuality: ImageQuality,
        val maxConcurrentOperations: Int
    )
    
    enum class PerformanceTier {
        LOW_END,    // Samsung J5 Prime, dispositivos antiguos
        MID_END,    // Dispositivos intermedios
        HIGH_END,   // Galaxy S25 Ultra, flagships
        PREMIUM     // Dispositivos de gama ultra-premium
    }
    
    enum class ImageQuality {
        BASIC,      // Máxima compresión, resolución reducida
        STANDARD,   // Compresión moderada
        HIGH,       // Alta calidad
        ULTRA       // Máxima calidad sin compresión
    }
    
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    
    fun detectCapabilities(): DeviceCapabilities {
        val ramMB = getTotalRAM()
        val cpuCores = Runtime.getRuntime().availableProcessors()
        val screenMetrics = getScreenMetrics()
        val modelName = Build.MODEL.lowercase()
        
        val tier = determinePerformanceTier(ramMB, cpuCores, modelName)
        
        return DeviceCapabilities(
            tier = tier,
            ramMB = ramMB,
            cpuCores = cpuCores,
            screenDensity = screenMetrics.density,
            screenSizePx = Pair(screenMetrics.widthPixels, screenMetrics.heightPixels),
            screenSizeInches = calculateScreenSizeInches(screenMetrics),
            supportsVulkan = supportsVulkan(),
            batteryOptimizationNeeded = needsBatteryOptimization(tier),
            recommendedAnimationScale = getRecommendedAnimationScale(tier),
            recommendedImageQuality = getRecommendedImageQuality(tier),
            maxConcurrentOperations = getMaxConcurrentOperations(tier, cpuCores)
        )
    }
    
    private fun getTotalRAM(): Long {
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        return memInfo.totalMem / (1024 * 1024) // Convert to MB
    }
    
    private fun getScreenMetrics(): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        return displayMetrics
    }
    
    private fun calculateScreenSizeInches(metrics: DisplayMetrics): Float {
        val widthInches = metrics.widthPixels / metrics.xdpi
        val heightInches = metrics.heightPixels / metrics.ydpi
        return sqrt(widthInches.pow(2) + heightInches.pow(2))
    }
    
    private fun supportsVulkan(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.packageManager.hasSystemFeature("android.hardware.vulkan.version")
        } else false
    }
    
    private fun determinePerformanceTier(ramMB: Long, cpuCores: Int, modelName: String): PerformanceTier {
        // Verificar modelos específicos primero
        when {
            LOW_END_MODELS.any { modelName.contains(it) } -> return PerformanceTier.LOW_END
            HIGH_END_MODELS.any { modelName.contains(it) } -> return PerformanceTier.HIGH_END
        }
        
        // Clasificación por especificaciones
        return when {
            ramMB >= 12288 && cpuCores >= 8 -> PerformanceTier.PREMIUM // 12GB+ RAM, 8+ cores
            ramMB >= MID_END_RAM_MB && cpuCores >= MID_END_CPU_CORES -> PerformanceTier.HIGH_END
            ramMB >= LOW_END_RAM_MB && cpuCores >= LOW_END_CPU_CORES -> PerformanceTier.MID_END
            else -> PerformanceTier.LOW_END
        }
    }
    
    private fun needsBatteryOptimization(tier: PerformanceTier): Boolean {
        return tier == PerformanceTier.LOW_END || tier == PerformanceTier.MID_END
    }
    
    private fun getRecommendedAnimationScale(tier: PerformanceTier): Float {
        return when (tier) {
            PerformanceTier.LOW_END -> 0.5f    // Animaciones muy reducidas
            PerformanceTier.MID_END -> 0.8f    // Animaciones moderadas
            PerformanceTier.HIGH_END -> 1.0f   // Animaciones completas
            PerformanceTier.PREMIUM -> 1.2f    // Animaciones mejoradas
        }
    }
    
    private fun getRecommendedImageQuality(tier: PerformanceTier): ImageQuality {
        return when (tier) {
            PerformanceTier.LOW_END -> ImageQuality.BASIC
            PerformanceTier.MID_END -> ImageQuality.STANDARD
            PerformanceTier.HIGH_END -> ImageQuality.HIGH
            PerformanceTier.PREMIUM -> ImageQuality.ULTRA
        }
    }
    
    private fun getMaxConcurrentOperations(tier: PerformanceTier, cpuCores: Int): Int {
        val baseOperations = when (tier) {
            PerformanceTier.LOW_END -> 2
            PerformanceTier.MID_END -> 4
            PerformanceTier.HIGH_END -> 6
            PerformanceTier.PREMIUM -> 8
        }
        
        // Ajustar basado en núcleos de CPU disponibles
        return minOf(baseOperations, maxOf(1, cpuCores - 1))
    }
    
    /**
     * Obtiene información detallada para debugging
     */
    fun getDetailedDeviceInfo(): String {
        val capabilities = detectCapabilities()
        return buildString {
            appendLine("=== DEVICE PERFORMANCE ANALYSIS ===")
            appendLine("Model: ${Build.MODEL}")
            appendLine("Brand: ${Build.BRAND}")
            appendLine("Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Performance Tier: ${capabilities.tier}")
            appendLine("RAM: ${capabilities.ramMB} MB")
            appendLine("CPU Cores: ${capabilities.cpuCores}")
            appendLine("Screen: ${capabilities.screenSizePx.first}x${capabilities.screenSizePx.second}")
            appendLine("Screen Size: ${"%.1f".format(capabilities.screenSizeInches)}\"")
            appendLine("Density: ${capabilities.screenDensity}")
            appendLine("Vulkan Support: ${capabilities.supportsVulkan}")
            appendLine("Animation Scale: ${capabilities.recommendedAnimationScale}")
            appendLine("Image Quality: ${capabilities.recommendedImageQuality}")
            appendLine("Max Concurrent Ops: ${capabilities.maxConcurrentOperations}")
            appendLine("Battery Optimization: ${capabilities.batteryOptimizationNeeded}")
        }
    }
}