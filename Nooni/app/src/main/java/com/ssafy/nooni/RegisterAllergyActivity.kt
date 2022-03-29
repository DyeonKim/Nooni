package com.ssafy.nooni

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.ERROR
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.GestureDetectorCompat
import com.ssafy.nooni.viewmodel.SttViewModel
import com.ssafy.nooni.databinding.ActivityRegisterAllergyBinding
import com.ssafy.nooni.util.STTUtil
import com.ssafy.nooni.util.SharedPrefArrayListUtil
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "RegisterAllergy"

class RegisterAllergyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterAllergyBinding
    private lateinit var sharePrefArrayListUtil: SharedPrefArrayListUtil
    private lateinit var mDetector: GestureDetectorCompat
    private lateinit var list: Array<String>
    private var tts2: TextToSpeech? = null
    private val allergyList = ArrayList<String>()
    private val sttViewModel: SttViewModel by viewModels()
    var cnt = 0
    var noonicnt = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterAllergyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharePrefArrayListUtil = SharedPrefArrayListUtil(this)
        mDetector = GestureDetectorCompat(this, MyGestureListener())
        list = resources.getStringArray(R.array.allergy_names)

        tts2 = TextToSpeech(this, TextToSpeech.OnInitListener {
            @Override
            fun onInit(status: Int) {
                if (status != ERROR) {
                    tts2?.language = Locale.KOREA
                }
            }
        })
        STTUtil.owner = this
        STTUtil.STTVM()
        init()
        initViewModel()
        Log.d("tst6", "onCreate: " + sttViewModel.stt.value)

        //처음에 시작할때 tts초기화랑 뭔가 타이밍이 안맞는것 같음 어쩔땐 되고 어쩔땐 안되서 억지로 딜레이늘림
        tts2?.setSpeechRate(2f)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            sttViewModel.setStt(resources.getString(R.string.init))
        }, 1000)
    }

    private fun ttsSpeak(text: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            tts2?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts2?.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private fun init() {
        Log.d("tst", "init: " + tts2)
        binding.tvAllergyAType.text = list[cnt]

        binding.btnAllergyANo.setOnClickListener {
            allergyNext()
        }
        binding.btnAllergyAYes.setOnClickListener {
            allergyList.add(list[cnt])
            allergyNext()
        }
    }

    private fun initViewModel() {
        Log.d("tst6", "tst: " + noonicnt + " " + cnt)
        sttViewModel.stt.observe(this) {
            Log.d("tst6", "onCreate: " + sttViewModel.stt.value)
            val resultString = sttViewModel.stt.value!!
            resources.getStringArray(R.array.yes).forEach {
                if (resultString.indexOf(it) > -1) {
                    Log.d("tst6", "onCreate: yes")
                    allergyList.add(list[cnt])
                    allergyNext()
                    return@observe
                }
            }
            resources.getStringArray(R.array.no).forEach {
                if (resultString.indexOf(it) > -1) {
                    Log.d("tst6", "onCreate: no")
                    allergyNext()
                    return@observe
                }
            }
            if (noonicnt == 0) {
                Log.d("tst6", "onCreate: ")
                ttsSpeak(resources.getString(R.string.AllergyQuestion))
                noonicnt++
            } else {
                ttsSpeak(resources.getString(R.string.AllergyNotice, list[cnt]))
                noonicnt = 0
            }
            return@observe
        }
    }

    fun allergyNext() {
        if (++cnt >= list.size) save()
        else {
            binding.tvAllergyAType.text = list[cnt]
            ttsSpeak(resources.getString(R.string.AllergyNotice, list[cnt]))
        }
    }

    private fun save() {
        sharePrefArrayListUtil.setAllergies(allergyList)
        Toast.makeText(this, resources.getString(R.string.AllergyFinish), Toast.LENGTH_SHORT).show()
        tts2?.speak(resources.getString(R.string.AllergyFinish), TextToSpeech.QUEUE_FLUSH, null)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            tts2?.shutdown()
            finish()
        }, 2000)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onRestart() {
        STTUtil.owner = this
        STTUtil.STTVM()
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()

        tts2?.stop()
        tts2?.shutdown()
    }

    override fun onBackPressed() {
        tts2?.speak(resources.getString(R.string.GoBack), TextToSpeech.QUEUE_FLUSH, null)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            tts2?.shutdown()
            finish()
        }, 1600)
    }

    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            allergyList.add(list[cnt])
            allergyNext()
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            allergyNext()
            return true
        }
    }
}