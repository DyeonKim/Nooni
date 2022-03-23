package com.ssafy.nooni.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ssafy.nooni.api.PrdInfoApi
import com.ssafy.nooni.config.ApplicationClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrdInfoRepository {
    private val TAG = "PrdInfoRepository"
    val prdInfoService = ApplicationClass.pRetrofit.create(PrdInfoApi::class.java)
    val _allergenList = MutableLiveData<List<String>>()

    suspend fun getAllergen(prdNo: String) {
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
}