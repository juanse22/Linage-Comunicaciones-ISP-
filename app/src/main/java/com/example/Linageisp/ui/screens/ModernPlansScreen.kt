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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Linageisp.data.PlanModern
import com.example.Linageisp.viewmodel.PlanModernViewModel

/**
 * Pantalla moderna de planes con estilo similar a Tigo
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ModernPlansScreen(
    viewModel: PlanModernViewModel = viewModel()
) {
    val plans by viewModel.plans
    val isLoading by viewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F4F0), // Beige claro
                        Color.White
                    )
                )
            )
    ) {
        // Header
        ModernPlansHeader()
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFF37321)
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(plans) { plan ->
                        ModernPlanCard(plan = plan)
                    }
                    
                    // Espaciado final para bottom navigation
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

/**
 * Header de la pantalla
 */
@Composable
private fun ModernPlansHeader() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFF37321),
                            Color(0xFFFF9B47)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "📶 Planes de Internet",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Encuentra la velocidad perfecta para ti",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
            }
        }
    }
}

/**
 * Tarjeta moderna de plan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernPlanCard(plan: PlanModern) {
    var isExpanded by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Animaciones
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "cardScale"
    )

    val cardElevation by animateDpAsState(
        targetValue = if (isExpanded) 12.dp else 8.dp,
        animationSpec = tween(300),
        label = "cardElevation"
    )

    val headerHeight by animateDpAsState(
        targetValue = if (isExpanded) 140.dp else 120.dp,
        animationSpec = tween(300),
        label = "headerHeight"
    )

    Box {
        // Badge de recomendado
        if (plan.isRecommended) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .zIndex(2f),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF4CAF50),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "⭐ Recomendado",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Badge de popular
        if (plan.isPopular) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 8.dp, y = (-8).dp)
                    .zIndex(2f),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFF5722),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "🔥 Popular",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Tarjeta principal
        Card(
            onClick = { 
                isExpanded = !isExpanded 
            },
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(
                    scaleX = cardScale,
                    scaleY = cardScale
                )
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                // Header con gradiente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(headerHeight)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(plan.color.primary, plan.color.secondary)
                            )
                        )
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    // Patrón decorativo
                    repeat(20) { index ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .offset(
                                    x = ((index % 6) * 50).dp,
                                    y = ((index / 6) * 35).dp
                                )
                                .background(
                                    Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(15.dp)
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
                                text = plan.speed,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 36.sp
                                )
                            )
                            Text(
                                text = "de velocidad",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        // Precio
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            // Precio anterior tachado
                            plan.originalPrice?.let { originalPrice ->
                                Text(
                                    text = originalPrice,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White.copy(alpha = 0.7f),
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                )
                            }
                            
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                shadowElevation = 4.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = plan.price,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            color = plan.color.primary,
                                            fontSize = 22.sp
                                        )
                                    )
                                    Text(
                                        text = "/mes",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF757575),
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Contenido expandible
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Beneficios principales (siempre visibles)
                    Text(
                        text = "✨ Beneficios incluidos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Mostrar algunos beneficios
                    val visibleBenefits = if (isExpanded) plan.benefits else plan.benefits.take(3)
                    
                    visibleBenefits.forEach { benefit ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(300)) + slideInVertically(tween(300))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(24.dp),
                                    shape = CircleShape,
                                    color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(4.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = benefit,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF424242)
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Botón expandir/contraer
                    if (plan.benefits.size > 3) {
                        TextButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isExpanded) "Ver menos" else "Ver más beneficios",
                                    color = plan.color.primary,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = plan.color.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón contratar con animación de rebote
                    val interactionSource = remember { MutableInteractionSource() }
                    
                    Button(
                        onClick = {
                            isPressed = true
                            
                            // Generar mensaje para WhatsApp
                            val message = "¡Hola! 👋 Estoy interesado en el plan de ${plan.speed} " +
                                    "por ${plan.price} al mes. ¿Podrían darme más información? Gracias 😊"
                            
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
                            containerColor = plan.color.primary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 12.dp
                        ),
                        interactionSource = interactionSource
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
                                    color = Color.White
                                )
                            )
                        }
                    }

                    // Información adicional
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "🚀 Activación inmediata | 📞 Soporte 24/7 | 🔧 Instalación gratuita",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF757575),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // Reset estado de presión
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(200)
            isPressed = false
        }
    }
}