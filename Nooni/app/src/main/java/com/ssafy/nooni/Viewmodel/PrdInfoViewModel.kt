package com.ssafy.nooni.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.nooni.repository.PrdInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrdInfoViewModel : ViewModel(){
    private val prdInfoRepository = PrdInfoRepository()
    val allergenList = prdInfoRepository._allergenList

    fun getAllergen(prdNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            prdInfoRepository.getAllergen(prdNo)
        }
    }
}