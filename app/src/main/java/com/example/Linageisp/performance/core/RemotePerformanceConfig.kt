package com.example.Linageisp.performance.core

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.JSONObject

/**
 * Configuración remota de performance para ajustar parámetros sin actualizar la app
 */
class RemotePerformanceConfig private constructor(
    private val context: Context
) {
    
    companion object {
        @Volatile
        private var INSTANCE: RemotePerformanceConfig? = null
        private const val PREFS_NAME = "performance_config"
        private const val CONFIG_KEY = "remote_config"
        private const val LAST_FETCH_KEY = "last_fetch_time"
        private const val FETCH_INTERVAL_MS = 30 * 60 * 1000L // 30 minutos
        
        fun getInstance(context: Context): RemotePerformanceConfig {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RemotePerformanceConfig(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    data class PerformanceConfig(
        val version: Int = 1,
        val enableExperimentalFeatures: Boolean = false,
        val frameRateTargets: Map<String, Int> = mapOf(
            "LOW_END" to 30,
            "MID_END" to 45,
            "HIGH_END" to 60,
            "PREMIUM" to 90
        ),
        val memoryThresholds: MemoryThresholds = MemoryThresholds(),
        val animationSettings: AnimationSettings = AnimationSettings(),
        val cacheSettings: CacheSettings = CacheSettings(),
        val loggingSettings: LoggingSettings = LoggingSettings(),
        val experimentFlags: Map<String, Boolean> = emptyMap(),
        val performanceOverrides: Map<String, String> = emptyMap()
    )
    
    data class MemoryThresholds(
        val warningThreshold: Float = 0.75f,
        val criticalThreshold: Float = 0.9f,
        val gcTriggerThreshold: Float = 0.85f,
        val emergencyCleanupThreshold: Float = 0.95f
    )
    
    data class AnimationSettings(
        val enableAnimationsOnLowEnd: Boolean = false,
        val animationScaleMultipliers: Map<String, Float> = mapOf(
            "LOW_END" to 0.3f,
            "MID_END" to 0.6f,
            "HIGH_END" to 0.8f,
            "PREMIUM" to 1.0f
        ),
        val minAnimationDuration: Long = 150L,
        val maxAnimationDuration: Long = 1000L
    )
    
    data class CacheSettings(
        val maxCacheSizeMB: Map<String, Int> = mapOf(
            "LOW_END" to 32,
            "MID_END" to 64,
            "HIGH_END" to 128,
            "PREMIUM" to 256
        ),
        val cacheTTLMinutes: Int = 60,
        val enableSmartPreloading: Boolean = true,
        val maxPreloadItems: Int = 20
    )
    
    data class LoggingSettings(
        val enableDetailedLogging: Boolean = false,
        val logLevel: String = "INFO",
        val enableFileLogging: Boolean = false,
        val maxLogEntries: Int = 1000,
        val enableMetricsCollection: Boolean = true
    )
    
    // Simplified config without JSON serialization
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val logger = PerformanceLogger.getInstance()
    
    private val configScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val _config = MutableStateFlow(getDefaultConfig())
    val config: StateFlow<PerformanceConfig> = _config.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadLocalConfig()
        startPeriodicFetch()
    }
    
    private fun getDefaultConfig(): PerformanceConfig = PerformanceConfig()
    
    private fun loadLocalConfig() {
        try {
            val configJson = prefs.getString(CONFIG_KEY, null)
            if (configJson != null) {
                // Load from simple storage - just use defaults for now
                _config.value = getDefaultConfig()
                logger.info("CONFIG", "Local config loaded successfully", mapOf(
                    "version" to _config.value.version
                ))
            }
        } catch (e: Exception) {
            logger.error("CONFIG", "Failed to load local config", e)
            _config.value = getDefaultConfig()
        }
    }
    
    private fun saveLocalConfig(config: PerformanceConfig) {
        try {
            // Save to simple storage - simplified for now
            val configJson = "version=${config.version}"
            prefs.edit()
                .putString(CONFIG_KEY, configJson)
                .putLong(LAST_FETCH_KEY, System.currentTimeMillis())
                .apply()
            
            logger.info("CONFIG", "Config saved locally", mapOf(
                "version" to config.version
            ))
        } catch (e: Exception) {
            logger.error("CONFIG", "Failed to save local config", e)
        }
    }
    
    private fun startPeriodicFetch() {
        configScope.launch {
            while (isActive) {
                val lastFetch = prefs.getLong(LAST_FETCH_KEY, 0)
                val shouldFetch = System.currentTimeMillis() - lastFetch > FETCH_INTERVAL_MS
                
                if (shouldFetch) {
                    fetchRemoteConfig()
                }
                
                delay(FETCH_INTERVAL_MS)
            }
        }
    }
    
    /**
     * Simula fetching de configuración remota (Firebase Remote Config, etc.)
     */
    suspend fun fetchRemoteConfig(): Boolean {
        return configScope.async {
            try {
                _isLoading.value = true
                logger.info("CONFIG", "Fetching remote config")
                
                // Simular delay de red
                delay(1000)
                
                // En producción, esto sería una llamada HTTP a Firebase Remote Config u otro servicio
                val remoteConfig = simulateRemoteConfigFetch()
                
                if (remoteConfig.version > _config.value.version) {
                    _config.value = remoteConfig
                    saveLocalConfig(remoteConfig)
                    
                    logger.info("CONFIG", "Remote config updated", mapOf(
                        "old_version" to _config.value.version,
                        "new_version" to remoteConfig.version
                    ))
                    
                    true
                } else {
                    logger.info("CONFIG", "Remote config up to date")
                    false
                }
            } catch (e: Exception) {
                logger.error("CONFIG", "Failed to fetch remote config", e)
                false
            } finally {
                _isLoading.value = false
            }
        }.await()
    }
    
    private fun simulateRemoteConfigFetch(): PerformanceConfig {
        // Simulación de respuesta del servidor con algunas optimizaciones
        return PerformanceConfig(
            version = _config.value.version + 1,
            enableExperimentalFeatures = true,
            frameRateTargets = mapOf(
                "LOW_END" to 30,
                "MID_END" to 50,  // Aumentado
                "HIGH_END" to 65, // Aumentado
                "PREMIUM" to 120  // Aumentado
            ),
            memoryThresholds = MemoryThresholds(
                warningThreshold = 0.7f,    // Más conservador
                criticalThreshold = 0.85f,  // Más conservador
                gcTriggerThreshold = 0.8f,
                emergencyCleanupThreshold = 0.9f
            ),
            animationSettings = AnimationSettings(
                enableAnimationsOnLowEnd = true, // Habilitado
                animationScaleMultipliers = mapOf(
                    "LOW_END" to 0.5f,    // Aumentado
                    "MID_END" to 0.7f,    // Aumentado
                    "HIGH_END" to 0.9f,   // Aumentado
                    "PREMIUM" to 1.0f
                )
            ),
            experimentFlags = mapOf(
                "enable_gpu_metrics" to true,
                "enable_smart_cache" to true,
                "enable_predictive_loading" to false
            )
        )
    }
    
    /**
     * Obtiene un valor de configuración con fallback
     */
    fun <T> getConfigValue(key: String, defaultValue: T): T {
        return try {
            when (defaultValue) {
                is Boolean -> _config.value.experimentFlags[key] as? T ?: defaultValue
                is String -> _config.value.performanceOverrides[key] as? T ?: defaultValue
                else -> defaultValue
            }
        } catch (e: Exception) {
            logger.warn("CONFIG", "Failed to get config value for key: $key", mapOf("error" to (e.message ?: "unknown")))
            defaultValue
        }
    }
    
    /**
     * Verifica si una feature experimental está habilitada
     */
    fun isExperimentalFeatureEnabled(featureName: String): Boolean {
        return _config.value.enableExperimentalFeatures && 
               _config.value.experimentFlags[featureName] == true
    }
    
    /**
     * Obtiene el target FPS para un tier específico
     */
    fun getTargetFpsForTier(tier: DeviceCapabilityDetector.PerformanceTier): Int {
        return _config.value.frameRateTargets[tier.name] ?: when (tier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> 30
            DeviceCapabilityDetector.PerformanceTier.MID_END -> 45
            DeviceCapabilityDetector.PerformanceTier.HIGH_END -> 60
            DeviceCapabilityDetector.PerformanceTier.PREMIUM -> 90
        }
    }
    
    /**
     * Obtiene la escala de animación para un tier específico
     */
    fun getAnimationScaleForTier(tier: DeviceCapabilityDetector.PerformanceTier): Float {
        return _config.value.animationSettings.animationScaleMultipliers[tier.name] ?: when (tier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> 0.3f
            DeviceCapabilityDetector.PerformanceTier.MID_END -> 0.6f
            DeviceCapabilityDetector.PerformanceTier.HIGH_END -> 0.8f
            DeviceCapabilityDetector.PerformanceTier.PREMIUM -> 1.0f
        }
    }
    
    /**
     * Obtiene el tamaño de caché para un tier específico
     */
    fun getCacheSizeForTier(tier: DeviceCapabilityDetector.PerformanceTier): Int {
        return _config.value.cacheSettings.maxCacheSizeMB[tier.name] ?: when (tier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> 32
            DeviceCapabilityDetector.PerformanceTier.MID_END -> 64
            DeviceCapabilityDetector.PerformanceTier.HIGH_END -> 128
            DeviceCapabilityDetector.PerformanceTier.PREMIUM -> 256
        }
    }
    
    /**
     * Override temporal de configuración (no se persiste)
     */
    fun setTemporaryOverride(key: String, value: Any) {
        val currentConfig = _config.value
        val newOverrides = currentConfig.performanceOverrides.toMutableMap()
        newOverrides[key] = value.toString()
        
        _config.value = currentConfig.copy(performanceOverrides = newOverrides)
        
        logger.info("CONFIG", "Temporary override set", mapOf(
            "key" to key,
            "value" to value.toString()
        ))
    }
    
    /**
     * Limpia overrides temporales
     */
    fun clearTemporaryOverrides() {
        val currentConfig = _config.value
        _config.value = currentConfig.copy(performanceOverrides = emptyMap())
        logger.info("CONFIG", "Temporary overrides cleared")
    }
    
    fun cleanup() {
        configScope.cancel()
    }
}