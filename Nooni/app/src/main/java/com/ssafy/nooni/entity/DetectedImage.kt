package com.ssafy.nooni.entity

import android.graphics.Bitmap

data class DetectedImage(
    var id: Int = 0,
    var image: Bitmap?,
    var confidence: Float = 0.0f
) : Comparable<DetectedImage> {

    override fun compareTo(other: DetectedImage): Int {
        return if(confidence < other.confidence) {
            1
        } else {
            -1
        }
    }
}