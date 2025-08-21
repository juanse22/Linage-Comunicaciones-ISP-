package com.example.Linageisp.fcm

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🔔 Gestor de notificaciones para Linage ISP
 * 
 * Características:
 * - Gestión centralizada de notificaciones
 * - Configuración de preferencias por usuario
 * - Rate limiting para evitar spam
 * - Analytics de engagement
 * - Soporte para Android 13+ permissions
 * - Badges animados para promociones
 */
@Singleton
class LinageNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAnalytics: FirebaseAnalytics
) {
    
    companion object {
        private const val TAG = "LinageNotificationManager"
        private const val PREFS_NAME = "linage_notification_prefs"
        
        // Rate limiting
        private const val MAX_NOTIFICATIONS_PER_HOUR = 5
        private const val RATE_LIMIT_WINDOW = 3600_000L // 1 hour
        
        // Preference keys
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_PROMOTIONS_ENABLED = "promotions_enabled"
        private const val KEY_TECHNICAL_ENABLED = "technical_enabled"
        private const val KEY_BILLING_ENABLED = "billing_enabled"
        private const val KEY_AI_SUGGESTIONS_ENABLED = "ai_suggestions_enabled"
        private const val KEY_QUIET_HOURS_ENABLED = "quiet_hours_enabled"
        private const val KEY_QUIET_HOURS_START = "quiet_hours_start"
        private const val KEY_QUIET_HOURS_END = "quiet_hours_end"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val notificationManager = NotificationManagerCompat.from(context)
    
    private val notificationHistory = mutableListOf<NotificationRecord>()
    
    private val _notificationPreferences = MutableStateFlow(getNotificationPreferences())
    val notificationPreferences: Flow<NotificationPreferences> = _notificationPreferences.asStateFlow()
    
    private val _activeBadges = MutableStateFlow<Map<String, Int>>(emptyMap())
    val activeBadges: Flow<Map<String, Int>> = _activeBadges.asStateFlow()

    /**
     * Data class para preferencias de notificaciones
     */
    data class NotificationPreferences(
        val notificationsEnabled: Boolean = true,
        val promotionsEnabled: Boolean = true,
        val technicalEnabled: Boolean = true,
        val billingEnabled: Boolean = true,
        val aiSuggestionsEnabled: Boolean = true,
        val quietHoursEnabled: Boolean = false,
        val quietHoursStart: Int = 22, // 10 PM
        val quietHoursEnd: Int = 8,   // 8 AM
        val vibrationEnabled: Boolean = true,
        val soundEnabled: Boolean = true
    )

    /**
     * Record de notificación para rate limiting y analytics
     */
    private data class NotificationRecord(
        val id: Int,
        val type: String,
        val timestamp: Long,
        val title: String,
        val shown: Boolean = true
    )

    /**
     * Mostrar notificación con validaciones
     */
    fun showNotification(
        id: Int,
        notification: Notification,
        type: String = "default",
        title: String = ""
    ) {
        try {
            // Verificar permisos
            if (!areNotificationsEnabled()) {
                Log.w(TAG, "⚠️ Notificaciones deshabilitadas por el usuario")
                logNotificationEvent("notification_permission_denied", type)
                return
            }
            
            // Verificar preferencias específicas del tipo
            if (!isNotificationTypeEnabled(type)) {
                Log.w(TAG, "⚠️ Tipo de notificación deshabilitado: $type")
                logNotificationEvent("notification_type_disabled", type)
                return
            }
            
            // Rate limiting
            if (!isWithinRateLimit(type)) {
                Log.w(TAG, "⚠️ Rate limit excedido para tipo: $type")
                logNotificationEvent("notification_rate_limited", type)
                return
            }
            
            // Verificar quiet hours
            if (isInQuietHours()) {
                Log.d(TAG, "🤫 En horario silencioso, no mostrando notificación")
                logNotificationEvent("notification_quiet_hours", type)
                return
            }
            
            // Mostrar notificación
            notificationManager.notify(id, notification)
            
            // Registrar en historial
            val record = NotificationRecord(
                id = id,
                type = type,
                timestamp = System.currentTimeMillis(),
                title = title,
                shown = true
            )
            addToHistory(record)
            
            // Log analytics
            logNotificationEvent("notification_shown", type)
            
            Log.d(TAG, "✅ Notificación mostrada: ID=$id, Type=$type")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al mostrar notificación", e)
            logNotificationEvent("notification_error", type)
        }
    }

    /**
     * Verificar si las notificaciones están habilitadas
     */
    fun areNotificationsEnabled(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return notificationManager.areNotificationsEnabled()
        }
        return true
    }

    /**
     * Verificar si un tipo específico de notificación está habilitado
     */
    private fun isNotificationTypeEnabled(type: String): Boolean {
        val prefs = _notificationPreferences.value
        
        if (!prefs.notificationsEnabled) return false
        
        return when (type) {
            "promotion" -> prefs.promotionsEnabled
            "technical_alert" -> prefs.technicalEnabled
            "billing_reminder" -> prefs.billingEnabled
            "ai_suggestion" -> prefs.aiSuggestionsEnabled
            else -> true
        }
    }

    /**
     * Verificar rate limiting
     */
    private fun isWithinRateLimit(type: String): Boolean {
        val now = System.currentTimeMillis()
        val cutoff = now - RATE_LIMIT_WINDOW
        
        // Limpiar historial antiguo
        notificationHistory.removeAll { it.timestamp < cutoff }
        
        // Contar notificaciones del mismo tipo en la última hora
        val recentCount = notificationHistory.count { 
            it.type == type && it.timestamp > cutoff && it.shown 
        }
        
        return recentCount < MAX_NOTIFICATIONS_PER_HOUR
    }

    /**
     * Verificar si estamos en horario silencioso
     */
    private fun isInQuietHours(): Boolean {
        val prefs = _notificationPreferences.value
        
        if (!prefs.quietHoursEnabled) return false
        
        val calendar = java.util.Calendar.getInstance()
        val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        
        return if (prefs.quietHoursStart <= prefs.quietHoursEnd) {
            // Mismo día (ej: 22:00 a 08:00 del día siguiente)
            currentHour >= prefs.quietHoursStart || currentHour < prefs.quietHoursEnd
        } else {
            // Rango normal (ej: 08:00 a 22:00)
            currentHour >= prefs.quietHoursStart && currentHour < prefs.quietHoursEnd
        }
    }

    /**
     * Añadir al historial
     */
    private fun addToHistory(record: NotificationRecord) {
        notificationHistory.add(record)
        
        // Mantener solo las últimas 100 notificaciones
        if (notificationHistory.size > 100) {
            notificationHistory.removeAt(0)
        }
    }

    /**
     * Actualizar preferencias de notificaciones
     */
    fun updateNotificationPreferences(preferences: NotificationPreferences) {
        prefs.edit()
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, preferences.notificationsEnabled)
            .putBoolean(KEY_PROMOTIONS_ENABLED, preferences.promotionsEnabled)
            .putBoolean(KEY_TECHNICAL_ENABLED, preferences.technicalEnabled)
            .putBoolean(KEY_BILLING_ENABLED, preferences.billingEnabled)
            .putBoolean(KEY_AI_SUGGESTIONS_ENABLED, preferences.aiSuggestionsEnabled)
            .putBoolean(KEY_QUIET_HOURS_ENABLED, preferences.quietHoursEnabled)
            .putInt(KEY_QUIET_HOURS_START, preferences.quietHoursStart)
            .putInt(KEY_QUIET_HOURS_END, preferences.quietHoursEnd)
            .putBoolean(KEY_VIBRATION_ENABLED, preferences.vibrationEnabled)
            .putBoolean(KEY_SOUND_ENABLED, preferences.soundEnabled)
            .apply()
            
        _notificationPreferences.value = preferences
        
        Log.d(TAG, "⚙️ Preferencias de notificaciones actualizadas")
        logNotificationEvent("notification_preferences_updated", "settings")
    }

    /**
     * Obtener preferencias actuales
     */
    private fun getNotificationPreferences(): NotificationPreferences {
        return NotificationPreferences(
            notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true),
            promotionsEnabled = prefs.getBoolean(KEY_PROMOTIONS_ENABLED, true),
            technicalEnabled = prefs.getBoolean(KEY_TECHNICAL_ENABLED, true),
            billingEnabled = prefs.getBoolean(KEY_BILLING_ENABLED, true),
            aiSuggestionsEnabled = prefs.getBoolean(KEY_AI_SUGGESTIONS_ENABLED, true),
            quietHoursEnabled = prefs.getBoolean(KEY_QUIET_HOURS_ENABLED, false),
            quietHoursStart = prefs.getInt(KEY_QUIET_HOURS_START, 22),
            quietHoursEnd = prefs.getInt(KEY_QUIET_HOURS_END, 8),
            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION_ENABLED, true),
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        )
    }

    /**
     * Gestión de badges para bottom navigation
     */
    fun addBadge(route: String, count: Int = 1) {
        val currentBadges = _activeBadges.value.toMutableMap()
        currentBadges[route] = (currentBadges[route] ?: 0) + count
        _activeBadges.value = currentBadges
        
        Log.d(TAG, "🔴 Badge añadido: $route = ${currentBadges[route]}")
        logNotificationEvent("badge_added", route)
    }

    /**
     * Remover badge
     */
    fun removeBadge(route: String) {
        val currentBadges = _activeBadges.value.toMutableMap()
        currentBadges.remove(route)
        _activeBadges.value = currentBadges
        
        Log.d(TAG, "⚪ Badge removido: $route")
        logNotificationEvent("badge_removed", route)
    }

    /**
     * Limpiar todos los badges
     */
    fun clearAllBadges() {
        _activeBadges.value = emptyMap()
        Log.d(TAG, "🧹 Todos los badges limpiados")
        logNotificationEvent("badges_cleared", "all")
    }

    /**
     * Cancelar notificación específica
     */
    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
        Log.d(TAG, "❌ Notificación cancelada: ID=$id")
    }

    /**
     * Cancelar todas las notificaciones
     */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
        Log.d(TAG, "🧹 Todas las notificaciones canceladas")
    }

    /**
     * Obtener estadísticas de notificaciones
     */
    fun getNotificationStats(): NotificationStats {
        val now = System.currentTimeMillis()
        val last24h = now - (24 * 60 * 60 * 1000)
        val last7d = now - (7 * 24 * 60 * 60 * 1000)
        
        val recent24h = notificationHistory.filter { it.timestamp > last24h && it.shown }
        val recent7d = notificationHistory.filter { it.timestamp > last7d && it.shown }
        
        return NotificationStats(
            totalNotifications = notificationHistory.size,
            notificationsLast24h = recent24h.size,
            notificationsLast7d = recent7d.size,
            notificationsByType = notificationHistory.groupBy { it.type }.mapValues { it.value.size },
            lastNotificationTime = notificationHistory.maxByOrNull { it.timestamp }?.timestamp
        )
    }

    /**
     * Estadísticas de notificaciones
     */
    data class NotificationStats(
        val totalNotifications: Int,
        val notificationsLast24h: Int,
        val notificationsLast7d: Int,
        val notificationsByType: Map<String, Int>,
        val lastNotificationTime: Long?
    )

    /**
     * Log evento de analytics
     */
    private fun logNotificationEvent(event: String, type: String) {
        val bundle = android.os.Bundle().apply {
            putString("notification_type", type)
            putLong("timestamp", System.currentTimeMillis())
        }
        firebaseAnalytics.logEvent(event, bundle)
    }

    /**
     * Solicitar permisos de notificación para Android 13+
     */
    fun shouldRequestNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && 
               !notificationManager.areNotificationsEnabled()
    }

    /**
     * Verificar si los canales de notificación están configurados
     */
    fun areNotificationChannelsConfigured(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val systemNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channels = systemNotificationManager.notificationChannels
            
            val requiredChannels = listOf(
                LinageMessagingService.CHANNEL_ID_DEFAULT,
                LinageMessagingService.CHANNEL_ID_PROMOTIONS,
                LinageMessagingService.CHANNEL_ID_TECHNICAL,
                LinageMessagingService.CHANNEL_ID_BILLING
            )
            
            return requiredChannels.all { channelId ->
                channels.any { it.id == channelId }
            }
        }
        return true
    }
}