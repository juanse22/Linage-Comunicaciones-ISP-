package com.example.Linageisp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Linageisp.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Botón animado reutilizable con efectos de rebote
 */
@Composable
fun AnimatedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emoji: String = "",
    backgroundColor: Color = LinageOrange,
    textColor: Color = LinageWhite,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 6.dp,
    enabled: Boolean = true
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "button_scale"
    )

    Button(
        onClick = {
            if (enabled) {
                isPressed = true
                onClick()
            }
        },
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(cornerRadius),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation,
            disabledElevation = 0.dp
        ),
        enabled = enabled
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (emoji.isNotEmpty()) {
                Text(
                    text = emoji,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = textColor,
                    fontWeight = FontWeight.Bold
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
 * Tarjeta con gradiente reutilizable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(LinageOrange, LinageOrangeLight),
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "gradient_card_scale"
    )

    Card(
        onClick = onClick?.let { { isPressed = true; it() } } ?: {},
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(gradientColors),
                    RoundedCornerShape(cornerRadius)
                )
        ) {
            // Patrón decorativo opcional
            repeat(15) { index ->
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .offset(
                            x = ((index % 6) * 45).dp,
                            y = ((index / 6) * 35).dp
                        )
                        .background(
                            LinageWhite.copy(alpha = 0.1f),
                            RoundedCornerShape(15.dp)
                        )
                )
            }
            
            Column(
                modifier = Modifier.fillMaxSize(),
                content = content
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
 * Indicador de carga con animación personalizada
 */
@Composable
fun CustomLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = LinageOrange,
    strokeWidth: Dp = 4.dp
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
        strokeWidth = strokeWidth
    )
}

/**
 * Chip animado reutilizable con animaciones fluidas
 */
@Composable
fun AnimatedChip(
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    emoji: String = "",
    selectedColor: Color = Color(0xFFF37321),
    unselectedColor: Color = Color(0x75757575),
    modifier: Modifier = Modifier
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
        modifier = modifier
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
                text = "Chip",
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
 * Tarjeta de información con emoji
 */
@Composable
fun InfoCard(
    title: String,
    description: String,
    emoji: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LinageWhite,
    onClick: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "info_card_scale"
    )

    Card(
        onClick = onClick?.let { { isPressed = true; it() } } ?: {},
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LinageGray
                    )
                )
            }
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
 * Texto animado con entrada suave
 */
@Composable
fun AnimatedText(
    text: String,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    delayMillis: Int = 0
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        visible = true
    }
    
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.fadeIn(
            animationSpec = tween(600, easing = EaseInOutCubic)
        ) + androidx.compose.animation.slideInVertically(
            animationSpec = tween(600, easing = EaseInOutCubic),
            initialOffsetY = { it / 4 }
        )
    ) {
        Text(
            text = text,
            modifier = modifier,
            style = style.copy(
                color = if (color != Color.Unspecified) color else style.color,
                fontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize,
                fontWeight = fontWeight ?: style.fontWeight,
                textAlign = textAlign ?: style.textAlign
            )
        )
    }
}

/**
 * Separador con gradiente
 */
@Composable
fun GradientDivider(
    modifier: Modifier = Modifier,
    height: Dp = 1.dp,
    colors: List<Color> = listOf(
        Color.Transparent,
        LinageOrange.copy(alpha = 0.5f),
        Color.Transparent
    )
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                Brush.horizontalGradient(colors)
            )
    )
}

/**
 * Contenedor con sombra personalizada
 */
@Composable
fun ShadowContainer(
    modifier: Modifier = Modifier,
    shadowColor: Color = LinageGray.copy(alpha = 0.1f),
    cornerRadius: Dp = 12.dp,
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = LinageWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        content()
    }
}