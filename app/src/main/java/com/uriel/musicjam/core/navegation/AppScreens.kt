package com.uriel.musicjam.core.navegation

sealed class AppScreens(val route: String) {
    object LogIn : AppScreens("login_screen")
    object SignUp : AppScreens("signup_screen")

    object Home : AppScreens("home_screen")

    object Search : AppScreens("search_screen")
    object Library : AppScreens("library_screen")

}
