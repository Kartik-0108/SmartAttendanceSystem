package com.example.smartattendancesystem.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartattendancesystem.presentation.components.CameraPreview
import com.example.smartattendancesystem.presentation.viewmodel.AttendanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    navController: NavController,
    viewModel: AttendanceViewModel = viewModel()
) {
    val status by viewModel.attendanceStatus.collectAsState()
    val lastStudent by viewModel.lastDetectedStudent.collectAsState()

    var currentEmbedding by remember { mutableStateOf<FloatArray?>(null) }
    var isLive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Face Attendance", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Black // Dark background for camera feel
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Camera Layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            ) {
                CameraPreview(
                    onEmbeddingGenerated = { embedding ->
                        currentEmbedding = embedding
                        if (isLive) {
                            viewModel.processFace(embedding, true)
                        }
                    },
                    onLivenessDetected = { live ->
                        isLive = live
                        currentEmbedding?.let { 
                            viewModel.processFace(it, live)
                        }
                    }
                )
                
                // Scanning Line Animation
                ScanningOverlay()
            }

            // Status Card (Bottom)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val statusIcon = when {
                            status.contains("Marked") -> Icons.Default.CheckCircle
                            status.contains("Failed") -> Icons.Default.Error
                            else -> Icons.Default.Info
                        }
                        val statusColor = when {
                            status.contains("Marked") -> Color(0xFF4CAF50)
                            status.contains("Failed") -> Color(0xFFF44336)
                            else -> MaterialTheme.colorScheme.primary
                        }

                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(28.dp)
                        )
                        
                        Text(
                            text = status,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    lastStudent?.let { student ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = student.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "|",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = student.rollNumber,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScanningOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "scanning")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "linePosition"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val height = maxHeight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .offset(y = height * offsetY)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Green.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Corner Brackets
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
        ) {
            // We can add custom drawing here for corner brackets if needed
        }
    }
}