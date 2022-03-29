package com.ssafy.nooni

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.nooni.databinding.FragmentStartRegisterBinding


class TutorialAllergyFragment : Fragment() {
    private lateinit var binding: FragmentStartRegisterBinding
    private lateinit var registerAllergyAct: RegisterAllergyActivity


    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerAllergyAct = context as RegisterAllergyActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentStartRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    private fun initEvent() {
        registerAllergyAct.onAnswerListener = object : RegisterAllergyActivity.OnAnswerListener {
            override fun setAnswer(answer: Boolean) {
                when(answer) {
                    true -> {
                        registerAllergyAct.startRegisterAllergy()
                    }
                    false -> {
                        registerAllergyAct.finish()
                    }
                }
            }
        }
    }
}