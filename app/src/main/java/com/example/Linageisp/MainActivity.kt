package com.example.Linageisp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.Linageisp.navigation.LinageNavHost
import com.example.Linageisp.navigation.LinageDestinations
import com.example.Linageisp.notifications.NotificationViewModel
import com.example.Linageisp.notifications.components.BadgedBottomNavigationBar
import com.example.Linageisp.notifications.components.NotificationPermissionCard
import com.example.Linageisp.notifications.components.InAppMessageCard
import com.example.Linageisp.fcm.LinageNotificationManager
import com.example.Linageisp.ui.theme.LinageTheme
import com.example.Linageisp.viewmodel.PlanViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    // Launcher para solicitar permisos de notificación
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "🔔 Permiso de notificaciones: ${if (isGranted) "Concedido" else "Denegado"}")
        // El ViewModel se actualizará automáticamente
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        initializeFirebase()
        
        // LOG para verificar que Genkit está activo
        Log.d("LINAGE", "========================================")
        Log.d("LINAGE", "🚀 LINAGE ISP - IA ACTIVADA")
        Log.d("LINAGE", "🤖 Modelo: Gemini 1.5 Flash")
        Log.d("LINAGE", "🔑 API Key: ${if (BuildConfig.GEMINI_KEY.isNotEmpty()) "✅" else "❌"}")
        Log.d("LINAGE", "========================================")
        
        // Manejar deep links de notificaciones
        handleNotificationIntent(intent)
        
        enableEdgeToEdge()
        setContent {
            LinageTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PerformanceIntegrationProvider {
                        LinageApp(
                            onRequestNotificationPermission = ::requestNotificationPermission
                        )
                    }
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleNotificationIntent(it) }
    }
    
    /**
     * Manejar intents de notificaciones con deep linking
     */
    private fun handleNotificationIntent(intent: Intent) {
        try {
            val fromNotification = intent.getBooleanExtra("from_notification", false)
            val deepLink = intent.getStringExtra("deep_link")
            val navigateTo = intent.getStringExtra("navigate_to")
            
            if (fromNotification) {
                Log.d(TAG, "📱 Intent desde notificación recibido")
                Log.d(TAG, "🔗 Deep link: $deepLink")
                Log.d(TAG, "📍 Navigate to: $navigateTo")
                
                // Analytics
                val analytics = FirebaseAnalytics.getInstance(this)
                val bundle = Bundle().apply {
                    putString("deep_link", deepLink ?: "unknown")
                    putString("navigate_to", navigateTo ?: "unknown")
                    putLong("timestamp", System.currentTimeMillis())
                }
                analytics.logEvent("notification_deep_link_opened", bundle)
                
                // El navigation controller se actualizará en el Composable
                intent.putExtra("deep_link_handled", true)
            }
            
            // Manejar URI schemes
            val data = intent.data
            if (data != null && data.scheme == "linageisp") {
                Log.d(TAG, "🔗 URI scheme recibido: $data")
                handleUriScheme(data)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error manejando intent de notificación", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
    
    /**
     * Manejar URI schemes personalizados
     */
    private fun handleUriScheme(uri: Uri) {
        when (uri.host) {
            "plans" -> {
                Log.d(TAG, "📋 Navegando a planes desde URI")
                // El navigation controller manejará esto
            }
            "ai_assistant" -> {
                Log.d(TAG, "🤖 Navegando a asistente IA desde URI")
            }
            "technical_support" -> {
                Log.d(TAG, "🔧 Navegando a soporte técnico desde URI")
            }
            "billing" -> {
                Log.d(TAG, "💳 Navegando a facturación desde URI")
            }
            "benefits" -> {
                Log.d(TAG, "⭐ Navegando a beneficios desde URI")
            }
            "speed_test" -> {
                Log.d(TAG, "⚡ Navegando a test de velocidad desde URI")
            }
            else -> {
                Log.d(TAG, "🏠 Navegando a home por defecto desde URI")
            }
        }
    }
    
    /**
     * Solicitar permisos de notificación para Android 13+
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "✅ Permisos de notificación ya concedidos")
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Log.d(TAG, "ℹ️ Mostrando rationale para permisos de notificación")
                    // El UI mostrará el rationale
                }
                else -> {
                    Log.d(TAG, "🔔 Solicitando permisos de notificación")
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            Log.d(TAG, "📱 Android < 13, permisos no requeridos")
        }
    }
    
    private fun initializeFirebase() {
        try {
            // Initialize Firebase App
            FirebaseApp.initializeApp(this)
            
            // Initialize Firebase Analytics
            FirebaseAnalytics.getInstance(this)
            
            // Initialize Firebase Crashlytics
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            
            // Initialize Firebase Performance Monitoring
            FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
            
            Log.d("Firebase", "Firebase initialized successfully")
            Log.d("FirebasePerformance", "Performance Monitoring enabled")
            
        } catch (e: Exception) {
            Log.e("Firebase", "Error initializing Firebase", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
    
}

@Composable
fun LinageApp(
    onRequestNotificationPermission: () -> Unit
) {
    val navController = rememberNavController()
    val planViewModel: PlanViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = hiltViewModel()
    
    // Estados de notificaciones - OPTIMIZED with collectAsStateWithLifecycle
    val uiState by notificationViewModel.uiState.collectAsStateWithLifecycle(initialValue = NotificationViewModel.NotificationUiState())
    val activeBadges by notificationViewModel.activeBadges.collectAsStateWithLifecycle(initialValue = emptyMap())
    val inAppMessages by notificationViewModel.inAppMessages.collectAsStateWithLifecycle(initialValue = emptyList())
    val notificationPreferences by notificationViewModel.notificationPreferences.collectAsStateWithLifecycle(initialValue = LinageNotificationManager.NotificationPreferences())
    
    // Manejar navegación desde deep links
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(navController) {
        if (context is ComponentActivity) {
            val intent = context.intent
            val fromNotification = intent.getBooleanExtra("from_notification", false)
            val navigateTo = intent.getStringExtra("navigate_to")
            val deepLinkHandled = intent.getBooleanExtra("deep_link_handled", false)
            
            if (fromNotification && !deepLinkHandled && navigateTo != null) {
                // Navegar a la pantalla específica
                when (navigateTo) {
                    "plans" -> navController.navigate(LinageDestinations.Plans.route)
                    "ai_assistant" -> navController.navigate(LinageDestinations.AIAssistant.route)
                    "technical_support" -> navController.navigate(LinageDestinations.TechnicalSupport.route)
                    "billing" -> navController.navigate(LinageDestinations.Billing.route)
                    "benefits" -> navController.navigate(LinageDestinations.Benefits.route)
                    "speed_test" -> navController.navigate(LinageDestinations.SpeedTest.route)
                    "account" -> navController.navigate(LinageDestinations.Account.route)
                    "settings" -> navController.navigate(LinageDestinations.Settings.route)
                    else -> navController.navigate(LinageDestinations.Home.route)
                }
                
                // Marcar como manejado
                intent.putExtra("deep_link_handled", true)
                
                // Analytics
                notificationViewModel.onNotificationReceived(
                    navigateTo,
                    mapOf("deep_link_clicked" to "true")
                )
                
                Log.d("LinageApp", "🔗 Navegación por deep link completada: $navigateTo")
            }
        }
    }
    
    // Manejar solicitud de permisos
    LaunchedEffect(uiState.showPermissionRationale) {
        if (uiState.showPermissionRationale) {
            onRequestNotificationPermission()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Contenido principal de navegación
        LinageNavHost(
            navController = navController,
            planViewModel = planViewModel,
            modifier = Modifier.fillMaxSize()
        )
        
        // Bottom Navigation Bar con badges
        BadgedBottomNavigationBar(
            currentRoute = navController.currentDestination?.route ?: LinageDestinations.Home.route,
            badges = activeBadges,
            onItemClick = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
                // Limpiar badge al hacer click
                notificationViewModel.removeBadge(route)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        // Overlay para permisos de notificación
        if (!uiState.hasNotificationPermission && !uiState.permissionRequested) {
            NotificationPermissionCard(
                onRequestPermission = {
                    notificationViewModel.requestNotificationPermission()
                    onRequestNotificationPermission()
                },
                onDismiss = {
                    notificationViewModel.onPermissionDenied()
                },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        
        // In-app messages overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = if (!uiState.hasNotificationPermission && !uiState.permissionRequested) 200.dp else 50.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            inAppMessages.forEach { message ->
                InAppMessageCard(
                    message = message,
                    onMessageClick = { route ->
                        // Navegar según la ruta del mensaje
                        when {
                            route.contains("plans") -> navController.navigate(LinageDestinations.Plans.route)
                            route.contains("ai_assistant") -> navController.navigate(LinageDestinations.AIAssistant.route)
                            route.contains("benefits") -> navController.navigate(LinageDestinations.Benefits.route)
                            route.contains("support") -> navController.navigate(LinageDestinations.TechnicalSupport.route)
                            route.contains("billing") -> navController.navigate(LinageDestinations.Billing.route)
                            else -> navController.navigate(LinageDestinations.Home.route)
                        }
                    },
                    onDismiss = { messageId ->
                        notificationViewModel.dismissInAppMessage(messageId)
                    }
                )
            }
        }
        
        // Indicador de estado FCM (solo en debug)
        if (BuildConfig.DEBUG && uiState.isLoading) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Inicializando FCM...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        // Error FCM (si existe)
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Error FCM: $error",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}