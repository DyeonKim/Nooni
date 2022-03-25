package com.ssafy.nooni.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.nooni.R
import com.ssafy.nooni.databinding.FragmentContactBinding
import com.ssafy.nooni.databinding.FragmentTutorialOneBinding


class TutorialOneFragment : Fragment() {
    private lateinit var binding: FragmentTutorialOneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorialOneBinding.inflate(inflater, container, false)
        return binding.root
    }

}