package com.ssafy.nooni.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.nooni.R
import com.ssafy.nooni.databinding.FragmentTutorialThreeBinding
import com.ssafy.nooni.databinding.FragmentTutorialTwoBinding

class TutorialThreeFragment : Fragment() {
    private lateinit var binding: FragmentTutorialThreeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorialThreeBinding.inflate(inflater, container, false)
        return binding.root
    }

}