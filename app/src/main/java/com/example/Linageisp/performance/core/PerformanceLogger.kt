package com.example.Linageisp.performance.core

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Sistema avanzado de logging de performance con niveles y persistencia
 */
class PerformanceLogger private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: PerformanceLogger? = null
        private const val TAG = "PerformanceLogger"
        private const val MAX_LOG_ENTRIES = 1000
        private const val LOG_FILE_NAME = "performance_logs.txt"
        
        fun getInstance(): PerformanceLogger {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PerformanceLogger().also { INSTANCE = it }
            }
        }
    }
    
    enum class LogLevel(val priority: Int, val tag: String) {
        DEBUG(3, "PERF_DEBUG"),
        INFO(4, "PERF_INFO"),
        WARN(5, "PERF_WARN"),
        ERROR(6, "PERF_ERROR"),
        CRITICAL(7, "PERF_CRITICAL")
    }
    
    data class PerformanceLogEntry(
        val timestamp: Long = System.currentTimeMillis(),
        val level: LogLevel,
        val category: String,
        val message: String,
        val metrics: Map<String, Any> = emptyMap(),
        val stackTrace: String? = null
    )
    
    private val logScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val logChannel = Channel<PerformanceLogEntry>(Channel.UNLIMITED)
    private val logBuffer = mutableListOf<PerformanceLogEntry>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    
    // Configuración
    private var minLogLevel = LogLevel.INFO
    private var enableConsoleLogging = true
    private var enableFileLogging = false
    private var logFile: File? = null
    
    private val _logFlow = MutableSharedFlow<PerformanceLogEntry>()
    val logFlow: SharedFlow<PerformanceLogEntry> = _logFlow.asSharedFlow()
    
    init {
        startLogProcessor()
    }
    
    private fun startLogProcessor() {
        logScope.launch {
            logChannel.consumeAsFlow().collect { entry ->
                processLogEntry(entry)
            }
        }
    }
    
    private suspend fun processLogEntry(entry: PerformanceLogEntry) = withContext(Dispatchers.IO) {
        // Agregar al buffer
        synchronized(logBuffer) {
            logBuffer.add(entry)
            if (logBuffer.size > MAX_LOG_ENTRIES) {
                logBuffer.removeAt(0)
            }
        }
        
        // Console logging
        if (enableConsoleLogging && entry.level.priority >= minLogLevel.priority) {
            val formattedMessage = formatLogMessage(entry)
            when (entry.level) {
                LogLevel.DEBUG -> Log.d(entry.level.tag, formattedMessage)
                LogLevel.INFO -> Log.i(entry.level.tag, formattedMessage)
                LogLevel.WARN -> Log.w(entry.level.tag, formattedMessage)
                LogLevel.ERROR, LogLevel.CRITICAL -> Log.e(entry.level.tag, formattedMessage)
            }
        }
        
        // File logging
        if (enableFileLogging && logFile != null) {
            try {
                logFile?.appendText("${formatLogMessage(entry)}\n")
            } catch (e: Exception) {
                Log.e(TAG, "Error writing to log file", e)
            }
        }
        
        // Emit to flow
        _logFlow.emit(entry)
    }
    
    private fun formatLogMessage(entry: PerformanceLogEntry): String {
        val timestamp = dateFormat.format(Date(entry.timestamp))
        val metricsStr = if (entry.metrics.isNotEmpty()) {
            " | Metrics: ${entry.metrics}"
        } else ""
        
        return "[$timestamp] [${entry.level.tag}] [${entry.category}] ${entry.message}$metricsStr"
    }
    
    // Logging methods
    fun debug(category: String, message: String, metrics: Map<String, Any> = emptyMap()) {
        log(LogLevel.DEBUG, category, message, metrics)
    }
    
    fun info(category: String, message: String, metrics: Map<String, Any> = emptyMap()) {
        log(LogLevel.INFO, category, message, metrics)
    }
    
    fun warn(category: String, message: String, metrics: Map<String, Any> = emptyMap()) {
        log(LogLevel.WARN, category, message, metrics)
    }
    
    fun error(category: String, message: String, throwable: Throwable? = null, metrics: Map<String, Any> = emptyMap()) {
        log(LogLevel.ERROR, category, message, metrics, throwable?.stackTraceToString())
    }
    
    fun critical(category: String, message: String, throwable: Throwable? = null, metrics: Map<String, Any> = emptyMap()) {
        log(LogLevel.CRITICAL, category, message, metrics, throwable?.stackTraceToString())
    }
    
    private fun log(level: LogLevel, category: String, message: String, metrics: Map<String, Any> = emptyMap(), stackTrace: String? = null) {
        val entry = PerformanceLogEntry(
            level = level,
            category = category,
            message = message,
            metrics = metrics,
            stackTrace = stackTrace
        )
        
        logScope.launch {
            logChannel.send(entry)
        }
    }
    
    // Performance-specific logging methods
    fun logFrameMetrics(fps: Float, frameTime: Float, jankCount: Int) {
        info("FRAME_METRICS", "Frame performance update", mapOf(
            "fps" to fps.roundToInt(),
            "frame_time_ms" to frameTime.roundToInt(),
            "jank_count" to jankCount
        ))
    }
    
    fun logMemoryMetrics(usedMemoryMB: Long, availableMemoryMB: Long, usagePercentage: Float) {
        val level = when {
            usagePercentage > 85f -> LogLevel.WARN
            usagePercentage > 95f -> LogLevel.CRITICAL
            else -> LogLevel.INFO
        }
        
        log(level, "MEMORY_METRICS", "Memory usage update", mapOf(
            "used_mb" to usedMemoryMB,
            "available_mb" to availableMemoryMB,
            "usage_percent" to usagePercentage.roundToInt()
        ))
    }
    
    fun logPerformanceAnomaly(anomalyType: String, severity: String, details: Map<String, Any>) {
        val level = when (severity.lowercase()) {
            "low" -> LogLevel.INFO
            "medium" -> LogLevel.WARN
            "high" -> LogLevel.ERROR
            "critical" -> LogLevel.CRITICAL
            else -> LogLevel.WARN
        }
        
        log(level, "PERFORMANCE_ANOMALY", "Performance anomaly detected: $anomalyType", details)
    }
    
    fun logOptimizationApplied(optimizationType: String, deviceTier: String, impact: Map<String, Any>) {
        info("OPTIMIZATION", "Applied optimization: $optimizationType for $deviceTier", impact)
    }
    
    // Configuration methods
    fun setMinLogLevel(level: LogLevel) {
        minLogLevel = level
        info("LOGGER_CONFIG", "Min log level changed to ${level.tag}")
    }
    
    fun enableFileLogging(file: File) {
        logFile = file
        enableFileLogging = true
        info("LOGGER_CONFIG", "File logging enabled: ${file.absolutePath}")
    }
    
    fun disableFileLogging() {
        enableFileLogging = false
        logFile = null
        info("LOGGER_CONFIG", "File logging disabled")
    }
    
    // Data retrieval
    fun getLogHistory(level: LogLevel? = null, category: String? = null): List<PerformanceLogEntry> {
        return synchronized(logBuffer) {
            logBuffer.filter { entry ->
                (level == null || entry.level == level) &&
                (category == null || entry.category == category)
            }.toList()
        }
    }
    
    fun getLogSummary(): Map<String, Any> {
        val entries = synchronized(logBuffer) { logBuffer.toList() }
        val levelCounts = entries.groupingBy { it.level }.eachCount()
        val categoryCounts = entries.groupingBy { it.category }.eachCount()
        
        return mapOf(
            "total_entries" to entries.size,
            "level_distribution" to levelCounts,
            "category_distribution" to categoryCounts,
            "oldest_entry" to (entries.minByOrNull { it.timestamp }?.timestamp ?: 0L),
            "newest_entry" to (entries.maxByOrNull { it.timestamp }?.timestamp ?: 0L)
        )
    }
    
    fun exportLogs(): String {
        return synchronized(logBuffer) {
            logBuffer.joinToString("\n") { formatLogMessage(it) }
        }
    }
    
    fun cleanup() {
        logScope.cancel()
        synchronized(logBuffer) {
            logBuffer.clear()
        }
    }
}

/**
 * Performance logging extensions
 */
inline fun <T> PerformanceLogger.measurePerformance(
    category: String,
    operation: String,
    block: () -> T
): T {
    val startTime = System.currentTimeMillis()
    return try {
        val result = block()
        val duration = System.currentTimeMillis() - startTime
        info(category, "Operation '$operation' completed", mapOf(
            "duration_ms" to duration,
            "status" to "success"
        ))
        result
    } catch (e: Exception) {
        val duration = System.currentTimeMillis() - startTime
        error(category, "Operation '$operation' failed", e, mapOf(
            "duration_ms" to duration,
            "status" to "error"
        ))
        throw e
    }
}

suspend inline fun <T> PerformanceLogger.measureSuspendPerformance(
    category: String,
    operation: String,
    block: suspend () -> T
): T {
    val startTime = System.currentTimeMillis()
    return try {
        val result = block()
        val duration = System.currentTimeMillis() - startTime
        info(category, "Suspend operation '$operation' completed", mapOf(
            "duration_ms" to duration,
            "status" to "success"
        ))
        result
    } catch (e: Exception) {
        val duration = System.currentTimeMillis() - startTime
        error(category, "Suspend operation '$operation' failed", e, mapOf(
            "duration_ms" to duration,
            "status" to "error"
        ))
        throw e
    }
}