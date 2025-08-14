package com.example.Linageisp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Linageisp.ai.GenkitAIService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado de la UI del asistente de IA
 */
data class AIAssistantUiState(
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val errorMessage: String? = null,
    val isTyping: Boolean = false
)

/**
 * Mensaje del chat
 */
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: MessageType = MessageType.GENERAL
)

/**
 * Tipos de mensajes para personalización
 */
enum class MessageType {
    GENERAL,           // Consulta general
    RECOMMENDATION,    // Recomendación de plan
    TECHNICAL_SUPPORT, // Soporte técnico
    COMPANY_INFO,      // Información corporativa
    WELCOME           // Mensaje de bienvenida
}

/**
 * ViewModel para el asistente de IA integrado con Firebase Genkit
 */
class AIAssistantViewModel : ViewModel() {
    
    private val aiService = GenkitAIService()
    
    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()
    
    init {
        // Mensaje de bienvenida inicial
        addWelcomeMessage()
    }
    
    /**
     * Agregar mensaje de bienvenida
     */
    private fun addWelcomeMessage() {
        val welcomeMessage = ChatMessage(
            text = "¡Hola! 👋 Soy tu asistente virtual de Linage ISP. Puedo ayudarte con:\n\n" +
                    "📶 Información sobre planes de internet\n" +
                    "💡 Recomendaciones personalizadas\n" +
                    "🔧 Soporte técnico\n" +
                    "🏢 Información de la empresa\n\n" +
                    "¿En qué puedo ayudarte hoy?",
            isFromUser = false,
            messageType = MessageType.WELCOME
        )
        
        _uiState.value = _uiState.value.copy(
            messages = listOf(welcomeMessage)
        )
    }
    
    /**
     * Enviar mensaje general
     */
    fun sendMessage(message: String) {
        if (message.isBlank()) return
        
        // Agregar mensaje del usuario
        addUserMessage(message)
        
        // Mostrar estado de escritura
        setTypingState(true)
        
        viewModelScope.launch {
            aiService.askAboutPlans(message)
                .onSuccess { response ->
                    addAIMessage(response, MessageType.GENERAL)
                }
                .onFailure { error ->
                    handleError("Error al procesar tu consulta: ${error.message}")
                }
            
            setTypingState(false)
        }
    }
    
    /**
     * Obtener recomendación personalizada de plan
     */
    fun getRecommendation(familySize: Int, usage: String, budget: String) {
        // Agregar mensaje del usuario con sus criterios
        val userMessage = "Quiero una recomendación de plan para:\n" +
                "👥 Familia de $familySize personas\n" +
                "🎯 Uso: $usage\n" +
                "💰 Presupuesto: $budget"
        
        addUserMessage(userMessage)
        setTypingState(true)
        
        viewModelScope.launch {
            aiService.recommendPlan(familySize, usage, budget)
                .onSuccess { recommendation ->
                    addAIMessage(recommendation, MessageType.RECOMMENDATION)
                }
                .onFailure { error ->
                    handleError("Error al generar recomendación: ${error.message}")
                }
            
            setTypingState(false)
        }
    }
    
    /**
     * Obtener soporte técnico
     */
    fun getTechnicalSupport(issue: String) {
        if (issue.isBlank()) return
        
        // Agregar mensaje del usuario
        addUserMessage("🔧 Problema técnico: $issue")
        setTypingState(true)
        
        viewModelScope.launch {
            aiService.technicalSupport(issue)
                .onSuccess { solution ->
                    addAIMessage(solution, MessageType.TECHNICAL_SUPPORT)
                }
                .onFailure { error ->
                    handleError("Error en soporte técnico: ${error.message}")
                }
            
            setTypingState(false)
        }
    }
    
    /**
     * Obtener información corporativa
     */
    fun getCompanyInfo(query: String) {
        if (query.isBlank()) return
        
        addUserMessage("🏢 Consulta: $query")
        setTypingState(true)
        
        viewModelScope.launch {
            aiService.getCompanyInfo(query)
                .onSuccess { info ->
                    addAIMessage(info, MessageType.COMPANY_INFO)
                }
                .onFailure { error ->
                    handleError("Error al obtener información: ${error.message}")
                }
            
            setTypingState(false)
        }
    }
    
    /**
     * Agregar mensaje del usuario
     */
    private fun addUserMessage(message: String) {
        val currentMessages = _uiState.value.messages.toMutableList()
        currentMessages.add(ChatMessage(message, isFromUser = true))
        
        _uiState.value = _uiState.value.copy(
            messages = currentMessages,
            errorMessage = null
        )
    }
    
    /**
     * Agregar mensaje de la IA
     */
    private fun addAIMessage(message: String, type: MessageType) {
        val currentMessages = _uiState.value.messages.toMutableList()
        currentMessages.add(
            ChatMessage(
                text = message,
                isFromUser = false,
                messageType = type
            )
        )
        
        _uiState.value = _uiState.value.copy(
            messages = currentMessages,
            isLoading = false
        )
    }
    
    /**
     * Establecer estado de escritura
     */
    private fun setTypingState(isTyping: Boolean) {
        _uiState.value = _uiState.value.copy(
            isLoading = isTyping,
            isTyping = isTyping,
            errorMessage = null
        )
    }
    
    /**
     * Manejar errores
     */
    private fun handleError(errorMessage: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isTyping = false,
            errorMessage = errorMessage
        )
        
        // Agregar mensaje de error como respuesta de la IA
        addAIMessage(
            "Lo siento, hubo un problema procesando tu solicitud. 😔\n\n" +
                    "Por favor intenta de nuevo o contacta nuestro soporte:\n" +
                    "📞 WhatsApp: +57 302 447 8864",
            MessageType.GENERAL
        )
    }
    
    /**
     * Limpiar chat
     */
    fun clearChat() {
        _uiState.value = AIAssistantUiState()
        addWelcomeMessage()
    }
    
    /**
     * Limpiar mensaje de error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Acciones rápidas predefinidas
     */
    fun sendQuickAction(action: QuickAction) {
        when (action) {
            QuickAction.VIEW_PLANS -> sendMessage("Muéstrame todos los planes disponibles con precios")
            QuickAction.RECOMMEND_FAMILY -> getRecommendation(4, "streaming", "medio")
            QuickAction.INTERNET_SLOW -> getTechnicalSupport("Mi internet está muy lento")
            QuickAction.COMPANY_INFO -> getCompanyInfo("Información general sobre Linage ISP")
            QuickAction.CONTACT_INFO -> sendMessage("¿Cómo puedo contactar a Linage ISP?")
            QuickAction.INSTALLATION -> sendMessage("¿Cómo es el proceso de instalación?")
        }
    }
}

/**
 * Acciones rápidas para el chat
 */
enum class QuickAction(val label: String, val emoji: String) {
    VIEW_PLANS("Ver planes", "📶"),
    RECOMMEND_FAMILY("Recomendar para familia", "👨‍👩‍👧‍👦"),
    INTERNET_SLOW("Internet lento", "🐌"),
    COMPANY_INFO("Info empresa", "🏢"),
    CONTACT_INFO("Contacto", "📞"),
    INSTALLATION("Instalación", "🔧")
}