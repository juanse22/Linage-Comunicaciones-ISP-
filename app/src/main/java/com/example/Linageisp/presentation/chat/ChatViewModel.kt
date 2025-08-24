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
    private val genkitAI: GenkitAI,
    private val crossPlatformOptimizer: com.example.Linageisp.performance.CrossPlatformOptimizer,
    private val performanceLogger: com.example.Linageisp.performance.PerformanceLogger
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
            val messageStartTime = System.currentTimeMillis()
            
            // Log performance del inicio de mensaje
            performanceLogger.logChatPerformanceIssue("message_sent", mapOf(
                "messageLength" to text.length,
                "timestamp" to messageStartTime
            ))
            
            // Agregar mensaje del usuario
            val addMessageStart = System.currentTimeMillis()
            addUserMessage(text)
            performanceLogger.measureRenderTime("ChatViewModel", "addUserMessage", addMessageStart, System.currentTimeMillis())
            
            // SIEMPRE mostrar typing
            _isTyping.value = true
            
            try {
                // NO VERIFICAR CONEXIÓN - SIEMPRE USAR IA
                Log.d("LINA", "🤖 Procesando con IA: $text")
                
                val aiStartTime = System.currentTimeMillis()
                
                // Usar el assistant completo
                val response = aiAssistant.processQuery(text, getCurrentContext())
                
                val aiEndTime = System.currentTimeMillis()
                val aiDuration = aiEndTime - aiStartTime
                
                // Log tiempo de respuesta de IA
                performanceLogger.logChatPerformanceIssue("ai_response_time", mapOf(
                    "duration" to aiDuration,
                    "messageLength" to text.length,
                    "responseLength" to response.content.length
                ))
                
                // Mostrar respuesta con streaming
                streamResponse(response.content)
                
                // Agregar acciones rápidas
                _quickActions.value = response.quickActions
                
                val totalDuration = System.currentTimeMillis() - messageStartTime
                Log.d("LINA", "✅ IA respondió exitosamente en ${totalDuration}ms")
                
                // Log éxito total
                performanceLogger.logChatPerformanceIssue("message_complete", mapOf(
                    "totalDuration" to totalDuration,
                    "aiDuration" to aiDuration,
                    "streamingWords" to response.content.split(" ").size
                ))
                
            } catch (e: Exception) {
                Log.e("LINA", "Error IA: ${e.message}", e)
                
                // Log error crítico
                performanceLogger.logChatPerformanceIssue("ai_error", mapOf(
                    "error" to e.message.orEmpty(),
                    "errorType" to e.javaClass.simpleName,
                    "duration" to (System.currentTimeMillis() - messageStartTime)
                ))
                
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
        // Obtener optimizaciones específicas del dispositivo
        val chatOptimizations = crossPlatformOptimizer.getChatOptimizations()
        
        val words = text.split(" ")
        var accumulated = ""
        
        // Delay adaptativo según capacidad del dispositivo
        val streamDelay = when {
            chatOptimizations?.enableTypingAnimations == false -> 0L // Sin delay en dispositivos lentos
            chatOptimizations?.enableScrollAnimations == false -> 10L // Delay mínimo
            else -> 30L // Delay normal para dispositivos capaces
        }
        
        Log.d("LINA", "Streaming con delay: ${streamDelay}ms para ${words.size} palabras")
        
        words.forEach { word ->
            accumulated += "$word "
            updateLastAIMessage(accumulated.trim())
            
            if (streamDelay > 0L) {
                delay(streamDelay)
            }
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