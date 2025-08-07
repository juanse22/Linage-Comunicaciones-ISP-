package com.example.Linageisp.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.Linageisp.ui.screens.*
import com.example.Linageisp.viewmodel.PlanViewModel

/**
 * Rutas de navegación de la aplicación
 */
sealed class LinageDestinations(val route: String) {
    object Home : LinageDestinations("home")
    object Plans : LinageDestinations("plans")
    object Benefits : LinageDestinations("benefits")
    object Account : LinageDestinations("account")
}

/**
 * NavHost principal de la aplicación con animaciones suaves
 */
@Composable
fun LinageNavHost(
    navController: NavHostController,
    planViewModel: PlanViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = LinageDestinations.Home.route,
        modifier = modifier.padding(bottom = 80.dp) // Espacio para bottom nav
    ) {
        // Pantalla de Inicio
        composable(LinageDestinations.Home.route) {
            NewHomeScreen(
                onNavigateToPlans = {
                    navController.navigate(LinageDestinations.Plans.route)
                }
            )
        }
        
        // Pantalla de Planes
        composable(LinageDestinations.Plans.route) {
            NewPlansScreen(
                planViewModel = planViewModel
            )
        }
        
        // Pantalla de Beneficios
        composable(LinageDestinations.Benefits.route) {
            BenefitsScreen()
        }
        
        // Pantalla de Cuenta
        composable(LinageDestinations.Account.route) {
            AccountScreen()
        }
    }
}