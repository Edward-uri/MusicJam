package com.uriel.musicjam.core.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.uriel.musicjam.features.auth.presentation.screens.LoginScreen
import com.uriel.musicjam.features.auth.presentation.screens.SignUpScreen
import com.uriel.musicjam.features.home.presentation.screens.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreens.LogIn.route
    ) {
        // Pantalla 1: Login
        composable(AppScreens.LogIn.route) {
            LoginScreen(
                onNavigateToHome = {
                    // Navega al Home y destruye el Login del historial (BackStack)
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.LogIn.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    // Simplemente pone la pantalla de Registro encima
                    navController.navigate(AppScreens.SignUp.route)
                }
            )
        }

        // Pantalla 2: Registro
        composable(AppScreens.SignUp.route) {
            SignUpScreen(
                onNavigateUp = {
                    // El botón físico o la acción de volver atrás (saca la pantalla de la pila)
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    // Si decides que al registrarse entre directo, limpiamos el historial hasta el login
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.LogIn.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppScreens.Home.route) {
            HomeScreen(
                onTrackClick = { track ->
                    // navController.navigate(AppScreens.Player.createRoute(track.id))
                },
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }
    }
}