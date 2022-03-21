package com.ssafy.nooni

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.ERROR
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.nooni.Viewmodel.SttViewModel
import com.ssafy.nooni.adapter.ViewpagerFragmentAdapter
import com.ssafy.nooni.databinding.ActivityMainBinding
import com.ssafy.nooni.util.PermissionUtil
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var permissionUtil: PermissionUtil
    private val sttViewModel: SttViewModel by viewModels()
    private lateinit var mRecognizer: SpeechRecognizer
    lateinit var tts: TextToSpeech
    lateinit var viewpager: ViewPager2
    lateinit var i: Intent
    private var cnt = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionUtil = PermissionUtil(this)
        permissionUtil.permissionListener = object : PermissionUtil.PermissionListener {
            override fun run() {
                init()
            }
        }
        STTinit()

        sttViewModel.stt.observe(this) {
            Log.d("tst5", "onCreate:1111 " + sttViewModel.stt.value)
        }
        sttViewModel.nooni.observe(this) {
            if (sttViewModel.nooni.value == true) {
                ttsSpeak(resources.getString(R.string.NooniReady))
            }
        }
    }

    fun STTinit() {
        i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        mRecognizer.setRecognitionListener(sttlistener)
        mRecognizer.startListening(i)
    }

    private fun init() {
        viewpager = binding.viewpager
        val viewpagerFragmentAdapter = ViewpagerFragmentAdapter(this)

        viewpager.adapter = viewpagerFragmentAdapter
        viewpager.currentItem = 1

        tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            @Override
            fun onInit(status: Int) {
                if (status != ERROR) {
                    tts.language = Locale.KOREA
                }
            }
        })
    }

    fun ttsSpeak(text: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    override fun onBackPressed() {
        tts.speak("누니를 종료합니다.", TextToSpeech.QUEUE_FLUSH, null)
        val handler = Handler()
        handler.postDelayed(Runnable {
            tts.shutdown()
            moveTaskToBack(true)
            finish()
        }, 1200)
    }

    override fun onDestroy() {
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
        if (mRecognizer != null) {
            mRecognizer.destroy()
            mRecognizer.cancel()
        }
        super.onDestroy()
    }

    private fun checkPermissions() {
        if (!permissionUtil.checkPermissions(
                listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.CALL_PHONE
                )
            )
        ) {
            permissionUtil.requestPermissions()
        } else {
            init()
        }
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
            mRecognizer.startListening(i)

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
            sttViewModel.setStt(matches)
            if (resultStr.isEmpty()) return
            resultStr = resultStr.replace(" ", "");
            moveActivity(resultStr);
            Log.d("tst5", "onResult: $matches")
            mRecognizer.startListening(i)
        }

        override fun onPartialResults(partialResults: Bundle) {
            // 부분 인식 결과를 사용할 수 있을 때 호출
        }

        override fun onEvent(eventType: Int, params: Bundle) {
            // 향후 이벤트를 추가하기 위해 예약
        }
    }

    fun moveActivity(resultString: String) {
        //누니야 부른상태 <- true
        if (sttViewModel.nooni.value == true) {

            //카메라(홈)화면으로 가기위한 말을 했는지 탐색 찾으면 수행하고 종료
            resources.getStringArray(R.array.camera).forEach {
                if (resultString.indexOf(it) > -1) {
                    viewpager.currentItem = 1
                    sttViewModel.setNooni(false)
                    return@moveActivity
                }
            }

            //연락처로 가기위한 말했는지 탐색 찾으면 수행하고 종료
            resources.getStringArray(R.array.contact).forEach {
                if (resultString.indexOf(it) > -1) {
                    viewpager.currentItem = 2
                    sttViewModel.setNooni(false)
                    return@moveActivity
                }
            }

            //1회만 다시말하기 요청 또 오류나면 누니종료 <- 누니야 다시 시작해야함
            if (cnt == 0) {
                ttsSpeak(resources.getString(R.string.NooniAgain))
                cnt++
            } else {
                sttViewModel.setNooni(false)
                cnt = 0
            }
            return@moveActivity
        }
        //누니야를 부르는것 탐색
        resources.getStringArray(R.array.CallNooni).forEach {
            if (resultString.indexOf(it) > -1) {
                sttViewModel.setNooni(true)
                return@moveActivity
            }
        }
    }
}