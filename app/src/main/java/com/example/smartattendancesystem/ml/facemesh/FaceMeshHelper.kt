package com.example.smartattendancesystem.ml.facemesh

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.google.mediapipe.framework.image.BitmapImageBuilder

class FaceMeshHelper(
    context: Context
) {

    private val faceLandmarker: FaceLandmarker

    init {

        val baseOptions =
            BaseOptions.builder()
                .setModelAssetPath(
                    "face_landmarker.task"
                )
                .build()

        val options =
            FaceLandmarker.FaceLandmarkerOptions
                .builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(
                    RunningMode.IMAGE
                )
                .setNumFaces(1)
                .build()

        faceLandmarker =
            FaceLandmarker.createFromOptions(
                context,
                options
            )
    }

    fun detectFaceMesh(
        bitmap: Bitmap
    ): FaceLandmarkerResult? {

        val mpImage =
            BitmapImageBuilder(bitmap)
                .build()

        return faceLandmarker.detect(mpImage)
    }
}