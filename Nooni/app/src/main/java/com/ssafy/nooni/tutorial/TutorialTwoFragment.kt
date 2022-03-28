package com.ssafy.nooni.tutorial

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.ssafy.nooni.R
import com.ssafy.nooni.databinding.FragmentTutorialOneBinding
import com.ssafy.nooni.databinding.FragmentTutorialTwoBinding

class TutorialTwoFragment : Fragment() {
    private lateinit var binding: FragmentTutorialTwoBinding
    private lateinit var tutorialActivity: TutorialActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorialTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        tutorialActivity = context as TutorialActivity
    }

    override fun onResume() {
        super.onResume()
        tutorialActivity.ttsSpeak(resources.getString(R.string.tutorial1))
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gestureListener = MyGesture()
        val doubleTapListener = MyDoubleGesture()
        val gestureDetector = GestureDetector(requireContext(), gestureListener)

        gestureDetector.setOnDoubleTapListener(doubleTapListener)

        binding.tutorial1.setOnTouchListener { v, event ->
            return@setOnTouchListener gestureDetector.onTouchEvent(event)
        }

        binding.tutorial1.setOnClickListener{}

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
            tutorialActivity.ttsSpeak(resources.getString(R.string.tutorial1))
            return true
        }

        override fun onDoubleTapEvent(p0: MotionEvent?): Boolean {
            return false
        }
    }


}