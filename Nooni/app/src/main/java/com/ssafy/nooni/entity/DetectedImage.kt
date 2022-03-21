package com.ssafy.nooni.entity

import android.graphics.Bitmap

data class DetectedImage(var cnt: Int = 0, var image: Bitmap?, var confidence: Float = 0.0f) {
    fun update(image: Bitmap, confidence: Float) {
        cnt++

        if(confidence > this.confidence) {
            this.image = image
            this.confidence = confidence
        }
    }
}