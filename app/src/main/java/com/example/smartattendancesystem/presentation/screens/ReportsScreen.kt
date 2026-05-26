package com.example.smartattendancesystem.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartattendancesystem.data.local.entity.AttendanceEntity
import com.example.smartattendancesystem.presentation.viewmodel.ReportsViewModel
import com.example.smartattendancesystem.utils.CSVExporter
import com.example.smartattendancesystem.utils.PDFExporter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    navController: NavController,
    viewModel: ReportsViewModel = viewModel()
) {
    val context = LocalContext.current
    val attendanceList by viewModel.allAttendance.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    var selectedFilter by remember { mutableStateOf("All") }

    // Refresh data when screen is opened
    LaunchedEffect(Unit) {
        viewModel.loadAttendance()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.syncData()
                        Toast.makeText(context, "Cloud sync started", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.CloudSync, contentDescription = "Sync", tint = MaterialTheme.colorScheme.primary)
                    }
                    
                    IconButton(onClick = {
                        if (attendanceList.isNotEmpty()) {
                            val path = PDFExporter.exportAttendanceToPDF(context, attendanceList)
                            if (path != null) {
                                Toast.makeText(context, "PDF saved: $path", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "No data to export", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = Color.Red)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "All",
                    onClick = { 
                        selectedFilter = "All"
                        viewModel.loadAttendance()
                    },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedFilter == "Today",
                    onClick = { 
                        selectedFilter = "Today"
                        viewModel.getDailyAttendance()
                    },
                    label = { Text("Today") }
                )
                FilterChip(
                    selected = selectedFilter == "Week",
                    onClick = { 
                        selectedFilter = "Week"
                        viewModel.getWeeklyAttendance()
                    },
                    label = { Text("Week") }
                )
            }

            if (attendanceList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.History, 
                            contentDescription = null, 
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No records found", color = MaterialTheme.colorScheme.outline)
                        Button(
                            onClick = { viewModel.loadAttendance() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Refresh")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(attendanceList) { attendance ->
                        AttendanceCard(attendance, dateFormat)
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceCard(attendance: AttendanceEntity, dateFormat: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (attendance.imagePath != null) {
                    AsyncImage(
                        model = attendance.imagePath,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = attendance.studentName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attendance.studentName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: ${attendance.studentId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            val formattedDate = try {
                dateFormat.format(Date(attendance.timestamp))
            } catch (e: Exception) {
                "Unknown Date"
            }
            val dateParts = formattedDate.split(", ")
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = dateParts.getOrElse(0) { "" },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                if (dateParts.size > 1) {
                    Text(
                        text = dateParts[1],
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}