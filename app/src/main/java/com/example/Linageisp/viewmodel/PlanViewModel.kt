package com.example.Linageisp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Linageisp.data.Plan
import com.example.Linageisp.data.getAllPlans
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
                val plans = getAllPlans()
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