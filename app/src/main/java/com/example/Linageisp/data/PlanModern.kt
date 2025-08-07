package com.example.Linageisp.data

/**
 * Modelo de datos para un plan de internet moderno
 */
data class PlanModern(
    val id: String,
    val speed: String, // "200 Mbps"
    val price: String, // "$65.000"
    val originalPrice: String? = null, // Precio anterior si hay descuento
    val benefits: List<String>,
    val isRecommended: Boolean = false,
    val isPopular: Boolean = false,
    val color: PlanColor = PlanColor.Orange
)

/**
 * Colores predefinidos para los planes
 */
enum class PlanColor(val primary: androidx.compose.ui.graphics.Color, val secondary: androidx.compose.ui.graphics.Color) {
    Orange(androidx.compose.ui.graphics.Color(0xFFF37321), androidx.compose.ui.graphics.Color(0xFFFF9B47)),
    Blue(androidx.compose.ui.graphics.Color(0xFF2196F3), androidx.compose.ui.graphics.Color(0xFF64B5F6)),
    Green(androidx.compose.ui.graphics.Color(0xFF4CAF50), androidx.compose.ui.graphics.Color(0xFF81C784)),
    Purple(androidx.compose.ui.graphics.Color(0xFF9C27B0), androidx.compose.ui.graphics.Color(0xFFBA68C8))
}