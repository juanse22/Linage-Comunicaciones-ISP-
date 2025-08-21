package com.example.Linageisp.notifications.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.Linageisp.notifications.NotificationViewModel

/**
 * 💬 Tarjeta para mostrar mensajes in-app
 * 
 * Características:
 * - Diferentes tipos de mensaje
 * - Animaciones de entrada/salida
 * - Auto-dismiss configurable
 * - Acciones personalizables
 * - Material 3 Design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppMessageCard(
    message: NotificationViewModel.InAppMessage,
    onMessageClick: ((String) -> Unit)? = null,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    
    // Auto-dismiss timer
    LaunchedEffect(message.id) {
        if (message.autoHide) {
            kotlinx.coroutines.delay(message.hideAfterMs)
            if (isVisible) {
                isVisible = false
                kotlinx.coroutines.delay(300) // Esperar animación de salida
                onDismiss(message.id)
            }
        }
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + expandVertically() + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        val (backgroundColor, contentColor, iconColor, icon) = when (message.type) {
            NotificationViewModel.MessageType.INFO -> {
                Quadruple(
                    Color(0xFFE3F2FD),
                    Color(0xFF1976D2),
                    Color(0xFF2196F3),
                    Icons.Default.Info
                )
            }
            NotificationViewModel.MessageType.SUCCESS -> {
                Quadruple(
                    Color(0xFFE8F5E8),
                    Color(0xFF2E7D32),
                    Color(0xFF4CAF50),
                    Icons.Default.CheckCircle
                )
            }
            NotificationViewModel.MessageType.WARNING -> {
                Quadruple(
                    Color(0xFFFFF3E0),
                    Color(0xFFE65100),
                    Color(0xFFFF9800),
                    Icons.Default.Warning
                )
            }
            NotificationViewModel.MessageType.ERROR -> {
                Quadruple(
                    Color(0xFFFFEBEE),
                    Color(0xFFC62828),
                    Color(0xFFF44336),
                    Icons.Default.Error
                )
            }
            NotificationViewModel.MessageType.PROMOTION -> {
                Quadruple(
                    Color(0xFFFFF3E0),
                    Color(0xFFE65100),
                    Color(0xFFF37321),
                    Icons.Default.LocalOffer
                )
            }
        }
        
        Card(
            onClick = {
                message.actionRoute?.let { route ->
                    onMessageClick?.invoke(route)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono del tipo de mensaje
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = iconColor
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Contenido del mensaje
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Título
                    Text(
                        text = message.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    
                    // Mensaje
                    if (message.message.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = message.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = contentColor.copy(alpha = 0.8f)
                        )
                    }
                    
                    // Botón de acción (si existe)
                    message.actionText?.let { actionText ->
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = {
                                message.actionRoute?.let { route ->
                                    onMessageClick?.invoke(route)
                                    isVisible = false
                                    onDismiss(message.id)
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = iconColor
                            )
                        ) {
                            Text(
                                text = actionText,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                // Botón de cerrar (si es dismissible)
                if (message.dismissible) {
                    IconButton(
                        onClick = {
                            isVisible = false
                            onDismiss(message.id)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            modifier = Modifier.size(16.dp),
                            tint = contentColor.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 🔔 Toast-style message para notificaciones rápidas
 */
@Composable
fun ToastMessage(
    message: NotificationViewModel.InAppMessage,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    
    // Auto-dismiss
    LaunchedEffect(message.id) {
        kotlinx.coroutines.delay(message.hideAfterMs)
        isVisible = false
        kotlinx.coroutines.delay(300)
        onDismiss(message.id)
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(8.dp),
            color = when (message.type) {
                NotificationViewModel.MessageType.SUCCESS -> Color(0xFF4CAF50)
                NotificationViewModel.MessageType.ERROR -> Color(0xFFF44336)
                NotificationViewModel.MessageType.WARNING -> Color(0xFFFF9800)
                NotificationViewModel.MessageType.PROMOTION -> Color(0xFFF37321)
                else -> Color(0xFF2196F3)
            },
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val icon = when (message.type) {
                    NotificationViewModel.MessageType.SUCCESS -> Icons.Default.CheckCircle
                    NotificationViewModel.MessageType.ERROR -> Icons.Default.Error
                    NotificationViewModel.MessageType.WARNING -> Icons.Default.Warning
                    NotificationViewModel.MessageType.PROMOTION -> Icons.Default.LocalOffer
                    else -> Icons.Default.Info
                }
                
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = message.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    if (message.message.isNotEmpty()) {
                        Text(
                            text = message.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
                
                if (message.dismissible) {
                    IconButton(
                        onClick = {
                            isVisible = false
                            onDismiss(message.id)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper data class for colors
 */
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)