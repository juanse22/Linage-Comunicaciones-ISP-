package com.example.Linageisp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.Linageisp.R
import com.example.Linageisp.data.Plan
import com.example.Linageisp.data.CategoryInfo
import com.example.Linageisp.data.getAllCategories
import com.example.Linageisp.data.getPlansByCategory
import com.example.Linageisp.ui.theme.LinageOrange
import com.example.Linageisp.ui.theme.LinageGray
import com.example.Linageisp.ui.theme.LinageWhite
import com.example.Linageisp.FirebaseManager
import com.example.Linageisp.PerformanceIntegration
import com.example.Linageisp.TraceScreenLoad
import android.util.Log

/**
 * Pantalla que muestra los planes específicos de una categoría seleccionada
 * Navegación: PlansScreen → CategoryPlansScreen → Back
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPlansScreen(
    categoryId: String,
    onNavigateBack: () -> Unit,
    onPlanSelected: (Plan) -> Unit
) {
    val context = LocalContext.current
    val performanceIntegration = remember { PerformanceIntegration.getInstance(context) }
    
    // Buscar la categoría específica por ID
    val category = remember(categoryId) {
        getAllCategories().find { it.id == categoryId }
    }
    
    // Obtener planes de la categoría
    val categoryPlans = remember(categoryId) {
        getPlansByCategory(categoryId)
    }
    
    // DEBUG: Log para tracking
    LaunchedEffect(categoryId) {
        Log.d("CategoryPlansScreen", "🎯 Mostrando categoría: $categoryId")
        category?.let { cat ->
            Log.d("CategoryPlansScreen", "📂 ${cat.emoji} ${cat.title} - ${categoryPlans.size} planes")
            FirebaseManager.logScreenView("CategoryPlansScreen", categoryId)
        }
    }
    
    TraceScreenLoad(screenName = "CategoryPlansScreen_$categoryId") {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar con navegación
            TopAppBar(
                title = {
                    Text(
                        text = category?.title ?: "Planes",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver a categorías"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = category?.color ?: LinageOrange,
                    titleContentColor = LinageWhite,
                    navigationIconContentColor = LinageWhite
                )
            )
            
            if (category == null) {
                // Error state - categoría no encontrada
                CategoryNotFoundState(
                    categoryId = categoryId,
                    onNavigateBack = onNavigateBack
                )
            } else {
                // Contenido principal con header y planes
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Header de la categoría
                        CategoryHeaderCard(category = category)
                    }
                    
                    item {
                        // Información de planes disponibles
                        PlansCountInfo(
                            planCount = categoryPlans.size,
                            categoryColor = category.color
                        )
                    }
                    
                    // Lista de planes de la categoría
                    items(
                        items = categoryPlans,
                        key = { plan -> plan.id }
                    ) { plan ->
                        CategoryPlanCard(
                            plan = plan,
                            categoryColor = category.color,
                            onPlanClick = {
                                // Log plan selection
                                Log.d("CategoryPlansScreen", "📋 Plan seleccionado: ${plan.nombre} - ${plan.precio}")
                                onPlanSelected(plan)
                            }
                        )
                    }
                    
                    item {
                        // Footer con información adicional
                        CategoryFooter(category = category)
                    }
                }
            }
        }
    }
}

/**
 * Header card de la categoría con emoji, título y descripción
 */
@Composable
private fun CategoryHeaderCard(category: CategoryInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = category.color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, category.color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji grande con background circular
            Card(
                modifier = Modifier.size(80.dp),
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
                        fontSize = 36.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            // Título y descripción
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = category.color
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 22.sp
                    )
                )
            }
        }
    }
}

/**
 * Información del número de planes disponibles
 */
@Composable
private fun PlansCountInfo(
    planCount: Int,
    categoryColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = categoryColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "$planCount planes disponibles en esta categoría",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = categoryColor
                )
            )
        }
    }
}

/**
 * Card de plan específico de la categoría
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPlanCard(
    plan: Plan,
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
            defaultElevation = if (plan.destacado) 8.dp else 4.dp
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (plan.destacado) {
            BorderStroke(2.dp, categoryColor.copy(alpha = 0.3f))
        } else null
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Badge "Destacado" si aplica
            if (plan.destacado) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = categoryColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "⭐ PLAN DESTACADO",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = LinageWhite
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
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
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    ),
                    modifier = Modifier.weight(1f)
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = categoryColor.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = plan.precio,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = categoryColor
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
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
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = plan.velocidad,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                
                if (plan.type.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = LinageGray.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = plan.type,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LinageGray,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            // Descripción corta si existe
            if (plan.descripcionCorta.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = plan.descripcionCorta,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                )
            }
            
            // Beneficios
            val beneficiosList = plan.getBeneficiosList()
            if (beneficiosList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Beneficios incluidos:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                beneficiosList.forEach { beneficio ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = categoryColor,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                        )
                        Text(
                            text = beneficio,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Botón Contratar
            Button(
                onClick = onPlanClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = categoryColor
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = "Contratar Este Plan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = LinageWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/**
 * Footer con información adicional de la categoría
 */
@Composable
private fun CategoryFooter(category: CategoryInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💡 ¿Necesitas ayuda para elegir?",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Todos nuestros planes incluyen instalación gratuita y soporte técnico 24/7",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * Estado de error cuando no se encuentra la categoría
 */
@Composable
private fun CategoryNotFoundState(
    categoryId: String,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "😕",
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Categoría no encontrada",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No pudimos encontrar la categoría '$categoryId'",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNavigateBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = LinageOrange
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Volver a Categorías",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = LinageWhite,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}