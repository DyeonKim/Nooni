package com.ssafy.nooni

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.ERROR
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.ssafy.nooni.Viewmodel.SttViewModel
import com.ssafy.nooni.databinding.ActivityRegisterAllergyBinding
import com.ssafy.nooni.util.STTUtil
import com.ssafy.nooni.util.SharedPrefArrayListUtil
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "RegisterAllergy"

class RegisterAllergyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterAllergyBinding
    private lateinit var tts2: TextToSpeech
    var sharePrefArrayListUtil = SharedPrefArrayListUtil()
    lateinit var list: Array<String>
    val allergyList = ArrayList<String>()
    var cnt = 0
    var noonicnt = 0
    private val sttViewModel: SttViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterAllergyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tts2 = TextToSpeech(this, TextToSpeech.OnInitListener {
            @Override
            fun onInit(status: Int) {
                if (status != ERROR) {
                    tts2.language = Locale.KOREA
                }
            }
        })
        list = resources.getStringArray(R.array.allergyList)
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
                ttsSpeak(resources.getString(R.string.AllergyNotice,list[cnt]))
            }
        }
    }

    private fun ttsSpeak(text: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            tts2.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts2.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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
            ttsSpeak(resources.getString(R.string.AllergyNotice,list[cnt]))
        }
    }

    private fun save() {
        sharePrefArrayListUtil.setStringArrayPref(this, "allergies", allergyList)
        Toast.makeText(this, resources.getString(R.string.AllergyFinish), Toast.LENGTH_SHORT).show()
        tts2.speak(resources.getString(R.string.AllergyFinish), TextToSpeech.QUEUE_FLUSH, null)
        val handler = Handler()
        handler.postDelayed(Runnable {
            tts2.shutdown()
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
        if (tts2 != null) {
            tts2.stop()
            tts2.shutdown()
        }
    }

    override fun onBackPressed() {
        tts2.speak(resources.getString(R.string.GoBack), TextToSpeech.QUEUE_FLUSH, null)
        val handler = Handler()
        handler.postDelayed(Runnable {
            tts2.shutdown()
            finish()
        }, 1600)
    }
}