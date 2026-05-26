package com.example.smartattendancesystem.ml.facedetection

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream

class FaceAnalyzer(
    private val faceDetectorHelper: FaceDetectorHelper
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {

        val bitmap =
            imageProxyToBitmap(image)

        if (bitmap != null) {
            faceDetectorHelper.detect(bitmap)
        }

        image.close()
    }

    private fun imageProxyToBitmap(
        image: ImageProxy
    ): Bitmap? {

        val planeProxy = image.planes[0]

        val buffer = planeProxy.buffer

        val bytes = ByteArray(buffer.remaining())

        buffer.get(bytes)

        val yuvImage =
            YuvImage(
                bytes,
                ImageFormat.NV21,
                image.width,
                image.height,
                null
            )

        val out =
            ByteArrayOutputStream()

        yuvImage.compressToJpeg(
            Rect(0, 0, image.width, image.height),
            100,
            out
        )

        val imageBytes = out.toByteArray()

        return android.graphics.BitmapFactory.decodeByteArray(
            imageBytes,
            0,
            imageBytes.size
        )
    }
}