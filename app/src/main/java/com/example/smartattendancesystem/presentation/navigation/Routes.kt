package com.example.smartattendancesystem.presentation.navigation

sealed class Routes(val route: String) {

    object Home : Routes("home")
    object Register : Routes("register")
    object Attendance : Routes("attendance")
    object Admin : Routes("admin")
    object Reports : Routes("reports")
    object Login : Routes("login")
    object SignUp : Routes("signup")
}