package com.ssafy.nooni.tutorial

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ssafy.nooni.MainActivity
import com.ssafy.nooni.R
import com.ssafy.nooni.databinding.FragmentTutorialOneBinding


class TutorialOneFragment : Fragment() {
    private lateinit var binding: FragmentTutorialOneBinding
    private lateinit var tutorialActivity: TutorialActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorialOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        tutorialActivity = context as TutorialActivity
    }

    override fun onResume() {
        super.onResume()
        tutorialActivity.ttsSpeak(resources.getString(R.string.tutorial0))
    }

}