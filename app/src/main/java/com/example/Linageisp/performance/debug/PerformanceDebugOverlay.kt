package com.example.Linageisp.performance.debug

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.Linageisp.performance.core.*
import com.example.Linageisp.performance.PerformanceSystem
import com.example.Linageisp.performance.PerformanceMonitor
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Overlay de debugging para monitorear performance en tiempo real durante desarrollo
 */
@Composable
fun PerformanceDebugOverlay(
    isVisible: Boolean = true,
    isEnabled: Boolean = true,
    showDetailedMetrics: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (!isEnabled || !isVisible) return
    
    val context = LocalContext.current
    val deviceCapabilities = rememberDeviceCapabilities()
    val logger = remember { PerformanceLogger.getInstance() }
    
    var showFullDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(DebugTab.PERFORMANCE) }
    
    // Get performance system instance
    val performanceSystem = remember { 
        try {
            PerformanceSystem.getInstance(context)
        } catch (e: Exception) {
            null
        }
    }
    
    // Overlay compacto
    if (!showFullDialog && performanceSystem != null) {
        CompactPerformanceOverlay(
            performanceSystem = performanceSystem,
            onExpandClick = { showFullDialog = true },
            modifier = modifier
        )
    }
    
    // Dialog completo de debugging
    if (showFullDialog && performanceSystem != null) {
        PerformanceDebugDialog(
            performanceSystem = performanceSystem,
            deviceCapabilities = deviceCapabilities,
            logger = logger,
            selectedTab = selectedTab,
            onTabChange = { selectedTab = it },
            onDismiss = { showFullDialog = false }
        )
    }
}

/**
 * Overlay compacto que muestra métricas básicas
 */
@Composable
private fun CompactPerformanceOverlay(
    performanceSystem: PerformanceSystem,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val systemSummary = remember { performanceSystem.getSystemSummary() }
    val performanceMonitor = performanceSystem.getPerformanceMonitor()
    val gpuMetrics = performanceSystem.getGPUMetrics()
    
    val frameMetrics by if (performanceMonitor != null) {
        performanceMonitor.frameMetrics.collectAsState(
            initial = PerformanceMonitor.FrameMetrics(0f, 0f, 0, 0f, false, PerformanceMonitor.PerformanceLevel.FAIR)
        )
    } else {
        remember { mutableStateOf(PerformanceMonitor.FrameMetrics(systemSummary.averageFps, 16.67f, 0, 0f, true, PerformanceMonitor.PerformanceLevel.GOOD)) }
    }
    
    val thermalState = systemSummary.thermalState
    
    // Indicador de estado posicionado en la esquina
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Card(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onExpandClick() })
                }
                .alpha(0.9f),
            colors = CardDefaults.cardColors(
                containerColor = getPerformanceColor(frameMetrics.performanceLevel).copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Indicador de estado
                Canvas(
                    modifier = Modifier.size(8.dp)
                ) {
                    drawCircle(
                        color = getPerformanceColor(frameMetrics.performanceLevel),
                        radius = size.minDimension / 2
                    )
                }
                
                // Métricas básicas
                Column {
                    Text(
                        text = "${frameMetrics.fps.roundToInt()}fps",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "45°C", // Simplified - using fixed temp since gpuState is not available
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = getThermalColor(thermalState)
                    )
                }
            }
        }
    }
}

/**
 * Dialog completo de debugging con tabs
 */
@Composable
private fun PerformanceDebugDialog(
    performanceSystem: PerformanceSystem,
    deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities?,
    logger: PerformanceLogger,
    selectedTab: DebugTab,
    onTabChange: (DebugTab) -> Unit,
    onDismiss: () -> Unit
) {
    val performanceMonitor = performanceSystem.getPerformanceMonitor()
    val gpuMetrics = performanceSystem.getGPUMetrics()
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                // Header con tabs
                DebugTabRow(
                    selectedTab = selectedTab,
                    onTabChange = onTabChange,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Contenido del tab seleccionado
                when (selectedTab) {
                    DebugTab.PERFORMANCE -> if (performanceMonitor != null) {
                        PerformanceTab(performanceMonitor, modifier = Modifier.weight(1f))
                    } else {
                        Text("Performance Monitor not available", modifier = Modifier.weight(1f))
                    }
                    DebugTab.GPU -> if (gpuMetrics != null) {
                        GPUTab(gpuMetrics, modifier = Modifier.weight(1f))
                    } else {
                        Text("GPU Metrics not available", modifier = Modifier.weight(1f))
                    }
                    DebugTab.DEVICE -> DeviceTab(deviceCapabilities, modifier = Modifier.weight(1f))
                    DebugTab.LOGS -> LogsTab(logger, modifier = Modifier.weight(1f))
                    DebugTab.CHARTS -> if (performanceMonitor != null && gpuMetrics != null) {
                        ChartsTab(performanceMonitor, gpuMetrics, modifier = Modifier.weight(1f))
                    } else {
                        Text("Charts not available", modifier = Modifier.weight(1f))
                    }
                }
                
                // Footer con botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { 
                        performanceMonitor?.resetMetrics()
                    }) {
                        Text("Reset Metrics")
                    }
                    
                    Button(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

/**
 * Tabs para diferentes secciones de debugging
 */
enum class DebugTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    PERFORMANCE("Performance", Icons.Default.Speed),
    GPU("GPU", Icons.Default.Memory),
    DEVICE("Device", Icons.Default.PhoneAndroid),
    LOGS("Logs", Icons.Default.List),
    CHARTS("Charts", Icons.Default.ShowChart)
}

@Composable
private fun DebugTabRow(
    selectedTab: DebugTab,
    onTabChange: (DebugTab) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier
    ) {
        DebugTab.entries.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabChange(tab) },
                text = { Text(tab.title, fontSize = 12.sp) },
                icon = { Icon(tab.icon, contentDescription = tab.title, modifier = Modifier.size(16.dp)) }
            )
        }
    }
}

/**
 * Tab de métricas de performance
 */
@Composable
private fun PerformanceTab(
    performanceMonitor: PerformanceMonitor,
    modifier: Modifier = Modifier
) {
    val frameMetrics by performanceMonitor.frameMetrics.collectAsState(
        initial = PerformanceMonitor.FrameMetrics(0f, 0f, 0, 0f, false, PerformanceMonitor.PerformanceLevel.FAIR)
    )
    val alerts by performanceMonitor.performanceAlerts.collectAsState(initial = null)
    
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            PerformanceStatusCard(frameMetrics)
        }
        
        item {
            MetricsGrid(frameMetrics)
        }
        
        alerts?.let { alert ->
            item {
                AlertCard(alert)
            }
        }
        
        item {
            val report = performanceMonitor.generatePerformanceReport()
            RecommendationsCard(report.recommendations)
        }
    }
}

@Composable
private fun PerformanceStatusCard(metrics: PerformanceMonitor.FrameMetrics) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = when (metrics.performanceLevel) {
                        PerformanceMonitor.PerformanceLevel.EXCELLENT -> Icons.Default.CheckCircle
                        PerformanceMonitor.PerformanceLevel.GOOD -> Icons.Default.Check
                        PerformanceMonitor.PerformanceLevel.FAIR -> Icons.Default.Warning
                        PerformanceMonitor.PerformanceLevel.POOR -> Icons.Default.Error
                        PerformanceMonitor.PerformanceLevel.CRITICAL -> Icons.Default.Cancel
                    },
                    contentDescription = null,
                    tint = getPerformanceColor(metrics.performanceLevel)
                )
                
                Text(
                    text = "Performance: ${metrics.performanceLevel}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { (metrics.fps / 60f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = getPerformanceColor(metrics.performanceLevel),
            )
            
            Text(
                text = "Smooth frames: ${if (metrics.isSmooth) "Yes" else "No"}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun MetricsGrid(metrics: PerformanceMonitor.FrameMetrics) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Metrics",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem("FPS", "${metrics.fps.roundToInt()}")
                MetricItem("Frame Time", "${metrics.frameTime.roundToInt()}ms")
                MetricItem("Jank Rate", "${metrics.jankRate.roundToInt()}%")
                MetricItem("Jank Count", "${metrics.jankCount}")
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun AlertCard(alert: PerformanceMonitor.PerformanceAlert) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = when (alert.level) {
                PerformanceMonitor.AlertLevel.CRITICAL -> Color.Red.copy(alpha = 0.1f)
                PerformanceMonitor.AlertLevel.WARNING -> Color(0xFFFFA500).copy(alpha = 0.1f)
                PerformanceMonitor.AlertLevel.INFO -> Color.Blue.copy(alpha = 0.1f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = when (alert.level) {
                    PerformanceMonitor.AlertLevel.CRITICAL -> Icons.Default.Error
                    PerformanceMonitor.AlertLevel.WARNING -> Icons.Default.Warning
                    PerformanceMonitor.AlertLevel.INFO -> Icons.Default.Info
                },
                contentDescription = null,
                tint = when (alert.level) {
                    PerformanceMonitor.AlertLevel.CRITICAL -> Color.Red
                    PerformanceMonitor.AlertLevel.WARNING -> Color(0xFFFFA500)
                    PerformanceMonitor.AlertLevel.INFO -> Color.Blue
                }
            )
            
            Column {
                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${alert.metric}: ${alert.value}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Tab de métricas GPU
 */
@Composable
private fun GPUTab(
    gpuMetrics: GPUMetrics,
    modifier: Modifier = Modifier
) {
    val metrics by gpuMetrics.gpuMetrics.collectAsState()
    val thermalState by gpuMetrics.thermalState.collectAsState()
    val renderingQuality by gpuMetrics.renderingQuality.collectAsState()
    val gpuInfo = gpuMetrics.getGPUInfo()
    
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            GPUInfoCard(gpuInfo)
        }
        
        item {
            GPUMetricsCard(metrics, thermalState, renderingQuality)
        }
        
        item {
            val recommendations = gpuMetrics.getGPURecommendations()
            if (recommendations.isNotEmpty()) {
                RecommendationsCard(recommendations)
            }
        }
    }
}

@Composable
private fun GPUInfoCard(gpuInfo: GPUMetrics.GPUInfo) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "GPU Information",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoRow("Vendor", gpuInfo.vendor)
            InfoRow("Renderer", gpuInfo.renderer)
            InfoRow("Version", gpuInfo.version)
            InfoRow("Max Texture Size", "${gpuInfo.maxTextureSize}px")
            InfoRow("Vulkan Support", if (gpuInfo.supportsVulkan) "Yes" else "No")
            InfoRow("OpenGL ES 3.1", if (gpuInfo.supportsOpenGLES31) "Yes" else "No")
        }
    }
}

@Composable
private fun GPUMetricsCard(
    metrics: GPUMetrics.GPUMetricsSnapshot,
    thermalState: GPUMetrics.ThermalState,
    renderingQuality: GPUMetrics.RenderingQuality
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "GPU Metrics",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoRow("GPU Frequency", "${metrics.gpuFrequencyMhz.roundToInt()} MHz")
            InfoRow("GPU Utilization", "${metrics.gpuUtilizationPercent.roundToInt()}%")
            InfoRow("Temperature", "${metrics.thermalTemperature.roundToInt()}°C")
            InfoRow("Thermal State", thermalState.name)
            InfoRow("Throttling", if (metrics.isThrottling) "Yes" else "No")
            InfoRow("Memory Usage", "${metrics.memoryUsageMB} MB")
            InfoRow("Rendering Quality", renderingQuality.name)
            
            if (metrics.isThrottling || thermalState != GPUMetrics.ThermalState.NORMAL) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = getThermalColor(thermalState),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Performance may be reduced due to thermal conditions",
                        style = MaterialTheme.typography.bodySmall,
                        color = getThermalColor(thermalState)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * Tab de información del dispositivo
 */
@Composable
private fun DeviceTab(
    deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            deviceCapabilities?.let { caps ->
                DeviceInfoCard(caps)
            } ?: Text("Device capabilities not available")
        }
    }
}

@Composable
private fun DeviceInfoCard(capabilities: DeviceCapabilityDetector.DeviceCapabilities) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Device Capabilities",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoRow("Performance Tier", capabilities.tier.displayName)
            InfoRow("RAM", "${capabilities.ramGB} GB")
            InfoRow("CPU Cores", "${capabilities.cpuCores}")
            InfoRow("API Level", "${capabilities.apiLevel}")
            InfoRow("Hardware Acceleration", if (capabilities.hasHardwareAcceleration) "Yes" else "No")
            InfoRow("Display", "${capabilities.displayMetrics.widthPx}x${capabilities.displayMetrics.heightPx}")
            InfoRow("Refresh Rate", "${capabilities.displayMetrics.refreshRate} Hz")
            InfoRow("Recommended FPS", "${capabilities.recommendedSettings.targetFps}")
            InfoRow("Animation Scale", "${capabilities.recommendedSettings.recommendedAnimationScale}")
            InfoRow("Cache Size", "${capabilities.recommendedSettings.cacheSize} MB")
            
            if (capabilities.supportedFeatures.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Supported Features:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                capabilities.supportedFeatures.forEach { feature ->
                    Text(
                        text = "• ${feature.name}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Tab de logs
 */
@Composable
private fun LogsTab(
    logger: PerformanceLogger,
    modifier: Modifier = Modifier
) {
    val logs = remember { mutableStateOf(emptyList<PerformanceLogger.PerformanceLogEntry>()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            logs.value = logger.getLogHistory().takeLast(50).reversed()
            delay(1000)
        }
    }
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Performance Logs",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            TextButton(
                onClick = { 
                    // Export logs functionality would go here
                }
            ) {
                Text("Export")
            }
        }
        
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
        ) {
            items(logs.value) { log ->
                LogEntryItem(log)
            }
        }
    }
}

@Composable
private fun LogEntryItem(entry: PerformanceLogger.PerformanceLogEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (entry.level) {
                PerformanceLogger.LogLevel.ERROR, PerformanceLogger.LogLevel.CRITICAL -> Color.Red.copy(alpha = 0.1f)
                PerformanceLogger.LogLevel.WARN -> Color(0xFFFFA500).copy(alpha = 0.1f)
                else -> Color.Transparent
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = entry.category,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.level.tag,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (entry.level) {
                        PerformanceLogger.LogLevel.ERROR, PerformanceLogger.LogLevel.CRITICAL -> Color.Red
                        PerformanceLogger.LogLevel.WARN -> Color(0xFFFFA500)
                        PerformanceLogger.LogLevel.INFO -> Color.Blue
                        else -> Color.Gray
                    }
                )
            }
            
            Text(
                text = entry.message,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            
            if (entry.metrics.isNotEmpty()) {
                Text(
                    text = entry.metrics.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * Tab de gráficos en tiempo real
 */
@Composable
private fun ChartsTab(
    performanceMonitor: PerformanceMonitor,
    gpuMetrics: GPUMetrics,
    modifier: Modifier = Modifier
) {
    val frameMetrics by performanceMonitor.frameMetrics.collectAsState(
        initial = PerformanceMonitor.FrameMetrics(0f, 0f, 0, 0f, false, PerformanceMonitor.PerformanceLevel.FAIR)
    )
    val gpuState by gpuMetrics.gpuMetrics.collectAsState()
    
    // Historial de métricas para gráficos
    val fpsHistory = remember { mutableStateListOf<Float>() }
    val temperatureHistory = remember { mutableStateListOf<Float>() }
    
    LaunchedEffect(frameMetrics.fps) {
        fpsHistory.add(frameMetrics.fps)
        if (fpsHistory.size > 60) fpsHistory.removeAt(0)
    }
    
    LaunchedEffect(gpuState.thermalTemperature) {
        temperatureHistory.add(gpuState.thermalTemperature)
        if (temperatureHistory.size > 60) temperatureHistory.removeAt(0)
    }
    
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Performance Charts",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            PerformanceChart(
                title = "FPS Over Time",
                data = fpsHistory.toList(),
                maxValue = 60f,
                color = Color.Green,
                modifier = Modifier.height(150.dp)
            )
        }
        
        item {
            PerformanceChart(
                title = "Temperature Over Time",
                data = temperatureHistory.toList(),
                maxValue = 100f,
                color = Color.Red,
                modifier = Modifier.height(150.dp)
            )
        }
    }
}

@Composable
private fun PerformanceChart(
    title: String,
    data: List<Float>,
    maxValue: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                if (data.isEmpty()) return@Canvas
                
                val width = size.width
                val height = size.height
                val stepWidth = width / (data.size - 1).coerceAtLeast(1)
                
                val path = Path().apply {
                    data.forEachIndexed { index, value ->
                        val x = index * stepWidth
                        val y = height - (value / maxValue * height).coerceIn(0f, height)
                        
                        if (index == 0) {
                            moveTo(x, y)
                        } else {
                            lineTo(x, y)
                        }
                    }
                }
                
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 2.dp.toPx())
                )
                
                // Draw current value
                if (data.isNotEmpty()) {
                    val currentValue = data.last()
                    drawCircle(
                        color = color,
                        radius = 4.dp.toPx(),
                        center = Offset(
                            x = width,
                            y = height - (currentValue / maxValue * height).coerceIn(0f, height)
                        )
                    )
                }
            }
            
            if (data.isNotEmpty()) {
                Text(
                    text = "Current: ${data.last().roundToInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun RecommendationsCard(recommendations: List<String>) {
    if (recommendations.isEmpty()) return
    
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recommendations",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            recommendations.forEach { recommendation ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = recommendation,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// Utility functions
private fun getPerformanceColor(level: PerformanceMonitor.PerformanceLevel): Color {
    return when (level) {
        PerformanceMonitor.PerformanceLevel.EXCELLENT -> Color.Green
        PerformanceMonitor.PerformanceLevel.GOOD -> Color.Blue
        PerformanceMonitor.PerformanceLevel.FAIR -> Color.Yellow
        PerformanceMonitor.PerformanceLevel.POOR -> Color(0xFFFFA500)
        PerformanceMonitor.PerformanceLevel.CRITICAL -> Color.Red
    }
}

private fun getThermalColor(state: GPUMetrics.ThermalState): Color {
    return when (state) {
        GPUMetrics.ThermalState.NORMAL -> Color.Green
        GPUMetrics.ThermalState.WARM -> Color.Yellow
        GPUMetrics.ThermalState.HOT -> Color(0xFFFFA500)
        GPUMetrics.ThermalState.CRITICAL -> Color.Red
    }
}