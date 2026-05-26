package com.example.smartattendancesystem.ml.facerecognition

import kotlin.math.sqrt

object EmbeddingComparator {

    fun compare(
        emb1: FloatArray,
        emb2: FloatArray
    ): Float {

        var sum = 0f

        for (i in emb1.indices) {

            val diff =
                emb1[i] - emb2[i]

            sum += diff * diff
        }

        return sqrt(sum)
    }
}