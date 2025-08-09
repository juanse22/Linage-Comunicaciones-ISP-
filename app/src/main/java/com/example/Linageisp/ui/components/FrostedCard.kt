@file:OptIn(ExperimentalAnimationApi::class)

package com.example.Linageisp.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Componente de tarjeta con efecto vidrio (glassmorphism) para Frutiger Aero
 */
@Composable
fun FrostedCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.animateContentSize(animationSpec = spring()),
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}