package com.example.Linageisp.performance

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.Linageisp.performance.core.DeviceCapabilityDetector

/**
 * EXTREMO RENDIMIENTO: Optimizaciones específicas por gama de dispositivo
 * Adapta la app automáticamente según las capacidades del hardware
 */
class DeviceTierOptimizer(private val context: Context) {
    
    private val deviceDetector = DeviceCapabilityDetector(context)
    
    enum class OptimizationLevel {
        MAXIMUM_PERFORMANCE,  // Gama alta - todos los efectos
        BALANCED,            // Gama media - efectos selectivos  
        BATTERY_SAVER        // Gama baja - mínimos efectos
    }
    
    val optimizationLevel: OptimizationLevel by lazy {
        // Simple device tier detection based on basic metrics
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRamMB = memoryInfo.totalMem / (1024 * 1024)
        val cpuCores = Runtime.getRuntime().availableProcessors()
        
        when {
            totalRamMB > 6144 && cpuCores >= 8 -> OptimizationLevel.MAXIMUM_PERFORMANCE // >6GB RAM + 8+ cores
            totalRamMB > 4096 && cpuCores >= 6 -> OptimizationLevel.BALANCED // >4GB RAM + 6+ cores
            else -> OptimizationLevel.BATTERY_SAVER
        }
    }
    
    /**
     * OPTIMIZACIÓN: Configuración de UI basada en hardware
     */
    @Composable
    fun getOptimizedUIConfig(): UIOptimizationConfig {
        return remember {
            when (optimizationLevel) {
                OptimizationLevel.MAXIMUM_PERFORMANCE -> UIOptimizationConfig(
                    enableBlur = true,
                    enableShadows = true,
                    enableComplexAnimations = true,
                    enableGradients = true,
                    imageQuality = ImageQuality.HIGH,
                    maxConcurrentAnimations = 8,
                    enableRippleEffects = true,
                    enableParallaxEffects = true
                )
                OptimizationLevel.BALANCED -> UIOptimizationConfig(
                    enableBlur = false,
                    enableShadows = true,
                    enableComplexAnimations = true,
                    enableGradients = true,
                    imageQuality = ImageQuality.MEDIUM,
                    maxConcurrentAnimations = 4,
                    enableRippleEffects = true,
                    enableParallaxEffects = false
                )
                OptimizationLevel.BATTERY_SAVER -> UIOptimizationConfig(
                    enableBlur = false,
                    enableShadows = false,
                    enableComplexAnimations = false,
                    enableGradients = false,
                    imageQuality = ImageQuality.LOW,
                    maxConcurrentAnimations = 2,
                    enableRippleEffects = false,
                    enableParallaxEffects = false
                )
            }
        }
    }
    
    /**
     * OPTIMIZACIÓN: Configuración de memoria
     */
    fun getMemoryConfig(): MemoryConfig {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        val availableMB = memoryInfo.availMem / (1024 * 1024)
        
        return when {
            availableMB > 1024 -> MemoryConfig( // >1GB disponible
                imageCacheSize = 256 * 1024 * 1024, // 256MB
                maxCachedImages = 200,
                preloadImages = true,
                enableLargeImageCache = true
            )
            availableMB > 512 -> MemoryConfig( // >512MB disponible
                imageCacheSize = 128 * 1024 * 1024, // 128MB
                maxCachedImages = 100,
                preloadImages = true,
                enableLargeImageCache = false
            )
            else -> MemoryConfig( // <512MB disponible
                imageCacheSize = 64 * 1024 * 1024, // 64MB
                maxCachedImages = 50,
                preloadImages = false,
                enableLargeImageCache = false
            )
        }
    }
    
    /**
     * OPTIMIZACIÓN: Configuración de red
     */
    fun getNetworkConfig(): NetworkConfig {
        return when (optimizationLevel) {
            OptimizationLevel.MAXIMUM_PERFORMANCE -> NetworkConfig(
                concurrentRequests = 6,
                timeoutMs = 10000,
                retryAttempts = 3,
                enablePreloading = true
            )
            OptimizationLevel.BALANCED -> NetworkConfig(
                concurrentRequests = 4,
                timeoutMs = 8000,
                retryAttempts = 2,
                enablePreloading = false
            )
            OptimizationLevel.BATTERY_SAVER -> NetworkConfig(
                concurrentRequests = 2,
                timeoutMs = 5000,
                retryAttempts = 1,
                enablePreloading = false
            )
        }
    }
    
    /**
     * OPTIMIZACIÓN: Configuración de animaciones
     */
    @Composable
    fun shouldUseAnimation(type: AnimationType): Boolean {
        val config = getOptimizedUIConfig()
        
        return remember(type, config) {
            when (type) {
                AnimationType.SIMPLE -> true
                AnimationType.COMPLEX -> config.enableComplexAnimations
                AnimationType.RIPPLE -> config.enableRippleEffects
                AnimationType.PARALLAX -> config.enableParallaxEffects
                AnimationType.BLUR -> config.enableBlur
            }
        }
    }
    
    /**
     * OPTIMIZACIÓN: Calidad de imagen adaptativa
     */
    fun getOptimizedImageSize(originalWidth: Int, originalHeight: Int): Pair<Int, Int> {
        val imageQuality = when (optimizationLevel) {
            OptimizationLevel.MAXIMUM_PERFORMANCE -> ImageQuality.HIGH
            OptimizationLevel.BALANCED -> ImageQuality.MEDIUM
            OptimizationLevel.BATTERY_SAVER -> ImageQuality.LOW
        }
        
        return when (imageQuality) {
            ImageQuality.HIGH -> originalWidth to originalHeight
            ImageQuality.MEDIUM -> {
                val scale = 0.75f
                (originalWidth * scale).toInt() to (originalHeight * scale).toInt()
            }
            ImageQuality.LOW -> {
                val scale = 0.5f
                (originalWidth * scale).toInt() to (originalHeight * scale).toInt()
            }
        }
    }
}

/**
 * Configuraciones de optimización
 */
data class UIOptimizationConfig(
    val enableBlur: Boolean,
    val enableShadows: Boolean, 
    val enableComplexAnimations: Boolean,
    val enableGradients: Boolean,
    val imageQuality: ImageQuality,
    val maxConcurrentAnimations: Int,
    val enableRippleEffects: Boolean,
    val enableParallaxEffects: Boolean
)

data class MemoryConfig(
    val imageCacheSize: Long,
    val maxCachedImages: Int,
    val preloadImages: Boolean,
    val enableLargeImageCache: Boolean
)

data class NetworkConfig(
    val concurrentRequests: Int,
    val timeoutMs: Long,
    val retryAttempts: Int,
    val enablePreloading: Boolean
)

enum class ImageQuality {
    HIGH, MEDIUM, LOW
}

enum class AnimationType {
    SIMPLE, COMPLEX, RIPPLE, PARALLAX, BLUR
}

/**
 * COMPOSABLES OPTIMIZADOS POR DISPOSITIVO
 */
val LocalDeviceTierOptimizer = compositionLocalOf<DeviceTierOptimizer> {
    error("DeviceTierOptimizer not provided")
}

@Composable
fun ProvideDeviceTierOptimizer(
    optimizer: DeviceTierOptimizer,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDeviceTierOptimizer provides optimizer
    ) {
        content()
    }
}

/**
 * UTILITY FUNCTIONS
 */
@Composable
fun adaptiveColor(
    highEnd: Color,
    midTier: Color,
    lowEnd: Color
): Color {
    val optimizer = LocalDeviceTierOptimizer.current
    return remember(optimizer.optimizationLevel) {
        when (optimizer.optimizationLevel) {
            DeviceTierOptimizer.OptimizationLevel.MAXIMUM_PERFORMANCE -> highEnd
            DeviceTierOptimizer.OptimizationLevel.BALANCED -> midTier
            DeviceTierOptimizer.OptimizationLevel.BATTERY_SAVER -> lowEnd
        }
    }
}

@Composable
fun adaptiveElevation(
    highEnd: androidx.compose.ui.unit.Dp,
    midTier: androidx.compose.ui.unit.Dp,
    lowEnd: androidx.compose.ui.unit.Dp = 0.dp
): androidx.compose.ui.unit.Dp {
    val optimizer = LocalDeviceTierOptimizer.current
    return remember(optimizer.optimizationLevel) {
        when (optimizer.optimizationLevel) {
            DeviceTierOptimizer.OptimizationLevel.MAXIMUM_PERFORMANCE -> highEnd
            DeviceTierOptimizer.OptimizationLevel.BALANCED -> midTier 
            DeviceTierOptimizer.OptimizationLevel.BATTERY_SAVER -> lowEnd
        }
    }
}