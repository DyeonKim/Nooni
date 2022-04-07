package com.ssafy.nooni.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.ssafy.nooni.entity.DetectedImage
import com.ssafy.nooni.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

private const val TAG = "ImageDetectUtil"

class ImageDetectUtil(val context: Context) {
    val IMAGE_SIZE = 224
    val GIVEN_TIME = 3.0 // 주어진 시간
    val CHECK_CNT = 3 // GIVEN_TIME 동안 검사할 횟수
    val SUCCESS_RATE = 98 // ex) 성공률 80% = 80

    var pq = PriorityQueue<DetectedImage>()

    fun classifyImage(image: Bitmap) {
        pq.clear()

        try {
            var model: Model = Model.newInstance(context)

            val inputFeature0: TensorBuffer = TensorBuffer.createFixedSize(intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * IMAGE_SIZE * IMAGE_SIZE * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)
            image.getPixels(intValues, 0, IMAGE_SIZE, 0, 0, image.width, image.height)

            var pixel = 0
            for (i in 0 until IMAGE_SIZE) {
                for (j in 0 until IMAGE_SIZE) {
                    val values = intValues[pixel++] // RGB

                    byteBuffer.putFloat((values shr 16 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((values shr 8 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((values and 0xFF) * (1f / 255f))
                }
            }

            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val confidences: FloatArray = outputFeature0.floatArray

            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }

            pq.add(DetectedImage(maxPos, image, maxConfidence))

            model.close()
        } catch (e: IOException) {
            Log.e(TAG, "Photo capture failed: ${e.message}")
        }
    }

    fun getEvaluatedImage(): DetectedImage? {
        return if(!pq.isEmpty()){
            pq.poll()
        } else {
            null
        }
    }
}