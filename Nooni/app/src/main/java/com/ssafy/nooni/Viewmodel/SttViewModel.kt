package com.ssafy.nooni.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SttViewModel:ViewModel() {
    private val _stt = MutableLiveData<String>()
    val stt : LiveData<String> get() = _stt

    private val _nooni = MutableLiveData<Boolean>()
    val nooni : LiveData<Boolean> get() = _nooni

    fun setStt(strlist:String){
        _stt.postValue(strlist)
    }
    fun setNooni(flag:Boolean){
        _nooni.postValue(flag)
    }
}