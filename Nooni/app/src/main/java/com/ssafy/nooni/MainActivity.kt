package com.ssafy.nooni

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.ERROR
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.nooni.viewmodel.SttViewModel
import com.ssafy.nooni.adapter.ViewpagerFragmentAdapter
import com.ssafy.nooni.databinding.ActivityMainBinding
import com.ssafy.nooni.util.PermissionUtil
import com.ssafy.nooni.util.STTUtil
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var permissionUtil: PermissionUtil
    private val sttViewModel: SttViewModel by viewModels()
    lateinit var tts: TextToSpeech
    lateinit var viewpager: ViewPager2
    private var cnt = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        STTUtil.owner = this
        STTUtil.STTinit(this, packageName)
        permissionUtil = PermissionUtil(this)
        permissionUtil.permissionListener = object : PermissionUtil.PermissionListener {
            override fun run() {
                init()
            }
        }


        sttViewModel.stt.observe(this) {
            val resultString = sttViewModel.stt.value!!
            if (sttViewModel.nooni.value == true) {

                //카메라(홈)화면으로 가기위한 말을 했는지 탐색 찾으면 수행하고 종료
                resources.getStringArray(R.array.camera).forEach {
                    if (resultString.indexOf(it) > -1) {
                        viewpager.currentItem = 1
                        sttViewModel.setNooni(false)
                        return@observe
                    }
                }

                //연락처로 가기위한 말했는지 탐색 찾으면 수행하고 종료
                resources.getStringArray(R.array.contact).forEach {
                    if (resultString.indexOf(it) > -1) {
                        viewpager.currentItem = 2
                        sttViewModel.setNooni(false)
                        return@observe
                    }
                }

                resources.getStringArray(R.array.allergy).forEach {
                    if (resultString.indexOf(it) > -1) {
                        //STTUtil.stop()
                        //startActivity(Intent(this,RegisterAllergyActivity::class.java))

                        viewpager.currentItem = 0
                        sttViewModel.setNooni(false)
                        return@observe
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
                return@observe
            }
            //누니야를 부르는것 탐색
            resources.getStringArray(R.array.CallNooni).forEach {
                if (resultString.indexOf(it) > -1) {
                    sttViewModel.setNooni(true)
                    return@observe
                }
            }
            Log.d("tst5", "onCreate:1111 " + sttViewModel.stt.value)
        }
        sttViewModel.nooni.observe(this) {
            if (sttViewModel.nooni.value == true) {
                ttsSpeak(resources.getString(R.string.NooniReady))
            }
        }
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
        ttsSpeak(resources.getString(R.string.NooniClose))
        val handler = Handler()
        handler.postDelayed(Runnable {
            tts.shutdown()
            moveTaskToBack(true)
            finish()
        }, 1200)
    }

    override fun onRestart() {
        STTUtil.owner = this
        STTUtil.STTVM()
        super.onRestart()
    }

    override fun onDestroy() {
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
        STTUtil.stop()
        super.onDestroy()
    }

    private fun checkPermissions() {
        if (!permissionUtil.checkPermissions(
                listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        ) {
            permissionUtil.requestPermissions()
        } else {
            init()
        }
    }
}