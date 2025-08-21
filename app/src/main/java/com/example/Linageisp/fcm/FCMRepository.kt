package com.example.Linageisp.fcm

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 📡 Repository para manejo de Firebase Cloud Messaging
 * 
 * Características:
 * - Gestión segura de tokens FCM
 * - Registro en backend con Custom Claims
 * - Encriptación de tokens
 * - Segmentación automática de usuarios
 * - Cache local de tokens
 * - Rate limiting para requests
 */
@Singleton
class FCMRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFunctions: FirebaseFunctions,
    private val firebaseMessaging: FirebaseMessaging
) {
    
    companion object {
        private const val TAG = "FCMRepository"
        private const val PREFS_NAME = "linage_fcm_prefs"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_TOKEN_TIMESTAMP = "token_timestamp"
        private const val KEY_USER_SEGMENTS = "user_segments"
        private const val KEY_LAST_SYNC = "last_sync"
        
        // Rate limiting
        private const val MIN_SYNC_INTERVAL = 60_000L // 1 minute
        
        // Encryption
        private const val ENCRYPTION_ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _tokenState = MutableStateFlow<FCMTokenState>(FCMTokenState.Loading)
    val tokenState: Flow<FCMTokenState> = _tokenState.asStateFlow()
    
    private val _userSegments = MutableStateFlow<Set<String>>(emptySet())
    val userSegments: Flow<Set<String>> = _userSegments.asStateFlow()

    /**
     * Estados del token FCM
     */
    sealed class FCMTokenState {
        object Loading : FCMTokenState()
        data class Success(val token: String) : FCMTokenState()
        data class Error(val exception: Exception) : FCMTokenState()
    }

    /**
     * Datos del usuario para segmentación
     */
    data class UserSegmentData(
        val isLinageCustomer: Boolean = false,
        val planType: String? = null,
        val customerSince: Long? = null,
        val lastActivity: Long = System.currentTimeMillis(),
        val preferredLanguage: String = "es",
        val deviceType: String = "android",
        val appVersion: String = "",
        val hasActiveService: Boolean = false
    )

    init {
        loadCachedData()
    }

    /**
     * Obtener token FCM actual
     */
    suspend fun getCurrentToken(): String? {
        return try {
            val token = firebaseMessaging.token.await()
            Log.d(TAG, "🔑 Token FCM obtenido: ${token.take(20)}...")
            _tokenState.value = FCMTokenState.Success(token)
            token
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al obtener token FCM", e)
            _tokenState.value = FCMTokenState.Error(e)
            null
        }
    }

    /**
     * Guardar token FCM en cache local
     */
    suspend fun saveToken(token: String) {
        try {
            val encryptedToken = encryptToken(token)
            prefs.edit()
                .putString(KEY_FCM_TOKEN, encryptedToken)
                .putLong(KEY_TOKEN_TIMESTAMP, System.currentTimeMillis())
                .apply()
                
            Log.d(TAG, "💾 Token FCM guardado en cache")
            _tokenState.value = FCMTokenState.Success(token)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al guardar token FCM", e)
        }
    }

    /**
     * Obtener token FCM desde cache
     */
    fun getCachedToken(): String? {
        return try {
            val encryptedToken = prefs.getString(KEY_FCM_TOKEN, null)
            encryptedToken?.let { decryptToken(it) }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al leer token desde cache", e)
            null
        }
    }

    /**
     * Registrar token en backend con segmentación
     */
    suspend fun registerTokenWithBackend(token: String) {
        val currentUser = firebaseAuth.currentUser
        val lastSync = prefs.getLong(KEY_LAST_SYNC, 0)
        val now = System.currentTimeMillis()
        
        // Rate limiting
        if (now - lastSync < MIN_SYNC_INTERVAL) {
            Log.d(TAG, "⏳ Rate limiting activo, saltando sync")
            return
        }
        
        try {
            val userSegmentData = getUserSegmentData()
            
            val data = hashMapOf(
                "fcmToken" to token,
                "userId" to (currentUser?.uid ?: "anonymous"),
                "userEmail" to (currentUser?.email ?: ""),
                "isLinageCustomer" to userSegmentData.isLinageCustomer,
                "planType" to userSegmentData.planType,
                "customerSince" to userSegmentData.customerSince,
                "lastActivity" to userSegmentData.lastActivity,
                "preferredLanguage" to userSegmentData.preferredLanguage,
                "deviceType" to userSegmentData.deviceType,
                "appVersion" to userSegmentData.appVersion,
                "hasActiveService" to userSegmentData.hasActiveService,
                "platform" to "android",
                "appPackage" to context.packageName,
                "timestamp" to now
            )
            
            // Llamar Cloud Function para registrar token
            val result = firebaseFunctions
                .getHttpsCallable("registerFCMToken")
                .call(data)
                .await()
                
            val response = result.data as? Map<*, *>
            val success = response?.get("success") as? Boolean ?: false
            val segments = response?.get("segments") as? List<*>
            
            if (success) {
                // Actualizar segmentos locales
                segments?.let { updateUserSegments(it.map { segment -> segment.toString() }.toSet()) }
                
                prefs.edit()
                    .putLong(KEY_LAST_SYNC, now)
                    .apply()
                    
                Log.d(TAG, "✅ Token registrado en backend exitosamente")
                Log.d(TAG, "🎯 Segmentos aplicados: ${_userSegments.value}")
            } else {
                Log.w(TAG, "⚠️ Backend reportó error al registrar token")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al registrar token en backend", e)
            // No lanzar excepción para no afectar el flujo principal
        }
    }

    /**
     * Suscribirse a tópicos según segmentación
     */
    suspend fun subscribeToTopics() {
        try {
            val segments = _userSegments.value
            val currentUser = firebaseAuth.currentUser
            
            // Tópicos base
            firebaseMessaging.subscribeToTopic("linage_all_users").await()
            firebaseMessaging.subscribeToTopic("android_users").await()
            
            // Tópicos por autenticación
            if (currentUser != null) {
                firebaseMessaging.subscribeToTopic("authenticated_users").await()
                
                // Tópicos por segmentos
                segments.forEach { segment ->
                    firebaseMessaging.subscribeToTopic("segment_$segment").await()
                    Log.d(TAG, "📡 Suscrito a tópico: segment_$segment")
                }
            } else {
                firebaseMessaging.subscribeToTopic("anonymous_users").await()
            }
            
            Log.d(TAG, "✅ Suscripción a tópicos completada")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al suscribirse a tópicos", e)
        }
    }

    /**
     * Desuscribirse de todos los tópicos (logout)
     */
    suspend fun unsubscribeFromAllTopics() {
        try {
            val segments = _userSegments.value
            
            // Desuscribirse de tópicos específicos
            segments.forEach { segment ->
                firebaseMessaging.unsubscribeFromTopic("segment_$segment").await()
            }
            
            firebaseMessaging.unsubscribeFromTopic("authenticated_users").await()
            
            Log.d(TAG, "✅ Desuscripción de tópicos completada")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al desuscribirse de tópicos", e)
        }
    }

    /**
     * Actualizar datos de segmentación del usuario
     */
    fun updateUserSegmentData(segmentData: UserSegmentData) {
        try {
            val segments = mutableSetOf<String>()
            
            // Segmentación por tipo de cliente
            if (segmentData.isLinageCustomer) {
                segments.add("linage_customer")
                
                // Segmentación por tipo de plan
                segmentData.planType?.let { planType ->
                    segments.add("plan_$planType")
                }
                
                // Segmentación por antigüedad
                segmentData.customerSince?.let { since ->
                    val monthsOld = (System.currentTimeMillis() - since) / (30L * 24 * 60 * 60 * 1000)
                    when {
                        monthsOld < 1 -> segments.add("new_customer")
                        monthsOld < 6 -> segments.add("recent_customer")
                        monthsOld < 12 -> segments.add("regular_customer")
                        else -> segments.add("loyal_customer")
                    }
                }
                
                if (segmentData.hasActiveService) {
                    segments.add("active_service")
                } else {
                    segments.add("inactive_service")
                }
            } else {
                segments.add("potential_customer")
            }
            
            // Segmentación por actividad
            val daysSinceActivity = (System.currentTimeMillis() - segmentData.lastActivity) / (24 * 60 * 60 * 1000)
            when {
                daysSinceActivity < 1 -> segments.add("highly_active")
                daysSinceActivity < 7 -> segments.add("active")
                daysSinceActivity < 30 -> segments.add("moderately_active")
                else -> segments.add("inactive")
            }
            
            updateUserSegments(segments)
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al actualizar segmentación", e)
        }
    }

    /**
     * Obtener datos de segmentación del usuario
     */
    private fun getUserSegmentData(): UserSegmentData {
        val currentUser = firebaseAuth.currentUser
        
        // Aquí deberías obtener datos reales del usuario desde tu backend/database
        // Por ahora, retornamos datos de ejemplo
        return UserSegmentData(
            isLinageCustomer = currentUser != null && currentUser.email?.contains("@linage") == true,
            planType = "basic", // Obtener del perfil del usuario
            customerSince = currentUser?.metadata?.creationTimestamp,
            lastActivity = System.currentTimeMillis(),
            preferredLanguage = "es",
            deviceType = "android",
            appVersion = getAppVersion(),
            hasActiveService = true // Verificar estado del servicio
        )
    }

    /**
     * Actualizar segmentos del usuario
     */
    private fun updateUserSegments(segments: Set<String>) {
        _userSegments.value = segments
        
        // Guardar en cache
        prefs.edit()
            .putStringSet(KEY_USER_SEGMENTS, segments)
            .apply()
            
        Log.d(TAG, "🎯 Segmentos actualizados: $segments")
    }

    /**
     * Cargar datos desde cache
     */
    private fun loadCachedData() {
        // Cargar segmentos
        val cachedSegments = prefs.getStringSet(KEY_USER_SEGMENTS, emptySet()) ?: emptySet()
        _userSegments.value = cachedSegments
        
        // Cargar token si existe
        getCachedToken()?.let { token ->
            _tokenState.value = FCMTokenState.Success(token)
        }
    }

    /**
     * Encriptar token para almacenamiento seguro
     */
    private fun encryptToken(token: String): String {
        return try {
            val key = getOrCreateEncryptionKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val encryptedBytes = cipher.doFinal(token.toByteArray())
            android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Error al encriptar token, guardando en texto plano", e)
            token // Fallback a texto plano
        }
    }

    /**
     * Desencriptar token
     */
    private fun decryptToken(encryptedToken: String): String {
        return try {
            val key = getOrCreateEncryptionKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key)
            val encryptedBytes = android.util.Base64.decode(encryptedToken, android.util.Base64.DEFAULT)
            String(cipher.doFinal(encryptedBytes))
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Error al desencriptar token, asumiendo texto plano", e)
            encryptedToken // Fallback a texto plano
        }
    }

    /**
     * Obtener o crear clave de encriptación
     */
    private fun getOrCreateEncryptionKey(): SecretKey {
        val keyString = prefs.getString("encryption_key", null)
        
        return if (keyString != null) {
            val keyBytes = android.util.Base64.decode(keyString, android.util.Base64.DEFAULT)
            SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM)
        } else {
            // Crear nueva clave
            val keyGen = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM)
            keyGen.init(256)
            val key = keyGen.generateKey()
            
            // Guardar clave
            val keyString = android.util.Base64.encodeToString(key.encoded, android.util.Base64.DEFAULT)
            prefs.edit().putString("encryption_key", keyString).apply()
            
            key
        }
    }

    /**
     * Obtener versión de la app
     */
    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    /**
     * Limpiar datos de FCM (logout)
     */
    suspend fun clearFCMData() {
        try {
            unsubscribeFromAllTopics()
            
            prefs.edit()
                .remove(KEY_FCM_TOKEN)
                .remove(KEY_TOKEN_TIMESTAMP)
                .remove(KEY_USER_SEGMENTS)
                .remove(KEY_LAST_SYNC)
                .apply()
                
            _userSegments.value = emptySet()
            _tokenState.value = FCMTokenState.Loading
            
            Log.d(TAG, "🧹 Datos FCM limpiados")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error al limpiar datos FCM", e)
        }
    }
}