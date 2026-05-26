package com.example.smartattendancesystem.ml.facedetection

import com.example.smartattendancesystem.ml.facemesh.FaceMeshAnalyzer
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.smartattendancesystem.ml.facerecognition.FaceRecognitionHelper
import java.io.ByteArrayOutputStream

class FaceAnalyzer(
    private val faceDetectorHelper: FaceDetectorHelper,
    private val faceRecognitionHelper: FaceRecognitionHelper,
    private val faceMeshAnalyzer: FaceMeshAnalyzer,

    private val onFacesDetected:
        (List<FaceDetectionResult>) -> Unit
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {

        val bitmap = imageProxyToBitmap(image)

        if (bitmap != null) {

            faceMeshAnalyzer.analyze(bitmap)

            val faces =
                faceDetectorHelper.detect(bitmap)

            onFacesDetected(faces)

            if (faces.isNotEmpty()) {

                Log.d(
                    "EmbeddingDebug",
                    "Face detected, starting embedding"
                )

                val face = faces[0]

                try {

                    val left =
                        face.left.toInt().coerceAtLeast(0)

                    val top =
                        face.top.toInt().coerceAtLeast(0)

                    val width =
                        (face.right - face.left)
                            .toInt()
                            .coerceAtLeast(1)

                    val height =
                        (face.bottom - face.top)
                            .toInt()
                            .coerceAtLeast(1)

                    val safeWidth =
                        if (left + width > bitmap.width) {
                            bitmap.width - left
                        } else {
                            width
                        }

                    val safeHeight =
                        if (top + height > bitmap.height) {
                            bitmap.height - top
                        } else {
                            height
                        }

                    Log.d(
                        "FaceCrop",
                        "left=$left top=$top width=$safeWidth height=$safeHeight"
                    )

                    val croppedFace =
                        Bitmap.createBitmap(
                            bitmap,
                            left,
                            top,
                            safeWidth,
                            safeHeight
                        )

                    Log.d(
                        "FaceRecognition",
                        "Face detected successfully"
                    )
//                    Thread {
//
//                        try {
//
//                            val embedding =
//                                faceRecognitionHelper
//                                    .getFaceEmbedding(croppedFace)
//
//                            Log.d(
//                                "EmbeddingDebug",
//                                "Embedding generated successfully"
//                            )
//
//                            Log.d(
//                                "EmbeddingSize",
//                                embedding.size.toString()
//                            )
//
//                        } catch (e: Exception) {
//
//                            Log.e(
//                                "FaceRecognition",
//                                "ERROR: ${e.message}"
//                            )
//
//                            e.printStackTrace()
//                        }
//
//                    }.start()

                } catch (e: Exception) {

                    Log.e(
                        "FaceCrop",
                        "ERROR: ${e.message}"
                    )

                    e.printStackTrace()
                }
            }
        }

        image.close()
    }

    private fun imageProxyToBitmap(
        image: ImageProxy
    ): Bitmap? {

        return try {

            val planeProxy = image.planes[0]

            val buffer = planeProxy.buffer

            val bytes =
                ByteArray(buffer.remaining())

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

            android.graphics.BitmapFactory
                .decodeByteArray(
                    imageBytes,
                    0,
                    imageBytes.size
                )

        } catch (e: Exception) {

            Log.e(
                "BitmapConversion",
                "ERROR: ${e.message}"
            )

            null
        }
    }
}