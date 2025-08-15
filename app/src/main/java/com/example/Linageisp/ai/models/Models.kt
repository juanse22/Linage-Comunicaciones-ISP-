package com.example.Linageisp.ai.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import java.time.LocalDateTime

/**
 * Mensaje de chat con información completa
 */
@Stable
data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val isTyping: Boolean = false,
    val quickActions: List<QuickAction> = emptyList(),
    val intent: Intent? = null,
    val confidence: Float = 0f
)

/**
 * Información de planes de Linage
 */
@Immutable
data class PlanInfo(
    val id: String,
    val name: String,
    val type: PlanType,
    val speed: String,
    val price: String,
    val benefits: List<String>,
    val includes: List<String>,
    val isPopular: Boolean = false,
    val hasFootball: Boolean = false,
    val hasNetflix: Boolean = false,
    val hasParamount: Boolean = false,
    val hasCameras: Boolean = false
)

/**
 * Tipos de planes disponibles
 */
enum class PlanType {
    WIN_PLUS,
    VIP,
    NETFLIX,
    CAMERAS,
    BASIC
}

/**
 * Contexto de conversación para seguimiento
 */
data class ChatContext(
    val userId: String,
    val sessionId: String,
    val conversationHistory: List<ChatMessage>,
    val currentIntent: Intent? = null,
    val lastPlanInterest: PlanType? = null,
    val technicalIssue: String? = null,
    val customerLocation: String? = null,
    val isNewCustomer: Boolean = true,
    val currentPlan: PlanInfo? = null,
    val location: String? = null,
    val accountStatus: String? = null
)

/**
 * Respuesta del sistema de IA
 */
data class AIResponse(
    val content: String,
    val intent: Intent,
    val confidence: Float,
    val quickActions: List<QuickAction>,
    val shouldEscalate: Boolean = false,
    val suggestedPlans: List<PlanInfo> = emptyList(),
    val responseTime: Long = System.currentTimeMillis(),
    val metadata: ResponseMetadata? = null
)

/**
 * Metadatos de respuesta de IA
 */
data class ResponseMetadata(
    val processingTime: Long,
    val intent: Intent,
    val model: String
)

/**
 * Acciones rápidas contextuales
 */
@Immutable
data class QuickAction(
    val id: String,
    val text: String,
    val action: ActionType,
    val icon: String? = null,
    val data: Map<String, String> = emptyMap()
)

/**
 * Tipos de acciones rápidas
 */
enum class ActionType {
    VIEW_PLAN,
    CONTACT_SUPPORT,
    CHECK_COVERAGE,
    SPEED_TEST,
    SCHEDULE_CALL,
    BILLING_INFO,
    TECHNICAL_HELP,
    VIEW_CHANNELS,
    RESTART_ROUTER,
    HELP,
    SHOW_PLANS,
    CONTRACT,
    GUIDE,
    TOOL,
    SUPPORT,
    NEW_CHAT
}

/**
 * Intenciones detectadas por el sistema de IA
 */
enum class Intent {
    // Planes y ventas
    PLANS_FOOTBALL,
    PLANS_GENERAL,
    PLANS_VIP,
    PLANS_NETFLIX,
    PLANS_CAMERAS,
    
    // Soporte técnico
    TECHNICAL_SUPPORT,
    INTERNET_SLOW,
    NO_CONNECTION,
    ROUTER_ISSUES,
    
    // Servicios y facturación
    BILLING,
    PAYMENT_HELP,
    SERVICE_ACTIVATION,
    
    // Información general
    COVERAGE,
    STREAMING,
    CHANNEL_INFO,
    CAMERAS,
    
    // Interacciones básicas
    GREETING,
    GOODBYE,
    HELP,
    
    // Estados especiales
    UNKNOWN,
    ESCALATE_HUMAN,
    ABOUT_AI,
    GENERAL
}

/**
 * Estados de conexión del sistema
 */
enum class ConnectionState {
    CONNECTED,
    CONNECTING,
    OFFLINE,
    ERROR
}

/**
 * Métricas de rendimiento de la IA
 */
data class AIMetrics(
    val responseTime: Long,
    val tokensUsed: Int,
    val confidence: Float,
    val intent: Intent,
    val satisfied: Boolean? = null,
    val escalated: Boolean = false
)

/**
 * Configuración de respuestas personalizadas
 */
data class ResponseConfig(
    val useEmojis: Boolean = true,
    val maxResponseLength: Int = 500,
    val includeQuickActions: Boolean = true,
    val streamingEnabled: Boolean = true,
    val personalityLevel: PersonalityLevel = PersonalityLevel.FRIENDLY
)

/**
 * Niveles de personalidad para LINA
 */
enum class PersonalityLevel {
    FORMAL,
    FRIENDLY,
    ENTHUSIASTIC,
    TECHNICAL
}

/**
 * Estado de la sesión de chat
 */
data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.OFFLINE,
    val context: ChatContext? = null,
    val suggestions: List<String> = emptyList(),
    val error: String? = null
)