package com.example.smartattendancesystem.presentation.components

import android.annotation.SuppressLint
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.smartattendancesystem.ml.facedetection.FaceAnalyzer
import com.example.smartattendancesystem.ml.facedetection.FaceDetectionResult
import com.example.smartattendancesystem.ml.facedetection.FaceDetectorHelper
import com.example.smartattendancesystem.ml.facemesh.FaceMeshAnalyzer
import com.example.smartattendancesystem.ml.facemesh.FaceMeshHelper
import com.example.smartattendancesystem.ml.facerecognition.FaceRecognitionHelper

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview(
    onEmbeddingGenerated: (FloatArray) -> Unit = {},
    onLivenessDetected: (Boolean) -> Unit = {}
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var faces by remember {
        mutableStateOf<List<FaceDetectionResult>>(emptyList())
    }

    var isLivenessVerified by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build()

                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(ctx),
                        FaceAnalyzer(
                            faceDetectorHelper = FaceDetectorHelper(ctx),
                            faceRecognitionHelper = FaceRecognitionHelper(ctx),
                            faceMeshAnalyzer = FaceMeshAnalyzer(
                                FaceMeshHelper(ctx)
                            ) { isLive ->
                                isLivenessVerified = isLive
                                onLivenessDetected(isLive)
                            },
                            onFacesDetected = { detectedFaces ->
                                faces = detectedFaces
                            },
                            onEmbeddingGenerated = { embedding ->
                                onEmbeddingGenerated(embedding)
                            }
                        )
                    )

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        FaceOverlay(faces)

        // STATUS OVERLAY
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLivenessVerified) {
                    Text(
                        text = "LIVENESS VERIFIED ✅",
                        color = Color.Green,
                        style = MaterialTheme.typography.titleLarge
                    )
                } else {
                    Text(
                        text = "BLINK TO VERIFY LIVENESS",
                        color = Color.Yellow,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}