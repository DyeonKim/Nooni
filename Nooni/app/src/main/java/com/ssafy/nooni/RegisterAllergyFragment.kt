package com.ssafy.nooni

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.ssafy.nooni.databinding.FragmentRegisterAllergyBinding
import com.ssafy.nooni.util.SharedPrefArrayListUtil
import com.ssafy.nooni.viewmodel.SttViewModel


class RegisterAllergyFragment : Fragment() {
    private lateinit var binding: FragmentRegisterAllergyBinding
    private lateinit var registerAllergyAct: RegisterAllergyActivity
    private lateinit var sharePrefArrayListUtil: SharedPrefArrayListUtil
    private lateinit var list: Array<String>
    private val allergyList = ArrayList<String>()
    private val sttViewModel: SttViewModel by activityViewModels()
    private var cnt = 0
    private var noonicnt = true


    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerAllergyAct = context as RegisterAllergyActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRegisterAllergyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharePrefArrayListUtil = SharedPrefArrayListUtil(requireContext())
        list = resources.getStringArray(R.array.allergy_names)

        binding.tvAllergyAType.text = list[cnt]

        initEvent()
        initViewModel()
    }

    private fun initEvent() {
        binding.btnAllergyANo.setOnClickListener {
            allergyNext()
        }
        binding.btnAllergyAYes.setOnClickListener {
            allergyList.add(list[cnt])
            allergyNext()
        }
        registerAllergyAct.onAnswerListener = object : RegisterAllergyActivity.OnAnswerListener {
            override fun setAnswer(answer: Boolean) {
                when(answer) {
                    true -> {
                        allergyList.add(list[cnt])
                        allergyNext()
                    }
                    false -> {
                        allergyNext()
                    }
                }
            }
        }
    }

    private fun initViewModel() {
        sttViewModel.stt.observe(viewLifecycleOwner) {
            if (noonicnt) {
                Log.d("tst6", "onCreate: ")
                registerAllergyAct.ttsSpeak(resources.getString(R.string.AllergyQuestion))
            } else {
                registerAllergyAct.ttsSpeak(resources.getString(R.string.AllergyNotice, list[cnt]))
            }
            noonicnt=!noonicnt
            return@observe
        }
    }

    private fun allergyNext() {
        if (++cnt >= list.size) save()
        else {
            binding.tvAllergyAType.text = list[cnt]
            registerAllergyAct.ttsSpeak(resources.getString(R.string.AllergyNotice, list[cnt]))
        }
    }

    private fun save() {
        sharePrefArrayListUtil.setAllergies(allergyList)
        Toast.makeText(requireContext(), resources.getString(R.string.AllergyFinish), Toast.LENGTH_SHORT).show()
        registerAllergyAct.tts2?.speak(resources.getString(R.string.AllergyFinish), TextToSpeech.QUEUE_FLUSH, null)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            registerAllergyAct.tts2?.shutdown()
            registerAllergyAct.finish()
        }, 2000)
    }
}