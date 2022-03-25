package com.ssafy.nooni.tutorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.nooni.R
import com.ssafy.nooni.databinding.ActivityMainBinding
import com.ssafy.nooni.databinding.ActivityTutorialBinding

class TutorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTutorialBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val pagerAdapter = TutorialViewPagerAdapter(this)
        // 3개의 Fragment Add
        pagerAdapter.addFragment(TutorialOneFragment())
        pagerAdapter.addFragment(TutorialTwoFragment())
        pagerAdapter.addFragment(TutorialThreeFragment())
        // Adapter
        binding.viewPager.adapter = pagerAdapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                binding.indicator0IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_gray))
                binding.indicator1IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_gray))
                binding.indicator2IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_gray))

                when(position) {
                    0 -> binding.indicator0IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_white))
                    1 -> binding.indicator1IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_white))
                    2 -> binding.indicator2IvTutorial.setImageDrawable(getDrawable(R.drawable.shape_circle_white))
                }

            }
        })


    }
}