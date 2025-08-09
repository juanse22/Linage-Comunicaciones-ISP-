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
 * Esquema de colores para modo oscuro mejorado
 * Utiliza la paleta naranja corporativa de Linage ISP con mejor contraste
 */
private val DarkColorScheme = darkColorScheme(
    primary = LinageOrange,
    onPrimary = LinageWhite,
    primaryContainer = LinageOrangeDark,
    onPrimaryContainer = LinageWhite,
    secondary = LinageGrayLight,
    onSecondary = LinageDarkBackground,
    secondaryContainer = LinageDarkSurfaceVariant,
    onSecondaryContainer = LinageWhite,
    tertiary = LinageOrangeLight,
    onTertiary = LinageGrayDark,
    background = LinageDarkBackground,
    onBackground = LinageWhite,
    surface = LinageDarkSurface,
    onSurface = LinageWhite,
    surfaceVariant = LinageDarkSurfaceVariant,
    onSurfaceVariant = LinageGrayLight,
    outline = LinageDarkOutline,
    inverseOnSurface = LinageDarkBackground,
    inverseSurface = LinageWhite,
    inversePrimary = LinageOrangeDark
)

/**
 * Esquema de colores para modo claro
 * Utiliza la paleta naranja corporativa de Linage ISP
 */
private val LightColorScheme = lightColorScheme(
    primary = LinageOrange,
    secondary = LinageGray,
    tertiary = LinageOrangeAccent,
    background = LinageBackground,
    surface = LinageWhite,
    onPrimary = LinageWhite,
    onSecondary = LinageWhite,
    onBackground = LinageGrayDark,
    onSurface = LinageGrayDark,
    primaryContainer = LinageOrangeSoft,
    onPrimaryContainer = LinageOrangeDark
)

@Composable
fun LinageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}