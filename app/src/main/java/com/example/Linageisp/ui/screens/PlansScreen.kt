package com.example.Linageisp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.Linageisp.data.Plan
import com.example.Linageisp.ui.theme.*
import com.example.Linageisp.FirebaseManager
import com.example.Linageisp.TraceScreenLoad
import com.example.Linageisp.utils.WhatsAppUtils
import android.util.Log

/**
 * Pantalla principal que muestra las categorías de planes expandibles
 * Implementación exacta según requerimientos: solo una categoría expandida a la vez
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCategory: (String) -> Unit = {} // No se usa, mantenemos para compatibilidad
) {
    val context = LocalContext.current
    
    // Estado para controlar qué categoría está expandida
    var expandedCategoryId by remember { mutableStateOf<String?>(null) }
    
    // Estado de scroll estable - CLAVE para evitar auto-scroll
    val listState = rememberLazyListState()
    
    // Firebase Analytics
    LaunchedEffect(Unit) {
        FirebaseManager.logScreenView("PlansScreen", "ExpandableCategories")
        Log.d("PlansScreen", "🚀 PlansScreen - CATEGORÍAS EXPANDIBLES MODE")
    }
    
    TraceScreenLoad(screenName = "PlansScreen") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepBlack) // Fondo negro profundo
        ) {
            // Top App Bar con diseño fruity aero negro
            TopAppBar(
                title = {
                    Text(
                        text = "Planes Premium ISP",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PureWhite
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver al inicio",
                            tint = PureWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBlack,
                    titleContentColor = PureWhite,
                    navigationIconContentColor = PureWhite
                )
            )
            
            // Lista de categorías expandibles
            LazyColumn(
                state = listState, // PRESERVAR POSICIÓN DE SCROLL
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    // Header principal
                    PlansHeader()
                }
                
                // Las 6 categorías exactas según documento
                items(
                    items = getCategoriesData(),
                    key = { category -> category.id }
                ) { category ->
                    ExpandableCategoryCard(
                        category = category,
                        isExpanded = expandedCategoryId == category.id,
                        onToggleExpanded = { 
                            expandedCategoryId = if (expandedCategoryId == category.id) {
                                null // Colapsar si ya está expandida
                            } else {
                                category.id // Expandir esta categoría (automáticamente colapsa otras)
                            }
                            Log.d("PlansScreen", "📊 Estado expansion: ${category.title} -> ${expandedCategoryId == category.id}")
                        }
                    )
                }
                
                item {
                    // Footer con consulta general
                    GeneralConsultationFooter()
                }
            }
        }
    }
}

/**
 * Data class para categorías con planes embebidos
 */
data class CategoryData(
    val id: String,
    val title: String,
    val emoji: String,
    val color: Color,
    val planCount: Int,
    val plans: List<PlanData>
)

data class PlanData(
    val name: String,
    val price: String,
    val speed: String,
    val description: String,
    val isHighlighted: Boolean = false
)

/**
 * Función que retorna las 6 categorías exactas con datos del documento
 */
fun getCategoriesData(): List<CategoryData> {
    return listOf(
        // 🏆 Planes para verdaderos fanáticos del fútbol
        CategoryData(
            id = "win_futbol",
            title = "Planes para verdaderos fanáticos del fútbol",
            emoji = "⚽",
            color = NeonGreen, // Verde neón brillante
            planCount = 3,
            plans = listOf(
                PlanData("Silver con Win+", "$113.000", "400 Megas", "400 Megas + DIRECTV GO Flex + Win+ Fútbol"),
                PlanData("Platinum con Win+", "$141.500", "400 Megas", "400 Megas + DIRECTV GO Básico + Win+ Fútbol", true),
                PlanData("Gold con Win+", "$163.000", "400 Megas", "400 Megas + DIRECTV GO FULL + Win+ Fútbol")
            )
        ),
        
        // 📺 Planes Premium  
        CategoryData(
            id = "premium_directv",
            title = "Planes Premium",
            emoji = "📺",
            color = ElectricBlue, // Azul eléctrico
            planCount = 3,
            plans = listOf(
                PlanData("Silver", "$90.000", "400 Megas", "400 Megas + DIRECTV GO Flex"),
                PlanData("Platinum", "$118.500", "400 Megas", "400 Megas + DIRECTV GO Básico", true),
                PlanData("Gold", "$140.000", "400 Megas", "400 Megas + DIRECTV GO FULL")
            )
        ),
        
        // ⚡ Planes VIP (Internet + Paramount)
        CategoryData(
            id = "vip_paramount", 
            title = "Planes VIP",
            emoji = "🎬",
            color = VibrantOrange, // Naranja vibrante
            planCount = 4,
            plans = listOf(
                PlanData("200 Megas", "$65.000", "200 Megas", "Solo internet + Paramount"),
                PlanData("400 Megas", "$70.000", "400 Megas", "Solo internet + Paramount", true),
                PlanData("600 Megas", "$85.000", "600 Megas", "Solo internet + Paramount"),
                PlanData("900 Megas", "$100.000", "900 Megas", "Solo internet + Paramount")
            )
        ),
        
        // 📺 Planes VIP Max (Internet + TV + Paramount)
        CategoryData(
            id = "vip_max_tv",
            title = "Planes VIP Max", 
            emoji = "📺",
            color = BrightPurple, // Morado brillante
            planCount = 4,
            plans = listOf(
                PlanData("200 Megas", "$80.000", "200 Megas", "Internet + TV 120 canales + Paramount"),
                PlanData("400 Megas", "$85.000", "400 Megas", "Internet + TV 120 canales + Paramount", true),
                PlanData("600 Megas", "$100.000", "600 Megas", "Internet + TV 120 canales + Paramount"),
                PlanData("900 Megas", "$115.000", "900 Megas", "Internet + TV 120 canales + Paramount")
            )
        ),
        
        // 🎬 Planes Netflix
        CategoryData(
            id = "netflix_plans",
            title = "Planes Netflix",
            emoji = "🎥",
            color = IntenseRed, // Rojo intenso
            planCount = 3,
            plans = listOf(
                PlanData("400 Megas", "$85.000", "400 Megas", "Internet + Netflix HD"),
                PlanData("600 Megas", "$100.000", "600 Megas", "Internet + Netflix HD", true),
                PlanData("900 Megas", "$115.000", "900 Megas", "Internet + Netflix HD")
            )
        ),
        
        // 📹 Planes Cámaras
        CategoryData(
            id = "camaras_seguridad",
            title = "Planes Cámaras",
            emoji = "📹",
            color = BrilliantCyan, // Cyan brillante
            planCount = 3,
            plans = listOf(
                PlanData("400 Megas", "$90.000", "400 Megas", "Internet + Cámara seguridad + Paramount"),
                PlanData("600 Megas", "$95.000", "600 Megas", "Internet + Cámara seguridad + Paramount", true),
                PlanData("900 Megas", "$105.000", "900 Megas", "Internet + Cámara seguridad + Paramount")
            )
        )
    )
}

/**
 * Header principal con glassmorphism
 * ELIMINADO: LaunchedEffect que causaba recomposiciones y auto-scroll
 */
@Composable
private fun PlansHeader() {
    // REMOVIDO: Estado de animación que causaba auto-scroll
    // El header ahora es estático para evitar recomposiciones
    
    Box( // Header premium con efectos metálicos
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphismMetallic(
                    borderRadius = 24.dp,
                    backgroundAlpha = 0.18f,
                    borderAlpha = 0.5f
                )
                .metallicShadow(
                    elevation = 12.dp,
                    borderRadius = 24.dp
                )
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🚀",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 48.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Encuentra tu Plan Perfecto",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MetallicCopper,
                        fontSize = 28.sp,
                        shadow = Shadow(
                            color = MetallicGraphite,
                            offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Tecnología Premium ISP • Servicio Empresarial",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MetallicSilver,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }

/**
 * Card de categoría expandible - NÚCLEO DE LA FUNCIONALIDAD
 */
@Composable
private fun ExpandableCategoryCard(
    category: CategoryData,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    val context = LocalContext.current
    
    AnimatedContent(
        targetState = isExpanded,
        transitionSpec = {
            if (targetState) {
                slideInVertically(
                    initialOffsetY = { -it / 4 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(
                    animationSpec = tween(300)
                ) togetherWith slideOutVertically(
                    targetOffsetY = { -it / 4 },
                    animationSpec = tween(200)
                ) + fadeOut(animationSpec = tween(200))
            } else {
                slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(200)
                ) + fadeIn(
                    animationSpec = tween(200)
                ) togetherWith slideOutVertically(
                    targetOffsetY = { it / 4 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        },
        label = "category_expansion"
    ) { expanded ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphismCategoryMetallic(
                    category = category.id,
                    borderRadius = 20.dp,
                    backgroundAlpha = if (expanded) 0.15f else 0.08f
                )
                .metallicShadow(
                    elevation = if (expanded) 16.dp else 8.dp,
                    borderRadius = 20.dp
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Log.d("PlansScreen", if (expanded) "🔼 Colapsando ${category.title}" else "🔽 Expandiendo ${category.title}")
                    onToggleExpanded()
                }
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header de la categoría (siempre visible)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Emoji
                        Box(
                            modifier = Modifier
                                .size(if (expanded) 60.dp else 48.dp)
                                .background(
                                    category.color.copy(alpha = 0.2f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category.emoji,
                                fontSize = if (expanded) 28.sp else 24.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = category.color,
                                    fontSize = if (expanded) 20.sp else 18.sp
                                )
                            )
                            Text(
                                text = "${category.planCount} planes disponibles",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                    
                    // Icono de expansión
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Colapsar" else "Expandir",
                        tint = category.color,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Planes (solo visible cuando expandido)
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(
                        animationSpec = tween(400, delayMillis = 100)
                    ),
                    exit = shrinkVertically(
                        animationSpec = tween(300)
                    ) + fadeOut(
                        animationSpec = tween(200)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        HorizontalDivider(
                            color = category.color.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        category.plans.forEachIndexed { index, plan ->
                            PlanItemCard(
                                plan = plan,
                                categoryColor = category.color,
                                onContractPlan = {
                                    Log.d("PlansScreen", "📋 Contratando plan: ${plan.name} - ${plan.price}")
                                    WhatsAppUtils.contractPlan(context, plan.name, plan.price)
                                }
                            )
                            
                            if (index < category.plans.size - 1) {
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card individual de plan con botón WhatsApp
 */
@Composable
private fun PlanItemCard(
    plan: PlanData,
    categoryColor: Color,
    onContractPlan: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphismMetallic(
                borderRadius = 16.dp,
                backgroundAlpha = if (plan.isHighlighted) 0.2f else 0.12f,
                borderAlpha = if (plan.isHighlighted) 0.6f else 0.4f
            )
            .metallicShadow(
                elevation = if (plan.isHighlighted) 12.dp else 6.dp,
                borderRadius = 16.dp
            )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header del plan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = plan.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = categoryColor
                            ),
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        
                        if (plan.isHighlighted) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        categoryColor,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "⭐ POPULAR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = plan.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Precio
                Text(
                    text = plan.price,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = categoryColor,
                        fontSize = 20.sp
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botón Contratar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF25D366),
                                Color(0xFF128C7E)
                            )
                        ),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onContractPlan() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "WhatsApp",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Contratar por WhatsApp",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

/**
 * Footer con consulta general
 */
@Composable
private fun GeneralConsultationFooter() {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphismMetallic(
                borderRadius = 20.dp,
                backgroundAlpha = 0.15f,
                borderAlpha = 0.4f
            )
            .metallicShadow(
                elevation = 10.dp,
                borderRadius = 20.dp
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🤝 ¿Necesitas Asesoría?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = LinageOrange
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Contacta con nuestros expertos",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                LinageOrange,
                                LinageOrange.copy(alpha = 0.8f)
                            )
                        ),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        WhatsAppUtils.openGeneralConsultation(context)
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Support,
                        contentDescription = "Consultar",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Consultar por WhatsApp",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}