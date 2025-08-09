package com.example.Linageisp.performance

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import coil.transform.Transformation
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

/**
 * Sistema de carga lazy optimizado para imágenes con adaptación por dispositivo
 * Integra Coil con optimizaciones personalizadas de memoria y rendimiento
 */
class LazyImageLoader private constructor(
    private val context: Context,
    private val memoryManager: MemoryResourceManager,
    private val deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities
) {
    
    companion object {
        @Volatile
        private var INSTANCE: LazyImageLoader? = null
        
        fun getInstance(
            context: Context,
            memoryManager: MemoryResourceManager,
            deviceCapabilities: DeviceCapabilityDetector.DeviceCapabilities
        ): LazyImageLoader {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LazyImageLoader(
                    context.applicationContext,
                    memoryManager,
                    deviceCapabilities
                ).also { INSTANCE = it }
            }
        }
        
        // Configuración de caché por tier de dispositivo
        private fun getCacheConfig(tier: DeviceCapabilityDetector.PerformanceTier) = when (tier) {
            DeviceCapabilityDetector.PerformanceTier.LOW_END -> CacheConfig(
                memoryPercent = 0.15, // 15% de RAM disponible
                diskSizeMB = 50,
                maxImageWidth = 800,
                maxImageHeight = 600
            )
            DeviceCapabilityDetector.PerformanceTier.MID_END -> CacheConfig(
                memoryPercent = 0.25, // 25% de RAM disponible  
                diskSizeMB = 100,
                maxImageWidth = 1200,
                maxImageHeight = 900
            )
            DeviceCapabilityDetector.PerformanceTier.HIGH_END -> CacheConfig(
                memoryPercent = 0.35, // 35% de RAM disponible
                diskSizeMB = 200,
                maxImageWidth = 1920,
                maxImageHeight = 1440
            )
            DeviceCapabilityDetector.PerformanceTier.PREMIUM -> CacheConfig(
                memoryPercent = 0.4, // 40% de RAM disponible
                diskSizeMB = 300,
                maxImageWidth = 2560,
                maxImageHeight = 1920
            )
        }
    }
    
    data class CacheConfig(
        val memoryPercent: Double,
        val diskSizeMB: Long,
        val maxImageWidth: Int,
        val maxImageHeight: Int
    )
    
    data class LoadingState(
        val isLoading: Boolean = false,
        val progress: Float = 0f,
        val error: Throwable? = null,
        val isSuccess: Boolean = false
    )
    
    private val cacheConfig = getCacheConfig(deviceCapabilities.tier)
    
    // ImageLoader optimizado según capacidades del dispositivo
    val imageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(cacheConfig.memoryPercent)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, "image_cache"))
                    .maxSizeBytes(cacheConfig.diskSizeMB * 1024 * 1024)
                    .build()
            }
            .components {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .respectCacheHeaders(false)
            .allowHardware(deviceCapabilities.tier != DeviceCapabilityDetector.PerformanceTier.LOW_END)
            .crossfade(if (deviceCapabilities.recommendedAnimationScale > 0.8f) 300 else 0)
            .build()
    }
    
    /**
     * Transformation personalizada para optimizar imágenes según el dispositivo
     */
    inner class AdaptiveResizeTransformation : Transformation {
        override val cacheKey: String = "adaptive_resize_${cacheConfig.maxImageWidth}_${cacheConfig.maxImageHeight}"
        
        override suspend fun transform(input: Bitmap, size: Size): Bitmap {
            return memoryManager.optimizeBitmap(input, cacheKey) ?: input
        }
    }
    
    /**
     * Crea una request optimizada para el dispositivo actual
     */
    fun createOptimizedRequest(
        data: Any?,
        targetWidth: Int? = null,
        targetHeight: Int? = null
    ): ImageRequest {
        val finalWidth = targetWidth?.coerceAtMost(cacheConfig.maxImageWidth) ?: cacheConfig.maxImageWidth
        val finalHeight = targetHeight?.coerceAtMost(cacheConfig.maxImageHeight) ?: cacheConfig.maxImageHeight
        
        return ImageRequest.Builder(context)
            .data(data)
            .size(finalWidth, finalHeight)
            .scale(Scale.FIT)
            .transformations(AdaptiveResizeTransformation())
            .memoryCacheKey("${data}_${finalWidth}_${finalHeight}_${deviceCapabilities.tier}")
            .diskCacheKey("${data}_adaptive")
            .allowHardware(deviceCapabilities.tier != DeviceCapabilityDetector.PerformanceTier.LOW_END)
            .build()
    }
    
    /**
     * Limpia cachés de imágenes
     */
    suspend fun clearCache() = withContext(Dispatchers.IO) {
        imageLoader.memoryCache?.clear()
        imageLoader.diskCache?.clear()
    }
    
    /**
     * Precarga una imagen en segundo plano
     */
    suspend fun preloadImage(data: Any) = withContext(Dispatchers.IO) {
        try {
            val request = createOptimizedRequest(data)
            imageLoader.execute(request)
        } catch (e: Exception) {
            // Silenciosamente falla para no interrumpir la UI
        }
    }
    
    /**
     * Obtiene estadísticas del caché
     */
    fun getCacheStats(): Map<String, Any> {
        val memoryCache = imageLoader.memoryCache
        val diskCache = imageLoader.diskCache
        
        return mapOf(
            "memoryCacheSize" to (memoryCache?.size ?: 0),
            "memoryCacheMaxSize" to (memoryCache?.maxSize ?: 0),
            "diskCacheSize" to (diskCache?.size ?: 0),
            "diskCacheMaxSize" to (diskCache?.maxSize ?: 0),
            "cacheConfig" to cacheConfig
        )
    }
}

/**
 * Composable optimizado para carga lazy de imágenes
 */
@Composable
fun OptimizedAsyncImage(
    data: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    placeholder: (@Composable () -> Unit)? = null,
    error: (@Composable () -> Unit)? = null,
    onLoading: ((Boolean) -> Unit)? = null,
    onSuccess: (() -> Unit)? = null,
    onError: ((Throwable?) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Crop,
    targetWidth: Int? = null,
    targetHeight: Int? = null
) {
    val context = LocalContext.current
    val memoryManager = remember { 
        MemoryResourceManager.getInstance(
            context,
            DeviceCapabilityDetector(context).detectCapabilities()
        ) 
    }
    val deviceCapabilities = remember { DeviceCapabilityDetector(context).detectCapabilities() }
    val imageLoader = remember { 
        LazyImageLoader.getInstance(context, memoryManager, deviceCapabilities) 
    }
    
    val request = remember(data, targetWidth, targetHeight) {
        imageLoader.createOptimizedRequest(data, targetWidth, targetHeight)
    }
    
    val painter = rememberAsyncImagePainter(
        model = request,
        imageLoader = imageLoader.imageLoader
    )
    
    // Monitorear estado de carga
    LaunchedEffect(painter.state) {
        when (val state = painter.state) {
            is AsyncImagePainter.State.Loading -> {
                onLoading?.invoke(true)
            }
            is AsyncImagePainter.State.Success -> {
                onLoading?.invoke(false)
                onSuccess?.invoke()
            }
            is AsyncImagePainter.State.Error -> {
                onLoading?.invoke(false)
                onError?.invoke(state.result.throwable)
            }
            else -> {
                onLoading?.invoke(false)
            }
        }
    }
    
    Box(modifier = modifier) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                placeholder?.invoke() ?: DefaultImagePlaceholder()
            }
            is AsyncImagePainter.State.Error -> {
                error?.invoke() ?: DefaultImageError()
            }
            else -> {
                Image(
                    painter = painter,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale
                )
            }
        }
    }
}

/**
 * Placeholder por defecto con shimmer effect adaptativo
 */
@Composable
private fun DefaultImagePlaceholder() {
    val deviceCapabilities = DeviceCapabilityDetector(LocalContext.current).detectCapabilities()
    
    // Solo mostrar shimmer en dispositivos con capacidad suficiente
    if (deviceCapabilities.recommendedAnimationScale >= 0.8f) {
        ShimmerPlaceholder()
    } else {
        StaticPlaceholder()
    }
}

/**
 * Placeholder con efecto shimmer
 */
@Composable
private fun ShimmerPlaceholder() {
    var shimmerTranslateAnim by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            shimmerTranslateAnim = if (shimmerTranslateAnim == 0f) 1f else 0f
            delay(1000)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp,
            color = Color.Gray.copy(alpha = 0.6f)
        )
    }
}

/**
 * Placeholder estático para dispositivos con recursos limitados
 */
@Composable
private fun StaticPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "📷",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Gray.copy(alpha = 0.6f)
        )
    }
}

/**
 * Error placeholder por defecto
 */
@Composable
private fun DefaultImageError() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Red.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "❌",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Error al cargar",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Composable para cargar múltiples imágenes de forma eficiente
 */
@Composable
fun OptimizedImageGrid(
    images: List<Any>,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    onImageClick: (Int, Any) -> Unit = { _, _ -> }
) {
    val deviceCapabilities = DeviceCapabilityDetector(LocalContext.current).detectCapabilities()
    
    // Limitar número de imágenes visibles según capacidad del dispositivo
    val maxImages = when (deviceCapabilities.tier) {
        DeviceCapabilityDetector.PerformanceTier.LOW_END -> 8
        DeviceCapabilityDetector.PerformanceTier.MID_END -> 16
        DeviceCapabilityDetector.PerformanceTier.HIGH_END -> 24
        DeviceCapabilityDetector.PerformanceTier.PREMIUM -> 32
    }
    
    val visibleImages = images.take(maxImages)
    
    LazyVerticalGrid(
        columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(visibleImages.size) { index ->
            val image = visibleImages[index]
            
            Card(
                onClick = { onImageClick(index, image) },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                OptimizedAsyncImage(
                    data = image,
                    contentDescription = "Imagen $index",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}