package com.ssafy.nooni.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.nooni.repository.PrdInfoRepository
import com.ssafy.nooni.util.SharedPrefArrayListUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrdInfoViewModel(private val context: Context) : ViewModel(){
    private val prdInfoRepository = PrdInfoRepository()
    private val sharedPrefArrayListUtil = SharedPrefArrayListUtil(context)
    private val _allergenList = prdInfoRepository._allergenList
    private val _noticeAllergy = MutableLiveData<String>()

    val allergenList: LiveData<List<String>>
        get() = _allergenList
    val noticeAllergy: LiveData<String>
        get() = _noticeAllergy


    fun loadAllergen(prdNo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            prdInfoRepository.loadAllergen(prdNo)
            _noticeAllergy.postValue(searchAllergen())
        }
    }

    private fun searchAllergen(): String {
        val allergies = sharedPrefArrayListUtil.getAllergies() ?: listOf()
        allergenList.value?.forEach {
            when {
                arrayOf("게", "새우", "가재").contains(it) -> {
                    if (allergies.contains("갑각류"))
                        return "알레르기 유발 물질이 있습니다."
                }
                arrayOf("호두", "잣").contains(it) -> {
                    if (allergies.contains("견과"))
                        return "알레르기 유발 물질이 있습니다."
                }
                arrayOf("계란").contains(it) -> {
                    if (allergies.contains("계란"))
                        return "알레르기 유발 물질이 있습니다."
                }
                arrayOf("땅콩").contains(it) -> {
                    if (allergies.contains("땅콩"))
                        return "알레르기 유발 물질이 있습니다."
                }
                arrayOf("밀").contains(it) -> {
                    if (allergies.contains("밀"))
                        return "알레르기 유발 물질이 있습니다."
                }
                arrayOf("오징어", "고등어").contains(it) -> {
                    if (allergies.contains("생선"))
                        return "알레르기 유발 물질이 있습니다."
                }
                arrayOf("우유").contains(it) -> {
                    if (allergies.contains("우유"))
                        return "알레르기 유발 물질이 있습니다."
                }
                arrayOf("조개").contains(it) -> {
                    if (allergies.contains("조개"))
                        return "알레르기 유발 물질이 있습니다."
                }
                arrayOf("콩").contains(it) -> {
                    if (allergies.contains("콩"))
                        return "알레르기 유발 물질이 있습니다."
                }
            }
        }
        return "알레르기 유발 물질이 없습니다."
    }
}