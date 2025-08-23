package com.example.Linageisp.data

import androidx.compose.ui.graphics.Color

/**
 * Data class para representar un plan de internet de Linage ISP
 * Estructura reorganizada con datos EXACTOS del documento oficial
 */
data class Plan(
    val id: String,
    val nombre: String,
    val velocidad: String,
    val precio: String,
    val beneficios: String,
    val type: String = "Fibra Óptica",
    val category: String,
    val categoryTitle: String,
    val categoryDescription: String,
    val categoryEmoji: String,
    val categoryColor: Color,
    val precioNumerico: Int,
    val destacado: Boolean = false,
    val descripcionCorta: String = ""
) {
    fun getBeneficiosList(): List<String> {
        return beneficios.split(",").map { it.trim() }
    }
}

/**
 * Data class para información de categorías
 */
data class CategoryInfo(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val color: Color
)

/**
 * DATOS EXACTOS SEGÚN DOCUMENTO OFICIAL
 * Todos los planes con precios y beneficios exactos
 */
fun getAllPlans(): List<Plan> {
    return listOf(
        // ===========================================
        // 1. PLANES WIN+ FÚTBOL - 3 PLANES
        // ===========================================
        Plan(
            id = "silver_win",
            nombre = "Plan Silver con Win+",
            velocidad = "400 Mbps",
            precio = "$113.000",
            beneficios = "400 Megas Fibra Óptica, DIRECTV GO Flex (20 canales), 2 Pantallas Simultáneas, 1.000 series y películas contenido on demand, 3 meses de PARAMOUNT gratis, Canal exclusivo Win+ Futbol, 1 Pantalla en simultáneo, Instalación Gratis",
            type = "Fibra Óptica + Win+",
            category = "win_futbol",
            categoryTitle = "Planes para verdaderos fanáticos del fútbol",
            categoryDescription = "Internet ultra rápido + Win+",
            categoryEmoji = "⚽",
            categoryColor = Color(0xFF4CAF50),
            precioNumerico = 113000,
            destacado = false,
            descripcionCorta = "Internet + Win+ Fútbol"
        ),
        Plan(
            id = "platinum_win",
            nombre = "Plan Platinum con Win+",
            velocidad = "400 Mbps",
            precio = "$141.500",
            beneficios = "400 Megas Fibra Óptica, DIRECTV GO Básico (40 canales), 2 Pantallas Simultáneas, 6.000 series y películas contenido on demand, 3 meses de PARAMOUNT gratis, Canal exclusivo Win+ Futbol, 1 Pantalla en simultáneo, Instalación Gratis",
            type = "Fibra Óptica + Win+",
            category = "win_futbol",
            categoryTitle = "Planes para verdaderos fanáticos del fútbol",
            categoryDescription = "Internet ultra rápido + Win+",
            categoryEmoji = "⚽",
            categoryColor = Color(0xFF4CAF50),
            precioNumerico = 141500,
            destacado = true,
            descripcionCorta = "Internet + Win+ Fútbol Premium"
        ),
        Plan(
            id = "gold_win",
            nombre = "Plan Gold con Win+",
            velocidad = "400 Mbps",
            precio = "$163.000",
            beneficios = "400 Megas Fibra Óptica, DIRECTV GO FULL (80 canales), 4 Pantallas Simultáneas, 10.000 series y películas contenido on demand, 3 meses de PARAMOUNT gratis, Canal exclusivo Win+ Futbol, 1 Pantalla en simultáneo, Instalación Gratis",
            type = "Fibra Óptica + Win+",
            category = "win_futbol",
            categoryTitle = "Planes para verdaderos fanáticos del fútbol",
            categoryDescription = "Internet ultra rápido + Win+",
            categoryEmoji = "⚽",
            categoryColor = Color(0xFF4CAF50),
            precioNumerico = 163000,
            destacado = false,
            descripcionCorta = "Internet + Win+ Fútbol Gold"
        ),

        // ===========================================
        // 2. PLANES PREMIUM - 3 PLANES
        // ===========================================
        Plan(
            id = "premium_silver",
            nombre = "Plan Silver",
            velocidad = "400 Mbps",
            precio = "$90.000",
            beneficios = "400 Megas Fibra Óptica, DIRECTV GO Flex (20 canales), 2 Pantalla Simultánea, 1.000 series y películas contenido on demand, 3 meses de PARAMOUNT gratis, Instalación Gratis",
            type = "Fibra Óptica + DIRECTV GO",
            category = "premium_directv",
            categoryTitle = "Planes Premium",
            categoryDescription = "Internet ultra rápido + DIRECTV GO",
            categoryEmoji = "📺",
            categoryColor = Color(0xFFFF9800),
            precioNumerico = 90000,
            destacado = false,
            descripcionCorta = "Internet + DIRECTV GO"
        ),
        Plan(
            id = "premium_platinum",
            nombre = "Plan Platinum",
            velocidad = "400 Mbps",
            precio = "$118.500",
            beneficios = "400 Megas Fibra Óptica, DIRECTV GO Básico (40 canales), 2 Pantalla Simultánea, 6.000 series y películas contenido on demand, 3 meses de PARAMOUNT gratis, Instalación Gratis",
            type = "Fibra Óptica + DIRECTV GO",
            category = "premium_directv",
            categoryTitle = "Planes Premium",
            categoryDescription = "Internet ultra rápido + DIRECTV GO",
            categoryEmoji = "📺",
            categoryColor = Color(0xFFFF9800),
            precioNumerico = 118500,
            destacado = true,
            descripcionCorta = "Internet + DIRECTV GO Premium"
        ),
        Plan(
            id = "premium_gold",
            nombre = "Plan Gold",
            velocidad = "400 Mbps",
            precio = "$140.000",
            beneficios = "400 Megas Fibra Óptica, DIRECTV GO FULL (80 canales), 4 Pantalla Simultánea, 10.000 series y películas contenido on demand, 3 meses de PARAMOUNT gratis, Instalación Gratis",
            type = "Fibra Óptica + DIRECTV GO",
            category = "premium_directv",
            categoryTitle = "Planes Premium",
            categoryDescription = "Internet ultra rápido + DIRECTV GO",
            categoryEmoji = "📺",
            categoryColor = Color(0xFFFF9800),
            precioNumerico = 140000,
            destacado = false,
            descripcionCorta = "Internet + DIRECTV GO Gold"
        ),

        // ===========================================
        // 3. PLANES VIP - 4 PLANES
        // ===========================================
        Plan(
            id = "vip_200",
            nombre = "200 Megas",
            velocidad = "200 Mbps",
            precio = "$65.000",
            beneficios = "200 Megas Fibra Óptica, 1 Mes de Paramount Gratis, 1 Pantalla Simultánea, Instalación Gratis",
            type = "Fibra Óptica + Paramount",
            category = "vip_paramount",
            categoryTitle = "Planes VIP",
            categoryDescription = "Internet ultra rápido + Paramount",
            categoryEmoji = "🎬",
            categoryColor = Color(0xFF9C27B0),
            precioNumerico = 65000,
            destacado = false,
            descripcionCorta = "Internet + Paramount"
        ),
        Plan(
            id = "vip_400",
            nombre = "400 Megas",
            velocidad = "400 Mbps",
            precio = "$70.000",
            beneficios = "400 Megas Fibra Óptica, 1 Mes de Paramount Gratis, 1 Pantalla Simultánea, Instalación Gratis",
            type = "Fibra Óptica + Paramount",
            category = "vip_paramount",
            categoryTitle = "Planes VIP",
            categoryDescription = "Internet ultra rápido + Paramount",
            categoryEmoji = "🎬",
            categoryColor = Color(0xFF9C27B0),
            precioNumerico = 70000,
            destacado = true,
            descripcionCorta = "Internet + Paramount"
        ),
        Plan(
            id = "vip_600",
            nombre = "600 Megas",
            velocidad = "600 Mbps",
            precio = "$85.000",
            beneficios = "600 Megas Fibra Óptica, 1 Mes de Paramount Gratis, 1 Pantalla Simultánea, Instalación Gratis",
            type = "Fibra Óptica + Paramount",
            category = "vip_paramount",
            categoryTitle = "Planes VIP",
            categoryDescription = "Internet ultra rápido + Paramount",
            categoryEmoji = "🎬",
            categoryColor = Color(0xFF9C27B0),
            precioNumerico = 85000,
            destacado = false,
            descripcionCorta = "Internet + Paramount"
        ),
        Plan(
            id = "vip_900",
            nombre = "900 Megas",
            velocidad = "900 Mbps",
            precio = "$100.000",
            beneficios = "900 Megas Fibra Óptica, 1 Mes de Paramount Gratis, 1 Pantalla Simultánea, Instalación Gratis",
            type = "Fibra Óptica + Paramount",
            category = "vip_paramount",
            categoryTitle = "Planes VIP",
            categoryDescription = "Internet ultra rápido + Paramount",
            categoryEmoji = "🎬",
            categoryColor = Color(0xFF9C27B0),
            precioNumerico = 100000,
            destacado = false,
            descripcionCorta = "Internet + Paramount"
        ),

        // ===========================================
        // 4. PLANES VIP MAX - 4 PLANES
        // ===========================================
        Plan(
            id = "vip_max_200",
            nombre = "200 Megas",
            velocidad = "200 Mbps",
            precio = "$80.000",
            beneficios = "200 Megas Fibra Óptica, 1 Mes de Paramount Gratis, 1 Pantalla Simultánea, Tv digital de 120 canales, Instalación Gratis",
            type = "Fibra Óptica + TV + Paramount",
            category = "vip_max_tv",
            categoryTitle = "Planes VIP Vive La Experiencia Max",
            categoryDescription = "Internet ultra rápido + TV digital + Paramount",
            categoryEmoji = "📺",
            categoryColor = Color(0xFF673AB7),
            precioNumerico = 80000,
            destacado = false,
            descripcionCorta = "Internet + TV + Paramount"
        ),
        Plan(
            id = "vip_max_400",
            nombre = "400 Megas",
            velocidad = "400 Mbps",
            precio = "$85.000",
            beneficios = "400 Megas Fibra Óptica, 1 Mes de Paramount Gratis, 1 Pantalla Simultánea, Tv digital de 120 canales, Instalación Gratis",
            type = "Fibra Óptica + TV + Paramount",
            category = "vip_max_tv",
            categoryTitle = "Planes VIP Vive La Experiencia Max",
            categoryDescription = "Internet ultra rápido + TV digital + Paramount",
            categoryEmoji = "📺",
            categoryColor = Color(0xFF673AB7),
            precioNumerico = 85000,
            destacado = true,
            descripcionCorta = "Internet + TV + Paramount"
        ),
        Plan(
            id = "vip_max_600",
            nombre = "600 Megas",
            velocidad = "600 Mbps",
            precio = "$100.000",
            beneficios = "600 Megas Fibra Óptica, 1 Mes de Paramount Gratis, 1 Pantalla Simultánea, Tv digital de 120 canales, Instalación Gratis",
            type = "Fibra Óptica + TV + Paramount",
            category = "vip_max_tv",
            categoryTitle = "Planes VIP Vive La Experiencia Max",
            categoryDescription = "Internet ultra rápido + TV digital + Paramount",
            categoryEmoji = "📺",
            categoryColor = Color(0xFF673AB7),
            precioNumerico = 100000,
            destacado = false,
            descripcionCorta = "Internet + TV + Paramount"
        ),
        Plan(
            id = "vip_max_900",
            nombre = "900 Megas",
            velocidad = "900 Mbps",
            precio = "$115.000",
            beneficios = "900 Megas Fibra Óptica, 1 Mes de Paramount Gratis, 1 Pantalla Simultánea, Tv digital de 120 canales, Instalación Gratis",
            type = "Fibra Óptica + TV + Paramount",
            category = "vip_max_tv",
            categoryTitle = "Planes VIP Vive La Experiencia Max",
            categoryDescription = "Internet ultra rápido + TV digital + Paramount",
            categoryEmoji = "📺",
            categoryColor = Color(0xFF673AB7),
            precioNumerico = 115000,
            destacado = false,
            descripcionCorta = "Internet + TV + Paramount"
        ),

        // ===========================================
        // 5. PLANES CÁMARAS - 3 PLANES
        // ===========================================
        Plan(
            id = "camaras_400",
            nombre = "Cámaras 400 MEGAS",
            velocidad = "400 Mbps",
            precio = "$90.000",
            beneficios = "400 Megas Fibra Óptica, 1 meses de PARAMOUNT gratis, 1 Pantallas Simultáneas, 1 Cámara de seguridad, Instalación Gratis",
            type = "Fibra Óptica + Cámaras",
            category = "camaras_seguridad",
            categoryTitle = "Planes Cámaras",
            categoryDescription = "Internet ultra rápido + Cámara de Seguridad",
            categoryEmoji = "📹",
            categoryColor = Color(0xFF607D8B),
            precioNumerico = 90000,
            destacado = false,
            descripcionCorta = "Internet + Seguridad"
        ),
        Plan(
            id = "camaras_600",
            nombre = "Cámaras 600 MEGAS",
            velocidad = "600 Mbps",
            precio = "$95.000",
            beneficios = "600 Megas Fibra Óptica, 1 meses de PARAMOUNT gratis, 1 Pantallas Simultáneas, 1 Cámara de seguridad, Instalación Gratis",
            type = "Fibra Óptica + Cámaras",
            category = "camaras_seguridad",
            categoryTitle = "Planes Cámaras",
            categoryDescription = "Internet ultra rápido + Cámara de Seguridad",
            categoryEmoji = "📹",
            categoryColor = Color(0xFF607D8B),
            precioNumerico = 95000,
            destacado = true,
            descripcionCorta = "Internet + Seguridad"
        ),
        Plan(
            id = "camaras_900",
            nombre = "Cámaras 900 MEGAS",
            velocidad = "900 Mbps",
            precio = "$105.000",
            beneficios = "900 Megas Fibra Óptica, 1 meses de PARAMOUNT gratis, 1 Pantallas Simultáneas, 1 Cámara de seguridad, Instalación Gratis",
            type = "Fibra Óptica + Cámaras",
            category = "camaras_seguridad",
            categoryTitle = "Planes Cámaras",
            categoryDescription = "Internet ultra rápido + Cámara de Seguridad",
            categoryEmoji = "📹",
            categoryColor = Color(0xFF607D8B),
            precioNumerico = 105000,
            destacado = false,
            descripcionCorta = "Internet + Seguridad"
        ),

        // ===========================================
        // 6. PLANES NETFLIX - 3 PLANES
        // ===========================================
        Plan(
            id = "netflix_400",
            nombre = "NETFLIX 400 MEGAS",
            velocidad = "400 Mbps",
            precio = "$85.000",
            beneficios = "400 Megas Fibra Óptica, NETFLIX (1 Dispositivo HD), Instalación Gratis",
            type = "Fibra Óptica + Netflix",
            category = "netflix_plans",
            categoryTitle = "Planes NETFLIX",
            categoryDescription = "Internet ultra rápido + Netflix",
            categoryEmoji = "🎥",
            categoryColor = Color(0xFFE91E63),
            precioNumerico = 85000,
            destacado = false,
            descripcionCorta = "Internet + Netflix"
        ),
        Plan(
            id = "netflix_600",
            nombre = "NETFLIX 600 MEGAS",
            velocidad = "600 Mbps",
            precio = "$100.000",
            beneficios = "600 Megas Fibra Óptica, NETFLIX (1 Dispositivo HD), Instalación Gratis",
            type = "Fibra Óptica + Netflix",
            category = "netflix_plans",
            categoryTitle = "Planes NETFLIX",
            categoryDescription = "Internet ultra rápido + Netflix",
            categoryEmoji = "🎥",
            categoryColor = Color(0xFFE91E63),
            precioNumerico = 100000,
            destacado = true,
            descripcionCorta = "Internet + Netflix"
        ),
        Plan(
            id = "netflix_900",
            nombre = "NETFLIX 900 MEGAS",
            velocidad = "900 Mbps",
            precio = "$115.000",
            beneficios = "900 Megas Fibra Óptica, NETFLIX (1 Dispositivo HD), Instalación Gratis",
            type = "Fibra Óptica + Netflix",
            category = "netflix_plans",
            categoryTitle = "Planes NETFLIX",
            categoryDescription = "Internet ultra rápido + Netflix",
            categoryEmoji = "🎥",
            categoryColor = Color(0xFFE91E63),
            precioNumerico = 115000,
            destacado = false,
            descripcionCorta = "Internet + Netflix"
        )
    )
}

/**
 * Obtener todas las categorías disponibles
 */
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
 * Obtener planes por categoría específica
 */
fun getPlansByCategory(categoryId: String): List<Plan> {
    val allPlans = getAllPlans()
    val filteredPlans = allPlans.filter { it.category == categoryId }
    
    // DEBUG: Log para verificar el filtrado
    android.util.Log.d("getPlansByCategory", "🔍 Buscando planes para: $categoryId")
    android.util.Log.d("getPlansByCategory", "📊 Total planes disponibles: ${allPlans.size}")
    android.util.Log.d("getPlansByCategory", "✅ Planes filtrados: ${filteredPlans.size}")
    
    filteredPlans.forEach { plan ->
        android.util.Log.d("getPlansByCategory", "📋 ${plan.nombre} - ${plan.precio} - categoria: ${plan.category}")
    }
    
    return filteredPlans
}