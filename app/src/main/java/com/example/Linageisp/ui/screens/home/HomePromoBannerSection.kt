package com.example.Linageisp.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Linageisp.R
import com.example.Linageisp.ui.animations.SharedAnimations
import com.example.Linageisp.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Professional promotional banner for Linage ISP
 */
data class LinageBanner(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val gradientColors: List<Color>,
    val textColor: Color = Color.White
)

/**
 * Professional promotional banner section matching Linage ISP design
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePromoBannerSection() {
    var isLoaded by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(400)
        isLoaded = true
    }
    
    val banners = remember {
        listOf(
            LinageBanner(
                title = "Paramount+ GRATIS",
                description = "1 mes incluido en todos los planes",
                icon = Icons.Default.LiveTv,
                gradientColors = listOf(
                    Color(0xFF2196F3),
                    Color(0xFF1976D2)
                )
            ),
            LinageBanner(
                title = "Asistente IA 24/7",
                description = "Resuelve tus dudas al instante",
                icon = Icons.Default.Psychology,
                gradientColors = listOf(
                    Color(0xFF7C4DFF),
                    Color(0xFF651FFF)
                )
            ),
            LinageBanner(
                title = "Hasta 900 Mbps",
                description = "Velocidad garantizada",
                icon = Icons.Default.Speed,
                gradientColors = listOf(
                    Color(0xFFFF6B00),
                    Color(0xFFFF9500)
                )
            ),
            LinageBanner(
                title = "Soporte 24/7",
                description = "Asistencia técnica especializada",
                icon = Icons.Default.Build,
                gradientColors = listOf(
                    Color(0xFF00BCD4),
                    Color(0xFF0097A7)
                )
            )
        )
    }
    
    val pagerState = rememberPagerState(pageCount = { banners.size })
    
    // Auto-scroll cada 4 segundos
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    
    AnimatedVisibility(
        visible = isLoaded,
        enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { 50 })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(horizontal = 16.dp)
        ) {
            // Professional banner carousel
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentPadding = PaddingValues(horizontal = 0.dp),
                pageSpacing = 8.dp
            ) { page ->
                LinageBannerCard(
                    banner = banners[page],
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Professional page indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(banners.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val animatedWidth by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = tween(300),
                        label = "width"
                    )
                    
                    Box(
                        modifier = Modifier
                            .width(animatedWidth)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isSelected) LinageOrange else Color.Gray.copy(alpha = 0.3f)
                            )
                    )
                    
                    if (index < banners.size - 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Call-to-action button
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LinageOrange
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Ver Todos los Planes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

/**
 * Professional banner card with Material Design principles
 */
@Composable
private fun LinageBannerCard(
    banner: LinageBanner,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = banner.gradientColors,
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
        ) {
            // Subtle watermark pattern (for Paramount+ banner)
            if (banner.title.contains("Paramount+")) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.1f)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                radius = 300f
                            )
                        )
                )
            }
            
            // Content layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Icon
                Icon(
                    imageVector = banner.icon,
                    contentDescription = null,
                    tint = banner.textColor,
                    modifier = Modifier.size(40.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title
                Text(
                    text = banner.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = banner.textColor,
                        fontSize = 24.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description
                Text(
                    text = banner.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = banner.textColor.copy(alpha = 0.9f),
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}


