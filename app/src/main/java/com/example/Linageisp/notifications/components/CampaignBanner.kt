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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.Linageisp.notifications.NotificationViewModel

/**
 * 🎯 Banner para mostrar campaigns activas
 * 
 * Características:
 * - Diseño Material 3
 * - Soporte para imágenes
 * - Animaciones de entrada
 * - Call-to-action personalizable
 * - Dismiss automático o manual
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignBanner(
    campaign: NotificationViewModel.Campaign,
    onCampaignClick: (NotificationViewModel.Campaign) -> Unit,
    onDismiss: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(animationSpec = tween(400)),
        exit = slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        Card(
            onClick = { onCampaignClick(campaign) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (campaign.type) {
                    NotificationViewModel.CampaignType.PROMOTION -> Color(0xFFFFF3E0)
                    NotificationViewModel.CampaignType.NEW_FEATURE -> Color(0xFFE8F5E8)
                    NotificationViewModel.CampaignType.TECHNICAL_UPDATE -> Color(0xFFFFEBEE)
                    NotificationViewModel.CampaignType.BILLING_REMINDER -> Color(0xFFE3F2FD)
                    NotificationViewModel.CampaignType.SURVEY -> Color(0xFFF3E5F5)
                }
            )
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Imagen del campaign (si existe)
                    campaign.imageUrl?.let { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    
                    // Icono por tipo si no hay imagen
                    if (campaign.imageUrl == null) {
                        val (icon, iconColor) = when (campaign.type) {
                            NotificationViewModel.CampaignType.PROMOTION -> Icons.Default.LocalOffer to Color(0xFFF37321)
                            NotificationViewModel.CampaignType.NEW_FEATURE -> Icons.Default.NewReleases to Color(0xFF4CAF50)
                            NotificationViewModel.CampaignType.TECHNICAL_UPDATE -> Icons.Default.Build to Color(0xFFF44336)
                            NotificationViewModel.CampaignType.BILLING_REMINDER -> Icons.Default.Payment to Color(0xFF2196F3)
                            NotificationViewModel.CampaignType.SURVEY -> Icons.Default.Quiz to Color(0xFF9C27B0)
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(
                                    iconColor.copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = iconColor
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    
                    // Contenido del campaign
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Título
                        Text(
                            text = campaign.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Descripción
                        Text(
                            text = campaign.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Badge de prioridad y tiempo restante
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Badge de tipo
                            Surface(
                                color = when (campaign.type) {
                                    NotificationViewModel.CampaignType.PROMOTION -> Color(0xFFF37321)
                                    NotificationViewModel.CampaignType.NEW_FEATURE -> Color(0xFF4CAF50)
                                    NotificationViewModel.CampaignType.TECHNICAL_UPDATE -> Color(0xFFF44336)
                                    NotificationViewModel.CampaignType.BILLING_REMINDER -> Color(0xFF2196F3)
                                    NotificationViewModel.CampaignType.SURVEY -> Color(0xFF9C27B0)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = when (campaign.type) {
                                        NotificationViewModel.CampaignType.PROMOTION -> "OFERTA"
                                        NotificationViewModel.CampaignType.NEW_FEATURE -> "NUEVO"
                                        NotificationViewModel.CampaignType.TECHNICAL_UPDATE -> "TÉCNICO"
                                        NotificationViewModel.CampaignType.BILLING_REMINDER -> "FACTURA"
                                        NotificationViewModel.CampaignType.SURVEY -> "ENCUESTA"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            
                            // Tiempo restante
                            val timeLeft = campaign.endTime - System.currentTimeMillis()
                            val daysLeft = (timeLeft / (24 * 60 * 60 * 1000)).toInt()
                            
                            if (daysLeft <= 7) {
                                Text(
                                    text = when {
                                        daysLeft <= 0 -> "¡Última oportunidad!"
                                        daysLeft == 1 -> "Queda 1 día"
                                        else -> "Quedan $daysLeft días"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (daysLeft <= 1) Color(0xFFF44336) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    fontWeight = if (daysLeft <= 1) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // CTA Button
                    Button(
                        onClick = { onCampaignClick(campaign) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF37321)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = campaign.ctaText,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                // Botón de dismiss (si está habilitado)
                onDismiss?.let { dismissCallback ->
                    IconButton(
                        onClick = {
                            isVisible = false
                            dismissCallback(campaign.id)
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
                
                // Indicador de prioridad alta
                if (campaign.priority >= 8) {
                    Surface(
                        color = Color(0xFFF44336),
                        shape = RoundedCornerShape(bottomEnd = 16.dp, topStart = 16.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PriorityHigh,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "URGENTE",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 📱 Banner compacto para campaigns
 */
@Composable
fun CompactCampaignBanner(
    campaign: NotificationViewModel.Campaign,
    onCampaignClick: (NotificationViewModel.Campaign) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onCampaignClick(campaign) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF37321).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFFF37321)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = campaign.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}