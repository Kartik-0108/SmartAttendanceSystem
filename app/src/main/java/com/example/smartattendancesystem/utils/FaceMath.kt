package com.example.smartattendancesystem.utils

import kotlin.math.sqrt

object FaceMath {

    fun calculateCosineSimilarity(
        embedding1: FloatArray,
        embedding2: FloatArray
    ): Float {

        if (embedding1.size != embedding2.size) {
            return 0f
        }

        var dotProduct = 0f
        var normA = 0f
        var normB = 0f

        for (i in embedding1.indices) {
            dotProduct += embedding1[i] * embedding2[i]
            normA += embedding1[i] * embedding1[i]
            normB += embedding2[i] * embedding2[i]
        }

        val norm = (sqrt(normA) * sqrt(normB))
        return if (norm == 0f) 0f else dotProduct / norm
    }
}