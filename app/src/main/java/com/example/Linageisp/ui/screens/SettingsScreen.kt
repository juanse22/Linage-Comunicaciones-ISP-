@file:OptIn(ExperimentalAnimationApi::class)

package com.example.Linageisp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Linageisp.R
import com.example.Linageisp.ui.components.*
import com.example.Linageisp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // Settings state
    var notificationsEnabled by remember { mutableStateOf(true) }
    var billingReminders by remember { mutableStateOf(true) }
    var serviceAlerts by remember { mutableStateOf(true) }
    var promotions by remember { mutableStateOf(false) }
    
    var darkTheme by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("Español") }
    var dataUsage by remember { mutableStateOf(true) }
    var autoBackup by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LinageBackground,
                        Color.White
                    )
                )
            )
    ) {
        // Top bar
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LinageOrange
                    )
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile section
            item {
                ProfileSection()
            }
            
            // Notifications section
            item {
                SettingsSection(
                    title = "Notificaciones"
                ) {
                    SettingsGroup {
                        SettingsGroupSwitchItem(
                            title = "Habilitar Notificaciones",
                            subtitle = "Recibir todas las notificaciones de la app",
                            icon = Icons.Default.Notifications,
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )
                        
                        SettingsGroupSwitchItem(
                            title = "Recordatorios de Facturación",
                            subtitle = "Notificaciones sobre fechas de vencimiento",
                            icon = Icons.Default.Receipt,
                            checked = billingReminders,
                            onCheckedChange = { billingReminders = it }
                        )
                        
                        SettingsGroupSwitchItem(
                            title = "Alertas de Servicio",
                            subtitle = "Notificaciones sobre el estado del servicio",
                            icon = Icons.Default.Warning,
                            checked = serviceAlerts,
                            onCheckedChange = { serviceAlerts = it }
                        )
                        
                        SettingsGroupSwitchItem(
                            title = "Promociones y Ofertas",
                            subtitle = "Recibir ofertas especiales y promociones",
                            icon = Icons.Default.LocalOffer,
                            checked = promotions,
                            onCheckedChange = { promotions = it },
                            showDivider = false
                        )
                    }
                }
            }
            
            // App preferences
            item {
                SettingsSection(
                    title = "Preferencias de la App"
                ) {
                    SettingsSwitchItem(
                        title = "Tema Oscuro",
                        subtitle = "Activar modo oscuro para la aplicación",
                        icon = Icons.Default.DarkMode,
                        checked = darkTheme,
                        onCheckedChange = { darkTheme = it }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SettingsDropdownItem(
                        title = "Idioma",
                        subtitle = "Idioma de la aplicación",
                        icon = Icons.Default.Language,
                        selectedValue = selectedLanguage,
                        options = listOf("Español", "English", "Português"),
                        onValueChanged = { selectedLanguage = it }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SettingsSwitchItem(
                        title = "Mostrar Uso de Datos",
                        subtitle = "Ver consumo de datos en tiempo real",
                        icon = Icons.Default.DataUsage,
                        checked = dataUsage,
                        onCheckedChange = { dataUsage = it }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SettingsSwitchItem(
                        title = "Respaldo Automático",
                        subtitle = "Respaldar configuraciones automáticamente",
                        icon = Icons.Default.Backup,
                        checked = autoBackup,
                        onCheckedChange = { autoBackup = it }
                    )
                }
            }
            
            // Account section
            item {
                SettingsSection(
                    title = "Cuenta"
                ) {
                    SettingsItem(
                        title = "Información Personal",
                        subtitle = "Editar datos del perfil y cuenta",
                        icon = Icons.Default.Person,
                        onClick = { /* Navigate to profile edit */ }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SettingsItem(
                        title = "Seguridad",
                        subtitle = "Cambiar contraseña y configuración de seguridad",
                        icon = Icons.Filled.Security,
                        onClick = { /* Navigate to security */ }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SettingsItem(
                        title = "Privacidad",
                        subtitle = "Controlar la información que compartes",
                        icon = Icons.Default.PrivacyTip,
                        onClick = { /* Navigate to privacy */ }
                    )
                }
            }
            
            // Help and support
            item {
                SettingsSection(
                    title = "Ayuda y Soporte"
                ) {
                    SettingsItem(
                        title = "Centro de Ayuda",
                        subtitle = "Preguntas frecuentes y tutoriales",
                        icon = Icons.Filled.Help,
                        onClick = { /* Navigate to help */ }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SettingsItem(
                        title = "Contactar Soporte",
                        subtitle = "Obtener ayuda de nuestro equipo técnico",
                        icon = Icons.Default.SupportAgent,
                        onClick = { /* Navigate to support */ }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SettingsItem(
                        title = "Enviar Comentarios",
                        subtitle = "Ayúdanos a mejorar la aplicación",
                        icon = Icons.Default.Feedback,
                        onClick = { /* Send feedback */ }
                    )
                }
            }
            
            // Legal section
            item {
                SettingsSection(
                    title = "Legal"
                ) {
                    SettingsItem(
                        title = stringResource(R.string.settings_terms),
                        subtitle = "Términos y condiciones del servicio",
                        icon = Icons.Default.Article,
                        onClick = { /* Show terms */ }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    SettingsItem(
                        title = stringResource(R.string.settings_privacy),
                        subtitle = "Política de privacidad y protección de datos",
                        icon = Icons.Default.Policy,
                        onClick = { /* Show privacy policy */ }
                    )
                }
            }
            
            // About and logout
            item {
                SettingsSection(
                    title = stringResource(R.string.settings_about)
                ) {
                    SettingsItem(
                        title = "Acerca de Linage ISP",
                        subtitle = "Versión 2.0.0",
                        icon = Icons.Default.Info,
                        onClick = { showAboutDialog = true }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true },
                        colors = CardDefaults.cardColors(
                            containerColor = ErrorRed.copy(alpha = 0.1f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                color = ErrorRed.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Logout,
                                        contentDescription = null,
                                        tint = ErrorRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Cerrar Sesión",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = ErrorRed
                                    )
                                )
                                Text(
                                    text = "Desconectar de tu cuenta",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ErrorRed.copy(alpha = 0.7f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
            
            // Add bottom padding for the last item
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // About dialog
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false }
        )
    }
    
    // Logout confirmation dialog
    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                // Handle logout
                showLogoutDialog = false
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@Composable
private fun ProfileSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile picture placeholder
            Surface(
                modifier = Modifier.size(64.dp),
                color = LinageOrange.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = LinageOrange,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // User info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Juan Sebastián",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    text = "juan.sebastian@email.com",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LinageGray
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Surface(
                    color = SuccessGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        )
                        Text(
                            text = "Plan Activo",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            
            IconButton(
                onClick = { /* Edit profile */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar perfil",
                    tint = LinageOrange
                )
            }
        }
    }
}

@Composable
private fun AboutDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    color = LinageOrange.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = LinageOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Column {
                    Text(
                        text = "Linage ISP",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Versión 2.0.0",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = LinageGray
                        )
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "Tu proveedor de internet confiable",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Desarrollado por Linage ISP\n\n" +
                            "© 2024 Linage ISP. Todos los derechos reservados.\n\n" +
                            "Esta aplicación te permite gestionar tu servicio de internet, " +
                            "realizar pagos, obtener soporte técnico y mucho más.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LinageGray,
                        lineHeight = 20.sp
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LinageOrange
                )
            ) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Cerrar Sesión",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que quieres cerrar sesión? " +
                        "Tendrás que iniciar sesión nuevamente para acceder a tu cuenta.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed
                )
            ) {
                Text("Cerrar Sesión")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancelar",
                    color = LinageGray
                )
            }
        }
    )
}