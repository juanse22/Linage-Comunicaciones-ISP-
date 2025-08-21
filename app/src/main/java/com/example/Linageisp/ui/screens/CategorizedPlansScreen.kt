package com.example.Linageisp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.Linageisp.data.*
import com.example.Linageisp.ui.animations.PlanAnimations
import com.example.Linageisp.ui.theme.*

/**
 * Pantalla de planes organizada por categorías con diseño de proporción áurea
 */
@Composable
fun CategorizedPlansScreen() {
    var categoriesState by remember { mutableStateOf<List<PlanCategory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Simular carga de planes (reemplazar con datos reales)
    LaunchedEffect(Unit) {
        delay(500)
        val samplePlans = createSamplePlans()
        categoriesState = createPlanCategories(samplePlans)
        isLoading = false
    }
    
    if (isLoading) {
        LoadingPlansScreen()
    } else {
        PlansContentScreen(
            categories = categoriesState,
            onCategoryToggle = { categoryId ->
                categoriesState = categoriesState.map { category ->
                    if (category.id == categoryId) {
                        category.copy(isExpanded = !category.isExpanded)
                    } else category
                }
            }
        )
    }
}

@Composable
private fun LoadingPlansScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(GoldenRatio.ICON_SIZE_XL),
                color = LinageOrange,
                strokeWidth = GoldenRatio.SPACING_XS
            )
            Spacer(modifier = Modifier.height(GoldenRatio.SPACING_LG))
            Text(
                text = "Cargando planes perfectos para ti...",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = LinageWhite,
                    fontSize = GoldenRatio.TEXT_SIZE_BODY_LARGE
                )
            )
        }
    }
}

@Composable
private fun PlansContentScreen(
    categories: List<PlanCategory>,
    onCategoryToggle: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(
            horizontal = GoldenRatio.SPACING_LG,
            vertical = GoldenRatio.SPACING_XL
        ),
        verticalArrangement = Arrangement.spacedBy(GoldenRatio.PLAN_VERTICAL_SPACING)
    ) {
        item {
            PlansHeaderSection()
        }
        
        itemsIndexed(categories) { index, category ->
            AnimatedVisibility(
                visible = true,
                enter = PlanAnimations.CategoryEntranceSpecs.slideInFromTop(
                    delayMs = index * GoldenRatio.PLAN_STAGGER_DELAY
                )
            ) {
                GoldenRatioCategoryCard(
                    category = category,
                    onToggle = { onCategoryToggle(category.id) }
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(GoldenRatio.SPACING_MEGA))
        }
    }
}

@Composable
private fun PlansHeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nuestros Planes",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                color = LinageWhite,
                fontSize = GoldenRatio.TEXT_SIZE_DISPLAY
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(GoldenRatio.SPACING_MD))
        Text(
            text = "Elige la categoría que mejor se adapte a ti",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = LinageWhite.copy(alpha = 0.8f),
                fontSize = GoldenRatio.TEXT_SIZE_BODY_LARGE
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(GoldenRatio.SPACING_XXL))
    }
}

@Composable
private fun GoldenRatioCategoryCard(
    category: PlanCategory,
    onToggle: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by PlanAnimations.rememberPlanCardInteraction(isPressed = isPressed)
    val arrowRotation by PlanAnimations.categoryExpansionAnimation(isExpanded = category.isExpanded)
    
    // Estilo Frutiger Aero: Gradientes suaves y efectos de vidrio
    val fruttigerBrush = Brush.radialGradient(
        colors = listOf(
            category.color.copy(alpha = 0.9f),
            category.color.copy(alpha = 0.7f),
            category.color.copy(alpha = 0.5f)
        ),
        radius = 800f
    )
    
    Card(
        onClick = {
            isPressed = true
            onToggle()
        },
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = GoldenRatio.ELEVATION_LG),
        shape = RoundedCornerShape(GoldenRatio.CATEGORY_CARD_RADIUS)
    ) {
        Column {
            // Header con estilo Frutiger Aero - Minimalista y limpio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(GoldenRatio.CATEGORY_HEADER_HEIGHT * GoldenRatio.PHI_INVERSE)
                    .background(fruttigerBrush)
            ) {
                // Patrón decorativo sutil estilo Aero
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val bubbleSize = size.width / (12 * GoldenRatio.PHI)
                    for (i in 0..20) {
                        val x = (i % 7) * bubbleSize * GoldenRatio.PHI * 1.5f
                        val y = (i / 7) * bubbleSize * GoldenRatio.PHI * 1.2f
                        drawCircle(
                            color = Color.White.copy(alpha = 0.08f),
                            radius = bubbleSize / 3,
                            center = Offset(x, y)
                        )
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(GoldenRatio.SPACING_LG),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ícono con efecto glassmorphism
                    Surface(
                        color = Color.White.copy(alpha = 0.25f),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        modifier = Modifier.size(GoldenRatio.CATEGORY_ICON_SIZE)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = category.icon,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = GoldenRatio.TEXT_SIZE_HEADLINE * GoldenRatio.PHI_INVERSE * 1.2f
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(GoldenRatio.SPACING_LG))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = GoldenRatio.PLAN_TITLE_SIZE * GoldenRatio.PHI * 1.1f,
                                letterSpacing = 0.3.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(GoldenRatio.SPACING_XS))
                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = GoldenRatio.PLAN_SPEED_SIZE * 1.1f,
                                lineHeight = GoldenRatio.PLAN_SPEED_SIZE * GoldenRatio.PHI * 1.2f,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    // Indicador de expansión estilo Aero con glassmorphism
                    Surface(
                        color = Color.White.copy(alpha = 0.25f),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        modifier = Modifier.size(GoldenRatio.SPACING_XXXL)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = if (category.isExpanded) "Contraer" else "Expandir",
                            tint = Color.White,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(GoldenRatio.SPACING_SM)
                                .graphicsLayer { rotationZ = arrowRotation }
                        )
                    }
                }
            }
            
            // Información compacta de la categoría
            if (!category.isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(GoldenRatio.SPACING_LG),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge con cantidad de planes - estilo Aero
                    Surface(
                        color = category.color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(GoldenRatio.PLAN_BUTTON_RADIUS * 2),
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Text(
                            text = "${category.plans.size} opciones",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = category.color,
                                fontWeight = FontWeight.Bold,
                                fontSize = GoldenRatio.PLAN_BENEFIT_SIZE * 1.2f
                            ),
                            modifier = Modifier.padding(
                                horizontal = GoldenRatio.SPACING_LG,
                                vertical = GoldenRatio.SPACING_SM
                            )
                        )
                    }
                    
                    // Texto "Ver planes" sutil
                    Text(
                        text = "Toca para ver planes",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontSize = GoldenRatio.PLAN_BENEFIT_SIZE,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            
            // Lista expandible de planes con proporción áurea
            AnimatedVisibility(
                visible = category.isExpanded,
                enter = PlanAnimations.CategoryEntranceSpecs.expandWithFade(),
                exit = PlanAnimations.CategoryExitSpecs.shrinkWithFade()
            ) {
                LazyRow(
                    modifier = Modifier.padding(GoldenRatio.SPACING_LG),
                    horizontalArrangement = Arrangement.spacedBy(GoldenRatio.PLAN_HORIZONTAL_SPACING),
                    contentPadding = PaddingValues(horizontal = GoldenRatio.SPACING_SM)
                ) {
                    itemsIndexed(category.plans) { index, plan ->
                        AnimatedVisibility(
                            visible = category.isExpanded,
                            enter = PlanAnimations.PlanCardEntranceSpecs.slideInFromRight(
                                delayMs = index * GoldenRatio.PLAN_STAGGER_DELAY
                            )
                        ) {
                            FrutigerAeroPlanCard(
                                plan = plan, 
                                categoryColor = category.color
                            )
                        }
                    }
                }
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(120)
            isPressed = false
        }
    }
}

@Composable
private fun FrutigerAeroPlanCard(
    plan: Plan, 
    categoryColor: Color
) {
    val context = LocalContext.current
    var isHovered by remember { mutableStateOf(false) }
    
    val elevation by PlanAnimations.rememberPlanCardElevation(isHovered = isHovered)
    
    // Estilo Frutiger Aero para las tarjetas de plan
    val aeroBrush = Brush.linearGradient(
        colors = listOf(
            categoryColor.copy(alpha = 0.9f),
            categoryColor.copy(alpha = 0.6f),
            categoryColor.copy(alpha = 0.8f)
        ),
        start = Offset(0f, 0f),
        end = Offset(300f, 300f)
    )
    
    Card(
        onClick = {
            val message = "Hola, estoy interesado en el plan ${plan.nombre} de ${plan.precio}. " +
                    "¿Podrían darme más información y disponibilidad? Gracias."
            val whatsappIntent = Intent(
                Intent.ACTION_VIEW, 
                Uri.parse("https://wa.me/+573053040593?text=${Uri.encode(message)}")
            )
            context.startActivity(whatsappIntent)
        },
        modifier = Modifier
            .width(GoldenRatio.PLAN_CARD_WIDTH)
            .height(GoldenRatio.PLAN_CARD_HEIGHT)
            .clickable {
                isHovered = !isHovered
            },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        shape = RoundedCornerShape(GoldenRatio.SPACING_LG * 1.5f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header del plan con estilo Aero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(GoldenRatio.PLAN_HEADER_HEIGHT)
                    .background(aeroBrush)
            ) {
                // Efecto de burbujas sutil
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val bubbleCount = 8
                    for (i in 0..bubbleCount) {
                        val x = (i % 3) * size.width / 3 + size.width / 6
                        val y = (i / 3) * size.height / 4 + size.height / 8
                        drawCircle(
                            color = Color.White.copy(alpha = 0.06f),
                            radius = size.width / 12,
                            center = Offset(x, y)
                        )
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(GoldenRatio.SPACING_LG),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Nombre del plan con glassmorphism
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(GoldenRatio.SPACING_MD),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = plan.nombre,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = GoldenRatio.PLAN_TITLE_SIZE * 1.1f,
                                letterSpacing = 0.3.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(GoldenRatio.SPACING_MD)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(GoldenRatio.SPACING_SM))
                    
                    // Precio destacado con efecto glow
                    Text(
                        text = plan.precio,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = GoldenRatio.PLAN_PRICE_SIZE * 1.2f,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.White.copy(alpha = 0.5f),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        ),
                        maxLines = 1
                    )
                    
                    // Velocidad con estilo pill
                    Surface(
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(GoldenRatio.PLAN_BUTTON_RADIUS * 2),
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Text(
                            text = plan.velocidad,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = GoldenRatio.PLAN_SPEED_SIZE * 1.1f
                            ),
                            modifier = Modifier.padding(
                                horizontal = GoldenRatio.SPACING_LG,
                                vertical = GoldenRatio.SPACING_SM
                            )
                        )
                    }
                }
            }
            
            // Contenido del plan con estilo Aero
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(GoldenRatio.SPACING_LG),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Beneficios principales con iconos mejorados
                Column {
                    plan.getBeneficiosList().take(3).forEach { beneficio ->
                        Row(
                            modifier = Modifier.padding(vertical = GoldenRatio.SPACING_XS),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = categoryColor.copy(alpha = 0.15f),
                                shape = androidx.compose.foundation.shape.CircleShape,
                                modifier = Modifier.size(GoldenRatio.SPACING_LG)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = categoryColor,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(GoldenRatio.SPACING_XS / 2)
                                )
                            }
                            Spacer(modifier = Modifier.width(GoldenRatio.SPACING_SM))
                            Text(
                                text = beneficio,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = GoldenRatio.PLAN_BENEFIT_SIZE * 1.1f,
                                    lineHeight = GoldenRatio.PLAN_BENEFIT_SIZE * GoldenRatio.PHI * 1.2f,
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                
                // Botón contratar con estilo Aero glassmorphism
                Surface(
                    onClick = { /* Se maneja en Card onClick */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(GoldenRatio.PLAN_BUTTON_HEIGHT * 1.2f),
                    color = categoryColor.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(GoldenRatio.PLAN_BUTTON_RADIUS * 2),
                    shadowElevation = GoldenRatio.ELEVATION_MD
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        categoryColor.copy(alpha = 0.8f),
                                        categoryColor.copy(alpha = 1f),
                                        categoryColor.copy(alpha = 0.8f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(GoldenRatio.SPACING_LG)
                            )
                            Spacer(modifier = Modifier.width(GoldenRatio.SPACING_SM))
                            Text(
                                text = "Contratar Plan",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = GoldenRatio.PLAN_SPEED_SIZE * 1.1f,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GoldenRatioPlanCard(
    plan: Plan, 
    categoryColor: Color
) {
    val context = LocalContext.current
    var isHovered by remember { mutableStateOf(false) }
    
    val elevation by PlanAnimations.rememberPlanCardElevation(isHovered = isHovered)
    
    Card(
        onClick = {
            val message = "Hola, estoy interesado en el plan ${plan.nombre} de ${plan.precio}. " +
                    "¿Podrían darme más información y disponibilidad? Gracias."
            val whatsappIntent = Intent(
                Intent.ACTION_VIEW, 
                Uri.parse("https://wa.me/+573053040593?text=${Uri.encode(message)}")
            )
            context.startActivity(whatsappIntent)
        },
        modifier = Modifier
            .width(GoldenRatio.PLAN_CARD_WIDTH)
            .height(GoldenRatio.PLAN_CARD_HEIGHT)
            .clickable {
                isHovered = !isHovered
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        shape = RoundedCornerShape(GoldenRatio.SPACING_LG)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header del plan con proporción áurea
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(GoldenRatio.PLAN_HEADER_HEIGHT)
                    .background(
                        Brush.verticalGradient(
                            listOf(categoryColor, categoryColor.copy(alpha = 0.8f))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(GoldenRatio.SPACING_LG),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Nombre del plan
                    Text(
                        text = plan.nombre,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = GoldenRatio.PLAN_TITLE_SIZE,
                            letterSpacing = 0.5.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Precio destacado
                    Text(
                        text = plan.precio,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = GoldenRatio.PLAN_PRICE_SIZE
                        ),
                        maxLines = 1
                    )
                    
                    // Velocidad
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(GoldenRatio.PLAN_BUTTON_RADIUS),
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Text(
                            text = plan.velocidad,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = GoldenRatio.PLAN_SPEED_SIZE
                            ),
                            modifier = Modifier.padding(
                                horizontal = GoldenRatio.SPACING_MD,
                                vertical = GoldenRatio.SPACING_XS
                            )
                        )
                    }
                }
            }
            
            // Contenido del plan
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(GoldenRatio.SPACING_LG),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Beneficios principales (máximo 3 para mantener proporción)
                Column {
                    plan.getBeneficiosList().take(3).forEach { beneficio ->
                        Row(
                            modifier = Modifier.padding(vertical = GoldenRatio.SPACING_XS / 2),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(GoldenRatio.SPACING_MD)
                            )
                            Spacer(modifier = Modifier.width(GoldenRatio.SPACING_XS))
                            Text(
                                text = beneficio,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = GoldenRatio.PLAN_BENEFIT_SIZE,
                                    lineHeight = GoldenRatio.PLAN_BENEFIT_SIZE * GoldenRatio.PHI
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                
                // Botón contratar con proporción áurea
                Button(
                    onClick = { /* Se maneja en Card onClick */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(GoldenRatio.PLAN_BUTTON_HEIGHT),
                    colors = ButtonDefaults.buttonColors(containerColor = categoryColor),
                    shape = RoundedCornerShape(GoldenRatio.PLAN_BUTTON_RADIUS),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = GoldenRatio.ELEVATION_SM,
                        pressedElevation = GoldenRatio.ELEVATION_LG
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(GoldenRatio.SPACING_MD)
                        )
                        Spacer(modifier = Modifier.width(GoldenRatio.SPACING_XS))
                        Text(
                            text = "Contratar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = GoldenRatio.PLAN_SPEED_SIZE,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

// Función para crear planes reales de Linage ISP
private fun createSamplePlans(): List<Plan> {
    return listOf(
        // PLANES WIN+ (Planes para verdaderos fanáticos del fútbol)
        Plan("win1", "Plan Silver con Win+", "400 Megas Fibra Óptica", "$113.000", "DIRECTV GO Flex (20 canales), 2 Pantallas Simultaneas, 1.000 series y películas contenido on demand, 3 meses de PARAMOUNT gratis, Canal exclusivo Win+ Futbol, 1 Pantalla en simultaneo, Instalación Gratis", "Fibra"),
        Plan("win2", "Plan Platinum con Win+", "400 Megas Fibra Óptica", "$141.500", "DIRECTV GO Basico (40 canales), 2 Pantallas Simultaneas, 6.000 series y películas contenido on demand, 3 meses de PARAMOUNT gratis, Canal exclusivo Win+ Futbol, 1 Pantalla en simultaneo, Instalación Gratis", "Fibra"),
        Plan("win3", "Plan Gold con Win+", "400 Megas Fibra Óptica", "$163.000", "DIRECTV GO FULL (80 canales), 4 Pantallas Simultaneas, 10.000 series y películas contenido on demand, 3 meses de PARAMOUNT gratis, Canal exclusivo Win+ Futbol, 1 Pantalla en simultaneo, Instalación Gratis", "Fibra"),
        
        // PLANES PREMIUM (Internet ultra rápido + DIRECTV GO)
        Plan("premium1", "Plan Silver", "400 Megas Fibra Óptica", "$90.000", "DIRECTV GO Flex (20 canales), 2 Pantalla Simultanea, 1.000 series y películas contenido on demand, 3 meses de PARAMOUNT gratis, Instalación Gratis", "Fibra"),
        Plan("premium2", "Plan Platinum", "400 Megas Fibra Óptica", "$118.500", "DIRECTV GO Basico (40 canales), 2 Pantalla Simultanea, 6.000 series y películas contenido on demand, 3 meses de PARAMOUNT gratis, Instalación Gratis", "Fibra"),
        Plan("premium3", "Plan Gold", "400 Megas Fibra Óptica", "$140.000", "DIRECTV GO FULL (80 canales), 4 Pantalla Simultanea, 10.000 series y películas contenido on demand, 3 meses de PARAMOUNT gratis, Instalación Gratis", "Fibra"),
        
        // PLANES VIP (Internet ultra rápido + Paramount)
        Plan("vip1", "200 Megas", "200 Megas Fibra Óptica", "$65.000", "1 Mes de Paramount Gratis, 1 Pantalla Simultanea, Instalación Gratis", "Fibra"),
        Plan("vip2", "400 Megas", "400 Megas Fibra Óptica", "$70.000", "1 Mes de Paramount Gratis, 1 Pantalla Simultanea, Instalación Gratis", "Fibra"),
        Plan("vip3", "600 Megas", "600 Megas Fibra Óptica", "$85.000", "1 Mes de Paramount Gratis, 1 Pantalla Simultanea, Instalación Gratis", "Fibra"),
        Plan("vip4", "900 Megas", "900 Megas Fibra Óptica", "$100.000", "1 Mes de Paramount Gratis, 1 Pantalla Simultanea, Instalación Gratis", "Fibra"),
        
        // PLANES VIP VIVE LA EXPERIENCIA MAX (Internet ultra rápido + TV digital + Paramount)
        Plan("viptv1", "200 Megas", "200 Megas Fibra Óptica", "$80.000", "1 Mes de Paramount Gratis, 1 Pantalla Simultanea, Tv digital de 120 canales, Instalación Gratis", "Fibra"),
        Plan("viptv2", "400 Megas", "400 Megas Fibra Óptica", "$85.000", "1 Mes de Paramount Gratis, 1 Pantalla Simultanea, Tv digital de 120 canales, Instalación Gratis", "Fibra"),
        Plan("viptv3", "600 Megas", "600 Megas Fibra Óptica", "$100.000", "1 Mes de Paramount Gratis, 1 Pantalla Simultanea, Tv digital de 120 canales, Instalación Gratis", "Fibra"),
        Plan("viptv4", "900 Megas", "900 Megas Fibra Óptica", "$115.000", "1 Mes de Paramount Gratis, 1 Pantalla Simultanea, Tv digital de 120 canales, Instalación Gratis", "Fibra"),
        
        // PLANES CÁMARAS (Internet ultra rápido + Cámara de Seguridad)
        Plan("cam1", "Camaras 400 MEGAS", "400 Megas Fibra Óptica", "$90.000", "1 meses de PARAMOUNT gratis, 1 Pantallas Simultaneas, 1 Camara de seguridad, Instalación Gratis", "Fibra"),
        Plan("cam2", "Camaras 600 MEGAS", "600 Megas Fibra Óptica", "$95.000", "1 meses de PARAMOUNT gratis, 1 Pantallas Simultaneas, 1 Camara de seguridad, Instalación Gratis", "Fibra"),
        Plan("cam3", "Camaras 900 MEGAS", "900 Megas Fibra Óptica", "$105.000", "1 meses de PARAMOUNT gratis, 1 Pantallas Simultaneas, 1 Camara de seguridad, Instalación Gratis", "Fibra"),
        
        // PLANES NETFLIX (Internet ultra rápido + Netflix)
        Plan("netflix1", "NETFLIX 400 MEGAS", "400 Megas Fibra Óptica", "$85.000", "NETFLIX (1 Dispositivo HD), Instalación Gratis", "Fibra"),
        Plan("netflix2", "NETFLIX 600 MEGAS", "600 Megas Fibra Óptica", "$100.000", "NETFLIX (1 Dispositivo HD), Instalación Gratis", "Fibra"),
        Plan("netflix3", "NETFLIX 900 MEGAS", "900 Megas Fibra Óptica", "$115.000", "NETFLIX (1 Dispositivo HD), Instalación Gratis", "Fibra")
    )
}