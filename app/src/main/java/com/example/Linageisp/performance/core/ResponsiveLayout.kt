package com.example.Linageisp.performance.core

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import android.content.Context
import androidx.compose.ui.platform.LocalContext

/**
 * Sistema de layouts responsivos adaptativos basados en tamaño de pantalla y capacidades del dispositivo
 */

/**
 * Configuración de breakpoints responsivos
 */
data class ResponsiveBreakpoints(
    val compact: Dp = 0.dp,      // Hasta 600dp (teléfonos)
    val medium: Dp = 600.dp,     // 600-840dp (tablets pequeñas)
    val expanded: Dp = 840.dp    // 840dp+ (tablets grandes, desktop)
) {
    companion object {
        val Default = ResponsiveBreakpoints()
        
        // Breakpoints Material Design 3
        val MaterialDesign3 = ResponsiveBreakpoints(
            compact = 0.dp,
            medium = 600.dp,
            expanded = 840.dp
        )
        
        // Breakpoints personalizados más granulares
        val Extended = ResponsiveBreakpoints(
            compact = 0.dp,
            medium = 480.dp,
            expanded = 768.dp
        )
    }
}

/**
 * Configuración de columnas adaptativas
 */
data class AdaptiveColumns(
    val compact: Int,
    val medium: Int, 
    val expanded: Int
) {
    companion object {
        val Default = AdaptiveColumns(
            compact = 1,
            medium = 2,
            expanded = 3
        )
        
        val Grid = AdaptiveColumns(
            compact = 2,
            medium = 3,
            expanded = 4
        )
        
        val Cards = AdaptiveColumns(
            compact = 1,
            medium = 2,
            expanded = 4
        )
    }
}

/**
 * Clase que representa el tamaño de ventana actual
 */
enum class WindowSizeClass {
    COMPACT,    // < 600dp
    MEDIUM,     // 600-840dp  
    EXPANDED    // > 840dp
}

/**
 * Hook para obtener el tamaño de ventana actual
 */
@Composable
fun rememberWindowSizeClass(
    breakpoints: ResponsiveBreakpoints = ResponsiveBreakpoints.Default
): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    
    return remember(screenWidthDp, breakpoints) {
        when {
            screenWidthDp < breakpoints.medium -> WindowSizeClass.COMPACT
            screenWidthDp < breakpoints.expanded -> WindowSizeClass.MEDIUM
            else -> WindowSizeClass.EXPANDED
        }
    }
}

/**
 * Hook para obtener información detallada de la ventana
 */
@Composable
fun rememberWindowInfo(): WindowInfo {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    
    return remember(configuration, density) {
        WindowInfo(
            screenWidthPx = configuration.screenWidthDp * density.density,
            screenHeightPx = configuration.screenHeightDp * density.density,
            screenWidthDp = configuration.screenWidthDp.dp,
            screenHeightDp = configuration.screenHeightDp.dp,
            densityDpi = configuration.densityDpi,
            orientation = if (configuration.screenWidthDp > configuration.screenHeightDp) {
                WindowOrientation.LANDSCAPE
            } else {
                WindowOrientation.PORTRAIT
            }
        )
    }
}

data class WindowInfo(
    val screenWidthPx: Float,
    val screenHeightPx: Float,
    val screenWidthDp: Dp,
    val screenHeightDp: Dp,
    val densityDpi: Int,
    val orientation: WindowOrientation
) {
    val aspectRatio: Float get() = screenWidthPx / screenHeightPx
    val isTablet: Boolean get() = screenWidthDp >= 600.dp
    val isLargeScreen: Boolean get() = screenWidthDp >= 840.dp
}

enum class WindowOrientation {
    PORTRAIT,
    LANDSCAPE
}

/**
 * Composable para layouts adaptativos basados en columnas
 */
@Composable
fun <T> AdaptiveGrid(
    items: List<T>,
    modifier: Modifier = Modifier,
    columns: AdaptiveColumns = AdaptiveColumns.Default,
    breakpoints: ResponsiveBreakpoints = ResponsiveBreakpoints.Default,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    deviceTier: DeviceCapabilityDetector.PerformanceTier? = null,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable BoxScope.(item: T) -> Unit
) {
    val windowSize = rememberWindowSizeClass(breakpoints)
    
    // Ajustar número de columnas basado en performance del dispositivo
    val adjustedColumns = when (deviceTier) {
        DeviceCapabilityDetector.PerformanceTier.LOW_END -> {
            // Reducir columnas para dispositivos LOW_END
            AdaptiveColumns(
                compact = maxOf(1, columns.compact - 1),
                medium = maxOf(1, columns.medium - 1),
                expanded = maxOf(2, columns.expanded - 2)
            )
        }
        else -> columns
    }
    
    val columnCount = when (windowSize) {
        WindowSizeClass.COMPACT -> adjustedColumns.compact
        WindowSizeClass.MEDIUM -> adjustedColumns.medium
        WindowSizeClass.EXPANDED -> adjustedColumns.expanded
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding
    ) {
        items(
            items = items,
            key = key
        ) { item ->
            Box {
                itemContent(item)
            }
        }
    }
}

/**
 * Layout responsivo con paneles laterales
 */
@Composable
fun ResponsiveSidePanel(
    mainContent: @Composable BoxScope.() -> Unit,
    sideContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    showSidePanel: Boolean = true,
    sidePanelWidth: Dp = 300.dp,
    breakpoints: ResponsiveBreakpoints = ResponsiveBreakpoints.Default
) {
    val windowSize = rememberWindowSizeClass(breakpoints)
    val shouldShowSidePanel = showSidePanel && windowSize != WindowSizeClass.COMPACT
    
    if (shouldShowSidePanel) {
        Row(modifier = modifier.fillMaxSize()) {
            // Panel principal
            Box(
                modifier = Modifier.weight(1f)
            ) {
                mainContent()
            }
            
            // Panel lateral
            Box(
                modifier = Modifier.width(sidePanelWidth)
            ) {
                sideContent()
            }
        }
    } else {
        // Solo contenido principal en pantallas compactas
        Box(modifier = modifier.fillMaxSize()) {
            mainContent()
        }
    }
}

/**
 * Lista responsiva que se adapta entre LazyColumn y LazyRow
 */
@Composable
fun <T> ResponsiveList(
    items: List<T>,
    modifier: Modifier = Modifier,
    forceOrientation: ListOrientation? = null,
    breakpoints: ResponsiveBreakpoints = ResponsiveBreakpoints.Default,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable (item: T) -> Unit
) {
    val windowInfo = rememberWindowInfo()
    val windowSize = rememberWindowSizeClass(breakpoints)
    
    val listOrientation = forceOrientation ?: when {
        windowSize == WindowSizeClass.COMPACT && windowInfo.orientation == WindowOrientation.PORTRAIT -> ListOrientation.VERTICAL
        windowSize != WindowSizeClass.COMPACT || windowInfo.orientation == WindowOrientation.LANDSCAPE -> ListOrientation.HORIZONTAL
        else -> ListOrientation.VERTICAL
    }
    
    when (listOrientation) {
        ListOrientation.VERTICAL -> {
            LazyColumn(
                modifier = modifier,
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = items,
                    key = key
                ) { item ->
                    itemContent(item)
                }
            }
        }
        ListOrientation.HORIZONTAL -> {
            LazyRow(
                modifier = modifier,
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = items,
                    key = key
                ) { item ->
                    itemContent(item)
                }
            }
        }
    }
}

enum class ListOrientation {
    VERTICAL,
    HORIZONTAL
}

/**
 * Espaciado adaptativo basado en tamaño de pantalla
 */
@Composable
fun adaptiveSpacing(
    compact: Dp = 8.dp,
    medium: Dp = 12.dp,
    expanded: Dp = 16.dp,
    breakpoints: ResponsiveBreakpoints = ResponsiveBreakpoints.Default
): Dp {
    val windowSize = rememberWindowSizeClass(breakpoints)
    
    return when (windowSize) {
        WindowSizeClass.COMPACT -> compact
        WindowSizeClass.MEDIUM -> medium
        WindowSizeClass.EXPANDED -> expanded
    }
}

/**
 * Padding adaptativo basado en tamaño de pantalla
 */
@Composable
fun adaptivePadding(
    compact: PaddingValues = PaddingValues(16.dp),
    medium: PaddingValues = PaddingValues(24.dp),
    expanded: PaddingValues = PaddingValues(32.dp),
    breakpoints: ResponsiveBreakpoints = ResponsiveBreakpoints.Default
): PaddingValues {
    val windowSize = rememberWindowSizeClass(breakpoints)
    
    return when (windowSize) {
        WindowSizeClass.COMPACT -> compact
        WindowSizeClass.MEDIUM -> medium
        WindowSizeClass.EXPANDED -> expanded
    }
}

/**
 * Card responsiva con tamaño adaptativo
 */
@Composable
fun AdaptiveCard(
    modifier: Modifier = Modifier,
    maxWidth: Dp = Dp.Unspecified,
    breakpoints: ResponsiveBreakpoints = ResponsiveBreakpoints.Default,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    content: @Composable ColumnScope.() -> Unit
) {
    val windowSize = rememberWindowSizeClass(breakpoints)
    val windowInfo = rememberWindowInfo()
    
    // Calcular ancho máximo basado en el tamaño de pantalla
    val adaptiveMaxWidth = when {
        maxWidth != Dp.Unspecified -> maxWidth
        windowSize == WindowSizeClass.COMPACT -> Dp.Unspecified
        windowSize == WindowSizeClass.MEDIUM -> 600.dp
        else -> 800.dp
    }
    
    val cardModifier = if (adaptiveMaxWidth != Dp.Unspecified) {
        modifier.widthIn(max = adaptiveMaxWidth)
    } else {
        modifier
    }
    
    Card(
        modifier = cardModifier,
        colors = colors,
        elevation = elevation
    ) {
        Column(
            modifier = Modifier.padding(adaptivePadding()),
            content = content
        )
    }
}

/**
 * Dialog responsivo que se adapta al tamaño de pantalla
 */
@Composable
fun ResponsiveDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val windowInfo = rememberWindowInfo()
    val windowSize = rememberWindowSizeClass()
    
    when (windowSize) {
        WindowSizeClass.COMPACT -> {
            // En pantallas compactas, usar dialog de pantalla completa
            if (windowInfo.orientation == WindowOrientation.PORTRAIT) {
                AlertDialog(
                    onDismissRequest = onDismissRequest,
                    confirmButton = { },
                    modifier = modifier.fillMaxWidth(0.95f),
                    text = content
                )
            } else {
                AlertDialog(
                    onDismissRequest = onDismissRequest,
                    confirmButton = { },
                    modifier = modifier.fillMaxWidth(0.8f),
                    text = content
                )
            }
        }
        else -> {
            // En pantallas más grandes, usar dialog centrado
            AlertDialog(
                onDismissRequest = onDismissRequest,
                confirmButton = { },
                modifier = modifier.widthIn(max = 560.dp),
                text = content
            )
        }
    }
}

/**
 * Bottom Sheet responsivo - simplificado para evitar dependencias problemáticas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponsiveBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val windowSize = rememberWindowSizeClass()
    
    when (windowSize) {
        WindowSizeClass.COMPACT -> {
            // Modal bottom sheet simplificado en pantallas compactas
            ModalBottomSheet(
                onDismissRequest = onDismissRequest,
                modifier = modifier,
                content = content
            )
        }
        else -> {
            // Modal bottom sheet en pantallas más grandes
            ModalBottomSheet(
                onDismissRequest = onDismissRequest,
                modifier = modifier.widthIn(max = 640.dp),
                content = content
            )
        }
    }
}

/**
 * Extensiones para modificadores adaptativos
 */
fun Modifier.adaptiveWidth(
    compact: Dp = Dp.Unspecified,
    medium: Dp = Dp.Unspecified,
    expanded: Dp = Dp.Unspecified
) = this.then(
    AdaptiveWidthModifier(compact, medium, expanded)
)

private data class AdaptiveWidthModifier(
    val compact: Dp,
    val medium: Dp,
    val expanded: Dp
) : androidx.compose.ui.layout.LayoutModifier {
    
    @Composable
    fun getWidth(): Dp {
        val windowSize = rememberWindowSizeClass()
        return when (windowSize) {
            WindowSizeClass.COMPACT -> compact
            WindowSizeClass.MEDIUM -> medium
            WindowSizeClass.EXPANDED -> expanded
        }
    }
    
    override fun androidx.compose.ui.layout.MeasureScope.measure(
        measurable: androidx.compose.ui.layout.Measurable,
        constraints: androidx.compose.ui.unit.Constraints
    ): androidx.compose.ui.layout.MeasureResult {
        // Implementación simplificada - en uso real necesitaría más lógica
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

/**
 * Utilidades para detectar características de la pantalla
 */
object ResponsiveUtils {
    
    /**
     * Verifica si es una tablet
     */
    @Composable
    fun isTablet(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp >= 600
    }
    
    /**
     * Verifica si es una pantalla grande
     */
    @Composable
    fun isLargeScreen(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp >= 840
    }
    
    /**
     * Obtiene la densidad de la pantalla
     */
    @Composable
    fun getScreenDensity(): Float {
        val density = LocalDensity.current
        return density.density
    }
    
    /**
     * Calcula el número óptimo de columnas basado en ancho disponible y ancho mínimo de elemento
     */
    @Composable
    fun calculateOptimalColumns(
        minItemWidth: Dp = 200.dp,
        spacing: Dp = 8.dp,
        contentPadding: PaddingValues = PaddingValues(16.dp)
    ): Int {
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        
        return remember(configuration.screenWidthDp, minItemWidth, spacing, contentPadding) {
            val availableWidthDp = configuration.screenWidthDp.dp - contentPadding.calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr) - contentPadding.calculateEndPadding(androidx.compose.ui.unit.LayoutDirection.Ltr)
            val itemWithSpacing = minItemWidth + spacing
            maxOf(1, (availableWidthDp / itemWithSpacing).toInt())
        }
    }
    
    /**
     * Obtiene configuración recomendada basada en el dispositivo
     */
    @Composable
    fun getRecommendedConfiguration(
        deviceTier: DeviceCapabilityDetector.PerformanceTier?
    ): ResponsiveConfiguration {
        val windowSize = rememberWindowSizeClass()
        val isTablet = isTablet()
        
        return remember(windowSize, deviceTier, isTablet) {
            when (deviceTier) {
                DeviceCapabilityDetector.PerformanceTier.LOW_END -> ResponsiveConfiguration(
                    maxColumns = if (isTablet) 2 else 1,
                    enableAnimations = false,
                    reducedSpacing = true,
                    simplifiedLayouts = true
                )
                DeviceCapabilityDetector.PerformanceTier.MID_END -> ResponsiveConfiguration(
                    maxColumns = when (windowSize) {
                        WindowSizeClass.COMPACT -> 2
                        WindowSizeClass.MEDIUM -> 3
                        WindowSizeClass.EXPANDED -> 4
                    },
                    enableAnimations = true,
                    reducedSpacing = false,
                    simplifiedLayouts = false
                )
                else -> ResponsiveConfiguration(
                    maxColumns = when (windowSize) {
                        WindowSizeClass.COMPACT -> 2
                        WindowSizeClass.MEDIUM -> 4
                        WindowSizeClass.EXPANDED -> 6
                    },
                    enableAnimations = true,
                    reducedSpacing = false,
                    simplifiedLayouts = false
                )
            }
        }
    }
}

data class ResponsiveConfiguration(
    val maxColumns: Int,
    val enableAnimations: Boolean,
    val reducedSpacing: Boolean,
    val simplifiedLayouts: Boolean
)

/**
 * Composable para crear layouts adaptativos complejos
 */
@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    deviceTier: DeviceCapabilityDetector.PerformanceTier? = null,
    compactContent: @Composable BoxScope.() -> Unit,
    mediumContent: @Composable BoxScope.() -> Unit = compactContent,
    expandedContent: @Composable BoxScope.() -> Unit = mediumContent
) {
    val windowSize = rememberWindowSizeClass()
    
    Box(modifier = modifier) {
        when (windowSize) {
            WindowSizeClass.COMPACT -> compactContent()
            WindowSizeClass.MEDIUM -> mediumContent()
            WindowSizeClass.EXPANDED -> expandedContent()
        }
    }
}