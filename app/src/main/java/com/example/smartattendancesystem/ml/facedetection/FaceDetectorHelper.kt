package com.example.smartattendancesystem.ml.facedetection

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector
import com.google.mediapipe.tasks.vision.facedetector.FaceDetector.FaceDetectorOptions
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.framework.image.BitmapImageBuilder

class FaceDetectorHelper(context: Context) {

    private val faceDetector: FaceDetector

    init {

        val baseOptions =
            BaseOptions.builder()
                .setModelAssetPath("blaze_face_short_range.tflite")
                .build()

        val options =
            FaceDetectorOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setMinDetectionConfidence(0.5f)
                .build()

        faceDetector =
            FaceDetector.createFromOptions(
                context,
                options
            )
    }

    fun detect(bitmap: Bitmap): List<FaceDetectionResult> {

        val mpImage =
            BitmapImageBuilder(bitmap).build()

        val result =
            faceDetector.detect(mpImage)

        val detections = mutableListOf<FaceDetectionResult>()

        for (detection in result.detections()) {

            val box = detection.boundingBox()

            detections.add(

                FaceDetectionResult(
                    left = box.left,
                    top = box.top,
                    right = box.right,
                    bottom = box.bottom
                )
            )
        }

        Log.d(
            "FaceDetection",
            "Faces Detected: ${detections.size}"
        )

        return detections
    }
}