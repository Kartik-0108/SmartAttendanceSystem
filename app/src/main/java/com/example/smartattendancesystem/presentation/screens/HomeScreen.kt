package com.example.smartattendancesystem.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartattendancesystem.presentation.navigation.Routes

@Composable
fun HomeScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Smart Attendance System",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                navController.navigate(Routes.Register.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register Student")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate(Routes.Attendance.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mark Attendance")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate(Routes.Admin.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Admin Dashboard")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate(Routes.Reports.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reports")
        }
    }
}