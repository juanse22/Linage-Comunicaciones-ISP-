package com.example.Linageisp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Elemento de navegación
 */
data class NavItem(
    val route: String,
    val emoji: String,
    val label: String
)

/**
 * Bottom Navigation Bar moderna
 */
@Composable
fun ModernBottomNavigationBar(
    currentRoute: String = "plans",
    onItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem("home", "🏠", "Inicio"),
        NavItem("plans", "📶", "Planes"),
        NavItem("benefits", "🎁", "Beneficios"),
        NavItem("account", "👤", "Cuenta")
    )

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            navItems.forEach { item ->
                ModernNavItem(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = { onItemClick(item.route) }
                )
            }
        }
    }
}

/**
 * Item individual de navegación con animaciones
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
private fun ModernNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    // Animaciones
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFF37321).copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(300, easing = EaseInOutCubic),
        label = "navBackgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFF37321) else Color(0xFF757575),
        animationSpec = tween(300, easing = EaseInOutCubic),
        label = "navContentColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "navItemScale"
    )

    val emojiScale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "emojiScale"
    )

    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(min = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Emoji con escala animada
            Text(
                text = item.emoji,
                fontSize = 22.sp,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = emojiScale
                        scaleY = emojiScale
                    }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Label con animación de contenido
            AnimatedContent(
                targetState = item.label,
                transitionSpec = {
                    fadeIn(tween(150)) + scaleIn(tween(150)) with
                            fadeOut(tween(150)) + scaleOut(tween(150))
                },
                label = "navLabel"
            ) { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = contentColor,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }

    // Reset pressed state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}