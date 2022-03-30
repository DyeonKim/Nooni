package com.ssafy.nooni

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.nooni.databinding.FragmentTutorialAllergyBinding


class TutorialAllergyFragment : Fragment() {
    private lateinit var binding: FragmentTutorialAllergyBinding
    private lateinit var registerAllergyAct: RegisterAllergyActivity
    private lateinit var tutoAllergyPagerAdapter: TutoAllergyPagerAdapter
    private val files = ArrayList<Pair<String, String>>()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerAllergyAct = context as RegisterAllergyActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTutorialAllergyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        files.add(Pair("yes_or_no.json", "방법 1"))
        files.add(Pair("voice_icon.json", "방법 2"))
        files.add(Pair("hand_click_gesture.json", "방법 3"))

        initView()
        initEvent()
    }

    private fun initView() {
        tutoAllergyPagerAdapter = TutoAllergyPagerAdapter(files)
        binding.vpagerAllergyTutorial.apply {
            adapter     = tutoAllergyPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }

        binding.dotsIndicator.setViewPager2(binding.vpagerAllergyTutorial)
    }

    private fun initEvent() {
        binding.btnPrev.setOnClickListener {
            val currentItem = binding.vpagerAllergyTutorial.currentItem

            if (currentItem > 0) {
                binding.vpagerAllergyTutorial.currentItem = currentItem - 1
            }
        }

        binding.btnNext.setOnClickListener {
            val currentItem = binding.vpagerAllergyTutorial.currentItem

            if (currentItem < files.size - 1) {
                binding.vpagerAllergyTutorial.currentItem = currentItem + 1
            }
        }

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