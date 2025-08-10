package com.example.Linageisp.performance.core

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min

/**
 * Sistema de caché inteligente con TTL, LRU y métricas de rendimiento
 */
class SmartCache<K : Any, V : Any>(
    private val maxSize: Int = 100,
    private val defaultTtlMs: Long = 30 * 60 * 1000L, // 30 minutos
    private val cleanupIntervalMs: Long = 5 * 60 * 1000L, // 5 minutos
    private val enableMetrics: Boolean = true
) {
    
    data class CacheEntry<V>(
        val value: V,
        val timestamp: Long,
        val ttlMs: Long,
        val accessCount: Int = 0,
        val lastAccessed: Long = System.currentTimeMillis()
    ) {
        val isExpired: Boolean
            get() = System.currentTimeMillis() - timestamp > ttlMs
        
        fun touch(): CacheEntry<V> = copy(
            accessCount = accessCount + 1,
            lastAccessed = System.currentTimeMillis()
        )
    }
    
    data class CacheMetrics(
        val totalRequests: Long = 0,
        val hits: Long = 0,
        val misses: Long = 0,
        val evictions: Long = 0,
        val expirations: Long = 0,
        val currentSize: Int = 0,
        val maxSize: Int = 0,
        val hitRate: Double = 0.0,
        val memoryUsageBytes: Long = 0
    ) {
        val missRate: Double get() = 1.0 - hitRate
        val utilizationRate: Double get() = if (maxSize > 0) currentSize.toDouble() / maxSize else 0.0
    }
    
    // Storage principal thread-safe
    private val cache = ConcurrentHashMap<K, CacheEntry<V>>()
    private val accessOrder = mutableListOf<K>() // Para LRU
    private val accessMutex = Mutex()
    
    // Métricas
    private val totalRequests = AtomicLong(0)
    private val hits = AtomicLong(0)
    private val misses = AtomicLong(0)
    private val evictions = AtomicLong(0)
    private val expirations = AtomicLong(0)
    
    // Scope para tareas de background
    private val cacheScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Flow de métricas
    private val _metrics = MutableStateFlow(CacheMetrics())
    val metrics: StateFlow<CacheMetrics> = _metrics.asStateFlow()
    
    // Logger
    private val logger = PerformanceLogger.getInstance()
    
    init {
        startPeriodicCleanup()
        if (enableMetrics) {
            startMetricsUpdater()
        }
    }
    
    /**
     * Obtiene un valor del caché
     */
    suspend fun get(key: K): V? = accessMutex.withLock {
        totalRequests.incrementAndGet()
        
        val entry = cache[key]
        
        if (entry == null) {
            misses.incrementAndGet()
            updateMetrics()
            return null
        }
        
        if (entry.isExpired) {
            cache.remove(key)
            accessOrder.remove(key)
            expirations.incrementAndGet()
            misses.incrementAndGet()
            
            if (enableMetrics) {
                logger.debug("CACHE", "Cache entry expired", mapOf(
                    "key" to key.toString(),
                    "age_ms" to (System.currentTimeMillis() - entry.timestamp)
                ))
            }
            
            updateMetrics()
            return null
        }
        
        // Actualizar orden LRU
        accessOrder.remove(key)
        accessOrder.add(key)
        
        // Actualizar entrada con nuevo access count
        cache[key] = entry.touch()
        
        hits.incrementAndGet()
        updateMetrics()
        
        if (enableMetrics) {
            logger.debug("CACHE", "Cache hit", mapOf(
                "key" to key.toString(),
                "access_count" to entry.accessCount
            ))
        }
        
        return entry.value
    }
    
    /**
     * Almacena un valor en el caché
     */
    suspend fun put(key: K, value: V, ttlMs: Long = defaultTtlMs) = accessMutex.withLock {
        // Si ya existe, actualizar
        if (cache.containsKey(key)) {
            accessOrder.remove(key)
        }
        
        // Verificar si necesitamos hacer espacio
        if (cache.size >= maxSize && !cache.containsKey(key)) {
            evictLRU()
        }
        
        // Agregar nueva entrada
        val entry = CacheEntry(
            value = value,
            timestamp = System.currentTimeMillis(),
            ttlMs = ttlMs
        )
        
        cache[key] = entry
        accessOrder.add(key)
        
        updateMetrics()
        
        if (enableMetrics) {
            logger.debug("CACHE", "Cache put", mapOf(
                "key" to key.toString(),
                "ttl_ms" to ttlMs,
                "cache_size" to cache.size
            ))
        }
    }
    
    /**
     * Remueve una entrada específica
     */
    suspend fun remove(key: K): V? = accessMutex.withLock {
        val entry = cache.remove(key)
        accessOrder.remove(key)
        updateMetrics()
        
        if (enableMetrics && entry != null) {
            logger.debug("CACHE", "Cache remove", mapOf(
                "key" to key.toString()
            ))
        }
        
        return entry?.value
    }
    
    /**
     * Obtiene o computa un valor
     */
    suspend fun getOrPut(
        key: K,
        ttlMs: Long = defaultTtlMs,
        valueFactory: suspend () -> V
    ): V {
        get(key)?.let { return it }
        
        val value = logger.measureSuspendPerformance("CACHE", "compute_value") {
            valueFactory()
        }
        
        put(key, value, ttlMs)
        return value
    }
    
    /**
     * Limpia entradas expiradas
     */
    suspend fun cleanup(): Int = accessMutex.withLock {
        val expiredKeys = mutableListOf<K>()
        val currentTime = System.currentTimeMillis()
        
        cache.forEach { (key, entry) ->
            if (currentTime - entry.timestamp > entry.ttlMs) {
                expiredKeys.add(key)
            }
        }
        
        expiredKeys.forEach { key ->
            cache.remove(key)
            accessOrder.remove(key)
        }
        
        expirations.addAndGet(expiredKeys.size.toLong())
        updateMetrics()
        
        if (enableMetrics && expiredKeys.isNotEmpty()) {
            logger.info("CACHE", "Cleanup completed", mapOf(
                "expired_entries" to expiredKeys.size,
                "remaining_entries" to cache.size
            ))
        }
        
        return expiredKeys.size
    }
    
    /**
     * Invalida todo el caché
     */
    suspend fun clear() = accessMutex.withLock {
        val oldSize = cache.size
        cache.clear()
        accessOrder.clear()
        updateMetrics()
        
        if (enableMetrics) {
            logger.info("CACHE", "Cache cleared", mapOf(
                "cleared_entries" to oldSize
            ))
        }
    }
    
    /**
     * Obtiene información de una entrada sin afectar LRU
     */
    fun peek(key: K): V? {
        val entry = cache[key]
        return if (entry?.isExpired == false) entry.value else null
    }
    
    /**
     * Verifica si una clave existe y no ha expirado
     */
    fun containsKey(key: K): Boolean {
        val entry = cache[key] ?: return false
        return !entry.isExpired
    }
    
    /**
     * Obtiene el tamaño actual del caché
     */
    val size: Int get() = cache.size
    
    /**
     * Verifica si el caché está vacío
     */
    val isEmpty: Boolean get() = cache.isEmpty()
    
    /**
     * Obtiene todas las claves no expiradas
     */
    fun keys(): Set<K> {
        val currentTime = System.currentTimeMillis()
        return cache.filterValues { entry ->
            currentTime - entry.timestamp <= entry.ttlMs
        }.keys
    }
    
    /**
     * Obtiene estadísticas del caché
     */
    fun getStats(): CacheMetrics = _metrics.value
    
    /**
     * Evict LRU entry
     */
    private suspend fun evictLRU() {
        if (accessOrder.isNotEmpty()) {
            val lruKey = accessOrder.removeAt(0)
            cache.remove(lruKey)
            evictions.incrementAndGet()
            
            if (enableMetrics) {
                logger.debug("CACHE", "LRU eviction", mapOf(
                    "evicted_key" to lruKey.toString(),
                    "cache_size" to cache.size
                ))
            }
        }
    }
    
    /**
     * Inicia limpieza periódica en background
     */
    private fun startPeriodicCleanup() {
        cacheScope.launch {
            while (isActive) {
                try {
                    delay(cleanupIntervalMs)
                    cleanup()
                } catch (e: Exception) {
                    if (enableMetrics) {
                        logger.error("CACHE", "Cleanup error", e)
                    }
                }
            }
        }
    }
    
    /**
     * Inicia actualizador de métricas
     */
    private fun startMetricsUpdater() {
        cacheScope.launch {
            while (isActive) {
                delay(10_000) // Actualizar cada 10 segundos
                updateMetrics()
            }
        }
    }
    
    /**
     * Actualiza las métricas
     */
    private fun updateMetrics() {
        if (!enableMetrics) return
        
        val requests = totalRequests.get()
        val hitCount = hits.get()
        val hitRate = if (requests > 0) hitCount.toDouble() / requests else 0.0
        
        // Estimar uso de memoria (aproximado)
        val memoryUsage = cache.size * 1024L // Aproximación simple
        
        _metrics.value = CacheMetrics(
            totalRequests = requests,
            hits = hitCount,
            misses = misses.get(),
            evictions = evictions.get(),
            expirations = expirations.get(),
            currentSize = cache.size,
            maxSize = maxSize,
            hitRate = hitRate,
            memoryUsageBytes = memoryUsage
        )
    }
    
    /**
     * Redimensiona el caché
     */
    suspend fun resize(newMaxSize: Int) = accessMutex.withLock {
        if (newMaxSize < cache.size) {
            // Evict entries hasta alcanzar el nuevo tamaño
            val toRemove = cache.size - newMaxSize
            repeat(toRemove) {
                evictLRU()
            }
        }
        
        updateMetrics()
        
        if (enableMetrics) {
            logger.info("CACHE", "Cache resized", mapOf(
                "old_size" to maxSize,
                "new_size" to newMaxSize,
                "current_entries" to cache.size
            ))
        }
    }
    
    /**
     * Optimiza el caché eliminando entradas menos usadas
     */
    suspend fun optimize() = accessMutex.withLock {
        val entries = cache.entries.sortedBy { it.value.accessCount }
        val toRemoveCount = min(cache.size / 4, cache.size - (maxSize * 0.75).toInt())
        
        if (toRemoveCount > 0) {
            val keysToRemove = entries.take(toRemoveCount).map { it.key }
            keysToRemove.forEach { key ->
                cache.remove(key)
                accessOrder.remove(key)
            }
            
            evictions.addAndGet(keysToRemove.size.toLong())
            updateMetrics()
            
            if (enableMetrics) {
                logger.info("CACHE", "Cache optimized", mapOf(
                    "removed_entries" to keysToRemove.size,
                    "remaining_entries" to cache.size
                ))
            }
        }
    }
    
    /**
     * Limpia recursos del cache
     */
    fun destroy() {
        cacheScope.cancel()
        cache.clear()
        accessOrder.clear()
    }
}

/**
 * Cache especializado para imágenes
 */
class ImageCache(
    maxSizeBytes: Long = 50 * 1024 * 1024, // 50MB por defecto
    deviceTier: DeviceCapabilityDetector.PerformanceTier
) {
    private val adjustedMaxSize = when (deviceTier) {
        DeviceCapabilityDetector.PerformanceTier.LOW_END -> maxSizeBytes / 4
        DeviceCapabilityDetector.PerformanceTier.MID_END -> maxSizeBytes / 2
        DeviceCapabilityDetector.PerformanceTier.HIGH_END -> maxSizeBytes
        DeviceCapabilityDetector.PerformanceTier.PREMIUM -> maxSizeBytes * 2
    }
    
    private val cache = SmartCache<String, ByteArray>(
        maxSize = (adjustedMaxSize / 1024).toInt(), // Aproximación de entradas
        defaultTtlMs = 60 * 60 * 1000L // 1 hora
    )
    
    suspend fun getImage(url: String): ByteArray? = cache.get(url)
    
    suspend fun putImage(url: String, imageData: ByteArray) {
        cache.put(url, imageData)
    }
    
    suspend fun preloadImages(urls: List<String>, imageLoader: suspend (String) -> ByteArray?) {
        urls.forEach { url ->
            if (!cache.containsKey(url)) {
                imageLoader(url)?.let { data ->
                    cache.put(url, data)
                }
            }
        }
    }
    
    fun getMetrics() = cache.getStats()
    
    fun cleanupExpired() {
        // Cleanup expired entries - calls the suspend version in a coroutine
        GlobalScope.launch {
            cache.cleanup()
        }
    }
}

/**
 * Composable para acceder al cache
 */
@Composable
fun <K : Any, V : Any> rememberSmartCache(
    maxSize: Int = 100,
    defaultTtlMs: Long = 30 * 60 * 1000L
): SmartCache<K, V> {
    return remember(maxSize, defaultTtlMs) {
        SmartCache<K, V>(maxSize, defaultTtlMs)
    }
}

/**
 * Hook para usar image cache
 */
@Composable
fun rememberImageCache(): ImageCache {
    val deviceCapabilities = rememberDeviceCapabilities()
    return remember(deviceCapabilities?.tier) {
        ImageCache(deviceTier = deviceCapabilities?.tier ?: DeviceCapabilityDetector.PerformanceTier.MID_END)
    }
}