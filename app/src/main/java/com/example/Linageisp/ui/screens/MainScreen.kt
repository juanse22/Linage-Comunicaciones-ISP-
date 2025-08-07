package com.example.Linageisp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Linageisp.R
import com.example.Linageisp.data.Plan
import com.example.Linageisp.ui.theme.*
import com.example.Linageisp.viewmodel.PlanViewModel
import kotlinx.coroutines.delay

data class StreamingService(
    val name: String,
    val logoRes: Int,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: PlanViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var isLoaded by remember { mutableStateOf(false) }
    
    // Trigger loading animation
    LaunchedEffect(Unit) {
        delay(500)
        isLoaded = true
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LinageBeige),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header with banner
            item {
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(tween(800)) + slideInVertically(
                        tween(800), initialOffsetY = { -it / 2 }
                    )
                ) {
                    HeaderSection()
                }
            }
            
            // Plans section
            item {
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(tween(600, 200)) + slideInVertically(
                        tween(600, 200), initialOffsetY = { it / 3 }
                    )
                ) {
                    PlansSection(uiState.plans)
                }
            }
            
            // Streaming services section
            item {
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(tween(800, 400)) + slideInVertically(
                        tween(800, 400), initialOffsetY = { it / 2 }
                    )
                ) {
                    StreamingServicesSection()
                }
            }
        }
        
        // Bottom navigation
        AnimatedVisibility(
            visible = isLoaded,
            enter = slideInVertically(
                tween(1000, 600), initialOffsetY = { it }
            ) + fadeIn(tween(800, 600)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomNavigationSection()
        }
        
        // Floating WhatsApp button
        AnimatedVisibility(
            visible = isLoaded,
            enter = scaleIn(tween(600, 800)) + fadeIn(tween(600, 800)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 80.dp)
        ) {
            FloatingWhatsAppButton()
        }
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.linagebanner),
            contentDescription = "Linage ISP Logo",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = "Linage ISP",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LinageOrange
                )
            )
            Text(
                text = "Internet de alta velocidad para tu hogar",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LinageGray
                )
            )
        }
    }
}

@Composable
private fun PlansSection(plans: List<Plan>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Planes de Internet",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = LinageOrangeDark
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        plans.forEachIndexed { index, plan ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(600, index * 150)) + slideInHorizontally(
                    tween(600, index * 150), initialOffsetX = { it / 3 }
                )
            ) {
                EnhancedPlanCard(plan = plan)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedPlanCard(plan: Plan) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = LinageWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header with speed and price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = plan.velocidad,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = LinageOrange,
                            fontSize = 32.sp
                        )
                    )
                    Text(
                        text = "de velocidad",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = LinageGray
                        )
                    )
                }
                
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
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Benefits with check icons
            val benefits = listOf(
                "Fibra óptica",
                "1 mes gratis de Paramount+",
                "1 pantalla simultánea",
                "Instalación gratuita"
            )
            
            benefits.forEach { benefit ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check",
                        tint = LinageOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = benefit,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Contract button
            Button(
                onClick = { 
                    isPressed = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LinageOrange
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text(
                    text = "Contratar",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = LinageWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
    
    // Reset pressed state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(200)
            isPressed = false
        }
    }
}

@Composable
private fun StreamingServicesSection() {
    val services = listOf(
        StreamingService("DIRECTV GO", R.drawable.directv_go, ""),
        StreamingService("Netflix", R.drawable.netflix, ""),
        StreamingService("Win Sports", R.drawable.win_sports, ""),
        StreamingService("Paramount+", R.drawable.paramount_plus, "")
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LinageOrangeTranslucent
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Servicios Destacados",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = LinageWhite
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Disfruta del mejor entretenimiento en Streaming y Multimedia",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LinageWhite.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                itemsIndexed(services) { index, service ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(600, index * 200)) + scaleIn(
                            tween(600, index * 200), initialScale = 0.8f
                        )
                    ) {
                        StreamingServiceItem(service)
                    }
                }
            }
        }
    }
}

@Composable
private fun StreamingServiceItem(service: StreamingService) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Card(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = LinageWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Image(
                painter = painterResource(id = service.logoRes),
                contentDescription = service.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = service.name,
            style = MaterialTheme.typography.labelSmall.copy(
                color = LinageWhite,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun BottomNavigationSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = LinageWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(
                icon = Icons.Default.Settings,
                label = "Servicios",
                onClick = { /* TODO: Navigate to services */ }
            )
            
            BottomNavItem(
                icon = Icons.Default.AccountBox,
                label = "Factura",
                onClick = { /* TODO: Navigate to invoice */ }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "nav_item_scale"
    )
    
    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = LinageOrangeSoft
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = LinageOrange,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = LinageOrangeDark,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
    
    // Reset pressed state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun FloatingWhatsAppButton() {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "whatsapp_scale"
    )
    
    FloatingActionButton(
        onClick = { 
            isPressed = true
            // TODO: Open WhatsApp
        },
        modifier = Modifier
            .size(64.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        containerColor = Color(0xFF25D366), // WhatsApp green
        shape = CircleShape
    ) {
        Text(
            text = "💬",
            fontSize = 24.sp
        )
    }
    
    // Reset pressed state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}