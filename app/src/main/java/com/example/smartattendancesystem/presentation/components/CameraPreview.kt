package com.example.smartattendancesystem.presentation.components

import com.example.smartattendancesystem.ml.facemesh.FaceMeshAnalyzer
import com.example.smartattendancesystem.ml.facemesh.FaceMeshHelper
import com.example.smartattendancesystem.ml.facerecognition.FaceRecognitionHelper
import android.annotation.SuppressLint
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.smartattendancesystem.ml.facedetection.FaceAnalyzer
import com.example.smartattendancesystem.ml.facedetection.FaceDetectionResult
import com.example.smartattendancesystem.ml.facedetection.FaceDetectorHelper

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview() {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var faces by remember {
        mutableStateOf<List<FaceDetectionResult>>(
            emptyList()
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),

            factory = { ctx ->

                val previewView = PreviewView(ctx)

                val cameraProviderFuture =
                    ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({

                    val cameraProvider =
                        cameraProviderFuture.get()

                    val preview =
                        Preview.Builder().build()

                    preview.setSurfaceProvider(
                        previewView.surfaceProvider
                    )

                    val cameraSelector =
                        CameraSelector.DEFAULT_FRONT_CAMERA

                    val imageAnalysis =
                        ImageAnalysis.Builder()
                            .setBackpressureStrategy(
                                ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                            )
                            .build()

                    imageAnalysis.setAnalyzer(

                        ContextCompat.getMainExecutor(ctx),

                        FaceAnalyzer(

                            FaceDetectorHelper(ctx),

                            FaceRecognitionHelper(ctx),

                            FaceMeshAnalyzer(
                                FaceMeshHelper(ctx)
                            )

                        ) { detectedFaces ->

                            faces = detectedFaces
                        }
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
    }
}