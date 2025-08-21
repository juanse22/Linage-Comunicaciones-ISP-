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
import kotlinx.coroutines.delay

/**
 * Data class for business partners
 */
data class BusinessPartner(
    val name: String,
    val emoji: String,
    val discount: String,
    val description: String,
    val backgroundColor: Color
)

/**
 * Business partners section with golden ratio design
 */
@Composable
fun HomePartnersSection() {
    var isLoaded by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(1000)
        isLoaded = true
    }
    
    val partners = remember {
        listOf(
            BusinessPartner(
                name = "Netflix",
                emoji = "🎬",
                discount = "Incluido",
                description = "Streaming premium en tu plan de internet",
                backgroundColor = Color(0xFFE50914)
            ),
            BusinessPartner(
                name = "Win Sports+",
                emoji = "⚽",
                discount = "Gratis 3 meses",
                description = "Todo el fútbol colombiano en vivo",
                backgroundColor = Color(0xFF9C27B0)
            ),
            BusinessPartner(
                name = "Paramount+",
                emoji = "🍿",
                discount = "1 mes gratis",
                description = "Series y películas exclusivas",
                backgroundColor = Color(0xFF0073E6)
            ),
            BusinessPartner(
                name = "DIRECTV GO",
                emoji = "📺",
                discount = "Incluido",
                description = "Canales premium y contenido on demand",
                backgroundColor = Color(0xFF2196F3)
            )
        )
    }
    
    AnimatedVisibility(
        visible = isLoaded,
        enter = SharedAnimations.EntranceSpecs.slideInFromBottom(delayMs = 400)
    ) {
        Column {
            // Section header
            Text(
                text = "Nuestros Aliados Estratégicos",
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
                text = "Contenido premium incluido en tus planes de internet",
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
            
            // Partners cards
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(GoldenRatio.SPACING_MD),
                contentPadding = PaddingValues(horizontal = GoldenRatio.SPACING_MD)
            ) {
                itemsIndexed(partners) { index, partner ->
                    AnimatedVisibility(
                        visible = isLoaded,
                        enter = SharedAnimations.EntranceSpecs.slideInFromRight(
                            delayMs = index * 150
                        )
                    ) {
                        PartnerCard(
                            partner = partner,
                            modifier = Modifier.width(GoldenRatio.CARD_WIDTH_MEDIUM)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(GoldenRatio.SPACING_XL))
            
            // Call to action
            AnimatedVisibility(
                visible = isLoaded,
                enter = SharedAnimations.EntranceSpecs.fadeInWithScale(delayMs = 800)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = GoldenRatio.SPACING_MD),
                    colors = CardDefaults.cardColors(
                        containerColor = LinageOrange.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(GoldenRatio.RADIUS_XL)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(GoldenRatio.SPACING_XL),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🚀",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(GoldenRatio.SPACING_MD))
                        Text(
                            text = "¿Listo para la mejor experiencia digital?",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = LinageWhite,
                                fontSize = GoldenRatio.TEXT_SIZE_TITLE_LARGE
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(GoldenRatio.SPACING_SM))
                        Text(
                            text = "Contrata ahora y disfruta de todos estos beneficios desde el primer día",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = LinageWhite.copy(alpha = 0.9f),
                                fontSize = GoldenRatio.TEXT_SIZE_BODY_LARGE
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PartnerCard(
    partner: BusinessPartner,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    
    val scale by SharedAnimations.rememberSharedSpring(
        targetValue = if (isHovered) 1.05f else 1f
    )
    
    Card(
        modifier = modifier
            .height(GoldenRatio.CARD_HEIGHT_MEDIUM * GoldenRatio.PHI_INVERSE)
            .clickable { isHovered = !isHovered }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(containerColor = LinageWhite),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) GoldenRatio.ELEVATION_XL else GoldenRatio.ELEVATION_MD
        ),
        shape = RoundedCornerShape(GoldenRatio.RADIUS_LG)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background with partner color
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                partner.backgroundColor.copy(alpha = 0.05f),
                                partner.backgroundColor.copy(alpha = 0.15f)
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
                // Header with discount badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        color = partner.backgroundColor.copy(alpha = 0.15f),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        modifier = Modifier.size(GoldenRatio.ICON_SIZE_LG)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = partner.emoji,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                    
                    // Discount badge
                    Surface(
                        color = partner.backgroundColor,
                        shape = RoundedCornerShape(GoldenRatio.RADIUS_MD)
                    ) {
                        Text(
                            text = partner.discount,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = GoldenRatio.TEXT_SIZE_BODY_SMALL
                            ),
                            modifier = Modifier.padding(
                                horizontal = GoldenRatio.SPACING_SM,
                                vertical = GoldenRatio.SPACING_XS
                            )
                        )
                    }
                }
                
                // Content
                Column {
                    Text(
                        text = partner.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = partner.backgroundColor,
                            fontSize = GoldenRatio.TEXT_SIZE_TITLE
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(GoldenRatio.SPACING_SM))
                    
                    Text(
                        text = partner.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = LinageGrayDark,
                            fontSize = GoldenRatio.TEXT_SIZE_BODY_SMALL,
                            lineHeight = GoldenRatio.TEXT_SIZE_BODY_SMALL * GoldenRatio.PHI_INVERSE * 2
                        )
                    )
                }
            }
        }
    }
}