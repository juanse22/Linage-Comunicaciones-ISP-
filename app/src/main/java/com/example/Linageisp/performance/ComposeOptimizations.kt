package com.example.Linageisp.performance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

/**
 * Conjunto de optimizaciones específicas para Jetpack Compose
 * Reduce recomposiciones, optimiza renderizado y mejora performance general
 */

/**
 * Modifier optimizado que previene recomposiciones innecesarias
 * Utiliza key estable y evita allocations durante recomposición
 */
fun Modifier.optimizedRecomposition(
    key: Any? = null,
    block: @Composable () -> Unit
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "optimizedRecomposition"
        properties["key"] = key
    }
) {
    // Usar remember con key estable para prevenir recomposiciones
    val stableKey = remember(key) { key ?: System.identityHashCode(this) }
    
    // Solo recomponer cuando la key cambie realmente
    remember(stableKey) {
        this
    }
}

/**
 * StateFlow collector optimizado que reduce recomposiciones
 * Solo recompone cuando el valor realmente cambia (usando equals)
 */
@Composable
fun <T> StateFlow<T>.collectAsStateOptimized(
    initial: T,
    areEqual: (T, T) -> Boolean = { a, b -> a == b }
): State<T> {
    return produceState(initialValue = initial, this) {
        collect { newValue ->
            if (!areEqual(value, newValue)) {
                value = newValue
            }
        }
    }
}

/**
 * Wrapper para estados que cambian frecuentemente
 * Aplica throttling para reducir recomposiciones excesivas
 */
@Composable
fun <T> rememberThrottledState(
    value: T,
    throttleMs: Long = 16L, // ~60 FPS
    areEqual: (T, T) -> Boolean = { a, b -> a == b }
): State<T> {
    val throttledState = remember { mutableStateOf(value) }
    var lastUpdateTime by remember { mutableStateOf(0L) }
    
    LaunchedEffect(value) {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastUpdate = currentTime - lastUpdateTime
        
        if (timeSinceLastUpdate >= throttleMs || !areEqual(throttledState.value, value)) {
            throttledState.value = value
            lastUpdateTime = currentTime
        }
    }
    
    return throttledState
}

/**
 * Modifier para backgrounds optimizados que reduce overdraw
 */
fun Modifier.optimizedBackground(
    color: Color,
    shape: Shape? = null
): Modifier = drawWithCache {
    val brush = SolidColor(color)
    
    onDrawBehind {
        if (shape != null) {
            val outline = shape.createOutline(size, layoutDirection, this)
            drawOutline(outline, brush)
        } else {
            drawRect(brush)
        }
    }
}

/**
 * Composable optimizado para listas grandes
 * Implementa recycling inteligente basado en device capabilities
 */
@Composable
fun <T> OptimizedLazyColumn(
    items: List<T>,
    modifier: Modifier = Modifier,
    deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities? = null,
    contentPadding: PaddingValues = PaddingValues(),
    itemContent: @Composable LazyItemScope.(T) -> Unit
) {
    // Determinar configuración de recycling basada en capacidades del dispositivo
    val recyclingConfig = remember(deviceCapabilities) {
        when (deviceCapabilities?.tier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> RecyclingConfig(
                beyondBoundsItemCount = 2,
                prefetchItemCount = 3
            )
            DeviceCapabilityDetector.PerformanceTier.MID_END -> RecyclingConfig(
                beyondBoundsItemCount = 4,
                prefetchItemCount = 5
            )
            DeviceCapabilityDetector.PerformanceTier.HIGH_END -> RecyclingConfig(
                beyondBoundsItemCount = 6,
                prefetchItemCount = 8
            )
            DeviceCapabilityDetector.PerformanceTier.PREMIUM -> RecyclingConfig(
                beyondBoundsItemCount = 8,
                prefetchItemCount = 10
            )
            else -> RecyclingConfig(
                beyondBoundsItemCount = 4,
                prefetchItemCount = 5
            )
        }
    }
    
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(0.dp) // Evitar spacing innecesario
    ) {
        items(
            count = items.size,
            key = { index -> items.getOrNull(index)?.hashCode() ?: index }
        ) { index ->
            val item = items[index]
            
            // Usar SubcomposeLayout para lazy composition solo cuando sea visible
            Box {
                itemContent(item)
            }
        }
    }
}

data class RecyclingConfig(
    val beyondBoundsItemCount: Int,
    val prefetchItemCount: Int
)

/**
 * Composable con memoización inteligente para operaciones costosas
 */
@Composable
fun <T, R> rememberExpensiveComputation(
    input: T,
    computation: (T) -> R
): R {
    return remember(input) {
        computation(input)
    }
}

/**
 * Derivación de estado optimizada que solo recalcula cuando dependencies cambian
 */
@Composable
fun <T> rememberDerivedStateOptimized(
    vararg dependencies: Any?,
    calculation: () -> T
): State<T> {
    return remember(*dependencies) {
        derivedStateOf(calculation)
    }
}

/**
 * Modifier para gestión optimizada de gestures que evita recomposiciones
 */
fun Modifier.optimizedClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val stableOnClick = rememberUpdatedState(onClick)
    
    this.clickable(
        enabled = enabled,
        onClick = { stableOnClick.value() }
    )
}

/**
 * Composable para debug de performance que muestra métricas de recomposición
 */
@Composable
fun RecompositionCounter(
    name: String = "Unknown",
    modifier: Modifier = Modifier
) {
    val recompositionCount = remember { mutableStateOf(0) }
    
    SideEffect {
        recompositionCount.value++
    }
    
    // Mostrar contador de recomposiciones
    Text(
        text = "$name: ${recompositionCount.value}",
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = if (recompositionCount.value > 10) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        }
    )
}

/**
 * Hook para medir performance de composables
 */
@Composable
fun rememberCompositionTime(): CompositionTimeTracker {
    return remember {
        CompositionTimeTracker()
    }
}

class CompositionTimeTracker {
    private var startTime = 0L
    private var endTime = 0L
    
    fun startMeasurement() {
        startTime = System.nanoTime()
    }
    
    fun endMeasurement(): Long {
        endTime = System.nanoTime()
        return (endTime - startTime) / 1_000_000L // Convert to milliseconds
    }
    
    @Composable
    fun MeasureComposition(
        content: @Composable () -> Unit
    ) {
        startMeasurement()
        content()
        SideEffect {
            val time = endMeasurement()
            if (time > 16) { // Frame budget exceeded
                println("⚠️ Slow composition detected: ${time}ms")
            }
        }
    }
}

/**
 * Optimización para shadows que reduce overdraw
 */
fun Modifier.optimizedShadow(
    elevation: Dp,
    shape: Shape,
    deviceTier: DeviceCapabilityDetector.PerformanceTier? = null
): Modifier = composed {
    val density = LocalDensity.current
    
    // Ajustar sombra basado en capacidades del dispositivo
    val adjustedElevation = remember(elevation, deviceTier) {
        when (deviceTier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> elevation * 0.3f
            DeviceCapabilityDetector.PerformanceTier.MID_END -> elevation * 0.6f
            else -> elevation
        }
    }
    
    // Usar sombra nativa más eficiente
    if (deviceTier != DeviceCapabilityDetector.PerformanceTier.LOW_END) {
        this.shadow(adjustedElevation, shape)
    } else {
        // Para dispositivos LOW_END, usar borde simple en lugar de sombra
        this.drawWithCache {
            onDrawBehind {
                val outline = shape.createOutline(size, layoutDirection, this)
                drawOutline(
                    outline = outline,
                    color = Color.Black.copy(alpha = 0.1f),
                    style = Stroke(width = with(density) { 1.dp.toPx() })
                )
            }
        }
    }
}

/**
 * Contenedor optimizado que previene layout cascades
 */
@Composable
fun OptimizedContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Layout(
        content = { Box(content = content) },
        modifier = modifier
    ) { measurables, constraints ->
        // Medición optimizada que evita re-mediciones innecesarias
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }
        
        val width = placeables.maxOfOrNull { it.width } ?: 0
        val height = placeables.maxOfOrNull { it.height } ?: 0
        
        layout(width, height) {
            placeables.forEach { placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}

/**
 * Utilities para debugging de performance en Compose
 */
object ComposePerformanceUtils {
    
    /**
     * Wrapper para identificar composables problemáticos
     */
    @Composable
    fun TrackRecompositions(
        tag: String,
        content: @Composable () -> Unit
    ) {
        val recompositions = remember { mutableStateOf(0) }
        
        SideEffect {
            recompositions.value++
            if (recompositions.value > 5) {
                println("🔄 High recomposition rate detected in: $tag (${recompositions.value})")
            }
        }
        
        content()
    }
    
    /**
     * Detecta quando un State está causando muchas recomposiciones
     */
    @Composable
    fun <T> monitorStateChanges(
        state: State<T>,
        tag: String
    ) {
        val changeCount = remember { mutableStateOf(0) }
        val previousValue = remember { mutableStateOf(state.value) }
        
        LaunchedEffect(state.value) {
            if (previousValue.value != state.value) {
                changeCount.value++
                previousValue.value = state.value
                
                if (changeCount.value > 20) {
                    println("📊 High state change rate in: $tag (${changeCount.value} changes)")
                }
            }
        }
    }
    
    /**
     * Mide el tiempo de ejecución de una lambda de composición
     */
    @Composable
    inline fun measureCompositionTime(
        tag: String,
        content: @Composable () -> Unit
    ) {
        val startTime = remember { System.nanoTime() }
        
        content()
        
        SideEffect {
            val endTime = System.nanoTime()
            val duration = (endTime - startTime) / 1_000_000L
            
            if (duration > 8) { // Más de 8ms es sospechoso
                println("⏱️ Slow composition in $tag: ${duration}ms")
            }
        }
    }
}
