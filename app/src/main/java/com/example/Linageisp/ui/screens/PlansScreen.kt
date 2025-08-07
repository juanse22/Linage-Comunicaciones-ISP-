package com.example.Linageisp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Linageisp.R
import com.example.Linageisp.data.Plan
import com.example.Linageisp.ui.theme.LinageOrange
import com.example.Linageisp.ui.theme.LinageGray
import com.example.Linageisp.ui.theme.LinageWhite
import com.example.Linageisp.viewmodel.PlanViewModel

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
    val uiState by viewModel.uiState.collectAsState()
    
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
            when {
                uiState.isLoading -> {
                    LoadingState()
                }
                
                uiState.errorMessage != null -> {
                    ErrorState(
                        errorMessage = uiState.errorMessage!!,
                        onRetry = { viewModel.retryLoading() }
                    )
                }
                
                uiState.plans.isNotEmpty() -> {
                    PlansList(
                        plans = uiState.plans,
                        onPlanSelected = onPlanSelected
                    )
                }
                
                else -> {
                    EmptyState(
                        onRefresh = { viewModel.retryLoading() }
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
 * Lista de planes disponibles
 */
@Composable
private fun PlansList(
    plans: List<Plan>,
    onPlanSelected: (Plan) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(plans) { plan ->
            PlanCard(
                plan = plan,
                onPlanClick = { onPlanSelected(plan) }
            )
        }
    }
}

/**
 * Tarjeta de plan actualizada para el nuevo modelo de datos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanCard(
    plan: Plan,
    onPlanClick: () -> Unit
) {
    Card(
        onClick = onPlanClick,
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