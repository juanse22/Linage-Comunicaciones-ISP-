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
import com.example.Linageisp.performance.core.DeviceCapabilityDetector as CoreDeviceCapabilityDetector

/**
 * Sistema de carga lazy optimizado para imágenes con adaptación por dispositivo
 * Integra Coil con optimizaciones personalizadas de memoria y rendimiento
 */
class LazyImageLoader private constructor(
    private val context: Context
) {
    
    companion object {
        @Volatile
        private var INSTANCE: LazyImageLoader? = null
        
        fun getInstance(context: Context): LazyImageLoader {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LazyImageLoader(context.applicationContext).also { INSTANCE = it }
            }
        }
        
        // Configuración simple de caché
        private val cacheConfig = CacheConfig(
            memoryPercent = 0.25,
            diskSizeMB = 100,
            maxImageWidth = 1200,
            maxImageHeight = 900
        )
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
    
    // Usar configuración simple
    
    // ImageLoader simple y fluido
    val imageLoader: ImageLoader by lazy {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, "image_cache"))
                    .maxSizeBytes(100 * 1024 * 1024) // 100MB
                    .build()
            }
            .respectCacheHeaders(false)
            .allowHardware(true)
            .crossfade(200) // Animación suave
            .build()
    }
    
    // Transformation simple y rápida
    inner class SimpleResizeTransformation : Transformation {
        override val cacheKey: String = "simple_resize"
        
        override suspend fun transform(input: Bitmap, size: Size): Bitmap {
            return input // Sin transformaciones complejas para mejor rendimiento
        }
    }
    
    // Request simple y rápida
    fun createOptimizedRequest(
        data: Any?,
        targetWidth: Int? = null,
        targetHeight: Int? = null
    ): ImageRequest {
        return ImageRequest.Builder(context)
            .data(data)
            .size(targetWidth ?: 1200, targetHeight ?: 900)
            .scale(Scale.FIT)
            .memoryCacheKey("${data}_simple")
            .diskCacheKey("${data}_cache")
            .allowHardware(true)
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
    
    // Stats simples sin overhead
    fun getCacheStats(): Map<String, Any> {
        return mapOf(
            "status" to "running",
            "type" to "simple_cache"
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
    val imageLoader = remember { LazyImageLoader.getInstance(context) }
    
    val request = remember(data, targetWidth, targetHeight) {
        imageLoader.createOptimizedRequest(data, targetWidth, targetHeight)
    }
    
    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        modifier = modifier,
        imageLoader = imageLoader.imageLoader,
        contentScale = contentScale,
        onLoading = { onLoading?.invoke(true) },
        onSuccess = { onSuccess?.invoke() },
        onError = { onError?.invoke(it.result.throwable) }
    )
}

/**
 * Placeholder simple y fluido
 */
@Composable
private fun DefaultImagePlaceholder() {
    StaticPlaceholder() // Siempre usar placeholder simple
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
    // Grid simple sin limitaciones complejas
    val visibleImages = images.take(20) // Límite simple
    
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