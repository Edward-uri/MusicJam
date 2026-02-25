package com.uriel.musicjam.core.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uriel.musicjam.features.home.presentation.screens.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Home.route
    ) {
        composable(AppScreens.LogIn.route) {
            // LogInScreen(navController = navController)
        }

        composable(AppScreens.SignUp.route) {
            // SignUpScreen(navController = navController)
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
