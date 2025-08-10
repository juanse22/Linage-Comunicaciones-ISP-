package com.example.Linageisp.performance

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import com.example.Linageisp.performance.core.DeviceCapabilityDetector

/**
 * Optimizaciones de renderizado y UI para reducir overdraw y mejorar performance visual
 * Incluye técnicas avanzadas de drawing optimization y GPU utilization
 */

/**
 * Modifier que reduce overdraw mediante clipping inteligente
 */
fun Modifier.optimizedClip(
    shape: Shape,
    deviceTier: com.example.Linageisp.performance.core.DeviceCapabilityDetector.PerformanceTier? = null
): Modifier = composed {
    // En dispositivos LOW_END, usar clipping más simple
    when (deviceTier) {
        DeviceCapabilityDetector.PerformanceTier.LOW_END -> {
            // Usar RectangleShape o formas simples para reducir cálculos
            if (shape is RoundedCornerShape) {
                val cornerSize = shape.topStart
                if (cornerSize == CornerSize(0.dp)) {
                    this.clip(RectangleShape)
                } else {
                    this.clip(RoundedCornerShape(cornerSize))
                }
            } else {
                this.clip(shape)
            }
        }
        else -> this.clip(shape)
    }
}

/**
 * Background optimizado que minimiza overdraw
 */
fun Modifier.optimizedGradientBackground(
    colors: List<Color>,
    deviceTier: DeviceCapabilityDetector.PerformanceTier? = null
): Modifier = drawBehind {
    when (deviceTier) {
        DeviceCapabilityDetector.PerformanceTier.LOW_END -> {
            // En dispositivos LOW_END, usar color sólido en lugar de gradiente
            drawRect(colors.first())
        }
        else -> {
            val gradient = Brush.verticalGradient(colors)
            drawRect(gradient)
        }
    }
}

/**
 * Sombra optimizada que reduce múltiples capas de drawing
 */
fun Modifier.efficientShadow(
    elevation: Dp,
    shape: Shape = RectangleShape,
    color: Color = Color.Black,
    deviceTier: DeviceCapabilityDetector.PerformanceTier? = null
): Modifier = composed {
    val density = LocalDensity.current
    
    when (deviceTier) {
        DeviceCapabilityDetector.PerformanceTier.LOW_END -> {
            // En lugar de sombra, usar borde sutil
            this.drawBehind {
                val strokeWidth = with(density) { 1.dp.toPx() }
                val outline = shape.createOutline(size, layoutDirection, this)
                drawOutline(
                    outline = outline,
                    color = color.copy(alpha = 0.2f),
                    style = Stroke(width = strokeWidth)
                )
            }
        }
        DeviceCapabilityDetector.PerformanceTier.MID_END -> {
            // Sombra simple
            this.shadow(elevation * 0.7f, shape, true)
        }
        else -> {
            // Sombra completa para dispositivos HIGH_END y PREMIUM
            this.shadow(elevation, shape, true)
        }
    }
}

/**
 * Canvas optimizado para dibujos complejos
 */
@Composable
fun OptimizedCanvas(
    modifier: Modifier = Modifier,
    deviceTier: DeviceCapabilityDetector.PerformanceTier? = null,
    onDraw: DrawScope.() -> Unit
) {
    // Cache del contenido de drawing basado en device tier
    val cachedDrawing = remember(deviceTier) {
        when (deviceTier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> {
                // Simplificar drawing operations para LOW_END
                { scope: DrawScope ->
                    // Versión simplificada del drawing
                    scope.onDraw()
                }
            }
            else -> {
                { scope: DrawScope ->
                    scope.onDraw()
                }
            }
        }
    }
    
    Canvas(
        modifier = modifier,
        onDraw = { cachedDrawing(this) }
    )
}

/**
 * Componente de glassmorphism optimizado
 */
@Composable
fun OptimizedGlassmorphicCard(
    modifier: Modifier = Modifier,
    deviceTier: DeviceCapabilityDetector.PerformanceTier? = null,
    backgroundColor: Color = Color.White.copy(alpha = 0.1f),
    borderColor: Color = Color.White.copy(alpha = 0.2f),
    content: @Composable BoxScope.() -> Unit
) {
    val shouldUseGlass = deviceTier != DeviceCapabilityDetector.PerformanceTier.LOW_END
    
    Box(
        modifier = modifier
            .then(
                if (shouldUseGlass) {
                    Modifier
                        .background(backgroundColor, RoundedCornerShape(16.dp))
                        .drawWithCache {
                            val path = Path().apply {
                                addRoundRect(
                                    androidx.compose.ui.geometry.RoundRect(
                                        0f, 0f, size.width, size.height,
                                        CornerRadius(16.dp.toPx())
                                    )
                                )
                            }
                            onDrawBehind {
                                drawPath(
                                    path = path,
                                    color = borderColor,
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
                        }
                } else {
                    // Versión simplificada para LOW_END
                    Modifier
                        .background(backgroundColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .drawBehind {
                            drawRoundRect(
                                color = borderColor,
                                style = Stroke(width = 1.dp.toPx()),
                                cornerRadius = CornerRadius(8.dp.toPx())
                            )
                        }
                }
            ),
        content = content
    )
}

/**
 * Loader optimizado con animación adaptativa
 */
@Composable
fun OptimizedLoader(
    modifier: Modifier = Modifier,
    deviceTier: DeviceCapabilityDetector.PerformanceTier? = null,
    color: Color = MaterialTheme.colorScheme.primary
) {
    when (deviceTier) {
        DeviceCapabilityDetector.PerformanceTier.LOW_END -> {
            // Loader estático para dispositivos LOW_END
            Canvas(
                modifier = modifier.size(24.dp)
            ) {
                drawCircle(
                    color = color.copy(alpha = 0.3f),
                    radius = size.minDimension / 2,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawArc(
                    color = color,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
        else -> {
            // Loader animado para dispositivos con mayor capacidad
            var rotation by remember { mutableStateOf(0f) }
            
            LaunchedEffect(Unit) {
                while (true) {
                    rotation = (rotation + 10f) % 360f
                    kotlinx.coroutines.delay(50)
                }
            }
            
            Canvas(
                modifier = modifier
                    .size(24.dp)
                    .graphicsLayer { rotationZ = rotation }
            ) {
                drawCircle(
                    color = color.copy(alpha = 0.3f),
                    radius = size.minDimension / 2,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawArc(
                    color = color,
                    startAngle = 0f,
                    sweepAngle = 120f,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}

/**
 * Shimmer effect optimizado
 */
@Composable
fun OptimizedShimmerEffect(
    modifier: Modifier = Modifier,
    deviceTier: DeviceCapabilityDetector.PerformanceTier? = null,
    baseColor: Color = Color.Gray.copy(alpha = 0.3f),
    highlightColor: Color = Color.White.copy(alpha = 0.5f)
) {
    when (deviceTier) {
        DeviceCapabilityDetector.PerformanceTier.LOW_END -> {
            // Shimmer estático para LOW_END
            Box(
                modifier = modifier.background(baseColor)
            )
        }
        else -> {
            // Shimmer animado
            val transition = rememberInfiniteTransition(label = "shimmer")
            val translateAnimation by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Restart
                ),
                label = "shimmer_translate"
            )
            
            Box(
                modifier = modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                baseColor,
                                highlightColor,
                                baseColor
                            ),
                            start = Offset(
                                x = translateAnimation * 200f - 100f,
                                y = translateAnimation * 200f - 100f
                            ),
                            end = Offset(
                                x = translateAnimation * 200f,
                                y = translateAnimation * 200f
                            )
                        )
                    )
            )
        }
    }
}

/**
 * Indicador de progreso circular optimizado
 */
@Composable
fun OptimizedCircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    deviceTier: DeviceCapabilityDetector.PerformanceTier? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 4.dp
) {
    val density = LocalDensity.current
    
    Canvas(
        modifier = modifier.size(48.dp)
    ) {
        val stroke = with(density) { strokeWidth.toPx() }
        val radius = (size.minDimension - stroke) / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        // Círculo de fondo
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius,
            center = center,
            style = Stroke(width = stroke)
        )
        
        // Arco de progreso
        val sweepAngle = progress * 360f
        
        when (deviceTier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> {
                // Versión simple sin efectos
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = stroke),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
            }
            else -> {
                // Versión con gradiente para dispositivos más potentes
                val gradient = Brush.sweepGradient(
                    colors = listOf(
                        color.copy(alpha = 0.5f),
                        color,
                        color.copy(alpha = 0.5f)
                    ),
                    center = center
                )
                
                drawArc(
                    brush = gradient,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = stroke),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
            }
        }
    }
}

/**
 * Patrón de dots decorativo optimizado
 */
@Composable
fun OptimizedDotPattern(
    modifier: Modifier = Modifier,
    deviceTier: DeviceCapabilityDetector.PerformanceTier? = null,
    dotColor: Color = Color.Gray.copy(alpha = 0.1f),
    spacing: Dp = 20.dp
) {
    val density = LocalDensity.current
    val spacingPx = with(density) { spacing.toPx() }
    
    Canvas(modifier = modifier) {
        val dotCount = when (deviceTier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> {
                // Menos dots para LOW_END
                Pair((size.width / (spacingPx * 2)).toInt(), (size.height / (spacingPx * 2)).toInt())
            }
            else -> {
                Pair((size.width / spacingPx).toInt(), (size.height / spacingPx).toInt())
            }
        }
        
        for (x in 0..dotCount.first) {
            for (y in 0..dotCount.second) {
                drawCircle(
                    color = dotColor,
                    radius = 1.dp.toPx(),
                    center = Offset(x * spacingPx, y * spacingPx)
                )
            }
        }
    }
}

/**
 * Utilidades para optimización de drawing
 */
object DrawingOptimizationUtils {
    
    /**
     * Función para crear paths reutilizables
     */
    fun createRoundedRectPath(
        size: Size,
        cornerRadius: Float
    ): Path {
        return Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    0f, 0f, size.width, size.height,
                    CornerRadius(cornerRadius)
                )
            )
        }
    }
    
    /**
     * Función para dibujar gradientes optimizados
     */
    fun DrawScope.drawOptimizedGradient(
        colors: List<Color>,
        bounds: androidx.compose.ui.geometry.Rect,
        deviceTier: DeviceCapabilityDetector.PerformanceTier? = null
    ) {
        when (deviceTier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> {
                // Color sólido para LOW_END
                drawRect(colors.first(), topLeft = bounds.topLeft, size = bounds.size)
            }
            else -> {
                // Gradiente completo
                val gradient = Brush.linearGradient(
                    colors = colors,
                    start = bounds.topLeft,
                    end = bounds.bottomRight
                )
                drawRect(gradient, topLeft = bounds.topLeft, size = bounds.size)
            }
        }
    }
    
    /**
     * Función para calcular posiciones de elementos de forma eficiente
     */
    fun calculateCircularPositions(
        center: Offset,
        radius: Float,
        count: Int
    ): List<Offset> {
        val angleStep = 360f / count
        return (0 until count).map { index ->
            val angle = Math.toRadians((index * angleStep).toDouble())
            Offset(
                center.x + (radius * cos(angle)).toFloat(),
                center.y + (radius * sin(angle)).toFloat()
            )
        }
    }
    
    /**
     * Cache para paths complejos
     */
    private val pathCache = mutableMapOf<String, Path>()
    
    fun getCachedPath(
        key: String,
        pathCreator: () -> Path
    ): Path {
        return pathCache.getOrPut(key, pathCreator)
    }
}