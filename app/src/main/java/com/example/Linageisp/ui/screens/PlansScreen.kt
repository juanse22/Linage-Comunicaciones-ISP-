package com.example.Linageisp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Linageisp.R
import com.example.Linageisp.data.Plan
import com.example.Linageisp.data.PlanCategory
import com.example.Linageisp.data.CategorizedPlan
import com.example.Linageisp.data.getAllPlanCategories
import com.example.Linageisp.ui.theme.LinageOrange
import com.example.Linageisp.ui.theme.LinageGray
import com.example.Linageisp.ui.theme.LinageWhite
import com.example.Linageisp.viewmodel.PlanViewModel
import com.example.Linageisp.FirebaseManager
import com.example.Linageisp.PerformanceIntegration
import com.example.Linageisp.TraceScreenLoad
import android.util.Log

/**
 * Pantalla principal que muestra la lista de planes de internet
 * Incluye funcionalidades de carga, manejo de errores y navegación
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCategory: (String) -> Unit
) {
    val context = LocalContext.current
    val performanceIntegration = remember { PerformanceIntegration.getInstance(context) }
    
    // Firebase Analytics - track screen view
    LaunchedEffect(Unit) {
        FirebaseManager.logScreenView("PlansScreen", "PlansScreen")
        Log.d("PlansScreen", "🚀 PlansScreen - CATEGORÍAS MODE")
    }
    
    // DEBUG: Verificar que las categorías se cargan
    LaunchedEffect(Unit) {
        val categories = getAllPlanCategories()
        Log.d("PlansScreen", "📊 Categorías para navegación: ${categories.size}")
        categories.forEach { category ->
            Log.d("PlansScreen", "📂 ${category.emoji} ${category.title} - ${category.plans.size} planes")
        }
    }
    
    TraceScreenLoad(screenName = "PlansScreen") {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Barra superior con navegación
            TopAppBar(
                title = {
                    Text(
                        text = "Planes de Internet",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver al inicio"
                        )
                    }
                },
                actions = {
                    // Sin acciones adicionales en modo categorías
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LinageOrange,
                    titleContentColor = LinageWhite,
                    navigationIconContentColor = LinageWhite,
                    actionIconContentColor = LinageWhite
                )
            )
            
            // CATEGORÍAS NAVEGABLES - Solo mostrar las 6 categorías principales
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Header descriptivo
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = LinageOrange.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🚀 Encuentra tu Plan Perfecto",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LinageOrange
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Selecciona la categoría que mejor se adapte a tus necesidades",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                
                items(
                    items = getAllPlanCategories(),
                    key = { category -> category.id }
                ) { category ->
                    NavigableCategoryCard(
                        category = category,
                        onCategoryClick = {
                            Log.d("PlansScreen", "🔄 Navegando a categoría: ${category.title}")
                            onNavigateToCategory(category.id)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Componente que muestra el estado de carga
 */
@Composable
private fun BoxScope.LoadingState() {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = LinageOrange,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Cargando planes disponibles...",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Componente que muestra el estado de error
 */
@Composable
private fun BoxScope.ErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = LinageOrange
            )
        ) {
            Text("Reintentar")
        }
    }
}

/**
 * Componente que muestra el estado vacío
 */
@Composable
private fun BoxScope.EmptyState(
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No hay planes disponibles",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRefresh,
            colors = ButtonDefaults.buttonColors(
                containerColor = LinageOrange
            )
        ) {
            Text("Actualizar")
        }
    }
}

/**
 * TEST ULTRA SIMPLIFICADO - Verificar que las categorías aparecen
 */
@Composable
private fun SimpleCategoriesTest(onPlanSelected: (Plan) -> Unit) {
    val categories = getAllPlanCategories()
    
    Log.d("SimpleCategoriesTest", "🚨🚨🚨 INICIANDO TEST ULTRA SIMPLIFICADO")
    Log.d("SimpleCategoriesTest", "🚨 Categorías disponibles: ${categories.size}")
    
    if (categories.isEmpty()) {
        Log.e("SimpleCategoriesTest", "❌ CRITICAL ERROR: getAllPlanCategories() retornó LISTA VACÍA")
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🚨 CRITICAL ERROR",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "getAllPlanCategories() retornó lista vacía",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Verificar PlanCategory.kt",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }
    
    // TEST: Mostrar lista super simple sin LazyColumn
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🚨 TEST: ${categories.size} CATEGORÍAS ENCONTRADAS",
            style = MaterialTheme.typography.headlineSmall,
            color = LinageOrange,
            modifier = Modifier.padding(16.dp)
        )
        
        categories.forEachIndexed { index, category ->
            Log.d("SimpleCategoriesTest", "🚨 Renderizando categoría [$index]: ${category.title}")
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = category.color.copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${category.emoji} ${category.title}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = category.color
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📋 ${category.plans.size} planes disponibles",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = category.color,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * 🎯 Card navegable para cada categoría de planes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigableCategoryCard(
    category: PlanCategory,
    onCategoryClick: () -> Unit
) {
    // Calcular rango de precios
    val prices = category.plans.map { it.precioNumerico }.sorted()
    val minPrice = prices.minOrNull() ?: 0
    val maxPrice = prices.maxOrNull() ?: 0
    
    Card(
        onClick = {
            // Log category selection
            Log.d("PlansScreen", "🎯 Categoría seleccionada: ${category.title} (${category.plans.size} planes)")
            onCategoryClick()
        },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = category.color.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, category.color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header con emoji y título
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Emoji grande con background circular
                    Card(
                        modifier = Modifier.size(60.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = category.color.copy(alpha = 0.15f)
                        ),
                        shape = CircleShape
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category.emoji,
                                style = MaterialTheme.typography.headlineLarge,
                                fontSize = 28.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = category.color
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${category.plans.size} planes disponibles",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
                
                // Arrow indicator
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Ver planes",
                    tint = category.color,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Descripción atractiva
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 24.sp
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Rango de precios y features
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rango de precios
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = category.color.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Desde",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                        Text(
                            text = "$${minPrice / 1000}k",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = category.color
                            )
                        )
                    }
                }
                
                // Features destacadas (primeros 2 beneficios del primer plan)
                Column {
                    category.plans.firstOrNull()?.beneficios?.take(2)?.forEach { benefit ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = category.color,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = benefit,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                ),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

/**
 * Lista de planes categorizados
 */
@Composable
private fun CategorizedPlansList(
    expandedCategories: Set<String>,
    onCategoryToggle: (String) -> Unit,
    onPlanSelected: (Plan) -> Unit
) {
    val categories = getAllPlanCategories()
    
    // DEBUG CRÍTICO: Verificar datos
    Log.d("PlansDebug", "🚨 CategorizedPlansList INICIANDO")
    Log.d("PlansDebug", "🚨 Categorías obtenidas: ${categories.size}")
    
    // DEBUG: Log que este componente se está ejecutando
    LaunchedEffect(categories) {
        Log.d("PlansDebug", "📱 CategorizedPlansList renderizado - ${categories.size} categorías")
        if (categories.isEmpty()) {
            Log.e("PlansDebug", "❌ ERROR: No hay categorías - getAllPlanCategories() retornó lista vacía")
        } else {
            categories.forEachIndexed { index, category ->
                Log.d("PlansDebug", "📋 [$index] ${category.emoji} ${category.title} - ${category.plans.size} planes")
            }
        }
    }
    
    // TEST SIMPLE: Mostrar texto si no hay categorías
    if (categories.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "❌ ERROR: No se pudieron cargar las categorías",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "getAllPlanCategories() retornó lista vacía",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }
    
    Log.d("PlansDebug", "🚨 Creando LazyColumn con ${categories.size} items")
    
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = categories,
            key = { category -> category.id },
            contentType = { "plan_category" }
        ) { category ->
            Log.d("PlansDebug", "🚨 Renderizando CategorySection para: ${category.title}")
            CategorySection(
                category = category,
                isExpanded = expandedCategories.contains(category.id),
                onToggle = { onCategoryToggle(category.id) },
                onPlanSelected = onPlanSelected
            )
        }
    }
}

/**
 * Sección de categoría con planes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySection(
    category: PlanCategory,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onPlanSelected: (Plan) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Header de categoría
            Card(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = category.color.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isExpanded) 4.dp else 16.dp,
                    bottomEnd = if (isExpanded) 4.dp else 16.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.emoji,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        
                        Column {
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = category.color
                                )
                            )
                            Text(
                                text = category.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                    
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Contraer" else "Expandir",
                        tint = category.color
                    )
                }
            }
            
            // Lista de planes expandible
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(),
                exit = shrinkVertically(
                    animationSpec = tween(300)
                ) + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    category.plans.forEach { categorizedPlan ->
                        CategorizedPlanCard(
                            plan = categorizedPlan,
                            categoryColor = category.color,
                            onPlanClick = {
                                // Convertir CategorizedPlan a Plan para mantener compatibilidad
                                val plan = Plan(
                                    id = categorizedPlan.id,
                                    nombre = categorizedPlan.nombre,
                                    velocidad = categorizedPlan.velocidad,
                                    precio = categorizedPlan.precio,
                                    beneficios = categorizedPlan.beneficios.joinToString(", "),
                                    type = categorizedPlan.tipo
                                )
                                onPlanSelected(plan)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de plan categorizado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorizedPlanCard(
    plan: CategorizedPlan,
    categoryColor: androidx.compose.ui.graphics.Color,
    onPlanClick: () -> Unit
) {
    Card(
        onClick = { 
            // Track plan view event
            FirebaseManager.logPlanViewEvent(plan.nombre, plan.precio, plan.velocidad)
            onPlanClick()
        },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (plan.destacado) {
                categoryColor.copy(alpha = 0.05f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (plan.destacado) 6.dp else 2.dp
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (plan.destacado) {
            BorderStroke(2.dp, categoryColor.copy(alpha = 0.3f))
        } else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Badge "Destacado" si aplica
            if (plan.destacado) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = categoryColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "⭐ DESTACADO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LinageWhite
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Encabezado con nombre y precio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    ),
                    modifier = Modifier.weight(1f)
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = categoryColor.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = plan.precio,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = categoryColor
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Velocidad y tipo
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_wifi),
                    contentDescription = "Velocidad de Internet",
                    colorFilter = ColorFilter.tint(categoryColor),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = plan.velocidad,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                
                if (plan.tipo.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${plan.tipo}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = LinageGray
                        )
                    )
                }
            }
            
            // Descripción corta si existe
            if (plan.descripcionCorta.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = plan.descripcionCorta,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                )
            }
            
            // Beneficios
            if (plan.beneficios.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                
                plan.beneficios.take(3).forEach { beneficio ->
                    Text(
                        text = "✓ $beneficio",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
                
                if (plan.beneficios.size > 3) {
                    Text(
                        text = "... y ${plan.beneficios.size - 3} beneficios más",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = categoryColor,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botón Contratar
            Button(
                onClick = onPlanClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = categoryColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Contratar Plan",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = LinageWhite,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

/**
 * Tarjeta de plan original (mantenida para compatibilidad)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanCard(
    plan: Plan,
    onPlanClick: () -> Unit
) {
    Card(
        onClick = { 
            // Track plan view event
            FirebaseManager.logPlanViewEvent(plan.nombre, plan.precio, plan.velocidad)
            onPlanClick()
        },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Encabezado con nombre y precio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nombre del plan
                Text(
                    text = plan.nombre,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = LinageOrange
                    ),
                    modifier = Modifier.weight(1f)
                )
                
                // Precio del plan
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = LinageOrange.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = plan.precio,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LinageOrange
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tipo de conexión
            if (plan.type.isNotEmpty()) {
                Text(
                    text = plan.type,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LinageGray
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            
            // Velocidad
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reemplazado: Usar imagen personalizada de WiFi desde drawable  
                Image(
                    painter = painterResource(id = R.drawable.ic_wifi),
                    contentDescription = "Velocidad de Internet",
                    colorFilter = ColorFilter.tint(LinageOrange), // Aplicar tinte del color corporativo
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(2.dp)) // Esquinas ligeramente redondeadas
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = plan.velocidad,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            
            // Beneficios (todos)
            if (plan.beneficios.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Mostrar todos los beneficios
                val beneficiosList = plan.getBeneficiosList()
                
                beneficiosList.forEach { beneficio ->
                    if (beneficio.isNotEmpty()) {
                        Text(
                            text = "✓ $beneficio",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón Contratar
            Button(
                onClick = { /* TODO: Handle contract action */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LinageOrange
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Contratar",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = LinageWhite,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}