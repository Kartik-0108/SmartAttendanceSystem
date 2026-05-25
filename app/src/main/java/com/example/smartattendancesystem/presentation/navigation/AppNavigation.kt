package com.example.smartattendancesystem.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.smartattendancesystem.presentation.screens.*

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {

        composable(Routes.Home.route) {
            HomeScreen(navController)
        }

        composable(Routes.Register.route) {
            RegisterStudentScreen(navController)
        }

        composable(Routes.Attendance.route) {
            AttendanceScreen(navController)
        }

        composable(Routes.Admin.route) {
            AdminDashboardScreen(navController)
        }

        composable(Routes.Reports.route) {
            ReportsScreen(navController)
        }
    }
}