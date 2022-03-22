package com.ssafy.nooni.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ssafy.nooni.MainActivity
import com.ssafy.nooni.Viewmodel.SttViewModel
import kotlin.properties.ReadOnlyProperty

class STTUtil(owner: ViewModelStoreOwner) {
    lateinit var sttIntent: Intent
    private var mRecognizer: SpeechRecognizer? = null
    private val sttViewModel =
        ViewModelProvider(owner, ViewModelProvider.NewInstanceFactory())[SttViewModel::class.java]

    //STT 시작
    fun STTinit(context: Context, packageName: String) {
        sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        mRecognizer!!.setRecognitionListener(sttlistener)
        mRecognizer!!.startListening(sttIntent)
    }

    val sttlistener: RecognitionListener = object : RecognitionListener {
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
            Log.d("tst5", "중지")
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
            //ttsSpeak("오류가 발생했습니다.")
            Log.d("tst5", "onError: $message")
            mRecognizer!!.startListening(sttIntent)

        }

        override fun onResults(results: Bundle) {
            // 인식 결과가 준비되면 호출
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            var resultStr = ""
            for (i in 0 until matches!!.size) {
                resultStr += matches[i];
                //textView.setText(matches!![i])
            }

            if (resultStr.isEmpty()) return
            resultStr = resultStr.replace(" ", "")
            sttViewModel.setStt(resultStr)
            Log.d("tst5", "onResult: $matches")
            mRecognizer!!.startListening(sttIntent)
        }

        override fun onPartialResults(partialResults: Bundle) {
            // 부분 인식 결과를 사용할 수 있을 때 호출
        }

        override fun onEvent(eventType: Int, params: Bundle) {
            // 향후 이벤트를 추가하기 위해 예약
        }
    }

    fun stop() {
        if (mRecognizer != null) {
            mRecognizer!!.destroy()
            mRecognizer!!.cancel()
            mRecognizer = null
        }
    }
}