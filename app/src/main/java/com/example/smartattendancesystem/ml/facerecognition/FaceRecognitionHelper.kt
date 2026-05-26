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
    private val INPUT_SIZE = 160
    private var outputSize: Int = 512

    init {
        interpreter = Interpreter(
            loadModelFile(context, "mobile_face_net.tflite")
        )
        
        // Dynamically get the output size from the model
        val outputShape = interpreter.getOutputTensor(0).shape()
        outputSize = outputShape[outputShape.size - 1]
        
        Log.d("FaceNet", "Model Loaded. Input: ${INPUT_SIZE}x${INPUT_SIZE}, Output: $outputSize")
    }

    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun getFaceEmbedding(bitmap: Bitmap): FloatArray {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        val inputBuffer = convertBitmapToBuffer(resizedBitmap)

        val output = Array(1) { FloatArray(outputSize) }
        interpreter.run(inputBuffer, output)

        return output[0]
    }

    private fun convertBitmapToBuffer(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(1 * INPUT_SIZE * INPUT_SIZE * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        for (pixel in pixels) {
            buffer.putFloat(((pixel shr 16 and 0xFF) - 128f) / 128f)
            buffer.putFloat(((pixel shr 8 and 0xFF) - 128f) / 128f)
            buffer.putFloat(((pixel and 0xFF) - 128f) / 128f)
        }

        return buffer
    }
}