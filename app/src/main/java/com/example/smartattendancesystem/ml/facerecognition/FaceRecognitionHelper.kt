package com.example.smartattendancesystem.ml.facerecognition

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class FaceRecognitionHelper(
    context: Context
) {

    private var interpreter: Interpreter

    init {

        interpreter =
            Interpreter(
                loadModelFile(
                    context,
                    "mobile_face_net.tflite"
                )
            )

        Log.d(
            "FaceNet",
            "Model Loaded Successfully"
        )
    }

    private fun loadModelFile(
        context: Context,
        modelName: String
    ): MappedByteBuffer {

        val fileDescriptor =
            context.assets.openFd(modelName)

        val inputStream =
            FileInputStream(
                fileDescriptor.fileDescriptor
            )

        val fileChannel =
            inputStream.channel

        val startOffset =
            fileDescriptor.startOffset

        val declaredLength =
            fileDescriptor.declaredLength

        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            startOffset,
            declaredLength
        )
    }

    fun getFaceEmbedding(
        bitmap: Bitmap
    ): FloatArray {

        val resizedBitmap =
            Bitmap.createScaledBitmap(
                bitmap,
                112,
                112,
                true
            )

        val inputBuffer =
            convertBitmapToBuffer(
                resizedBitmap
            )

        val output =
            Array(1) {
                FloatArray(192)
            }

        Log.d(
            "FaceNet",
            "Running inference..."
        )

        interpreter.run(
            inputBuffer,
            output
        )

        Log.d(
            "FaceNet",
            "Inference completed"
        )

        return output[0]
    }

    private fun convertBitmapToBuffer(
        bitmap: Bitmap
    ): ByteBuffer {

        val buffer =
            ByteBuffer.allocateDirect(
                1 * 112 * 112 * 3 * 4
            )

        buffer.order(
            ByteOrder.nativeOrder()
        )

        val pixels =
            IntArray(112 * 112)

        bitmap.getPixels(
            pixels,
            0,
            112,
            0,
            0,
            112,
            112
        )

        var pixelIndex = 0

        for (i in 0 until 112) {

            for (j in 0 until 112) {

                val pixel =
                    pixels[pixelIndex++]

                buffer.putFloat(
                    ((pixel shr 16 and 0xFF) - 128f) / 128f
                )

                buffer.putFloat(
                    ((pixel shr 8 and 0xFF) - 128f) / 128f
                )

                buffer.putFloat(
                    ((pixel and 0xFF) - 128f) / 128f
                )
            }
        }

        return buffer
    }
}