package com.example.Linageisp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Linageisp.data.Plan
import com.example.Linageisp.network.PlanScrapingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlanUiState(
    val plans: List<Plan> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedPlan: Plan? = null
)

class PlanViewModel : ViewModel() {
    
    private val planScrapingService = PlanScrapingService()
    
    private val _uiState = MutableStateFlow(PlanUiState())
    val uiState: StateFlow<PlanUiState> = _uiState.asStateFlow()
    
    init {
        loadPlans()
    }
    
    fun loadPlans() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val plans = getStaticPlans()
                _uiState.value = _uiState.value.copy(
                    plans = plans,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar los planes: ${e.message}"
                )
            }
        }
    }
    
    private fun getStaticPlans(): List<Plan> {
        return listOf(
            // PLANES BÁSICOS
            Plan(
                id = "plan_200_basic",
                nombre = "200 Megas",
                velocidad = "200 Mbps",
                precio = "$80.000",
                beneficios = "200 Megas Fibra Óptica, 1 Mes de Paramount+ Gratis, 1 Pantalla Simultánea, TV digital de 120 canales, Instalación Gratis",
                type = "Fibra Óptica"
            ),
            Plan(
                id = "plan_400_basic", 
                nombre = "400 Megas",
                velocidad = "400 Mbps",
                precio = "$85.000",
                beneficios = "400 Megas Fibra Óptica, 1 Mes de Paramount+ Gratis, 1 Pantalla Simultánea, TV digital de 120 canales, Instalación Gratis",
                type = "Fibra Óptica"
            ),
            Plan(
                id = "plan_600_basic",
                nombre = "600 Megas", 
                velocidad = "600 Mbps",
                precio = "$100.000",
                beneficios = "600 Megas Fibra Óptica, 1 Mes de Paramount+ Gratis, 1 Pantalla Simultánea, TV digital de 120 canales, Instalación Gratis",
                type = "Fibra Óptica"
            ),
            Plan(
                id = "plan_900_basic",
                nombre = "900 Megas",
                velocidad = "900 Mbps", 
                precio = "$115.000",
                beneficios = "900 Megas Fibra Óptica, 1 Mes de Paramount+ Gratis, 1 Pantalla Simultánea, TV digital de 120 canales, Instalación Gratis",
                type = "Fibra Óptica"
            ),
            
            // PLANES WIN+ PREMIUM
            Plan(
                id = "plan_silver_win",
                nombre = "Silver Win+",
                velocidad = "400 Mbps",
                precio = "$113.000", 
                beneficios = "400 Megas Fibra Óptica, DIRECTV GO Flex (20 canales), 2 Pantallas Simultáneas, 1.000 series y películas on demand, 3 meses PARAMOUNT+ gratis, Canal Win+ Fútbol, Instalación Gratis",
                type = "Premium Win+"
            ),
            Plan(
                id = "plan_platinum_win",
                nombre = "Platinum Win+", 
                velocidad = "400 Mbps",
                precio = "$141.500",
                beneficios = "400 Megas Fibra Óptica, DIRECTV GO Básico (40 canales), 2 Pantallas Simultáneas, 6.000 series y películas on demand, 3 meses PARAMOUNT+ gratis, Canal Win+ Fútbol, Instalación Gratis",
                type = "Premium Win+"
            ),
            Plan(
                id = "plan_gold_win",
                nombre = "Gold Win+",
                velocidad = "400 Mbps", 
                precio = "$163.000",
                beneficios = "400 Megas Fibra Óptica, DIRECTV GO FULL (80 canales), 4 Pantallas Simultáneas, 10.000 series y películas on demand, 3 meses PARAMOUNT+ gratis, Canal Win+ Fútbol, Instalación Gratis",
                type = "Premium Win+"
            ),
            
            // PLANES DIRECTV GO
            Plan(
                id = "plan_silver_dtv",
                nombre = "Silver DIRECTV",
                velocidad = "400 Mbps",
                precio = "$90.000",
                beneficios = "400 Megas Fibra Óptica, DIRECTV GO Flex (20 canales), 1 Pantalla Simultánea, 1.000 series y películas on demand, 3 meses PARAMOUNT+ gratis, Instalación Gratis", 
                type = "DIRECTV GO"
            ),
            Plan(
                id = "plan_platinum_dtv",
                nombre = "Platinum DIRECTV",
                velocidad = "400 Mbps",
                precio = "$118.500",
                beneficios = "400 Megas Fibra Óptica, DIRECTV GO Básico (40 canales), 2 Pantallas Simultáneas, 6.000 series y películas on demand, 3 meses PARAMOUNT+ gratis, Instalación Gratis",
                type = "DIRECTV GO"
            ),
            Plan(
                id = "plan_gold_dtv", 
                nombre = "Gold DIRECTV",
                velocidad = "400 Mbps",
                precio = "$140.000",
                beneficios = "400 Megas Fibra Óptica, DIRECTV GO FULL (80 canales), 4 Pantallas Simultáneas, 10.000 series y películas on demand, 3 meses PARAMOUNT+ gratis, Instalación Gratis",
                type = "DIRECTV GO"
            ),
            
            // PLANES NETFLIX
            Plan(
                id = "plan_netflix_400",
                nombre = "Netflix 400",
                velocidad = "400 Mbps", 
                precio = "$85.000",
                beneficios = "400 Megas Fibra Óptica, NETFLIX (1 Dispositivo HD), Instalación Gratis",
                type = "Netflix"
            ),
            Plan(
                id = "plan_netflix_600", 
                nombre = "Netflix 600",
                velocidad = "600 Mbps",
                precio = "$100.000",
                beneficios = "600 Megas Fibra Óptica, NETFLIX (1 Dispositivo HD), Instalación Gratis",
                type = "Netflix"
            ),
            Plan(
                id = "plan_netflix_900",
                nombre = "Netflix 900",
                velocidad = "900 Mbps",
                precio = "$115.000", 
                beneficios = "900 Megas Fibra Óptica, NETFLIX (1 Dispositivo HD), Instalación Gratis",
                type = "Netflix"
            ),
            
            // PLANES CON CÁMARAS (NUEVO)
            Plan(
                id = "plan_camaras_400",
                nombre = "Camaras 400 MEGAS",
                velocidad = "400 Mbps",
                precio = "$90.000",
                beneficios = "400 Megas Fibra Óptica, 1 mes de PARAMOUNT+ gratis, 1 Pantallas Simultáneas, 1 Cámara de seguridad, Instalación Gratis",
                type = "Cámaras"
            ),
            Plan(
                id = "plan_camaras_600",
                nombre = "Camaras 600 MEGAS",
                velocidad = "600 Mbps",
                precio = "$95.500",
                beneficios = "600 Megas Fibra Óptica, 1 mes de PARAMOUNT+ gratis, 1 Pantallas Simultáneas, 1 Cámara de seguridad, Instalación Gratis",
                type = "Cámaras"
            ),
            Plan(
                id = "plan_camaras_900",
                nombre = "Camaras 900 MEGAS",
                velocidad = "900 Mbps",
                precio = "$105.000",
                beneficios = "900 Megas Fibra Óptica, 1 mes de PARAMOUNT+ gratis, 1 Pantallas Simultáneas, 1 Cámara de seguridad, Instalación Gratis",
                type = "Cámaras"
            )
        )
    }
    
    fun selectPlan(plan: Plan) {
        _uiState.value = _uiState.value.copy(selectedPlan = plan)
    }
    
    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedPlan = null)
    }
    
    fun retryLoading() {
        loadPlans()
    }
}