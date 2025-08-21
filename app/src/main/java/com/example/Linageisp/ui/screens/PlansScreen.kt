package com.example.Linageisp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
    onPlanSelected: (Plan) -> Unit,
    viewModel: PlanViewModel = viewModel()
) {
    // State for category expansion
    var expandedCategories by remember { mutableStateOf(setOf<String>()) }
    val context = LocalContext.current
    val performanceIntegration = remember { PerformanceIntegration.getInstance(context) }
    val uiState by viewModel.uiState.collectAsState()
    
    // Firebase Analytics - track screen view
    LaunchedEffect(Unit) {
        FirebaseManager.logScreenView("PlansScreen", "PlansScreen")
        Log.d("PlansScreen", "🚀 PlansScreen iniciado - mostrando categorías")
    }
    
    // DEBUG: Verificar que las categorías se cargan
    LaunchedEffect(Unit) {
        val categories = getAllPlanCategories()
        Log.d("PlansScreen", "📊 Categorías cargadas: ${categories.size}")
        categories.forEach { category ->
            Log.d("PlansScreen", "📂 ${category.emoji} ${category.title} - ${category.plans.size} planes")
        }
    }
    
    // Track plan scraping performance
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            val startTime = System.currentTimeMillis()
            val trace = performanceIntegration.tracePlanScraping()
            
            // Wait for loading to complete
            while (uiState.isLoading) {
                kotlinx.coroutines.delay(100)
            }
            
            val scrapingTime = System.currentTimeMillis() - startTime
            performanceIntegration.completePlanScraping(
                trace, 
                uiState.plans.size, 
                scrapingTime, 
                uiState.errorMessage == null
            )
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
                    IconButton(onClick = { viewModel.retryLoading() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar planes"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LinageOrange,
                    titleContentColor = LinageWhite,
                    navigationIconContentColor = LinageWhite,
                    actionIconContentColor = LinageWhite
                )
            )
            
            // Contenido principal con estados
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // CRÍTICO: Siempre mostrar categorías independientemente del ViewModel
                CategorizedPlansList(
                    expandedCategories = expandedCategories,
                    onCategoryToggle = { categoryId ->
                        Log.d("PlansScreen", "🔄 Toggle categoría: $categoryId")
                        expandedCategories = if (expandedCategories.contains(categoryId)) {
                            expandedCategories - categoryId
                        } else {
                            expandedCategories + categoryId
                        }
                    },
                    onPlanSelected = onPlanSelected
                )
                
                // Overlay de estado de carga si está cargando algo adicional
                if (uiState.isLoading) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = LinageOrange.copy(alpha = 0.9f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = LinageWhite,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Actualizando...",
                                style = MaterialTheme.typography.labelSmall,
                                color = LinageWhite
                            )
                        }
                    }
                }
                
                // Overlay de error si hay error
                uiState.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️ $error",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { viewModel.retryLoading() }
                            ) {
                                Text(
                                    text = "Reintentar",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
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
 * Lista de planes categorizados
 */
@Composable
private fun CategorizedPlansList(
    expandedCategories: Set<String>,
    onCategoryToggle: (String) -> Unit,
    onPlanSelected: (Plan) -> Unit
) {
    val categories = getAllPlanCategories()
    
    // DEBUG: Log que este componente se está ejecutando
    LaunchedEffect(categories) {
        Log.d("PlansScreen", "📱 CategorizedPlansList renderizado - ${categories.size} categorías")
        categories.forEachIndexed { index, category ->
            Log.d("PlansScreen", "📋 [$index] ${category.emoji} ${category.title}")
        }
    }
    
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = categories,
            key = { category -> category.id },
            contentType = { "plan_category" }
        ) { category ->
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