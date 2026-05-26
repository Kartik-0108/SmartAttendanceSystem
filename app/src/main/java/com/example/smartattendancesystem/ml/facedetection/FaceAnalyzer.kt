package com.example.smartattendancesystem.ml.facedetection

import android.annotation.SuppressLint
import android.graphics.*
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.smartattendancesystem.ml.facemesh.FaceMeshAnalyzer
import com.example.smartattendancesystem.ml.facerecognition.FaceRecognitionHelper
import java.util.concurrent.atomic.AtomicBoolean

class FaceAnalyzer(
    private val faceDetectorHelper: FaceDetectorHelper,
    private val faceRecognitionHelper: FaceRecognitionHelper,
    private val faceMeshAnalyzer: FaceMeshAnalyzer,
    private val onFacesDetected: (List<FaceDetectionResult>) -> Unit,
    private val onEmbeddingGenerated: (FloatArray) -> Unit = {}
) : ImageAnalysis.Analyzer {

    private val isProcessingEmbedding = AtomicBoolean(false)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        try {
            val bitmap = image.toBitmap()
            val rotationDegrees = image.imageInfo.rotationDegrees
            
            val rotatedBitmap = rotateBitmap(bitmap, rotationDegrees.toFloat())
            
            faceMeshAnalyzer.analyze(rotatedBitmap)

            val faces = faceDetectorHelper.detect(rotatedBitmap)
            onFacesDetected(faces)

            if (faces.isNotEmpty()) {
                if (!isProcessingEmbedding.get()) {
                    val face = faces[0]
                    
                    var left = face.left
                    var top = face.top
                    var right = face.right
                    var bottom = face.bottom

                    // Handle normalized coordinates (MediaPipe sometimes returns 0..1)
                    if (right <= 1.05f && bottom <= 1.05f) {
                        left *= rotatedBitmap.width
                        top *= rotatedBitmap.height
                        right *= rotatedBitmap.width
                        bottom *= rotatedBitmap.height
                    }

                    val x = left.toInt().coerceIn(0, rotatedBitmap.width - 1)
                    val y = top.toInt().coerceIn(0, rotatedBitmap.height - 1)
                    val w = (right - left).toInt().coerceAtMost(rotatedBitmap.width - x).coerceAtLeast(1)
                    val h = (bottom - top).toInt().coerceAtMost(rotatedBitmap.height - y).coerceAtLeast(1)

                    if (w > 20 && h > 20) {
                        val croppedFace = Bitmap.createBitmap(rotatedBitmap, x, y, w, h)

                        if (isProcessingEmbedding.compareAndSet(false, true)) {
                            Thread {
                                try {
                                    val embedding = faceRecognitionHelper.getFaceEmbedding(croppedFace)
                                    onEmbeddingGenerated(embedding)
                                    Log.d("FaceCapture", "Embedding Generated Successfully")
                                } catch (e: Exception) {
                                    Log.e("FaceCapture", "Error: ${e.message}")
                                } finally {
                                    isProcessingEmbedding.set(false)
                                }
                            }.start()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FaceAnalyzer", "Analyze failure", e)
        } finally {
            image.close()
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        if (degrees == 0f) return bitmap
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}