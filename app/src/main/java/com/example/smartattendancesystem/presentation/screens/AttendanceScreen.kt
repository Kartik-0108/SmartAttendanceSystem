package com.example.smartattendancesystem.presentation.screens

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scanner", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
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
                    .padding(bottom = 120.dp)
                    .clip(RoundedCornerShape(32.dp))
            ) {
                CameraPreview(
                    onEmbeddingGenerated = { embedding, bitmap ->
                        currentEmbedding = embedding
                        currentBitmap = bitmap
                        if (isLive) {
                            viewModel.processFace(embedding, bitmap, true)
                        }
                    },
                    onLivenessDetected = { live ->
                        isLive = live
                        val embedding = currentEmbedding
                        val bitmap = currentBitmap
                        if (live && embedding != null && bitmap != null) { 
                            viewModel.processFace(embedding, bitmap, live)
                        }
                    }
                )
                
                // Scanning Line Animation
                if (status == "Scan Face to Start" || status == "Ready for next student" || status.contains("Not Recognized")) {
                    ScanningOverlay()
                }

                // Liveness Badge (Inside Camera View)
                Surface(
                    modifier = Modifier
                        .padding(24.dp)
                        .align(Alignment.TopEnd),
                    color = if (isLive) Color(0xFF4CAF50).copy(alpha = 0.8f) else Color.Red.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.White, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLive) "LIVE" else "BLINK",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Status Panel (Bottom)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(32.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Recognition Result Card
                    AnimatedContent(
                        targetState = lastStudent,
                        transitionSpec = {
                            fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically()
                        },
                        label = "StudentStatus"
                    ) { student ->
                        if (student != null && status.contains("Marked")) {
                            SuccessStudentView(student)
                        } else {
                            ScanningStatusView(status)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessStudentView(student: com.example.smartattendancesystem.data.local.entity.StudentEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4CAF50))
        ) {
            AsyncImage(
                model = student.imagePath,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                "Welcome, ${student.name}!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            Text(
                "Attendance recorded successfully",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ScanningStatusView(status: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val color = when {
            status.contains("Failed") -> MaterialTheme.colorScheme.error
            status.contains("Recognized") -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.primary
        }
        
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 3.dp,
            color = color
        )
        
        Text(
            text = status,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
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
                .height(4.dp)
                .offset(y = height * offsetY)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}