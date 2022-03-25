package com.ssafy.nooni

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.ERROR
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
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
    private var tts2: TextToSpeech? = null
    private val list = resources.getStringArray(R.array.allergy_names)
    private val allergyList = ArrayList<String>()
    var cnt = 0
    var noonicnt = 0
    private val sttViewModel: SttViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterAllergyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharePrefArrayListUtil = SharedPrefArrayListUtil(this)

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
        Log.d("tst6", "onCreate: " + sttViewModel.stt.value)
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
                if (cnt == 0) {
                    ttsSpeak(resources.getString(R.string.AllergyQuestion))
                } else {
                    ttsSpeak(resources.getString(R.string.NooniAgain))
                }
                noonicnt++
            } else {
                sttViewModel.setNooni(false)
                noonicnt = 0
            }
            return@observe
        }


        sttViewModel.nooni.observe(this) {

            if (sttViewModel.nooni.value == false) {
                ttsSpeak("나는 " + list[cnt] + " 알레르기가 있다")
            }
        }
    }

    private fun ttsSpeak(text: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            tts2?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts2?.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private fun init() {
        // TODO: 맨 처음 멘트 안나오는거 수정해야함
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

    private fun allergyNext() {
        if (++cnt >= list.size) save()
        else {
            binding.tvAllergyAType.text = list[cnt]
            // TODO: 이런식으로 안드로이드 string.xml에서 가져와서 연결하면 tts가 깨짐 도대체 왜????
//            val sb = StringBuilder()
//            sb.append(resources.getString(R.string.AllergyPrefix))
//            sb.append(list[cnt])
//            sb.append(resources.getString(R.string.AllergyPostfix))
//            ttsSpeak(sb.toString())
            ttsSpeak("나는 " + list[cnt] + " 알레르기가 있다")
        }
    }

    private fun save() {
        sharePrefArrayListUtil.setAllergies(allergyList)
        Toast.makeText(this, "알레르기 정보 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        tts2?.speak("알레르기 정보 등록이 완료되었습니다.", TextToSpeech.QUEUE_FLUSH, null)
        val handler = Handler()
        handler.postDelayed(Runnable {
            tts2?.shutdown()
            finish()
        }, 2000)
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
        val handler = Handler()
        handler.postDelayed(Runnable {
            tts2?.shutdown()
            finish()
        }, 1600)
    }
}