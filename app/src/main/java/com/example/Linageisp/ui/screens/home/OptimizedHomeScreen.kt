package com.example.Linageisp.ui.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.Linageisp.R
import com.example.Linageisp.ui.theme.GoldenRatio
import com.example.Linageisp.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Banner promocional para el carrusel (Original)
 */
data class PromoBanner(
    val title: String,
    val description: String,
    val imageRes: Int,
    val gradientColors: List<Color>
)

/**
 * Optimized and modularized home screen with golden ratio design
 * This replaces the large 1325-line NewHomeScreen with clean, maintainable components
 */
@Composable
fun OptimizedHomeScreen(
    onNavigateToPlans: () -> Unit,
    onNavigateToSupport: () -> Unit = {},
    onNavigateToAI: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(bottom = GoldenRatio.SPACING_MEGA)
    ) {
        // Header section with logo and greeting
        item {
            HomeHeaderSection()
        }
        
        // Quick actions for main app features
        item {
            HomeQuickActionsSection(
                onNavigateToPlans = onNavigateToPlans,
                onNavigateToSupport = onNavigateToSupport,
                onNavigateToAI = onNavigateToAI
            )
        }
        
        // Promotional banner (Original)
        item {
            OriginalPromoBannerCarousel()
        }
        
        // Benefits section
        item {
            HomeBenefitsSection()
        }
        
        // Business partners section
        item {
            HomePartnersSection()
        }
        
        // Final spacer for bottom navigation
        item {
            Spacer(modifier = Modifier.height(GoldenRatio.SPACING_MEGA))
        }
    }
}

/**
 * Carrusel de banners promocionales (ORIGINAL)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OriginalPromoBannerCarousel() {
    val banners = listOf(
        PromoBanner(
            title = "¡Internet Super Rápido!",
            description = "Hasta 900 Mbps de velocidad",
            imageRes = R.drawable.linagebanner,
            gradientColors = listOf(LinageOrange, LinageOrangeLight)
        ),
        PromoBanner(
            title = "Servicio de Cámaras",
            description = "Seguridad y vigilancia 24/7",
            imageRes = R.drawable.linagebanner,
            gradientColors = listOf(Color(0xFFFF6D00), Color(0xFFFF8F00))
        ),
        PromoBanner(
            title = "DIRECTV GO",
            description = "Streaming premium incluido",
            imageRes = R.drawable.directv_go,
            gradientColors = listOf(Color(0xFF1976D2), Color(0xFF42A5F5))
        ),
        PromoBanner(
            title = "Paramount+ GRATIS",
            description = "1 mes incluido en todos los planes",
            imageRes = R.drawable.paramount_plus,
            gradientColors = listOf(Color(0xFF0073E6), Color(0xFF4A9EFF))
        ),
        PromoBanner(
            title = "Fibra Óptica",
            description = "La tecnología más avanzada",
            imageRes = R.drawable.linagebanner,
            gradientColors = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
        )
    )
    
    val pagerState = rememberPagerState(pageCount = { banners.size })
    
    // Auto-scroll del carrusel
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000) // 4 segundos por banner
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }
    
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Carrusel
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            pageSpacing = 8.dp
        ) { page ->
            OriginalBannerCard(banner = banners[page])
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Indicadores de página
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 24.dp else 8.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isSelected) LinageOrange else LinageGray.copy(alpha = 0.3f)
                        )
                        .animateContentSize(
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                )
                if (index < banners.size - 1) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

/**
 * Tarjeta individual del banner (ORIGINAL)
 */
@Composable
private fun OriginalBannerCard(banner: PromoBanner) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                clip = true
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(banner.gradientColors)
                )
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = banner.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.2f },
                contentScale = ContentScale.Crop
            )
            
            // Contenido del banner estándar
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = banner.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LinageWhite
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = banner.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = LinageWhite.copy(alpha = 0.9f)
                    )
                )
            }
        }
    }
}