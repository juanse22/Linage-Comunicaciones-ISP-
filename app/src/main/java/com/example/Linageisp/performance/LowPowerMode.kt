package com.example.Linageisp.performance

import android.content.Context
import android.os.BatteryManager
import android.os.PowerManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Sistema de modo bajo consumo para dispositivos antiguos y con batería limitada
 * Ajusta automáticamente configuraciones para preservar batería y rendimiento
 */
class LowPowerMode private constructor(
    private val context: Context,
    private val deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities
) {
    
    companion object {
        @Volatile
        private var INSTANCE: LowPowerMode? = null
        
        fun getInstance(
            context: Context,
            deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities
        ): LowPowerMode {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LowPowerMode(
                    context.applicationContext,
                    deviceCapabilities
                ).also { INSTANCE = it }
            }
        }
        
        // Umbrales de batería
        private const val BATTERY_LOW_THRESHOLD = 20f // 20%
        private const val BATTERY_CRITICAL_THRESHOLD = 10f // 10%
        private const val BATTERY_VERY_LOW_THRESHOLD = 5f // 5%
        
        // Configuraciones por modo
        private val POWER_SAVE_CONFIG = LowPowerConfig(
            animationScale = 0.3f,
            imageQuality = DeviceCapabilityDetector.ImageQuality.BASIC,
            maxConcurrentOperations = 2,
            frameRateTarget = 30,
            enableBackgroundProcessing = false,
            useHardwareAcceleration = false,
            cacheSize = 0.1 // 10% del normal
        )
        
        private val EXTREME_POWER_SAVE_CONFIG = LowPowerConfig(
            animationScale = 0.1f,
            imageQuality = DeviceCapabilityDetector.ImageQuality.BASIC,
            maxConcurrentOperations = 1,
            frameRateTarget = 20,
            enableBackgroundProcessing = false,
            useHardwareAcceleration = false,
            cacheSize = 0.05 // 5% del normal
        )
    }
    
    data class LowPowerConfig(
        val animationScale: Float,
        val imageQuality: DeviceCapabilityDetector.ImageQuality,
        val maxConcurrentOperations: Int,
        val frameRateTarget: Int,
        val enableBackgroundProcessing: Boolean,
        val useHardwareAcceleration: Boolean,
        val cacheSize: Double // Factor multiplicador para el tamaño de caché
    )
    
    data class BatteryInfo(
        val level: Float,
        val isCharging: Boolean,
        val isPowerSaveMode: Boolean,
        val temperature: Float,
        val health: BatteryHealth
    )
    
    enum class BatteryHealth {
        GOOD, OVERHEAT, DEAD, OVER_VOLTAGE, UNSPECIFIED_FAILURE, COLD, UNKNOWN
    }
    
    enum class PowerMode {
        NORMAL,           // Batería > 20%, no power save
        POWER_SAVE,       // Batería <= 20% o power save activado
        EXTREME_SAVE,     // Batería <= 10%
        EMERGENCY         // Batería <= 5%
    }
    
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    
    private val monitoringScope = CoroutineScope(
        SupervisorJob() + 
        Dispatchers.Default + 
        CoroutineName("LowPowerMode")
    )
    
    // Estado actual del modo de energía
    private val _powerMode = MutableStateFlow(PowerMode.NORMAL)
    val powerMode: StateFlow<PowerMode> = _powerMode.asStateFlow()
    
    private val _batteryInfo = MutableStateFlow(getCurrentBatteryInfo())
    val batteryInfo: StateFlow<BatteryInfo> = _batteryInfo.asStateFlow()
    
    private val _isLowPowerActive = MutableStateFlow(false)
    val isLowPowerActive: StateFlow<Boolean> = _isLowPowerActive.asStateFlow()
    
    // Configuración activa
    private val _activeConfig = MutableStateFlow(getConfigForMode(PowerMode.NORMAL))
    val activeConfig: StateFlow<LowPowerConfig> = _activeConfig.asStateFlow()
    
    init {
        startBatteryMonitoring()
    }
    
    /**
     * Inicia el monitoreo de batería y modo de energía
     */
    private fun startBatteryMonitoring() {
        monitoringScope.launch {
            while (isActive) {
                val batteryInfo = getCurrentBatteryInfo()
                _batteryInfo.value = batteryInfo
                
                val newPowerMode = determinePowerMode(batteryInfo)
                if (newPowerMode != _powerMode.value) {
                    _powerMode.value = newPowerMode
                    _activeConfig.value = getConfigForMode(newPowerMode)
                    _isLowPowerActive.value = newPowerMode != PowerMode.NORMAL
                }
                
                delay(5000) // Verificar cada 5 segundos
            }
        }
    }
    
    /**
     * Obtiene información actual de la batería
     */
    private fun getCurrentBatteryInfo(): BatteryInfo {
        val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toFloat()
        val isCharging = batteryManager.isCharging
        val isPowerSaveMode = powerManager.isPowerSaveMode
        val temperature = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 10f
        
        val healthInt = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        val health = when (healthInt) {
            BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealth.GOOD
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealth.OVERHEAT
            BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealth.DEAD
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealth.OVER_VOLTAGE
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BatteryHealth.UNSPECIFIED_FAILURE
            BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealth.COLD
            else -> BatteryHealth.UNKNOWN
        }
        
        return BatteryInfo(level, isCharging, isPowerSaveMode, temperature, health)
    }
    
    /**
     * Determina el modo de energía basado en la información de batería y dispositivo
     */
    private fun determinePowerMode(batteryInfo: BatteryInfo): PowerMode {
        // Dispositivos LOW_END siempre en modo ahorro si batería < 50%
        if (deviceCapabilities.tier == DeviceCapabilityDetector.PerformanceTier.LOW_END) {
            return when {
                batteryInfo.level <= BATTERY_VERY_LOW_THRESHOLD -> PowerMode.EMERGENCY
                batteryInfo.level <= BATTERY_CRITICAL_THRESHOLD -> PowerMode.EXTREME_SAVE
                batteryInfo.level <= 50f || batteryInfo.isPowerSaveMode -> PowerMode.POWER_SAVE
                else -> PowerMode.NORMAL
            }
        }
        
        // Lógica estándar para otros dispositivos
        return when {
            batteryInfo.level <= BATTERY_VERY_LOW_THRESHOLD -> PowerMode.EMERGENCY
            batteryInfo.level <= BATTERY_CRITICAL_THRESHOLD -> PowerMode.EXTREME_SAVE
            batteryInfo.level <= BATTERY_LOW_THRESHOLD || batteryInfo.isPowerSaveMode -> PowerMode.POWER_SAVE
            batteryInfo.isCharging -> PowerMode.NORMAL
            else -> PowerMode.NORMAL
        }
    }
    
    /**
     * Obtiene configuración para un modo específico
     */
    private fun getConfigForMode(mode: PowerMode): LowPowerConfig {
        return when (mode) {
            PowerMode.NORMAL -> LowPowerConfig(
                animationScale = deviceCapabilities.recommendedAnimationScale,
                imageQuality = deviceCapabilities.recommendedImageQuality,
                maxConcurrentOperations = deviceCapabilities.maxConcurrentOperations,
                frameRateTarget = when (deviceCapabilities.tier) {
                    DeviceCapabilityDetector.PerformanceTier.LOW_END -> 30
                    DeviceCapabilityDetector.PerformanceTier.MID_END -> 45
                    DeviceCapabilityDetector.PerformanceTier.HIGH_END -> 60
                    DeviceCapabilityDetector.PerformanceTier.PREMIUM -> 90
                },
                enableBackgroundProcessing = true,
                useHardwareAcceleration = true,
                cacheSize = 1.0
            )
            
            PowerMode.POWER_SAVE -> POWER_SAVE_CONFIG.copy(
                animationScale = deviceCapabilities.recommendedAnimationScale * 0.5f
            )
            
            PowerMode.EXTREME_SAVE, PowerMode.EMERGENCY -> EXTREME_POWER_SAVE_CONFIG.copy(
                animationScale = if (mode == PowerMode.EMERGENCY) 0.0f else 0.1f,
                frameRateTarget = if (mode == PowerMode.EMERGENCY) 15 else 20
            )
        }
    }
    
    /**
     * Fuerza un modo de energía específico (para testing o configuración manual)
     */
    fun setForcedPowerMode(mode: PowerMode?) {
        monitoringScope.launch {
            if (mode != null) {
                _powerMode.value = mode
                _activeConfig.value = getConfigForMode(mode)
                _isLowPowerActive.value = mode != PowerMode.NORMAL
            } else {
                // Reanudar detección automática
                val batteryInfo = getCurrentBatteryInfo()
                val detectedMode = determinePowerMode(batteryInfo)
                _powerMode.value = detectedMode
                _activeConfig.value = getConfigForMode(detectedMode)
                _isLowPowerActive.value = detectedMode != PowerMode.NORMAL
            }
        }
    }
    
    /**
     * Verifica si se debe aplicar una optimización específica
     */
    fun shouldApplyOptimization(optimization: OptimizationType): Boolean {
        val config = _activeConfig.value
        
        return when (optimization) {
            OptimizationType.REDUCE_ANIMATIONS -> config.animationScale < 0.5f
            OptimizationType.REDUCE_IMAGE_QUALITY -> config.imageQuality == DeviceCapabilityDetector.ImageQuality.BASIC
            OptimizationType.LIMIT_CONCURRENT_OPS -> config.maxConcurrentOperations <= 2
            OptimizationType.DISABLE_BACKGROUND_PROCESSING -> !config.enableBackgroundProcessing
            OptimizationType.DISABLE_HARDWARE_ACCELERATION -> !config.useHardwareAcceleration
            OptimizationType.REDUCE_CACHE_SIZE -> config.cacheSize < 0.5
            OptimizationType.LIMIT_FRAME_RATE -> config.frameRateTarget <= 30
        }
    }
    
    enum class OptimizationType {
        REDUCE_ANIMATIONS,
        REDUCE_IMAGE_QUALITY,
        LIMIT_CONCURRENT_OPS,
        DISABLE_BACKGROUND_PROCESSING,
        DISABLE_HARDWARE_ACCELERATION,
        REDUCE_CACHE_SIZE,
        LIMIT_FRAME_RATE
    }
    
    /**
     * Obtiene recomendaciones específicas para el estado actual
     */
    fun getCurrentRecommendations(): List<String> {
        val mode = _powerMode.value
        val batteryInfo = _batteryInfo.value
        val recommendations = mutableListOf<String>()
        
        when (mode) {
            PowerMode.NORMAL -> {
                recommendations.add("Funcionamiento normal")
                if (deviceCapabilities.tier == DeviceCapabilityDetector.PerformanceTier.LOW_END) {
                    recommendations.add("Dispositivo de gama baja: algunas optimizaciones activas")
                }
            }
            
            PowerMode.POWER_SAVE -> {
                recommendations.add("Modo ahorro de batería activado")
                recommendations.add("Animaciones reducidas")
                recommendations.add("Calidad de imagen optimizada")
            }
            
            PowerMode.EXTREME_SAVE -> {
                recommendations.add("Ahorro extremo de batería")
                recommendations.add("Animaciones mínimas")
                recommendations.add("Solo operaciones esenciales")
                recommendations.add("Considera cargar el dispositivo")
            }
            
            PowerMode.EMERGENCY -> {
                recommendations.add("⚠️ BATERÍA CRÍTICA")
                recommendations.add("Funcionalidad mínima activada")
                recommendations.add("Carga el dispositivo inmediatamente")
                recommendations.add("Solo funciones esenciales disponibles")
            }
        }
        
        // Recomendaciones adicionales basadas en temperatura
        if (batteryInfo.temperature > 40f) {
            recommendations.add("🌡️ Dispositivo caliente: rendimiento reducido")
        }
        
        return recommendations
    }
    
    /**
     * Limpia recursos
     */
    fun cleanup() {
        monitoringScope.cancel()
    }
}

/**
 * Composable para integrar el modo bajo consumo con Compose
 */
@Composable
fun rememberLowPowerMode(): LowPowerMode {
    val context = LocalContext.current
    val deviceCapabilities = remember { DeviceCapabilityDetector(context).detectCapabilities() }
    
    return remember {
        LowPowerMode.getInstance(context, deviceCapabilities)
    }
}

/**
 * Hook para obtener configuraciones de animación adaptativas
 */
@Composable
fun rememberAdaptiveAnimationSpec(
    lowPowerMode: LowPowerMode = rememberLowPowerMode(),
    baseDuration: Int = 300
): AnimationSpec<Float> {
    val config by lowPowerMode.activeConfig.collectAsState()
    
    return remember(config.animationScale) {
        when {
            config.animationScale <= 0.1f -> snap() // Sin animaciones
            config.animationScale <= 0.3f -> tween(
                durationMillis = (baseDuration * 0.3f).toInt(),
                easing = LinearEasing
            )
            config.animationScale <= 0.5f -> tween(
                durationMillis = (baseDuration * 0.5f).toInt(),
                easing = FastOutSlowInEasing
            )
            else -> tween(
                durationMillis = (baseDuration * config.animationScale).toInt(),
                easing = FastOutSlowInEasing
            )
        }
    }
}

/**
 * Composable para mostrar indicador de modo bajo consumo
 */
@Composable
fun LowPowerModeIndicator(
    lowPowerMode: LowPowerMode = rememberLowPowerMode(),
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val isLowPowerActive by lowPowerMode.isLowPowerActive.collectAsState()
    val powerMode by lowPowerMode.powerMode.collectAsState()
    val batteryInfo by lowPowerMode.batteryInfo.collectAsState()
    
    if (isLowPowerActive) {
        androidx.compose.material3.Card(
            modifier = modifier,
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = when (powerMode) {
                    LowPowerMode.PowerMode.POWER_SAVE -> androidx.compose.ui.graphics.Color.Yellow.copy(alpha = 0.2f)
                    LowPowerMode.PowerMode.EXTREME_SAVE -> androidx.compose.ui.graphics.Color.Orange.copy(alpha = 0.2f)
                    LowPowerMode.PowerMode.EMERGENCY -> androidx.compose.ui.graphics.Color.Red.copy(alpha = 0.2f)
                    else -> androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.1f)
                }
            )
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = androidx.compose.ui.Modifier.padding(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                androidx.compose.material3.Text(
                    text = when (powerMode) {
                        LowPowerMode.PowerMode.POWER_SAVE -> "🔋"
                        LowPowerMode.PowerMode.EXTREME_SAVE -> "⚡"
                        LowPowerMode.PowerMode.EMERGENCY -> "🚨"
                        else -> "💡"
                    },
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
                
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(4.dp))
                
                androidx.compose.material3.Text(
                    text = "${batteryInfo.level.toInt()}%",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}