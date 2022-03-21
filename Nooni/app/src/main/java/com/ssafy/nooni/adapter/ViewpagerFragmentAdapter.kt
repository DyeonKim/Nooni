package com.ssafy.nooni.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.nooni.AllergyFragment
import com.ssafy.nooni.CameraFragment
import com.ssafy.nooni.ContactFragment
import com.ssafy.nooni.MapFragment

class ViewpagerFragmentAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    // 1. ViewPager2에 연결할 Fragment 들을 생성
    val fragmentList = listOf<Fragment>(AllergyFragment(), CameraFragment(), ContactFragment(), MapFragment())

    // 2. ViewPager2에서 노출시킬 Fragment 의 갯수 설정
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    // 3. ViewPager2의 각 페이지에서 노출할 Fragment 설정
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}