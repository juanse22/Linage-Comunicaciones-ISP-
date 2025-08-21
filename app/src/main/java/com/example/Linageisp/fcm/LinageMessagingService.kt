package com.example.Linageisp.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.Linageisp.MainActivity
import com.example.Linageisp.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 🔔 Firebase Cloud Messaging Service para Linage ISP
 * 
 * Características:
 * - Manejo completo de tokens FCM
 * - Notifications foreground y background
 * - Deep linking a pantallas específicas
 * - Analytics tracking completo
 * - Custom notification layouts Material 3
 * - Segmentación automática por tipo de usuario
 */
@AndroidEntryPoint
class LinageMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmRepository: FCMRepository
    
    @Inject
    lateinit var linageNotificationManager: LinageNotificationManager
    
    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "LinageMessagingService"
        const val CHANNEL_ID_DEFAULT = "linage_default_channel"
        const val CHANNEL_ID_PROMOTIONS = "linage_promotions_channel"
        const val CHANNEL_ID_TECHNICAL = "linage_technical_channel"
        const val CHANNEL_ID_BILLING = "linage_billing_channel"
        
        // Deep link routes
        const val DEEP_LINK_PLANS = "linageisp://plans"
        const val DEEP_LINK_AI_ASSISTANT = "linageisp://ai_assistant"
        const val DEEP_LINK_SUPPORT = "linageisp://technical_support"
        const val DEEP_LINK_BILLING = "linageisp://billing"
        const val DEEP_LINK_BENEFITS = "linageisp://benefits"
        const val DEEP_LINK_SPEED_TEST = "linageisp://speed_test"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        Log.d(TAG, "🔔 LinageMessagingService creado")
    }

    /**
     * Manejo de nuevo token FCM
     * Se ejecuta cuando:
     * - La app se instala por primera vez
     * - Se restaura la app en un nuevo dispositivo
     * - El usuario desinstala/reinstala la app
     * - El token se renueva (rotación de seguridad)
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "🔑 Nuevo token FCM recibido: ${token.take(20)}...")
        
        serviceScope.launch {
            try {
                // Guardar token en repository
                fcmRepository.saveToken(token)
                
                // Registrar token en backend
                fcmRepository.registerTokenWithBackend(token)
                
                // Analytics event
                firebaseAnalytics.logEvent("fcm_token_refreshed", null)
                
                Log.d(TAG, "✅ Token FCM registrado exitosamente")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al manejar nuevo token FCM", e)
                firebaseAnalytics.logEvent("fcm_token_error", null)
            }
        }
    }

    /**
     * Manejo de mensajes recibidos cuando la app está en foreground
     * Para background, Firebase maneja automáticamente las notificaciones
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val from = remoteMessage.from ?: "unknown"
        Log.d(TAG, "📱 Mensaje FCM recibido de: $from")
        
        // Log analytics event
        val bundle = android.os.Bundle().apply {
            putString("notification_type", remoteMessage.data["type"] ?: "unknown")
            putString("notification_source", from)
        }
        firebaseAnalytics.logEvent("fcm_notification_received", bundle)

        // Determinar tipo de notificación
        val notificationType = remoteMessage.data["type"] ?: "default"
        
        when (notificationType) {
            "promotion" -> handlePromotionMessage(remoteMessage)
            "technical_alert" -> handleTechnicalMessage(remoteMessage)
            "billing_reminder" -> handleBillingMessage(remoteMessage)
            "new_plan" -> handleNewPlanMessage(remoteMessage)
            "ai_suggestion" -> handleAISuggestionMessage(remoteMessage)
            else -> handleDefaultMessage(remoteMessage)
        }
    }

    /**
     * Crear canales de notificación para Android 8.0+
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            
            // Canal por defecto
            val defaultChannel = NotificationChannel(
                CHANNEL_ID_DEFAULT,
                "Notificaciones Generales",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones generales de Linage ISP"
                enableLights(true)
                lightColor = android.graphics.Color.parseColor("#F37321") // Linage Orange
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
            }
            
            // Canal de promociones
            val promotionsChannel = NotificationChannel(
                CHANNEL_ID_PROMOTIONS,
                "Promociones y Ofertas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Promociones especiales y ofertas de planes"
                enableLights(true)
                lightColor = android.graphics.Color.parseColor("#F37321")
                enableVibration(true)
            }
            
            // Canal técnico
            val technicalChannel = NotificationChannel(
                CHANNEL_ID_TECHNICAL,
                "Soporte Técnico",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas técnicas y mantenimiento"
                enableLights(true)
                lightColor = android.graphics.Color.parseColor("#FF5722") // Red for alerts
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 300, 500)
            }
            
            // Canal de facturación
            val billingChannel = NotificationChannel(
                CHANNEL_ID_BILLING,
                "Facturación",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios de pago y facturación"
                enableLights(true)
                lightColor = android.graphics.Color.parseColor("#4CAF50") // Green for billing
                enableVibration(true)
            }
            
            notificationManager.createNotificationChannels(listOf(
                defaultChannel,
                promotionsChannel,
                technicalChannel,
                billingChannel
            ))
            
            Log.d(TAG, "📢 Canales de notificación creados")
        }
    }

    /**
     * Manejar mensajes de promociones
     */
    private fun handlePromotionMessage(message: RemoteMessage) {
        val title = message.data["title"] ?: "🎉 Nueva Promoción Linage"
        val body = message.data["body"] ?: "Descubre nuestras ofertas especiales"
        val deepLink = message.data["deep_link"] ?: DEEP_LINK_PLANS
        
        val intent = createDeepLinkIntent(deepLink).apply {
            putExtra("promotion_id", message.data["promotion_id"])
            putExtra("campaign_id", message.data["campaign_id"])
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_PROMOTIONS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setColor(android.graphics.Color.parseColor("#F37321"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .addAction(R.mipmap.ic_launcher, "Ver Oferta", pendingIntent)
            .build()
            
        linageNotificationManager.showNotification(
            id = System.currentTimeMillis().toInt(),
            notification = notification
        )
        
        // Analytics
        val bundle = android.os.Bundle().apply {
            putString("promotion_id", message.data["promotion_id"])
            putString("campaign_id", message.data["campaign_id"])
        }
        firebaseAnalytics.logEvent("promotion_notification_shown", bundle)
    }

    /**
     * Manejar mensajes técnicos
     */
    private fun handleTechnicalMessage(message: RemoteMessage) {
        val title = message.data["title"] ?: "🔧 Alerta Técnica"
        val body = message.data["body"] ?: "Información técnica importante"
        val severity = message.data["severity"] ?: "medium"
        
        val intent = createDeepLinkIntent(DEEP_LINK_SUPPORT).apply {
            putExtra("alert_type", "technical")
            putExtra("severity", severity)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_TECHNICAL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setColor(android.graphics.Color.parseColor("#FF5722"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(if (severity == "high") NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()
            
        linageNotificationManager.showNotification(
            id = System.currentTimeMillis().toInt(),
            notification = notification
        )
    }

    /**
     * Manejar mensajes de facturación
     */
    private fun handleBillingMessage(message: RemoteMessage) {
        val title = message.data["title"] ?: "💳 Recordatorio de Pago"
        val body = message.data["body"] ?: "Tu factura está lista"
        
        val intent = createDeepLinkIntent(DEEP_LINK_BILLING).apply {
            putExtra("bill_id", message.data["bill_id"])
            putExtra("amount", message.data["amount"])
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_BILLING)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setColor(android.graphics.Color.parseColor("#4CAF50"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .addAction(R.mipmap.ic_launcher, "Ver Factura", pendingIntent)
            .build()
            
        linageNotificationManager.showNotification(
            id = System.currentTimeMillis().toInt(),
            notification = notification
        )
    }

    /**
     * Manejar mensajes de nuevos planes
     */
    private fun handleNewPlanMessage(message: RemoteMessage) {
        val title = message.data["title"] ?: "🚀 Nuevo Plan Disponible"
        val body = message.data["body"] ?: "Descubre nuestro nuevo plan de internet"
        
        val intent = createDeepLinkIntent(DEEP_LINK_PLANS).apply {
            putExtra("plan_id", message.data["plan_id"])
            putExtra("highlight_plan", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_PROMOTIONS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setColor(android.graphics.Color.parseColor("#F37321"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .addAction(R.mipmap.ic_launcher, "Ver Plan", pendingIntent)
            .build()
            
        linageNotificationManager.showNotification(
            id = System.currentTimeMillis().toInt(),
            notification = notification
        )
    }

    /**
     * Manejar mensajes de sugerencias de IA
     */
    private fun handleAISuggestionMessage(message: RemoteMessage) {
        val title = message.data["title"] ?: "🤖 LINA tiene una sugerencia"
        val body = message.data["body"] ?: "Tu asistente virtual tiene algo para ti"
        
        val intent = createDeepLinkIntent(DEEP_LINK_AI_ASSISTANT).apply {
            putExtra("ai_suggestion", message.data["suggestion"])
            putExtra("context", message.data["context"])
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setColor(android.graphics.Color.parseColor("#F37321"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .addAction(R.mipmap.ic_launcher, "Hablar con LINA", pendingIntent)
            .build()
            
        linageNotificationManager.showNotification(
            id = System.currentTimeMillis().toInt(),
            notification = notification
        )
    }

    /**
     * Manejar mensajes por defecto
     */
    private fun handleDefaultMessage(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "Linage ISP"
        val body = message.notification?.body ?: message.data["body"] ?: "Nueva notificación"
        val deepLink = message.data["deep_link"] ?: "linageisp://home"
        
        val intent = createDeepLinkIntent(deepLink)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setColor(android.graphics.Color.parseColor("#F37321"))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()
            
        linageNotificationManager.showNotification(
            id = System.currentTimeMillis().toInt(),
            notification = notification
        )
    }

    /**
     * Crear Intent para deep linking
     */
    private fun createDeepLinkIntent(deepLink: String): Intent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("deep_link", deepLink)
            putExtra("from_notification", true)
            putExtra("timestamp", System.currentTimeMillis())
        }
        
        // Mapear deep links a navegación interna
        when (deepLink) {
            DEEP_LINK_PLANS -> intent.putExtra("navigate_to", "plans")
            DEEP_LINK_AI_ASSISTANT -> intent.putExtra("navigate_to", "ai_assistant")
            DEEP_LINK_SUPPORT -> intent.putExtra("navigate_to", "technical_support")
            DEEP_LINK_BILLING -> intent.putExtra("navigate_to", "billing")
            DEEP_LINK_BENEFITS -> intent.putExtra("navigate_to", "benefits")
            DEEP_LINK_SPEED_TEST -> intent.putExtra("navigate_to", "speed_test")
            else -> intent.putExtra("navigate_to", "home")
        }
        
        return intent
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "🔔 LinageMessagingService destruido")
    }
}