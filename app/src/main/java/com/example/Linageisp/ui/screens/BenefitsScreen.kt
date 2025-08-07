package com.example.Linageisp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Linageisp.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Categoría de beneficios
 */
data class BenefitCategory(
    val title: String,
    val emoji: String,
    val description: String,
    val benefits: List<BenefitItem>,
    val gradientColors: List<Color>
)

/**
 * Ítem de beneficio individual
 */
data class BenefitItem(
    val title: String,
    val description: String,
    val emoji: String,
    val tag: String = ""
)

/**
 * Pantalla de Beneficios con categorías y animaciones
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BenefitsScreen() {
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
                    endY = 1500f
                )
            ),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header de la pantalla
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = slideInVertically(
                    tween(600), initialOffsetY = { -it / 2 }
                ) + fadeIn(tween(600))
            ) {
                BenefitsHeader()
            }
        }
        
        // Estadísticas rápidas
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = slideInHorizontally(
                    tween(700, 200), initialOffsetX = { -it / 2 }
                ) + fadeIn(tween(700, 200))
            ) {
                QuickStats()
            }
        }
        
        // Categorías de beneficios
        val categories = getBenefitCategories()
        itemsIndexed(categories) { index, category ->
            AnimatedVisibility(
                visible = isLoaded,
                enter = slideInVertically(
                    tween(800, index * 200),
                    initialOffsetY = { it / 3 }
                ) + fadeIn(tween(800, index * 200))
            ) {
                BenefitCategoryCard(category = category)
            }
        }
        
        // Call to action
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = scaleIn(tween(600, 1000)) + fadeIn(tween(600, 1000))
            ) {
                CallToActionCard()
            }
        }
    }
}

/**
 * Header de la pantalla de beneficios
 */
@Composable
private fun BenefitsHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LinageWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
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
            // Decoración de fondo
            repeat(15) { index ->
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .offset(
                            x = ((index % 6) * 60).dp,
                            y = ((index / 6) * 40).dp
                        )
                        .background(
                            LinageWhite.copy(alpha = 0.1f),
                            RoundedCornerShape(15.dp)
                        )
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎁",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Beneficios Exclusivos",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = LinageWhite
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Descubre todo lo que incluye ser cliente Linage",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = LinageWhite.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Estadísticas rápidas
 */
@Composable
private fun QuickStats() {
    val stats = listOf(
        Triple("50+", "Beneficios", "🏆"),
        Triple("24/7", "Soporte", "🛠️"),
        Triple("1000+", "Clientes", "👥"),
        Triple("99%", "Satisfacción", "⭐")
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        itemsIndexed(stats) { index, (number, label, emoji) ->
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    tween(600, index * 150),
                    initialScale = 0.8f
                ) + fadeIn(tween(600, index * 150))
            ) {
                StatCard(number = number, label = label, emoji = emoji)
            }
        }
    }
}

/**
 * Tarjeta de estadística
 */
@Composable
private fun StatCard(number: String, label: String, emoji: String) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "stat_scale"
    )

    Card(
        onClick = { isPressed = true },
        modifier = Modifier
            .width(90.dp)
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = number,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = LinageOrange
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = LinageGray,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
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
 * Tarjeta de categoría de beneficios
 */
@Composable
private fun BenefitCategoryCard(category: BenefitCategory) {
    var isExpanded by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "category_scale"
    )
    
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "arrow_rotation"
    )

    Card(
        onClick = {
            isPressed = true
            isExpanded = !isExpanded
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
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            // Header de la categoría
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(category.gradientColors)
                    )
            ) {
                // Patrón decorativo
                repeat(12) { index ->
                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .offset(
                                x = ((index % 5) * 50).dp,
                                y = ((index / 5) * 30).dp
                            )
                            .background(
                                LinageWhite.copy(alpha = 0.15f),
                                RoundedCornerShape(12.dp)
                            )
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category.emoji,
                        fontSize = 36.sp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = LinageWhite
                            )
                        )
                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = LinageWhite.copy(alpha = 0.9f)
                            )
                        )
                    }
                    
                    // Indicador de expansión
                    Text(
                        text = if (isExpanded) "▲" else "▼",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = LinageWhite,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .graphicsLayer {
                                rotationZ = arrowRotation
                            }
                    )
                }
            }
            
            // Lista expandible de beneficios
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    category.benefits.forEachIndexed { index, benefit ->
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = slideInVertically(
                                tween(400, index * 100),
                                initialOffsetY = { it / 2 }
                            ) + fadeIn(tween(400, index * 100))
                        ) {
                            BenefitItemCard(benefit = benefit)
                        }
                    }
                }
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
 * Tarjeta de beneficio individual
 */
@Composable
private fun BenefitItemCard(benefit: BenefitItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LinageBeige
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = benefit.emoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = benefit.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (benefit.tag.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = LinageOrange
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = benefit.tag,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = LinageWhite,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = benefit.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LinageGray
                    )
                )
            }
        }
    }
}

/**
 * Call to action card
 */
@Composable
private fun CallToActionCard() {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "cta_scale"
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
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(LinageOrange, LinageOrangeDark),
                        radius = 800f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🚀",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¡Únete a Linage ISP!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = LinageWhite
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Disfruta de todos estos beneficios y más",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = LinageWhite.copy(alpha = 0.9f)
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { /* TODO: Navigate to plans */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LinageWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "📶 Ver Planes Disponibles",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = LinageOrange,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(200)
            isPressed = false
        }
    }
}

/**
 * Obtener las categorías de beneficios
 */
private fun getBenefitCategories(): List<BenefitCategory> {
    return listOf(
        BenefitCategory(
            title = "Conectividad",
            emoji = "📡",
            description = "Internet de alta velocidad y estabilidad",
            gradientColors = listOf(LinageOrange, LinageOrangeLight),
            benefits = listOf(
                BenefitItem(
                    title = "Fibra Óptica Premium",
                    description = "Tecnología de última generación",
                    emoji = "⚡",
                    tag = "NUEVO"
                ),
                BenefitItem(
                    title = "Velocidades hasta 900 Mbps",
                    description = "Para toda tu familia y dispositivos",
                    emoji = "🚀"
                ),
                BenefitItem(
                    title = "Conexión Estable 24/7",
                    description = "Sin interrupciones ni cortes",
                    emoji = "🔥"
                ),
                BenefitItem(
                    title = "IPv6 Nativo",
                    description = "Futuro de la conectividad",
                    emoji = "🌐"
                )
            )
        ),
        BenefitCategory(
            title = "Entretenimiento",
            emoji = "🎬",
            description = "Los mejores servicios de streaming",
            gradientColors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6)),
            benefits = listOf(
                BenefitItem(
                    title = "Paramount+ Gratis",
                    description = "1 mes incluido en todos los planes",
                    emoji = "🎭",
                    tag = "GRATIS"
                ),
                BenefitItem(
                    title = "Netflix Sin Límites",
                    description = "Streaming 4K sin buffering",
                    emoji = "📺"
                ),
                BenefitItem(
                    title = "Win Sports+",
                    description = "Toda la programación deportiva",
                    emoji = "⚽",
                    tag = "DEPORTES"
                ),
                BenefitItem(
                    title = "DIRECTV GO",
                    description = "Canales premium disponibles",
                    emoji = "📡"
                )
            )
        ),
        BenefitCategory(
            title = "Soporte y Servicio",
            emoji = "🛠️",
            description = "Asistencia técnica especializada",
            gradientColors = listOf(Color(0xFF4CAF50), Color(0xFF81C784)),
            benefits = listOf(
                BenefitItem(
                    title = "Soporte 24/7",
                    description = "Asistencia técnica las 24 horas",
                    emoji = "📞",
                    tag = "24/7"
                ),
                BenefitItem(
                    title = "Instalación Gratuita",
                    description = "Sin costo adicional de instalación",
                    emoji = "🔧",
                    tag = "GRATIS"
                ),
                BenefitItem(
                    title = "Técnicos Certificados",
                    description = "Personal altamente capacitado",
                    emoji = "👨‍🔧"
                ),
                BenefitItem(
                    title = "Mantenimiento Preventivo",
                    description = "Revisiones periódicas sin costo",
                    emoji = "🔍"
                )
            )
        ),
        BenefitCategory(
            title = "Descuentos Especiales",
            emoji = "💰",
            description = "Ofertas exclusivas para clientes",
            gradientColors = listOf(Color(0xFF9C27B0), Color(0xFFBA68C8)),
            benefits = listOf(
                BenefitItem(
                    title = "Descuento Familiar",
                    description = "20% OFF en segundo plan residencial",
                    emoji = "👨‍👩‍👧‍👦",
                    tag = "20% OFF"
                ),
                BenefitItem(
                    title = "Sin Permanencia Mínima",
                    description = "Cancela cuando quieras",
                    emoji = "🗓️"
                ),
                BenefitItem(
                    title = "Precio Fijo 12 Meses",
                    description = "Sin aumentos durante el primer año",
                    emoji = "🔒"
                ),
                BenefitItem(
                    title = "Referidos",
                    description = "Gana dinero por cada amigo referido",
                    emoji = "🎁"
                )
            )
        )
    )
}