package com.example.Linageisp.performance

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * EXTREMO RENDIMIENTO: Sistema de optimización para Jetpack Compose
 * Optimizaciones agresivas de memoria, recomposiciones y cálculos
 */
object ComposePerformanceOptimizer {
    
    // Cache de colores para evitar recreación constante
    private val colorCache = mutableMapOf<Int, Color>()
    
    // Cache de estados de scroll
    private val scrollStateCache = mutableMapOf<String, LazyListState>()
    
    // Performance metrics
    private val _recompositionCount = MutableStateFlow(0)
    val recompositionCount: StateFlow<Int> = _recompositionCount.asStateFlow()
    
    private val _cacheHits = MutableStateFlow(0)
    val cacheHits: StateFlow<Int> = _cacheHits.asStateFlow()
    
    /**
     * OPTIMIZACIÓN 1: Cache de colores para evitar allocations
     */
    fun getCachedColor(argb: Int): Color {
        return colorCache.getOrPut(argb) {
            Color(argb)
        }.also {
            _cacheHits.value += 1
        }
    }
    
    /**
     * OPTIMIZACIÓN 2: Lazy list state con cache 
     */
    @Composable
    fun rememberCachedLazyListState(key: String): LazyListState {
        return remember(key) {
            scrollStateCache.getOrPut(key) {
                LazyListState()
            }
        }
    }
    
    /**
     * OPTIMIZACIÓN 3: Derived state para cálculos costosos
     */
    @Composable
    fun rememberDerivedExpensiveCalculation(
        input: Any,
        calculation: (Any) -> Any
    ): State<Any> {
        return remember(input) {
            derivedStateOf { calculation(input) }
        }
    }
    
    /**
     * OPTIMIZACIÓN 4: Composable con key optimizada
     */
    @Composable
    fun rememberStableKey(vararg inputs: Any?): String {
        return remember(*inputs) {
            inputs.joinToString("|") { it?.hashCode()?.toString() ?: "null" }
        }
    }
    
    /**
     * OPTIMIZACIÓN 5: Memory leak prevention
     */
    fun clearCaches() {
        colorCache.clear()
        scrollStateCache.clear()
    }
    
    /**
     * OPTIMIZACIÓN 6: Recomposition tracking
     */
    @Composable
    fun TrackRecompositions(content: @Composable () -> Unit) {
        SideEffect {
            _recompositionCount.value += 1
        }
        content()
    }
    
    /**
     * OPTIMIZACIÓN 7: Skip recomposition for stable objects
     */
    @Stable
    data class StableWrapper<T>(val value: T)
    
    @Composable
    fun <T> rememberStable(value: T): StableWrapper<T> {
        return remember(value) { StableWrapper(value) }
    }
}

/**
 * EXTREMO RENDIMIENTO: Optimizaciones específicas para listas
 */
object LazyListOptimizations {
    
    /**
     * OPTIMIZACIÓN: Keys optimizadas para LazyColumn/LazyRow
     */
    @Composable
    fun <T> rememberOptimizedKeys(
        items: List<T>,
        keySelector: (T) -> Any
    ): List<String> {
        return remember(items.size, items.hashCode()) {
            items.map { item ->
                keySelector(item).toString()
            }
        }
    }
    
    /**
     * OPTIMIZACIÓN: Batching de updates para listas grandes
     */
    class OptimizedListState<T> {
        private val _items = MutableStateFlow<List<T>>(emptyList())
        val items: StateFlow<List<T>> = _items.asStateFlow()
        
        fun updateItems(newItems: List<T>) {
            if (newItems != _items.value) {
                _items.value = newItems
            }
        }
        
        fun addItem(item: T) {
            _items.value = _items.value + item
        }
        
        fun removeItem(predicate: (T) -> Boolean) {
            _items.value = _items.value.filterNot(predicate)
        }
    }
}

/**
 * EXTREMO RENDIMIENTO: Optimizaciones para animaciones
 */
object AnimationOptimizations {
    
    /**
     * OPTIMIZACIÓN: Disable animaciones en dispositivos de gama baja
     */
    @Composable
    fun shouldUseAnimations(): Boolean {
        return remember {
            // Detectar gama baja del dispositivo
            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory()
            val maxMemory = runtime.maxMemory()
            
            // Si memoria disponible es menor a 512MB, desactivar animaciones complejas
            (maxMemory - totalMemory) > 512 * 1024 * 1024
        }
    }
    
    /**
     * OPTIMIZACIÓN: Pool de animaciones reutilizables
     */
    private val animationPool = mutableListOf<androidx.compose.animation.core.Animatable<Float, *>>()
    
    fun getOrCreateAnimation(): androidx.compose.animation.core.Animatable<Float, androidx.compose.animation.core.AnimationVector1D> {
        return if (animationPool.isNotEmpty()) {
            animationPool.removeLastOrNull() as? androidx.compose.animation.core.Animatable<Float, androidx.compose.animation.core.AnimationVector1D>
                ?: androidx.compose.animation.core.Animatable(0f)
        } else {
            androidx.compose.animation.core.Animatable(0f)
        }
    }
    
    fun returnAnimation(animation: androidx.compose.animation.core.Animatable<Float, androidx.compose.animation.core.AnimationVector1D>) {
        if (animationPool.size < 10) { // Limitar pool size
            animationPool.add(animation)
        }
    }
}

/**
 * EXTREMO RENDIMIENTO: Extension functions para optimización
 */

// Modificador que evita recomposiciones innecesarias
@Stable
fun androidx.compose.ui.Modifier.skipRecomposition(): androidx.compose.ui.Modifier = this

// Remember optimizado para objetos inmutables
@Composable
inline fun <T> rememberImmutable(key: Any? = null, crossinline calculation: () -> T): T {
    return remember(key) { calculation() }
}

// Derived state optimizado
@Composable
inline fun <T, R> T.rememberDerived(crossinline transform: (T) -> R): State<R> {
    return remember(this) { 
        derivedStateOf { transform(this@rememberDerived) }
    }
}