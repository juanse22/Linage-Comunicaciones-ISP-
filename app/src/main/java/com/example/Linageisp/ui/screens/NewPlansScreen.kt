@file:OptIn(ExperimentalAnimationApi::class)

package com.example.Linageisp.ui.screens

import android.content.Intent
import android.net.Uri

import androidx.compose.animation.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
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
 * Nueva pantalla de planes con diseño EXACTO de las imágenes - fondo negro, gradientes naranjas
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NewPlansScreen(
    planViewModel: PlanViewModel
) {
    val uiState by planViewModel.uiState.collectAsState()
    
    // FONDO NEGRO COMO EN LAS IMÁGENES
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // FONDO NEGRO OBLIGATORIO
    ) {
        when {
            uiState.isLoading -> LoadingStateModern()
            uiState.errorMessage != null -> ErrorStateModern(
                errorMessage = uiState.errorMessage!!,
                onRetry = { planViewModel.retryLoading() }
            )
            uiState.plans.isNotEmpty() -> {
                // MOSTRAR TODOS LOS PLANES SIN FILTROS
                PlansListHorizontalStyle(plans = uiState.plans)
            }
            else -> EmptyStateModern(onRefresh = { planViewModel.retryLoading() })
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
 * Lista horizontal por categorías como en las imágenes - SIN FILTROS
 */
@Composable
private fun PlansListHorizontalStyle(plans: List<Plan>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // FONDO NEGRO
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // GRUPO: PLANES BÁSICOS
        item {
            PlanCategorySection(
                title = "Planes Básicos",
                plans = plans.filter { it.type == "Fibra Óptica" }
            )
        }
        
        // GRUPO: PLANES WIN+
        item {
            PlanCategorySection(
                title = "Planes Win+ Premium",
                plans = plans.filter { it.type == "Premium Win+" }
            )
        }
        
        // GRUPO: PLANES DIRECTV GO
        item {
            PlanCategorySection(
                title = "Planes DIRECTV GO",
                plans = plans.filter { it.type == "DIRECTV GO" }
            )
        }
        
        // GRUPO: PLANES NETFLIX
        item {
            PlanCategorySection(
                title = "Planes Netflix",
                plans = plans.filter { it.type == "Netflix" }
            )
        }
        
        // GRUPO: PLANES CON CÁMARAS (NUEVO)
        item {
            PlanCategorySection(
                title = "Planes con Cámaras",
                plans = plans.filter { it.type == "Cámaras" }
            )
        }
        
        // Espaciado final para el bottom nav
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PlanCategorySection(title: String, plans: List<Plan>) {
    if (plans.isNotEmpty()) {
        Column {
            // Título de categoría
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White // TEXTO BLANCO SOBRE FONDO NEGRO
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // SCROLL HORIZONTAL COMO EN LAS IMÁGENES
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                itemsIndexed(plans) { index, plan ->
                    PlanCardImageStyle(
                        plan = plan,
                        categoryIndex = index
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta de plan con diseño EXACTO de las imágenes - gradientes naranjas, fondo negro
 */
@Composable
private fun PlanCardImageStyle(plan: Plan, categoryIndex: Int) {
    val context = LocalContext.current
    
    // COLORES ESPECÍFICOS SEGÚN CATEGORÍA COMO EN LAS IMÁGENES
    val gradientColors = when {
        plan.type == "Fibra Óptica" -> listOf(
            Color(0xFFFF8A00), Color(0xFFFFB347) // Naranja básico
        )
        plan.type == "Premium Win+" -> listOf(
            Color(0xFFFF7043), Color(0xFFFF8A65) // Naranja rojizo Win+
        )
        plan.type == "DIRECTV GO" -> listOf(
            Color(0xFF424242), Color(0xFF616161) // Gris oscuro DIRECTV
        )
        plan.type == "Netflix" -> listOf(
            Color(0xFF8D4004), Color(0xFFBF6B04) // Marrón Netflix
        )
        plan.type == "Cámaras" -> listOf(
            Color(0xFFE65100), Color(0xFFFF9800) // Naranja cámaras
        )
        else -> listOf(Color(0xFFF37321), Color(0xFFFF9B47))
    }
    
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(320.dp), // TAMAÑO SIMILAR A LAS IMÁGENES
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(gradientColors)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // NOMBRE DEL PLAN GRANDE Y BLANCO
                Text(
                    text = plan.nombre,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                )
                
                // PRECIO GRANDE
                Text(
                    text = plan.precio,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 36.sp
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // BENEFICIOS COMO BULLETS
                plan.getBeneficiosList().take(5).forEach { beneficio ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = beneficio,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // LOGOS EN LA PARTE INFERIOR
                when {
                    plan.type.contains("Win+") -> {
                        Text(
                            text = "WIN+ sports",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                    plan.type.contains("DIRECTV") -> {
                        Text(
                            text = "DIRECTV GO",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                    plan.type.contains("Netflix") -> {
                        Text(
                            text = "NETFLIX",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color(0xFFE50914),
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                    plan.type.contains("Cámaras") -> {
                        Text(
                            text = "📹 SEGURIDAD",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // BOTÓN CONTRATAR
                Button(
                    onClick = { 
                        // Generar mensaje de WhatsApp
                        val message = "¡Hola! 👋 Estoy interesado en el plan ${plan.nombre} de ${plan.precio} " +
                                "con velocidad de ${plan.velocidad}. ¿Podrían darme más información? Gracias 😊"
                        
                        val whatsappIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://wa.me/?text=${Uri.encode(message)}")
                        )
                        context.startActivity(whatsappIntent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Contratar",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
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
    
    // Colores del gradiente basados en el tipo de plan
    val gradientColors = when (plan.type) {
        "Fibra Óptica" -> listOf(Color(0xFF4CAF50), Color(0xFF81C784))  // Verde para básicos
        "Premium Win+" -> listOf(Color(0xFFFF6D00), Color(0xFFFF8F00))  // Naranja para Win+
        "DIRECTV GO" -> listOf(Color(0xFF2196F3), Color(0xFF64B5F6))    // Azul para DIRECTV
        "Netflix" -> listOf(Color(0xFFE50914), Color(0xFFFF5722))      // Rojo Netflix
        else -> listOf(LinageOrange, LinageOrangeLight)                  // Naranja por defecto
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

/**
 * Fila de chips de filtros por categoría
 */
@Composable
private fun FilterChipsRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LinageWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "🎯 Filtrar por categoría",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LinageOrangeDark
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                itemsIndexed(categories) { index, category ->
                    FilterChip(
                        category = category,
                        isSelected = category == selectedCategory,
                        onClick = { onCategorySelected(category) }
                    )
                }
            }
        }
    }
}

/**
 * Chip individual de filtro
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) LinageOrange else LinageWhite,
        animationSpec = tween(300),
        label = "chip_background"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) LinageWhite else LinageGray,
        animationSpec = tween(300),
        label = "chip_text"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(2.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji específico por categoría
            val emoji = when (category) {
                "Todos" -> "📋"
                "Básicos" -> "🔥"
                "Win+" -> "⚽"
                "DIRECTV GO" -> "📺"
                "Netflix" -> "🎬"
                else -> "📶"
            }
            
            Text(
                text = emoji,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.width(6.dp))
            
            Text(
                text = category,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
            )
        }
    }
}