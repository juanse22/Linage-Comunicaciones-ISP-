package com.example.Linageisp.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.Linageisp.ui.animations.SharedAnimations
import com.example.Linageisp.ui.theme.*
import com.example.Linageisp.ui.theme.GoldenRatio
import java.time.LocalTime

/**
 * Modular header section with golden ratio proportions and optimized animations
 */
@Composable
fun HomeHeaderSection() {
    var isLoaded by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        isLoaded = true
    }
    
    AnimatedVisibility(
        visible = isLoaded,
        enter = SharedAnimations.EntranceSpecs.slideInFromBottom(0) + 
                SharedAnimations.EntranceSpecs.fadeInWithScale(0)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GoldenRatio.SPACING_MD),
            colors = CardDefaults.cardColors(containerColor = LinageWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = GoldenRatio.ELEVATION_MD),
            shape = RoundedCornerShape(GoldenRatio.RADIUS_XL)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(GoldenRatio.HEADER_HEIGHT_LARGE)
                    .background(
                        Brush.horizontalGradient(
                            listOf(LinageOrange, LinageOrangeLight)
                        )
                    )
            ) {
                // Decorative pattern
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val patternSize = size.width / (10 * GoldenRatio.PHI)
                    for (i in 0..30) {
                        val x = (i % 8) * patternSize * GoldenRatio.PHI
                        val y = (i / 8) * patternSize * GoldenRatio.PHI_INVERSE * 4
                        drawCircle(
                            color = LinageWhite.copy(alpha = 0.05f),
                            radius = patternSize / 8,
                            center = androidx.compose.ui.geometry.Offset(x, y)
                        )
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(GoldenRatio.SPACING_XL),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Logo section
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = LinageWhite.copy(alpha = 0.15f),
                                shape = androidx.compose.foundation.shape.CircleShape,
                                modifier = Modifier.size(GoldenRatio.ICON_SIZE_LG)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = "🌐",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(GoldenRatio.SPACING_LG))
                            Column {
                                Text(
                                    text = "Linage ISP",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = LinageWhite,
                                        fontSize = GoldenRatio.TEXT_SIZE_TITLE_LARGE
                                    )
                                )
                                Text(
                                    text = "Conectamos el futuro",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = LinageWhite.copy(alpha = 0.9f),
                                        fontSize = GoldenRatio.TEXT_SIZE_BODY_LARGE
                                    )
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(GoldenRatio.SPACING_MD))
                        
                        // Greeting with time-based personalization
                        val greetingMessage = remember {
                            val currentHour = try {
                                LocalTime.now().hour
                            } catch (e: Exception) {
                                12 // Default to noon if time API fails
                            }
                            
                            when (currentHour) {
                                in 5..11 -> "Buenos días ☀️"
                                in 12..17 -> "Buenas tardes 🌤️"
                                in 18..22 -> "Buenas noches 🌙"
                                else -> "¡Hola! 👋"
                            }
                        }
                        
                        Surface(
                            color = LinageWhite.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(GoldenRatio.RADIUS_LG)
                        ) {
                            Text(
                                text = greetingMessage,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = LinageWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = GoldenRatio.TEXT_SIZE_TITLE
                                ),
                                modifier = Modifier.padding(
                                    horizontal = GoldenRatio.SPACING_LG,
                                    vertical = GoldenRatio.SPACING_SM
                                )
                            )
                        }
                    }
                    
                    // Status indicator
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val pulseAlpha by SharedAnimations.rememberPulseAnimation()
                        
                        Surface(
                            color = Color.Green.copy(alpha = pulseAlpha),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            modifier = Modifier.size(GoldenRatio.SPACING_MD)
                        ) {
                            // Online indicator
                        }
                        Spacer(modifier = Modifier.height(GoldenRatio.SPACING_XS))
                        Text(
                            text = "En línea",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = LinageWhite.copy(alpha = 0.8f),
                                fontSize = GoldenRatio.TEXT_SIZE_BODY_SMALL
                            )
                        )
                    }
                }
            }
        }
    }
}