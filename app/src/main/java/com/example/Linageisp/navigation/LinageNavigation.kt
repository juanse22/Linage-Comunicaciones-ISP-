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
    object SpeedTest : LinageDestinations("speed_test")
    object TechnicalSupport : LinageDestinations("technical_support")
    object Billing : LinageDestinations("billing")
    object Settings : LinageDestinations("settings")
    object AIAssistant : LinageDestinations("ai_assistant")
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
                },
                onNavigateToSupport = {
                    navController.navigate(LinageDestinations.TechnicalSupport.route)
                },
                onNavigateToAI = {
                    navController.navigate(LinageDestinations.AIAssistant.route)
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
            AccountScreen(
                onNavigateToSpeedTest = {
                    navController.navigate(LinageDestinations.SpeedTest.route)
                },
                onNavigateToSupport = {
                    navController.navigate(LinageDestinations.TechnicalSupport.route)
                },
                onNavigateToBilling = {
                    navController.navigate(LinageDestinations.Billing.route)
                },
                onNavigateToSettings = {
                    navController.navigate(LinageDestinations.Settings.route)
                }
            )
        }
        
        // Pantalla de Test de Velocidad
        composable(
            route = LinageDestinations.SpeedTest.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            SpeedTestScreen()
        }
        
        // Pantalla de Soporte Técnico
        composable(
            route = LinageDestinations.TechnicalSupport.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            TechnicalSupportScreen()
        }
        
        // Pantalla de Facturación
        composable(
            route = LinageDestinations.Billing.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            BillingScreen()
        }
        
        // Pantalla de Configuración
        composable(
            route = LinageDestinations.Settings.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300, easing = EaseInOutCubic)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            SettingsScreen()
        }
        
        // Pantalla del Asistente de IA - LINA
        composable(
            route = LinageDestinations.AIAssistant.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(400, easing = EaseInOutCubic)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(400, easing = EaseInOutCubic)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            // Importar y usar nuestro nuevo ChatScreen
            com.example.Linageisp.presentation.chat.ChatScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}