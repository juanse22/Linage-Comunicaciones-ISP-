package com.example.Linageisp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Linageisp.ui.components.ModernBottomNavigationBar
import com.example.Linageisp.viewmodel.PlanModernViewModel

/**
 * Pantalla principal que contiene la navegación y el contenido
 */
@Composable
fun MainModernScreen() {
    var currentRoute by remember { mutableStateOf("plans") }
    val planViewModel: PlanModernViewModel = viewModel()

    Box(modifier = Modifier.fillMaxSize()) {
        // Contenido principal
        when (currentRoute) {
            "home" -> {
                // Placeholder para pantalla de inicio
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏠 Pantalla de Inicio")
                }
            }
            "plans" -> {
                ModernPlansScreen(viewModel = planViewModel)
            }
            "benefits" -> {
                // Placeholder para pantalla de beneficios
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎁 Pantalla de Beneficios")
                }
            }
            "account" -> {
                // Placeholder para pantalla de cuenta
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤 Pantalla de Cuenta")
                }
            }
        }

        // Bottom Navigation Bar
        ModernBottomNavigationBar(
            currentRoute = currentRoute,
            onItemClick = { route -> currentRoute = route },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}