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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinageTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LinageApp()
                }
            }
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