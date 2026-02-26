package com.uriel.musicjam.core.navegation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.uriel.musicjam.features.auth.presentation.screens.LoginScreen
import com.uriel.musicjam.features.auth.presentation.screens.SignUpScreen
import com.uriel.musicjam.features.home.presentation.screens.HomeScreen
import com.uriel.musicjam.features.search.presentation.screens.SearchScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreens.LogIn.route,
        enterTransition = {
            fadeIn(animationSpec = tween(400)) +
                    scaleIn(initialScale = 0.92f, animationSpec = tween(400))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(400)) +
                    scaleOut(targetScale = 1.08f, animationSpec = tween(400))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(400)) +
                    scaleIn(initialScale = 1.08f, animationSpec = tween(400))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(400)) +
                    scaleOut(targetScale = 0.92f, animationSpec = tween(400))
        }
    ){
        composable(AppScreens.LogIn.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.LogIn.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppScreens.SignUp.route)
                }
            )
        }

        composable(AppScreens.SignUp.route) {
            SignUpScreen(
                onNavigateUp = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.LogIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppScreens.Home.route) {
            HomeScreen(
                navController = navController,
                onTrackClick = { track ->
                    // navController.navigate(AppScreens.Player.createRoute(track.id))
                },
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(AppScreens.Search.route) {
            SearchScreen(navController = navController)
        }

        composable(AppScreens.Library.route) {
            // LibraryScreen()
        }
    }
}