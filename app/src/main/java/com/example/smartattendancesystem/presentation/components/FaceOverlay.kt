package com.example.smartattendancesystem.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.smartattendancesystem.ml.facedetection.FaceDetectionResult

@Composable
fun FaceOverlay(
    faces: List<FaceDetectionResult>
) {

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {

        faces.forEach { face ->

            drawRect(
                color = Color.Green,

                topLeft = Offset(
                    face.left,
                    face.top
                ),

                size = Size(
                    face.right - face.left,
                    face.bottom - face.top
                ),

                style = Stroke(width = 5f)
            )
        }
    }
}