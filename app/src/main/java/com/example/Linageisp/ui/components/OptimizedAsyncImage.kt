package com.example.Linageisp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.request.CachePolicy
import com.example.Linageisp.ui.animations.SharedAnimations
import com.example.Linageisp.ui.theme.GoldenRatio
import com.example.Linageisp.ui.theme.LinageOrange
import com.example.Linageisp.ui.theme.LinageGray

/**
 * Optimized AsyncImage component with advanced caching and golden ratio proportions
 * OPTIMIZADO: Memoria, disco y red cache para máximo rendimiento
 */
@Composable
fun OptimizedAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RoundedCornerShape(GoldenRatio.RADIUS_MD),
    colorFilter: ColorFilter? = null,
    alpha: Float = 1f,
    placeholder: Int? = null,
    error: Int? = null,
    fallback: Int? = null,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    showLoadingAnimation: Boolean = true,
    enableMemoryCache: Boolean = true,
    enableDiskCache: Boolean = true,
    enableNetworkCache: Boolean = true
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    
    // Optimized image request with comprehensive caching
    val imageRequest = remember(model) {
        ImageRequest.Builder(context)
            .data(model)
            .memoryCachePolicy(if (enableMemoryCache) CachePolicy.ENABLED else CachePolicy.DISABLED)
            .diskCachePolicy(if (enableDiskCache) CachePolicy.ENABLED else CachePolicy.DISABLED)
            .networkCachePolicy(if (enableNetworkCache) CachePolicy.ENABLED else CachePolicy.DISABLED)
            .memoryCacheKey("${model.hashCode()}_memory")
            .diskCacheKey("${model.hashCode()}_disk")
            .allowHardware(true)
            .allowRgb565(true)
            .placeholder(placeholder ?: android.R.drawable.ic_menu_gallery)
            .error(error ?: android.R.drawable.ic_delete)
            .fallback(fallback ?: android.R.drawable.ic_menu_gallery)
            .crossfade(GoldenRatio.DURATION_NORMAL)
            .build()
    }

    Box(
        modifier = modifier
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageRequest,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
            colorFilter = colorFilter,
            alpha = alpha,
            onState = { state ->
                when (state) {
                    is AsyncImagePainter.State.Loading -> {
                        isLoading = true
                        hasError = false
                        onLoading?.invoke(state)
                    }
                    is AsyncImagePainter.State.Success -> {
                        isLoading = false
                        hasError = false
                        onSuccess?.invoke(state)
                    }
                    is AsyncImagePainter.State.Error -> {
                        isLoading = false
                        hasError = true
                        onError?.invoke(state)
                    }
                    else -> {
                        isLoading = false
                        hasError = false
                    }
                }
            }
        )

        // Loading animation with golden ratio proportions
        if (isLoading && showLoadingAnimation) {
            LoadingOverlay()
        }

        // Error state with retry option
        if (hasError) {
            ErrorOverlay(
                onRetry = {
                    hasError = false
                    isLoading = true
                }
            )
        }
    }
}

@Composable
private fun LoadingOverlay() {
    val pulseAlpha by SharedAnimations.rememberPulseAnimation()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LinageGray.copy(alpha = 0.1f),
        shape = RoundedCornerShape(GoldenRatio.RADIUS_MD)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(GoldenRatio.SPACING_XXXL),
                    color = LinageOrange,
                    strokeWidth = GoldenRatio.SPACING_XS / 2
                )
                Spacer(modifier = Modifier.height(GoldenRatio.SPACING_MD))
                Surface(
                    color = Color.White.copy(alpha = (pulseAlpha * 0.8f).coerceIn(0f, 1f)),
                    shape = RoundedCornerShape(GoldenRatio.RADIUS_SM)
                ) {
                    Text(
                        text = "Cargando...",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = LinageGray,
                            fontSize = GoldenRatio.TEXT_SIZE_BODY_SMALL
                        ),
                        modifier = Modifier.padding(
                            horizontal = GoldenRatio.SPACING_MD,
                            vertical = GoldenRatio.SPACING_XS
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorOverlay(
    onRetry: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Red.copy(alpha = 0.1f),
        shape = RoundedCornerShape(GoldenRatio.RADIUS_MD)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(GoldenRatio.SPACING_MD),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(GoldenRatio.SPACING_SM))
            Text(
                text = "Error al cargar",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Red,
                    fontSize = GoldenRatio.TEXT_SIZE_BODY_SMALL
                )
            )
            Spacer(modifier = Modifier.height(GoldenRatio.SPACING_SM))
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .wrapContentSize()
                    .height(GoldenRatio.BUTTON_HEIGHT_COMPACT),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LinageOrange
                ),
                shape = RoundedCornerShape(GoldenRatio.RADIUS_SM)
            ) {
                Text(
                    text = "Reintentar",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontSize = GoldenRatio.TEXT_SIZE_BODY_SMALL
                    )
                )
            }
        }
    }
}

/**
 * Optimized image component specifically for avatars with circular crop
 */
@Composable
fun OptimizedAvatarImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = GoldenRatio.ICON_SIZE_XL,
    placeholder: Int? = null
) {
    OptimizedAsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        contentScale = ContentScale.Crop,
        shape = androidx.compose.foundation.shape.CircleShape,
        placeholder = placeholder,
        showLoadingAnimation = true
    )
}

/**
 * Optimized banner image with golden ratio aspect ratio
 */
@Composable
fun OptimizedBannerImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    aspectRatio: Float = GoldenRatio.ASPECT_RATIO_GOLDEN
) {
    OptimizedAsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier.aspectRatio(aspectRatio),
        contentScale = ContentScale.Crop,
        shape = RoundedCornerShape(GoldenRatio.RADIUS_LG),
        showLoadingAnimation = true
    )
}

/**
 * Optimized thumbnail with perfect square proportions
 */
@Composable
fun OptimizedThumbnailImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = GoldenRatio.CARD_WIDTH_COMPACT / 3
) {
    OptimizedAsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        contentScale = ContentScale.Crop,
        shape = RoundedCornerShape(GoldenRatio.RADIUS_SM),
        showLoadingAnimation = false  // Disable loading animation for small thumbnails
    )
}