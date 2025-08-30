package com.example.Linageisp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Esquema de colores metálicos premium para modo oscuro
 * Diseño ISP profesional con acabados metálicos
 */
private val MetallicDarkColorScheme = darkColorScheme(
    primary = MetallicCopper,
    onPrimary = MetallicPearl,
    primaryContainer = MetallicBronze,
    onPrimaryContainer = MetallicPearl,
    secondary = MetallicSilver,
    onSecondary = MetallicGraphiteDark,
    secondaryContainer = MetallicGraphite,
    onSecondaryContainer = MetallicTitanium,
    tertiary = MetallicRoseGold,
    onTertiary = MetallicGraphite,
    background = MetallicGraphiteDark,
    onBackground = MetallicPearl,
    surface = MetallicGraphite,
    onSurface = MetallicPearl,
    surfaceVariant = MetallicGraphite,
    onSurfaceVariant = MetallicSilver,
    outline = MetallicCopper.copy(alpha = 0.5f),
    inverseOnSurface = MetallicGraphiteDark,
    inverseSurface = MetallicPearl,
    inversePrimary = MetallicBronze
)

/**
 * Esquema de colores metálicos premium para modo claro
 * Diseño ISP profesional con acabados metálicos
 */
private val MetallicLightColorScheme = lightColorScheme(
    primary = MetallicCopper,
    secondary = MetallicSilver,
    tertiary = MetallicRoseGold,
    background = MetallicPearl.copy(alpha = 0.95f),
    surface = MetallicPearl.copy(alpha = 0.15f),
    onPrimary = MetallicPearl,
    onSecondary = MetallicGraphite,
    onBackground = MetallicGraphite,
    onSurface = MetallicGraphite,
    primaryContainer = MetallicBronze.copy(alpha = 0.2f),
    onPrimaryContainer = MetallicCopper,
    secondaryContainer = MetallicSilver.copy(alpha = 0.3f),
    onSecondaryContainer = MetallicGraphite,
    surfaceVariant = MetallicTitanium.copy(alpha = 0.2f),
    onSurfaceVariant = MetallicCopper,
    outline = MetallicCopper.copy(alpha = 0.3f)
)

@Composable
fun LinageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled to force metallic premium theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> MetallicDarkColorScheme
        else -> MetallicLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}