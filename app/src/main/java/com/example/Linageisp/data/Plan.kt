package com.example.Linageisp.data

import androidx.compose.ui.graphics.Color

/**
 * Modelo de datos para representar un plan de internet de Linage ISP
 * 
 * @param id Identificador único del plan
 * @param nombre Nombre del plan (ej: "5 MEGAS", "10 MEGAS")
 * @param velocidad Velocidad del plan como texto (ej: "5 Mbps descarga / 2.5 Mbps subida")
 * @param precio Precio del plan como texto (ej: "$25.000 COP/mes")
 * @param beneficios Beneficios del plan como texto separado por comas
 * @param type Tipo de conexión (Inalámbrico, Fibra, etc.)
 * @param category Categoría del plan para agrupación
 * @param categoryTitle Título de la categoría
 * @param categoryDescription Descripción de la categoría
 * @param categoryEmoji Emoji representativo de la categoría
 * @param categoryColor Color de la categoría
 * @param precioNumerico Precio numérico para ordenamiento
 * @param destacado Si el plan está destacado
 * @param descripcionCorta Descripción corta del plan
 */
data class Plan(
    val id: String,
    val nombre: String,
    val velocidad: String,
    val precio: String,
    val beneficios: String,
    val type: String = "",
    val category: String = "",
    val categoryTitle: String = "",
    val categoryDescription: String = "",
    val categoryEmoji: String = "",
    val categoryColor: Color = Color.Gray,
    val precioNumerico: Int = 0,
    val destacado: Boolean = false,
    val descripcionCorta: String = ""
) {
    /**
     * Función auxiliar para obtener la velocidad de descarga extraída del texto
     * @return Velocidad de descarga en Mbps como entero, o 0 si no se puede extraer
     */
    fun getDownloadSpeed(): Int {
        return try {
            val regex = """(\d+)\s*Mbps\s*descarga""".toRegex(RegexOption.IGNORE_CASE)
            regex.find(velocidad)?.groupValues?.get(1)?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Función auxiliar para obtener la velocidad de subida extraída del texto
     * @return Velocidad de subida en Mbps como entero, o 0 si no se puede extraer
     */
    fun getUploadSpeed(): Int {
        return try {
            val regex = """(\d+(?:\.\d+)?)\s*Mbps\s*subida""".toRegex(RegexOption.IGNORE_CASE)
            regex.find(velocidad)?.groupValues?.get(1)?.toDoubleOrNull()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Función auxiliar para obtener los beneficios como lista
     * @return Lista de beneficios separados y limpios
     */
    fun getBeneficiosList(): List<String> {
        return beneficios.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
    
    /**
     * Función auxiliar para generar el mensaje de WhatsApp
     * @return Mensaje prellenado para WhatsApp con información del plan
     */
    fun getWhatsAppMessage(): String {
        return "Hola, estoy interesado en el plan $nombre. " +
                "Velocidad: $velocidad. " +
                "¿Podrían proporcionarme más información y confirmar el precio? Gracias."
    }
}

/**
 * Tipos de planes disponibles
 */
enum class PlanType(val displayName: String) {
    WIRELESS_AND_FIBER("Internet Inalámbrico y Fibra"),
    FIBER_ONLY("Internet por Fibra"),
    WIRELESS_ONLY("Internet Inalámbrico")
}

/**
 * 🏆 Obtener planes con Win Sports+ (Fútbol)
 */
fun getWinSportsPlansNew(): List<Plan> {
    val category = "win_sports"
    val categoryTitle = "Planes con Win+"
    val categoryDescription = "Disfruta el fútbol colombiano con Win Sports+ incluido"
    val categoryEmoji = "🏆"
    val categoryColor = Color(0xFF9C27B0)
    
    return listOf(
        Plan(
            id = "silver_win",
            nombre = "Plan Silver con Win+",
            velocidad = "200 Mbps",
            precio = "$113.000",
            beneficios = "200 Mbps de velocidad, Win Sports+ incluido, Soporte técnico 24/7, Instalación gratis",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 113000,
            destacado = false,
            descripcionCorta = "Internet rápido + Fútbol colombiano"
        ),
        Plan(
            id = "platinum_win",
            nombre = "Plan Platinum con Win+",
            velocidad = "400 Mbps",
            precio = "$141.500",
            beneficios = "400 Mbps de velocidad, Win Sports+ incluido, Soporte técnico prioritario, Instalación gratis, Router de alta gama",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 141500,
            destacado = true,
            descripcionCorta = "Velocidad premium + Fútbol"
        ),
        Plan(
            id = "gold_win",
            nombre = "Plan Gold con Win+",
            velocidad = "600 Mbps",
            precio = "$163.000",
            beneficios = "600 Mbps de velocidad, Win Sports+ incluido, Soporte técnico VIP, Instalación gratis, Router empresarial, IP estática opcional",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 163000,
            destacado = false,
            descripcionCorta = "Máxima velocidad + Fútbol"
        )
    )
}

/**
 * 📺 Obtener planes Premium con DIRECTV
 */
fun getPremiumPlansNew(): List<Plan> {
    val category = "premium_directv"
    val categoryTitle = "Planes Premium"
    val categoryDescription = "Entretenimiento completo con DIRECTV incluido"
    val categoryEmoji = "📺"
    val categoryColor = Color(0xFF2196F3)
    
    return listOf(
        Plan(
            id = "silver_premium",
            nombre = "Plan Silver",
            velocidad = "200 Mbps",
            precio = "$90.000",
            beneficios = "200 Mbps de velocidad, DIRECTV GO incluido, Soporte técnico 24/7, Instalación gratis",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 90000,
            destacado = false,
            descripcionCorta = "Internet + TV premium"
        ),
        Plan(
            id = "platinum_premium",
            nombre = "Plan Platinum",
            velocidad = "400 Mbps",
            precio = "$118.500",
            beneficios = "400 Mbps de velocidad, DIRECTV GO incluido, Soporte técnico prioritario, Instalación gratis, Router de alta gama",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 118500,
            destacado = true,
            descripcionCorta = "Velocidad premium + TV"
        ),
        Plan(
            id = "gold_premium",
            nombre = "Plan Gold",
            velocidad = "600 Mbps",
            precio = "$140.000",
            beneficios = "600 Mbps de velocidad, DIRECTV GO incluido, Soporte técnico VIP, Instalación gratis, Router empresarial, IP estática opcional",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 140000,
            destacado = false,
            descripcionCorta = "Máxima velocidad + TV"
        )
    )
}

/**
 * ⚡ Obtener planes VIP (Solo Internet + Paramount)
 */
fun getVipPlansNew(): List<Plan> {
    val category = "vip_internet"
    val categoryTitle = "Planes VIP"
    val categoryDescription = "Solo Internet + Paramount incluido"
    val categoryEmoji = "⚡"
    val categoryColor = Color(0xFFF37321)
    
    return listOf(
        Plan(
            id = "vip_200",
            nombre = "200 Megas",
            velocidad = "200 Mbps",
            precio = "$65.000",
            beneficios = "200 Mbps de velocidad, Paramount+ incluido, Soporte técnico 24/7, Instalación gratis",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 65000,
            destacado = false,
            descripcionCorta = "Internet básico + Streaming"
        ),
        Plan(
            id = "vip_400",
            nombre = "400 Megas",
            velocidad = "400 Mbps",
            precio = "$70.000",
            beneficios = "400 Mbps de velocidad, Paramount+ incluido, Soporte técnico 24/7, Instalación gratis, Router mejorado",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 70000,
            destacado = true,
            descripcionCorta = "Internet rápido + Streaming"
        ),
        Plan(
            id = "vip_600",
            nombre = "600 Megas",
            velocidad = "600 Mbps",
            precio = "$85.000",
            beneficios = "600 Mbps de velocidad, Paramount+ incluido, Soporte técnico prioritario, Instalación gratis, Router de alta gama",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 85000,
            destacado = false,
            descripcionCorta = "Internet premium + Streaming"
        ),
        Plan(
            id = "vip_900",
            nombre = "900 Megas",
            velocidad = "900 Mbps",
            precio = "$100.000",
            beneficios = "900 Mbps de velocidad, Paramount+ incluido, Soporte técnico VIP, Instalación gratis, Router empresarial, IP estática incluida",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 100000,
            destacado = false,
            descripcionCorta = "Internet ultra rápido + Streaming"
        )
    )
}

/**
 * 📺 Obtener planes VIP Max (Internet + TV Digital + Paramount)
 */
fun getVipMaxPlansNew(): List<Plan> {
    val category = "vip_max"
    val categoryTitle = "Planes VIP Max"
    val categoryDescription = "Internet + TV Digital + Paramount incluido"
    val categoryEmoji = "📺"
    val categoryColor = Color(0xFF4CAF50)
    
    return listOf(
        Plan(
            id = "vip_max_200",
            nombre = "200 Megas",
            velocidad = "200 Mbps",
            precio = "$80.000",
            beneficios = "200 Mbps de velocidad, TV Digital incluida, Paramount+ incluido, Soporte técnico 24/7, Instalación gratis",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 80000,
            destacado = false,
            descripcionCorta = "Internet + TV + Streaming"
        ),
        Plan(
            id = "vip_max_400",
            nombre = "400 Megas",
            velocidad = "400 Mbps",
            precio = "$85.000",
            beneficios = "400 Mbps de velocidad, TV Digital incluida, Paramount+ incluido, Soporte técnico 24/7, Instalación gratis, Router mejorado",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 85000,
            destacado = true,
            descripcionCorta = "Internet rápido + TV + Streaming"
        ),
        Plan(
            id = "vip_max_600",
            nombre = "600 Megas",
            velocidad = "600 Mbps",
            precio = "$100.000",
            beneficios = "600 Mbps de velocidad, TV Digital incluida, Paramount+ incluido, Soporte técnico prioritario, Instalación gratis, Router de alta gama",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 100000,
            destacado = false,
            descripcionCorta = "Internet premium + TV + Streaming"
        ),
        Plan(
            id = "vip_max_900",
            nombre = "900 Megas",
            velocidad = "900 Mbps",
            precio = "$115.000",
            beneficios = "900 Mbps de velocidad, TV Digital incluida, Paramount+ incluido, Soporte técnico VIP, Instalación gratis, Router empresarial, IP estática incluida",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 115000,
            destacado = false,
            descripcionCorta = "Internet ultra rápido + TV + Streaming"
        )
    )
}

/**
 * 🎬 Obtener planes Netflix
 */
fun getNetflixPlansNew(): List<Plan> {
    val category = "netflix"
    val categoryTitle = "Planes Netflix"
    val categoryDescription = "Internet con Netflix incluido"
    val categoryEmoji = "🎬"
    val categoryColor = Color(0xFFE50914)
    
    return listOf(
        Plan(
            id = "netflix_400",
            nombre = "400 Megas",
            velocidad = "400 Mbps",
            precio = "$85.000",
            beneficios = "400 Mbps de velocidad, Netflix incluido, Soporte técnico 24/7, Instalación gratis, Router mejorado",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 85000,
            destacado = false,
            descripcionCorta = "Internet rápido + Netflix"
        ),
        Plan(
            id = "netflix_600",
            nombre = "600 Megas",
            velocidad = "600 Mbps",
            precio = "$100.000",
            beneficios = "600 Mbps de velocidad, Netflix incluido, Soporte técnico prioritario, Instalación gratis, Router de alta gama",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 100000,
            destacado = true,
            descripcionCorta = "Internet premium + Netflix"
        ),
        Plan(
            id = "netflix_900",
            nombre = "900 Megas",
            velocidad = "900 Mbps",
            precio = "$115.000",
            beneficios = "900 Mbps de velocidad, Netflix incluido, Soporte técnico VIP, Instalación gratis, Router empresarial, IP estática incluida",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 115000,
            destacado = false,
            descripcionCorta = "Internet ultra rápido + Netflix"
        )
    )
}

/**
 * 📹 Obtener planes con Cámaras
 */
fun getCameraPlansNew(): List<Plan> {
    val category = "cameras"
    val categoryTitle = "Planes con Cámaras"
    val categoryDescription = "Internet + Sistema de cámaras de seguridad"
    val categoryEmoji = "📹"
    val categoryColor = Color(0xFF795548)
    
    return listOf(
        Plan(
            id = "cameras_400",
            nombre = "400 Megas",
            velocidad = "400 Mbps",
            precio = "$90.000",
            beneficios = "400 Mbps de velocidad, Sistema de cámaras incluido, Monitoreo 24/7, Soporte técnico 24/7, Instalación gratis, Router mejorado",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 90000,
            destacado = false,
            descripcionCorta = "Internet + Seguridad"
        ),
        Plan(
            id = "cameras_600",
            nombre = "600 Megas",
            velocidad = "600 Mbps",
            precio = "$95.000",
            beneficios = "600 Mbps de velocidad, Sistema de cámaras incluido, Monitoreo 24/7, Soporte técnico prioritario, Instalación gratis, Router de alta gama, App móvil incluida",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 95000,
            destacado = true,
            descripcionCorta = "Internet premium + Seguridad"
        ),
        Plan(
            id = "cameras_900",
            nombre = "900 Megas",
            velocidad = "900 Mbps",
            precio = "$105.000",
            beneficios = "900 Mbps de velocidad, Sistema de cámaras avanzado, Monitoreo 24/7, Soporte técnico VIP, Instalación gratis, Router empresarial, App móvil premium, Almacenamiento en la nube",
            type = "Fibra Óptica",
            category = category,
            categoryTitle = categoryTitle,
            categoryDescription = categoryDescription,
            categoryEmoji = categoryEmoji,
            categoryColor = categoryColor,
            precioNumerico = 105000,
            destacado = false,
            descripcionCorta = "Internet ultra rápido + Seguridad avanzada"
        )
    )
}

/**
 * Obtener todos los planes organizados
 */
fun getAllPlans(): List<Plan> {
    return getWinSportsPlansNew() + getPremiumPlansNew() + getVipPlansNew() + 
           getVipMaxPlansNew() + getNetflixPlansNew() + getCameraPlansNew()
}

/**
 * Obtener planes agrupados por categoría
 */
fun getPlansByCategory(): Map<String, List<Plan>> {
    return getAllPlans().groupBy { it.category }
}

/**
 * Obtener información de categorías únicas
 */
data class CategoryInfo(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val color: Color
)

fun getAllCategories(): List<CategoryInfo> {
    return getAllPlans()
        .distinctBy { it.category }
        .map { plan ->
            CategoryInfo(
                id = plan.category,
                title = plan.categoryTitle,
                description = plan.categoryDescription,
                emoji = plan.categoryEmoji,
                color = plan.categoryColor
            )
        }
}

/**
 * Compatibility functions for PlansScreen and CategoryPlansScreen
 * These emulate the old PlanCategory interface using the new Plan-based data
 */

// For compatibility with existing PlansScreen code
fun getAllPlanCategories(): List<CategoryInfo> {
    return getAllCategories()
}

// Get plans by category ID
fun getPlansByCategory(categoryId: String): List<Plan> {
    return getAllPlans().filter { it.category == categoryId }
}