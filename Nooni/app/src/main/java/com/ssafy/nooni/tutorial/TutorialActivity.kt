package com.ssafy.nooni.tutorial

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.nooni.MainActivity
import com.ssafy.nooni.R
import com.ssafy.nooni.adapter.ViewpagerFragmentAdapter
import com.ssafy.nooni.databinding.ActivityMainBinding
import com.ssafy.nooni.databinding.ActivityTutorialBinding
import java.util.*

class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding
    lateinit var tts: TextToSpeech
    var isLastPageScroll = false
    var counterPageScroll = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
            putBoolean("COMPLETED_ONBOARDING", true)
            apply()
        }



        initTTS()

        binding.btn.setOnClickListener {
            var current = binding.viewPager.currentItem
            if(current  == 3) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()

            } else {
                binding.viewPager.setCurrentItem(current+1, false)
            }
        }


    }

    fun initTTS() {
        tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            @Override
            fun onInit(status: Int) {
                if (status != TextToSpeech.ERROR) {

                    tts.language = Locale.KOREA
                }
            }
        })
        initViewPager()

    }

    fun initViewPager() {
        val pagerAdapter = TutorialViewPagerAdapter(this)
        // 4개의 Fragment Add
        pagerAdapter.addFragment(TutorialOneFragment())
        pagerAdapter.addFragment(TutorialTwoFragment())
        pagerAdapter.addFragment(TutorialThreeFragment())
        pagerAdapter.addFragment(TutorialFourFragment())
        // Adapter
        binding.viewPager.adapter = pagerAdapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                binding.indicator0IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_gray))
                binding.indicator1IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_gray))
                binding.indicator2IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_gray))
                binding.indicator3IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_gray))

                when(position) {

                    0 -> {
                        binding.indicator0IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_white))
                        binding.btnTv.text = "다음"
                    }
                    1 -> {
                        binding.indicator1IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_white))
                        binding.btnTv.text = "다음"
                    }
                    2 -> {
                        binding.indicator2IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_white))
                        binding.btnTv.text = "다음"
                    }
                    3 -> {
                        binding.indicator3IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_white))
                        binding.btnTv.text = "시작하기"
                    }
                }

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if(position == 3 && positionOffset == 0.0f && !isLastPageScroll) {
                    if(counterPageScroll != 0) {
                        isLastPageScroll = true
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    }
                    counterPageScroll++
                } else {
                    counterPageScroll = 0
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

    override fun onPause() {
        super.onPause()
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
    }
}