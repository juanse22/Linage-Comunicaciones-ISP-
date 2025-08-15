package com.example.Linageisp.ai.core

import com.example.Linageisp.ai.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinageAIAssistant @Inject constructor(
    private val genkitAI: GenkitAI
) {
    
    suspend fun processQuery(
        query: String,
        context: ChatContext
    ): AIResponse {
        
        val startTime = System.currentTimeMillis()
        
        // Enriquecer el prompt con contexto
        val enrichedQuery = buildEnrichedQuery(query, context)
        
        // LLAMAR A GENKIT REAL
        val aiResponse = genkitAI.generateResponse(enrichedQuery)
        
        val processingTime = System.currentTimeMillis() - startTime
        
        return AIResponse(
            content = aiResponse,
            intent = classifyIntent(query),
            confidence = 0.95f, // Alta confianza porque es IA real
            quickActions = generateSmartActions(query, aiResponse),
            suggestedPlans = emptyList(),
            metadata = ResponseMetadata(
                processingTime = processingTime,
                intent = classifyIntent(query),
                model = "gemini-1.5-flash"
            )
        )
    }
    
    private fun buildEnrichedQuery(query: String, context: ChatContext): String {
        return """
        Contexto del cliente:
        - Plan actual: ${context.currentPlan?.name ?: "Sin plan"}
        - Ubicación: ${context.location ?: "No especificada"}
        - Estado cuenta: ${context.accountStatus ?: "Activa"}
        
        Pregunta: $query
        
        Responde de manera útil y personalizada.
        Si te preguntan si eres IA, confirma con orgullo que sí.
        NUNCA sugieras contactar servicio humano, siempre ayuda tú.
        """.trimIndent()
    }
    
    fun classifyIntent(query: String): Intent {
        val lower = query.lowercase()
        return when {
            lower.contains("ia") || lower.contains("robot") || lower.contains("inteligencia artificial") -> Intent.ABOUT_AI
            lower.contains("fútbol") || lower.contains("futbol") || lower.contains("win+") -> Intent.PLANS_FOOTBALL
            lower.contains("plan") || lower.contains("precio") -> Intent.PLANS_GENERAL
            lower.contains("problema") || lower.contains("lento") -> Intent.TECHNICAL_SUPPORT
            lower.contains("hola") || lower.contains("buenas") -> Intent.GREETING
            else -> Intent.GENERAL
        }
    }
    
    private fun generateSmartActions(query: String, response: String): List<QuickAction> {
        return when {
            query.contains("plan", ignoreCase = true) -> listOf(
                QuickAction("prices", "💰 Ver precios", ActionType.SHOW_PLANS, "prices"),
                QuickAction("football", "⚽ Planes Win+", ActionType.SHOW_PLANS, "football"),
                QuickAction("contract", "🚀 Contratar ahora", ActionType.CONTRACT, "new")
            )
            query.contains("problema", ignoreCase = true) -> listOf(
                QuickAction("restart", "🔧 Reiniciar router", ActionType.GUIDE, "restart"),
                QuickAction("speedtest", "📊 Test velocidad", ActionType.TOOL, "speedtest"),
                QuickAction("other", "💬 Otro problema", ActionType.SUPPORT, "other")
            )
            else -> listOf(
                QuickAction("plans", "⚽ Ver planes", ActionType.SHOW_PLANS, "all"),
                QuickAction("new_chat", "💬 Nueva pregunta", ActionType.NEW_CHAT, "clear")
            )
        }
    }
    
    // Streaming de respuesta para efecto de escritura
    suspend fun generateResponseStream(query: String, context: ChatContext): Flow<String> {
        val enrichedQuery = buildEnrichedQuery(query, context)
        return genkitAI.generateResponseStream(enrichedQuery)
    }
}