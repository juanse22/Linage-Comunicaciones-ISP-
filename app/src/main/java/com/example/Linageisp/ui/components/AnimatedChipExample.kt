package com.example.Linageisp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Chip animado funcional que compila sin errores
 * Incluye todas las animaciones correctas con tipos explícitos
 */
@Composable
fun AnimatedChip(
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    emoji: String = "",
    selectedColor: Color = Color(0xFFF37321),
    unselectedColor: Color = Color(0x75757575)
) {
    var isPressed by remember { mutableStateOf(false) }

    val backgroundColor: Color by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        animationSpec = tween(durationMillis = 300),
        label = "chip_background"
    )

    val textColor: Color by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Gray,
        animationSpec = tween(durationMillis = 300),
        label = "chip_text"
    )

    val scale: Float by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(),
        label = "chip_scale"
    )

    Surface(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = if (isSelected) 4.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (emoji.isNotEmpty()) {
                Text(
                    text = emoji,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = "Animated Chip",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = textColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
    
    // Reset pressed state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

/**
 * Ejemplo de uso del AnimatedChip
 */
@Composable
fun AnimatedChipDemo() {
    var chipSelected by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Demo del AnimatedChip",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Chip básico
        AnimatedChip(
            isSelected = chipSelected,
            onClick = { chipSelected = !chipSelected }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Chip con emoji
        AnimatedChip(
            isSelected = chipSelected,
            onClick = { chipSelected = !chipSelected },
            emoji = "🚀"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Chip con colores personalizados
        AnimatedChip(
            isSelected = chipSelected,
            onClick = { chipSelected = !chipSelected },
            emoji = "⭐",
            selectedColor = Color(0xFF4CAF50),
            unselectedColor = Color(0xFFE0E0E0)
        )
    }
}