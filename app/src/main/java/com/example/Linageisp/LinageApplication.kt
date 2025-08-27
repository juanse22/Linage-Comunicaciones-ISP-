package com.example.Linageisp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.example.Linageisp.performance.AppStartupOptimizer
import com.example.Linageisp.performance.DeviceTierOptimizer
import com.example.Linageisp.performance.FirebaseOptimizer
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
    
    // EXTREMO RENDIMIENTO: Optimizadores de performance
    private lateinit var startupOptimizer: AppStartupOptimizer
    private lateinit var deviceOptimizer: DeviceTierOptimizer
    private lateinit var firebaseOptimizer: FirebaseOptimizer
    
    override fun onCreate() {
        super.onCreate()
        
        Log.d(TAG, "🚀 Inicializando Linage ISP Application con EXTREMO RENDIMIENTO")
        
        // FASE 1: Inicialización crítica (<500ms)
        initializeCriticalPath()
        
        // FASE 2: Las tareas pesadas se ejecutan en background automáticamente
    }
    
    /**
     * EXTREMO RENDIMIENTO: Inicialización crítica mínima
     * Solo lo esencial para mostrar UI inmediatamente
     */
    private fun initializeCriticalPath() {
        try {
            // Inicializar optimizador de startup
            startupOptimizer = AppStartupOptimizer(this)
            startupOptimizer.initializeCriticalPath()
            
            // Inicializar optimizadores de rendimiento
            deviceOptimizer = DeviceTierOptimizer(this)
            firebaseOptimizer = FirebaseOptimizer.getInstance(this)
            
            Log.d(TAG, "✅ Critical path inicializado - UI lista para mostrar")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error en critical path", e)
            // Fallback a inicialización tradicional
            fallbackInitialization(e)
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
    
    /**
     * Fallback a inicialización tradicional si falla la optimizada
     */
    private fun fallbackInitialization(originalError: Exception) {
        try {
            Log.w(TAG, "⚠️ Usando inicialización fallback")
            
            // Firebase básico
            initializeFirebase()
            
            // FCM básico
            initializeFCM()
            
            // Report del error pero continúa funcionando
            FirebaseCrashlytics.getInstance().recordException(originalError)
            
            Log.d(TAG, "✅ Fallback initialization completada")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error crítico en fallback initialization", e)
        }
    }
    
    /**
     * Accessor para optimizadores (para uso en actividades)
     */
    fun getStartupOptimizer(): AppStartupOptimizer? = if (::startupOptimizer.isInitialized) startupOptimizer else null
    fun getDeviceOptimizer(): DeviceTierOptimizer? = if (::deviceOptimizer.isInitialized) deviceOptimizer else null
    fun getFirebaseOptimizer(): FirebaseOptimizer? = if (::firebaseOptimizer.isInitialized) firebaseOptimizer else null
    
    /**
     * Cleanup resources cuando la app se destruye
     */
    override fun onTerminate() {
        super.onTerminate()
        
        try {
            if (::startupOptimizer.isInitialized) {
                startupOptimizer.cleanup()
            }
            if (::firebaseOptimizer.isInitialized) {
                firebaseOptimizer.cleanup()
            }
            Log.d(TAG, "🧹 Resources cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error cleaning up resources", e)
        }
    }
}