package com.example.Linageisp.ai.analytics

import com.example.Linageisp.ai.models.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.os.Bundle

/**
 * Sistema de analytics para el asistente de IA LINA
 * Rastrea interacciones, conversiones y métricas de rendimiento
 */
object AIAnalytics {
    
    private val analytics: FirebaseAnalytics by lazy { Firebase.analytics }
    private val scope = CoroutineScope(Dispatchers.IO)
    
    // Eventos personalizados
    private const val EVENT_AI_INTERACTION = "ai_interaction"
    private const val EVENT_AI_INTENT_DETECTED = "ai_intent_detected"
    private const val EVENT_AI_RESPONSE_GENERATED = "ai_response_generated"
    private const val EVENT_AI_QUICK_ACTION = "ai_quick_action"
    private const val EVENT_AI_ESCALATION = "ai_escalation"
    private const val EVENT_AI_CONVERSION = "ai_conversion"
    private const val EVENT_AI_ERROR = "ai_error"
    private const val EVENT_AI_FEEDBACK = "ai_feedback"
    
    /**
     * Registra una interacción básica con el asistente
     */
    fun logInteraction(
        userQuery: String,
        intent: Intent,
        confidence: Float,
        responseTime: Long,
        sessionId: String
    ) {
        scope.launch {
            analytics.logEvent(EVENT_AI_INTERACTION) {
                param("query_length", userQuery.length.toLong())
                param("intent", intent.name)
                param("confidence", confidence.toDouble())
                param("response_time_ms", responseTime)
                param("session_id", sessionId)
                param("timestamp", System.currentTimeMillis())
            }
        }
    }
    
    /**
     * Registra la detección de intención por parte del modelo
     */
    fun logIntentDetection(
        intent: Intent,
        confidence: Float,
        alternativeIntents: List<Intent> = emptyList(),
        context: String? = null
    ) {
        scope.launch {
            analytics.logEvent(EVENT_AI_INTENT_DETECTED) {
                param("primary_intent", intent.name)
                param("confidence", confidence.toDouble())
                param("alternatives_count", alternativeIntents.size.toLong())
                param("has_context", (context != null).toString())
                param("timestamp", System.currentTimeMillis())
            }
        }
    }
    
    /**
     * Registra la generación de respuesta exitosa
     */
    fun logResponseGenerated(
        intent: Intent,
        responseLength: Int,
        quickActionsCount: Int,
        suggestedPlansCount: Int,
        processingTime: Long
    ) {
        scope.launch {
            analytics.logEvent(EVENT_AI_RESPONSE_GENERATED) {
                param("intent", intent.name)
                param("response_length", responseLength.toLong())
                param("quick_actions_count", quickActionsCount.toLong())
                param("suggested_plans_count", suggestedPlansCount.toLong())
                param("processing_time_ms", processingTime)
                param("timestamp", System.currentTimeMillis())
            }
        }
    }
    
    /**
     * Registra cuando el usuario hace clic en una acción rápida
     */
    fun logQuickActionClicked(
        action: QuickAction,
        intent: Intent,
        sessionId: String
    ) {
        scope.launch {
            analytics.logEvent(EVENT_AI_QUICK_ACTION) {
                param("action_id", action.id)
                param("action_type", action.action.name)
                param("action_text", action.text)
                param("parent_intent", intent.name)
                param("session_id", sessionId)
                param("timestamp", System.currentTimeMillis())
            }
        }
    }
    
    /**
     * Registra escalaciones a soporte humano
     */
    fun logEscalation(
        reason: String,
        intent: Intent,
        attempts: Int,
        sessionDuration: Long,
        sessionId: String
    ) {
        scope.launch {
            analytics.logEvent(EVENT_AI_ESCALATION) {
                param("reason", reason)
                param("original_intent", intent.name)
                param("attempts_before_escalation", attempts.toLong())
                param("session_duration_ms", sessionDuration)
                param("session_id", sessionId)
                param("timestamp", System.currentTimeMillis())
            }
        }
    }
    
    /**
     * Registra conversiones (interés en planes, agendamiento, etc.)
     */
    fun logConversion(
        conversionType: ConversionType,
        planId: String? = null,
        value: Double? = null,
        sessionId: String
    ) {
        scope.launch {
            analytics.logEvent(EVENT_AI_CONVERSION) {
                param("conversion_type", conversionType.name)
                planId?.let { param("plan_id", it) }
                value?.let { param("estimated_value", it) }
                param("session_id", sessionId)
                param("timestamp", System.currentTimeMillis())
            }
            
            // También registrar como evento de conversión estándar de Firebase
            if (value != null) {
                analytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
                    param(FirebaseAnalytics.Param.CURRENCY, "COP")
                    param(FirebaseAnalytics.Param.VALUE, value)
                    param("source", "ai_assistant")
                }
            }
        }
    }
    
    /**
     * Registra errores del sistema de IA
     */
    fun logError(
        errorType: AIErrorType,
        errorMessage: String,
        intent: Intent? = null,
        sessionId: String
    ) {
        scope.launch {
            analytics.logEvent(EVENT_AI_ERROR) {
                param("error_type", errorType.name)
                param("error_message", errorMessage.take(100)) // Limitar longitud
                intent?.let { param("intent", it.name) }
                param("session_id", sessionId)
                param("timestamp", System.currentTimeMillis())
            }
        }
    }
    
    /**
     * Registra feedback del usuario sobre las respuestas
     */
    fun logFeedback(
        rating: Int, // 1-5
        helpful: Boolean,
        comment: String? = null,
        intent: Intent,
        sessionId: String
    ) {
        scope.launch {
            analytics.logEvent(EVENT_AI_FEEDBACK) {
                param("rating", rating.toLong())
                param("helpful", helpful.toString())
                comment?.let { param("comment", it.take(200)) }
                param("intent", intent.name)
                param("session_id", sessionId)
                param("timestamp", System.currentTimeMillis())
            }
        }
    }
    
    /**
     * Registra métricas de rendimiento detalladas
     */
    fun logPerformanceMetrics(metrics: AIMetrics, sessionId: String) {
        scope.launch {
            analytics.logEvent("ai_performance") {
                param("response_time_ms", metrics.responseTime)
                param("tokens_used", metrics.tokensUsed.toLong())
                param("confidence", metrics.confidence.toDouble())
                param("intent", metrics.intent.name)
                param("satisfied", metrics.satisfied?.toString() ?: "unknown")
                param("escalated", metrics.escalated.toString())
                param("session_id", sessionId)
                param("timestamp", System.currentTimeMillis())
            }
        }
    }
    
    /**
     * Establece propiedades del usuario para segmentación
     */
    fun setUserProperties(
        isNewCustomer: Boolean,
        preferredPlanType: PlanType?,
        location: String?
    ) {
        scope.launch {
            analytics.setUserProperty("is_new_customer", isNewCustomer.toString())
            preferredPlanType?.let { 
                analytics.setUserProperty("preferred_plan_type", it.name) 
            }
            location?.let { 
                analytics.setUserProperty("customer_location", it) 
            }
            analytics.setUserProperty("uses_ai_assistant", "true")
        }
    }
    
    /**
     * Registra inicio de sesión de chat
     */
    fun logSessionStart(sessionId: String, source: String = "direct") {
        scope.launch {
            analytics.logEvent("ai_session_start") {
                param("session_id", sessionId)
                param("source", source)
                param("timestamp", System.currentTimeMillis())
            }
        }
    }
    
    /**
     * Registra fin de sesión con métricas resumidas
     */
    fun logSessionEnd(
        sessionId: String,
        duration: Long,
        messageCount: Int,
        intentsSeen: Set<Intent>,
        converted: Boolean,
        escalated: Boolean
    ) {
        scope.launch {
            analytics.logEvent("ai_session_end") {
                param("session_id", sessionId)
                param("duration_ms", duration)
                param("message_count", messageCount.toLong())
                param("unique_intents", intentsSeen.size.toLong())
                param("converted", converted.toString())
                param("escalated", escalated.toString())
                param("timestamp", System.currentTimeMillis())
            }
        }
    }
    
    /**
     * Obtiene métricas resumidas para dashboard
     */
    suspend fun getMetrics(timeRange: TimeRange = TimeRange.LAST_7_DAYS): AIMetricsSummary {
        // En un escenario real, esto consultaría Firebase Analytics o una base de datos
        // Por ahora retornamos datos mock para la estructura
        return AIMetricsSummary(
            totalInteractions = 0,
            averageResponseTime = 0.0,
            averageConfidence = 0.0f,
            topIntents = emptyMap(),
            conversionRate = 0.0f,
            escalationRate = 0.0f,
            userSatisfaction = 0.0f
        )
    }
}

/**
 * Tipos de conversión para tracking
 */
enum class ConversionType {
    PLAN_INTEREST,
    SCHEDULE_INSTALLATION,
    CONTACT_SALES,
    TECHNICAL_SUPPORT_RESOLVED,
    PAYMENT_COMPLETED,
    COVERAGE_CHECK
}

/**
 * Tipos de errores del sistema de IA
 */
enum class AIErrorType {
    MODEL_ERROR,
    TIMEOUT,
    PARSING_ERROR,
    NETWORK_ERROR,
    CONFIGURATION_ERROR,
    UNKNOWN_INTENT
}

/**
 * Rangos de tiempo para métricas
 */
enum class TimeRange {
    LAST_24_HOURS,
    LAST_7_DAYS,
    LAST_30_DAYS,
    LAST_90_DAYS
}

/**
 * Resumen de métricas del sistema de IA
 */
data class AIMetricsSummary(
    val totalInteractions: Int,
    val averageResponseTime: Double,
    val averageConfidence: Float,
    val topIntents: Map<Intent, Int>,
    val conversionRate: Float,
    val escalationRate: Float,
    val userSatisfaction: Float
)