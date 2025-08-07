package com.example.Linageisp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Linageisp.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

/**
 * Premio de la ruleta Lyon Sport
 */
data class LyonSportPrize(
    val id: Int,
    val name: String,
    val emoji: String,
    val description: String,
    val color: Color,
    val isSpecial: Boolean = false
)

/**
 * Resultado de la ruleta
 */
data class WheelResult(
    val prize: LyonSportPrize,
    val message: String
)

/**
 * Componente de Ruleta de Premios Lyon Sport
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LyonSportWheelOfFortune(
    modifier: Modifier = Modifier,
    onResult: (WheelResult) -> Unit = {}
) {
    // Lista de premios Lyon Sport
    val prizes = remember {
        listOf(
            LyonSportPrize(
                id = 1,
                name = "Camiseta Deportiva",
                emoji = "👕",
                description = "Camiseta deportiva Lyon Sport",
                color = Color(0xFF2196F3)
            ),
            LyonSportPrize(
                id = 2,
                name = "Netflix Premium",
                emoji = "🎬",
                description = "3 meses de Netflix Premium",
                color = Color(0xFFE50914),
                isSpecial = true
            ),
            LyonSportPrize(
                id = 3,
                name = "Gorra Deportiva",
                emoji = "🧢",
                description = "Gorra oficial Lyon Sport",
                color = Color(0xFF4CAF50)
            ),
            LyonSportPrize(
                id = 4,
                name = "Descuento 20%",
                emoji = "💰",
                description = "20% OFF en toda la tienda",
                color = Color(0xFFFF9800),
                isSpecial = true
            ),
            LyonSportPrize(
                id = 5,
                name = "Zapatillas",
                emoji = "👟",
                description = "Zapatillas deportivas Lyon Sport",
                color = Color(0xFF9C27B0)
            ),
            LyonSportPrize(
                id = 6,
                name = "Short Deportivo",
                emoji = "🩳",
                description = "Short deportivo premium",
                color = Color(0xFF00BCD4)
            ),
            LyonSportPrize(
                id = 7,
                name = "Descuento 10%",
                emoji = "🏷️",
                description = "10% OFF en compras",
                color = Color(0xFFFFEB3B)
            ),
            LyonSportPrize(
                id = 8,
                name = "Medias Deportivas",
                emoji = "🧦",
                description = "Pack de medias Lyon Sport",
                color = Color(0xFF795548)
            )
        )
    }

    var isSpinning by remember { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(0f) }
    var selectedPrize by remember { mutableStateOf<LyonSportPrize?>(null) }
    var showResult by remember { mutableStateOf(false) }

    // Animación de rotación
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(
            durationMillis = 3000,
            easing = EaseOutCubic
        ),
        finishedListener = {
            if (isSpinning) {
                isSpinning = false
                showResult = true
                selectedPrize?.let { prize ->
                    onResult(
                        WheelResult(
                            prize = prize,
                            message = "¡Felicitaciones! Ganaste: ${prize.description}"
                        )
                    )
                }
            }
        },
        label = "wheel_rotation"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la ruleta
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    text = "🎯",
                    fontSize = 32.sp
                )
                Text(
                    text = "RULETA LYON SPORT",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "¡Gira y gana increíbles premios deportivos!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Contenedor de la ruleta
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(300.dp)
        ) {
            // Ruleta
            Canvas(
                modifier = Modifier
                    .size(280.dp)
                    .rotate(animatedRotation)
            ) {
                drawWheel(prizes)
            }

            // Indicador central
            Canvas(
                modifier = Modifier
                    .size(30.dp)
                    .offset(y = (-140).dp)
            ) {
                drawIndicator()
            }

            // Centro de la ruleta
            Card(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 24.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para girar
        Button(
            onClick = {
                if (!isSpinning) {
                    isSpinning = true
                    showResult = false
                    selectedPrize = null

                    // Generar rotación aleatoria
                    val baseRotation = Random.nextFloat() * 360f
                    val extraRotations = Random.nextInt(3, 6) * 360f
                    val finalRotation = rotation + baseRotation + extraRotations

                    // Calcular el premio ganador basado en la rotación final
                    val normalizedRotation = (360f - (finalRotation % 360f)) % 360f
                    val sectionSize = 360f / prizes.size
                    val winnerIndex = ((normalizedRotation + sectionSize / 2) / sectionSize).toInt() % prizes.size
                    selectedPrize = prizes[winnerIndex]

                    rotation = finalRotation
                }
            },
            enabled = !isSpinning,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSpinning) Color.Gray else Color(0xFF1565C0),
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isSpinning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Girando...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                } else {
                    Text(
                        text = "🎲",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "¡GIRAR RULETA!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        // Resultado
        AnimatedVisibility(
            visible = showResult && selectedPrize != null,
            enter = scaleIn(tween(500)) + fadeIn(tween(500)),
            exit = scaleOut(tween(300)) + fadeOut(tween(300))
        ) {
            selectedPrize?.let { prize ->
                ResultCard(prize = prize)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de premios disponibles
        PrizesGrid(prizes = prizes)
    }
}

/**
 * Dibuja la ruleta en el Canvas
 */
private fun DrawScope.drawWheel(prizes: List<LyonSportPrize>) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = size.minDimension / 2

    val sectionAngle = 360f / prizes.size

    prizes.forEachIndexed { index, prize ->
        val startAngle = index * sectionAngle
        
        // Dibujar sección
        drawArc(
            color = prize.color,
            startAngle = startAngle,
            sweepAngle = sectionAngle,
            useCenter = true,
            topLeft = Offset.Zero,
            size = size
        )

        // Dibujar borde
        drawArc(
            color = Color.White,
            startAngle = startAngle,
            sweepAngle = sectionAngle,
            useCenter = true,
            topLeft = Offset.Zero,
            size = size,
            style = Stroke(width = 4.dp.toPx())
        )

        // Calcular posición del texto
        val textAngle = Math.toRadians((startAngle + sectionAngle / 2).toDouble())
        val textRadius = radius * 0.7
        val textX = center.x + (textRadius * cos(textAngle)).toFloat()
        val textY = center.y + (textRadius * sin(textAngle)).toFloat()

        // Dibujar emoji
        drawContext.canvas.nativeCanvas.drawText(
            prize.emoji,
            textX,
            textY,
            android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 40.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
                setShadowLayer(4f, 2f, 2f, android.graphics.Color.BLACK)
            }
        )
    }

    // Borde exterior
    drawCircle(
        color = Color.White,
        radius = radius,
        center = center,
        style = Stroke(width = 8.dp.toPx())
    )
}

/**
 * Dibuja el indicador de la ruleta
 */
private fun DrawScope.drawIndicator() {
    val path = Path().apply {
        moveTo(size.width / 2, 0f)
        lineTo(size.width / 2 - 15.dp.toPx(), 30.dp.toPx())
        lineTo(size.width / 2 + 15.dp.toPx(), 30.dp.toPx())
        close()
    }
    
    drawPath(
        path = path,
        color = Color.Red
    )
    
    drawPath(
        path = path,
        color = Color.White,
        style = Stroke(width = 2.dp.toPx())
    )
}

/**
 * Tarjeta de resultado
 */
@Composable
private fun ResultCard(prize: LyonSportPrize) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (prize.isSpecial) Color(0xFFFFD700) else prize.color.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (prize.isSpecial) {
                Text(
                    text = "✨🎉✨",
                    fontSize = 24.sp
                )
            }
            
            Text(
                text = prize.emoji,
                fontSize = 48.sp
            )
            
            Text(
                text = "¡FELICITACIONES!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = if (prize.isSpecial) Color.Black else Color.White
                )
            )
            
            Text(
                text = "Has ganado:",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (prize.isSpecial) Color.Black.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.9f)
                )
            )
            
            Text(
                text = prize.description,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (prize.isSpecial) Color.Black else Color.White
                ),
                textAlign = TextAlign.Center
            )
            
            if (prize.isSpecial) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🌟 PREMIO ESPECIAL 🌟",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                )
            }
        }
    }
}

/**
 * Grilla de premios disponibles
 */
@Composable
private fun PrizesGrid(prizes: List<LyonSportPrize>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🏆 PREMIOS LYON SPORT",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            prizes.chunked(2).forEach { rowPrizes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowPrizes.forEach { prize ->
                        PrizeItem(
                            prize = prize,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Relleno si la fila no está completa
                    if (rowPrizes.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * Item individual de premio
 */
@Composable
private fun PrizeItem(
    prize: LyonSportPrize,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = prize.color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = prize.emoji,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = prize.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
                if (prize.isSpecial) {
                    Text(
                        text = "ESPECIAL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = prize.color,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}