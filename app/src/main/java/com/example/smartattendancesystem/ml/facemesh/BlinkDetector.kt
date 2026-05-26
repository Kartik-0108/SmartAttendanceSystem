package com.example.smartattendancesystem.ml.facemesh

import android.util.Log
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.sqrt

class BlinkDetector {

    private var previousBlinkState = false

    fun detectBlink(
        landmarks: List<NormalizedLandmark>
    ): Boolean {

        // Left Eye Landmarks
        val top =
            landmarks[159]

        val bottom =
            landmarks[145]

        val left =
            landmarks[33]

        val right =
            landmarks[133]

        val verticalDistance =
            distance(top, bottom)

        val horizontalDistance =
            distance(left, right)

        val ear =
            verticalDistance / horizontalDistance

        Log.d(
            "BlinkEAR",
            ear.toString()
        )

        val eyeClosed =
            ear < 0.26

        var blinkDetected = false

        if (previousBlinkState && !eyeClosed) {

            blinkDetected = true

            Log.d(
                "BlinkDetector",
                "BLINK DETECTED"
            )
        }

        previousBlinkState = eyeClosed

        return blinkDetected
    }

    private fun distance(
        p1: NormalizedLandmark,
        p2: NormalizedLandmark
    ): Float {

        return sqrt(
            ((p1.x() - p2.x()) *
                    (p1.x() - p2.x()) +

                    (p1.y() - p2.y()) *
                    (p1.y() - p2.y()))
        )
    }
}