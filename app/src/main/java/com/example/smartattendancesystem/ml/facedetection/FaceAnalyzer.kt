package com.example.smartattendancesystem.ml.facedetection

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class FaceAnalyzer : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {

        Log.d(
            "FaceAnalyzer",
            "Frame Received: ${image.width} x ${image.height}"
        )

        image.close()
    }
}