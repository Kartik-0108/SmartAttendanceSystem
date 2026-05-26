package com.example.smartattendancesystem.ml.facemesh

import android.graphics.Bitmap
import android.util.Log

class FaceMeshAnalyzer(
    private val faceMeshHelper: FaceMeshHelper,
    private val onLivenessDetected: (Boolean) -> Unit
) {

    private val blinkDetector =
        BlinkDetector()

    fun analyze(bitmap: Bitmap) {

        try {

            val result =
                faceMeshHelper
                    .detectFaceMesh(bitmap)

            if (
                result != null &&
                result.faceLandmarks().isNotEmpty()
            ) {

                Log.d(
                    "FaceMesh",
                    "Face landmarks detected"
                )

                val landmarks =
                    result.faceLandmarks()[0]

                val blink =
                    blinkDetector.detectBlink(
                        landmarks
                    )

                Log.d(
                    "BlinkDetector",
                    "Blink Status = $blink"
                )

                if (blink) {

                    Log.d(
                        "Liveness",
                        "REAL PERSON DETECTED"
                    )

                    onLivenessDetected(true)
                }

            } else {

                Log.d(
                    "FaceMesh",
                    "No face landmarks detected"
                )
            }

        } catch (e: Exception) {

            Log.e(
                "FaceMesh",
                "ERROR: ${e.message}"
            )

            e.printStackTrace()
        }
    }
}