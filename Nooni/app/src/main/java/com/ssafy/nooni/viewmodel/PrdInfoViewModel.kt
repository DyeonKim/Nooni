package com.ssafy.nooni.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.nooni.repository.PrdInfoRepository
import com.ssafy.nooni.util.SharedPrefArrayListUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrdInfoViewModel : ViewModel(){
    private val prdInfoRepository = PrdInfoRepository()

    private val _allergenList = prdInfoRepository._allergenList

    val allergenList : LiveData<List<String>>
        get() = _allergenList


    fun loadAllergen(prdNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            prdInfoRepository.loadAllergen(prdNo)
        }
    }
}