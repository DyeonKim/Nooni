package com.ssafy.nooni.util

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast

class SttUtil {

    val listener: RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {
            // 말하기 시작할 준비가되면 호출
            Log.d("tst5", "시작")
        }

        override fun onBeginningOfSpeech() {
            // 말하기 시작했을 때 호출
        }

        override fun onRmsChanged(rmsdB: Float) {
            // 입력받는 소리의 크기를 알려줌
        }

        override fun onBufferReceived(buffer: ByteArray) {
            // 말을 시작하고 인식이 된 단어를 buffer에 담음
        }

        override fun onEndOfSpeech() {
            // 말하기를 중지하면 호출
        }

        override fun onError(error: Int) {
            // 네트워크 또는 인식 오류가 발생했을 때 호출
            val message: String
            message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            Log.d("tst5", "onError: $message")
        }

        override fun onResults(results: Bundle) {
            // 인식 결과가 준비되면 호출
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in 0 until matches!!.size) {

                //textView.setText(matches!![i])
            }
            Log.d("tst5", "onError: $matches")
        }

        override fun onPartialResults(partialResults: Bundle) {
            // 부분 인식 결과를 사용할 수 있을 때 호출
        }

        override fun onEvent(eventType: Int, params: Bundle) {
            // 향후 이벤트를 추가하기 위해 예약
        }
    }
}