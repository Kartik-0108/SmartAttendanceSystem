package com.example.smartattendancesystem.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartattendancesystem.presentation.components.CameraPreview

@Composable
fun AttendanceScreen(navController: NavController) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        CameraPreview()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Camera Ready for Attendance"
        )
    }
}