package com.example.smartattendancesystem.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartattendancesystem.presentation.components.CameraPreview
import com.example.smartattendancesystem.presentation.viewmodel.AttendanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterStudentScreen(
    navController: NavController,
    viewModel: AttendanceViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var rollNumber by remember { mutableStateOf("") }
    var currentEmbedding by remember { mutableStateOf<FloatArray?>(null) }
    var isLive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register New Student", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Camera Preview Section
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .aspectRatio(1f) // Square camera preview
                    .clip(RoundedCornerShape(32.dp)),
                tonalElevation = 4.dp
            ) {
                Box {
                    CameraPreview(
                        onEmbeddingGenerated = { currentEmbedding = it },
                        onLivenessDetected = { isLive = it }
                    )
                    
                    // Liveness Status Indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(
                                if (isLive) Color(0xFF4CAF50).copy(alpha = 0.8f) 
                                else Color.Red.copy(alpha = 0.8f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isLive) "LIVE" else "BLINK",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Form Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        placeholder = { Text("e.g. John Doe") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Default.Face, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = rollNumber,
                        onValueChange = { rollNumber = it },
                        label = { Text("Roll Number") },
                        placeholder = { Text("e.g. CS101") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Default.Fingerprint, contentDescription = null) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    currentEmbedding?.let { 
                        viewModel.registerStudent(name, rollNumber, it)
                        navController.popBackStack()
                    }
                },
                enabled = name.isNotBlank() && rollNumber.isNotBlank() && currentEmbedding != null && isLive,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Complete Registration", fontWeight = FontWeight.Bold)
            }
            
            if (currentEmbedding != null && !isLive) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Liveness verification required",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}