package com.example.Linageisp.performance.core

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Detector avanzado de capacidades del dispositivo con métricas dinámicas
 */
class DeviceCapabilityDetector(private val context: Context) {
    
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    
    private val _capabilities = MutableStateFlow<DeviceCapabilities?>(null)
    val capabilities: StateFlow<DeviceCapabilities?> = _capabilities.asStateFlow()
    
    companion object {
        // Umbrales para clasificación
        private const val LOW_END_RAM_GB = 3
        private const val MID_END_RAM_GB = 6
        private const val HIGH_END_RAM_GB = 8
        
        private const val LOW_END_CPU_CORES = 4
        private const val MID_END_CPU_CORES = 6
        private const val HIGH_END_CPU_CORES = 8
        
        private const val LOW_END_API_LEVEL = 26
        private const val MID_END_API_LEVEL = 29
        private const val HIGH_END_API_LEVEL = 31
    }
    
    data class DeviceCapabilities(
        val tier: PerformanceTier,
        val ramGB: Int,
        val cpuCores: Int,
        val apiLevel: Int,
        val hasHardwareAcceleration: Boolean,
        val supportedFeatures: Set<DeviceFeature>,
        val thermalCapabilities: ThermalCapabilities,
        val displayMetrics: DisplayMetrics,
        val recommendedSettings: PerformanceSettings
    )
    
    data class ThermalCapabilities(
        val supportsThermalAPI: Boolean,
        val maxSustainedPerformance: Float,
        val thermalThrottlingTemp: Float
    )
    
    data class DisplayMetrics(
        val widthPx: Int,
        val heightPx: Int,
        val densityDpi: Int,
        val refreshRate: Float
    )
    
    data class PerformanceSettings(
        val targetFps: Int,
        val recommendedAnimationScale: Float,
        val recommendedImageQuality: ImageQuality,
        val maxConcurrentOperations: Int,
        val enableAdvancedEffects: Boolean,
        val cacheSize: Int // MB
    )
    
    enum class PerformanceTier(val displayName: String, val priority: Int) {
        LOW_END("Básico", 1),
        MID_END("Medio", 2), 
        HIGH_END("Alto", 3),
        PREMIUM("Premium", 4)
    }
    
    enum class DeviceFeature {
        VULKAN_API,
        HARDWARE_ACCELERATION,
        HIGH_REFRESH_DISPLAY,
        THERMAL_MANAGEMENT,
        ADVANCED_MEMORY_MANAGEMENT,
        GPU_COMPUTE,
        NEURAL_PROCESSING
    }
    
    enum class ImageQuality {
        BASIC,
        STANDARD, 
        HIGH,
        ULTRA
    }
    
    /**
     * Detecta las capacidades del dispositivo de forma asíncrona
     */
    suspend fun detectCapabilities(): DeviceCapabilities {
        val ramGB = getTotalRAMGB()
        val cpuCores = Runtime.getRuntime().availableProcessors()
        val apiLevel = Build.VERSION.SDK_INT
        val hasHWAccel = hasHardwareAcceleration()
        val features = detectSupportedFeatures()
        val thermal = detectThermalCapabilities()
        val display = getDisplayMetrics()
        
        val tier = classifyPerformanceTier(ramGB, cpuCores, apiLevel, features)
        val settings = generateRecommendedSettings(tier, features)
        
        val capabilities = DeviceCapabilities(
            tier = tier,
            ramGB = ramGB,
            cpuCores = cpuCores,
            apiLevel = apiLevel,
            hasHardwareAcceleration = hasHWAccel,
            supportedFeatures = features,
            thermalCapabilities = thermal,
            displayMetrics = display,
            recommendedSettings = settings
        )
        
        _capabilities.value = capabilities
        return capabilities
    }
    
    private fun getTotalRAMGB(): Int {
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        return (memInfo.totalMem / (1024 * 1024 * 1024)).toInt()
    }
    
    private fun hasHardwareAcceleration(): Boolean {
        return try {
            val pm = context.packageManager
            pm.hasSystemFeature("android.hardware.vulkan.level") ||
            pm.hasSystemFeature("android.software.opengles.deqp.level")
        } catch (e: Exception) {
            false
        }
    }
    
    private fun detectSupportedFeatures(): Set<DeviceFeature> {
        val features = mutableSetOf<DeviceFeature>()
        val pm = context.packageManager
        
        if (pm.hasSystemFeature("android.hardware.vulkan.level")) {
            features.add(DeviceFeature.VULKAN_API)
        }
        
        if (hasHardwareAcceleration()) {
            features.add(DeviceFeature.HARDWARE_ACCELERATION)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            features.add(DeviceFeature.THERMAL_MANAGEMENT)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            features.add(DeviceFeature.ADVANCED_MEMORY_MANAGEMENT)
        }
        
        // Detectar pantallas de alta frecuencia
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = context.display
            if (display != null && display.refreshRate > 60f) {
                features.add(DeviceFeature.HIGH_REFRESH_DISPLAY)
            }
        } else {
            // Fallback for older Android versions
            try {
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display = windowManager.defaultDisplay
                if (display.refreshRate > 60f) {
                    features.add(DeviceFeature.HIGH_REFRESH_DISPLAY)
                }
            } catch (e: Exception) {
                // Ignore if we can't detect refresh rate
            }
        }
        
        return features
    }
    
    private fun detectThermalCapabilities(): ThermalCapabilities {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val thermalService = context.getSystemService("thermal")
            ThermalCapabilities(
                supportsThermalAPI = true,
                maxSustainedPerformance = 1.0f, // Placeholder
                thermalThrottlingTemp = 70f
            )
        } else {
            ThermalCapabilities(
                supportsThermalAPI = false,
                maxSustainedPerformance = 0.8f,
                thermalThrottlingTemp = 65f
            )
        }
    }
    
    private fun getDisplayMetrics(): DisplayMetrics {
        val displayMetrics = context.resources.displayMetrics
        
        val refreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.refreshRate ?: 60f
        } else {
            try {
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                windowManager.defaultDisplay.refreshRate
            } catch (e: Exception) {
                60f
            }
        }
        
        return DisplayMetrics(
            widthPx = displayMetrics.widthPixels,
            heightPx = displayMetrics.heightPixels,
            densityDpi = displayMetrics.densityDpi,
            refreshRate = refreshRate
        )
    }
    
    private fun classifyPerformanceTier(
        ramGB: Int,
        cpuCores: Int,
        apiLevel: Int,
        features: Set<DeviceFeature>
    ): PerformanceTier {
        var score = 0
        
        // RAM scoring
        score += when {
            ramGB >= HIGH_END_RAM_GB -> 3
            ramGB >= MID_END_RAM_GB -> 2
            ramGB >= LOW_END_RAM_GB -> 1
            else -> 0
        }
        
        // CPU scoring
        score += when {
            cpuCores >= HIGH_END_CPU_CORES -> 3
            cpuCores >= MID_END_CPU_CORES -> 2
            cpuCores >= LOW_END_CPU_CORES -> 1
            else -> 0
        }
        
        // API Level scoring
        score += when {
            apiLevel >= HIGH_END_API_LEVEL -> 3
            apiLevel >= MID_END_API_LEVEL -> 2
            apiLevel >= LOW_END_API_LEVEL -> 1
            else -> 0
        }
        
        // Feature bonus
        if (features.contains(DeviceFeature.VULKAN_API)) score += 2
        if (features.contains(DeviceFeature.HIGH_REFRESH_DISPLAY)) score += 1
        if (features.contains(DeviceFeature.HARDWARE_ACCELERATION)) score += 1
        
        return when {
            score >= 9 -> PerformanceTier.PREMIUM
            score >= 6 -> PerformanceTier.HIGH_END
            score >= 3 -> PerformanceTier.MID_END
            else -> PerformanceTier.LOW_END
        }
    }
    
    private fun generateRecommendedSettings(
        tier: PerformanceTier,
        features: Set<DeviceFeature>
    ): PerformanceSettings {
        return when (tier) {
            PerformanceTier.LOW_END -> PerformanceSettings(
                targetFps = 30,
                recommendedAnimationScale = 0.3f,
                recommendedImageQuality = ImageQuality.BASIC,
                maxConcurrentOperations = 2,
                enableAdvancedEffects = false,
                cacheSize = 32
            )
            PerformanceTier.MID_END -> PerformanceSettings(
                targetFps = 45,
                recommendedAnimationScale = 0.6f,
                recommendedImageQuality = ImageQuality.STANDARD,
                maxConcurrentOperations = 4,
                enableAdvancedEffects = false,
                cacheSize = 64
            )
            PerformanceTier.HIGH_END -> PerformanceSettings(
                targetFps = if (features.contains(DeviceFeature.HIGH_REFRESH_DISPLAY)) 90 else 60,
                recommendedAnimationScale = 0.8f,
                recommendedImageQuality = ImageQuality.HIGH,
                maxConcurrentOperations = 6,
                enableAdvancedEffects = true,
                cacheSize = 128
            )
            PerformanceTier.PREMIUM -> PerformanceSettings(
                targetFps = if (features.contains(DeviceFeature.HIGH_REFRESH_DISPLAY)) 120 else 60,
                recommendedAnimationScale = 1.0f,
                recommendedImageQuality = ImageQuality.ULTRA,
                maxConcurrentOperations = 8,
                enableAdvancedEffects = true,
                cacheSize = 256
            )
        }
    }
    
    /**
     * Composable hook para acceder a las capacidades del dispositivo
     */
    @Composable
    fun rememberDeviceCapabilities(): DeviceCapabilities? {
        val capabilities by capabilities.collectAsState()
        
        LaunchedEffect(Unit) {
            if (capabilities == null) {
                detectCapabilities()
            }
        }
        
        return capabilities
    }
}

/**
 * Composable convenience function
 */
@Composable
fun rememberDeviceCapabilities(): DeviceCapabilityDetector.DeviceCapabilities? {
    val context = LocalContext.current
    val detector = remember { DeviceCapabilityDetector(context) }
    return detector.rememberDeviceCapabilities()
}