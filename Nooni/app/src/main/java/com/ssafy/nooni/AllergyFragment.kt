package com.ssafy.nooni

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.nooni.adapter.AllergyRVAdapter
import com.ssafy.nooni.databinding.FragmentAllergyBinding
import com.ssafy.nooni.util.SharedPrefArrayListUtil


class AllergyFragment : Fragment() {
    lateinit var binding: FragmentAllergyBinding
    lateinit var allergyRVAdapter: AllergyRVAdapter
    var sharePrefArrayListUtil = SharedPrefArrayListUtil()
    var allergyList: ArrayList<String>? = null
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllergyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        var gestureListener = MyGesture()
        var doubleTapListener = MyDoubleGesture()
        var gestureDetector = GestureDetector(requireContext(), gestureListener)
        gestureDetector.setOnDoubleTapListener(doubleTapListener)
        binding.llAllergyF.setOnTouchListener { v, event ->
            return@setOnTouchListener gestureDetector.onTouchEvent(event)
        }

        allergyList = sharePrefArrayListUtil.getStringArrayPref(requireContext(), "allergies")

        // 왜인지는 모르겠으나 onTouchListener만 달아놓으면 더블클릭 인식이 안되고 clickListener도 같이 달아놔야만 더블클릭 인식됨; 뭐징
        binding.llAllergyF.setOnClickListener{}

        setRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.findViewById<TextView>(R.id.tv_title).text = "알레르기"
    }

    private fun setRecyclerView() {
        allergyRVAdapter = AllergyRVAdapter()
        binding.rvAllergyFAllergy.apply{
            adapter = allergyRVAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

        if(allergyList?.size ?: 0 == 0){
            allergyRVAdapter.setData(listOf("없음"))
        } else {
            allergyList?.let { allergyRVAdapter.setData(it) }
        }
    }

    inner class MyGesture: GestureDetector.OnGestureListener {
        override fun onDown(p0: MotionEvent?): Boolean { return false }

        override fun onShowPress(p0: MotionEvent?) {}

        override fun onSingleTapUp(p0: MotionEvent?): Boolean { return false }

        override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean { return false }

        override fun onLongPress(p0: MotionEvent?) {}

        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean { return false }


    }

    inner class MyDoubleGesture: GestureDetector.OnDoubleTapListener {
        override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean { return false }

        override fun onDoubleTap(p0: MotionEvent?): Boolean {
            startActivity(Intent(requireActivity(), RegisterAllergyActivity::class.java))
            return true
        }

        override fun onDoubleTapEvent(p0: MotionEvent?): Boolean { return false }
    }


}