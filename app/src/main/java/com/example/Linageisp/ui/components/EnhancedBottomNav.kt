package com.example.Linageisp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Linageisp.ui.theme.*

/**
 * Elemento de navegación mejorado con iconos Material
 */
data class EnhancedNavItem(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val label: String,
    val badge: String? = null
)

/**
 * Bottom Navigation Bar completamente rediseñado
 */
@Composable
fun EnhancedBottomNavigationBar(
    currentRoute: String = "plans",
    onItemClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        EnhancedNavItem(
            route = "home", 
            icon = Icons.Default.Home, 
            selectedIcon = Icons.Filled.Home,
            label = "Inicio"
        ),
        EnhancedNavItem(
            route = "plans", 
            icon = Icons.Default.Router, 
            selectedIcon = Icons.Filled.Router,
            label = "Planes"
        ),
        EnhancedNavItem(
            route = "benefits", 
            icon = Icons.Default.CardGiftcard, 
            selectedIcon = Icons.Filled.CardGiftcard,
            label = "Beneficios",
            badge = "2"
        ),
        EnhancedNavItem(
            route = "account", 
            icon = Icons.Default.Person, 
            selectedIcon = Icons.Filled.Person,
            label = "Cuenta"
        )
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 20.dp,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp, 
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        // Fondo con gradiente sutil
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    EnhancedNavItem(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = { onItemClick(item.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Item individual mejorado con animaciones fluidas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedNavItem(
    item: EnhancedNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    // Animaciones mejoradas
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        animationSpec = tween(300, easing = EaseInOutCubic),
        label = "navBackgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
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

    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconScale"
    )

    val containerHeight by animateDpAsState(
        targetValue = if (isSelected) 64.dp else 56.dp,
        animationSpec = tween(300, easing = EaseInOutCubic),
        label = "containerHeight"
    )

    // Contenedor del item
    Box(
        modifier = modifier
            .height(containerHeight)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Contenedor del icono con badge
            Box {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                    contentDescription = item.label,
                    tint = contentColor,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            scaleX = iconScale
                            scaleY = iconScale
                        }
                )

                // Badge si existe
                item.badge?.let { badge ->
                    Surface(
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = 8.dp, y = (-8).dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onError
                                )
                            )
                        }
                    }
                }
            }

            // Espaciado dinámico
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
            ) {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Label con animación
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(tween(300, 100)) + slideInVertically(
                    tween(300, 100),
                    initialOffsetY = { it / 2 }
                ),
                exit = fadeOut(tween(200)) + slideOutVertically(
                    tween(200),
                    targetOffsetY = { it / 2 }
                )
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor,
                        fontSize = 12.sp
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

/**
 * Floating Action Button integrado para acciones principales
 */
@Composable
fun IntegratedFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 12.dp,
            pressedElevation = 16.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = "Test de velocidad",
            modifier = Modifier.size(24.dp)
        )
    }
}