@file:OptIn(ExperimentalAnimationApi::class)

package com.example.Linageisp.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Linageisp.ui.theme.LinageOrange
import com.example.Linageisp.ui.theme.LinageGrayLight
import androidx.compose.runtime.getValue
import kotlin.math.*

data class SpeedTestResult(
    val download: Float = 0f,
    val upload: Float = 0f,
    val ping: Int = 0
)

enum class SpeedTestState {
    IDLE, TESTING, COMPLETED
}

@Composable
fun SpeedMeter(
    currentSpeed: Float,
    maxSpeed: Float = 100f,
    state: SpeedTestState,
    modifier: Modifier = Modifier
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = currentSpeed,
        animationSpec = tween(
            durationMillis = 1000,
            easing = EaseInOutCubic
        ),
        label = "speedAnimation"
    )

    val sweepAngle = (animatedSpeed / maxSpeed) * 270f

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val center = center
            val radius = size.minDimension / 2.5f
            val strokeWidth = 20.dp.toPx()

            // Background arc
            drawArc(
                color = LinageGrayLight,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )

            // Speed arc
            val gradient = Brush.sweepGradient(
                0f to Color(0xFF4CAF50),
                0.3f to Color(0xFFFFC107),
                0.7f to LinageOrange,
                1f to Color(0xFFF44336),
                center = center
            )

            drawArc(
                brush = gradient,
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )

            // Draw speed indicators
            val numTicks = 6
            for (i in 0..numTicks) {
                val angle = (135f + (270f * i / numTicks)) * PI / 180
                val startRadius = radius - strokeWidth / 2 - 10.dp.toPx()
                val endRadius = startRadius - 15.dp.toPx()
                
                val startX = center.x + cos(angle).toFloat() * startRadius
                val startY = center.y + sin(angle).toFloat() * startRadius
                val endX = center.x + cos(angle).toFloat() * endRadius
                val endY = center.y + sin(angle).toFloat() * endRadius

                drawLine(
                    color = Color.Gray,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Needle
            if (state != SpeedTestState.IDLE) {
                val needleAngle = (135f + sweepAngle) * PI / 180
                val needleLength = radius - strokeWidth / 2 - 30.dp.toPx()
                
                val needleEndX = center.x + cos(needleAngle).toFloat() * needleLength
                val needleEndY = center.y + sin(needleAngle).toFloat() * needleLength

                // Needle line
                drawLine(
                    color = LinageOrange,
                    start = center,
                    end = Offset(needleEndX, needleEndY),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Center circle
                drawCircle(
                    color = LinageOrange,
                    radius = 12.dp.toPx(),
                    center = center
                )
            }
        }

        // Speed text in center
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format("%.1f", animatedSpeed),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = LinageOrange
                )
            )
            Text(
                text = "Mbps",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
fun LoadingSpeedMeter(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loadingAnimation")
    
    val animatedValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAnimation"
    )

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val center = center
            val radius = size.minDimension / 2.5f
            val strokeWidth = 20.dp.toPx()

            // Background circle
            drawCircle(
                color = LinageGrayLight,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Animated loading arc
            drawArc(
                color = LinageOrange,
                startAngle = animatedValue,
                sweepAngle = 90f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
        }

        Text(
            text = "Probando...",
            style = MaterialTheme.typography.titleMedium.copy(
                color = LinageOrange,
                fontWeight = FontWeight.Medium
            )
        )
    }
}