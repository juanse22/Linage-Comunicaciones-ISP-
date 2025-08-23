package com.example.Linageisp.network

import com.example.Linageisp.data.Plan
import com.example.Linageisp.data.getAllPlans
import com.example.Linageisp.data.getAllCategories
import com.example.Linageisp.data.getPlansByCategory
import com.example.Linageisp.data.CategoryInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Servicio mejorado para obtener los planes de internet organizados por categorías
 * Usa los datos estáticos optimizados definidos en Plan.kt con categorización completa
 */
class PlanScrapingService {
    
    private val baseUrl = "https://linagecomunicaciones.com/media/servicios/internet/planes.html"
    
    /**
     * Obtiene la lista completa de planes usando los datos oficiales del documento
     * @return Lista de todos los planes disponibles organizados por categorías
     */
    suspend fun fetchPlans(): List<Plan> = withContext(Dispatchers.IO) {
        // Log para debugging
        android.util.Log.d("PlanScrapingService", "🚀 Obteniendo todos los planes...")
        
        val allPlans = getAllPlans()
        
        // Debug: Log statistics
        android.util.Log.d("PlanScrapingService", "📊 Total planes cargados: ${allPlans.size}")
        
        val categories = allPlans.distinctBy { it.category }
        android.util.Log.d("PlanScrapingService", "📂 Categorías disponibles: ${categories.size}")
        
        categories.forEach { plan ->
            val categoryPlans = allPlans.filter { it.category == plan.category }
            android.util.Log.d("PlanScrapingService", "${plan.categoryEmoji} ${plan.categoryTitle}: ${categoryPlans.size} planes")
        }
        
        return@withContext allPlans
    }
    
    /**
     * Obtiene planes filtrados por categoría específica
     * @param categoryId ID de la categoría a filtrar
     * @return Lista de planes de la categoría especificada
     */
    suspend fun fetchPlansByCategory(categoryId: String): List<Plan> = withContext(Dispatchers.IO) {
        android.util.Log.d("PlanScrapingService", "🔍 Filtrando planes para categoría: $categoryId")
        
        val categoryPlans = getPlansByCategory(categoryId)
        
        android.util.Log.d("PlanScrapingService", "✅ Planes encontrados para $categoryId: ${categoryPlans.size}")
        
        return@withContext categoryPlans
    }
    
    /**
     * Obtiene todas las categorías disponibles
     * @return Lista de información de categorías
     */
    suspend fun fetchCategories(): List<CategoryInfo> = withContext(Dispatchers.IO) {
        android.util.Log.d("PlanScrapingService", "📂 Obteniendo todas las categorías...")
        
        val categories = getAllCategories()
        
        android.util.Log.d("PlanScrapingService", "✅ Categorías cargadas: ${categories.size}")
        
        return@withContext categories
    }
}