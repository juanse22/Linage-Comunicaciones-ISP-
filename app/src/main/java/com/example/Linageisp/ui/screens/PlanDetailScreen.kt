package com.example.Linageisp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.Linageisp.R
import com.example.Linageisp.data.Plan
import com.example.Linageisp.ui.theme.LinageOrange
import com.example.Linageisp.ui.theme.LinageOrangeDark
import com.example.Linageisp.ui.theme.LinageGray
import com.example.Linageisp.ui.theme.LinageWhite
import com.example.Linageisp.ui.theme.SuccessGreen

/**
 * Pantalla de detalles del plan seleccionado
 * Muestra información completa y botón para solicitar por WhatsApp
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    plan: Plan,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Barra superior
        TopAppBar(
            title = {
                Text(
                    text = "Detalles del Plan",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = LinageOrange,
                titleContentColor = LinageWhite,
                navigationIconContentColor = LinageWhite
            )
        )
        
        // Contenido scrolleable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Tarjeta principal del plan
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = LinageOrange.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = plan.nombre,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = LinageOrange
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (plan.type.isNotEmpty()) {
                        Text(
                            text = plan.type,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = LinageGray
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Precio destacado
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = LinageOrange
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = plan.precio,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = LinageWhite
                            ),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }
            }
            
            // Información de velocidad
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Velocidad",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
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
                                .size(32.dp)
                                .clip(RoundedCornerShape(4.dp)) // Esquinas ligeramente redondeadas
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = plan.velocidad,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            
            // Beneficios del plan
            if (plan.beneficios.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Beneficios Incluidos",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Usar la función auxiliar para obtener la lista de beneficios
                        val beneficiosList = plan.getBeneficiosList()
                        beneficiosList.forEach { beneficio ->
                            if (beneficio.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "✓",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    
                                    Text(
                                        text = beneficio,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Información adicional basada en el nombre del plan
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "¿Por qué elegir este plan?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    val recommendations = when {
                        plan.nombre.contains("5") -> listOf(
                            "Perfecto para navegación básica",
                            "Ideal para 1-2 dispositivos",
                            "Correo electrónico y redes sociales"
                        )
                        plan.nombre.contains("10") -> listOf(
                            "Excelente para uso doméstico",
                            "Streaming en calidad estándar",
                            "Hasta 3-4 dispositivos conectados"
                        )
                        plan.nombre.contains("20") -> listOf(
                            "Perfecto para familias",
                            "Streaming HD sin interrupciones",
                            "Trabajo desde casa",
                            "Hasta 6-8 dispositivos"
                        )
                        else -> listOf(
                            "Máximo rendimiento",
                            "Gaming online sin lag",
                            "Streaming 4K",
                            "Múltiples dispositivos sin límites"
                        )
                    }
                    
                    recommendations.forEach { recommendation ->
                        Text(
                            text = "• $recommendation",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Botón de solicitud por WhatsApp
            Button(
                onClick = {
                    // Usar la función auxiliar del modelo Plan para generar el mensaje
                    val message = plan.getWhatsAppMessage()
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
                    containerColor = SuccessGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Solicitar",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Solicitar este Plan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = LinageWhite,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            
            // Nota informativa
            Text(
                text = "Al solicitar este plan, serás redirigido a WhatsApp para contactar directamente con nuestro equipo de ventas.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}