package com.example.Linageisp.notifications.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 🔴 Bottom Navigation con badges animados para notificaciones
 * 
 * Características:
 * - Badges animados con contador
 * - Diferentes tipos de badges
 * - Animaciones suaves
 * - Material 3 Design
 * - Compatible con NavigationBar
 */
@Composable
fun BadgedBottomNavigationBar(
    currentRoute: String,
    badges: Map<String, Int>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val navigationItems = listOf(
            NavigationItem("home", "Inicio", Icons.Default.Home),
            NavigationItem("plans", "Planes", Icons.Default.Wifi),
            NavigationItem("benefits", "Beneficios", Icons.Default.Star),
            NavigationItem("account", "Cuenta", Icons.Default.Person)
        )
        
        navigationItems.forEach { item ->
            val isSelected = currentRoute == item.route
            val badgeCount = badges[item.route] ?: 0
            val hasBadge = badgeCount > 0
            
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemClick(item.route) },
                icon = {
                    BadgedIcon(
                        icon = item.icon,
                        badgeCount = badgeCount,
                        showBadge = hasBadge,
                        isSelected = isSelected
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFF37321),
                    selectedTextColor = Color(0xFFF37321),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = Color(0xFFF37321).copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
private fun BadgedIcon(
    icon: ImageVector,
    badgeCount: Int,
    showBadge: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        
        // Badge animado
        AnimatedVisibility(
            visible = showBadge,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            ) + fadeIn(),
            exit = scaleOut(
                animationSpec = tween(150)
            ) + fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            NotificationBadge(
                count = badgeCount,
                isSelected = isSelected
            )
        }
    }
}

@Composable
private fun NotificationBadge(
    count: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    // Animación de pulso para badges nuevos
    val infiniteTransition = rememberInfiniteTransition(label = "badge_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    Box(
        modifier = modifier
            .offset(x = 8.dp, y = (-8).dp)
            .size(
                if (count > 99) 28.dp else if (count > 9) 20.dp else 16.dp
            )
            .graphicsLayer {
                scaleX = if (count <= 3) pulseScale else 1f // Solo pulsar para pocos badges
                scaleY = if (count <= 3) pulseScale else 1f
            }
            .background(
                color = Color(0xFFF44336), // Rojo notificación
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when {
                count > 99 -> "99+"
                count > 0 -> count.toString()
                else -> ""
            },
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 🎯 Badge especializado para promociones
 */
@Composable
fun PromotionBadge(
    modifier: Modifier = Modifier
) {
    // Animación de gradiente rotativo
    val infiniteTransition = rememberInfiniteTransition(label = "promo_gradient")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "rotation"
    )
    
    Box(
        modifier = modifier
            .size(20.dp)
            .graphicsLayer { rotationZ = rotation }
            .background(
                brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFFF37321),
                        Color(0xFFFF9B47),
                        Color(0xFFF37321)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "!",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 🟢 Badge para estados activos
 */
@Composable
fun StatusBadge(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isActive) Color(0xFF4CAF50) else Color(0xFFFF9800)
    
    Box(
        modifier = modifier
            .size(12.dp)
            .background(
                color = color,
                shape = CircleShape
            )
    )
}

/**
 * Data class para elementos de navegación
 */
private data class NavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

/**
 * 🔔 Badge flotante para notificaciones urgentes
 */
@Composable
fun FloatingNotificationBadge(
    count: Int,
    onBadgeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = count > 0,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(300)
        ) + fadeOut(),
        modifier = modifier
    ) {
        FloatingActionButton(
            onClick = onBadgeClick,
            modifier = Modifier.size(56.dp),
            containerColor = Color(0xFFF44336),
            contentColor = Color.White
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(20.dp)
                )
                
                if (count <= 99) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "99+",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}