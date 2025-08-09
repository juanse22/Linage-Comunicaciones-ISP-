@file:OptIn(ExperimentalAnimationApi::class)

package com.example.Linageisp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Linageisp.R
import com.example.Linageisp.ui.theme.*
import com.example.Linageisp.ui.components.FrostedCard
import androidx.compose.runtime.getValue
import kotlinx.coroutines.delay

/**
 * Banner promocional para el carrusel
 */
data class PromoBanner(
    val title: String,
    val description: String,
    val imageRes: Int,
    val gradientColors: List<Color>
)

/**
 * Beneficio destacado con emoji
 */
data class HomeBenefit(
    val emoji: String,
    val title: String,
    val description: String,
    val backgroundColor: Color
)

/**
 * Aliado comercial
 */
data class BusinessPartner(
    val name: String,
    val emoji: String,
    val discount: String,
    val description: String,
    val backgroundColor: Color
)

/**
 * Pantalla de Inicio moderna con carrusel y beneficios
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewHomeScreen(
    onNavigateToPlans: () -> Unit,
    onNavigateToSpeedTest: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {}
) {
    var isLoaded by remember { mutableStateOf(false) }
    
    // Activar animaciones después de un delay
    LaunchedEffect(Unit) {
        delay(300)
        isLoaded = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(LinageBeige, LinageWhite),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header con logo y saludo
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = fadeIn(tween(600)) + slideInVertically(
                    tween(600), initialOffsetY = { -it / 2 }
                )
            ) {
                HeaderSection()
            }
        }
        
        // Banner promocional rotativo
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = fadeIn(tween(800, 200)) + slideInHorizontally(
                    tween(800, 200), initialOffsetX = { -it / 3 }
                )
            ) {
                PromoBannerCarousel()
            }
        }
        
        // Botón de acción rápida
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = scaleIn(tween(600, 400)) + fadeIn(tween(600, 400))
            ) {
                QuickActionButton(onClick = onNavigateToPlans)
            }
        }
        
        // Accesos rápidos a nuevas funciones
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = fadeIn(tween(800, 500)) + slideInVertically(
                    tween(800, 500), initialOffsetY = { it / 4 }
                )
            ) {
                QuickAccessSection(
                    onSpeedTestClick = onNavigateToSpeedTest,
                    onSupportClick = onNavigateToSupport
                )
            }
        }
        
        // Sección de beneficios
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = fadeIn(tween(800, 600)) + slideInVertically(
                    tween(800, 600), initialOffsetY = { it / 3 }
                )
            ) {
                BenefitsSection()
            }
        }
        
        // Sección Aliados Linage
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = fadeIn(tween(1000, 800)) + slideInVertically(
                    tween(1000, 800), initialOffsetY = { it / 2 }
                )
            ) {
                BusinessPartnersSection()
            }
        }
        
        // Sección DIRECTV GO - Socio Estratégico
        item {
            AnimatedVisibility(
                visible = isLoaded,
                enter = fadeIn(tween(1200, 1000)) + slideInVertically(
                    tween(1200, 1000), initialOffsetY = { it / 3 }
                )
            ) {
                DirectTVPartnerSection()
            }
        }
    }
}

/**
 * Header con logo y saludo personalizado
 */
@Composable
private fun HeaderSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LinageWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.linagebanner),
                contentDescription = "Linage ISP Logo",
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Texto de bienvenida
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¡Bienvenido!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = LinageOrange
                    )
                )
                Text(
                    text = "Tu conexión de confianza",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LinageGray
                    )
                )
            }
            
            // Notificación
            Card(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = LinageOrangeSoft
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔔",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

/**
 * Carrusel de banners promocionales
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PromoBannerCarousel() {
    val banners = listOf(
        PromoBanner(
            title = "¡Internet Super Rápido!",
            description = "Hasta 900 Mbps de velocidad",
            imageRes = R.drawable.linagebanner,
            gradientColors = listOf(LinageOrange, LinageOrangeLight)
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
            BannerCard(banner = banners[page])
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
 * Tarjeta individual del banner
 */
@Composable
private fun BannerCard(banner: PromoBanner) {
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
            
            // Contenido del banner
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

/**
 * Botón de acción rápida
 */
@Composable
private fun QuickActionButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "quick_action_scale"
    )

    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(60.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = LinageOrange
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📶",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Ver Todos los Planes",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LinageWhite
                )
            )
        }
    }
    
    // Reset presión
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}


/**
 * Sección de beneficios principales
 */
@Composable
private fun BenefitsSection() {
    val benefits = listOf(
        HomeBenefit(
            emoji = "⚡",
            title = "Velocidad",
            description = "Hasta 900 Mbps",
            backgroundColor = LinageOrangeSoft
        ),
        HomeBenefit(
            emoji = "🛠️",
            title = "Soporte 24/7",
            description = "Asistencia técnica",
            backgroundColor = Color(0xFFE3F2FD)
        ),
        HomeBenefit(
            emoji = "🎬",
            title = "Streaming",
            description = "Sin interrupciones",
            backgroundColor = Color(0xFFF3E5F5)
        ),
        HomeBenefit(
            emoji = "🔧",
            title = "Instalación",
            description = "100% Gratuita",
            backgroundColor = Color(0xFFE8F5E8)
        )
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "¿Por qué Linage?",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = LinageOrangeDark
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            itemsIndexed(benefits) { index, benefit ->
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(
                        tween(600, index * 150),
                        initialScale = 0.8f
                    ) + fadeIn(tween(600, index * 150))
                ) {
                    BenefitCard(benefit = benefit)
                }
            }
        }
    }
}

/**
 * Tarjeta de beneficio
 */
@Composable
private fun BenefitCard(benefit: HomeBenefit) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "benefit_scale"
    )

    Card(
        onClick = { isPressed = true },
        modifier = Modifier
            .width(140.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = benefit.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = benefit.emoji,
                fontSize = 36.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = benefit.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = benefit.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = LinageGray
                ),
                textAlign = TextAlign.Center
            )
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}


/**
 * Sección de aliados comerciales
 */
@Composable
private fun BusinessPartnersSection() {
    val partners = listOf(
        BusinessPartner(
            name = "Lyon Sport",
            emoji = "👕",
            discount = "25% OFF",
            description = "Ropa deportiva premium",
            backgroundColor = Color(0xFFFFF3E0)
        ),
        BusinessPartner(
            name = "Netflix Premium",
            emoji = "🎬",
            discount = "3 MESES",
            description = "Streaming premium gratis",
            backgroundColor = Color(0xFFFFE0E0)
        ),
        BusinessPartner(
            name = "Deportes & Más",
            emoji = "⚽",
            discount = "15% OFF",
            description = "Equipos deportivos",
            backgroundColor = Color(0xFFE8F5E8)
        )
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Header de la sección
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🤝",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Aliados Linage",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = LinageOrangeDark
                )
            )
        }
        
        Text(
            text = "Descuentos exclusivos para nuestros clientes",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = LinageGray
            ),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            itemsIndexed(partners) { index, partner ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(
                        tween(600, index * 200),
                        initialOffsetX = { it / 2 }
                    ) + fadeIn(tween(600, index * 200))
                ) {
                    PartnerCard(partner = partner)
                }
            }
        }
    }
}

/**
 * Tarjeta de aliado comercial
 */
@Composable
private fun PartnerCard(partner: BusinessPartner) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "partner_scale"
    )

    Card(
        onClick = { isPressed = true },
        modifier = Modifier
            .width(180.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = partner.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con emoji y descuento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = partner.emoji,
                    fontSize = 32.sp
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = LinageOrange
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = partner.discount,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LinageWhite
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Nombre del negocio
            Text(
                text = partner.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Descripción
            Text(
                text = partner.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = LinageGray
                )
            )
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}


/**
 * Sección de accesos rápidos a nuevas funcionalidades
 */
@Composable
private fun QuickAccessSection(
    onSpeedTestClick: () -> Unit,
    onSupportClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Accesos Rápidos",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = LinageOrangeDark
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Test de Velocidad
            QuickAccessCard(
                modifier = Modifier.weight(1f),
                title = "Test Velocidad",
                description = "Mide tu conexión",
                emoji = "⚡",
                backgroundColor = Color(0xFFE8F5E8),
                onClick = onSpeedTestClick
            )
            
            // Soporte Técnico
            QuickAccessCard(
                modifier = Modifier.weight(1f),
                title = "Soporte",
                description = "Ayuda instantánea",
                emoji = "🆘",
                backgroundColor = Color(0xFFE3F2FD),
                onClick = onSupportClick
            )
        }
    }
}

/**
 * Tarjeta de acceso rápido individual
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickAccessCard(
    title: String,
    description: String,
    emoji: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "quick_access_scale"
    )

    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = LinageGray
                ),
                textAlign = TextAlign.Center
            )
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

/**
 * Sección especial para DIRECTV GO - Socio Estratégico
 */
@Composable
private fun DirectTVPartnerSection() {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "directv_scale"
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        // Header de la sección con estilo especial
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Socio Estratégico",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = LinageOrangeDark
                )
            )
        }
        
        Text(
            text = "Entretenimiento premium para nuestros clientes",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = LinageGray
            ),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Tarjeta especial de DIRECTV GO con estilo Frutiger Aero
        Card(
            onClick = { isPressed = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            colors = CardDefaults.cardColors(
                containerColor = LinageWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFE3F2FD), // Azul claro
                                Color(0xFFF3E5F5), // Rosa claro
                                LinageOrangeSoft    // Naranja suave Linage
                            ),
                            startX = 0f,
                            endX = Float.POSITIVE_INFINITY
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo de DIRECTV GO con marco glassmorphic
                    Card(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = LinageWhite.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.directv_go),
                                contentDescription = "DIRECTV GO Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    // Contenido de texto
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Badge de "ALIADO OFICIAL"
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = LinageOrange
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "ALIADO OFICIAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LinageWhite,
                                    letterSpacing = 0.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "DIRECTV GO",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2) // Azul DIRECTV
                            )
                        )
                        
                        Text(
                            text = "Streaming premium incluido",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = LinageGray,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        
                        Text(
                            text = "Miles de contenidos al alcance de tu mano",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LinageGray.copy(alpha = 0.8f)
                            )
                        )
                    }
                    
                    // Icono de acción con efecto glassmorphic
                    Card(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = LinageOrange.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "▶️",
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }
        
        // Texto descriptivo adicional con estilo sutil
        Text(
            text = "🔥 Disponible para todos nuestros clientes de fibra óptica",
            style = MaterialTheme.typography.bodySmall.copy(
                color = LinageOrangeDark,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(top = 12.dp),
            textAlign = TextAlign.Center
        )
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(200)
            isPressed = false
        }
    }
}