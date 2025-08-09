@file:OptIn(ExperimentalAnimationApi::class)

package com.example.Linageisp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import android.content.Intent
import android.net.Uri
import com.example.Linageisp.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Opción del menú de cuenta
 */
data class AccountMenuItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val emoji: String,
    val hasNotification: Boolean = false
)

/**
 * Información de facturación
 */
data class BillingInfo(
    val amount: String,
    val dueDate: String,
    val status: String,
    val statusColor: Color
)

/**
 * Pantalla de Cuenta con información del usuario y opciones
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AccountScreen(
    onNavigateToSpeedTest: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {},
    onNavigateToBilling: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    var isLoaded by remember { mutableStateOf(false) }
    
    // Trigger de animaciones
    LaunchedEffect(Unit) {
        delay(400)
        isLoaded = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(LinageBeige, LinageWhite),
                    startY = 0f,
                    endY = 1200f
                )
            ),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header con perfil del usuario
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = slideInVertically(
                    tween(600), initialOffsetY = { -it / 2 }
                ) + fadeIn(tween(600))
            ) {
                UserProfileHeader()
            }
        }
        
        // Información de facturación
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = slideInHorizontally(
                    tween(700, 200), initialOffsetX = { -it / 3 }
                ) + fadeIn(tween(700, 200))
            ) {
                BillingInfoCard()
            }
        }
        
        // Estado de conexión
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = slideInHorizontally(
                    tween(800, 400), initialOffsetX = { it / 3 }
                ) + fadeIn(tween(800, 400))
            ) {
                ConnectionStatusCard()
            }
        }
        
        // Opciones del menú
        val menuItems = getAccountMenuItems()
        items(menuItems) { menuItem ->
            AnimatedVisibility(
                visible = isLoaded,
                enter = slideInVertically(
                    tween(600, 600), initialOffsetY = { it / 4 }
                ) + fadeIn(tween(600, 600))
            ) {
                AccountMenuItemCard(
                    menuItem = menuItem,
                    onNavigateToSpeedTest = onNavigateToSpeedTest,
                    onNavigateToSupport = onNavigateToSupport,
                    onNavigateToBilling = onNavigateToBilling,
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        }
        
        // Información de contacto
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = scaleIn(tween(600, 1000)) + fadeIn(tween(600, 1000))
            ) {
                ContactInfoCard()
            }
        }
    }
}

/**
 * Header con perfil del usuario
 */
@Composable
private fun UserProfileHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LinageWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(LinageOrange, LinageOrangeLight)
                    )
                )
        ) {
            // Patrón decorativo
            repeat(20) { index ->
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .offset(
                            x = ((index % 6) * 50).dp,
                            y = ((index / 6) * 35).dp
                        )
                        .background(
                            LinageWhite.copy(alpha = 0.1f),
                            RoundedCornerShape(17.dp)
                        )
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar del usuario
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(LinageWhite, CircleShape)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(LinageOrangeSoft, LinageBeige)
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 36.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Información del usuario
                Text(
                    text = "Juan Sebastián",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LinageWhite
                    )
                )
                
                Text(
                    text = "Cliente Premium • Plan 400 Megas",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = LinageWhite.copy(alpha = 0.9f)
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Badge de estado
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessGreen
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "🟢 Servicio Activo",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = LinageWhite,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta de información de facturación
 */
@Composable
private fun BillingInfoCard() {
    val context = LocalContext.current
    val billingInfo = BillingInfo(
        amount = "$70.000",
        dueDate = "15 de Agosto",
        status = "Pagado",
        statusColor = SuccessGreen
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LinageWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💳 Facturación",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = LinageOrangeDark
                    )
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = billingInfo.statusColor.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = billingInfo.status,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = billingInfo.statusColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información de factura
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Monto",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = LinageGray
                        )
                    )
                    Text(
                        text = billingInfo.amount,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LinageOrange
                        )
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Próximo Pago",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = LinageGray
                        )
                    )
                    Text(
                        text = billingInfo.dueDate,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón de pago
            Button(
                onClick = { 
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://combopay.co/invoices/linage-comunicaciones"))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LinageOrangeSoft
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "💰 Pagar con ComboPay",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = LinageOrangeDark,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/**
 * Tarjeta de estado de conexión
 */
@Composable
private fun ConnectionStatusCard() {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "connection_scale"
    )

    Card(
        onClick = { isPressed = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = LinageWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de conexión
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        SuccessGreen.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📶",
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Conexión Estable",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Velocidad: 387 Mbps • Latencia: 12ms",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LinageGray
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🟢 Online desde hace 15 días",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = SuccessGreen,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            
            // Botón de test de velocidad
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = LinageOrangeSoft
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Test",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = LinageOrange,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

/**
 * Tarjeta de opción del menú
 */
@Composable
private fun AccountMenuItemCard(
    menuItem: AccountMenuItem,
    onNavigateToSpeedTest: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {},
    onNavigateToBilling: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "menu_item_scale"
    )

    Card(
        onClick = { 
            isPressed = true
            when (menuItem.title) {
                "Soporte Técnico" -> onNavigateToSupport()
                "Configuración" -> onNavigateToSettings()
                else -> { /* Handle other menu items */ }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = LinageWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con emoji
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        LinageOrangeSoft,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = menuItem.emoji,
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Texto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = menuItem.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = menuItem.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LinageGray
                    )
                )
            }
            
            // Notificación e indicador
            if (menuItem.hasNotification) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            ErrorRed,
                            CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Navegar",
                tint = LinageGray
            )
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

/**
 * Tarjeta de información de contacto
 */
@Composable
private fun ContactInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LinageWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📞 Contactanos",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = LinageOrangeDark
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Opciones de contacto
            val contactOptions = listOf(
                Triple("📱", "WhatsApp", "+57 302 4478864"),
                Triple("☎️", "Línea de Atención", "+57 (1) 234 5678"),
                Triple("📧", "Email", "soporte@linageisp.com"),
                Triple("💰", "Pagar Factura", "ComboPay")
            )
            
            contactOptions.forEach { (emoji, title, info) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = emoji,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = info,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LinageGray
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Horario de atención
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = LinageBeige
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "🕐 Horario de Atención: Lunes a Domingo 24/7",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

/**
 * Obtener opciones del menú de cuenta
 */
private fun getAccountMenuItems(): List<AccountMenuItem> {
    return listOf(
        AccountMenuItem(
            icon = Icons.Default.Person,
            title = "Perfil de Usuario",
            description = "Actualiza tu información personal",
            emoji = "👤"
        ),
        AccountMenuItem(
            icon = Icons.Default.AccountCircle,
            title = "Mi Plan Actual",
            description = "Detalles y configuración del plan",
            emoji = "📋"
        ),
        AccountMenuItem(
            icon = Icons.Default.Notifications,
            title = "Notificaciones",
            description = "Configurar alertas y avisos",
            emoji = "🔔",
            hasNotification = true
        ),
        AccountMenuItem(
            icon = Icons.Default.Lock,
            title = "Seguridad",
            description = "Cambiar contraseña y configuración",
            emoji = "🔐"
        ),
        AccountMenuItem(
            icon = Icons.Default.Info,
            title = "Soporte Técnico",
            description = "Reportar problemas o consultas",
            emoji = "🛠️"
        ),
        AccountMenuItem(
            icon = Icons.Default.Settings,
            title = "Configuración",
            description = "Ajustes de la aplicación",
            emoji = "⚙️"
        ),
        AccountMenuItem(
            icon = Icons.Default.Info,
            title = "Acerca de",
            description = "Información de la aplicación",
            emoji = "ℹ️"
        )
    )
}