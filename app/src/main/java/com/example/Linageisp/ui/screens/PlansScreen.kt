package com.example.Linageisp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.Linageisp.data.CategoryInfo
import com.example.Linageisp.data.getAllCategories
import com.example.Linageisp.data.getPlansByCategory
import com.example.Linageisp.ui.theme.LinageOrange
import com.example.Linageisp.ui.theme.LinageWhite
import com.example.Linageisp.FirebaseManager
import com.example.Linageisp.PerformanceIntegration
import com.example.Linageisp.TraceScreenLoad
import android.util.Log

/**
 * Pantalla principal que muestra las categorías de planes de internet
 * Navegación: Home → PlansScreen → CategoryPlansScreen
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
        val categories = getAllCategories()
        Log.d("PlansScreen", "📊 Categorías para navegación: ${categories.size}")
        categories.forEach { category ->
            val categoryPlans = getPlansByCategory(category.id)
            Log.d("PlansScreen", "📂 ${category.emoji} ${category.title} - ${categoryPlans.size} planes")
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LinageOrange,
                    titleContentColor = LinageWhite,
                    navigationIconContentColor = LinageWhite,
                    actionIconContentColor = LinageWhite
                )
            )
            
            // CATEGORÍAS NAVEGABLES
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
                    items = getAllCategories(),
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
 * 🎯 Card navegable para cada categoría de planes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigableCategoryCard(
    category: CategoryInfo,
    onCategoryClick: () -> Unit
) {
    // Calcular rango de precios de los planes de la categoría
    val categoryPlans = getPlansByCategory(category.id)
    val prices = categoryPlans.map { it.precioNumerico }.sorted()
    val minPrice = prices.minOrNull() ?: 0
    
    Card(
        onClick = {
            // Log category selection
            Log.d("PlansScreen", "🎯 Categoría seleccionada: ${category.title} (${categoryPlans.size} planes)")
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
                            text = "${categoryPlans.size} planes disponibles",
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
                
                // Features destacadas
                if (categoryPlans.isNotEmpty()) {
                    Column {
                        val firstPlanBenefits = categoryPlans.first().getBeneficiosList()
                        firstPlanBenefits.take(2).forEach { benefit ->
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
}