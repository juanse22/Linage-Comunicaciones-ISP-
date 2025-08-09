package com.example.Linageisp.performance

import android.os.Build
import android.view.Choreographer
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * Administrador de sincronización vertical (VSync) para optimización de rendimiento
 * Previene tearing, stuttering y mejora la fluidez de animaciones
 */
class VSyncManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: VSyncManager? = null
        
        fun getInstance(): VSyncManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VSyncManager().also { INSTANCE = it }
            }
        }
        
        private const val TARGET_FPS_60 = 16_666_666L // 60 FPS en nanosegundos
        private const val TARGET_FPS_90 = 11_111_111L // 90 FPS en nanosegundos  
        private const val TARGET_FPS_120 = 8_333_333L // 120 FPS en nanosegundos
        private const val FRAME_BUDGET_WARNING_NS = TARGET_FPS_60 * 0.8 // 80% del budget
    }
    
    private val choreographer = Choreographer.getInstance()
    private val isVSyncActive = AtomicBoolean(false)
    private val frameCount = AtomicLong(0)
    private val lastFrameTime = AtomicLong(0)
    private val frameDropCount = AtomicLong(0)
    
    // Flow para eventos de frame
    private val _frameEvents = MutableSharedFlow<FrameInfo>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val frameEvents: SharedFlow<FrameInfo> = _frameEvents.asSharedFlow()
    
    // Callback optimizado para VSync
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (!isVSyncActive.get()) return
            
            val currentFrameTime = System.nanoTime()
            val lastTime = lastFrameTime.get()
            
            if (lastTime != 0L) {
                val frameDuration = currentFrameTime - lastTime
                val frameInfo = FrameInfo(
                    frameNumber = frameCount.incrementAndGet(),
                    frameTimeNanos = frameTimeNanos,
                    frameDurationNanos = frameDuration,
                    isFrameDropped = frameDuration > TARGET_FPS_60 * 1.5, // Considera dropped si excede 150% del target
                    budget = TARGET_FPS_60,
                    actualFps = calculateInstantFPS(frameDuration)
                )
                
                if (frameInfo.isFrameDropped) {
                    frameDropCount.incrementAndGet()
                }
                
                _frameEvents.tryEmit(frameInfo)
            }
            
            lastFrameTime.set(currentFrameTime)
            
            // Reprogramar siguiente frame solo si VSync sigue activo
            if (isVSyncActive.get()) {
                choreographer.postFrameCallback(this)
            }
        }
    }
    
    data class FrameInfo(
        val frameNumber: Long,
        val frameTimeNanos: Long,
        val frameDurationNanos: Long,
        val isFrameDropped: Boolean,
        val budget: Long,
        val actualFps: Float
    ) {
        val budgetUsagePercent: Float = (frameDurationNanos.toFloat() / budget) * 100f
        val isWithinBudget: Boolean = frameDurationNanos <= budget
        val performanceRating: PerformanceRating = when {
            budgetUsagePercent <= 60f -> PerformanceRating.EXCELLENT
            budgetUsagePercent <= 80f -> PerformanceRating.GOOD  
            budgetUsagePercent <= 100f -> PerformanceRating.ACCEPTABLE
            budgetUsagePercent <= 150f -> PerformanceRating.POOR
            else -> PerformanceRating.TERRIBLE
        }
    }
    
    enum class PerformanceRating {
        EXCELLENT, GOOD, ACCEPTABLE, POOR, TERRIBLE
    }
    
    /**
     * Inicia la sincronización VSync
     */
    fun startVSync() {
        if (isVSyncActive.compareAndSet(false, true)) {
            resetMetrics()
            choreographer.postFrameCallback(frameCallback)
        }
    }
    
    /**
     * Detiene la sincronización VSync
     */
    fun stopVSync() {
        if (isVSyncActive.compareAndSet(true, false)) {
            choreographer.removeFrameCallback(frameCallback)
        }
    }
    
    /**
     * Verifica si VSync está activo
     */
    fun isActive(): Boolean = isVSyncActive.get()
    
    /**
     * Obtiene métricas de rendimiento actuales
     */
    fun getPerformanceMetrics(): PerformanceMetrics {
        val totalFrames = frameCount.get()
        val droppedFrames = frameDropCount.get()
        val dropRate = if (totalFrames > 0) (droppedFrames.toFloat() / totalFrames) * 100f else 0f
        
        return PerformanceMetrics(
            totalFrames = totalFrames,
            droppedFrames = droppedFrames,
            frameDropRate = dropRate,
            isPerformanceGood = dropRate < 5f // Menos del 5% de frames dropped es bueno
        )
    }
    
    data class PerformanceMetrics(
        val totalFrames: Long,
        val droppedFrames: Long,
        val frameDropRate: Float,
        val isPerformanceGood: Boolean
    )
    
    /**
     * Reinicia las métricas de rendimiento
     */
    fun resetMetrics() {
        frameCount.set(0)
        frameDropCount.set(0)
        lastFrameTime.set(0)
    }
    
    private fun calculateInstantFPS(frameDurationNanos: Long): Float {
        return if (frameDurationNanos > 0) {
            1_000_000_000f / frameDurationNanos
        } else 0f
    }
    
    /**
     * Ejecuta una acción sincronizada con el próximo VSync
     */
    fun executeOnNextVSync(action: () -> Unit) {
        choreographer.postFrameCallback { 
            action()
        }
    }
    
    /**
     * Espera al próximo VSync de forma suspendida
     */
    suspend fun awaitNextVSync(): Long {
        return kotlin.coroutines.suspendCoroutine { continuation ->
            choreographer.postFrameCallback { frameTimeNanos ->
                continuation.resumeWith(Result.success(frameTimeNanos))
            }
        }
    }
    
    /**
     * Optimiza el timing basado en el refresh rate del display
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun getOptimalFrameBudget(refreshRate: Float): Long {
        return (1_000_000_000L / refreshRate).toLong()
    }
}

/**
 * Composable para manejo automático de VSync con ciclo de vida
 */
@Composable
fun rememberVSyncManager(): VSyncManager {
    val vSyncManager = remember { VSyncManager.getInstance() }
    val lifecycleOwner = LocalLifecycleOwner.current
    
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> vSyncManager.startVSync()
                Lifecycle.Event.ON_PAUSE -> vSyncManager.stopVSync()
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            vSyncManager.stopVSync()
        }
    }
    
    return vSyncManager
}

/**
 * Composable para monitoreo en tiempo real de métricas VSync
 */
@Composable
fun VSyncMetricsCollector(
    onMetricsUpdate: (VSyncManager.PerformanceMetrics) -> Unit = {}
) {
    val vSyncManager = rememberVSyncManager()
    
    LaunchedEffect(vSyncManager) {
        var lastMetricsTime = 0L
        
        vSyncManager.frameEvents.collect { frameInfo ->
            val currentTime = System.currentTimeMillis()
            
            // Actualizar métricas cada segundo
            if (currentTime - lastMetricsTime >= 1000) {
                val metrics = vSyncManager.getPerformanceMetrics()
                onMetricsUpdate(metrics)
                lastMetricsTime = currentTime
            }
        }
    }
}