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
            Plan(
                id = "plan_200",
                nombre = "200 Megas",
                velocidad = "200 Mbps",
                precio = "$65.000",
                beneficios = "Internet por fibra óptica, 1 mes gratis de Paramount+, 1 pantalla simultánea, Instalación gratuita",
                type = "Fibra Óptica"
            ),
            Plan(
                id = "plan_400",
                nombre = "400 Megas",
                velocidad = "400 Mbps",
                precio = "$70.000",
                beneficios = "Internet por fibra óptica, 1 mes gratis de Paramount+, 1 pantalla simultánea, Instalación gratuita",
                type = "Fibra Óptica"
            ),
            Plan(
                id = "plan_600",
                nombre = "600 Megas",
                velocidad = "600 Mbps",
                precio = "$85.000",
                beneficios = "Internet por fibra óptica, 1 mes gratis de Paramount+, 1 pantalla simultánea, Instalación gratuita",
                type = "Fibra Óptica"
            ),
            Plan(
                id = "plan_900",
                nombre = "900 Megas",
                velocidad = "900 Mbps",
                precio = "$100.000",
                beneficios = "Internet por fibra óptica, 1 mes gratis de Paramount+, 1 pantalla simultánea, Instalación gratuita",
                type = "Fibra Óptica"
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