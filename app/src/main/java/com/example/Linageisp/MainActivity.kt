package com.example.Linageisp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.Linageisp.navigation.LinageNavHost
import com.example.Linageisp.ui.components.LinageBottomNavigationBar
import com.example.Linageisp.ui.theme.LinageTheme
import com.example.Linageisp.viewmodel.PlanViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
        
        enableEdgeToEdge()
        setContent {
            LinageTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PerformanceIntegrationProvider {
                        LinageApp()
                    }
                }
            }
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
fun LinageApp() {
    val navController = rememberNavController()
    val planViewModel: PlanViewModel = viewModel()
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Contenido principal de navegación
        LinageNavHost(
            navController = navController,
            planViewModel = planViewModel,
            modifier = Modifier.fillMaxSize()
        )
        
        // Bottom Navigation Bar fijo
        LinageBottomNavigationBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}