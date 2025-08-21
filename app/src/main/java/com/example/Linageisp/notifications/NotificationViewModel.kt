package com.example.Linageisp.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Linageisp.fcm.FCMRepository
import com.example.Linageisp.fcm.LinageNotificationManager
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 🔔 ViewModel para gestión de notificaciones en Linage ISP
 * 
 * Características:
 * - Estado reactivo de notificaciones
 * - Gestión de permisos Android 13+
 * - Preferencias de usuario
 * - Analytics de engagement
 * - Badges para bottom navigation
 * - Campaigns in-app
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val fcmRepository: FCMRepository,
    private val notificationManager: LinageNotificationManager,
    private val firebaseAnalytics: FirebaseAnalytics
) : ViewModel() {

    // Estados del ViewModel
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    // Flujos de datos
    val notificationPreferences = notificationManager.notificationPreferences
    val activeBadges = notificationManager.activeBadges
    val userSegments = fcmRepository.userSegments
    val tokenState = fcmRepository.tokenState

    // Campaigns activas
    private val _activeCampaigns = MutableStateFlow<List<Campaign>>(emptyList())
    val activeCampaigns: StateFlow<List<Campaign>> = _activeCampaigns.asStateFlow()

    // In-app messages
    private val _inAppMessages = MutableStateFlow<List<InAppMessage>>(emptyList())
    val inAppMessages: StateFlow<List<InAppMessage>> = _inAppMessages.asStateFlow()

    init {
        initializeNotifications()
        loadActiveCampaigns()
    }

    /**
     * Estado del UI
     */
    data class NotificationUiState(
        val isLoading: Boolean = false,
        val permissionRequested: Boolean = false,
        val hasNotificationPermission: Boolean = true,
        val showPermissionRationale: Boolean = false,
        val error: String? = null,
        val fcmTokenReady: Boolean = false,
        val lastSyncTime: Long? = null
    )

    /**
     * Campaign data class
     */
    data class Campaign(
        val id: String,
        val title: String,
        val description: String,
        val imageUrl: String? = null,
        val ctaText: String,
        val ctaAction: String,
        val priority: Int = 0,
        val startTime: Long,
        val endTime: Long,
        val targetSegments: List<String> = emptyList(),
        val isActive: Boolean = true,
        val type: CampaignType = CampaignType.PROMOTION
    )

    enum class CampaignType {
        PROMOTION,
        NEW_FEATURE,
        TECHNICAL_UPDATE,
        BILLING_REMINDER,
        SURVEY
    }

    /**
     * In-app message data class
     */
    data class InAppMessage(
        val id: String,
        val title: String,
        val message: String,
        val type: MessageType,
        val priority: Int = 0,
        val timestamp: Long,
        val actionText: String? = null,
        val actionRoute: String? = null,
        val dismissible: Boolean = true,
        val autoHide: Boolean = true,
        val hideAfterMs: Long = 5000
    )

    enum class MessageType {
        INFO,
        SUCCESS,
        WARNING,
        ERROR,
        PROMOTION
    }

    /**
     * Inicializar sistema de notificaciones
     */
    private fun initializeNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Verificar permisos
                val hasPermission = notificationManager.areNotificationsEnabled()
                val shouldRequest = notificationManager.shouldRequestNotificationPermission()
                
                _uiState.value = _uiState.value.copy(
                    hasNotificationPermission = hasPermission,
                    showPermissionRationale = shouldRequest && !hasPermission
                )
                
                // Obtener token FCM
                val token = fcmRepository.getCurrentToken()
                if (token != null) {
                    _uiState.value = _uiState.value.copy(fcmTokenReady = true)
                    
                    // Suscribirse a tópicos
                    fcmRepository.subscribeToTopics()
                    
                    logEvent("fcm_initialization_success")
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Error al obtener token FCM"
                    )
                    logEvent("fcm_initialization_error")
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastSyncTime = System.currentTimeMillis()
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
                logEvent("fcm_initialization_exception")
            }
        }
    }

    /**
     * Solicitar permisos de notificación
     */
    fun requestNotificationPermission() {
        _uiState.value = _uiState.value.copy(
            permissionRequested = true,
            showPermissionRationale = false
        )
        
        logEvent("notification_permission_requested")
    }

    /**
     * Callback cuando se otorga el permiso
     */
    fun onPermissionGranted() {
        _uiState.value = _uiState.value.copy(
            hasNotificationPermission = true,
            showPermissionRationale = false
        )
        
        // Reinicializar FCM
        initializeNotifications()
        
        logEvent("notification_permission_granted")
    }

    /**
     * Callback cuando se deniega el permiso
     */
    fun onPermissionDenied() {
        _uiState.value = _uiState.value.copy(
            hasNotificationPermission = false,
            showPermissionRationale = true
        )
        
        logEvent("notification_permission_denied")
    }

    /**
     * Actualizar preferencias de notificaciones
     */
    fun updatePreferences(preferences: LinageNotificationManager.NotificationPreferences) {
        notificationManager.updateNotificationPreferences(preferences)
        logEvent("notification_preferences_updated")
    }

    /**
     * Limpiar error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Añadir badge a navegación
     */
    fun addBadge(route: String, count: Int = 1) {
        notificationManager.addBadge(route, count)
    }

    /**
     * Remover badge
     */
    fun removeBadge(route: String) {
        notificationManager.removeBadge(route)
    }

    /**
     * Cargar campaigns activas
     */
    private fun loadActiveCampaigns() {
        viewModelScope.launch {
            try {
                // Aquí cargarías campaigns desde Firebase Remote Config o Firestore
                val mockCampaigns = listOf(
                    Campaign(
                        id = "promo_fiber_2024",
                        title = "🚀 ¡Nueva Fibra Óptica!",
                        description = "Velocidades hasta 900 Mbps. Instala ahora y recibe 2 meses gratis.",
                        ctaText = "Ver Planes",
                        ctaAction = "navigate:plans",
                        startTime = System.currentTimeMillis() - 86400000, // Ayer
                        endTime = System.currentTimeMillis() + 604800000, // 7 días
                        targetSegments = listOf("potential_customer", "inactive"),
                        type = CampaignType.PROMOTION
                    ),
                    Campaign(
                        id = "football_win_sports",
                        title = "⚽ Win Sports+ GRATIS",
                        description = "Con tu plan de 200MB o más, disfruta fútbol colombiano sin costo adicional.",
                        ctaText = "Activar Win+",
                        ctaAction = "navigate:benefits",
                        startTime = System.currentTimeMillis() - 172800000, // 2 días atrás
                        endTime = System.currentTimeMillis() + 1209600000, // 14 días
                        targetSegments = listOf("linage_customer", "active"),
                        type = CampaignType.NEW_FEATURE
                    )
                )
                
                // Filtrar campaigns por segmentos del usuario
                val currentSegments = userSegments.firstOrNull() ?: emptySet()
                val filteredCampaigns = mockCampaigns.filter { campaign ->
                    campaign.isActive && 
                    System.currentTimeMillis() in campaign.startTime..campaign.endTime &&
                    (campaign.targetSegments.isEmpty() || campaign.targetSegments.any { it in currentSegments })
                }
                
                _activeCampaigns.value = filteredCampaigns
                
                // Añadir badges si hay campaigns
                if (filteredCampaigns.isNotEmpty()) {
                    notificationManager.addBadge("benefits", filteredCampaigns.size)
                }
                
                logEvent("campaigns_loaded", mapOf("count" to filteredCampaigns.size))
                
            } catch (e: Exception) {
                logEvent("campaigns_load_error")
            }
        }
    }

    /**
     * Mostrar mensaje in-app
     */
    fun showInAppMessage(message: InAppMessage) {
        val currentMessages = _inAppMessages.value.toMutableList()
        currentMessages.add(message)
        _inAppMessages.value = currentMessages
        
        // Auto-hide si está configurado
        if (message.autoHide) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(message.hideAfterMs)
                dismissInAppMessage(message.id)
            }
        }
        
        logEvent("in_app_message_shown", mapOf("type" to message.type.name))
    }

    /**
     * Dismiss mensaje in-app
     */
    fun dismissInAppMessage(messageId: String) {
        val currentMessages = _inAppMessages.value.toMutableList()
        currentMessages.removeAll { it.id == messageId }
        _inAppMessages.value = currentMessages
        
        logEvent("in_app_message_dismissed")
    }

    /**
     * Manejar click en campaign
     */
    fun onCampaignClick(campaign: Campaign) {
        logEvent("campaign_clicked", mapOf(
            "campaign_id" to campaign.id,
            "campaign_type" to campaign.type.name
        ))
        
        // El MainActivity manejará la navegación basada en ctaAction
    }

    /**
     * Manejar notificación recibida
     */
    fun onNotificationReceived(type: String, data: Map<String, String>) {
        logEvent("notification_received", mapOf("type" to type))
        
        // Añadir badge según el tipo
        when (type) {
            "promotion" -> addBadge("benefits")
            "technical_alert" -> addBadge("account")
            "billing_reminder" -> addBadge("account")
            "new_plan" -> addBadge("plans")
            "ai_suggestion" -> addBadge("ai_assistant")
        }
        
        // Mostrar in-app message si es apropiado
        if (type == "promotion") {
            val message = InAppMessage(
                id = "promo_${System.currentTimeMillis()}",
                title = data["title"] ?: "Nueva Promoción",
                message = data["body"] ?: "Tienes una nueva oferta disponible",
                type = MessageType.PROMOTION,
                priority = 0,
                timestamp = System.currentTimeMillis(),
                actionText = "Ver",
                actionRoute = data["deep_link"]
            )
            showInAppMessage(message)
        }
    }

    /**
     * Obtener estadísticas de notificaciones
     */
    fun getNotificationStats(): LinageNotificationManager.NotificationStats {
        return notificationManager.getNotificationStats()
    }

    /**
     * Actualizar segmentación del usuario
     */
    fun updateUserSegmentation(
        isLinageCustomer: Boolean,
        planType: String? = null,
        hasActiveService: Boolean = false
    ) {
        val segmentData = FCMRepository.UserSegmentData(
            isLinageCustomer = isLinageCustomer,
            planType = planType,
            hasActiveService = hasActiveService,
            lastActivity = System.currentTimeMillis()
        )
        
        fcmRepository.updateUserSegmentData(segmentData)
        
        // Recargar campaigns con nueva segmentación
        loadActiveCampaigns()
        
        logEvent("user_segmentation_updated", mapOf(
            "is_customer" to isLinageCustomer,
            "plan_type" to (planType ?: "none"),
            "has_active_service" to hasActiveService
        ))
    }

    /**
     * Limpiar datos de FCM (logout)
     */
    fun clearFCMData() {
        viewModelScope.launch {
            fcmRepository.clearFCMData()
            notificationManager.clearAllBadges()
            _activeCampaigns.value = emptyList()
            _inAppMessages.value = emptyList()
            
            _uiState.value = NotificationUiState()
            
            logEvent("fcm_data_cleared")
        }
    }

    /**
     * Forzar refresh de token
     */
    fun refreshFCMToken() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val token = fcmRepository.getCurrentToken()
                if (token != null) {
                    fcmRepository.registerTokenWithBackend(token)
                    fcmRepository.subscribeToTopics()
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        fcmTokenReady = true,
                        lastSyncTime = System.currentTimeMillis()
                    )
                    
                    logEvent("fcm_token_refreshed_manual")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al refrescar token FCM"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Log analytics event
     */
    private fun logEvent(event: String, parameters: Map<String, Any> = emptyMap()) {
        val bundle = android.os.Bundle()
        parameters.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                is Double -> bundle.putDouble(key, value)
            }
        }
        firebaseAnalytics.logEvent(event, bundle)
    }
}