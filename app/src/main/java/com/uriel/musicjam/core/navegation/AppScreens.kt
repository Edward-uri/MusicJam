package com.uriel.musicjam.core.navegation

sealed class AppScreens(val route: String) {
    object LogIn   : AppScreens("login_screen")
    object SignUp  : AppScreens("signup_screen")
    object Home    : AppScreens("home_screen")
    object Search  : AppScreens("search_screen")
    object Player  : AppScreens("player_screen")

    companion object {
        fun playerRoute(trackId: String? = null): String =
            if (trackId != null) "player_screen?trackId=$trackId"
            else "player_screen"
    }
}
