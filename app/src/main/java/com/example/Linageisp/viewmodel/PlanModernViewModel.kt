package com.example.Linageisp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import com.example.Linageisp.data.PlanModern
import com.example.Linageisp.data.PlanColor

class PlanModernViewModel : ViewModel() {
    
    private val _plans = mutableStateOf<List<PlanModern>>(emptyList())
    val plans: State<List<PlanModern>> = _plans
    
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    
    init {
        loadPlans()
    }
    
    private fun loadPlans() {
        _isLoading.value = true
        
        // Simular carga de datos
        _plans.value = listOf(
            PlanModern(
                id = "modern_200",
                speed = "200 Mbps", 
                price = "$80.000",
                benefits = listOf(
                    "Fibra óptica premium",
                    "Paramount+ gratis 1 mes",
                    "TV digital 120 canales",
                    "Instalación sin costo",
                    "Soporte técnico 24/7"
                ),
                isPopular = true,
                color = PlanColor.Orange
            ),
            PlanModern(
                id = "modern_silver_win",
                speed = "400 Mbps",
                price = "$113.000",
                benefits = listOf(
                    "Fibra óptica premium", 
                    "DIRECTV GO Flex (20 canales)",
                    "2 pantallas simultáneas",
                    "1.000 series y películas",
                    "3 meses Paramount+ gratis",
                    "Canal Win+ Fútbol exclusivo"
                ),
                isRecommended = true,
                color = PlanColor.Blue
            ),
            PlanModern(
                id = "modern_gold_win",
                speed = "400 Mbps",
                price = "$163.000", 
                benefits = listOf(
                    "Fibra óptica premium",
                    "DIRECTV GO FULL (80 canales)",
                    "4 pantallas simultáneas", 
                    "10.000 series y películas",
                    "3 meses Paramount+ gratis",
                    "Canal Win+ Fútbol exclusivo"
                ),
                color = PlanColor.Purple
            ),
            PlanModern(
                id = "modern_netflix_900",
                speed = "900 Mbps",
                price = "$115.000",
                benefits = listOf(
                    "Fibra óptica premium",
                    "Netflix HD incluido",
                    "Máxima velocidad",
                    "Instalación sin costo", 
                    "Soporte técnico 24/7"
                ),
                color = PlanColor.Green
            )
        )
        
        _isLoading.value = false
    }
}