package com.ssafy.nooni.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.nooni.R
import com.ssafy.nooni.databinding.FragmentTutorialFourBinding


class TutorialFourFragment : Fragment() {
    private lateinit var binding: FragmentTutorialFourBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorialFourBinding.inflate(inflater, container, false)
        return binding.root
    }


}