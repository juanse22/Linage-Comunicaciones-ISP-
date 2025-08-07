package com.example.Linageisp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Linageisp.ui.components.LyonSportWheelOfFortune
import com.example.Linageisp.ui.components.WheelResult
import com.example.Linageisp.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Pantalla principal de la Ruleta Lyon Sport
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LyonSportWheelScreen(
    onNavigateBack: () -> Unit = {}
) {
    var isLoaded by remember { mutableStateOf(false) }
    var wheelResult by remember { mutableStateOf<WheelResult?>(null) }
    var showInstructions by remember { mutableStateOf(true) }

    // Trigger de animación inicial
    LaunchedEffect(Unit) {
        delay(500)
        isLoaded = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1565C0),
                        Color(0xFF42A5F5),
                        Color(0xFFE3F2FD)
                    )
                )
            )
    ) {
        // TopBar
        AnimatedVisibility(
            visible = isLoaded,
            enter = slideInVertically(tween(600), initialOffsetY = { -it })
        ) {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎯",
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ruleta Lyon Sport",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Instrucciones
            item {
                AnimatedVisibility(
                    visible = isLoaded && showInstructions,
                    enter = fadeIn(tween(800, 200)) + slideInVertically(
                        tween(800, 200), initialOffsetY = { it / 3 }
                    )
                ) {
                    InstructionsCard(
                        onDismiss = { showInstructions = false }
                    )
                }
            }

            // Ruleta
            item {
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = scaleIn(tween(1000, 400)) + fadeIn(tween(1000, 400))
                ) {
                    LyonSportWheelOfFortune(
                        onResult = { result ->
                            wheelResult = result
                        }
                    )
                }
            }

            // Términos y condiciones
            item {
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(tween(1200, 800)) + slideInVertically(
                        tween(1200, 800), initialOffsetY = { it / 2 }
                    )
                ) {
                    TermsAndConditionsCard()
                }
            }

            // Promociones adicionales
            item {
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(tween(1400, 1000)) + slideInVertically(
                        tween(1400, 1000), initialOffsetY = { it / 2 }
                    )
                ) {
                    AdditionalPromotionsCard()
                }
            }
        }
    }

    // Dialog de resultado (si existe)
    wheelResult?.let { result ->
        WinnerDialog(
            result = result,
            onDismiss = { wheelResult = null }
        )
    }
}

/**
 * Tarjeta de instrucciones
 */
@Composable
private fun InstructionsCard(
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 Instrucciones",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                )
                
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "✕",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            val instructions = listOf(
                "1. 🎯 Presiona el botón \"¡GIRAR RULETA!\"",
                "2. ⏳ Espera a que la ruleta se detenga",
                "3. 🏆 ¡Descubre tu premio Lyon Sport!",
                "4. 🛍️ Canjea tu premio en nuestra tienda"
            )

            instructions.forEach { instruction ->
                Text(
                    text = instruction,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF424242)
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E8)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "💡 Tip: Los premios especiales (Netflix y descuentos) tienen probabilidades más bajas pero ¡mayores recompensas!",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Diálogo de ganador
 */
@Composable
private fun WinnerDialog(
    result: WheelResult,
    onDismiss: () -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDismiss()
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    )
                ) {
                    Text(
                        text = "¡Genial!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = result.prize.emoji,
                        fontSize = 48.sp
                    )
                    Text(
                        text = "¡FELICITACIONES!",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1565C0)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = result.message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (result.prize.isSpecial) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFD700)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "⭐ PREMIO ESPECIAL ⭐",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1565C0)
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Presenta este resultado en Lyon Sport para canjearlo.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

/**
 * Tarjeta de términos y condiciones
 */
@Composable
private fun TermsAndConditionsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📜 Términos y Condiciones",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val terms = listOf(
                "• Solo un premio por cliente por día",
                "• Los premios tienen fecha de vencimiento de 30 días",
                "• Netflix requiere activación con documento de identidad",
                "• Descuentos no acumulables con otras promociones",
                "• Lyon Sport se reserva el derecho de modificar premios"
            )

            terms.forEach { term ->
                Text(
                    text = term,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF666666)
                    ),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Tarjeta de promociones adicionales
 */
@Composable
private fun AdditionalPromotionsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1565C0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🛍️",
                fontSize = 32.sp
            )
            
            Text(
                text = "¡Más Promociones Lyon Sport!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Visita nuestra tienda física y descubre:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.9f)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            val promotions = listOf(
                "👕 Nueva colección deportiva",
                "👟 Zapatillas de última tecnología", 
                "🏃‍♂️ Ropa para running y fitness",
                "⚽ Equipos para deportes de equipo"
            )

            promotions.forEach { promotion ->
                Text(
                    text = promotion,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White
                    ),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "📍 Lyon Sport - Centro Comercial Plaza Central",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.8f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}