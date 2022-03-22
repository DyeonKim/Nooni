package com.ssafy.nooni.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.ssafy.nooni.entity.DetectedImage
import com.ssafy.nooni.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

private const val TAG = "ImageDetectUtil"

class ImageDetectUtil(val context: Context) {
    val classes = arrayOf(
        "꼬깔콘고소한맛",
        "크라운콘초",
        "해태맛동산",
        "오리온고소미",
        "해태에이스",
        "머거본알땅콩",
        "해태오예스",
        "해태오사쯔",
        "해태구운감자",
        "크라운초코하임",
        "맥콜",
        "킨사이다",
        "코카콜라",
        "펩시",
        "갈배사이다",
        "아침에사과",
        "하늘보리",
        "환타오렌지",
        "환타파인애플",
        "레쓰비",
        "광동제약위생천",
        "마데카솔",
        "바른생각익스트림에어핏",
        "안티푸라민연고",
        "해피홈아쿠아밴드",
        "가그린오리지널",
        "유한해피홈멸균밴드",
        "오카모토리얼핏003",
        "페리오46cm쿨민트치약",
        "카카오프렌즈밴드중형"
    )

    var detectImage = Array(classes.size) {
        DetectedImage(0, null, 0.0f)
    }

    val IMAGE_SIZE = 224
    val GIVEN_TIME = 3.0 // 주어진 시간
    val CHECK_CNT = 10 // GIVEN_TIME 동안 검사할 횟수
    val INTERVAL = GIVEN_TIME * 1000 / CHECK_CNT // 검사 하는 시간 간격 (ms)
    private val SUCCESS_RATE = 0.8 // 성공률 80% = 0.8

    fun classifyImage(image: Bitmap, originImage: Bitmap) {
        try {
            var model: Model = Model.newInstance(context)

            val inputFeature0: TensorBuffer = TensorBuffer.createFixedSize(intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * IMAGE_SIZE * IMAGE_SIZE * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)
            image.getPixels(intValues, 0, IMAGE_SIZE, 0, 0, image.width, image.height)

            // 244 244
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
            // find the index of the class with the biggest confidence.
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }

            if(detectImage[maxPos] == null) {
                detectImage[maxPos] = DetectedImage(1, originImage, maxConfidence)
            } else {
                detectImage[maxPos]!!.update(originImage, maxConfidence)
            }

            model.close()
        } catch (e: IOException) {
            Log.e(TAG, "Photo capture failed: ${e.message}")
        }
    }

    fun evaluateImage() {
        var maxPos = 0

        for (i in detectImage.indices) {
            if (detectImage[i].cnt > detectImage[maxPos].cnt) {
                maxPos = i
            }
        }

        if(detectImage[maxPos].cnt / CHECK_CNT.toFloat() >= SUCCESS_RATE) {
            Toast.makeText(context, "${String.format("%s: %.1f%%\n", classes[maxPos], detectImage[maxPos].confidence * 100)}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "${String.format("%s: %.1f%%\n", classes[maxPos], detectImage[maxPos].confidence * 100)}", Toast.LENGTH_SHORT).show()
            // 카톡 전송으로 빠지기
        }
    }
}