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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.Linageisp.ui.animations.SharedAnimations
import com.example.Linageisp.ui.theme.*
import com.example.Linageisp.ui.theme.GoldenRatio
import kotlinx.coroutines.delay

/**
 * Data class for home benefits
 */
data class HomeBenefit(
    val emoji: String,
    val title: String,
    val description: String,
    val backgroundColor: Color
)

/**
 * Benefits section showcasing key advantages with golden ratio layout
 */
@Composable
fun HomeBenefitsSection() {
    var isLoaded by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(800)
        isLoaded = true
    }
    
    val benefits = remember {
        listOf(
            HomeBenefit(
                emoji = "⚡",
                title = "Velocidad Extrema",
                description = "Fibra óptica hasta 1 Gbps para toda tu familia",
                backgroundColor = LinageOrange
            ),
            HomeBenefit(
                emoji = "🛡️",
                title = "Conexión Segura",
                description = "Protección avanzada contra amenazas digitales",
                backgroundColor = Color(0xFF4CAF50)
            ),
            HomeBenefit(
                emoji = "📞",
                title = "Soporte 24/7",
                description = "Atención especializada cuando la necesites",
                backgroundColor = Color(0xFF2196F3)
            ),
            HomeBenefit(
                emoji = "🎬",
                title = "Entretenimiento+",
                description = "Netflix, Prime Video, Disney+ y más incluidos",
                backgroundColor = Color(0xFFE50914)
            )
        )
    }
    
    AnimatedVisibility(
        visible = isLoaded,
        enter = SharedAnimations.EntranceSpecs.slideInFromBottom(delayMs = 200)
    ) {
        Column {
            // Section header
            Text(
                text = "¿Por qué elegir Linage ISP?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = LinageWhite,
                    fontSize = GoldenRatio.TEXT_SIZE_TITLE_LARGE
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = GoldenRatio.SPACING_MD,
                        vertical = GoldenRatio.SPACING_LG
                    ),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Tecnología de vanguardia con el mejor servicio al cliente",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = LinageWhite.copy(alpha = 0.8f),
                    fontSize = GoldenRatio.TEXT_SIZE_BODY_LARGE
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GoldenRatio.SPACING_MD),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(GoldenRatio.SPACING_XL))
            
            // Benefits cards
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(GoldenRatio.SPACING_MD),
                contentPadding = PaddingValues(horizontal = GoldenRatio.SPACING_MD)
            ) {
                itemsIndexed(benefits) { index, benefit ->
                    AnimatedVisibility(
                        visible = isLoaded,
                        enter = SharedAnimations.EntranceSpecs.fadeInWithScale(
                            delayMs = index * 200
                        )
                    ) {
                        BenefitCard(
                            benefit = benefit,
                            modifier = Modifier.width(GoldenRatio.CARD_WIDTH_MEDIUM)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BenefitCard(
    benefit: HomeBenefit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    
    val elevation by SharedAnimations.rememberOptimizedFloat(
        initialValue = GoldenRatio.ELEVATION_SM.value,
        targetValue = if (isHovered) GoldenRatio.ELEVATION_LG.value else GoldenRatio.ELEVATION_SM.value
    )
    
    Card(
        modifier = modifier
            .height(GoldenRatio.CARD_HEIGHT_COMPACT)
            .clickable { isHovered = !isHovered },
        colors = CardDefaults.cardColors(containerColor = LinageWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
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
                        Brush.radialGradient(
                            colors = listOf(
                                benefit.backgroundColor.copy(alpha = 0.1f),
                                benefit.backgroundColor.copy(alpha = 0.05f)
                            ),
                            radius = 300f
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(GoldenRatio.SPACING_LG),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon with animated container
                Surface(
                    color = benefit.backgroundColor.copy(alpha = 0.15f),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    modifier = Modifier.size(GoldenRatio.ICON_SIZE_XL)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = benefit.emoji,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = GoldenRatio.TEXT_SIZE_HEADLINE * GoldenRatio.PHI_INVERSE
                            )
                        )
                    }
                }
                
                // Content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = benefit.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = benefit.backgroundColor,
                            fontSize = GoldenRatio.TEXT_SIZE_TITLE
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(GoldenRatio.SPACING_SM))
                    
                    Text(
                        text = benefit.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = LinageGrayDark,
                            fontSize = GoldenRatio.TEXT_SIZE_BODY_SMALL,
                            lineHeight = GoldenRatio.TEXT_SIZE_BODY_SMALL * GoldenRatio.PHI_INVERSE * 2
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}