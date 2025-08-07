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
                id = "plan_200",
                speed = "200 Mbps",
                price = "$65.000",
                benefits = listOf(
                    "Fibra óptica premium",
                    "Paramount+ gratis 1 mes",
                    "Instalación sin costo",
                    "Soporte técnico 24/7",
                    "1 pantalla simultánea"
                ),
                isPopular = true,
                color = PlanColor.Orange
            ),
            PlanModern(
                id = "plan_400",
                speed = "400 Mbps",
                price = "$70.000",
                originalPrice = "$85.000",
                benefits = listOf(
                    "Fibra óptica premium",
                    "Paramount+ gratis 2 meses",
                    "Netflix básico incluido",
                    "Instalación sin costo",
                    "Soporte técnico 24/7",
                    "2 pantallas simultáneas"
                ),
                isRecommended = true,
                color = PlanColor.Blue
            ),
            PlanModern(
                id = "plan_600",
                speed = "600 Mbps",
                price = "$85.000",
                benefits = listOf(
                    "Fibra óptica premium",
                    "Paramount+ gratis 3 meses",
                    "Netflix estándar incluido",
                    "Win Sports+ incluido",
                    "Instalación sin costo",
                    "Soporte técnico 24/7",
                    "4 pantallas simultáneas"
                ),
                color = PlanColor.Green
            ),
            PlanModern(
                id = "plan_900",
                speed = "900 Mbps",
                price = "$100.000",
                benefits = listOf(
                    "Fibra óptica premium",
                    "Paramount+ gratis 6 meses",
                    "Netflix premium incluido",
                    "Win Sports+ incluido",
                    "DIRECTV GO incluido",
                    "Instalación sin costo",
                    "Soporte técnico 24/7",
                    "Dispositivos ilimitados"
                ),
                color = PlanColor.Purple
            )
        )
        
        _isLoading.value = false
    }
}