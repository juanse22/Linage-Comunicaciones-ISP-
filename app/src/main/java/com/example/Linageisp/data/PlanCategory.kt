package com.example.Linageisp.data

import androidx.compose.ui.graphics.Color

/**
 * 📂 Data classes para categorización de planes de Linage ISP
 * 
 * Organización específica por tipo de servicio con precios exactos
 */

/**
 * Categoría de planes con información visual
 */
data class PlanCategory(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val color: Color,
    val plans: List<CategorizedPlan>,
    val isExpanded: Boolean = false
)

/**
 * Plan categorizado con información específica
 */
data class CategorizedPlan(
    val id: String,
    val nombre: String,
    val velocidad: String,
    val precio: String,
    val precioNumerico: Int, // Para ordenamiento
    val beneficios: List<String>,
    val destacado: Boolean = false,
    val tipo: String = "",
    val descripcionCorta: String = ""
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
 * 🏆 Obtener planes con Win Sports+ (Fútbol)
 */
fun getWinSportsPlans(): PlanCategory {
    return PlanCategory(
        id = "win_sports",
        title = "Planes con Win+",
        description = "Disfruta el fútbol colombiano con Win Sports+ incluido",
        emoji = "🏆",
        color = Color(0xFF9C27B0),
        plans = listOf(
            CategorizedPlan(
                id = "silver_win",
                nombre = "Plan Silver con Win+",
                velocidad = "200 Mbps",
                precio = "$113.000",
                precioNumerico = 113000,
                beneficios = listOf(
                    "200 Mbps de velocidad",
                    "Win Sports+ incluido",
                    "Soporte técnico 24/7",
                    "Instalación gratis"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet rápido + Fútbol colombiano"
            ),
            CategorizedPlan(
                id = "platinum_win",
                nombre = "Plan Platinum con Win+",
                velocidad = "400 Mbps",
                precio = "$141.500",
                precioNumerico = 141500,
                beneficios = listOf(
                    "400 Mbps de velocidad",
                    "Win Sports+ incluido",
                    "Soporte técnico prioritario",
                    "Instalación gratis",
                    "Router de alta gama"
                ),
                destacado = true,
                tipo = "Fibra Óptica",
                descripcionCorta = "Velocidad premium + Fútbol"
            ),
            CategorizedPlan(
                id = "gold_win",
                nombre = "Plan Gold con Win+",
                velocidad = "600 Mbps",
                precio = "$163.000",
                precioNumerico = 163000,
                beneficios = listOf(
                    "600 Mbps de velocidad",
                    "Win Sports+ incluido",
                    "Soporte técnico VIP",
                    "Instalación gratis",
                    "Router empresarial",
                    "IP estática opcional"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Máxima velocidad + Fútbol"
            )
        )
    )
}

/**
 * 📺 Obtener planes Premium con DIRECTV
 */
fun getPremiumPlans(): PlanCategory {
    return PlanCategory(
        id = "premium_directv",
        title = "Planes Premium",
        description = "Entretenimiento completo con DIRECTV incluido",
        emoji = "📺",
        color = Color(0xFF2196F3),
        plans = listOf(
            CategorizedPlan(
                id = "silver_premium",
                nombre = "Plan Silver",
                velocidad = "200 Mbps",
                precio = "$90.000",
                precioNumerico = 90000,
                beneficios = listOf(
                    "200 Mbps de velocidad",
                    "DIRECTV GO incluido",
                    "Soporte técnico 24/7",
                    "Instalación gratis"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet + TV premium"
            ),
            CategorizedPlan(
                id = "platinum_premium",
                nombre = "Plan Platinum",
                velocidad = "400 Mbps",
                precio = "$118.500",
                precioNumerico = 118500,
                beneficios = listOf(
                    "400 Mbps de velocidad",
                    "DIRECTV GO incluido",
                    "Soporte técnico prioritario",
                    "Instalación gratis",
                    "Router de alta gama"
                ),
                destacado = true,
                tipo = "Fibra Óptica",
                descripcionCorta = "Velocidad premium + TV"
            ),
            CategorizedPlan(
                id = "gold_premium",
                nombre = "Plan Gold",
                velocidad = "600 Mbps",
                precio = "$140.000",
                precioNumerico = 140000,
                beneficios = listOf(
                    "600 Mbps de velocidad",
                    "DIRECTV GO incluido",
                    "Soporte técnico VIP",
                    "Instalación gratis",
                    "Router empresarial",
                    "IP estática opcional"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Máxima velocidad + TV"
            )
        )
    )
}

/**
 * ⚡ Obtener planes VIP (Solo Internet + Paramount)
 */
fun getVipPlans(): PlanCategory {
    return PlanCategory(
        id = "vip_internet",
        title = "Planes VIP",
        description = "Solo Internet + Paramount incluido",
        emoji = "⚡",
        color = Color(0xFFF37321),
        plans = listOf(
            CategorizedPlan(
                id = "vip_200",
                nombre = "200 Megas",
                velocidad = "200 Mbps",
                precio = "$65.000",
                precioNumerico = 65000,
                beneficios = listOf(
                    "200 Mbps de velocidad",
                    "Paramount+ incluido",
                    "Soporte técnico 24/7",
                    "Instalación gratis"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet básico + Streaming"
            ),
            CategorizedPlan(
                id = "vip_400",
                nombre = "400 Megas",
                velocidad = "400 Mbps",
                precio = "$70.000",
                precioNumerico = 70000,
                beneficios = listOf(
                    "400 Mbps de velocidad",
                    "Paramount+ incluido",
                    "Soporte técnico 24/7",
                    "Instalación gratis",
                    "Router mejorado"
                ),
                destacado = true,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet rápido + Streaming"
            ),
            CategorizedPlan(
                id = "vip_600",
                nombre = "600 Megas",
                velocidad = "600 Mbps",
                precio = "$85.000",
                precioNumerico = 85000,
                beneficios = listOf(
                    "600 Mbps de velocidad",
                    "Paramount+ incluido",
                    "Soporte técnico prioritario",
                    "Instalación gratis",
                    "Router de alta gama"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet premium + Streaming"
            ),
            CategorizedPlan(
                id = "vip_900",
                nombre = "900 Megas",
                velocidad = "900 Mbps",
                precio = "$100.000",
                precioNumerico = 100000,
                beneficios = listOf(
                    "900 Mbps de velocidad",
                    "Paramount+ incluido",
                    "Soporte técnico VIP",
                    "Instalación gratis",
                    "Router empresarial",
                    "IP estática incluida"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet ultra rápido + Streaming"
            )
        )
    )
}

/**
 * 📺 Obtener planes VIP Max (Internet + TV Digital + Paramount)
 */
fun getVipMaxPlans(): PlanCategory {
    return PlanCategory(
        id = "vip_max",
        title = "Planes VIP Max",
        description = "Internet + TV Digital + Paramount incluido",
        emoji = "📺",
        color = Color(0xFF4CAF50),
        plans = listOf(
            CategorizedPlan(
                id = "vip_max_200",
                nombre = "200 Megas",
                velocidad = "200 Mbps",
                precio = "$80.000",
                precioNumerico = 80000,
                beneficios = listOf(
                    "200 Mbps de velocidad",
                    "TV Digital incluida",
                    "Paramount+ incluido",
                    "Soporte técnico 24/7",
                    "Instalación gratis"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet + TV + Streaming"
            ),
            CategorizedPlan(
                id = "vip_max_400",
                nombre = "400 Megas",
                velocidad = "400 Mbps",
                precio = "$85.000",
                precioNumerico = 85000,
                beneficios = listOf(
                    "400 Mbps de velocidad",
                    "TV Digital incluida",
                    "Paramount+ incluido",
                    "Soporte técnico 24/7",
                    "Instalación gratis",
                    "Router mejorado"
                ),
                destacado = true,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet rápido + TV + Streaming"
            ),
            CategorizedPlan(
                id = "vip_max_600",
                nombre = "600 Megas",
                velocidad = "600 Mbps",
                precio = "$100.000",
                precioNumerico = 100000,
                beneficios = listOf(
                    "600 Mbps de velocidad",
                    "TV Digital incluida",
                    "Paramount+ incluido",
                    "Soporte técnico prioritario",
                    "Instalación gratis",
                    "Router de alta gama"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet premium + TV + Streaming"
            ),
            CategorizedPlan(
                id = "vip_max_900",
                nombre = "900 Megas",
                velocidad = "900 Mbps",
                precio = "$115.000",
                precioNumerico = 115000,
                beneficios = listOf(
                    "900 Mbps de velocidad",
                    "TV Digital incluida",
                    "Paramount+ incluido",
                    "Soporte técnico VIP",
                    "Instalación gratis",
                    "Router empresarial",
                    "IP estática incluida"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet ultra rápido + TV + Streaming"
            )
        )
    )
}

/**
 * 🎬 Obtener planes Netflix
 */
fun getNetflixPlans(): PlanCategory {
    return PlanCategory(
        id = "netflix",
        title = "Planes Netflix",
        description = "Internet con Netflix incluido",
        emoji = "🎬",
        color = Color(0xFFE50914),
        plans = listOf(
            CategorizedPlan(
                id = "netflix_400",
                nombre = "400 Megas",
                velocidad = "400 Mbps",
                precio = "$85.000",
                precioNumerico = 85000,
                beneficios = listOf(
                    "400 Mbps de velocidad",
                    "Netflix incluido",
                    "Soporte técnico 24/7",
                    "Instalación gratis",
                    "Router mejorado"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet rápido + Netflix"
            ),
            CategorizedPlan(
                id = "netflix_600",
                nombre = "600 Megas",
                velocidad = "600 Mbps",
                precio = "$100.000",
                precioNumerico = 100000,
                beneficios = listOf(
                    "600 Mbps de velocidad",
                    "Netflix incluido",
                    "Soporte técnico prioritario",
                    "Instalación gratis",
                    "Router de alta gama"
                ),
                destacado = true,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet premium + Netflix"
            ),
            CategorizedPlan(
                id = "netflix_900",
                nombre = "900 Megas",
                velocidad = "900 Mbps",
                precio = "$115.000",
                precioNumerico = 115000,
                beneficios = listOf(
                    "900 Mbps de velocidad",
                    "Netflix incluido",
                    "Soporte técnico VIP",
                    "Instalación gratis",
                    "Router empresarial",
                    "IP estática incluida"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet ultra rápido + Netflix"
            )
        )
    )
}

/**
 * 📹 Obtener planes con Cámaras
 */
fun getCameraPlans(): PlanCategory {
    return PlanCategory(
        id = "cameras",
        title = "Planes con Cámaras",
        description = "Internet + Sistema de cámaras de seguridad",
        emoji = "📹",
        color = Color(0xFF795548),
        plans = listOf(
            CategorizedPlan(
                id = "cameras_400",
                nombre = "400 Megas",
                velocidad = "400 Mbps",
                precio = "$90.000",
                precioNumerico = 90000,
                beneficios = listOf(
                    "400 Mbps de velocidad",
                    "Sistema de cámaras incluido",
                    "Monitoreo 24/7",
                    "Soporte técnico 24/7",
                    "Instalación gratis",
                    "Router mejorado"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet + Seguridad"
            ),
            CategorizedPlan(
                id = "cameras_600",
                nombre = "600 Megas",
                velocidad = "600 Mbps",
                precio = "$95.000",
                precioNumerico = 95000,
                beneficios = listOf(
                    "600 Mbps de velocidad",
                    "Sistema de cámaras incluido",
                    "Monitoreo 24/7",
                    "Soporte técnico prioritario",
                    "Instalación gratis",
                    "Router de alta gama",
                    "App móvil incluida"
                ),
                destacado = true,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet premium + Seguridad"
            ),
            CategorizedPlan(
                id = "cameras_900",
                nombre = "900 Megas",
                velocidad = "900 Mbps",
                precio = "$105.000",
                precioNumerico = 105000,
                beneficios = listOf(
                    "900 Mbps de velocidad",
                    "Sistema de cámaras avanzado",
                    "Monitoreo 24/7",
                    "Soporte técnico VIP",
                    "Instalación gratis",
                    "Router empresarial",
                    "App móvil premium",
                    "Almacenamiento en la nube"
                ),
                destacado = false,
                tipo = "Fibra Óptica",
                descripcionCorta = "Internet ultra rápido + Seguridad avanzada"
            )
        )
    )
}

/**
 * Obtener todas las categorías de planes organizadas
 */
fun getAllPlanCategories(): List<PlanCategory> {
    return listOf(
        getWinSportsPlans(),
        getPremiumPlans(),
        getVipPlans(),
        getVipMaxPlans(),
        getNetflixPlans(),
        getCameraPlans()
    )
}