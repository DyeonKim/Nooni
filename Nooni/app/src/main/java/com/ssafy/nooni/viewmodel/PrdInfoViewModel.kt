package com.ssafy.nooni.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.nooni.repository.PrdInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrdInfoViewModel(private val prdInfoRepository: PrdInfoRepository) : ViewModel() {
    private val _allergenList = prdInfoRepository.getAllergenList()
    private val _noticeAllergy = MutableLiveData<String>()

    val allergenList: LiveData<List<String>>
        get() = _allergenList
    val noticeAllergy: LiveData<String>
        get() = _noticeAllergy


    fun loadAllergen(prdNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            prdInfoRepository.loadAllergen(prdNo)
            _noticeAllergy.postValue(prdInfoRepository.hasAllergen())
        }
    }
}