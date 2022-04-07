package com.ssafy.nooni.tutorial

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.ssafy.nooni.R
import com.ssafy.nooni.databinding.FragmentTutorialOneBinding
import com.ssafy.nooni.databinding.FragmentTutorialZeroBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TutorialZeroFragment : Fragment() {
    private lateinit var binding: FragmentTutorialZeroBinding
    private lateinit var tutorialActivity: TutorialActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorialZeroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        tutorialActivity = context as TutorialActivity
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch {
            delay(500)
            tutorialActivity.tts!!.setSpeechRate(2f)
            tutorialActivity.ttsSpeak(resources.getString(R.string.tutorial))
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gestureListener = MyGesture()
        val doubleTapListener = MyDoubleGesture()
        val gestureDetector = GestureDetector(requireContext(), gestureListener)

        gestureDetector.setOnDoubleTapListener(doubleTapListener)

        binding.tutorial0.setOnTouchListener { v, event ->
            return@setOnTouchListener gestureDetector.onTouchEvent(event)
        }

        binding.tutorial0.setOnClickListener{}

    }

    inner class MyGesture: GestureDetector.OnGestureListener {
        override fun onDown(p0: MotionEvent?): Boolean {
            return false
        }

        override fun onShowPress(p0: MotionEvent?) {

        }

        override fun onSingleTapUp(p0: MotionEvent?): Boolean {
            return false
        }

        override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            return false
        }

        override fun onLongPress(p0: MotionEvent?) {

        }

        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            return true
        }

    }

    inner class MyDoubleGesture : GestureDetector.OnDoubleTapListener {
        override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean {
            return false
        }

        override fun onDoubleTap(p0: MotionEvent?): Boolean {
            tutorialActivity.ttsSpeak(resources.getString(R.string.tutorial))
            return true
        }

        override fun onDoubleTapEvent(p0: MotionEvent?): Boolean {
            return false
        }
    }


}