package com.example.Linageisp.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Colores corporativos de Linage ISP
 * Paleta principal basada en naranja brillante como color corporativo
 * Inspirada en el estilo Frutiger Aero: colores vibrantes y modernos
 */
val LinageOrange = Color(0xFFF37321) // Naranja corporativo brillante principal
val LinageOrangeDark = Color(0xFFD85A00) // Naranja más oscuro
val LinageOrangeLight = Color(0xFFFF9B47) // Naranja más claro
val LinageOrangeAccent = Color(0xFFFF8533) // Naranja de acento

// Colores complementarios
val LinageGray = Color(0xFF757575) // Gris medio
val LinageGrayLight = Color(0xFFBDBDBD) // Gris claro
val LinageGrayDark = Color(0xFF424242) // Gris oscuro
val LinageWhite = Color(0xFFFFFFFF) // Blanco puro
val LinageBackground = Color(0xFFFAFAFA) // Fondo gris muy claro

// Colores de estado
val SuccessGreen = Color(0xFF4CAF50) // Verde para éxito
val ErrorRed = Color(0xFFF44336) // Rojo para errores
val InfoBlue = Color(0xFF2196F3) // Azul para información

// Colores adicionales para gradientes y efectos
val LinageOrangeSoft = Color(0xFFFFF3E0) // Naranja suave para fondos
val LinageOrangeTranslucent = Color(0x88F37321) // Naranja translúcido para secciones destacadas
val LinageBeige = Color(0xFFF8F4F0) // Beige suave para fondos alternativos
val LinageAccentSecondary = Color(0xFF795548) // Marrón complementario

// Colores para modo oscuro mejorados
val LinageDarkBackground = Color(0xFF121212) // Negro suave
val LinageDarkSurface = Color(0xFF1E1E1E) // Superficie oscura
val LinageDarkSurfaceVariant = Color(0xFF2D2D2D) // Variante de superficie
val LinageDarkOutline = Color(0xFF404040) // Contornos modo oscuro

// Colores semánticos mejorados
val SuccessGreenLight = Color(0xFFE8F5E8) // Verde claro para fondos
val WarningAmber = Color(0xFFFF8F00) // Ámbar para advertencias
val WarningAmberLight = Color(0xFFFFF3E0) // Ámbar claro para fondos
val InfoBlueLight = Color(0xFFE3F2FD) // Azul claro para fondos

// PALETA FRUITY AERO - FONDO NEGRO CON COLORES VIBRANTES
// Fondo base negro profundo
val DeepBlack = Color(0xFF000000) // Negro profundo principal
val DeepBlackSoft = Color(0xFF0A0A0A) // Negro suave alternativo
val BlackSemiTransparent = Color(0xFF1A1A1A) // Negro semi-transparente para cards

// Colores vibrantes neón para categorías sobre fondo negro
val NeonGreen = Color(0xFF00FF41) // Win+ Fútbol - Verde neón brillante
val ElectricBlue = Color(0xFF0099FF) // Premium DIRECTV - Azul eléctrico
val VibrantOrange = Color(0xFFFF6600) // VIP Paramount - Naranja vibrante  
val BrightPurple = Color(0xFF9966FF) // VIP Max - Morado brillante
val IntenseRed = Color(0xFFFF3366) // Netflix - Rojo intenso
val BrilliantCyan = Color(0xFF00FFCC) // Cámaras - Cyan brillante

// Colores de texto para legibilidad perfecta en negro
val PureWhite = Color(0xFFFFFFFF) // Títulos - Blanco puro
val LightGray = Color(0xFFCCCCCC) // Descripciones - Gris claro
val SoftWhite = Color(0xFFF5F5F5) // Texto secundario

// Gradientes vibrantes específicos por categoría
val NeonGreenGradient = listOf(NeonGreen, NeonGreen.copy(alpha = 0.7f))
val ElectricBlueGradient = listOf(ElectricBlue, ElectricBlue.copy(alpha = 0.7f))
val VibrantOrangeGradient = listOf(VibrantOrange, VibrantOrange.copy(alpha = 0.7f))
val BrightPurpleGradient = listOf(BrightPurple, BrightPurple.copy(alpha = 0.7f))
val IntenseRedGradient = listOf(IntenseRed, IntenseRed.copy(alpha = 0.7f))
val BrilliantCyanGradient = listOf(BrilliantCyan, BrilliantCyan.copy(alpha = 0.7f))

// COLORES METÁLICOS PREMIUM
val MetallicCopper = Color(0xFFB87333) // Cobre metálico
val MetallicBronze = Color(0xFFCD7F32) // Bronce metálico
val MetallicSilver = Color(0xFFC0C0C0) // Plata metálica
val MetallicTitanium = Color(0xFF8C92AC) // Titanio metálico
val MetallicPearl = Color(0xFFF8F8F8) // Perla metálica
val MetallicGraphite = Color(0xFF41424C) // Grafito metálico
val MetallicGraphiteDark = Color(0xFF2F3039) // Grafito oscuro
val MetallicRoseGold = Color(0xFFE8B4A0) // Oro rosa metálico

// Gradientes metálicos premium
val LinageGradientPrimary = listOf(
    MetallicCopper,
    MetallicBronze
)
val LinageGradientSecondary = listOf(
    MetallicSilver.copy(alpha = 0.8f),
    MetallicTitanium.copy(alpha = 0.6f),
    MetallicPearl.copy(alpha = 0.4f)
)
val LinageGradientDark = listOf(
    MetallicGraphite,
    MetallicGraphiteDark
)

// EFECTOS GLASSMORPHISM FRUITY AERO - FONDO NEGRO

/**
 * Modificador glassmorphism sobre fondo negro con efectos vibrantes
 * Fondo semi-transparente con bordes de colores vibrantes y glow effect
 */
fun Modifier.glassmorphismFruityAero(
    borderRadius: androidx.compose.ui.unit.Dp = 16.dp,
    backgroundAlpha: Float = 0.08f,
    borderColor: Color = PureWhite,
    borderAlpha: Float = 0.4f
) = this
    .background(
        brush = Brush.radialGradient(
            colors = listOf(
                PureWhite.copy(alpha = backgroundAlpha),
                Color.Transparent
            )
        ),
        shape = RoundedCornerShape(borderRadius)
    )
    .border(
        width = 1.dp,
        color = borderColor.copy(alpha = borderAlpha),
        shape = RoundedCornerShape(borderRadius)
    )
    .clip(RoundedCornerShape(borderRadius))

/**
 * Modificador glassmorphism específico por categoría con colores vibrantes
 */
fun Modifier.glassmorphismCategoryFruityAero(
    category: String,
    borderRadius: androidx.compose.ui.unit.Dp = 16.dp,
    backgroundAlpha: Float = 0.08f
): Modifier {
    val categoryColor = when (category) {
        "win_futbol" -> NeonGreen
        "premium_directv" -> ElectricBlue
        "vip_paramount" -> VibrantOrange
        "vip_max_tv" -> BrightPurple
        "netflix_plans" -> IntenseRed
        "camaras_seguridad" -> BrilliantCyan
        else -> PureWhite
    }
    
    return this
        .background(
            brush = Brush.radialGradient(
                colors = listOf(
                    PureWhite.copy(alpha = backgroundAlpha),
                    Color.Transparent
                )
            ),
            shape = RoundedCornerShape(borderRadius)
        )
        .border(
            width = 1.dp,
            color = categoryColor.copy(alpha = 0.4f),
            shape = RoundedCornerShape(borderRadius)
        )
        .clip(RoundedCornerShape(borderRadius))
}

/**
 * Modificador para efecto glow sobre fondo negro
 * Crea un resplandor sutil alrededor de las cards
 */
fun Modifier.glowEffect(
    glowColor: Color = PureWhite,
    elevation: androidx.compose.ui.unit.Dp = 8.dp,
    borderRadius: androidx.compose.ui.unit.Dp = 16.dp
) = this
    .background(
        brush = Brush.radialGradient(
            colors = listOf(
                glowColor.copy(alpha = 0.1f),
                glowColor.copy(alpha = 0.05f),
                Color.Transparent
            ),
            radius = elevation.value * 15f
        ),
        shape = RoundedCornerShape(borderRadius)
    )

// ANIMACIONES PREMIUM ISP

/**
 * Animación premium con spring physics elegante para hover effects
 */
@Composable
fun Modifier.premiumHoverEffect(
    isHovered: Boolean = false,
    scaleAmount: Float = 1.02f
): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (isHovered) scaleAmount else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "premium_hover_scale"
    )
    
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Animación premium de entrada para cards metálicas
 */
@Composable
fun Modifier.premiumEntranceAnimation(
    visible: Boolean = true
): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "premium_entrance_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = EaseOutCubic
        ),
        label = "premium_entrance_alpha"
    )
    
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
    }
}

/**
 * Micro-animación premium para bordes metálicos
 */
@Composable
fun Modifier.premiumMetallicBorderGlow(
    isActive: Boolean = true
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "metallic_border_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    return if (isActive) {
        this.border(
            width = 0.5.dp,
            color = MetallicCopper.copy(alpha = glowAlpha),
            shape = RoundedCornerShape(16.dp)
        )
    } else this
}

/**
 * Modificador glassmorphism metálico premium
 */
fun Modifier.glassmorphismMetallic(
    borderRadius: androidx.compose.ui.unit.Dp = 16.dp,
    backgroundAlpha: Float = 0.1f,
    borderAlpha: Float = 0.3f
) = this
    .background(
        brush = Brush.radialGradient(
            colors = listOf(
                MetallicCopper.copy(alpha = backgroundAlpha),
                Color.Transparent
            )
        ),
        shape = RoundedCornerShape(borderRadius)
    )
    .border(
        width = 1.dp,
        color = MetallicSilver.copy(alpha = borderAlpha),
        shape = RoundedCornerShape(borderRadius)
    )
    .clip(RoundedCornerShape(borderRadius))

/**
 * Modificador glassmorphism metálico específico por categoría
 */
fun Modifier.glassmorphismCategoryMetallic(
    category: String,
    borderRadius: androidx.compose.ui.unit.Dp = 16.dp,
    backgroundAlpha: Float = 0.08f
): Modifier {
    val categoryColor = when (category) {
        "win_futbol" -> NeonGreen
        "premium_directv" -> ElectricBlue
        "vip_paramount" -> VibrantOrange
        "vip_max_tv" -> BrightPurple
        "netflix_plans" -> IntenseRed
        "camaras_seguridad" -> BrilliantCyan
        else -> MetallicSilver
    }
    
    return this
        .background(
            brush = Brush.radialGradient(
                colors = listOf(
                    categoryColor.copy(alpha = backgroundAlpha),
                    Color.Transparent
                )
            ),
            shape = RoundedCornerShape(borderRadius)
        )
        .border(
            width = 1.dp,
            color = categoryColor.copy(alpha = 0.4f),
            shape = RoundedCornerShape(borderRadius)
        )
        .clip(RoundedCornerShape(borderRadius))
}

/**
 * Modificador de sombra metálica premium
 */
fun Modifier.metallicShadow(
    elevation: androidx.compose.ui.unit.Dp = 8.dp,
    borderRadius: androidx.compose.ui.unit.Dp = 16.dp
) = this
    .background(
        brush = Brush.radialGradient(
            colors = listOf(
                MetallicGraphite.copy(alpha = 0.2f),
                MetallicGraphite.copy(alpha = 0.1f),
                Color.Transparent
            ),
            radius = elevation.value * 8f
        ),
        shape = RoundedCornerShape(borderRadius)
    )