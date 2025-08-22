package com.example.Linageisp.notifications

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.Linageisp.fcm.LinageNotificationManager

/**
 * 🔔 Pantalla de configuración de notificaciones
 * 
 * Características:
 * - Configuración completa de preferencias
 * - Horarios silenciosos
 * - Tipos de notificación
 * - Estadísticas de uso
 * - Material 3 Design
 * - Animaciones suaves
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    val preferences by notificationViewModel.notificationPreferences.collectAsStateWithLifecycle(initialValue = LinageNotificationManager.NotificationPreferences())
    val uiState by notificationViewModel.uiState.collectAsStateWithLifecycle(initialValue = NotificationViewModel.NotificationUiState())
    val notificationStats = remember { notificationViewModel.getNotificationStats() }
    
    var showStatsDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Configuración de Notificaciones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            },
            actions = {
                IconButton(onClick = { showStatsDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = "Estadísticas"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Estado FCM
            item {
                FCMStatusCard(
                    uiState = uiState,
                    onRefreshToken = { notificationViewModel.refreshFCMToken() }
                )
            }
            
            // Configuración principal
            item {
                MainNotificationSettings(
                    preferences = preferences,
                    onPreferencesChanged = { newPreferences ->
                        notificationViewModel.updatePreferences(newPreferences)
                    }
                )
            }
            
            // Tipos de notificación
            item {
                NotificationTypesSettings(
                    preferences = preferences,
                    onPreferencesChanged = { newPreferences ->
                        notificationViewModel.updatePreferences(newPreferences)
                    }
                )
            }
            
            // Horarios silenciosos
            item {
                QuietHoursSettings(
                    preferences = preferences,
                    onPreferencesChanged = { newPreferences ->
                        notificationViewModel.updatePreferences(newPreferences)
                    }
                )
            }
            
            // Configuración de sonido y vibración
            item {
                SoundVibrationSettings(
                    preferences = preferences,
                    onPreferencesChanged = { newPreferences ->
                        notificationViewModel.updatePreferences(newPreferences)
                    }
                )
            }
            
            // Estadísticas rápidas
            item {
                NotificationStatsCard(
                    stats = notificationStats,
                    onShowFullStats = { showStatsDialog = true }
                )
            }
            
            // Acciones de administración
            item {
                AdminActions(
                    onClearAllNotifications = {
                        // Implementar limpieza de notificaciones
                    },
                    onResetSettings = {
                        val defaultPreferences = LinageNotificationManager.NotificationPreferences()
                        notificationViewModel.updatePreferences(defaultPreferences)
                    }
                )
            }
        }
    }
    
    // Dialog de estadísticas
    if (showStatsDialog) {
        NotificationStatsDialog(
            stats = notificationStats,
            onDismiss = { showStatsDialog = false }
        )
    }
}

@Composable
private fun FCMStatusCard(
    uiState: NotificationViewModel.NotificationUiState,
    onRefreshToken: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (uiState.fcmTokenReady) {
                Color(0xFFE8F5E8)
            } else {
                Color(0xFFFFEBEE)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (uiState.fcmTokenReady) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.Error
                    },
                    contentDescription = null,
                    tint = if (uiState.fcmTokenReady) {
                        Color(0xFF4CAF50)
                    } else {
                        Color(0xFFF44336)
                    }
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Estado FCM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = if (uiState.fcmTokenReady) {
                            "Conectado y funcionando"
                        } else {
                            "Desconectado o con errores"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(onClick = onRefreshToken) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refrescar"
                        )
                    }
                }
            }
            
            uiState.lastSyncTime?.let { lastSync ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Última sincronización: ${formatTimestamp(lastSync)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun MainNotificationSettings(
    preferences: LinageNotificationManager.NotificationPreferences,
    onPreferencesChanged: (LinageNotificationManager.NotificationPreferences) -> Unit
) {
    SettingsCard(
        title = "Configuración Principal",
        icon = Icons.Default.Notifications
    ) {
        SettingsSwitch(
            title = "Habilitar Notificaciones",
            subtitle = "Recibir todas las notificaciones de Linage",
            checked = preferences.notificationsEnabled,
            onCheckedChange = { enabled ->
                onPreferencesChanged(preferences.copy(notificationsEnabled = enabled))
            }
        )
    }
}

@Composable
private fun NotificationTypesSettings(
    preferences: LinageNotificationManager.NotificationPreferences,
    onPreferencesChanged: (LinageNotificationManager.NotificationPreferences) -> Unit
) {
    SettingsCard(
        title = "Tipos de Notificación",
        icon = Icons.Default.Category
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsSwitch(
                title = "Promociones y Ofertas",
                subtitle = "Descuentos especiales y nuevos planes",
                checked = preferences.promotionsEnabled,
                onCheckedChange = { enabled ->
                    onPreferencesChanged(preferences.copy(promotionsEnabled = enabled))
                },
                enabled = preferences.notificationsEnabled
            )
            
            SettingsSwitch(
                title = "Alertas Técnicas",
                subtitle = "Mantenimiento y problemas de servicio",
                checked = preferences.technicalEnabled,
                onCheckedChange = { enabled ->
                    onPreferencesChanged(preferences.copy(technicalEnabled = enabled))
                },
                enabled = preferences.notificationsEnabled
            )
            
            SettingsSwitch(
                title = "Recordatorios de Facturación",
                subtitle = "Fechas de pago y facturas",
                checked = preferences.billingEnabled,
                onCheckedChange = { enabled ->
                    onPreferencesChanged(preferences.copy(billingEnabled = enabled))
                },
                enabled = preferences.notificationsEnabled
            )
            
            SettingsSwitch(
                title = "Sugerencias de LINA",
                subtitle = "Consejos personalizados de tu asistente virtual",
                checked = preferences.aiSuggestionsEnabled,
                onCheckedChange = { enabled ->
                    onPreferencesChanged(preferences.copy(aiSuggestionsEnabled = enabled))
                },
                enabled = preferences.notificationsEnabled
            )
        }
    }
}

@Composable
private fun QuietHoursSettings(
    preferences: LinageNotificationManager.NotificationPreferences,
    onPreferencesChanged: (LinageNotificationManager.NotificationPreferences) -> Unit
) {
    SettingsCard(
        title = "Horarios Silenciosos",
        icon = Icons.Default.Schedule
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsSwitch(
                title = "Habilitar Modo Silencioso",
                subtitle = "No mostrar notificaciones en horarios específicos",
                checked = preferences.quietHoursEnabled,
                onCheckedChange = { enabled ->
                    onPreferencesChanged(preferences.copy(quietHoursEnabled = enabled))
                },
                enabled = preferences.notificationsEnabled
            )
            
            if (preferences.quietHoursEnabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Inicio
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Inicio",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        OutlinedTextField(
                            value = "${preferences.quietHoursStart}:00",
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(Icons.Default.Schedule, null)
                            }
                        )
                    }
                    
                    // Fin
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fin",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        OutlinedTextField(
                            value = "${preferences.quietHoursEnd}:00",
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(Icons.Default.Schedule, null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SoundVibrationSettings(
    preferences: LinageNotificationManager.NotificationPreferences,
    onPreferencesChanged: (LinageNotificationManager.NotificationPreferences) -> Unit
) {
    SettingsCard(
        title = "Sonido y Vibración",
        icon = Icons.Default.VolumeUp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsSwitch(
                title = "Sonido",
                subtitle = "Reproducir sonido al recibir notificaciones",
                checked = preferences.soundEnabled,
                onCheckedChange = { enabled ->
                    onPreferencesChanged(preferences.copy(soundEnabled = enabled))
                },
                enabled = preferences.notificationsEnabled
            )
            
            SettingsSwitch(
                title = "Vibración",
                subtitle = "Vibrar al recibir notificaciones",
                checked = preferences.vibrationEnabled,
                onCheckedChange = { enabled ->
                    onPreferencesChanged(preferences.copy(vibrationEnabled = enabled))
                },
                enabled = preferences.notificationsEnabled
            )
        }
    }
}

@Composable
private fun NotificationStatsCard(
    stats: LinageNotificationManager.NotificationStats,
    onShowFullStats: () -> Unit
) {
    Card(
        onClick = onShowFullStats,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = Color(0xFFF37321)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Estadísticas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Última 24h",
                    value = stats.notificationsLast24h.toString()
                )
                
                StatItem(
                    label = "Última semana",
                    value = stats.notificationsLast7d.toString()
                )
                
                StatItem(
                    label = "Total",
                    value = stats.totalNotifications.toString()
                )
            }
        }
    }
}

@Composable
private fun AdminActions(
    onClearAllNotifications: () -> Unit,
    onResetSettings: () -> Unit
) {
    SettingsCard(
        title = "Administración",
        icon = Icons.Default.AdminPanelSettings
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onClearAllNotifications,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ClearAll,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Limpiar Todas las Notificaciones")
            }
            
            OutlinedButton(
                onClick = onResetSettings,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFF44336)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.RestoreFromTrash,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restaurar Configuración")
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFF37321)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            content()
        }
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                }
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                }
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFFF37321),
                checkedTrackColor = Color(0xFFF37321).copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF37321)
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NotificationStatsDialog(
    stats: LinageNotificationManager.NotificationStats,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "📊 Estadísticas Detalladas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Resumen de Actividad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Total de notificaciones: ${stats.totalNotifications}")
                        Text("Últimas 24 horas: ${stats.notificationsLast24h}")
                        Text("Última semana: ${stats.notificationsLast7d}")
                        
                        stats.lastNotificationTime?.let { lastTime ->
                            Text("Última notificación: ${formatTimestamp(lastTime)}")
                        }
                    }
                }
                
                if (stats.notificationsByType.isNotEmpty()) {
                    item {
                        Text(
                            text = "Por Tipo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            stats.notificationsByType.forEach { (type, count) ->
                                Text("${type.replaceFirstChar { it.uppercase() }}: $count")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)
    
    return when {
        minutes < 1 -> "Ahora"
        minutes < 60 -> "Hace ${minutes}m"
        hours < 24 -> "Hace ${hours}h"
        days < 7 -> "Hace ${days}d"
        else -> "Hace más de una semana"
    }
}