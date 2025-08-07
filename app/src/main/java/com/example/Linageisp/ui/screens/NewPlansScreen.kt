package com.example.Linageisp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Linageisp.data.Plan
import com.example.Linageisp.ui.theme.*
import com.example.Linageisp.viewmodel.PlanViewModel
import kotlinx.coroutines.delay

/**
 * Nueva pantalla de planes con diseño moderno estilo Tigo
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NewPlansScreen(
    planViewModel: PlanViewModel
) {
    val uiState by planViewModel.uiState.collectAsState()
    var isLoaded by remember { mutableStateOf(false) }
    
    // Animación de carga
    LaunchedEffect(Unit) {
        delay(400)
        isLoaded = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(LinageBeige, LinageWhite),
                    startY = 0f,
                    endY = 1000f
                )
            )
    ) {
        // Header de la pantalla
        AnimatedVisibility(
            visible = isLoaded,
            enter = slideInVertically(
                tween(600), initialOffsetY = { -it / 2 }
            ) + fadeIn(tween(600))
        ) {
            PlansHeader(
                onRefresh = { planViewModel.retryLoading() }
            )
        }

        when {
            uiState.isLoading -> {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(400))
                ) {
                    LoadingStateModern()
                }
            }
            
            uiState.errorMessage != null -> {
                ErrorStateModern(
                    errorMessage = uiState.errorMessage!!,
                    onRetry = { planViewModel.retryLoading() }
                )
            }
            
            uiState.plans.isNotEmpty() -> {
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(tween(800, 300)) + slideInVertically(
                        tween(800, 300), initialOffsetY = { it / 3 }
                    )
                ) {
                    PlansListModern(plans = uiState.plans)
                }
            }
            
            else -> {
                EmptyStateModern(
                    onRefresh = { planViewModel.retryLoading() }
                )
            }
        }
    }
}

/**
 * Header moderno de la pantalla de planes
 */
@Composable
private fun PlansHeader(onRefresh: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "📶 Planes de Internet",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LinageOrange
                    )
                )
                Text(
                    text = "Encuentra el plan perfecto para ti",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LinageGray
                    )
                )
            }
            
            // Botón de actualizar con animación
            var isRefreshing by remember { mutableStateOf(false) }
            val rotationAngle by animateFloatAsState(
                targetValue = if (isRefreshing) 360f else 0f,
                animationSpec = tween(1000, easing = EaseInOutCubic),
                label = "refresh_rotation"
            )
            
            IconButton(
                onClick = {
                    isRefreshing = true
                    onRefresh()
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        LinageOrangeSoft,
                        RoundedCornerShape(12.dp)
                    )
                    .graphicsLayer {
                        rotationZ = rotationAngle
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualizar",
                    tint = LinageOrange
                )
            }
            
            // Reset rotación
            LaunchedEffect(isRefreshing) {
                if (isRefreshing) {
                    delay(1000)
                    isRefreshing = false
                }
            }
        }
    }
}

/**
 * Estado de carga moderno
 */
@Composable
private fun LoadingStateModern() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .size(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = LinageWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Indicador de progreso personalizado
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    color = LinageOrange,
                    strokeWidth = 4.dp
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "Cargando planes...",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = LinageGray
                    ),
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "✨",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

/**
 * Estado de error moderno
 */
@Composable
private fun ErrorStateModern(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = LinageWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 48.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Error al cargar",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LinageGray
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LinageOrange
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🔄 Reintentar",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = LinageWhite,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

/**
 * Estado vacío moderno
 */
@Composable
private fun EmptyStateModern(onRefresh: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = LinageWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📭",
                    fontSize = 48.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "No hay planes disponibles",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Intenta actualizar la lista",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LinageGray
                    )
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Button(
                    onClick = onRefresh,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LinageOrange
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🔄 Actualizar",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = LinageWhite,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

/**
 * Lista moderna de planes
 */
@Composable
private fun PlansListModern(plans: List<Plan>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(plans) { index, plan ->
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    tween(600, index * 150),
                    initialOffsetX = { if (index % 2 == 0) -it / 2 else it / 2 }
                ) + fadeIn(tween(600, index * 150))
            ) {
                ModernPlanCard(
                    plan = plan,
                    isRecommended = index == 1 // El segundo plan como recomendado
                )
            }
        }
        
        // Espaciado final para el bottom nav
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * Tarjeta de plan moderna estilo Tigo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernPlanCard(
    plan: Plan,
    isRecommended: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "plan_card_scale"
    )
    
    // Colores del gradiente basados en el plan
    val gradientColors = when {
        plan.nombre.contains("200") -> listOf(Color(0xFF4CAF50), Color(0xFF81C784))
        plan.nombre.contains("400") -> listOf(LinageOrange, LinageOrangeLight)
        plan.nombre.contains("600") -> listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
        else -> listOf(Color(0xFF9C27B0), Color(0xFFBA68C8))
    }

    Box {
        // Badge de recomendado
        if (isRecommended) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .zIndex(1f),
                colors = CardDefaults.cardColors(
                    containerColor = SuccessGreen
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "⭐ Recomendado",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = LinageWhite,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            colors = CardDefaults.cardColors(
                containerColor = LinageWhite
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isRecommended) 12.dp else 8.dp
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header con gradiente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.horizontalGradient(gradientColors),
                            RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    // Patrón de fondo
                    repeat(20) { index ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .offset(
                                    x = ((index % 5) * 50).dp,
                                    y = ((index / 5) * 30).dp
                                )
                                .background(
                                    LinageWhite.copy(alpha = 0.1f),
                                    RoundedCornerShape(20.dp)
                                )
                        )
                    }
                    
                    // Contenido del header
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = plan.nombre,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = LinageWhite,
                                    fontSize = 32.sp
                                )
                            )
                            Text(
                                text = plan.velocidad,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = LinageWhite.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        
                        // Etiqueta de precio destacada
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = LinageWhite
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = plan.precio.replace("$", ""),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = gradientColors[0],
                                        fontSize = 20.sp
                                    )
                                )
                                Text(
                                    text = "/mes",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = LinageGray,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
                
                // Contenido principal
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Beneficios principales destacados
                    val mainBenefits = listOf(
                        "🔥 Fibra óptica premium",
                        "🎬 1 mes gratis de Paramount+",
                        "📺 1 pantalla simultánea",
                        "🛠️ Instalación gratuita"
                    )
                    
                    Text(
                        text = "✨ Beneficios incluidos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LinageOrangeDark
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    mainBenefits.forEach { benefit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = benefit,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Botón de contratar moderno
                    Button(
                        onClick = {
                            isPressed = true
                            
                            // Generar mensaje de WhatsApp
                            val message = "¡Hola! 👋 Estoy interesado en el plan ${plan.nombre} de ${plan.precio} " +
                                    "con velocidad de ${plan.velocidad}. ¿Podrían darme más información? Gracias 😊"
                            
                            val whatsappIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://wa.me/?text=${Uri.encode(message)}")
                            )
                            context.startActivity(whatsappIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = gradientColors[0]
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💬",
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Contratar Plan",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LinageWhite
                                )
                            )
                        }
                    }
                    
                    // Información adicional
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "🚀 Activación inmediata | 📞 Soporte 24/7",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = LinageGray,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
    
    // Reset pressed state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(200)
            isPressed = false
        }
    }
}