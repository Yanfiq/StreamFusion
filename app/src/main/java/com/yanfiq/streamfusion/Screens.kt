package com.yanfiq.streamfusion

sealed class Screens(val route : String) {
    object Home : Screens("home_route")
    object Search : Screens("search_route")
    object Settings : Screens("settings_route")
}