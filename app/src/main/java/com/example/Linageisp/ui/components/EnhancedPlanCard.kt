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
import com.example.Linageisp.data.PlanModern
import com.example.Linageisp.ui.theme.*

/**
 * Tarjeta de plan mejorada con mejor UX y accesibilidad
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedPlanCard(plan: PlanModern) {
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

    Box {
        // Badges mejorados con mejor posicionamiento
        if (plan.isRecommended) {
            EnhancedBadge(
                text = "Recomendado",
                icon = Icons.Default.Star,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .zIndex(2f)
            )
        }

        if (plan.isPopular) {
            EnhancedBadge(
                text = "Popular",
                icon = Icons.Default.TrendingUp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 8.dp, y = (-8).dp)
                    .zIndex(2f)
            )
        }

        // Tarjeta principal con diseño mejorado
        Card(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(
                    scaleX = cardScale,
                    scaleY = cardScale
                )
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = if (plan.isRecommended) BorderStroke(
                2.dp, 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            ) else null
        ) {
            Column {
                // Header con gradiente mejorado
                EnhancedPlanHeader(
                    plan = plan,
                    isExpanded = isExpanded
                )

                // Contenido principal
                EnhancedPlanContent(
                    plan = plan,
                    isExpanded = isExpanded,
                    onContractClick = {
                        isPressed = true
                        
                        val message = "¡Hola! 👋 Estoy interesado en el plan de ${plan.speed} " +
                                "por ${plan.price} al mes. ¿Podrían darme más información? Gracias 😊"
                        
                        val whatsappIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://wa.me/?text=${Uri.encode(message)}")
                        )
                        context.startActivity(whatsappIntent)
                    }
                )
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

@Composable
private fun EnhancedBadge(
    text: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun EnhancedPlanHeader(
    plan: PlanModern,
    isExpanded: Boolean
) {
    val headerHeight by animateDpAsState(
        targetValue = if (isExpanded) 160.dp else 140.dp,
        animationSpec = tween(400, easing = EaseOutCubic),
        label = "headerHeight"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(plan.color.primary, plan.color.secondary)
                )
            )
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
    ) {
        // Patrón de fondo sutil
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val patternSize = 60f
            for (i in 0 until (size.width / patternSize).toInt()) {
                for (j in 0 until (size.height / patternSize).toInt()) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = 15f,
                        center = androidx.compose.ui.geometry.Offset(
                            i * patternSize + patternSize / 2,
                            j * patternSize + patternSize / 2
                        )
                    )
                }
            }
        }

        // Contenido del header
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información de velocidad
            Column {
                Text(
                    text = plan.speed,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 40.sp
                    )
                )
                Text(
                    text = "de velocidad",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // Precio con mejor diseño
            EnhancedPriceDisplay(plan = plan)
        }
    }
}

@Composable
private fun EnhancedPriceDisplay(plan: PlanModern) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        // Precio anterior si existe
        plan.originalPrice?.let { originalPrice ->
            Text(
                text = originalPrice,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    textDecoration = TextDecoration.LineThrough
                )
            )
        }
        
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = plan.price,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = plan.color.primary
                    )
                )
                Text(
                    text = "por mes",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun EnhancedPlanContent(
    plan: PlanModern,
    isExpanded: Boolean,
    onContractClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        // Título de beneficios
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Beneficios incluidos",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        // Lista de beneficios mejorada
        val visibleBenefits = if (isExpanded) plan.benefits else plan.benefits.take(3)
        
        visibleBenefits.forEachIndexed { index, benefit ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(300, delayMillis = index * 100)) + 
                       slideInVertically(tween(300, delayMillis = index * 100))
            ) {
                EnhancedBenefitItem(benefit = benefit)
            }
        }

        // Botón expandir si hay más beneficios
        if (plan.benefits.size > 3) {
            TextButton(
                onClick = { /* Toggle expansion */ },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (isExpanded) "Ver menos" else "Ver ${plan.benefits.size - 3} beneficios más",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón contratar mejorado
        EnhancedContractButton(
            onClick = onContractClick,
            color = plan.color.primary
        )

        // Información adicional
        Spacer(modifier = Modifier.height(16.dp))
        EnhancedInfoChips()
    }
}

@Composable
private fun EnhancedBenefitItem(benefit: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(24.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = benefit,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun EnhancedContractButton(
    onClick: () -> Unit,
    color: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(18.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        ),
        interactionSource = interactionSource
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Contratar Plan",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun EnhancedInfoChips() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            "Activación inmediata" to Icons.Default.Speed,
            "Soporte 24/7" to Icons.Default.Support,
            "Instalación gratuita" to Icons.Default.Build
        ).forEach { (text, icon) ->
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}