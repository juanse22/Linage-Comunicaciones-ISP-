package com.example.Linageisp.presentation.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Linageisp.ai.core.GenkitAI
import com.example.Linageisp.ai.core.LinageAIAssistant
import com.example.Linageisp.ai.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val aiAssistant: LinageAIAssistant,
    private val genkitAI: GenkitAI
) : ViewModel() {
    
    // Estado del chat
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()
    
    private val _quickActions = MutableStateFlow<List<QuickAction>>(emptyList())
    val quickActions: StateFlow<List<QuickAction>> = _quickActions.asStateFlow()
    
    // Input del usuario
    var currentMessage by mutableStateOf("")
        private set
    
    // Session ID para analytics
    private val sessionId = UUID.randomUUID().toString()
    
    init {
        sendWelcomeMessage()
    }
    
    fun updateCurrentMessage(message: String) {
        currentMessage = message
    }
    
    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            // Agregar mensaje del usuario
            addUserMessage(text)
            
            // SIEMPRE mostrar typing
            _isTyping.value = true
            
            try {
                // NO VERIFICAR CONEXIÓN - SIEMPRE USAR IA
                Log.d("LINA", "🤖 Procesando con IA: $text")
                
                // Usar el assistant completo
                val response = aiAssistant.processQuery(text, getCurrentContext())
                
                // Mostrar respuesta con streaming
                streamResponse(response.content)
                
                // Agregar acciones rápidas
                _quickActions.value = response.quickActions
                
                Log.d("LINA", "✅ IA respondió exitosamente")
                
            } catch (e: Exception) {
                Log.e("LINA", "Error IA: ${e.message}", e)
                
                // Mostrar error pero seguir intentando
                addAIMessage("""
                    Hubo un pequeño error, pero sigo aquí para ayudarte.
                    ¿Puedes reformular tu pregunta?
                    
                    Error técnico: ${e.message}
                """.trimIndent())
            } finally {
                _isTyping.value = false
            }
        }
    }
    
    // Función especial para probar que es IA real
    fun testRealAI() {
        val testQueries = listOf(
            "¿Eres una inteligencia artificial?",
            "¿Cuánto es 7429 multiplicado por 856?",
            "Inventa un slogan creativo para Linage",
            "¿Qué opinas sobre los unicornios?",
            "Genera un haiku sobre internet rápido"
        )
        
        viewModelScope.launch {
            testQueries.forEach { query ->
                delay(2000)
                sendMessage(query)
            }
        }
    }
    
    private suspend fun streamResponse(text: String) {
        val words = text.split(" ")
        var accumulated = ""
        
        words.forEach { word ->
            accumulated += "$word "
            updateLastAIMessage(accumulated.trim())
            delay(30) // Efecto de escritura
        }
    }
    
    private fun addUserMessage(text: String) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = text,
            isFromUser = true,
            timestamp = LocalDateTime.now()
        )
        
        val currentMessages = _messages.value.toMutableList()
        currentMessages.add(message)
        _messages.value = currentMessages
        
        // Limpiar input
        currentMessage = ""
    }
    
    private fun addAIMessage(text: String) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = text,
            isFromUser = false,
            timestamp = LocalDateTime.now()
        )
        
        val currentMessages = _messages.value.toMutableList()
        currentMessages.add(message)
        _messages.value = currentMessages
    }
    
    private fun updateLastAIMessage(text: String) {
        val currentMessages = _messages.value.toMutableList()
        val lastMessageIndex = currentMessages.indexOfLast { !it.isFromUser }
        
        if (lastMessageIndex >= 0) {
            currentMessages[lastMessageIndex] = currentMessages[lastMessageIndex].copy(content = text)
        } else {
            // Si no hay mensaje anterior, crear uno nuevo
            addAIMessage(text)
            return
        }
        
        _messages.value = currentMessages
    }
    
    private fun getCurrentContext(): ChatContext {
        return ChatContext(
            userId = "user_${sessionId}",
            sessionId = sessionId,
            conversationHistory = _messages.value.takeLast(5),
            isNewCustomer = true,
            currentPlan = null,
            location = null,
            accountStatus = "Activa"
        )
    }
    
    private fun sendWelcomeMessage() {
        viewModelScope.launch {
            delay(500) // Pequeño delay para simular carga
            
            addAIMessage("""
                ¡Hola! 👋 Soy LINA, tu asistente de Linage ISP.
                
                Estoy aquí para ayudarte con:
                ⚽ Planes Win+ con fútbol
                🚀 Internet fibra óptica súper rápido  
                📺 Paquetes con Netflix y streaming
                🔧 Soporte técnico 24/7
                
                ¿En qué puedo ayudarte hoy?
            """.trimIndent())
            
            // Acciones rápidas iniciales
            _quickActions.value = listOf(
                QuickAction("plans", "⚽ Ver planes", ActionType.SHOW_PLANS, "all"),
                QuickAction("football", "🏆 Planes Win+", ActionType.SHOW_PLANS, "football"),
                QuickAction("support", "🔧 Soporte técnico", ActionType.TECHNICAL_HELP, "help")
            )
        }
    }
    
    fun handleQuickAction(action: QuickAction) {
        when (action.action) {
            ActionType.SHOW_PLANS -> {
                if (action.data.containsValue("football")) {
                    sendMessage("Quiero ver los planes Win+ con fútbol")
                } else {
                    sendMessage("Quiero ver todos los planes disponibles")
                }
            }
            ActionType.TECHNICAL_HELP -> {
                sendMessage("Necesito ayuda técnica")
            }
            ActionType.CONTRACT -> {
                sendMessage("Quiero contratar un plan")
            }
            ActionType.NEW_CHAT -> {
                clearChat()
            }
            else -> {
                sendMessage("Ayuda con ${action.text}")
            }
        }
    }
    
    private fun clearChat() {
        _messages.value = emptyList()
        _quickActions.value = emptyList()
        sendWelcomeMessage()
    }
}