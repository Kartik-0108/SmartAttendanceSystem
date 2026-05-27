package com.example.smartattendancesystem.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.*
import com.example.smartattendancesystem.presentation.screens.*

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val authViewModel: com.example.smartattendancesystem.presentation.viewmodel.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val isLoggedIn by authViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.Home.route else Routes.Login.route
    ) {

        composable(Routes.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        composable(Routes.SignUp.route) {
            SignUpScreen(navController, authViewModel)
        }

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