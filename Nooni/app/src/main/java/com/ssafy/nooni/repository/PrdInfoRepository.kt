package com.ssafy.nooni.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ssafy.nooni.R
import com.ssafy.nooni.api.PrdInfoApi
import com.ssafy.nooni.config.ApplicationClass
import com.ssafy.nooni.util.SharedPrefArrayListUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrdInfoRepository(val context: Context) {
    private val TAG = "PrdInfoRepository"
    private val sharedPrefArrayListUtil = SharedPrefArrayListUtil(context)
    private val prdInfoService = ApplicationClass.pRetrofit.create(PrdInfoApi::class.java)

    private val allergyMap = HashMap<String, ArrayList<String>>()
    private val _allergenList = MutableLiveData<List<String>>()


    init {
        val keys = context.resources.getStringArray(R.array.allergy_names)
        val values = context.resources.getStringArray(R.array.allergen_names)

        for (i in keys.indices) {
            val items = values[i].split(", ")
            for (j in items.indices)
                allergyMap[keys[i]]?.add(items[j]) ?: allergyMap.put(keys[i], arrayListOf(items[j]))
        }
    }

    fun getAllergenList(): MutableLiveData<List<String>> {
        return _allergenList
    }

    suspend fun loadAllergen(prdNo: String) {
        var list = listOf("알 수 없음")

        try {
            val response = withContext(Dispatchers.IO) {
                prdInfoService.getPrdInfo(prdNo)
            }
            if (response.isSuccessful) {
                if (response.body() != null) {
                    val data = response.body()!!
                    if (data.totalCount.toLong() > 0) {
                        var strAllergen = data.list[0].allergy
                        if (strAllergen.length > 3 && strAllergen.substring(strAllergen.length - 3) == " 함유")
                            strAllergen = strAllergen.substring(0 until strAllergen.length - 3)
                        list = strAllergen.split(",")
                    }
                }
            } else {
                Log.d(TAG, "onError: Error Code ${response.code()}")
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message ?: "onFailure")
        } finally {
            _allergenList.postValue(list)
        }
    }

    fun hasAllergen(): String {
        val allergies = sharedPrefArrayListUtil.getAllergies() ?: listOf()

        if (_allergenList.value == null || _allergenList.value!![0] == "알 수 없음")
            return "알레르기 유발 물질을 알 수 없습니다."

        for (allergy in allergies) {
            allergyMap[allergy]?.forEach {
                if (_allergenList.value?.contains(it) == true)
                    return "알레르기 유발 물질이 있습니다."
            }
        }
        return "알레르기 유발 물질이 없습니다."
    }
}