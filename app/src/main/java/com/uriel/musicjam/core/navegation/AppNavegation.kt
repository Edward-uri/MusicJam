package com.uriel.musicjam.core.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.uriel.musicjam.features.auth.presentation.screens.LoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreens.LogIn.route
    ) {
        composable(AppScreens.LogIn.route) {
            LoginScreen(
                onNavigateToHome = {},
                onNavigateToRegister = {}
            )
        }
        composable(AppScreens.SignUp.route) { //
        // SignUpScreen(navController = navController)
        }
        composable(AppScreens.Home.route) {
            //HomeScreen(navController = navController)
        }

    }
}