package com.ssafy.nooni.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SttViewModel:ViewModel() {
    private val _stt = MutableLiveData<ArrayList<String>>()
    val stt : LiveData<ArrayList<String>> get() = _stt

    fun setStt(strlist:ArrayList<String>){
        _stt.postValue(strlist)
    }
}