package com.example.Linageisp.performance

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Debug
import androidx.annotation.WorkerThread
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.min
import com.example.Linageisp.performance.core.DeviceCapabilityDetector

/**
 * Administrador de memoria y recursos para optimización de rendimiento extremo
 * Gestiona memoria, compresión de imágenes y limpieza automática de recursos
 */
class MemoryResourceManager private constructor(
    private val context: Context,
    private val deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities
) {
    
    companion object {
        @Volatile
        private var INSTANCE: MemoryResourceManager? = null
        
        fun getInstance(
            context: Context,
            deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities
        ): MemoryResourceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MemoryResourceManager(
                    context.applicationContext,
                    deviceCapabilities
                ).also { INSTANCE = it }
            }
        }
        
        // Umbrales de memoria crítica
        private const val MEMORY_CRITICAL_THRESHOLD = 0.9f // 90%
        private const val MEMORY_WARNING_THRESHOLD = 0.75f // 75%
        private const val GC_TRIGGER_THRESHOLD = 0.85f // 85%
        
        // Configuración de compresión por tier
        private val COMPRESSION_CONFIGS = mapOf(
            DeviceCapabilityDetector.PerformanceTier.LOW_END to BitmapCompressionConfig(
                maxWidth = 800,
                maxHeight = 600,
                quality = 60,
                format = Bitmap.CompressFormat.JPEG
            ),
            DeviceCapabilityDetector.PerformanceTier.MID_END to BitmapCompressionConfig(
                maxWidth = 1200,
                maxHeight = 900,
                quality = 75,
                format = Bitmap.CompressFormat.JPEG
            ),
            DeviceCapabilityDetector.PerformanceTier.HIGH_END to BitmapCompressionConfig(
                maxWidth = 1920,
                maxHeight = 1440,
                quality = 85,
                format = Bitmap.CompressFormat.JPEG
            ),
            DeviceCapabilityDetector.PerformanceTier.PREMIUM to BitmapCompressionConfig(
                maxWidth = 2560,
                maxHeight = 1920,
                quality = 90,
                format = Bitmap.CompressFormat.PNG
            )
        )
    }
    
    data class BitmapCompressionConfig(
        val maxWidth: Int,
        val maxHeight: Int,
        val quality: Int,
        val format: Bitmap.CompressFormat
    )
    
    data class MemoryMetrics(
        val totalMemoryMB: Long,
        val availableMemoryMB: Long,
        val usedMemoryMB: Long,
        val usagePercentage: Float,
        val isLowMemory: Boolean,
        val heapSizeMB: Long,
        val heapUsedMB: Long,
        val nativeHeapSizeMB: Long
    )
    
    data class ResourceCleanupStats(
        val bitmapsReleased: Int,
        val cacheEntriesCleared: Int,
        val memoryFreedMB: Long,
        val cleanupDurationMs: Long
    )
    
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val memoryCache = ConcurrentHashMap<String, WeakReference<Bitmap>>()
    private val resourceReferences = ConcurrentHashMap<String, WeakReference<Any>>()
    private val compressionConfig = COMPRESSION_CONFIGS[deviceCapabilities.tier] 
        ?: COMPRESSION_CONFIGS[DeviceCapabilityDetector.PerformanceTier.MID_END]!!
    
    // Scope para operaciones de limpieza automática
    private val cleanupScope = CoroutineScope(
        SupervisorJob() + 
        Dispatchers.Default + 
        CoroutineName("MemoryCleanup")
    )
    
    // Flow para métricas de memoria en tiempo real
    private val _memoryMetrics = MutableSharedFlow<MemoryMetrics>(
        replay = 1,
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val memoryMetrics: SharedFlow<MemoryMetrics> = _memoryMetrics.asSharedFlow()
    
    init {
        startMemoryMonitoring()
    }
    
    /**
     * Inicia el monitoreo continuo de memoria
     */
    private fun startMemoryMonitoring() {
        cleanupScope.launch {
            while (isActive) {
                val metrics = getCurrentMemoryMetrics()
                _memoryMetrics.tryEmit(metrics)
                
                // Trigger limpieza automática si es necesario
                when {
                    metrics.usagePercentage >= MEMORY_CRITICAL_THRESHOLD -> {
                        performEmergencyCleanup()
                    }
                    metrics.usagePercentage >= GC_TRIGGER_THRESHOLD -> {
                        suggestGarbageCollection()
                    }
                    metrics.usagePercentage >= MEMORY_WARNING_THRESHOLD -> {
                        performLightCleanup()
                    }
                }
                
                delay(2000) // Monitorear cada 2 segundos
            }
        }
    }
    
    /**
     * Obtiene métricas actuales de memoria
     */
    fun getCurrentMemoryMetrics(): MemoryMetrics {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        val totalMemoryMB = memoryInfo.totalMem / (1024 * 1024)
        val availableMemoryMB = memoryInfo.availMem / (1024 * 1024)
        val usedMemoryMB = totalMemoryMB - availableMemoryMB
        val usagePercentage = (usedMemoryMB.toFloat() / totalMemoryMB) * 100f
        
        val runtime = Runtime.getRuntime()
        val heapSizeMB = runtime.totalMemory() / (1024 * 1024)
        val heapUsedMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val nativeHeapSizeMB = Debug.getNativeHeapSize() / (1024 * 1024)
        
        return MemoryMetrics(
            totalMemoryMB = totalMemoryMB,
            availableMemoryMB = availableMemoryMB,
            usedMemoryMB = usedMemoryMB,
            usagePercentage = usagePercentage,
            isLowMemory = memoryInfo.lowMemory,
            heapSizeMB = heapSizeMB,
            heapUsedMB = heapUsedMB,
            nativeHeapSizeMB = nativeHeapSizeMB
        )
    }
    
    /**
     * Optimiza un bitmap según las capacidades del dispositivo
     */
    @WorkerThread
    suspend fun optimizeBitmap(
        originalBitmap: Bitmap?,
        cacheKey: String? = null
    ): Bitmap? = withContext(Dispatchers.Default) {
        if (originalBitmap == null || originalBitmap.isRecycled) return@withContext null
        
        // Verificar si ya está en caché
        cacheKey?.let { key ->
            memoryCache[key]?.get()?.let { cachedBitmap ->
                if (!cachedBitmap.isRecycled) {
                    return@withContext cachedBitmap
                } else {
                    memoryCache.remove(key)
                }
            }
        }
        
        val optimizedBitmap = when {
            // Para dispositivos LOW_END: máxima compresión
            deviceCapabilities.tier == DeviceCapabilityDetector.PerformanceTier.LOW_END -> {
                compressBitmap(originalBitmap, aggressive = true)
            }
            // Para dispositivos MID_END: compresión moderada
            deviceCapabilities.tier == DeviceCapabilityDetector.PerformanceTier.MID_END -> {
                compressBitmap(originalBitmap, aggressive = false)
            }
            // Para dispositivos HIGH_END/PREMIUM: compresión mínima o sin compresión
            else -> {
                if (needsBitmapResize(originalBitmap)) {
                    resizeBitmap(originalBitmap)
                } else {
                    originalBitmap
                }
            }
        }
        
        // Guardar en caché si se especifica clave
        cacheKey?.let { key ->
            optimizedBitmap?.let { bitmap ->
                memoryCache[key] = WeakReference(bitmap)
            }
        }
        
        // Limpiar bitmap original si es diferente del optimizado
        if (optimizedBitmap != originalBitmap && !originalBitmap.isRecycled) {
            originalBitmap.recycle()
        }
        
        optimizedBitmap
    }
    
    /**
     * Comprime un bitmap según la configuración del dispositivo
     */
    private suspend fun compressBitmap(
        bitmap: Bitmap,
        aggressive: Boolean = false
    ): Bitmap? = withContext(Dispatchers.Default) {
        try {
            val config = if (aggressive) {
                compressionConfig.copy(
                    quality = (compressionConfig.quality * 0.7f).toInt(),
                    maxWidth = (compressionConfig.maxWidth * 0.8f).toInt(),
                    maxHeight = (compressionConfig.maxHeight * 0.8f).toInt()
                )
            } else {
                compressionConfig
            }
            
            // Redimensionar si es necesario
            val resizedBitmap = if (bitmap.width > config.maxWidth || bitmap.height > config.maxHeight) {
                val scaleFactor = min(
                    config.maxWidth.toFloat() / bitmap.width,
                    config.maxHeight.toFloat() / bitmap.height
                )
                val newWidth = (bitmap.width * scaleFactor).toInt()
                val newHeight = (bitmap.height * scaleFactor).toInt()
                
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }
            
            // Comprimir según formato
            val outputStream = ByteArrayOutputStream()
            val compressionSuccess = resizedBitmap.compress(config.format, config.quality, outputStream)
            
            if (!compressionSuccess) return@withContext resizedBitmap
            
            // Crear bitmap desde bytes comprimidos
            val compressedBytes = outputStream.toByteArray()
            outputStream.close()
            
            val compressedBitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
            
            // Limpiar bitmap temporal si es diferente del original
            if (resizedBitmap != bitmap && !resizedBitmap.isRecycled) {
                resizedBitmap.recycle()
            }
            
            compressedBitmap
        } catch (e: Exception) {
            // En caso de error, devolver bitmap redimensionado o original
            resizeBitmap(bitmap) ?: bitmap
        }
    }
    
    /**
     * Redimensiona un bitmap manteniendo aspecto
     */
    private fun resizeBitmap(bitmap: Bitmap): Bitmap? {
        if (bitmap.isRecycled) return null
        
        val maxWidth = compressionConfig.maxWidth
        val maxHeight = compressionConfig.maxHeight
        
        if (bitmap.width <= maxWidth && bitmap.height <= maxHeight) {
            return bitmap
        }
        
        val scaleFactor = min(
            maxWidth.toFloat() / bitmap.width,
            maxHeight.toFloat() / bitmap.height
        )
        
        val newWidth = (bitmap.width * scaleFactor).toInt()
        val newHeight = (bitmap.height * scaleFactor).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * Verifica si un bitmap necesita redimensionamiento
     */
    private fun needsBitmapResize(bitmap: Bitmap): Boolean {
        return bitmap.width > compressionConfig.maxWidth || 
               bitmap.height > compressionConfig.maxHeight
    }
    
    /**
     * Registra una referencia de recurso para limpieza automática
     */
    fun registerResource(key: String, resource: Any) {
        resourceReferences[key] = WeakReference(resource)
    }
    
    /**
     * Limpia recursos registrados que ya no están en uso
     */
    fun cleanupUnusedResources(): ResourceCleanupStats {
        val startTime = System.currentTimeMillis()
        var bitmapsReleased = 0
        var cacheEntriesCleared = 0
        val initialMemory = getCurrentMemoryMetrics().usedMemoryMB
        
        // Limpiar caché de bitmaps con referencias nulas
        val bitmapIterator = memoryCache.entries.iterator()
        while (bitmapIterator.hasNext()) {
            val entry = bitmapIterator.next()
            val bitmap = entry.value.get()
            if (bitmap == null || bitmap.isRecycled) {
                bitmapIterator.remove()
                cacheEntriesCleared++
            } else if (needsAggressiveCleanup()) {
                bitmap.recycle()
                bitmapIterator.remove()
                bitmapsReleased++
                cacheEntriesCleared++
            }
        }
        
        // Limpiar referencias de recursos
        val resourceIterator = resourceReferences.entries.iterator()
        while (resourceIterator.hasNext()) {
            val entry = resourceIterator.next()
            if (entry.value.get() == null) {
                resourceIterator.remove()
                cacheEntriesCleared++
            }
        }
        
        val endTime = System.currentTimeMillis()
        val finalMemory = getCurrentMemoryMetrics().usedMemoryMB
        val memoryFreed = maxOf(0, initialMemory - finalMemory)
        
        return ResourceCleanupStats(
            bitmapsReleased = bitmapsReleased,
            cacheEntriesCleared = cacheEntriesCleared,
            memoryFreedMB = memoryFreed,
            cleanupDurationMs = endTime - startTime
        )
    }
    
    /**
     * Limpieza ligera para advertencia de memoria
     */
    private suspend fun performLightCleanup() = withContext(Dispatchers.Default) {
        cleanupUnusedResources()
    }
    
    /**
     * Limpieza de emergencia para memoria crítica
     */
    private suspend fun performEmergencyCleanup() = withContext(Dispatchers.Default) {
        performEmergencyCleanupSync()
    }
    
    /**
     * Limpieza de emergencia síncrona para memoria crítica
     */
    private fun performEmergencyCleanupSync() {
        // Limpiar todo el caché de bitmaps
        memoryCache.values.forEach { weakRef ->
            weakRef.get()?.let { bitmap ->
                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }
            }
        }
        memoryCache.clear()
        
        // Limpiar referencias
        resourceReferences.clear()
        
        // Sugerir GC agresivo
        suggestGarbageCollection()
    }
    
    /**
     * Sugiere garbage collection
     */
    private fun suggestGarbageCollection() {
        System.gc()
        Runtime.getRuntime().gc()
    }
    
    /**
     * Verifica si se necesita limpieza agresiva
     */
    private fun needsAggressiveCleanup(): Boolean {
        val metrics = getCurrentMemoryMetrics()
        return metrics.usagePercentage >= MEMORY_WARNING_THRESHOLD ||
               metrics.isLowMemory ||
               deviceCapabilities.tier == DeviceCapabilityDetector.PerformanceTier.LOW_END
    }
    
    /**
     * Limpia todos los recursos y detiene el monitoreo
     */
    fun cleanup() {
        // Realizar limpieza síncrona antes de cancelar el scope
        performEmergencyCleanupSync()
        cleanupScope.cancel()
    }
    
    /**
     * Obtiene estadísticas del caché
     */
    fun getCacheStats(): Map<String, Any> {
        val activeBitmaps = memoryCache.values.count { it.get()?.let { !it.isRecycled } ?: false }
        val totalCacheEntries = memoryCache.size
        val activeResources = resourceReferences.values.count { it.get() != null }
        
        return mapOf(
            "activeBitmaps" to activeBitmaps,
            "totalCacheEntries" to totalCacheEntries,
            "activeResources" to activeResources,
            "compressionConfig" to compressionConfig,
            "deviceTier" to deviceCapabilities.tier.name
        )
    }
}