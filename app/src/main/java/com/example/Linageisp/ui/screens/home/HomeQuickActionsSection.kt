package com.example.Linageisp.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.Linageisp.ui.animations.SharedAnimations
import com.example.Linageisp.ui.theme.*
import com.example.Linageisp.ui.theme.GoldenRatio

/**
 * Data class for quick actions
 */
data class QuickAction(
    val title: String,
    val description: String,
    val emoji: String,
    val color: Color,
    val action: () -> Unit
)

/**
 * Quick actions section with golden ratio layout
 */
@Composable
fun HomeQuickActionsSection(
    onNavigateToPlans: () -> Unit,
    onNavigateToSupport: () -> Unit = {},
    onNavigateToAI: () -> Unit = {}
) {
    var isLoaded by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(400)
        isLoaded = true
    }
    
    val quickActions = remember {
        listOf(
            QuickAction(
                title = "Ver Planes",
                description = "Descubre nuestros planes perfectos",
                emoji = "📊",
                color = LinageOrange,
                action = onNavigateToPlans
            ),
            QuickAction(
                title = "IA Assistant",
                description = "Consulta inteligente 24/7",
                emoji = "🤖",
                color = Color(0xFF6A1B9A),
                action = onNavigateToAI
            ),
            QuickAction(
                title = "Soporte",
                description = "Ayuda técnica especializada",
                emoji = "🛠️",
                color = Color(0xFF2196F3),
                action = onNavigateToSupport
            )
        )
    }
    
    AnimatedVisibility(
        visible = isLoaded,
        enter = SharedAnimations.EntranceSpecs.fadeInWithScale(delayMs = 200)
    ) {
        Column {
            // Section header
            Text(
                text = "Acciones Rápidas",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = LinageWhite,
                    fontSize = GoldenRatio.TEXT_SIZE_TITLE_LARGE
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = GoldenRatio.SPACING_MD,
                        vertical = GoldenRatio.SPACING_SM
                    ),
                textAlign = TextAlign.Center
            )
            
            // Quick action cards
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = GoldenRatio.SPACING_SM),
                horizontalArrangement = Arrangement.spacedBy(GoldenRatio.SPACING_MD),
                contentPadding = PaddingValues(horizontal = GoldenRatio.SPACING_MD)
            ) {
                itemsIndexed(quickActions) { index, action ->
                    AnimatedVisibility(
                        visible = isLoaded,
                        enter = SharedAnimations.EntranceSpecs.slideInFromRight(
                            delayMs = index * 150
                        )
                    ) {
                        QuickActionCard(
                            action = action,
                            modifier = Modifier.width(GoldenRatio.CARD_WIDTH_MEDIUM)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionCard(
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by SharedAnimations.rememberSharedSpring(
        targetValue = if (isPressed) 0.95f else 1f
    )
    
    Card(
        onClick = {
            isPressed = true
            action.action()
        },
        modifier = modifier
            .height(GoldenRatio.CARD_HEIGHT_COMPACT)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(containerColor = LinageWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = GoldenRatio.ELEVATION_MD),
        shape = RoundedCornerShape(GoldenRatio.RADIUS_LG)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                action.color.copy(alpha = 0.1f),
                                action.color.copy(alpha = 0.05f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(GoldenRatio.SPACING_LG),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Icon with golden ratio container
                Surface(
                    color = action.color.copy(alpha = 0.15f),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    modifier = Modifier
                        .size(GoldenRatio.ICON_SIZE_LG)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = action.emoji,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = GoldenRatio.TEXT_SIZE_HEADLINE * GoldenRatio.PHI_INVERSE
                            )
                        )
                    }
                }
                
                // Content
                Column {
                    Text(
                        text = action.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = action.color,
                            fontSize = GoldenRatio.TEXT_SIZE_TITLE
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(GoldenRatio.SPACING_XS))
                    Text(
                        text = action.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = LinageGrayDark,
                            fontSize = GoldenRatio.TEXT_SIZE_BODY_SMALL,
                            lineHeight = GoldenRatio.TEXT_SIZE_BODY_SMALL * GoldenRatio.PHI_INVERSE
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}