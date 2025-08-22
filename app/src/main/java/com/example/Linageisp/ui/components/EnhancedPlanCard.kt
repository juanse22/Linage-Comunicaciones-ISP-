package com.example.Linageisp.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.Linageisp.data.Plan
import com.example.Linageisp.ui.theme.*

/**
 * Tarjeta de plan mejorada con mejor UX y accesibilidad
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedPlanCard(plan: Plan) {
    var isExpanded by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Animaciones mejoradas
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "cardScale"
    )

    val cardElevation by animateDpAsState(
        targetValue = if (isExpanded) 16.dp else 8.dp,
        animationSpec = tween(400, easing = EaseOutCubic),
        label = "cardElevation"
    )

    // Simple card design for basic Plan model
    Card(
        onClick = { isExpanded = !isExpanded },
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(
                scaleX = cardScale,
                scaleY = cardScale
            ),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Plan name
            Text(
                text = plan.nombre,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Speed and price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = plan.velocidad,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = plan.precio,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Benefits
            Text(
                text = plan.beneficios,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Contract button
            Button(
                onClick = {
                    isPressed = true
                    val message = "¡Hola! 👋 Estoy interesado en el plan ${plan.nombre} " +
                            "por ${plan.precio}. ¿Podrían darme más información? Gracias 😊"
                    val whatsappIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://wa.me/?text=${Uri.encode(message)}")
                    )
                    context.startActivity(whatsappIntent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Contratar Plan")
            }
        }
    }

    // Reset presión
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(200)
            isPressed = false
        }
    }
}
