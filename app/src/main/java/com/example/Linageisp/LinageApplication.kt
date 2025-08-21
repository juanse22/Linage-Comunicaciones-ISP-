package com.example.Linageisp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

/**
 * 🚀 Aplicación principal de Linage ISP
 * 
 * Características:
 * - Inicialización de Firebase completa
 * - Configuración de FCM automática
 * - Analytics y Crashlytics habilitados
 * - Dependency injection con Hilt
 */
@HiltAndroidApp
class LinageApplication : Application() {
    
    companion object {
        private const val TAG = "LinageApplication"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        Log.d(TAG, "🚀 Inicializando Linage ISP Application")
        
        try {
            // Inicializar Firebase
            initializeFirebase()
            
            // Configurar FCM
            initializeFCM()
            
            Log.d(TAG, "✅ Linage Application inicializada correctamente")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error inicializando aplicación", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
    
    /**
     * Inicializar Firebase y servicios base
     */
    private fun initializeFirebase() {
        try {
            // Inicializar Firebase App
            FirebaseApp.initializeApp(this)
            
            // Configurar Analytics
            FirebaseAnalytics.getInstance(this).apply {
                setAnalyticsCollectionEnabled(true)
                setUserId("linage_user_${System.currentTimeMillis()}")
                setUserProperty("app_version", BuildConfig.VERSION_NAME)
                setUserProperty("build_type", BuildConfig.BUILD_TYPE)
            }
            
            // Configurar Crashlytics
            FirebaseCrashlytics.getInstance().apply {
                setCrashlyticsCollectionEnabled(true)
                setCustomKey("app_package", packageName)
                setCustomKey("app_version", BuildConfig.VERSION_NAME)
                setCustomKey("build_type", BuildConfig.BUILD_TYPE)
            }
            
            Log.d(TAG, "🔥 Firebase inicializado correctamente")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error inicializando Firebase", e)
            throw e
        }
    }
    
    /**
     * Inicializar Firebase Cloud Messaging
     */
    private fun initializeFCM() {
        try {
            FirebaseMessaging.getInstance().apply {
                // Configurar auto-init
                isAutoInitEnabled = true
                
                // Obtener token inicial
                token.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "⚠️ Error obteniendo token FCM", task.exception)
                        return@addOnCompleteListener
                    }
                    
                    val token = task.result
                    Log.d(TAG, "🔑 Token FCM obtenido: ${token.take(20)}...")
                    
                    // Analytics de inicialización exitosa
                    FirebaseAnalytics.getInstance(this@LinageApplication).logEvent(
                        "fcm_token_initialized", 
                        null
                    )
                }
                
                // Suscribirse a tópico general
                subscribeToTopic("linage_all_users").addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "📡 Suscrito a tópico general")
                    } else {
                        Log.w(TAG, "⚠️ Error suscribiéndose a tópico general", task.exception)
                    }
                }
            }
            
            Log.d(TAG, "🔔 FCM inicializado correctamente")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error inicializando FCM", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}