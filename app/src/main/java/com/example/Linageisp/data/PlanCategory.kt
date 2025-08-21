package com.example.Linageisp.data

import androidx.compose.ui.graphics.Color

/**
 * Data class para representar una categoría de planes expandible
 */
data class PlanCategory(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val color: Color,
    val plans: List<Plan>,
    val isExpanded: Boolean = false
)

/**
 * Estado UI para manejar las categorías de planes agrupadas
 */
data class PlansUiState(
    val categories: List<PlanCategory> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Tipos de categorías de planes basadas en los datos reales de Linage
 */
enum class PlanCategoryType(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val color: Color
) {
    PLANES_WIN_PLUS(
        id = "win_plus",
        title = "Planes Win+ ⚽",
        description = "Planes para verdaderos fanáticos del fútbol - Internet ultra rápido + Win+",
        icon = "⚽",
        color = Color(0xFF9C27B0) // Purple
    ),
    
    PLANES_PREMIUM_DIRECTV(
        id = "premium_directv", 
        title = "Planes Premium",
        description = "Internet ultra rápido + DIRECTV GO - Silver, Platinum, Gold",
        icon = "📺",
        color = Color(0xFF2196F3) // Blue
    ),
    
    PLANES_VIP(
        id = "vip",
        title = "Planes VIP",
        description = "Internet ultra rápido + Paramount - 200, 400, 600, 900 Megas",
        icon = "👑",
        color = Color(0xFFF37321) // LinageOrange
    ),
    
    PLANES_VIP_TV(
        id = "vip_tv",
        title = "Planes VIP Vive La Experiencia Max",
        description = "Internet ultra rápido + TV digital + Paramount",
        icon = "🎬",
        color = Color(0xFF4CAF50) // Green
    ),
    
    PLANES_CAMARAS(
        id = "camaras",
        title = "Planes Cámaras",
        description = "Internet ultra rápido + Cámara de Seguridad",
        icon = "📹",
        color = Color(0xFF795548) // Brown
    ),
    
    PLANES_NETFLIX(
        id = "netflix",
        title = "Planes NETFLIX",
        description = "Internet ultra rápido + Netflix",
        icon = "🍿",
        color = Color(0xFFE50914) // Netflix Red
    )
}

/**
 * Función helper para crear categorías con planes predefinidos
 */
fun createPlanCategories(allPlans: List<Plan>): List<PlanCategory> {
    return PlanCategoryType.values().mapNotNull { categoryType ->
        val categoryPlans = when (categoryType) {
            PlanCategoryType.PLANES_WIN_PLUS -> allPlans.filter { 
                it.nombre.contains("win", ignoreCase = true) ||
                it.beneficios.contains("win sports", ignoreCase = true) ||
                it.beneficios.contains("win+", ignoreCase = true) ||
                it.beneficios.contains("fútbol", ignoreCase = true)
            }
            
            PlanCategoryType.PLANES_PREMIUM_DIRECTV -> allPlans.filter { 
                it.beneficios.contains("directv", ignoreCase = true) &&
                !it.beneficios.contains("win", ignoreCase = true) &&
                (it.nombre.contains("silver", ignoreCase = true) ||
                 it.nombre.contains("platinum", ignoreCase = true) ||
                 it.nombre.contains("gold", ignoreCase = true))
            }
            
            PlanCategoryType.PLANES_VIP -> allPlans.filter {
                it.beneficios.contains("paramount", ignoreCase = true) &&
                !it.beneficios.contains("tv digital", ignoreCase = true) &&
                !it.beneficios.contains("directv", ignoreCase = true) &&
                !it.beneficios.contains("win", ignoreCase = true)
            }
            
            PlanCategoryType.PLANES_VIP_TV -> allPlans.filter {
                it.beneficios.contains("paramount", ignoreCase = true) &&
                it.beneficios.contains("tv digital", ignoreCase = true)
            }
            
            PlanCategoryType.PLANES_CAMARAS -> allPlans.filter {
                it.nombre.contains("cámara", ignoreCase = true) ||
                it.nombre.contains("camara", ignoreCase = true) ||
                it.beneficios.contains("cámara", ignoreCase = true) ||
                it.beneficios.contains("camara", ignoreCase = true) ||
                it.beneficios.contains("seguridad", ignoreCase = true)
            }
            
            PlanCategoryType.PLANES_NETFLIX -> allPlans.filter {
                it.nombre.contains("netflix", ignoreCase = true) ||
                it.beneficios.contains("netflix", ignoreCase = true)
            }
        }
        
        // Solo crear la categoría si tiene planes
        if (categoryPlans.isNotEmpty()) {
            PlanCategory(
                id = categoryType.id,
                title = categoryType.title,
                description = categoryType.description,
                icon = categoryType.icon,
                color = categoryType.color,
                plans = categoryPlans,
                isExpanded = false
            )
        } else null
    }
}